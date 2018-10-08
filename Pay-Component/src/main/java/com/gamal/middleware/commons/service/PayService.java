/**
 * Project Name:Pay-Component
 * File Name:PayService.java
 * Package Name:com.purcotton.middleware.pay
 * Date:2017年12月4日上午10:39:59
 * Copyright (c) 2017, All Rights Reserved.
 */

package com.gamal.middleware.commons.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * ClassName:PayService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2017年12月4日 上午10:39:59 <br/>
 * 
 * @author zfzhu
 * @version
 * @see
 */
public interface PayService {
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
	String mobilePay(String ip, String outTradeNo, String totalFee, String body) throws Exception;

	/**
	 * App 支付【同mobilePay ，升级后】
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
	String appPay(String ip, String outTradeNo, BigDecimal totalFee, String body) throws Exception;

	/**
	 * 支付宝--- PC 支付 微信 --手机浏览器支付
	 *
	 * @author zfzhu
	 * @param outTradeNo
	 *            商户订单号(在同一个商户号下唯一)
	 * @param totalFee
	 *            支付总金额
	 * @param body
	 *            商品描述
	 * @return
	 * @throws Exception
	 */
	String h5Pay(String outTradeNo, BigDecimal totalFee, String body) throws Exception;

	/**
	 * 微信 --- 公众号支付 支付宝-- 手机网站支付
	 * 
	 * @author zfzhu
	 * @param ip
	 * @param outTradeNo
	 *            商户订单号(在同一个商户号下唯一)
	 * @param totalFee
	 *            支付总金额
	 * @param body
	 *            商品描述
	 * @return
	 */
	Map<String, String> wapPay(String ip, String outTradeNo, BigDecimal totalFee, String body) throws Exception;

	/**
	 * 小程序支付
	 *
	 * @author zfzhu
	 * @param openId
	 *            小程序分配给用户的ID
	 * @param ip
	 * @param outTradeNo
	 *            商户订单号(在同一个商户号下唯一)
	 * @param totalFee
	 *            支付总金额
	 * @param body
	 *            商品描述
	 * @return
	 */
	Map<String, String> mpPay(String openId, String ip, String outTradeNo, BigDecimal totalFee, String body)
			throws Exception;

}
