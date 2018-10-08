/**
 * Project Name:Pay-Component
 * File Name:AliPayService.java
 * Package Name:com.purcotton.middleware.pay.alipay
 * Date:2017年12月4日上午10:48:03
 * Copyright (c) 2017, All Rights Reserved.
 */

package com.gamal.middleware.alipay.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Map;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.gamal.middleware.alipay.bean.AliPayConfig;
import com.gamal.middleware.alipay.bean.Base64;
import com.gamal.middleware.commons.service.PayConfig;
import com.gamal.middleware.commons.service.PayService;

/**
 * 支付宝支付 Date: 2017年12月4日 上午10:48:03 <br/>
 * 
 * @author zfzhu
 * @version
 * @see
 */
public class AliPayServiceImpl implements PayService {

	private AliPayConfig aliPayConfig;

	private AlipayClient alipayClient;

	public AliPayServiceImpl(PayConfig payConfig) {
		super();
		aliPayConfig = (AliPayConfig) payConfig;
		// 实例化客户端
		alipayClient = new DefaultAlipayClient(aliPayConfig.getPayGateway(), aliPayConfig.getAppID(),
				aliPayConfig.getAppPrivateKey(), "json", AlipayConstants.CHARSET_UTF8, aliPayConfig.getAliPublicKey(),
				"RSA2");
	}

	/**
	 * 移动 支付【旧版本】
	 *
	 * @author zfzhu
	 * @param ip
	 *            远程IP
	 * @param outTradeNo
	 *            商户订单号(在同一个商户号下唯一)
	 * @param totalFee
	 *            支付总金额
	 * @param body
	 *            商品描述
	 * @return
	 */
	public String mobilePay(String ip, String outTradeNo, String totalFee, String body) throws Exception {
		if (alipayClient == null) {
			throw new RuntimeException("支付宝客户端获取异常");
		}

		StringBuffer sb = new StringBuffer();
		sb.append("partner=\"").append(aliPayConfig.getAppID()).append("\"");
		sb.append("&seller_id=\"").append(aliPayConfig.getAppID()).append("\"");
		sb.append("&out_trade_no=\"").append(outTradeNo).append("\"");
		sb.append("&subject=\"").append(aliPayConfig.getSubject()).append("\"");
		sb.append("&body=\"").append(body).append("\"");
		sb.append("&total_fee=\"").append(totalFee.toString()).append("\"");
		sb.append("&notify_url=\"").append(aliPayConfig.getNotifyUrl()).append("\"");
		sb.append("&service=\"").append(aliPayConfig.getService()).append("\"");
		sb.append("&payment_type=\"1\"");
		sb.append("&_input_charset=\"UTF-8\"");
		sb.append("&it_b_pay=\"").append(aliPayConfig.getTimeoutExpress()).append("\"");

		String sign = mobileSign(sb.toString(), aliPayConfig.getAppPrivateKey());

		sign = URLEncoder.encode(sign, "UTF-8");

		sb.append("&sign=\"").append(sign).append("\"");
		sb.append("&sign_type=\"RSA\"");

		return sb.toString();

	}

	/**
	 * 老版本加密
	 *
	 * @author zfzhu
	 * @param content
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	private String mobileSign(String content, String privateKey) throws Exception {
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
		KeyFactory keyf = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyf.generatePrivate(priPKCS8);

		Signature signature = Signature.getInstance("SHA1WithRSA");

		signature.initSign(priKey);
		signature.update(content.getBytes("UTF-8"));

		byte[] signed = signature.sign();

		return Base64.encode(signed);
	}

	public String appPay(String ip, String outTradeNo, BigDecimal totalFee, String body) throws Exception {
		if (alipayClient == null) {
			throw new RuntimeException("支付宝客户端获取异常");
		}

		// 实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
		AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
		// SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
		AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
		model.setBody(body);
		model.setSubject(aliPayConfig.getSubject());
		model.setOutTradeNo(outTradeNo);
		model.setTimeoutExpress(aliPayConfig.getTimeoutExpress());
		model.setTotalAmount(totalFee.toString());
		model.setProductCode("QUICK_MSECURITY_PAY"); // 固定值
		request.setBizModel(model);
		request.setNotifyUrl(aliPayConfig.getNotifyUrl());

		// 这里和普通的接口调用不同，使用的是sdkExecute
		AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
		System.out.println(response.getBody());// 就是orderString
												// 可以直接给客户端请求，无需再做处理。

		return response.getBody();
	}

	public String h5Pay(String outTradeNo, BigDecimal totalFee, String body) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("    \"out_trade_no\":").append(outTradeNo).append(",");
		sb.append("    \"product_code\":\"FAST_INSTANT_TRADE_PAY\",").append(",");
		sb.append("    \"total_amount\":").append(totalFee.toString()).append(",");
		sb.append("    \"subject\":").append(aliPayConfig.getSubject()).append(",");
		sb.append("    \"body\":").append(body).append(",");
		sb.append("}");

		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();// 创建API对应的request
		alipayRequest.setReturnUrl(aliPayConfig.getReturnUrl());
		alipayRequest.setNotifyUrl(aliPayConfig.getNotifyUrl());// 在公共参数中设置回跳和通知地址
		alipayRequest.setBizContent(sb.toString());// 填充业务参数
		String form = alipayClient.pageExecute(alipayRequest).getBody(); // 调用SDK生成表单
		return form;
	}

	public Map<String, String> wapPay(String ip, String outTradeNo, BigDecimal totalFee, String body) throws Exception {

		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("    \"out_trade_no\":").append(outTradeNo).append(",");
		sb.append("    \"product_code\":\"QUICK_WAP_PAY\",").append(",");
		sb.append("    \"total_amount\":").append(totalFee.toString()).append(",");
		sb.append("    \"subject\":").append(aliPayConfig.getSubject());
		// sb.append(" \"body\":").append(body).append(",");
		sb.append("}");

		AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();// 创建API对应的request
		alipayRequest.setReturnUrl(aliPayConfig.getReturnUrl());
		alipayRequest.setNotifyUrl(aliPayConfig.getNotifyUrl());// 在公共参数中设置回跳和通知地址
		alipayRequest.setBizContent(sb.toString());// 填充业务参数
		String form = alipayClient.pageExecute(alipayRequest).getBody(); // 调用SDK生成表单

		return null;
	}

	/**
	 * 小程序支付
	 */
	public Map<String, String> mpPay(String openId, String ip, String outTradeNo, BigDecimal totalFee, String body)
			throws Exception {

		return null;
	}

	public Map<String, String> mpRefund(String openId, String ip, String outTradeNo, BigDecimal totalFee,
			BigDecimal refundFee, String body) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
