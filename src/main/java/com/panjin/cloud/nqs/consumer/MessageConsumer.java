/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.consumer;

import com.panjin.cloud.nqs.Message;
import com.panjin.cloud.nqs.Shutdownable;
import com.panjin.cloud.nqs.exception.MessageClientException;

/**
 * Interface for message consumer.
 *
 * @author panjin
 * @version $Id: MessageConsumer.java 2016年7月20日 上午10:25:43 $
 */
public interface MessageConsumer extends Shutdownable {
    
    /**
     * Get one message from the server.
     * 
     * @return a Message object if there is messages in the queue, null if none
     * @throws MessageClientException
     *             if any error occurs while getting message
     */
    public Message getMessage() throws MessageClientException;

    /**
     * Consume message with a handler. All received message will delivered to
     * the provided handler, and will send a ack command if the handle method
     * return true, nack if otherwise.
     * 
     * @param handler
     *            the handler object
     * @throws MessageClientException
     *             if any error occurs while consuming message
     */
    public void consumeMessage(MessageHandler handler) throws MessageClientException;
}
