/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client.auth;

import java.util.HashMap;
import java.util.Map;

import com.panjin.cloud.nqs.client.ClientConfig;
import com.panjin.cloud.nqs.client.util.AuthUtil;

/**
 *
 *
 * @author panjin
 * @version $Id: CloudAuthBackend.java 2016年7月19日 下午6:01:13 $
 */
public class CloudAuthBackend implements AuthBackend {

    /**
     * Key name for product id.
     */
    private static final String PRODUCT_ID = "product_id";

    /**
     * Client config for connecting RabbitMQ.
     */
    private ClientConfig clientConfig;

    public CloudAuthBackend(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
    
    /** 
     * @see com.panjin.cloud.nqs.client.auth.AuthBackend#getUsername()
     */
    @Override
    public String getUsername() {
        return clientConfig.getProductId();
    }

    /** 
     * @see com.panjin.cloud.nqs.client.auth.AuthBackend#getPassword()
     */
    @Override
    public String getPassword() {
        String productId = clientConfig.getProductId();
        String accessKey = clientConfig.getAccessKey();
        String accessSecret = clientConfig.getAccessSecret();
        Map<String, String> paras = new HashMap<String, String>();
        paras.put(PRODUCT_ID, productId);

        return AuthUtil.getSignWithParas(accessKey, accessSecret, paras);
    }

}
