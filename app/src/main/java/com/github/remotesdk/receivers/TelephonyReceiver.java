package com.github.remotesdk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class TelephonyReceiver extends BroadcastReceiver {
    public static final String TELEPHONY_COMMAND = "telephony_remote";
    public static final String TELEPHONY_REMOTE = "com.github.remotesdk.TELEPHONY_REMOTE";

    private TelephonyManager adapter;

    public TelephonyReceiver(TelephonyManager adapter) {
        this.adapter = adapter;
    }

    private final String GET_CALL_STATE = "getCallState";


    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(TELEPHONY_REMOTE)) {
                String command = intent.getStringExtra(TELEPHONY_COMMAND);
                if (command.equals(GET_CALL_STATE)) {
                    int result = adapter.getCallState();
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
