/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.exception;

/**
 * Exception that will throw when a connection has already been closed.
 *
 * @author panjin
 * @version $Id: AlreadyClosedException.java 2016年7月19日 下午6:22:37 $
 */
public class AlreadyClosedException extends MessageClientException {

    /**  */
    private static final long serialVersionUID = -6390513609228234957L;

    /**
     * Constructor.
     * 
     * @param message
     *            the message for the exception
     */
    public AlreadyClosedException(String message) {
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
    public AlreadyClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     * 
     * @param cause
     *            the throwable object for the exception
     */
    public AlreadyClosedException(Throwable cause) {
        super(cause);
    }
}
