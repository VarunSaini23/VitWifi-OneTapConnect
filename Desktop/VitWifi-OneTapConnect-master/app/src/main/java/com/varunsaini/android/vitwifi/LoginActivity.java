package com.varunsaini.android.vitwifi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    EditText editTextPassword,editTextUsername;
    Button btnAddAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnAddAccount = findViewById(R.id.btnAddAccount);

        MobileAds.initialize(this, getString(R.string.admob_app_id));
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editTextPassword.getText().toString().equals("") && !editTextUsername.getText().toString().equals("")){
                getSharedPreferences("my_prefs",Context.MODE_PRIVATE).edit().putString(editTextUsername.getText().toString(),editTextPassword.getText().toString()).apply();
                getSharedPreferences("my_prefs",Context.MODE_PRIVATE).edit().putString("primary_account",editTextUsername.getText().toString()).apply();
                getSharedPreferences("my_prefs",Context.MODE_PRIVATE).edit().putString("is_login_account_set","yes").apply();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
                }else{
                    Toasty.error(LoginActivity.this,"Fields can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
