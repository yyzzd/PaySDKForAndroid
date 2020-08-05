package com.davdian.service.dvdpay.bean;

/**
 *
 * @author dengyizheng
 * @date 2017/8/14
 * 微信支付结果
 */

public class WxPayResultEvent {
    private int resultCode;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}
