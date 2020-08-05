package com.davdian.service.dvdpay.wxpay;

import android.content.Context;

import com.davdian.service.dvdpay.PayContract;
import com.davdian.service.dvdpay.R;
import com.davdian.service.dvdpay.bean.WxPayDetailInfoData;
import com.davdian.service.dvdpay.bean.WxPayResultEvent;
import com.davdian.service.dvdpay.resultinterface.OnSdkPayFinishListener;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author dengyizheng
 * @date 2017/8/12
 * 微信支付的一层封装
 */

public class WxPay {
    private OnSdkPayFinishListener onSdkPayFinishListener;
    //微信支付
    public IWXAPI msgApi;
    private Context context;

    /**
     * 调起微信支付
     *
     * @param context                上下文对象
     * @param appKey                 app在微信注册的key
     * @param jsonRequestData        调起支付所需数据
     * @param onSDKPayFinishListener 结果回调
     */
    public void toRequestAndPay(Context context, String appKey, String jsonRequestData, OnSdkPayFinishListener onSDKPayFinishListener) {
        onSdkPayFinishListener = onSDKPayFinishListener;
        this.context = context;
        msgApi = WXAPIFactory.createWXAPI(context, null);
        if (!msgApi.isWXAppInstalled()) {
            if (onSdkPayFinishListener != null) {
                onSdkPayFinishListener.onPayFailed(PayContract.WX_PAY, context.getString(R.string.tip_wx_uninstall), String.valueOf(PayContract.WX_PAY_FAILED));
            }
            return;
        }
        msgApi.registerApp(appKey);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Gson gson = new Gson();
        WxPayDetailInfoData detailInfoData = gson.fromJson(jsonRequestData, WxPayDetailInfoData.class);
        PayReq req = detailInfoData.createPayReq();
        msgApi.sendReq(req);
    }

    /**
     * 接受sdk支付结果
     *
     * @param event 支付结果
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayCallBack(WxPayResultEvent event) {
        if (onSdkPayFinishListener == null) {
            return;
        }
        int resultCode = event.getResultCode();
        switch (resultCode) {
            case PayContract.WX_PAY_FAILED:
                onSdkPayFinishListener.onPayFailed(PayContract.WX_PAY, context.getString(R.string.tip_pay_failed), String.valueOf(PayContract.WX_PAY_FAILED));
                break;
            case PayContract.WX_PAY_SUCCESS:
                onSdkPayFinishListener.onPaySuccess(PayContract.WX_PAY, context.getString(R.string.tip_pay_success), String.valueOf(PayContract.WX_PAY_SUCCESS));
                break;
            case PayContract.WX_PAY_CANCEL:
                onSdkPayFinishListener.onPayCancel(PayContract.WX_PAY, context.getString(R.string.tip_pay_cancel), String.valueOf(PayContract.WX_PAY_FAILED));
                break;
            default:
                onSdkPayFinishListener.onPayCancel(PayContract.WX_PAY, context.getString(R.string.tip_pay_cancel), String.valueOf(PayContract.WX_PAY_FAILED));
                break;
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * 解绑微信支付结果通知
     */
    public void unRegister() {
        EventBus.getDefault().unregister(this);
    }
}
