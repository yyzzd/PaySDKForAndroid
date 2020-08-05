package com.davdian.service.dvdpay.resultinterface;

/**
 * @author dengyizheng
 * @date 2017/8/12
 */

public interface OnPayFinishListener {
    /**
     * 支付成功
     *
     * @param type       支付渠道
     * @param msg        提示
     * @param resultCode 支付结果码
     */
    void onPaySuccess(String type, String msg, String resultCode);

    /**
     * 支付失败
     *
     * @param type       支付渠道
     * @param msg        提示
     * @param resultCode 支付结果码
     */
    void onPayFailed(String type, String msg, String resultCode);

    /**
     * 支付取消
     *
     * @param type       支付渠道
     * @param msg        提示
     * @param resultCode 支付结果码
     */
    void onPayCancel(String type, String msg, String resultCode);

    /**
     * 支付结果未知，此时业务可能需要调用后端接口查询结果
     *
     * @param type 支付渠道
     */
    void onPayUnknown(String type);
}
