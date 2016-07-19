/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.cloud.nqs.auth;

/**
 * 获取RabbitMQ认证信息接口
 *
 * @author panjin
 * @version $Id: AuthBackend.java 2016年7月19日 下午5:57:54 $
 */
public interface AuthBackend {

    /**
     * 获取后端RabbitMQ的用户名
     * 
     * @return 用户名
     */
    public String getUsername();
    
    /**
     * 获取后端RabbitMQ的密码
     * 
     * @return
     */
    public String getPassword();
}
