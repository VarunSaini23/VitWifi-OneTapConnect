package com.varunsaini.android.vitwifi;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int secondsDelayed = 2;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (getSharedPreferences("my_prefs",MODE_PRIVATE).getString("is_login_account_set","").equals("yes")){
                startActivity(new Intent(SplashActivity.this, MainActivity.class));}
                else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));}
                finish();
            }

            }, secondsDelayed * 1000);
    }
}
