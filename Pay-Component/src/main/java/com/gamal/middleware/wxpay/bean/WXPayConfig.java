package com.gamal.middleware.wxpay.bean;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.gamal.middleware.commons.service.PayConfig;
import com.gamal.middleware.wxpay.bean.WXPayConstants.SignType;

public abstract class WXPayConfig implements PayConfig
{

    /**
     * 扩展参数
     */
    private Map<String, String> extendParamsMap = new HashMap<String, String>();


    /**
     * 获取 App ID
     *
     * @return App ID
     */
    public abstract String getAppID();

    /**
     * 获取 Mch ID
     *
     * @return Mch ID
     */
    public abstract String getMchID();

    /**
     * 获取 API 密钥
     *
     * @return API密钥
     */
    public abstract String getKey();

    /**
     * 获取商户证书内容
     *
     * @return 商户证书内容
     */
    public abstract InputStream getCertStream();

    public void put(String key, String value)
    {
        extendParamsMap.put(key, value);
    }

    public String get(String key)
    {
        return extendParamsMap.get(key);
    }

    /**
     * 获取通知URL
     *
     * @author zfzhu
     * @return
     */
    public abstract String getNotifyUrl();

    /**
     * HTTP(S) 连接超时时间，单位毫秒
     *
     * @return
     */
    public int getHttpConnectTimeoutMs()
    {
        return 6 * 1000;
    }

    /**
     * HTTP(S) 读数据超时时间，单位毫秒
     *
     * @return
     */
    public int getHttpReadTimeoutMs()
    {
        return 8 * 1000;
    }

    /**
     * 获取WXPayDomain, 用于多域名容灾自动切换
     * 
     * @return
     */
    public abstract IWXPayDomain getWXPayDomain();

    public abstract SignType getSignType();

    /**
     * 是否自动上报。
     * 若要关闭自动上报，子类中实现该函数返回 false 即可。
     *
     * @return
     */
    public boolean shouldAutoReport()
    {
        return true;
    }

    /**
     * 进行健康上报的线程的数量
     *
     * @return
     */
    public int getReportWorkerNum()
    {
        return 6;
    }

    /**
     * 健康上报缓存消息的最大数量。会有线程去独立上报
     * 粗略计算：加入一条消息200B，10000消息占用空间 2000 KB，约为2MB，可以接受
     *
     * @return
     */
    public int getReportQueueMaxSize()
    {
        return 10000;
    }

    /**
     * 批量上报，一次最多上报多个数据
     *
     * @return
     */
    public int getReportBatchSize()
    {
        return 10;
    }

    public boolean useSandbox()
    {
        return false;
    }


    public Map<String, String> getAllExtendParamsMap()
    {
        return extendParamsMap;
    }
}
