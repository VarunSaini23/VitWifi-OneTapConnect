package com.varunsaini.android.vitwifi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fenjuly.library.ArrowDownloadButton;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {


    ArrowDownloadButton button;
    public static boolean isConnected = false;
    TextView textViewStatus,textViewConnectDisconnect,primaryAccountTextview,yourPrimaryAccountHeading;
    String BASE_LOGIN_URL = "http://phc.prontonetworks.com/cgi-bin/authlogin?URI=http://www.msftconnecttest.com/redirect";
    String LOGOUT_URL = "http://phc.prontonetworks.com/cgi-bin/authlogout";
    String username,password;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        username = getActivity().getSharedPreferences("my_prefs",Context.MODE_PRIVATE).getString("primary_account","No Account");
        password = getActivity().getSharedPreferences("my_prefs",Context.MODE_PRIVATE).getString(username,"No Account");

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        yourPrimaryAccountHeading = v.findViewById(R.id.your_primary_account_heading);
        textViewConnectDisconnect = v.findViewById(R.id.textViewConnectDisconnect);
        primaryAccountTextview = v.findViewById(R.id.primary_account_textview);
        textViewStatus = v.findViewById(R.id.textViewStatus);
        button = (ArrowDownloadButton)v.findViewById(R.id.arrow_download_button);
        if(isConnected==true){
            button.setProgress((float) 100);
            textViewConnectDisconnect.setText("Tap to Disconnect");
            textViewStatus.setText("Connected \uD83D\uDE42");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected==false){
                    MyWifiConnector wfc = new MyWifiConnector();
                    wfc.execute(username,password);}
                else if (isConnected==true){
                    MyWifiDisConnector wfdc = new MyWifiDisConnector();
                    wfdc.execute();
                }
            }
        });
        primaryAccountTextview.setText(username);

        //setting typefaces
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat.ttf");
        textViewConnectDisconnect.setTypeface(tf);
        textViewStatus.setTypeface(tf);
        yourPrimaryAccountHeading.setTypeface(tf);
        primaryAccountTextview.setTypeface(tf);



        return v;
    }

    public class MyWifiConnector extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection.Response baseLoginPage = Jsoup.connect(BASE_LOGIN_URL).header("Accept-Encoding", "gzip, deflate").followRedirects(false).method(Connection.Method.GET)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .execute();

                Document doc = Jsoup.connect(BASE_LOGIN_URL).header("Accept-Encoding", "gzip, deflate").maxBodySize(1000000000).timeout(600000).followRedirects(false)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0").data("userId",strings[0])
                        .data("password",strings[1])
                        .data("serviceName","ProntoAuthentication")
                        .data("Submit22","Login")
                        .cookies(baseLoginPage.cookies())
                        .post();

                Log.d("postlogin", "doInBackground: "+doc.toString());

                if(doc.title().equals("Successful Pronto Authentication")){
                    return "Successful";
                }else if (doc.title().equals("301 Moved Permanently")){
                    return "Wifi Not Connected";
                }else if(doc.title().equals("")){
                    return "Already Connected";
                }else if (doc.title().equals("VOLSWIFI Authentication")){
                    return "Invalid Credentials";
                }

                Log.d("title", "doInBackground: "+doc.title());
                Log.d("body", "doInBackground: "+doc.body());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "nono";
        }

        @Override
        protected void onPostExecute(String state) {
            super.onPostExecute(state);


            if(state.equals("Successful")){
                isConnected = true;
                final int[] progress = {0};
                textViewConnectDisconnect.setText("Tap to Disconnect");
                button.startAnimating();
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress[0] = progress[0] + 1;
                                button.setProgress(progress[0]);
                            }
                        });
                    }
                }, 800, 20);
                textViewStatus.setText("Connected \uD83D\uDE42");
                Toasty.success(getActivity(),"Connected",Toast.LENGTH_SHORT, true).show();
                progress[0] = 0;

            }else if (state.equals("Wifi Not Connected")){
                button.reset();
                isConnected = false;
                textViewStatus.setText("Connect Vit Wifi First \uD83D\uDE41");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(" Please connect to Vit Wifi")
                        .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String networkSSID = "VIT2.4G";
                                WifiConfiguration conf = new WifiConfiguration();
                                conf.SSID = "\"" + networkSSID + "\"";
                                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                                WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                if(!wifiManager.isWifiEnabled()){
                                    wifiManager.setWifiEnabled(true);
                                }
                                wifiManager.addNetwork(conf);
                                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                                for( WifiConfiguration i : list ) {
                                    if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                                        wifiManager.disconnect();
                                        wifiManager.enableNetwork(i.networkId, true);
                                        wifiManager.reconnect();
//                                        Toasty.info(getActivity(), "VIT Wifi Connected", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
            }else if (state.equals("Already Connected")){
                isConnected = true;
                button.startAnimating();
                Timer timer = new Timer();
                final int[] progress = {0};
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress[0] = progress[0] + 1;
                                button.setProgress(progress[0]);
                                if (progress[0]==99){
                                    textViewStatus.setText("Already Connected \uD83D\uDE42");                                }
                            }
                        });
                    }
                }, 800, 20);
                Toasty.success(getActivity(),"Already Connected",Toast.LENGTH_SHORT, true).show();
                textViewConnectDisconnect.setText("Tap to Disconnect");
            }else if (state.equals("Invalid Credentials")){
                button.reset();
                isConnected = false;
                textViewStatus.setText("Invalid Credentials \uD83D\uDE41");
                Toasty.error(getActivity(),"Invalid Credentials",Toast.LENGTH_SHORT, true).show();
            }

        }
    }

    public class MyWifiDisConnector extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Connection.Response baseLoginPage = Jsoup.connect(LOGOUT_URL).header("Accept-Encoding", "gzip, deflate").maxBodySize(1000000000).timeout(600000).followRedirects(false).method(Connection.Method.GET)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .execute();
                Log.d("disconnect", "doInBackground: "+baseLoginPage.parse().html());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isConnected = false;
            textViewConnectDisconnect.setText("Tap to Connect");
            button.reset();
            textViewStatus.setText("Disconnected \uD83D\uDE10");
            Toasty.success(getActivity(),"Disconnected",Toast.LENGTH_SHORT, true).show();
            super.onPostExecute(aVoid);
        }

    }



}
