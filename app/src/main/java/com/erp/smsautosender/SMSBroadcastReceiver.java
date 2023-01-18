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

                SmsMessage sms = messages[0];
                String body;
                try {
                    if (messages.length == 1 || sms.isReplace()) {
                        body = sms.getDisplayMessageBody();
                    } else {
                        StringBuilder bodyText = new StringBuilder();
                        for (int i = 0; i < messages.length; i++) {
                            bodyText.append(messages[i].getMessageBody());
                        }
                        SmsApi smsApi = new SmsApi();
                        String messageText = bodyText.toString();
                        String messagePhone = messages[0].getOriginatingAddress();
                        Toast.makeText(context, "Recieved message from " + messagePhone + ": " +messageText , Toast.LENGTH_LONG).show();
                        smsApi.APIReceiveSMS(messagePhone,messageText);
                    }
                } catch (Exception e) {

                }
            }
        }
    }
}
