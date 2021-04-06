package com.github.remotesdk.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.remotesdk.utils.CallHistory;
import com.github.remotesdk.utils.Contact;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TelephonyReceiver extends BroadcastReceiver {
    public static final String TELEPHONY_COMMAND = "telephony_remote";
    public static final String TELEPHONY_REMOTE = "com.github.remotesdk.TELEPHONY_REMOTE";

    private TelephonyManager adapter;
    private String incomingCallNumber = "";


    private final String GET_CALL_STATE = "getCallState";
    private final String GET_CALL_HISTORY = "getCallHistory";
    private final String GET_CONTACTS = "getContacts";

    private final String CALL = "callNumber";
    private final String END_CALL = "endCallProgrammatically";
    private final String ANSWER_RINGING_CALL = "answerRingingCall";
    private final String GET_INCOMING_CALL_NUMBER = "getIncomingCallNumber";
    private final String SET_DATA_ENABLED = "setDataEnabled";
    private final String IS_NETWORK_ROAMING = "isNetworkRoaming";
    private final String IS_DATA_ENABLED = "isDataEnabled";


    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;


    public TelephonyReceiver(TelephonyManager telephonyManager) {
        this.adapter = telephonyManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(TELEPHONY_REMOTE)) {
                String command = intent.getStringExtra(TELEPHONY_COMMAND);
                if (command.equals(GET_CALL_STATE)) {
                    int result = adapter.getCallState();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_CALL_HISTORY)) {
                    String[] projection = new String[]{
                            CallLog.Calls.CACHED_NAME,
                            CallLog.Calls.NUMBER,
                            CallLog.Calls.TYPE,
                            CallLog.Calls.DATE,
                    };
                    List<CallHistory> callHistoryArrayList = new ArrayList<>();
                    Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null, null, null);
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(0);
                        String number = cursor.getString(1);
                        String type = cursor.getString(2);
                        String time = cursor.getString(3);
                        callHistoryArrayList.add(new CallHistory(name, number, Integer.parseInt(type), Long.parseLong(time)));
                    }
                    cursor.close();
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writeValueAsString(callHistoryArrayList);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_CONTACTS)) {
                    List<Contact> list = new ArrayList<>();
                    ContentResolver contentResolver = context.getContentResolver();
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                    if (cursor.getCount() > 0) {
                        while (cursor.moveToNext()) {
                            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                                Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                                while (cursorInfo.moveToNext()) {
                                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                    String number = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Contact info = new Contact(id, name, number);
                                    list.add(info);
                                }

                                cursorInfo.close();
                            }
                        }
                        cursor.close();
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writeValueAsString(list);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.contains(CALL)) {
                    String number = command.split(",")[1];
                    String uriString = "";
                    for (char c : number.toCharArray()) {
                        if (c == '#')
                            uriString += Uri.encode("#");
                        else
                            uriString += c;
                    }
                    Uri call = Uri.parse("tel:" + uriString);
                    Intent callIntent = new Intent(Intent.ACTION_CALL, call);
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(callIntent);
                    setResultCode(SUCCESS_CODE);
                } else if (command.equals(END_CALL)) {
                    try {
                        Class c = Class.forName(adapter.getClass().getName());
                        Method m = c.getDeclaredMethod("getITelephony");
                        m.setAccessible(true);
                        Object telephonyService = m.invoke(adapter);

                        c = Class.forName(telephonyService.getClass().getName());
                        m = c.getDeclaredMethod("endCall");
                        m.setAccessible(true);
                        m.invoke(telephonyService);
                    } catch (Exception e) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                            telecomManager.endCall();
                        }
                    }
                    setResultCode(SUCCESS_CODE);
                } else if (command.equals(ANSWER_RINGING_CALL)) {
                    try {
                        Class c = Class.forName(adapter.getClass().getName());
                        Method m = c.getDeclaredMethod("getITelephony");
                        m.setAccessible(true);
                        Object telephonyService = m.invoke(adapter);

                        c = Class.forName(telephonyService.getClass().getName());
                        m = c.getDeclaredMethod("answerRingingCall");
                        m.setAccessible(true);
                        m.invoke(telephonyService);
                    } catch (Exception e) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                            telecomManager.acceptRingingCall();
                        }
                    }
                    setResultCode(SUCCESS_CODE);
                } else if (command.equals(GET_INCOMING_CALL_NUMBER)) {
                    setResult(SUCCESS_CODE, String.valueOf(incomingCallNumber), new Bundle());
                } else if (command.contains(SET_DATA_ENABLED)) {
                    boolean state = Boolean.parseBoolean(command.split(",")[1]);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        adapter.setDataEnabled(state);
                    }
                    setResultCode(SUCCESS_CODE);
                }else if (command.equals(IS_NETWORK_ROAMING)) {
                    boolean result = adapter.isNetworkRoaming();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }else if (command.equals(IS_DATA_ENABLED)) {
                    boolean result = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        result = adapter.isDataEnabled();
                    }
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

            } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    incomingCallNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                }
            }
        } catch (Exception e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                String json = mapper.writeValueAsString(e);
                setResult(ERROR_CODE, json, new Bundle());
            } catch (JsonProcessingException jsonProcessingException) {
                jsonProcessingException.printStackTrace();
            }
        }

    }
}
