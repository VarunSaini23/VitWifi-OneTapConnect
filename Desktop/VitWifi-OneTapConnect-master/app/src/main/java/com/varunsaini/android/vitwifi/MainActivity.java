package com.varunsaini.android.vitwifi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Shared Preferences
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_wifi) {
                    HomeFragment homeFrag = new HomeFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout,homeFrag);
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }if(tabId==R.id.tab_accounts){
                    AccountsFragment accountsFrag = new AccountsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frameLayout,accountsFrag);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });


    }
}
