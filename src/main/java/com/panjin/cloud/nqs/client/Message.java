/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client;

import java.util.Arrays;

import com.panjin.cloud.nqs.client.util.Utils;

/**
 *
 *
 * @author panjin
 * @version $Id: Message.java 2016年7月19日 下午6:28:43 $
 */
public class Message {

    /**
     * The message body.
     */
    private byte[] body;
    /**
     * TTL for the message.
     */
    private int ttl = 0;
    /**
     * Whether message should be persistent.
     */
    private boolean persistent = false;

    /**
     * Constructor.
     * 
     * @param body
     *            message body
     */
    public Message(byte[] body) {
        Utils.checkNotNull(body, "body");

        this.body = body;
    }

    /**
     * Constructor.
     * 
     * @param body
     *            message body
     * @param ttl
     *            TTL for the message
     */
    public Message(byte[] body, int ttl) {
        Utils.checkNotNull(body, "body");

        this.body = body;
        this.ttl = ttl;
    }

    /**
     * Constructor.
     * 
     * @param body
     *            message body
     * @param persistent
     *            whether message should be persistent
     */
    public Message(byte[] body, boolean persistent) {
        Utils.checkNotNull(body, "body");

        this.body = body;
        this.persistent = persistent;
    }

    /**
     * Constructor.
     * 
     * @param body
     *            message body
     * @param ttl
     *            TTL for the message
     * @param persistent
     *            whether message should be persistent
     */
    public Message(byte[] body, int ttl, boolean persistent) {
        Utils.checkNotNull(body, "body");

        this.body = body;
        this.ttl = ttl;
        this.persistent = persistent;
    }

    /**
     * Get the body of the message.
     * 
     * @return the message body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * Get the ttl of the message.
     * 
     * @return the ttl for the message
     */
    public int getTtl() {
        return ttl;
    }

    /**
     * Whether the message should be persistent.
     * 
     * @return true if message should be persistent, false otherwise
     */
    public boolean isPersistent() {
        return persistent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Message) {
            Message tmp = (Message) other;
            return Arrays.equals(tmp.getBody(), body);
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 17;
        for (int i = 0; i < body.length; i++) {
            result = 37 * result + body[i];
        }
        return result;
    }

}
