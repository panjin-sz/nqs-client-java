/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.consumer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panjin.cloud.nqs.AbstractSessionFactory;
import com.panjin.cloud.nqs.AbstractSimpleClient;
import com.panjin.cloud.nqs.Message;
import com.panjin.cloud.nqs.exception.MessageClientException;
import com.panjin.cloud.nqs.util.Utils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 *
 *
 * @author panjin
 * @version $Id: SimpleMessageConsumer.java 2016年7月20日 上午10:27:21 $
 */
public class SimpleMessageConsumer extends AbstractSimpleClient implements MessageConsumer {
    /**
     * Timeout for delivery.
     */
    private static final int DELIVER_TIMEOUT = 5000;

    /**
     * The logger object.
     */
    private static Logger logger = LoggerFactory.getLogger(SimpleMessageConsumer.class);

    /**
     * Config for this consumer object.
     */
    private ConsumerConfig consumerConfig = null;
    /**
     * Queue properties for this consumer.
     */
    private QueueProps queueProps = null;

    /**
     * True is consumer called consumeMessage.
     */
    private volatile boolean consumed = false;

    /**
     * True is consumer stopped successfully.
     */
    private volatile boolean stopped = false;

    /**
     * Constructor, create the consumer object.
     * 
     * @param sessionFactory
     *            session object for consumer
     * @param consumerConfig
     *            config for consumer
     * @throws MessageClientException
     *             if any error occurs when create the consumer
     */
    public SimpleMessageConsumer(AbstractSessionFactory sessionFactory, ConsumerConfig consumerConfig) throws MessageClientException {
        Utils.checkNotNull(sessionFactory, "simpleMessageSessionFactory");
        Utils.checkNotNull(consumerConfig, "consumerConfig");

        Utils.checkNotEmpty(consumerConfig.getQueueName(), "queueName");
        Utils.checkNotEmpty(consumerConfig.getGroup(), "group");
        if (consumerConfig.getPrefetchCount() < 0) {
            throw new IllegalArgumentException("prefetchCount cann't be negative");
        }

        this.clientConfig = sessionFactory.getClientConfig();
        this.consumerConfig = consumerConfig;

        queueProps = new QueueProps(consumerConfig.getProductId(), consumerConfig.getQueueName());

        // create the connection
        connect(queueProps.getProductId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.panjin.mq.client.consumer.MessageConsumer#getMessage()
     */
    @Override
    public Message getMessage() throws MessageClientException {
        String queueName = queueProps.getQueueName();

        Channel channel = getChannel(queueProps);

        try {
            GetResponse getResponse = channel.basicGet(queueName, !consumerConfig.isRequireAck());
            if (getResponse == null) {
                return null;
            }

            Message message = new Message(getResponse.getBody());
            if (consumerConfig.isRequireAck()) {
                channel.basicAck(getResponse.getEnvelope().getDeliveryTag(), false);
            }

            return message;
        } catch (ShutdownSignalException e) {
            logger.error("getMessage exception", e);
            throw new MessageClientException(e);
        } catch (IOException e) {
            logger.error("getMessage exception", e);
            throw new MessageClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.panjin.mq.client.consumer.MessageConsumer#consumeMessage(com.netease
     * .mq.client.consumer.MessageHandler)
     */
    @Override
    public void consumeMessage(final MessageHandler handler) throws MessageClientException {
        Utils.checkNotNull(handler, "handler");

        String queueName = queueProps.getQueueName();

        Channel channel = getChannel(queueProps);
        try {
            if (consumerConfig.getPrefetchCount() > 0) {
                // set prefetchCount for amqp
                channel.basicQos(consumerConfig.getPrefetchCount());
            }

            LinkedBlockingQueue<QueueingConsumer.Delivery> blockingQueue = new LinkedBlockingQueue<QueueingConsumer.Delivery>();
            QueueingConsumer consumer = new QueueingConsumer(channel, blockingQueue);
            channel.basicConsume(queueName, !consumerConfig.isRequireAck(), consumerConfig.getGroup(), consumer);

            consumed = true;

            boolean cancelled = false;
            while (true) {
                if (stop) {
                    if (!cancelled) {
                        channel.basicCancel(consumerConfig.getGroup());
                        cancelled = true;
                    }

                    if (blockingQueue.size() == 0) {
                        stopped = true;
                        break;
                    }
                }

                QueueingConsumer.Delivery delivery = null;
                try {
                    delivery = consumer.nextDelivery(DELIVER_TIMEOUT);
                    if (delivery != null) {
                        long deliveryTag = delivery.getEnvelope().getDeliveryTag();

                        Message message = new Message(delivery.getBody());
                        boolean result = handler.handle(message);
                        if (consumerConfig.isRequireAck()) {
                            if (result) {
                                channel.basicAck(deliveryTag, false);
                            } else {
                                channel.basicNack(deliveryTag, false, false);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    // retry to get next delivery
                } catch (Throwable e) {
                    // logger.error("consumeMessage exception", e);
                    // 若这里不回ack；上层的消费者重连，若消息确实处理不了，则会出现一直不停下发的死循环现象..
                    // 这里暂时打印一下消息体，然后不requeue，需要上层配置dead letter queue！
                    if (delivery != null) {
                        String msgString = null;
                        try {
                            msgString = new String(delivery.getBody(), "UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                        }
                        logger.info("consumeMessage occur exception, message:" + msgString);
                        long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                        channel.basicNack(deliveryTag, false, false);
                    }

                    // 将队列里面的数据清空,让shutdown方法可以结束;因为其他的这些消息没有回ack,所以待会连接断开之后,消息还是会被下发下来
                    blockingQueue.clear();
                    throw new MessageClientException(e);
                }
            }
        } catch (ShutdownSignalException e) {
            logger.error("consumeMessage exception", e);
            throw new MessageClientException(e);
        } catch (IOException e) {
            logger.error("consumeMessage exception", e);
            throw new MessageClientException(e);
        } finally {
            stopped = true;
        }
    }

    @Override
    public void shutdown() {
        if (stop) {
            // already been shutdown
            return;
        }
        stop = true;

        if (consumed && !stopped) {
            stopped = false;
            while (!stopped) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }

        super.shutdown();
    }
}
