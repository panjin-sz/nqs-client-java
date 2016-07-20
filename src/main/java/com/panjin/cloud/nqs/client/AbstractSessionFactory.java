/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client;

import com.panjin.cloud.nqs.client.util.Utils;

/**
 * Abstract message session factory.
 *
 * @author panjin
 * @version $Id: AbstractSessionFactory.java 2016年7月20日 上午10:29:02 $
 */
public abstract class AbstractSessionFactory implements Shutdownable {

    /**
     * Client config for the session.
     */
    private ClientConfig clientConfig;

    /**
     * Constructor, create the session factory.
     * 
     * @param config
     *            the client config
     */
    public AbstractSessionFactory(ClientConfig config) {
        Utils.checkNotNull(config, "config");

        Utils.checkNotEmpty(config.getHost(), "host");
        Utils.checkNotEmpty(config.getProductId(), "productId");
        Utils.checkNotEmpty(config.getAccessKey(), "accessKey");
        Utils.checkNotEmpty(config.getAccessSecret(), "accessSecret");

        this.clientConfig = config;
    }

    /**
     * Get the client config for this session.
     * 
     * @return the client config for this session
     */
    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.panjin.mq.client.Shutdownable#shutdown()
     */
    @Override
    public void shutdown() {
        // do nothing right now
    }
}
