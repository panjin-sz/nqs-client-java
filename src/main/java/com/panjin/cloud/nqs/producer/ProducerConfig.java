/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.producer;

/**
 * Config object for producer.
 *
 * @author panjin
 * @version $Id: ProducerConfig.java 2016年7月19日 下午6:28:02 $
 */
public class ProducerConfig {
    /**
     * The default wait timeout for confirm.
     */
    public static final long DEFAULT_WAIT_TIMEOUT = 5000;

    /**
     * Product id for target queue.
     */
    private String productId;
    /**
     * QueueId for the producer.
     */
    private String queueName;
    /**
     * Whether producer should wait the confirm from the server.
     */
    private boolean requireConfirm = false;
    /**
     * Timeout for waiting the confirm.
     */
    private long waitTimeout = DEFAULT_WAIT_TIMEOUT;

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
     * Set the queueName.
     * 
     * @param queueName
     *            the queueName
     */
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * Check whether the producer should wait the confirm.
     * 
     * @return whether the producer should wait the confirm
     */
    public boolean isRequireConfirm() {
        return requireConfirm;
    }

    /**
     * Set whether the producer should wait the confirm.
     * 
     * @param requireConfirm
     *            where the producer should wait the confirm
     */
    public void setRequireConfirm(boolean requireConfirm) {
        this.requireConfirm = requireConfirm;
    }

    /**
     * Get the timeout for waiting confirm.
     * 
     * @return the timeout
     */
    public long getWaitTimeout() {
        return waitTimeout;
    }

    /**
     * Set the timeout for waiting confirm.
     * 
     * @param waitTimeout
     *            the timeout to be set
     */
    public void setWaitTimeout(long waitTimeout) {
        this.waitTimeout = waitTimeout;
    }
}
