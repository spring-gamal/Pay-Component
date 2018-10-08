/**
 * Project Name:Pay-Component
 * File Name:WXPayNotifyService.java
 * Package Name:com.purcotton.middleware.wxpay.service
 * Date:2017年12月11日上午9:15:42
 * Copyright (c) 2017, All Rights Reserved.
 */

package com.gamal.middleware.wxpay.service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gamal.middleware.commons.PayConstants;
import com.gamal.middleware.commons.service.PayConfig;
import com.gamal.middleware.commons.service.PayNotifyService;
import com.gamal.middleware.wxpay.bean.WXPayConfig;
import com.gamal.middleware.wxpay.bean.WXPayUtil;

/**
 * 微信支付回调
 * ClassName:WXPayNotifyService <br/>
 * Date: 2017年12月11日 上午9:15:42 <br/>
 * 
 * @author zfzhu
 * @version
 * @see
 */
public class WXPayNotifyServiceImpl implements PayNotifyService
{

    private WXPayConfig wxPayConfig;

    public WXPayNotifyServiceImpl(PayConfig payConfig)
    {
        super();
        wxPayConfig = (WXPayConfig) payConfig;
    }

    public synchronized void notify(HttpServletRequest request, HttpServletResponse response) throws RuntimeException
    {
        String notityXml = "";
        String inputLine = "";
        try
        {
            while ((inputLine = request.getReader().readLine()) != null)
            {
                notityXml += inputLine;
            }
            request.getReader().close();

            Map<String, String> paramMap = WXPayUtil.xmlToMap(notityXml);

            String returnCode = paramMap.get("return_code");// 返回状态码
            String returnMsg = paramMap.get("return_msg");
            String resultCode = paramMap.get("result_code");// 业务结果

            if ("SUCCESS".equals(returnCode) && "SUCCESS".equals(resultCode))
            {
                String errCode = paramMap.get("err_code");// 错误代码
                String errCodeDes = paramMap.get("err_code_des");// 错误代码描述
                String outTradeNo = paramMap.get("out_trade_no");// 商户订单号
                String transactionId = paramMap.get("transaction_id");// 微信支付订单号
                String cashFee = paramMap.get("cash_fee");// 现金支付金额
                String openid = paramMap.get("openid");// 用户在商户appid下的唯一标识

                returnCode = resultCode;
                returnMsg = errCode + " | " + errCodeDes;

                if (WXPayUtil.isSignatureValid(paramMap, wxPayConfig.getKey()))// 验证签名是否正确
                {
                    // 支付日志
                    wxPayConfig.notifyHandler(returnCode, returnMsg, PayConstants.PayType.WXPay.toString(),
                            transactionId, outTradeNo, cashFee, openid);
                }
                else
                {
                    throw new RuntimeException("微信支付回调验证失败。notityXml：" + notityXml);
                }
            }
            else // 验证失败
            {
                throw new RuntimeException("微信支付回调验证失败。notityXml：" + notityXml);
            }
        }
        catch (IOException exception)
        {
            throw new RuntimeException(exception.getMessage(), exception.getCause());
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception.getMessage(), exception.getCause());
        }
        finally
        {
            // 返回微信
            try
            {
                wxOutPrint(response);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * 返回通知微信：通知成功
     *
     * @author zfzhu
     * @param response
     * @throws Exception
     */
    private void wxOutPrint(HttpServletResponse response) throws Exception
    {
        Map<String, String> returnParam = new HashMap<String, String>();
        returnParam.put("return_code", "SUCCESS");
        returnParam.put("return_msg", "OK");
        String resXml = WXPayUtil.mapToXml(returnParam);
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        out.write(resXml.getBytes());
        out.flush();
        out.close();
    }

    /**
     * 移动支付回调通知【老版本】
     *
     * @author zfzhu
     * @param request
     * @param response
     */
    public void mobileNotify(HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        // TODO Auto-generated method stub

    }

}
