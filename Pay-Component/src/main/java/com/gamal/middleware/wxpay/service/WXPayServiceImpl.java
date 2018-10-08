/**
 * Project Name:Pay-Component
 * File Name:WechatPayService.java
 * Package Name:com.purcotton.middleware.pay.wxpay
 * Date:2017年12月4日上午10:48:27
 * Copyright (c) 2017, All Rights Reserved.
 */

package com.gamal.middleware.wxpay.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.gamal.middleware.commons.service.PayConfig;
import com.gamal.middleware.commons.service.PayService;
import com.gamal.middleware.wxpay.bean.WXPay;
import com.gamal.middleware.wxpay.bean.WXPayConfig;
import com.gamal.middleware.wxpay.bean.WXPayUtil;
import com.gamal.middleware.wxpay.bean.WXPayConstants.SignType;

/**
 * 微信支付
 * Date: 2017年12月4日 上午10:48:27 <br/>
 * 
 * @author zfzhu
 * @version
 * @see
 */
public class WXPayServiceImpl implements PayService
{
    private WXPay wxpay;
    private WXPayConfig wxPayConfig;

    private Logger logger = LoggerFactory.getLogger(WXPayServiceImpl.class);

    public WXPayServiceImpl(final PayConfig payconfig) throws Exception
    {
        super();
        wxPayConfig = (WXPayConfig) payconfig;
        this.wxpay = new WXPay((WXPayConfig) payconfig, false, wxPayConfig.useSandbox());
    }

    public String mobilePay(String ip, String outTradeNo, String totalFee, String body) throws Exception
    {
        return null;
    }

    public String appPay(String ip, String outTradeNo, BigDecimal totalFee, String body) throws Exception
    {
        String tradeType = "APP";

        totalFee = totalFee.multiply(new BigDecimal(100)).setScale(0);
        Map<String, String> map = unifiedOrder(ip, outTradeNo, totalFee, body, tradeType);

        SignType signType = null;
        // if (wxPayConfig.useMD5())
        // {
        // signType = SignType.MD5; // 沙箱环境
        // }
        // else
        // {
        // signType = SignType.HMACSHA256;
        // }
        signType = wxPayConfig.getSignType();

        String prepayId = map.get("prepay_id");
        Map<String, String> payParam = new TreeMap<String, String>();
        if (prepayId == null)
        {
            throw new RuntimeException("(appPay)统一支付，获取预支付Id异常。。。。。。。。。。。。。。err_code：" + map.get("err_code")
                    + " | err_code_des:" + map.get("err_code_des") + " | return_msg:" + map.get("return_msg"));
        }
        payParam.put("prepayid", prepayId);
        payParam.put("noncestr", WXPayUtil.generateUUID());
        payParam.put("appid", wxPayConfig.getAppID());
        payParam.put("partnerid", wxPayConfig.getMchID());
        payParam.put("package", "Sign=WXPay");
        payParam.put("timestamp", System.currentTimeMillis() / 1000 + "");
        payParam.put("sign", WXPayUtil.generateSignature(payParam, wxPayConfig.getKey(), signType));

        return JSON.toJSONString(payParam);
    }

    /**
     * 统一下单
     *
     * @author zfzhu
     * @param ip
     * @param outTradeNo
     * @param totalFee
     * @param body
     * @param tradeType
     * @return
     * @throws Exception
     */
    private Map<String, String> unifiedOrder(String ip, String outTradeNo, BigDecimal totalFee, String body,
            String tradeType) throws Exception
    {
        if (totalFee != null)
        {
            totalFee = totalFee.setScale(0, BigDecimal.ROUND_HALF_UP);
        }
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.putAll(wxPayConfig.getAllExtendParamsMap());
        paramMap.put("spbill_create_ip", ip);
        paramMap.put("total_fee", totalFee.toString());
        paramMap.put("out_trade_no", outTradeNo);
        paramMap.put("body", body);
        paramMap.put("trade_type", tradeType);

        return wxpay.unifiedOrder(paramMap);
    }

    public String h5Pay(String outTradeNo, BigDecimal totalFee, String body) throws Exception
    {
        String tradeType = "MWEB";
        unifiedOrder(null, outTradeNo, totalFee, body, tradeType);
        return null;
    }

    public Map<String, String> wapPay(String ip, String outTradeNo, BigDecimal totalFee, String body) throws Exception
    {

        String tradeType = "JSAPI";
        unifiedOrder(ip, outTradeNo, totalFee, body, tradeType);
        return null;
    }

    /**
     * 小程序支付
     */
    public Map<String, String> mpPay(String openId, String ip, String outTradeNo, BigDecimal totalFee, String body)
            throws Exception
    {
        Map<String, String> payParam = new HashMap<String, String>();
        wxPayConfig.put("openid", openId);
        // 统一支付
        Map<String, String> map = unifiedOrder(ip, outTradeNo, totalFee, body, "JSAPI");
        if (MapUtils.isNotEmpty(map))
        {
            String returnCode = map.get("return_code");// 通信标示
            String resultCode = map.get("result_code");// 是否成功标示
            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode))
            {
                SignType signType = null;
                // if (wxPayConfig.useMD5())
                // {
                // signType = SignType.MD5; // 沙箱环境
                // }
                // else
                // {
                // signType = SignType.HMACSHA256;
                // }
                signType = wxPayConfig.getSignType();

                payParam.put("appId", map.get("appid"));// 小程序id
                payParam.put("nonceStr", WXPayUtil.generateUUID());
                // payParam.put("nonceStr", map.get("nonceStr"));
                payParam.put("package", "prepay_id=" + map.get("prepay_id"));// 预支付交易会话标识
                payParam.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
                payParam.put("signType", signType.toString());
                payParam.put("paySign", WXPayUtil.generateSignature(payParam, wxPayConfig.getKey(), signType));
                payParam.put("errCodeDes", "");
                payParam.put("errCode", "");
                payParam.remove("appId");
                return payParam;
            }
            else
            {
                logger.error("(mpPay)统一支付，获取预支付Id异常。。。。。。。。。。。。。。err_code：{}  | err_code_des:{}  | return_msg:{}",
                        map.get("err_code"), map.get("err_code_des"), map.get("return_msg"));
                payParam.put("errCodeDes", map.get("err_code_des"));
                payParam.put("errCode", map.get("err_code"));
                return payParam;
            }
        }
        return null;
    }

}
