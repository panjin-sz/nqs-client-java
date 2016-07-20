/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client.producer;

import com.panjin.cloud.nqs.client.Message;
import com.panjin.cloud.nqs.client.Shutdownable;
import com.panjin.cloud.nqs.client.exception.MessageClientException;

/**
 * Interface for producer.
 *
 * @author panjin
 * @version $Id: MessageProducer.java 2016年7月20日 上午10:32:40 $
 */
public interface MessageProducer extends Shutdownable {

    /**
     * Send one message to the server.
     * 
     * @param message
     *            the message object
     * @return true if send successfully, false otherwise
     * @throws MessageClientException
     *             if any error occurs while sending the message
     */
    public boolean sendMessage(Message message) throws MessageClientException;
}
