/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.consumer;

/**
 * Config object for consumer.
 *
 * @author panjin
 * @version $Id: ConsumerConfig.java 2016年7月20日 上午10:24:38 $
 */
public class ConsumerConfig {

    /**
     * Product id for target queue.
     */
    private String productId;
    /**
     * QueueId for the producer.
     */
    private String queueName;
    /**
     * Whether consumer should ack received message to the server.
     */
    private boolean requireAck = true;
    /**
     * Group name for the consumer.
     */
    private String group;
    /**
     * Prefecth count for the consumer.
     */
    private int prefetchCount = 0;

    /**
     * Get the product id.
     * 
     * @return the product id
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Set the product id.
     * 
     * @param productId
     *            product id to be set
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Get the queue name.
     * 
     * @return the queue name
     */
    public String getQueueName() {
        return queueName;
    }

    /**
     * Set the queue name.
     * 
     * @param queueName
     *            name the queue name to be set
     */
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * Get requireAck.
     * 
     * @return whether the consumer should ack to the server or not
     */
    public boolean isRequireAck() {
        return requireAck;
    }

    /**
     * Set requireAck.
     * 
     * @param requireAck
     *            whether the consumer should ack to the server or not
     */
    public void setRequireAck(boolean requireAck) {
        this.requireAck = requireAck;
    }

    /**
     * Get the group name.
     * 
     * @return the group name
     */
    public String getGroup() {
        return group;
    }

    /**
     * Set the group name.
     * 
     * @param group
     *            the group name
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Get the value of prefetchCount.
     * 
     * @return the value of prefetchCount
     */
    public int getPrefetchCount() {
        return prefetchCount;
    }

    /**
     * Set prefetchCount.
     * 
     * @param prefetchCount
     *            value of prefetchCount
     */
    public void setPrefetchCount(int prefetchCount) {
        this.prefetchCount = prefetchCount;
    }

}
