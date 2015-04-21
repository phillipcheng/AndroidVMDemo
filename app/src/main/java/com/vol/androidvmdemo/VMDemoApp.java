package com.vol.androidvmdemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cheng Yi on 4/21/2015.
 */
public class VMDemoApp extends Application {

    private static final String APP_PREF_KEY="setting";

    private static final String trafficProxyHost_Key="trafficProxyHost";
    private static final String trafficProxyPort_Key="trafficProxyPort";
    private static final String volASUrl_Key="VolASUrl";
    private static final String userId_Key="userId";
    private static final String tenantId_Key="tenantId";
    private static final String customKey_Key="customKey";
    private static final String customValue_Key="customValue";

    private String trafficProxyHost;
    private int trafficProxyPort;
    private String volASUrl;
    private String userid;
    private int tenantid;
    private String customKey;
    private String customValue;

    public void loadPreference(){
        SharedPreferences prefs = getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE);
        trafficProxyHost = prefs.getString(trafficProxyHost_Key,"120.25.231.28");
        trafficProxyPort = prefs.getInt(trafficProxyPort_Key, 8080);
        volASUrl = prefs.getString(volASUrl_Key, "http://52.1.96.115:80");
        userid = prefs.getString(userId_Key, "123");
        tenantid = prefs.getInt(tenantId_Key, 4);
        customKey = prefs.getString(customKey_Key, "");
        customValue = prefs.getString(customValue_Key, "");
    }

    public void savePreference(){
        SharedPreferences prefs = getSharedPreferences(APP_PREF_KEY, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(trafficProxyHost_Key, trafficProxyHost)
                .putInt(trafficProxyPort_Key, trafficProxyPort)
                .putString(volASUrl_Key, volASUrl)
                .putString(userId_Key, userid)
                .putInt(tenantId_Key, tenantid)
                .putString(customKey_Key, customKey)
                .putString(customValue_Key, customValue)
                .apply();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        this.loadPreference();
    }
    public String getTrafficProxyHost() {
        return trafficProxyHost;
    }

    public void setTrafficProxyHost(String trafficProxyHost) {
        this.trafficProxyHost = trafficProxyHost;
    }

    public int getTrafficProxyPort() {
        return trafficProxyPort;
    }

    public void setTrafficProxyPort(int trafficProxyPort) {
        this.trafficProxyPort = trafficProxyPort;
    }

    public String getVolASUrl() {
        return volASUrl;
    }

    public void setVolASUrl(String volASUrl) {
        this.volASUrl = volASUrl;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getTenantid() {
        return tenantid;
    }

    public void setTenantid(int tenantid) {
        this.tenantid = tenantid;
    }

    public String getCustomKey() {
        return customKey;
    }

    public void setCustomKey(String customKey) {
        this.customKey = customKey;
    }

    public String getCustomValue() {
        return customValue;
    }

    public void setCustomValue(String customValue) {
        this.customValue = customValue;
    }

}
