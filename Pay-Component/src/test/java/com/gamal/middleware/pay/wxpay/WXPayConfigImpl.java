package com.gamal.middleware.pay.wxpay;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.gamal.middleware.wxpay.bean.IWXPayDomain;
import com.gamal.middleware.wxpay.bean.WXPayConfig;
import com.gamal.middleware.wxpay.bean.WXPayConstants.SignType;

public class WXPayConfigImpl extends WXPayConfig
{

    private byte[] certData;
    private static WXPayConfigImpl INSTANCE;


    private WXPayConfigImpl() throws Exception
    {
        String OS = System.getProperty("os.name").toLowerCase();
        String certPath = "C:/Users/zfzhu/Desktop/aa/aa/apiclient_cert.p12";
        if (OS.indexOf("linux") >= 0)
        {
            certPath = "/data/wx/refund/apiclient_cert.p12";
        }

        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    public static WXPayConfig getInstance() throws Exception
    {
        if (INSTANCE == null)
        {
            synchronized (WXPayConfigImpl.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new WXPayConfigImpl();
                }
            }
        }
        return INSTANCE;
    }

    public String getAppID()
    {
        return "wxab8acb865bb1637e";
    }

    public String getMchID()
    {
        return "11473623";
    }

    public String getKey()
    {
        return "2ab9071b06b9f739b950ddb41db2690d";
    }

    public InputStream getCertStream()
    {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    public int getHttpConnectTimeoutMs()
    {
        return 2000;
    }

    public int getHttpReadTimeoutMs()
    {
        return 10000;
    }

    public IWXPayDomain getWXPayDomain()
    {
        return WXPayDomainSimpleImpl.instance();
    }

    public String getNotifyUrl()
    {
        return "http://test.letiantian.me/wxpay/notify";
    }

    public String getPrimaryDomain()
    {
        return "api.mch.weixin.qq.com";
    }

    public String getAlternateDomain()
    {
        return "api2.mch.weixin.qq.com";
    }

    @Override
    public int getReportWorkerNum()
    {
        return 1;
    }

    @Override
    public int getReportBatchSize()
    {
        return 2;
    }

    public void notifyHandler(String statusCode, String message, String tradeType, String outTradeNo, String billNo,
            String amount, String buyerId)
    {
        // TODO Auto-generated method stub

    }

    public SignType getSignType()
    {
        return SignType.MD5;
    }

}
