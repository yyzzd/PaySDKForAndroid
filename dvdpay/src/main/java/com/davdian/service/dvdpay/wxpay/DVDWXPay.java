package com.davdian.service.dvdpay.wxpay;

import android.content.Context;

import com.davdian.service.dvdpay.DVDPayContract;
import com.davdian.service.dvdpay.R;
import com.davdian.service.dvdpay.bean.WXPayDetailInfoData;
import com.davdian.service.dvdpay.bean.WXPayResultEvent;
import com.davdian.service.dvdpay.resultinterface.onSDKPayFinishListener;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by dengyizheng on 2017/8/12.
 * 微信支付的一层封装
 */

public class DVDWXPay {
    private onSDKPayFinishListener mSDKPayFinishListener;
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
    public void toRequestAndPay(Context context, String appKey, String jsonRequestData, onSDKPayFinishListener onSDKPayFinishListener) {
        mSDKPayFinishListener = onSDKPayFinishListener;
        this.context = context;
        msgApi = WXAPIFactory.createWXAPI(context, null);
        if (!msgApi.isWXAppInstalled()) {
            if (mSDKPayFinishListener != null) {
                mSDKPayFinishListener.onPayFailed(DVDPayContract.WX_PAY, context.getString(R.string.tip_wx_uninstall), String.valueOf(DVDPayContract.WX_PAY_FAILED));
            }
            return;
        }
        msgApi.registerApp(appKey);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        Gson gson = new Gson();
        WXPayDetailInfoData detailInfoData = gson.fromJson(jsonRequestData, WXPayDetailInfoData.class);
        PayReq req = detailInfoData.createPayReq();
        msgApi.sendReq(req);
    }

    /**
     * 接受sdk支付结果
     *
     * @param event 支付结果
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPayCallBack(WXPayResultEvent event) {
        if (mSDKPayFinishListener == null) {
            return;
        }
        int resultCode = event.getResultCode();
        switch (resultCode) {
            case DVDPayContract.WX_PAY_FAILED:
                mSDKPayFinishListener.onPayFailed(DVDPayContract.WX_PAY, context.getString(R.string.tip_pay_failed), String.valueOf(DVDPayContract.WX_PAY_FAILED));
                break;
            case DVDPayContract.WX_PAY_SUCCESS:
                mSDKPayFinishListener.onPaySuccess(DVDPayContract.WX_PAY, context.getString(R.string.tip_pay_success), String.valueOf(DVDPayContract.WX_PAY_SUCCESS));
                break;
            case DVDPayContract.WX_PAY_CANCEL:
                mSDKPayFinishListener.onPayCancel(DVDPayContract.WX_PAY, context.getString(R.string.tip_pay_cancel), String.valueOf(DVDPayContract.WX_PAY_FAILED));
                break;
            default:
                mSDKPayFinishListener.onPayCancel(DVDPayContract.WX_PAY, context.getString(R.string.tip_pay_cancel), String.valueOf(DVDPayContract.WX_PAY_FAILED));
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
