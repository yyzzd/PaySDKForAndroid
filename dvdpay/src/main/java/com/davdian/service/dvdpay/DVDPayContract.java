package com.davdian.service.dvdpay;

/**
 * Created by dengyizheng on 2017/8/15.
 */

public class DVDPayContract {
    /**
     * 微信支付状态码
     */
    public static final int WX_PAY_SUCCESS = 100;
    public static final int WX_PAY_FAILED = 102;
    public static final int WX_PAY_CANCEL = 101;
    /**
     * 阿里支付状态码
     */
    public static String ALI_PAY_SUCCESS = "9000";
    public static String ALI_PAY_FAILED = "4000";
    public static String ALI_PAY_CANCEL = "6001";
    /**
     * 一网通支付状态码
     */
    public static String YWT_PAY_FAILED = "0";
    /**
     * 支付类型
     */
    public static String ALI_PAY = "AliPay";
    public static String ALI_CROSS_PAY = "cross";
    public static String WX_PAY = "WXPay";
    public static String YWT_PAY = "YWTPay";
}
