/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client.exception;

/**
 * Message client exception for the message client.
 *
 * @author panjin
 * @version $Id: MessageClientException.java 2016年7月19日 下午6:21:43 $
 */
public class MessageClientException extends Exception {

    /**  */
    private static final long serialVersionUID = 4587259635457438436L;

    /**
     * Constructor.
     * 
     * @param message
     *            the message for the exception
     */
    public MessageClientException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message
     *            the message for the exception
     * @param cause
     *            the throwable object for the exception
     */
    public MessageClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     *            the throwable object for the exception
     */
    public MessageClientException(Throwable cause) {
        super(cause);
    }
}
