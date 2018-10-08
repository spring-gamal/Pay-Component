package com.gamal.middleware.alipay.bean;

import java.util.Map;

import com.gamal.middleware.commons.service.PayConfig;

public abstract class AliPayConfig implements PayConfig
{

    /**
     * 获取 App ID
     *
     * @return App ID
     */
    public abstract String getAppID();

    // /**
    // * 获取 Mch ID
    // *
    // * @return Mch ID
    // */
    // public abstract String getMchID();

    /**
     * 私钥
     *
     * @return
     */
    public abstract String getAppPrivateKey();

    /**
     * 阿里公钥
     *
     * @return
     */
    public abstract String getAliPublicKey();

    /**
     * 主题
     *
     * @return
     */
    public abstract String getSubject();

    /**
     * 获取通知URL
     *
     * @author zfzhu
     * @return
     */
    public abstract String getNotifyUrl();

    /**
     * 获取返回URL（页面通知URL）
     *
     * @author zfzhu
     * @return
     */
    public abstract String getReturnUrl();

    /**
     * 获取扩展参数
     *
     * @author zfzhu
     * @return
     */
    public abstract Map<String, String> getExtendParams();

    /**
     * 获取支付网关
     *
     * @author zfzhu
     * @return
     */
    public String getPayGateway()
    {
        return "https://openapi.alipay.com/gateway.do";
    }

    public String getTimeoutExpress()
    {
        return "30m";
    }

    /**
     * 获取服务名（老版本）
     *
     * @author zfzhu
     * @return
     */
    public String getService()
    {
        return "mobile.securitypay.pay";
    }

}
