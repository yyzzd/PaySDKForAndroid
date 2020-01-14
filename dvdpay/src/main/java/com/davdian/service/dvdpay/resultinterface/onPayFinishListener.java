package com.davdian.service.dvdpay.resultinterface;

/**
 * Created by dengyizheng on 2017/8/12.
 */

public interface onPayFinishListener {
    void onPaySuccess(String type, String msg, String resultCode);

    void onPayFailed(String type, String msg, String resultCode);

    void onPayCancel(String type, String msg, String resultCode);

    void onPayUnknown(String type);
}
