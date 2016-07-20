/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client;

import com.panjin.cloud.nqs.client.consumer.ConsumerConfig;
import com.panjin.cloud.nqs.client.consumer.MessageConsumer;
import com.panjin.cloud.nqs.client.consumer.SimpleMessageConsumer;
import com.panjin.cloud.nqs.client.exception.MessageClientException;
import com.panjin.cloud.nqs.client.producer.MessageProducer;
import com.panjin.cloud.nqs.client.producer.ProducerConfig;
import com.panjin.cloud.nqs.client.producer.SimpleMessageProducer;

/**
 *
 *
 * @author panjin
 * @version $Id: SimpleMessageSessionFactory.java 2016年7月20日 上午10:37:30 $
 */
public class SimpleMessageSessionFactory extends AbstractSessionFactory implements MessageSessionFactory {

    /**
     * Constructor, create the session factory instance.
     * 
     * @param config
     *            client config for the session
     */
    public SimpleMessageSessionFactory(ClientConfig config) {
        super(config);
    }

    /**
     * @see com.panjin.cloud.nqs.client.MessageSessionFactory#createProducer(com.panjin.cloud.nqs.client.producer.ProducerConfig)
     */
    @Override
    public MessageProducer createProducer(ProducerConfig producerConfig) throws MessageClientException {
        return new SimpleMessageProducer(this, producerConfig);
    }

    /**
     * @see com.panjin.cloud.nqs.client.MessageSessionFactory#createConsumer(com.panjin.cloud.nqs.client.consumer.ConsumerConfig)
     */
    @Override
    public MessageConsumer createConsumer(ConsumerConfig consumerConfig) throws MessageClientException {
        return new SimpleMessageConsumer(this, consumerConfig);
    }

}
