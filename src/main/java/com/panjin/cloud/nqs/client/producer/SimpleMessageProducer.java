/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client.producer;

import java.io.IOException;
import java.util.SortedSet;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panjin.cloud.nqs.client.AbstractSessionFactory;
import com.panjin.cloud.nqs.client.AbstractSimpleClient;
import com.panjin.cloud.nqs.client.Message;
import com.panjin.cloud.nqs.client.exception.MessageClientException;
import com.panjin.cloud.nqs.client.util.Utils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.impl.ChannelN;

/**
 *
 *
 * @author panjin
 * @version $Id: SimpleMessageProducer.java 2016年7月20日 上午10:33:47 $
 */
public class SimpleMessageProducer extends AbstractSimpleClient implements MessageProducer {
    /**
     * The logger object.
     */
    private static Logger logger = LoggerFactory.getLogger(SimpleMessageProducer.class);

    /**
     * Config for the producer.
     */
    private ProducerConfig producerConfig = null;
    /**
     * Queue properties for the producer.
     */
    private QueueProps queueProps = null;

    /**
     * Constructor, create the producer object.
     * 
     * @param sessionFactory
     *            the session factory for the producer
     * @param producerConfig
     *            config for the producer
     * @throws MessageClientException
     *             if any error occurs while creating the producer object
     */
    public SimpleMessageProducer(AbstractSessionFactory sessionFactory, ProducerConfig producerConfig) throws MessageClientException {
        Utils.checkNotNull(sessionFactory, "simpleMessageSessionFactory");
        Utils.checkNotNull(producerConfig, "producerConfig");

        Utils.checkNotEmpty(producerConfig.getQueueName(), "queueName");

        this.clientConfig = sessionFactory.getClientConfig();
        this.producerConfig = producerConfig;

        queueProps = new QueueProps(producerConfig.getProductId(), producerConfig.getQueueName());

        // connect to the server
        connect(queueProps.getProductId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.panjin.mq.client.producer.MessageProducer#sendMessage(com.panjin
     * .mq.client.Message)
     */
    @Override
    public boolean sendMessage(Message message) throws MessageClientException {
        Utils.checkNotNull(message, "message");

        String queueName = queueProps.getQueueName();

        Channel channel = getChannel(queueProps, producerConfig.isRequireConfirm());
        try {
            // use vhost as the exchange name
            String exchangeName = queueName;
            // use queueName as routingKey
            String routingKey = queueName;

            AMQP.BasicProperties messageProperties = MessageProperties.BASIC;
            if (message.isPersistent()) {
                messageProperties = MessageProperties.PERSISTENT_BASIC;
            }

            int ttl = message.getTtl();
            if (ttl > 0) {
                messageProperties = messageProperties.builder().expiration(String.valueOf(ttl * 1000)).build();
            }

            channel.basicPublish(exchangeName, routingKey, messageProperties, message.getBody());

            if (producerConfig.isRequireConfirm()) {
                channel.waitForConfirms(producerConfig.getWaitTimeout());
            }

            return true;
        } catch (ShutdownSignalException e) {
            logger.error("sendMessage exception", e);
            throw new MessageClientException(e);
        } catch (IOException e) {
            logger.error("sendMessage exception", e);
            throw new MessageClientException(e);
        } catch (InterruptedException e) {
            logger.error("sendMessage exception", e);
            throw new MessageClientException(e);
        } catch (TimeoutException e) {
            if (channel instanceof ChannelN) {
                // trick, clear the unconfirmed set to avoid TimeoutException
                SortedSet<?> unconfirmedSet = SortedSet.class.cast(Utils.getPrivateFieldValue(channel, "unconfirmedSet"));
                unconfirmedSet.clear();
            }

            logger.error("sendMessage exception", e);
            throw new MessageClientException(e);
        }
    }
}
