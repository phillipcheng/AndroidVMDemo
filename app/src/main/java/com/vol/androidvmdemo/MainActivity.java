package com.vol.androidvmdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import snae.tmcandroid.app.TMPublicClient;
import snae.tmcandroid.app.UserQuota;


public class MainActivity extends ActionBarActivity {

    SharedPreferences settings;

    TextView leftQuotaText;
    EditText useridText;
    EditText tenantidText;
    EditText snaeProxyHostText;
    EditText snaeProxyPortText;
    EditText snaeASUrlText;

    EditText customKeyText;
    EditText customValueText;

    VMDemoApp myApp = null;


    TMPublicClient publicClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidUtil.removeStrict();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApp = (VMDemoApp)this.getApplication();

        useridText = (EditText) findViewById(R.id.userid);
        useridText.setText(myApp.getUserid());

        tenantidText = (EditText) findViewById(R.id.tenantid);
        tenantidText.setText(String.valueOf(myApp.getTenantid()));

        customKeyText = (EditText) findViewById(R.id.customKey);
        customKeyText.setText(myApp.getCustomKey());

        customValueText = (EditText) findViewById(R.id.customValue);
        customValueText.setText(myApp.getCustomValue());

        snaeProxyHostText = (EditText) findViewById(R.id.SNAEHostTxt);
        snaeProxyHostText.setText(myApp.getTrafficProxyHost());

        snaeProxyPortText = (EditText) findViewById(R.id.SNAEPortTxt);
        snaeProxyPortText.setText(String.valueOf(myApp.getTrafficProxyPort()));

        snaeASUrlText = (EditText) findViewById(R.id.SNAEAsUrlTxt);
        snaeASUrlText.setText(myApp.getVolASUrl());

        {
            Button b = (Button) findViewById(R.id.save);

            leftQuotaText = (TextView) findViewById(R.id.leftQuota);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //save them
                    myApp.setUserid(useridText.getText().toString());
                    myApp.setTenantid(Integer.parseInt(tenantidText.getText().toString()));
                    myApp.setCustomKey(customKeyText.getText().toString().trim());
                    myApp.setCustomValue(customValueText.getText().toString().trim());
                    myApp.setTrafficProxyHost(snaeProxyHostText.getText().toString().trim());
                    myApp.setTrafficProxyPort(Integer.parseInt(snaeProxyPortText.getText().toString()));
                    myApp.setVolASUrl(snaeASUrlText.getText().toString().trim());
                    hideSoftKeyboard();
                    myApp.savePreference();
                    updateUI();
                }
            });
        }

        updateUI();
    }

    void hideSoftKeyboard() {
        if (getCurrentFocus() != null && getCurrentFocus() instanceof EditText) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(useridText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(tenantidText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(customKeyText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(customValueText.getWindowToken(), 0);
        }
    }

    public void openFreeQuotaManager(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, FreeQuotaManagerActivity.class);
        startActivity(intent);
    }


    public void openBrowser(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, com.danielme.android.webviewdemo.WebViewDemoActivity.class);
        startActivity(intent);
    }

    void updateUI() {

        publicClient = new TMPublicClient(myApp.getVolASUrl());
        // fetch left quota from app server
        UserQuota userQuota = publicClient.getQuota(myApp.getTenantid(), myApp.getUserid());
        long balance = 0;
        if (userQuota != null) {
            balance = userQuota.getBalance();
        }
        if (balance < 0) {
            balance = 0;
        }
        leftQuotaText.setText(balance + " bytes");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
