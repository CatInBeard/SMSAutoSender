package com.erp.smsautosender;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION ="android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_SENDER="123456789";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == ACTION) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                }
                if (messages.length > -1) {
                    SmsApi smsApi = new SmsApi();
                    String messageText = messages[0].getMessageBody();
                    String messagePhone = messages[0].getOriginatingAddress();
                    Toast.makeText(context, "Recieved message from " + messagePhone + ": " +messageText , Toast.LENGTH_LONG).show();
                    smsApi.APIReceiveSMS(messagePhone,messageText);
                }
            }
        }
    }
}
