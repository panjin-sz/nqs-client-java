/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client;

import com.panjin.cloud.nqs.client.consumer.ConsumerConfig;
import com.panjin.cloud.nqs.client.consumer.MessageConsumer;
import com.panjin.cloud.nqs.client.exception.MessageClientException;
import com.panjin.cloud.nqs.client.producer.MessageProducer;
import com.panjin.cloud.nqs.client.producer.ProducerConfig;

/**
 * The session factory interface for the message client.
 *
 * @author panjin
 * @version $Id: MessageSessionFactory.java 2016年7月20日 上午10:36:27 $
 */
public interface MessageSessionFactory {

    /**
     * Create a producer.
     * 
     * @param producerConfig
     *            config for the producer
     * @return the producer object
     * @throws MessageClientException
     *             if any error occurs while create the producer object
     */
    public MessageProducer createProducer(ProducerConfig producerConfig) throws MessageClientException;

    /**
     * Create a consumer.
     * 
     * @param consumerConfig
     *            config for the consumer
     * @return the consumer object
     * @throws MessageClientException
     *             if any error occurs while create the consumer object
     */
    public MessageConsumer createConsumer(ConsumerConfig consumerConfig) throws MessageClientException;
}
