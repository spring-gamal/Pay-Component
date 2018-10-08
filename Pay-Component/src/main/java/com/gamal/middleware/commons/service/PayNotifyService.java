/**
 * Project Name:Pay-Component
 * File Name:PayNotifyService.java
 * Package Name:com.purcotton.middleware.commons.service
 * Date:2017年12月11日上午8:53:41
 * Copyright (c) 2017, All Rights Reserved.
 */

package com.gamal.middleware.commons.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支付回调
 * ClassName:PayNotifyService <br/>
 * Date: 2017年12月11日 上午8:53:41 <br/>
 * 
 * @author zfzhu
 * @version
 * @see
 */
public interface PayNotifyService
{
    /**
     * 支付回调通知
     *
     * @author zfzhu
     * @param request
     * @param response
     */
    void notify(HttpServletRequest request, HttpServletResponse response) throws RuntimeException;

    /**
     * 移动支付回调通知【老版本】
     *
     * @author zfzhu
     * @param request
     * @param response
     */
    void mobileNotify(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
