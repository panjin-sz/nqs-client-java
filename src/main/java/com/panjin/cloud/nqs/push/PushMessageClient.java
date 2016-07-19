/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.push;

import java.io.IOException;
import java.util.SortedSet;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.panjin.cloud.nqs.AbstractSimpleClient;
import com.panjin.cloud.nqs.ClientConfig;
import com.panjin.cloud.nqs.Message;
import com.panjin.cloud.nqs.exception.MessageClientException;
import com.panjin.cloud.nqs.producer.ProducerConfig;
import com.panjin.cloud.nqs.util.Utils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.impl.ChannelN;

/**
 * Message client for push platform.
 *
 * @author panjin
 * @version $Id: PushMessageClient.java 2016年7月19日 下午6:31:43 $
 */
public class PushMessageClient extends AbstractSimpleClient {

    /**
     * The logger object.
     */
    private static Logger logger = LoggerFactory.getLogger(PushMessageClient.class);

    /**
     * Config for the producer.
     */
    private ProducerConfig producerConfig = null;
    
    /**
     * Queue properties for the producer.
     */
    private QueueProps queueProps = null;

    /**
     * Constructor, create the default queue operator object.
     * 
     * @param clientConfig
     *            the client config for the producer
     * @throws MessageClientException
     *             if any error occurs while creating the producer object
     */
    public PushMessageClient(ClientConfig clientConfig) throws MessageClientException {
        Utils.checkNotNull(clientConfig, "clientConfig");

        this.clientConfig = clientConfig;

        // connect to the server
        connect(clientConfig.getProductId());
    }

    /**
     * Constructor, create the producer object.
     * 
     * @param clientConfig
     *            the client config for the producer
     * @throws MessageClientException
     *             if any error occurs while creating the producer object
     */
    public PushMessageClient(ClientConfig clientConfig, ProducerConfig producerConfig) throws MessageClientException {
        Utils.checkNotNull(clientConfig, "clientConfig");
        Utils.checkNotNull(producerConfig, "producerConfig");

        this.clientConfig = clientConfig;
        this.producerConfig = producerConfig;

        queueProps = new QueueProps(producerConfig.getProductId(), producerConfig.getQueueName());

        // connect to the server
        connect(queueProps.getProductId());
    }

    /**
     * Create a topic queue.
     * 
     * @param queueAlias
     *            the queue name
     * @param isHa
     *            true if create a HA queue, false for normal queue
     * @param routingKeys
     *            binded routing keys
     * @return the created queue id
     * @throws MessageClientException
     *             if any error occurs while creating the queue
     */
    public String createTopicQueue(String queueAlias, boolean isHa, String... routingKeys) throws MessageClientException {
        Utils.checkNotEmpty(queueAlias, "queueAlias");

        String vhost = clientConfig.getProductId();
        String encodedQueueName = null;
        if (isHa) {
            encodedQueueName = "8ha." + queueAlias;
        } else {
            encodedQueueName = "4." + queueAlias;
        }

        Channel channel = getChannel(queueProps);

        try {
            // use vhost as the exchange name
            String exchangeName = vhost;

            channel.exchangeDeclare(exchangeName, "topic", true);
            channel.queueDeclare(encodedQueueName, true, false, false, null);

            for (String rk : routingKeys) {
                channel.queueBind(encodedQueueName, exchangeName, rk);
            }

            return vhost + "/" + encodedQueueName;
        } catch (IOException e) {
            logger.error("createQueue exception", e);
            throw new MessageClientException(e);
        }
    }

    /**
     * Delete the queue.
     * 
     * @param queueId
     *            the queue id
     * @return true if queue deleted successfully, false otherwise
     * @throws MessageClientException
     *             if any error occurs while deleting the queue
     */
    public boolean deleteQueue(String queueId) throws MessageClientException {
        String[] attribute = queueId.split("/", 2);
        if (attribute.length != 2) {
            throw new IllegalArgumentException("invalid queue id");
        }

        QueueProps queueProps = new QueueProps(attribute[0], attribute[1]);
        Channel channel = getChannel(queueProps);

        try {
            channel.queueDelete(queueProps.getQueueName(), true, true);

            return true;
        } catch (IOException e) {
            logger.error("deleteQueue exception", e);
            throw new MessageClientException(e);
        }
    }

    /**
     * Send one message to the server with specific routing key.
     * 
     * @param message
     *            the message object
     * @param routingKey
     *            the routing key for the message
     * @return true if send successfully, false otherwise
     * @throws MessageClientException
     *             if any error occurs while sending the message
     */
    public boolean sendMessageWithRoutingKey(Message message, String routingKey) throws MessageClientException {
        Utils.checkNotNull(message, "message");

        Channel channel = getChannel(queueProps, producerConfig.isRequireConfirm());
        try {
            // use vhost as the exchange name
            String exchangeName = queueProps.getProductId();

            AMQP.BasicProperties messageProperties = MessageProperties.BASIC;
            if (message.isPersistent()) {
                messageProperties = MessageProperties.PERSISTENT_BASIC;
            }

            int ttl = message.getTtl();
            if (ttl > 0) {
                messageProperties = messageProperties.builder().expiration(String.valueOf(ttl * 1000)).build();
            }

            channel.basicPublish(exchangeName, routingKey, true, messageProperties, message.getBody());

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
