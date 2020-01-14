package com.davdian.service.dvdpay.resultinterface;

/**
 * Created by dengyizheng on 2018/5/24.
 */

public abstract class PayResultListenerAdapter implements onPayFinishListener {
    @Override
    public void onPaySuccess(String type, String msg, String resultCode) {

    }

    @Override
    public void onPayFailed(String type, String msg, String resultCode) {

    }

    @Override
    public void onPayCancel(String type, String msg, String resultCode) {

    }

    @Override
    public void onPayUnknown(String type) {

    }
}
