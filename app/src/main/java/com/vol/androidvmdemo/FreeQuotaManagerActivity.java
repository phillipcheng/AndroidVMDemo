package com.vol.androidvmdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import snae.tmcandroid.app.TMPublicClient;
import snae.tmcandroid.app.UserBonus;
import snae.tmcandroid.app.UserBonusResult;
import snae.tmcandroid.app.UserPromotion;
import snae.tmcandroid.app.UserQuota;


public class FreeQuotaManagerActivity extends ActionBarActivity {

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    LayoutInflater LayoutInflater;
    TextView leftQuotaText;

    VMDemoApp myApp = null;
    TMPublicClient publicClient = null;
    String userid;
    int tenantid;
    List<UserPromotion> userPromotions;
    List<UserBonus> userBonuses;
    ViewGroup promotionList;
    ViewGroup bonusList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidUtil.removeStrict();

        setTitle("红包管理");

        super.onCreate(savedInstanceState);

        LayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        myApp = (VMDemoApp)this.getApplication();
        publicClient = new TMPublicClient(myApp.getVolASUrl());
        userid = myApp.getUserid();
        tenantid = myApp.getTenantid();

        setContentView(R.layout.free_quota_manager);

        leftQuotaText = (TextView) findViewById(R.id.leftQuota);

        promotionList = (ViewGroup) findViewById(R.id.promotionList);
        bonusList = (ViewGroup) findViewById(R.id.bonusList);

        updateUI();
    }

    void updateUI() {
        userPromotions = publicClient.listPromotions(tenantid);
        promotionList.removeAllViews();

        for (final UserPromotion p : userPromotions) {
            View v = LayoutInflater.inflate(R.layout.draw_bonus, null);
            ((TextView) v.findViewById(R.id.promotionName)).setText(p.getName() + " " + p.getDescription());
            Date endDate = new Date(p.getEndTime());

            ((TextView) v.findViewById(R.id.endTime)).setText(dateFormat.format(endDate));

            Button drawButton = (Button) v.findViewById(R.id.draw);
            drawButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    if (!myApp.getCustomKey().isEmpty()) {
                        map.put(myApp.getCustomKey(), myApp.getCustomValue());
                    }
                    UserBonusResult userBonusResult = publicClient.grabBonus(tenantid, (int) p.getId(), userid, map);
                    if (userBonusResult.getBonus() == null) {
                        Toast.makeText(FreeQuotaManagerActivity.this, userBonusResult.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FreeQuotaManagerActivity.this, "成功抢到" + userBonusResult.getBonus().getSize() + "字节的流量红包!", Toast.LENGTH_LONG).show();
                        updateUI();
                    }
                }
            });
            promotionList.addView(v);
        }

        userBonuses = publicClient.listUserBonus(tenantid, userid);

        bonusList.removeAllViews();
        Collections.sort(userBonuses, new Comparator<UserBonus>() {
            @Override
            public int compare(UserBonus lhs, UserBonus rhs) {
                if (lhs.getActivationTime() == 0 && rhs.getActivationTime() == 0) {
                    return (int) (lhs.getExpirationTime() - rhs.getExpirationTime());
                }
                if (lhs.getActivationTime() == 0) {
                    return -1;
                }
                if (rhs.getActivationTime() == 0) {
                    return 1;
                }
                return -1 * (int) (lhs.getActivationTime() - rhs.getActivationTime());
            }
        });

        for (final UserBonus p : userBonuses) {

            // ? what if bonus is expired
            View v = LayoutInflater.inflate(R.layout.active_bonus, null);
            ((TextView) v.findViewById(R.id.quotaSize)).setText(p.getSize() + " bytes");

            boolean activated = (p.getActivationTime() > 0);

            if (activated) {
                ((TextView) v.findViewById(R.id.timeLabel)).setText("激活时间:");
            } else {
                ((TextView) v.findViewById(R.id.timeLabel)).setText("失效时间:");
            }

            Date endDate = new Date(activated ? p.getActivationTime() : p.getExpirationTime());
            ((TextView) v.findViewById(R.id.endTime)).setText(dateFormat.format(endDate));

            Button activeButton = (Button) v.findViewById(R.id.active);

            if (activated) {
                activeButton.setText("已激活");
                activeButton.setEnabled(false);
            } else {
                activeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean result = publicClient.activateBonus(tenantid, p.getId());
                        if (result) {
                            Toast.makeText(FreeQuotaManagerActivity.this, "成功激活" + p.getSize() + "字节的流量红包!", Toast.LENGTH_LONG).show();
                            updateUI();
                        }
                    }
                });
            }
            bonusList.addView(v);
        }

        // fetch left quota from app server
        UserQuota userQuota = publicClient.getQuota(tenantid, userid);
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
