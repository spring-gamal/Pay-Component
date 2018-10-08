package com.gamal.middleware.commons.service;

public interface PayConfig
{

    /**
     * 支付回调处理
     *
     * @author zfzhu
     * @param statusCode
     *            支付状态
     * @param message
     *            支付消息
     * @param tradeType
     *            交易类型
     * @param outTradeNo
     *            第三方交易单号
     * @param billNo
     *            系统交易单号（订单、退款单）
     * @param amount
     *            交易金额
     * @param buyerId
     *            买家账号
     */
    void notifyHandler(String statusCode, String message, String tradeType, String outTradeNo, String billNo,
            String amount, String buyerId);

}
