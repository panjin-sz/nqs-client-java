/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.auth;

import com.panjin.cloud.nqs.ClientConfig;

/**
 * Plain auth, use accessKey as username, accessSecret as password.
 *
 * @author panjin
 * @version $Id: PlainAuthBackend.java 2016年7月19日 下午6:06:14 $
 */
public class PlainAuthBackend implements AuthBackend {
    
    /**
     * Client config for connecting RabbitMQ.
     */
    private ClientConfig clientConfig;

    public PlainAuthBackend(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    /** 
     * @see com.panjin.cloud.nqs.auth.AuthBackend#getUsername()
     */
    @Override
    public String getUsername() {
        return clientConfig.getAccessKey();
    }

    /** 
     * @see com.panjin.cloud.nqs.auth.AuthBackend#getPassword()
     */
    @Override
    public String getPassword() {
        return clientConfig.getAccessSecret();
    }

}
