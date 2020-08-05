package com.davdian.service.dvdpay.ywt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.davdian.service.dvdpay.R;
import com.davdian.service.dvdpay.bean.YwtPayEvent;
import com.davdian.service.dvdpay.bean.YwtReqBean;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

/**
 * @author dengyizheng
 * @date 2018/5/24
 * 一网通支付页面
 */
public class YwtPayActivity extends Activity {
    /**
     * 支付地址key
     */
    public static final String PAY_URL = "pay_url";
    /**
     * 支付数据key
     */
    public static final String JSON_REQUEST_DATA = "jsonRequestData";
    /**
     * 浏览器
     */
    private WebView mWebView;
    /**
     * 判断是否成功加载
     */
    private boolean pageSuccess = false;
    /**
     * 点击返回商户按钮会加载这个url
     */
    private String returnUrl;
    /**
     * 调起支付所需数据
     */
    private String jsonRequestData;
    /**
     * 支付页面地址
     */
    private String payUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ywt_pay_layout);
        initData();
        initView();
    }

    /**
     * 数据初始化
     */
    private void initData() {
        jsonRequestData = getIntent().getStringExtra(JSON_REQUEST_DATA);
        payUrl = getIntent().getStringExtra(PAY_URL);
        try {
            Gson gson = new Gson();
            YwtReqBean bean = gson.fromJson(jsonRequestData, YwtReqBean.class);
            if (bean != null && bean.getReqData() != null) {
                returnUrl = bean.getReqData().getReturnUrl();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 控件初始化
     */
    private void initView() {
        View status = findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int height = getStatusBarHeight(this);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) status.getLayoutParams();
            params.height = height;
            status.setLayoutParams(params);
            status.setVisibility(View.VISIBLE);
        } else {
            status.setVisibility(View.GONE);
        }
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    finish();
                }
            }
        });
        String json = "jsonRequestData=" + jsonRequestData;
        mWebView = findViewById(R.id.wv_ywt_pay);
        setSettings();
        mWebView.postUrl(payUrl, json.getBytes());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!TextUtils.isEmpty(returnUrl) && TextUtils.equals(returnUrl, url)) {
                    //用户点击了返回商户按钮
                    EventBus.getDefault().post(new YwtPayEvent());
                    finish();
                } else if (pageSuccess && TextUtils.equals(payUrl, url)) {
                    mWebView.destroy();
                    finish();
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pageSuccess = true;
            }
        });
    }

    /**
     * 获取状态栏高度
     */
    static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 设置浏览器
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setSettings() {
        // 支持获取焦点
        mWebView.requestFocusFromTouch();
        // 取消滚动条
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebView.setVerticalScrollBarEnabled(false);
        WebSettings settings = mWebView.getSettings();
        settings.setUseWideViewPort(true);
        // 页面缩放至屏幕
        settings.setLoadWithOverviewMode(true);
        // 支持JavaScript
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setBlockNetworkImage(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //https / http混合页面
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            try {
                settings.setMediaPlaybackRequiresUserGesture(false);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
