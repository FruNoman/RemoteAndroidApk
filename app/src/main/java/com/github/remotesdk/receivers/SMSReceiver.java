package com.github.remotesdk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
    public static final String COMMAND = "command";
    public static final String SMS_REMOTE = "com.github.remotesdk.SMS_REMOTE";

    private final String SEND_SMS = "sendSMS";
    private final String IS_SMS_RECEIVED = "isSMSReceived";
    private final String GET_LAST_SMS_NUMBER = "getLastSMSNumber";
    private final String GET_LAST_SMS_TEXT = "getLastSMSText";
    private final String IS_MAP_PROFILE_MESSAGE_RECEIVED = "isMAPProfileMessageReceived";
    private final String GET_LAST_MAP_PROFILE_SMS_NUMBER = "getLastMAPProfileSMSNumber";
    private final String GET_LAST_MAP_PROFILE_SMS_TEXT = "getLastMAPProfileSMSText";
    private final String GET_LAST_MAP_PROFILE_SMS_URI = "getLastMAPProfileSMSURI";

    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    private Object monitor = new Object();
    private String smsNumber;
    private String smsText;
    private boolean smsReceived = false;

    private boolean mapProfileMessageReceived = false;
    private String mapProfileSenderUri;
    private String mapProfileSenderName;
    private String mapProfileSenderText;
    private SmsManager smsManager = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(SMS_REMOTE)) {
                String command = intent.getStringExtra(COMMAND);
                if (command.equals(SEND_SMS)) {
                    String phoneNumber = intent.getStringExtra("param0");
                    String text = intent.getStringExtra("param1");
                    smsManager.sendTextMessage(phoneNumber, null, text, null, null);
                    setResult(SUCCESS_CODE, "", new Bundle());
                } else if (command.equals(GET_LAST_SMS_NUMBER)) {
                    setResult(SUCCESS_CODE, smsNumber, new Bundle());
                } else if (command.equals(GET_LAST_SMS_TEXT)) {
                    setResult(SUCCESS_CODE, smsText, new Bundle());
                } else if (command.equals(IS_SMS_RECEIVED)) {
                    setResult(SUCCESS_CODE, String.valueOf(smsReceived), new Bundle());
                    synchronized (monitor) {
                        smsReceived = false;
                    }
                } else if (command.equals(IS_MAP_PROFILE_MESSAGE_RECEIVED)) {
                    setResult(SUCCESS_CODE, String.valueOf(mapProfileMessageReceived), new Bundle());
                    synchronized (monitor) {
                        mapProfileMessageReceived = false;
                    }
                } else if (command.equals(GET_LAST_MAP_PROFILE_SMS_NUMBER)) {
                    setResult(SUCCESS_CODE, String.valueOf(mapProfileSenderName), new Bundle());
                } else if (command.equals(GET_LAST_MAP_PROFILE_SMS_TEXT)) {
                    setResult(SUCCESS_CODE, String.valueOf(mapProfileSenderText), new Bundle());
                } else if (command.equals(GET_LAST_MAP_PROFILE_SMS_URI)) {
                    setResult(SUCCESS_CODE, String.valueOf(mapProfileSenderUri), new Bundle());
                }
            } else if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs = null;
                if (bundle != null) {
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], "3gpp2");
                            synchronized (monitor) {
                                smsNumber = msgs[i].getOriginatingAddress();
                                smsText = msgs[i].getMessageBody();
                                smsReceived = true;
                            }
                        }
                    } catch (Exception e) {

                    }
                }
            } else if (action.equals("android.bluetooth.mapmce.profile.action.MESSAGE_RECEIVED")) {
                synchronized (monitor) {
                    mapProfileMessageReceived = true;
                    mapProfileSenderUri = intent.getStringExtra("android.bluetooth.mapmce.profile.extra.SENDER_CONTACT_URI");
                    mapProfileSenderName = intent.getStringExtra("android.bluetooth.mapmce.profile.extra.SENDER_CONTACT_NAME");
                    mapProfileSenderText = intent.getStringExtra(Intent.EXTRA_TEXT);
                }
            }
        } catch (Exception e) {
            setResult(ERROR_CODE, e.getLocalizedMessage(), new Bundle());
        }
    }
}
