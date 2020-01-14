package com.davdian.service.dvdpay;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.davdian.service.dvdpay.alipay.DVDAliPay;
import com.davdian.service.dvdpay.bean.YWTPayEvent;
import com.davdian.service.dvdpay.resultinterface.onPayFinishListener;
import com.davdian.service.dvdpay.resultinterface.onSDKPayFinishListener;
import com.davdian.service.dvdpay.wxpay.DVDWXPay;
import com.davdian.service.dvdpay.ywt.YWTPayActivity;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dengyizheng on 2017/8/12.
 * 支付服务
 */

public class DVDPayService implements onSDKPayFinishListener {
    /**
     * 标记是否收到了支付结果回调
     */
    private boolean receiveCallBack = false;
    /**
     * 支付结果回调
     */
    private onPayFinishListener mPayFinishListener;
    /**
     * 用于解绑微信支付回调结果的DVDWXPay对象
     */
    private DVDWXPay mWXPay;
    private DVDAliPay mAliPay;
    private IWXAPI msgApi;
    /**
     * 用于监听收银台的生命周期
     */
    private MyCallBack myCallBack;

    /**
     * 返回一个service实例
     *
     * @return service实例
     */
    public static DVDPayService getInstance() {
        return new DVDPayService();
    }

    /**
     * 微信支付
     *
     * @param appKey              app在微信注册的key
     * @param jsonRequestData     jsonRequestData
     * @param onPayFinishListener 支付回调
     */
    public void toWXPay(Activity activity, String appKey, String jsonRequestData, onPayFinishListener onPayFinishListener) {
        mPayFinishListener = onPayFinishListener;
        if (TextUtils.isEmpty(jsonRequestData)) {
            if (mPayFinishListener != null) {
                mPayFinishListener.onPayFailed(DVDPayContract.WX_PAY, activity.getString(R.string.tip_no_jsonRequestData), String.valueOf(DVDPayContract.WX_PAY_FAILED));
            }
            return;
        }
        if (mWXPay == null) {
            mWXPay = new DVDWXPay();
        }
        initReceiveState(activity, DVDPayContract.WX_PAY);
        msgApi = mWXPay.msgApi;
        mWXPay.toRequestAndPay(activity, appKey, jsonRequestData, this);
    }

    /**
     * 阿里支付
     *
     * @param jsonRequestData     jsonRequestData
     * @param onPayFinishListener 支付回调
     */
    public void toAliPay(Activity activity, String jsonRequestData, onPayFinishListener onPayFinishListener) {
        mPayFinishListener = onPayFinishListener;
        if (TextUtils.isEmpty(jsonRequestData)) {
            if (mPayFinishListener != null) {
                mPayFinishListener.onPayFailed(DVDPayContract.ALI_PAY, activity.getString(R.string.tip_no_jsonRequestData), DVDPayContract.ALI_PAY_FAILED);
            }
            return;
        }
        if (mAliPay == null) {
            mAliPay = new DVDAliPay();
        }
        initReceiveState(activity, DVDPayContract.ALI_PAY);
        mAliPay.toRequestAndPay(activity, jsonRequestData, this, DVDPayContract.ALI_PAY);
    }

    /**
     * 阿里支付国际版
     *
     * @param jsonRequestData     jsonRequestData
     * @param onPayFinishListener 支付回调
     */
    public void toAliPayCross(Activity activity, String jsonRequestData, onPayFinishListener onPayFinishListener) {
        mPayFinishListener = onPayFinishListener;
        if (TextUtils.isEmpty(jsonRequestData)) {
            if (mPayFinishListener != null) {
                mPayFinishListener.onPayFailed(DVDPayContract.ALI_CROSS_PAY, activity.getString(R.string.tip_no_jsonRequestData), DVDPayContract.ALI_PAY_FAILED);
            }
            return;
        }
        if (mAliPay == null) {
            mAliPay = new DVDAliPay();
        }
        initReceiveState(activity, DVDPayContract.ALI_CROSS_PAY);
        mAliPay.toRequestAndPay(activity, jsonRequestData, this, DVDPayContract.ALI_CROSS_PAY);
    }

    /**
     * 一网通支付
     *
     * @param payUrl          支付地址
     * @param jsonRequestData 支付数据
     */
    public void toYWTPay(Activity activity, String payUrl, String jsonRequestData, onPayFinishListener finishListener) {
        mPayFinishListener = finishListener;
        if (TextUtils.isEmpty(jsonRequestData)) {
            if (mPayFinishListener != null) {
                mPayFinishListener.onPayFailed(DVDPayContract.YWT_PAY, activity.getString(R.string.tip_no_jsonRequestData), DVDPayContract.YWT_PAY_FAILED);
            }
            return;
        }
        initReceiveState(activity, DVDPayContract.YWT_PAY);
        if (DVDPayUtil.isCMBAppInstalled(activity)) {
            DVDPayUtil.callCMBApp(jsonRequestData, activity);
        } else {
            if (TextUtils.isEmpty(payUrl)) {
                if (mPayFinishListener != null) {
                    mPayFinishListener.onPayFailed(DVDPayContract.YWT_PAY, activity.getString(R.string.tip_no_payUrl), DVDPayContract.YWT_PAY_FAILED);
                }
                return;
            }
            Intent intent = new Intent(activity, YWTPayActivity.class);
            intent.putExtra(YWTPayActivity.PAY_URL, payUrl);
            intent.putExtra(YWTPayActivity.JSON_REQUEST_DATA, jsonRequestData);
            activity.startActivity(intent);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    /**
     * 返回一网通app是否已经安装
     */
    public boolean isCMBAppInstalled(Context context) {
        return DVDPayUtil.isCMBAppInstalled(context);
    }

    /**
     * 初始化支付结果接收状态
     *
     * @param type 支付渠道
     */
    private void initReceiveState(Activity activity, String type) {
        receiveCallBack = false;
        if (myCallBack == null) {
            myCallBack = new MyCallBack(activity);
        }
        myCallBack.setType(type);
    }

    /**
     * 一网通支付回调
     *
     * @param aEvent 支付结果
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void YWTPayResult(YWTPayEvent aEvent) {
        EventBus.getDefault().unregister(this);
        receiveCallBack = true;
        if (mPayFinishListener != null) {
            mPayFinishListener.onPayUnknown(DVDPayContract.YWT_PAY);
        }
    }

    @Override
    public void onPaySuccess(String type, String msg, String resultCode) {
        receiveCallBack = true;
        finishPay();
        if (mPayFinishListener != null) {
            mPayFinishListener.onPaySuccess(type, msg, resultCode);
        }
    }

    @Override
    public void onPayFailed(String type, String msg, String resultCode) {
        receiveCallBack = true;
        finishPay();
        if (mPayFinishListener != null) {
            mPayFinishListener.onPayFailed(type, msg, resultCode);
        }
    }

    @Override
    public void onPayCancel(String type, String msg, String resultCode) {
        receiveCallBack = true;
        finishPay();
        if (mPayFinishListener != null) {
            mPayFinishListener.onPayCancel(type, msg, resultCode);
        }
    }

    /**
     * 如果业务使用的是微信支付，必须调用此方法解绑微信支付的eventBus，否则会造成内存泄漏
     */
    private void finishPay() {
        if (mWXPay != null) {
            mWXPay.unRegister();
            mWXPay = null;
        }
        if (msgApi != null) {
            msgApi.unregisterApp();
            msgApi.detach();
            msgApi = null;
        }
        if (mAliPay != null) {
            mAliPay.finishPay();
            mAliPay = null;
        }
    }

    /**
     * 用于支付没有收到回调时，回调业务支付结果未知
     */
    private class MyCallBack implements Application.ActivityLifecycleCallbacks {

        private Activity activity;
        private String type;

        MyCallBack(Activity activity) {
            this.activity = activity;
            if (!this.activity.isFinishing()) {
                this.activity.getApplication().registerActivityLifecycleCallbacks(this);
            }
        }

        /**
         * 设置支付渠道
         *
         * @param type 支付渠道
         */
        void setType(String type) {
            this.type = type;
        }


        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (this.activity == activity
                    && !receiveCallBack
                    && !TextUtils.equals(type, DVDPayContract.ALI_PAY)
                    && mPayFinishListener != null) {
                mPayFinishListener.onPayUnknown(type);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (this.activity == activity) {
                this.activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                EventBus.getDefault().unregister(this);
                finishPay();
            }
        }
    }
}
