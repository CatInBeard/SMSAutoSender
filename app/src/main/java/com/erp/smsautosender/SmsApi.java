package com.erp.smsautosender;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class SmsApi {
    private Context applicationContext;

    public void APIReceiveSMS(String phone, String text){
        applicationContext = MainActivity.getContextOfApplication();
        SharedPreferences preferences = applicationContext.getSharedPreferences("APIPreferences", Context.MODE_PRIVATE);
        String receiveApiUrl = preferences.getString("API_RECEIVE", applicationContext.getResources().getString(R.string.receive_api_url_edittext_placeholder)) + "?phone=" + phone + "&text=" + text;
        try {
            MainActivity.getPageSource(receiveApiUrl);
        } catch (Exception e) {
            e.printStackTrace();
            displayShortToast(applicationContext.getResources().getString(R.string.network_error));
        }
    }

    protected void displayShortToast(String text) {
        displayToast(text, Toast.LENGTH_SHORT);
    }

    protected void displayToast(String text, int duration) {
        Toast toast = Toast.makeText(applicationContext,
                text, duration);
        toast.show();
    }
}
