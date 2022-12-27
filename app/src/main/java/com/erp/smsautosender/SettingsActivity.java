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
        errorApiEditText = findViewById(R.id.editTextErrorApi);

    }
    protected void setListeners(){
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefEditor = preferences.edit();

                String api_list_val = listApiEditText.getText().toString();
                String api_error_val = errorApiEditText.getText().toString();

                prefEditor.putString("API_LIST", api_list_val);
                prefEditor.apply();
                prefEditor.putString("API_ERROR", api_error_val);
                prefEditor.apply();

                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
            }
        });
    }
    protected void drawComponents(){
        String listApi = preferences.getString("API_LIST", getResources().getString(R.string.list_api_url_edittext_placeholder));
        String errorApi = preferences.getString("API_ERROR",getResources().getString(R.string.error_api_url_edittext_placeholder));

        listApiEditText.setText(listApi);
        errorApiEditText.setText(errorApi);


    }
}
