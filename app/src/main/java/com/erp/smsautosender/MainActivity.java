package com.erp.smsautosender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static Handler mHandler;
    private static boolean isRun = false;
    private static String LastMessageText;
    private static long startTime;
    private static boolean firstCreate = true;
    private static PendingIntent pi;
    private static SmsManager sms;
    private TextView statusTextView;
    private TextView lastMessagetextView;
    private Button primaryButton;
    private Button settingsButton;
    private SharedPreferences preferences;
    private final static int connectionTimeout = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setInit();
    }

    protected void sendNextMessage() {
        String inputLine = "";
        String listApiUrl = preferences.getString("API_LIST", getResources().getString(R.string.list_api_url_edittext_placeholder));
        try {
            inputLine = getPageSource(listApiUrl);
        } catch (Exception e) {
            e.printStackTrace();
            displayShortToast(getResources().getString(R.string.network_error));
        }
        if (inputLine != "") {
            displayShortToast(getResources().getString(R.string.processing_sms));
            proceed_sms_request(inputLine);
        } else {
            displayShortToast(getResources().getString(R.string.no_new_messages));
        }
    }

    protected static String getPageSource(String targetUrl) throws IOException {

        URLConnection connection = (new URL(targetUrl)).openConnection();
        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(connectionTimeout);
        connection.connect();

        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder html = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            html.append(line);
        }
        in.close();

        return html.toString();
    }

    protected void proceed_sms_request(String inputLine) {
        String[] parts = inputLine.split(";");
        String id = "0";
        String phone = "";
        String messageText = "";
        try {
            id = parts[0];
            phone = parts[1];
            messageText = parts[2];
        } catch (Exception e) {
            displayShortToast(getResources().getString(R.string.network_error));
            return;
        }
        for (int i = 3; i < parts.length; i++) {
            messageText += ";" + parts[i];
        }

        boolean sms_sent_status = sendSms(phone, messageText);

        if (!sms_sent_status) {
            displayShortToast(getResources().getString(R.string.not_delivered));
            try {
                String sendErrorUrl = preferences.getString("API_ERROR",getResources().getString(R.string.error_api_url_edittext_placeholder));
                getPageSource(sendErrorUrl + id);
            } catch (Exception e) {
                displayShortToast(getResources().getString(R.string.network_error));
            }
        }

        String status = sms_sent_status ? getResources().getString(R.string.sent) : getResources().getString(R.string.not_sent);

        LastMessageText = getResources().getString(R.string.last_message) + ":\nID:" + id +
                "\n" + getResources().getString(R.string.phone) + ":" + phone +
                "\n" + getResources().getString(R.string.message_text_placeholder) + ":" + messageText +
                "\n" + getResources().getString(R.string.status) + ":" + status;
        lastMessagetextView.setText(LastMessageText);
    }

    protected boolean sendSms(String phone, String text) {
        try {
            sms.sendTextMessage(phone, null, text, pi, null);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    protected void setInit() {
        initComponents();
        if (firstCreate) {
            firstCreate = false;
            LastMessageText = getResources().getString(R.string.last_message_dots);
            lastMessagetextView.setText(LastMessageText);
            setPolicies();
            startInterval();
            displayShortToast(getResources().getString(R.string.welcome));
        }
        onCreateDraw();
        checkSMSSendPermission();
        buttonEventHandler();
    }

    protected void buttonEventHandler() {
        primaryButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            public void onClick(View v) {
                String text = (String) primaryButton.getText();

                if (!isRun) {
                    isRun = true;
                    statusTextView.setText(getResources().getString(R.string.wait));
                    prepareToProceedMessages();
                    startTime = System.currentTimeMillis();
                    primaryButton.setText(getResources().getString(R.string.stop));
                } else {
                    statusTextView.setText(getResources().getString(R.string.not_started));
                    isRun = false;
                    primaryButton.setText(getResources().getString(R.string.start));
                }
                displayShortToast(text);
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    protected void onCreateDraw() {
        if (isRun) {
            long Minutes = ((System.currentTimeMillis() - startTime)) / 1000 / 60;
            String workingTimeText = getResources().getString(R.string.work_in) + Minutes +
                    getResources().getString(R.string.minutes);
            statusTextView.setText(workingTimeText);
            primaryButton.setText(getResources().getString(R.string.stop));
        } else {
            statusTextView.setText(getResources().getString(R.string.not_started));
        }
        lastMessagetextView.setText(LastMessageText);
    }

    protected void initComponents() {
        statusTextView = (TextView) findViewById(R.id.textViewStatus);
        preferences = getSharedPreferences("APIPreferences", MODE_PRIVATE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);
        sms = SmsManager.getDefault();
        lastMessagetextView = (TextView) findViewById(R.id.textViewLastMessage);
        primaryButton = (Button) findViewById(R.id.buttonPrimary);
        settingsButton = (Button) findViewById(R.id.settingsButton);
    }

    protected void setPolicies() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @SuppressLint("HandlerLeak")
    protected void startInterval() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (isRun) {
                    prepareToProceedMessages();
                }

                mHandler.sendEmptyMessageDelayed(0, 60000);
            }
        };
        mHandler.sendEmptyMessage(0);
    }

    protected void prepareToProceedMessages() {
        long Minutes = 0;
        String workTimeText = getResources().getString(R.string.work_in) + " " + Minutes + " "
                + getResources().getString(R.string.minutes);
        statusTextView.setText(workTimeText);
        sendNextMessage();
    }

    protected void checkSMSSendPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            statusTextView.setText(getResources().getString(R.string.need_sms_permission));
            primaryButton.setEnabled(false);
        }
    }

    protected void displayShortToast(String text) {
        displayToast(text, Toast.LENGTH_SHORT);
    }

    protected void displayToast(String text, int duration) {
        Toast toast = Toast.makeText(getApplicationContext(),
                text, duration);
        toast.show();
    }
}