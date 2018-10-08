/**
 * Project Name:Pay-Component
 * File Name:PayFactory.java
 * Package Name:com.purcotton.middleware.pay
 * Date:2017年12月5日上午11:24:18
 * Copyright (c) 2017, All Rights Reserved.
 */

package com.gamal.middleware.commons;

import com.gamal.middleware.alipay.service.AliPayNotifyServiceImpl;
import com.gamal.middleware.alipay.service.AliPayServiceImpl;
import com.gamal.middleware.alipay.service.AliRefundServiceImpl;
import com.gamal.middleware.commons.PayConstants.PayType;
import com.gamal.middleware.commons.service.PayConfig;
import com.gamal.middleware.commons.service.PayNotifyService;
import com.gamal.middleware.commons.service.PayService;
import com.gamal.middleware.commons.service.RefundService;
import com.gamal.middleware.wxpay.service.WXPayNotifyServiceImpl;
import com.gamal.middleware.wxpay.service.WXPayServiceImpl;
import com.gamal.middleware.wxpay.service.WXRefundServiceImpl;

/**
 * 获取支付接口
 * Date: 2017年12月5日 上午11:24:18 <br/>
 * 
 * @author zfzhu
 * @version
 * @see
 */
public class PayFactory
{

    /**
     * 获取支付对象
     *
     * @author zfzhu
     * @param type
     * @param payConfig
     * @return
     * @throws Exception
     */
    public static PayService getPayService(PayType type, PayConfig payConfig) throws Exception
    {
        PayService payService = null;
        if (PayConstants.PayType.AliPay == type)
        {
            payService = new AliPayServiceImpl(payConfig);
        }
        else if (PayConstants.PayType.WXPay == type)// 微信支付
        {
            payService = new WXPayServiceImpl(payConfig);
        }
        return payService;
    }

    /**
     * 获取支付回调
     * getPayNotifyService:(这里用一句话描述这个方法的作用). <br/>
     *
     * @author zfzhu
     * @param type
     * @param payConfig
     * @return
     */
    public static PayNotifyService getPayNotifyService(PayType type, PayConfig payConfig)
    {
        PayNotifyService payNotifyService = null;
        if (PayConstants.PayType.AliPay == type)
        {
            payNotifyService = new AliPayNotifyServiceImpl(payConfig);
        }
        else if (PayConstants.PayType.WXPay == type)// 微信支付
        {
            payNotifyService = new WXPayNotifyServiceImpl(payConfig);
        }
        return payNotifyService;
    }

    /**
     * 获取退款服务接口
     *
     * @author zfzhu
     * @param type
     * @param payConfig
     * @return
     * @throws Exception
     */
    public static RefundService getRefundService(PayType type, PayConfig payConfig) throws Exception
    {
        RefundService refundService = null;
        if (PayConstants.PayType.AliPay == type)
        {
            refundService = new AliRefundServiceImpl(payConfig);
        }
        else if (PayConstants.PayType.WXPay == type)// 微信支付
        {
            refundService = new WXRefundServiceImpl(payConfig);
        }
        return refundService;
    }

}
