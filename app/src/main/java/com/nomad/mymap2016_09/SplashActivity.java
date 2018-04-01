package com.nomad.mymap2016_09;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    private static final int GO_HOME = 1000;
    private static final long SPLASH_DELAY_MILLTS = 3000;
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLTS);
    }
}
