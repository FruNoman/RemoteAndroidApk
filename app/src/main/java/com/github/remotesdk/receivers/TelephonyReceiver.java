package com.github.remotesdk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.remotesdk.utils.CallHistory;

import java.util.ArrayList;
import java.util.List;

public class TelephonyReceiver extends BroadcastReceiver {
    public static final String TELEPHONY_COMMAND = "telephony_remote";
    public static final String TELEPHONY_REMOTE = "com.github.remotesdk.TELEPHONY_REMOTE";

    private TelephonyManager adapter;


    private final String GET_CALL_STATE = "getCallState";
    private final String GET_CALL_HISTORY = "getCallHistory";

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
