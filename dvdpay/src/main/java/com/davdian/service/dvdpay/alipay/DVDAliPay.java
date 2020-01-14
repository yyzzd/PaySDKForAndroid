package com.davdian.service.dvdpay.alipay;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.davdian.service.dvdpay.DVDPayContract;
import com.davdian.service.dvdpay.R;
import com.davdian.service.dvdpay.bean.PayResult;
import com.davdian.service.dvdpay.resultinterface.onSDKPayFinishListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;


/**
 * Created by dengyizheng on 2017/8/12.
 * 对阿里支付的一层封装
 */

public class DVDAliPay {

    /**
     * 支付线程
     */
    private AliPayTask aliPayTask;

    /**
     * 调起阿里支付
     *
     * @param activity               activity对象
     * @param jsonRequestData        调起支付所需数据
     * @param onSDKPayFinishListener 结果回调
     */
    public void toRequestAndPay(final Activity activity, String jsonRequestData, onSDKPayFinishListener onSDKPayFinishListener, String type) {
        Map<String, String> map = new Gson().fromJson(jsonRequestData, new TypeToken<Map<String, String>>() {
        }.getType());
        StringBuffer stringBuffer = new StringBuffer();
        try {
            TreeMap<String, String> treeMap = new TreeMap<>(map);
            for (Map.Entry<String, String> stringEntry : treeMap.entrySet()) {
                if (TextUtils.isEmpty(stringEntry.getValue())) {
                    continue;
                }
                stringBuffer.append(stringEntry.getKey());
                stringBuffer.append("=");
                if (type.equals(DVDPayContract.ALI_PAY)) {
                    stringBuffer.append(URLEncoder.encode(stringEntry.getValue(), "UTF-8"));
                } else {
                    stringBuffer.append(stringEntry.getValue());
                }
                stringBuffer.append("&");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        stringBuffer = stringBuffer.deleteCharAt(stringBuffer.toString().length() - 1);
        aliPayTask = new AliPayTask(activity, onSDKPayFinishListener, type);
        aliPayTask.execute(stringBuffer.toString());
    }

    /**
     * 支付宝支付结果
     */
    private static class AliPayTask extends AsyncTask<String, String, Map<String, String>> {
        private String type;
        /**
         * 结果回调
         */
        private onSDKPayFinishListener mSDKPayFinishListener;
        /**
         * 持有外部activity软引用
         */
        private WeakReference<Activity> weakReference;

        /**
         * 支付线程构造，获取外部activity
         *
         * @param activity              持有外部activity软引用
         * @param mSDKPayFinishListener 结果回调
         */
        AliPayTask(Activity activity, onSDKPayFinishListener mSDKPayFinishListener, String type) {
            this.type = type;
            weakReference = new WeakReference<>(activity);
            this.mSDKPayFinishListener = mSDKPayFinishListener;
        }

        /**
         * 关闭线程
         */
        private void finishPay() {
            if (!this.isCancelled()) {
                cancel(true);
            }
        }

        @Override
        protected Map<String, String> doInBackground(String... params) {
            Activity activity = weakReference.get();
            if (activity != null) {
                // 构造PayTask 对象
                PayTask aliPay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                return aliPay.payV2(params[0], true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Map<String, String> param) {
            PayResult payResult;
            payResult = new PayResult(param);

            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
            if (TextUtils.equals(resultStatus, DVDPayContract.ALI_PAY_SUCCESS)) {
                mSDKPayFinishListener.onPaySuccess(DVDPayContract.ALI_PAY, weakReference.get().getString(R.string.tip_pay_success), DVDPayContract.ALI_PAY_SUCCESS);
            } else if (TextUtils.equals(resultStatus, DVDPayContract.ALI_PAY_CANCEL)) {
                mSDKPayFinishListener.onPayCancel(DVDPayContract.ALI_PAY, weakReference.get().getString(R.string.tip_pay_cancel), DVDPayContract.ALI_PAY_CANCEL);
            } else if (TextUtils.equals(resultStatus, DVDPayContract.ALI_PAY_FAILED)) {
                mSDKPayFinishListener.onPayFailed(DVDPayContract.ALI_PAY, weakReference.get().getString(R.string.tip_pay_failed), DVDPayContract.ALI_PAY_FAILED);
            } else {
                mSDKPayFinishListener.onPayCancel(DVDPayContract.ALI_PAY, weakReference.get().getString(R.string.tip_pay_cancel), DVDPayContract.ALI_PAY_CANCEL);
            }

            finishPay();
        }
    }

    /**
     * 关闭线程
     */
    public void finishPay() {
        if (aliPayTask != null) {
            aliPayTask.cancel(true);
            aliPayTask = null;
        }
    }
}
