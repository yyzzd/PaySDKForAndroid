package com.davdian.service.dvdpay.resultinterface;

/**
 * @author dengyizheng
 * @date 2017/8/12
 */

public interface OnSdkPayFinishListener {
    /**
     * 支付成功
     *
     * @param type   支付渠道
     * @param msg    提示
     * @param result 支付结果码
     */
    void onPaySuccess(String type, String msg, String result);

    /**
     * 支付失败
     *
     * @param type   支付渠道
     * @param msg    提示
     * @param result 支付结果码
     */
    void onPayFailed(String type, String msg, String result);

    /**
     * 支付取消
     *
     * @param type   支付渠道
     * @param msg    提示
     * @param result 支付结果码
     */
    void onPayCancel(String type, String msg, String result);
}
