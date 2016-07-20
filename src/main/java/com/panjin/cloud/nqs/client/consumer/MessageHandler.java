/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client.consumer;

import com.panjin.cloud.nqs.client.Message;

/**
 * Interface for message handler.
 *
 * @author panjin
 * @version $Id: MessageHandler.java 2016年7月20日 上午10:26:24 $
 */
public interface MessageHandler {

    /**
     * Process received messages.
     * 
     * @param message
     *            the message object
     * @return true if the message is processed successfully, false otherwise
     */
    public boolean handle(Message message);
}
