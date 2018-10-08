package com.gamal.middleware.alipay.service;

import java.math.BigDecimal;
import java.util.Map;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.gamal.middleware.alipay.bean.AliPayConfig;
import com.gamal.middleware.commons.service.PayConfig;
import com.gamal.middleware.commons.service.RefundService;

/**
 * 阿里退款申请
 * 
 * @author zfzhu
 */
public class AliRefundServiceImpl implements RefundService
{

    private AliPayConfig aliPayConfig;

    private AlipayClient alipayClient;

    public AliRefundServiceImpl(PayConfig payConfig)
    {
        super();
        aliPayConfig = (AliPayConfig) payConfig;
        // 实例化客户端
        alipayClient = new DefaultAlipayClient(aliPayConfig.getPayGateway(), aliPayConfig.getAppID(),
                aliPayConfig.getAppPrivateKey(), "json", AlipayConstants.CHARSET_UTF8, aliPayConfig.getAliPublicKey(),
                "RSA2");
    }

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
     *         RefundId 微信退款号
     *         errCode 退款申请错误码
     *         errCodeDesc 退款申请错误描述
     * @throws Exception
     */
    public Map<String, String> mpRefundApply(String orderNo, String refundNo, BigDecimal orderTotalFee,
            BigDecimal refundTotalFee, String refundDesc) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, String> mpRefundQuery(String refundNo) throws Exception
    {
        // TODO Auto-generated method stub
        return null;
    }

}
