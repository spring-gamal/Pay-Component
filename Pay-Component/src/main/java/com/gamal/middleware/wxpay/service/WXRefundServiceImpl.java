package com.gamal.middleware.wxpay.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gamal.middleware.commons.service.PayConfig;
import com.gamal.middleware.commons.service.RefundService;
import com.gamal.middleware.wxpay.bean.WXPay;
import com.gamal.middleware.wxpay.bean.WXPayConfig;

public class WXRefundServiceImpl implements RefundService
{
    private Logger logger = LoggerFactory.getLogger(WXRefundServiceImpl.class);

    private WXPay wxpay;
    private WXPayConfig wxPayConfig;

    public WXRefundServiceImpl(final PayConfig payconfig) throws Exception
    {
        super();
        wxPayConfig = (WXPayConfig) payconfig;
        this.wxpay = new WXPay((WXPayConfig) payconfig, false, wxPayConfig.useSandbox());
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
     *         wxRefundId 微信退款号
     *         errCode 退款申请错误码
     *         errCodeDesc 退款申请错误描述
     * @throws Exception
     */
    public Map<String, String> mpRefundApply(String orderNo, String refundNo, BigDecimal orderTotalFee,
            BigDecimal refundTotalFee, String refundDesc) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("小程序退款申请，参数：orderNo:{},refundNo:{},refundTotalFee:{},desc:{}", orderNo, refundNo,
                    refundTotalFee, refundDesc);
        }

        // 微信数值记录的是分
        orderTotalFee = orderTotalFee.multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
        refundTotalFee = refundTotalFee.multiply(BigDecimal.valueOf(100)).setScale(0, BigDecimal.ROUND_HALF_UP);

        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put("out_trade_no", orderNo);
        reqData.put("out_refund_no", refundNo);
        reqData.put("total_fee", orderTotalFee.toString());
        reqData.put("refund_fee", refundTotalFee.toString());
        reqData.put("refund_desc", refundDesc);

        Map<String, String> resultMap = wxpay.refund(reqData);
        if (MapUtils.isNotEmpty(resultMap) && "SUCCESS".equals(resultMap.get("return_code"))
                && "SUCCESS".equals(resultMap.get("result_code")))
        {
            resultMap.put("statusCode", "SUCCESS");
        }
        else
        {
            String errCode = resultMap.get("err_code");
            String errCodeDesc = resultMap.get("err_code_des");
            logger.error("微信小程序退款异常, return_msg:{}, err_code:{}, err_code_des:{}, 返回参数：{}", resultMap.get("return_msg"),
                    errCode, errCodeDesc, resultMap.toString());

            resultMap.put("statusCode", "FAIL");
            resultMap.put("errCode", errCode);
            resultMap.put("errCodeDesc", errCodeDesc);
        }
        resultMap.put("wxRefundId", resultMap.get("refund_id"));

        return resultMap;
    }

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
    public Map<String, String> mpRefundQuery(String refundNo) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("微信小程序退款查询，refundNo：{}", refundNo);
        }

        Map<String, String> reqData = new HashMap<String, String>();
        reqData.put("out_refund_no", refundNo);
        Map<String, String> resultMap = wxpay.refundQuery(reqData);
        if (MapUtils.isNotEmpty(resultMap) && "SUCCESS".equals(resultMap.get("return_code"))
                && "SUCCESS".equals(resultMap.get("result_code")) && "SUCCESS".equals(resultMap.get("refund_status_0")))
        {
            resultMap.put("statusCode", "SUCCESS");
            resultMap.put("refundChannel", resultMap.get("refund_channel_0")); // 退款渠道
            resultMap.put("refundStatus", resultMap.get("refund_status_0"));// 退款状态
            resultMap.put("refundRecvAccout", resultMap.get("refund_recv_accout_0"));// 退款入账账户
            resultMap.put("refundSuccessTime", resultMap.get("refund_success_time_0"));// 退款成功时间
            resultMap.put("wxRefundId", resultMap.get("refund_id_1"));
        }
        else
        {
            String errCode = resultMap.get("err_code");
            String errCodeDesc = resultMap.get("err_code_des");

            resultMap.put("statusCode", "FAIL");
            resultMap.put("errCode", errCode);
            resultMap.put("errCodeDesc", errCodeDesc);

            if ("PROCESSING".equals(resultMap.get("refund_status_0")))// 退款进行中
            {
                resultMap.put("statusCode", "PROCESSING");
                logger.info("微信小程序退款进行中, return_msg:{}, err_code:{}, err_code_des:{}, 返回参数：{}",
                        resultMap.get("return_msg"), errCode, errCodeDesc, resultMap.toString());
            }
            else
            {
                logger.error("微信小程序退款查询异常, return_msg:{}, err_code:{}, err_code_des:{}, 返回参数：{}",
                        resultMap.get("return_msg"), errCode, errCodeDesc, resultMap.toString());
            }

        }
        return resultMap;
    }

}
