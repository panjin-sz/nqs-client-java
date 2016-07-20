/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client.auth;

import com.panjin.cloud.nqs.client.ClientConfig;

/**
 * Factory for creating auth backend.
 *
 * @author panjin
 * @version $Id: AuthBackendFactory.java 2016年7月19日 下午6:00:36 $
 */
public class AuthBackendFactory {

    /**
     * Create auth backend based on client config.
     * 
     * @param clientConfig
     *            the client config
     * @return the proper auth backend
     */
    public static AuthBackend createAuthBackend(ClientConfig clientConfig) {
        String autMechanism = clientConfig.getAuthMechanism();
        if (autMechanism.equals(ClientConfig.AUTH_CLOUD)) {
            return new CloudAuthBackend(clientConfig);
        } else if (autMechanism.equals(ClientConfig.AUTH_PLAIN)) {
            return new PlainAuthBackend(clientConfig);
        } else {
            throw new IllegalArgumentException("unknown auth mechanism: " + autMechanism);
        }
    }
}
