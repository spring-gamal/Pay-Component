package com.gamal.middleware.alipay.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import com.gamal.middleware.alipay.bean.AliPayConfig;
import com.gamal.middleware.alipay.bean.RSA;
import com.gamal.middleware.commons.PayConstants;
import com.gamal.middleware.commons.service.PayConfig;
import com.gamal.middleware.commons.service.PayNotifyService;

/**
 * 支付宝支付回调
 * Date: 2017年12月11日 上午9:14:58 <br/>
 * 
 * @author zfzhu
 * @version
 * @see
 */
public class AliPayNotifyServiceImpl implements PayNotifyService
{

    private AliPayConfig aliPayConfig;

    public AliPayNotifyServiceImpl(PayConfig payConfig)
    {
        super();
        aliPayConfig = (AliPayConfig) payConfig;
    }

    @SuppressWarnings("rawtypes")
    public synchronized void notify(HttpServletRequest request, HttpServletResponse response) throws RuntimeException
    {
        String valueStr = "";
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();)
        {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);

            for (int i = 0; i < values.length; i++)
            {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        try
        {
            String tradeStatus = params.get("trade_status");
            if ("TRADE_SUCCESS".equals(tradeStatus)) // 验证交易是否成功
            {
                boolean flag = AlipaySignature.rsaCheckV1(params, aliPayConfig.getAliPublicKey(), AlipayConstants.CHARSET_UTF8, "RSA2");
                if (flag)
                {
                    String tradeNo = params.get("trade_no");
                    String outTradeNo = params.get("out_trade_no");
                    String totalFee = params.get("total_fee");
                    String buyerId = params.get("buyer_email"); // 买家账号

                    // 支付日志
                    aliPayConfig.notifyHandler(tradeStatus, "", PayConstants.PayType.AliPay.toString(), tradeNo, outTradeNo, totalFee, buyerId);
                }
                else // 验证异常
                {
                    throw new RuntimeException("微信支付回调验证失败。notityStr：" + valueStr);
                }
            }
            else// 验证异常
            {
                throw new RuntimeException("微信支付回调验证失败。notityStr：" + valueStr);
            }
        }
        catch (AlipayApiException e)
        {
            throw new RuntimeException(e.getErrCode() + " | " + e.getErrMsg(), e.getCause());
        }
        finally
        {
            try
            {
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write("success".getBytes());
                out.flush();
                out.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException("微信支付回调验证失败。notityStr：" + valueStr);
            }
        }
    }

    /**
     * 移动支付回调通知【老版本】
     *
     * @author zfzhu
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public void mobileNotify(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String valueStr = "";
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new TreeMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();)
        {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);

            for (int i = 0; i < values.length; i++)
            {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        try
        {
            // 交易状态
            String tradeStatus = params.get("trade_status");
            if (tradeStatus.equals("TRADE_FINISHED") || "TRADE_SUCCESS".equals(tradeStatus)) // 验证交易是否成功
            {
                if (notfiyCheck(params))
                {
                    String tradeNo = params.get("trade_no");
                    String outTradeNo = params.get("out_trade_no");
                    String totalFee = params.get("total_fee");
                    String buyerId = params.get("buyer_email"); // 买家账号

                    // 支付日志
                    aliPayConfig.notifyHandler(tradeStatus, "", PayConstants.PayType.AliPay.toString(), tradeNo, outTradeNo, totalFee, buyerId);
                }
                else // 验证异常
                {
                    throw new RuntimeException("微信支付回调验证失败。notityStr：" + valueStr);
                }
            }
            else// 验证异常
            {
                throw new RuntimeException("微信支付回调验证失败。notityStr：" + valueStr);
            }
        }
        catch (AlipayApiException e)
        {
            throw new RuntimeException(e.getErrCode() + " | " + e.getErrMsg(), e.getCause());
        }
        finally
        {
            try
            {
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write("success".getBytes());
                out.flush();
                out.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException("微信支付回调验证失败。notityStr：" + valueStr);
            }
        }
    }

    /**
     * 支付回调验证
     *
     * @author zfzhu
     * @param params
     * @return
     * @throws Exception
     */
    private boolean notfiyCheck(Map<String, String> params) throws Exception
    {
        boolean responseTxt = true;
        if (params.get("notify_id") != null)
        {
            String notifyId = params.get("notify_id");
            responseTxt = urlVerify(notifyId);
        }

        StringBuffer sb = new StringBuffer();
        Iterator<String> it = params.keySet().iterator();
        while (it.hasNext())
        {
            String key = it.next();
            if (key.equalsIgnoreCase("sign") || key.equalsIgnoreCase("sign_type"))
            {
                continue;
            }
            sb.append(key).append(params.get(key)).append("&");
        }

        String sign = params.get("sign");
        String signStr = sb.substring(0, sb.length() - 1);
        boolean isSign = RSA.verify(signStr, sign, aliPayConfig.getAliPublicKey(), "utf-8");

        return (isSign && responseTxt);
    }

    /**
     * 获取远程服务器ATN结果,验证返回URL
     * 
     * @author zfzhu
     * @param notifyId
     * @return
     * @throws
     *             @throws
     *             Exception
     */
    private boolean urlVerify(String notifyId) throws Exception
    {
        String urlV = "https://mapi.alipay.com/gateway.do?service=notify_verify&partner=" + aliPayConfig.getAppID() + "&notify_id=" + notifyId;

        URL url = new URL(urlV);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String inputLine = in.readLine();
        return inputLine.equals("true");
    }

}
