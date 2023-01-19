package com.erp.smsautosender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public class IncomingCallsReceiver extends BroadcastReceiver {
    private Context applicationContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                if(!incomingNumber.isEmpty()) {
                    applicationContext = MainActivity.getContextOfApplication();
                    Toast.makeText(context, applicationContext.getResources().getString(R.string.new_call_from) + " " + incomingNumber, Toast.LENGTH_SHORT).show();
                    SmsApi smsApi = new SmsApi();
                    smsApi.APIReceiveIncomingCall(incomingNumber);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
