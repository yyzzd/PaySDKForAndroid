package com.davdian.service.dvdpay.resultinterface;

/**
 * Created by dengyizheng on 2017/8/12.
 */

public interface onSDKPayFinishListener {
    void onPaySuccess(String type, String msg, String result);

    void onPayFailed(String type, String msg, String result);

    void onPayCancel(String type, String msg, String result);
}
