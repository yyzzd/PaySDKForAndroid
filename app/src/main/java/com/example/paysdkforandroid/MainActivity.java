package com.example.paysdkforandroid;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.davdian.service.dvdpay.PayService;
import com.davdian.service.dvdpay.resultinterface.PayResultListenerAdapter;

/**
 * @author yizheng.deng
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_to_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayService.getInstance().toAliPay(MainActivity.this, "{\"key\":\"value\"}", new PayResultListenerAdapter() {
                    @Override
                    public void onPaySuccess(String type, String msg, String result) {

                    }

                    @Override
                    public void onPayFailed(String type, String msg, String result) {

                    }

                    @Override
                    public void onPayUnknown(String type) {

                    }

                    @Override
                    public void onPayCancel(String type, String msg, String result) {

                    }
                });
            }
        });

    }
}
