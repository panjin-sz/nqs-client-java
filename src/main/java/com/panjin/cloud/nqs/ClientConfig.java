/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs;

/**
 * The client config for the session.
 *
 * @author panjin
 * @version $Id: ClientConfig.java 2016年7月19日 下午6:01:51 $
 */
public class ClientConfig {

    /**
     * Auth type for plain mechanism.
     */
    public static final String AUTH_PLAIN = "plain";

    /**
     * Auth type for cloud mechanism.
     */
    public static final String AUTH_CLOUD = "cloud";

    /**
     * Default heartbeat timeout for the connection (ten minutes).
     */
    private static final int DEFAULT_HEARTBEAT_TIMEOUT = 10 * 60;

    /**
     * Default idle timeout for channel(minute).
     */
    private static final int DEFAULT_IDLE_TIMEOUT = 60;

    /**
     * The default port for the connection.
     */
    private static final int DEFAULT_PORT = 5672;

    /**
     * Host of server.
     */
    private String host = null;
    /**
     * Port of server.
     */
    private int port = DEFAULT_PORT;
    /**
     * Product id for the user.
     */
    private String productId = null;
    /**
     * Access key for the user.
     */
    private String accessKey = null;
    /**
     * Access secret for the user.
     */
    private String accessSecret = null;
    /**
     * Heartbeat timeout for the connection.
     */
    private int heartbeatTimeout = DEFAULT_HEARTBEAT_TIMEOUT;
    /**
     * Timeout for idle channel(minute).After this timeout, channel will be
     * closed.
     */
    private int idleTimeout = DEFAULT_IDLE_TIMEOUT;
    /**
     * Auth mechanism.
     */
    private String authMechanism = "cloud";

    /**
     * Get the host.
     * 
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * Set host to the server.
     * 
     * @param host
     *            the host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Get the port of the server.
     * 
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port of the server.
     * 
     * @param port
     *            the port
     */
    public void setPort(int port) {
        this.port = port;
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
     * Set the product id.
     * 
     * @param productId
     *            the product id
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Get the access key.
     * 
     * @return the access key
     */
    public String getAccessKey() {
        return accessKey;
    }

    /**
     * Set the access key.
     * 
     * @param accessKey
     *            the access key
     */
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * Set the access secret.
     * 
     * @param accessSecret
     *            the access secret
     */
    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    /**
     * Get the access secret.
     * 
     * @return the access secret
     */
    public String getAccessSecret() {
        return this.accessSecret;
    }

    /**
     * Get the heartbeat timeout setting.
     * 
     * @return the heartbeat timeout
     */
    public int getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    /**
     * Set the heartbeat timeout.
     * 
     * @param heartbeatTimeout
     *            heartbeat timeout to be set
     */
    public void setHeartbeatTimeout(int heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }

    /**
     * Get idle timeout for channel(minute).
     * 
     * @return the idle timeout for channel
     */
    public int getIdleTimeout() {
        return idleTimeout;
    }

    /**
     * Set idle timeout for channel(minute).
     * 
     * @param idleTimeout
     *            the idle timeout for channel
     */
    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    /**
     * Get auth mechanism.
     * 
     * @return the auth mechanism
     */
    public String getAuthMechanism() {
        return authMechanism;
    }

    /**
     * Set auth mechanism.
     * 
     * @param authMechanism
     *            the auth mechanism.
     */
    public void setAuthMechanism(String authMechanism) {
        this.authMechanism = authMechanism;
    }
}
