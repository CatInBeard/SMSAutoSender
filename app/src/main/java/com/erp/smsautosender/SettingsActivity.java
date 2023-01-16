package com.erp.smsautosender;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    protected SharedPreferences preferences;
    protected EditText listApiEditText;
    protected EditText errorApiEditText;
    protected EditText receiveApiEditText;
    protected EditText refreshTimeEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initComponents();
        drawComponents();
        setListeners();
    }

    protected void initComponents() {
        preferences = getSharedPreferences("APIPreferences", MODE_PRIVATE);
        listApiEditText = findViewById(R.id.editTextListApi);
        receiveApiEditText = findViewById(R.id.editTextReceiveApi);
        errorApiEditText = findViewById(R.id.editTextErrorApi);
        refreshTimeEditText = findViewById(R.id.editTextNumberDecimalRefreshTime);

    }
    protected void setListeners(){
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefEditor = preferences.edit();

                String api_list_val = listApiEditText.getText().toString();
                String api_error_val = errorApiEditText.getText().toString();
                String api_receive_val = receiveApiEditText.getText().toString();
                int refresh_time_val = Integer.parseInt(refreshTimeEditText.getText().toString());

                prefEditor.putString("API_LIST", api_list_val);
                prefEditor.apply();
                prefEditor.putString("API_ERROR", api_error_val);
                prefEditor.apply();
                prefEditor.putString("API_RECEIVE", api_receive_val);
                prefEditor.apply();
                prefEditor.putInt("REFRESH_TIME", refresh_time_val);
                prefEditor.apply();


                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });
    }
    protected void drawComponents(){
        String listApi = preferences.getString("API_LIST", getResources().getString(R.string.list_api_url_edittext_placeholder));
        String errorApi = preferences.getString("API_ERROR",getResources().getString(R.string.error_api_url_edittext_placeholder));
        String receiveApi = preferences.getString("API_RECEIVE",getResources().getString(R.string.receive_api_url_edittext_placeholder));
        int refresh_time = preferences.getInt("REFRESH_TIME",60);
        listApiEditText.setText(listApi);
        errorApiEditText.setText(errorApi);
        receiveApiEditText.setText(receiveApi);
        refreshTimeEditText.setText(Integer.toString(refresh_time));

    }
}
