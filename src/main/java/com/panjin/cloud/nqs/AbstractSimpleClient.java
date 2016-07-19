/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.panjin.cloud.nqs.auth.AuthBackend;
import com.panjin.cloud.nqs.auth.AuthBackendFactory;
import com.panjin.cloud.nqs.exception.AlreadyClosedException;
import com.panjin.cloud.nqs.exception.MessageClientException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * Abstract simple message client.
 *
 * @author panjin
 * @version $Id: AbstractSimpleClient.java 2016年7月19日 下午6:25:27 $
 */
public abstract class AbstractSimpleClient implements Shutdownable {

    /**
     * Logger for this class.
     */
    private static Logger logger = LoggerFactory.getLogger(AbstractSimpleClient.class);

    /**
     * Default timeout for creating connection.
     */
    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

    /**
     * Client config for this client.
     */
    protected ClientConfig clientConfig = null;

    /**
     * Channel map, thread id as the key, and channel object as the value.
     */
    private Cache<Long, Channel> channelCache = null;

    /**
     * Connection factory for this client.
     */
    private ConnectionFactory factory = null;
    /**
     * Connection for this client.
     */
    private Connection connection = null;

    /**
     * True if this client already be stopped, false otherwise.
     */
    protected volatile boolean stop = false;

    /**
     * Queue properties.
     * 
     * @author jiaozhihui@corp.netease.com
     */
    protected class QueueProps {
        /**
         * Product id for the queue.
         */
        private String productId;
        /**
         * Actual queue name.
         */
        private String queueName;

        /**
         * Constructor.
         * 
         * @param productId
         *            the product id for the queue
         * @param queueName
         *            the queue name
         */
        public QueueProps(String productId, String queueName) {
            this.productId = productId;
            this.queueName = queueName;
        }

        /**
         * Get the product id.
         * 
         * @return the product id
         */
        public String getProductId() {
            return productId;
        }

        /**
         * Get the queue name.
         * 
         * @return the queue name
         */
        public String getQueueName() {
            return queueName;
        }
    }

    /**
     * Create the connection factory.
     * 
     * @param vhost
     *            target vhost
     */
    private void createFactory(String vhost) {
        factory = new ConnectionFactory();
        AuthBackend authBackend = AuthBackendFactory.createAuthBackend(clientConfig);

        // user product id as the user name
        factory.setUsername(authBackend.getUsername());
        factory.setPassword(authBackend.getPassword());
        factory.setVirtualHost(vhost);
        factory.setHost(clientConfig.getHost());
        factory.setPort(clientConfig.getPort());
        factory.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
        factory.setRequestedHeartbeat(clientConfig.getHeartbeatTimeout());
    }

    /**
     * Create the connection.
     * 
     * @throws MessageClientException
     *             if any error occurs while creating the connection
     */
    private void createConnection() throws MessageClientException {
        try {
            connection = factory.newConnection();
            logger.info("connection created");

            channelCache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(clientConfig.getIdleTimeout(), TimeUnit.MINUTES)
                    .removalListener(new RemovalListener<Long, Channel>() {
                        @Override
                        public void onRemoval(RemovalNotification<Long, Channel> notify) {
                            try {
                                if (notify.getCause() == RemovalCause.EXPIRED) {
                                    Channel channel = notify.getValue();
                                    logger.info("close expired channel: " + channel.getChannelNumber() + ", connection.isOpen="
                                            + channel.getConnection().isOpen() + ", channel.isOpen=" + channel.isOpen());
                                    if (channel.getConnection().isOpen()) {
                                        channel.close();
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("close channel exception", e);
                            }
                        }
                    }).build();
        } catch (IOException e) {
            connection = null;
            logger.error("createConnection exception", e);

            // throw out the exception
            throw new MessageClientException(e);
        }
    }

    /**
     * Connect to the server.
     * 
     * @param vhost
     *            target vhost
     * @throws MessageClientException
     *             if any error occurs while connecting to the server
     */
    protected void connect(String vhost) throws MessageClientException {
        createFactory(vhost);
        createConnection();
    }

    /**
     * Get channel for current thread with specific queue properties.
     * 
     * @param queueProps
     *            queue properties
     * @return the channel object
     * @throws MessageClientException
     *             if any error occurs while creating the channel
     */
    protected Channel getChannel(QueueProps queueProps) throws MessageClientException {
        return getChannel(queueProps, false);
    }

    /**
     * Get channel for current thread with specific queue properties and confirm
     * mode.
     * 
     * @param queueProps
     *            queue properties
     * @param requireConfirm
     *            whether the channel should in confirm model
     * @return the channel object
     * @throws MessageClientException
     *             if any error occurs while creating the channel
     */
    protected synchronized Channel getChannel(QueueProps queueProps, boolean requireConfirm) throws MessageClientException {
        if (stop) {
            throw new AlreadyClosedException("client already been shutdown");
        }

        final Long threadId = Thread.currentThread().getId();

        Channel channel = channelCache.getIfPresent(threadId);

        if (channel != null) {
            return channel;
        }

        return createChannel(threadId, requireConfirm);
    }

    /**
     * Create channel.
     * 
     * @param threadId
     *            the thread id for while this channel bind to
     * @param requireConfirm
     *            whether the channel should in confirm model
     * @return the channel object
     * @throws MessageClientException
     *             if any error occurs while creating the channel
     */
    private Channel createChannel(final Long threadId, final boolean requireConfirm) throws MessageClientException {
        Channel channel = null;
        try {
            channel = connection.createChannel();
            if (requireConfirm) {
                channel.confirmSelect();
            }

            channel.addShutdownListener(new ShutdownListener() {
                @Override
                public void shutdownCompleted(ShutdownSignalException shutdownSignalException) {
                    if (shutdownSignalException.isInitiatedByApplication()) {
                        return;
                    }

                    channelCache.invalidate(threadId);

                    if (shutdownSignalException.isHardError()) {
                        return;
                    }

                    Object ref = shutdownSignalException.getReference();
                    if (ref instanceof Channel) {
                        Channel channel = (Channel) ref;
                        channel.removeShutdownListener(this);
                    }
                }
            });

            channel.addReturnListener(new ReturnListener() {
                @Override
                public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String msg = new String(body, "UTF-8");

                    String errorLog = "publish message fail, replyText:" + replyText + ",exchange:" + exchange + ", routingKey:" + routingKey + ", msg:" + msg;
                    System.err.println(errorLog);
                    new MessageClientException(errorLog).printStackTrace();
                }
            });

            channelCache.put(threadId, channel);

            return channel;
        } catch (IOException e) {
            logger.error("createChannel exception", e);
            throw new MessageClientException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.panjin.cloud.nqs.Shutdownable#shutdown()
     */
    @Override
    public synchronized void shutdown() {
        if (stop && connection == null) {
            return;
        }

        for (Map.Entry<Long, Channel> entry : channelCache.asMap().entrySet()) {
            Channel channel = entry.getValue();
            try {
                channel.abort();
            } catch (Exception e) {
                // ignore
                logger.error("channelClose exeption: " + e.getMessage());
            }
        }

        channelCache.invalidateAll();

        if (connection != null) {
            connection.abort();
            connection = null;
        }

        stop = true;
    }

}
