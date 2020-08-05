package com.davdian.service.dvdpay.resultinterface;

/**
 *
 * @author dengyizheng
 * @date 2018/5/24
 */

public abstract class PayResultListenerAdapter implements OnPayFinishListener {
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
