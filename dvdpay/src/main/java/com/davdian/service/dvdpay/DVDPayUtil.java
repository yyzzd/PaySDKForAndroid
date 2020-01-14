package com.davdian.service.dvdpay;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by dengyizheng on 2018/5/24.
 */

class DVDPayUtil {
    /**
     * 判断是否安装了招商银行app
     *
     * @return true已安装
     * false 未安装
     */
    static boolean isCMBAppInstalled(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("cmb.pb", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
        }
        return packageInfo != null;
    }

    /**
     * 调起招行app
     *
     * @param jsonRequest 参数
     * @param context     上下文对象
     */
    static void callCMBApp(String jsonRequest, Context context) {
        String url = "cmbmobilebank://CMBLS/FunctionJump?action=gofuncid&funcid=200007&serverid=CMBEUserPay&requesttype=post&cmb_app_trans_parms_start=here&charset=utf-8&jsonRequestData=";
        try {
            Intent intent = new Intent();
            Uri data = Uri.parse(url + jsonRequest);
            intent.setData(data);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
