/**
 * Project Name:Pay-Component
 * File Name:RefundService.java
 * Package Name:com.purcotton.middleware.commons.service
 * Date:2017年12月18日上午10:25:14
 * Copyright (c) 2017, All Rights Reserved.
 */

package com.gamal.middleware.commons.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 退款业务
 * ClassName:RefundService <br/>
 * Date: 2017年12月18日 上午10:25:14 <br/>
 * 
 * @author zfzhu
 * @version
 * @see
 */
public interface RefundService
{
    /**
     * 小程序退款申请
     * 
     * @param orderNo
     *            订单号
     * @param refundNo
     *            退款号
     * @param orderTotalFee
     *            退款订单总金额(单位：元)
     * @param refundTotalFee
     *            退款金额(单位：元)
     * @param refundDesc
     *            退款描述(若商户传入，会在下发给用户的退款消息中体现退款原因)
     * @return
     *         statusCode 退款状态码
     *         wxRefundId 微信退款号
     *         errCode 退款申请错误码
     *         errCodeDesc 退款申请错误描述
     * @throws Exception
     */
    Map<String, String> mpRefundApply(String orderNo, String refundNo, BigDecimal orderTotalFee,
            BigDecimal refundTotalFee, String refundDesc) throws Exception;

    /**
     * 小程序退款查询
     * 
     * @param refundNo
     *            退款单号
     * @return
     *         statusCode 退款状态码
     *         refundChannel 退款渠道
     *         refundStatus 退款状态
     *         refundRecvAccout 退款入账账户
     *         refundSuccessTime 退款成功时间
     *         errCode 退款申请错误码
     *         errCodeDesc 退款申请错误描述
     * @throws Exception
     */
    public Map<String, String> mpRefundQuery(String refundNo) throws Exception;

}
