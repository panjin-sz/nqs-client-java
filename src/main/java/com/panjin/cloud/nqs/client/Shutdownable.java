/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.client;

/**
 * Interface for shutdownable object.
 *
 * @author panjin
 * @version $Id: Shutdownable.java 2016年7月19日 下午6:23:59 $
 */
public interface Shutdownable {

    /**
     * Shut down the object.
     */
    public void shutdown();
}
