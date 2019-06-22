package com.varunsaini.android.vitwifi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import es.dmoral.toasty.Toasty;

public class AccountsFragment extends Fragment {

    FloatingActionButton fab;
    SharedPreferences vitWifiSharedPreferences;
    TextView topInfo;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    Map<String,String> accountsToDisplay;
    String[] arrayOfAccounts;
    String[] duplicateArrayOfAccounts;
    String changedPassword = "";
    final CharSequence[] optionsDialog = {"Select as Primary Account","Change Password","Delete Account"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_accounts, container, false);
        vitWifiSharedPreferences = getActivity().getSharedPreferences("my_prefs",Context.MODE_PRIVATE);

        topInfo = v.findViewById(R.id.top_info);
        recyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getDataAndSetAdapter();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {
                        // TODO Handle item click
                        Log.d("aaa", "onItemClick: "+position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Choose an Option");
                        builder.setItems(optionsDialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // the user clicked on colors[which]
                                switch (which){

                                    case 0:
                                        //set as primary account
                                        if((HomeFragment.isConnected)==true){
                                            Toasty.info(getActivity(), "Firstly disconnect from existing Primary Account", Toast.LENGTH_SHORT).show();
                                        }else {
                                            vitWifiSharedPreferences.edit().putString("primary_account", arrayOfAccounts[position]).apply();
                                        }
                                        break;

                                    case 1:
                                        //changePassword
                                        changePasswordDialog(position);
                                        break;

                                    case 2:
                                        //delete account
                                        vitWifiSharedPreferences.edit().remove(arrayOfAccounts[position]).apply();
                                        Log.d("remove", "removed: "+arrayOfAccounts[position]);
                                        adapter.notifyItemRemoved(position);
                                        break;

                                }
                            }
                        });
                        builder.show();
                    }
                })
        );


        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(getActivity());

                final View add_account_dialog = factory.inflate(R.layout.add_account_dialog, null);

                final EditText regNoEdit = (EditText) add_account_dialog.findViewById(R.id.regNoEdit);
                final EditText passwordEdit = (EditText) add_account_dialog.findViewById(R.id.passwordEdit);


//                regNoEdit.setText(TextView.BufferType.EDITABLE);
//                passwordEdit.setText("DefaultValue", TextView.BufferType.EDITABLE);

                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setIcon(R.drawable.baseline_person_add_black_18dp).setTitle("Add Account").setView(add_account_dialog).setPositiveButton("Add Account",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {

                                SharedPreferences.Editor editor = vitWifiSharedPreferences.edit();
                                editor.putString(regNoEdit.getText().toString(),passwordEdit.getText().toString()).apply();
                                Log.i("AlertDialog","TextEntry 1 Entered "+regNoEdit.getText().toString());
                                Log.i("AlertDialog","TextEntry 2 Entered "+passwordEdit.getText().toString());
                                adapter.notifyDataSetChanged();
                                getDataAndSetAdapter();


                            }
                        }).setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                /*
                                 * User clicked cancel so do some stuff
                                 */
                            }
                        });
                alert.show();
            }
        });

        //setting typefaces
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Montserrat.ttf");
        topInfo.setTypeface(tf);
        return v;

    }

    public void getDataAndSetAdapter(){
        accountsToDisplay = new HashMap<>();
        Map<String,?> allAccounts = vitWifiSharedPreferences.getAll();
        for(Map.Entry<String,?> entry : allAccounts.entrySet()){
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
            if (entry.getKey().equals("primary_account") || entry.getKey().equals("is_login_account_set")){continue;}
            accountsToDisplay.put(entry.getKey(),entry.getValue().toString());
        }

        Set<String> keysSet = accountsToDisplay.keySet();
        arrayOfAccounts = keysSet.toArray(new String[keysSet.size()]);
        duplicateArrayOfAccounts = arrayOfAccounts.clone();
        adapter = new CustomAdapter(arrayOfAccounts);
        recyclerView.setAdapter(adapter);

    }

    public void changePasswordDialog(final int position){
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View password_change_dialog = factory.inflate(R.layout.password_change_dialog, null);

        final EditText changePasswordEdit = (EditText) password_change_dialog.findViewById(R.id.changePasswordEdit);

        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setIcon(R.drawable.baseline_person_add_black_18dp).setTitle("Update Password").setView(password_change_dialog).setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        changedPassword = changePasswordEdit.getText().toString();
                        if (changedPassword.equals("")){ }
                        else{
                            vitWifiSharedPreferences.edit().putString(arrayOfAccounts[position],changedPassword).commit();
                            Toasty.success(getActivity(), "Password Updated", Toast.LENGTH_SHORT).show();
                        }

                        Log.d("abv", "onClick: changed"+changedPassword);

                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                    changedPassword = "";
                    }
                });
        alert.show();

    }




}
