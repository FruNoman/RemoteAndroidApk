package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.Arrays;

public class EnvironmentReceiver extends BroadcastReceiver {
    public static final String ENVIRONMENT_COMMAND = "environment_remote";
    public static final String ENVIRONMENT_REMOTE = "com.github.remotesdk.ENVIRONMENT_REMOTE";

    private final String GET_EXTERNAL_STORAGE_DIRECTORY = "getExternalStorageDirectory";
    private final String GET_ROOT_DIRECTORY = "getRootDirectory";
    private final String GET_DATA_DIRECTORY = "getDataDirectory";
    private final String GET_DOWNLOAD_CACHE_DIRECTORY = "getDownloadCacheDirectory";
    private final String IS_FILE_EXIST = "isFileExist";
    private final String LIST_FILES = "listFiles";
    private final String IS_DIRECTORY = "isDirectory";
    private final String IS_FILE = "isFile";
    private final String GET_NAME = "getName";
    private final String GET_PARENT = "getParent";





    private final int ERROR_CODE = 123;
    private final int SUCCESS_CODE = 373;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(ENVIRONMENT_REMOTE)) {
                String command = intent.getStringExtra(ENVIRONMENT_COMMAND);
                if (command.equals(GET_EXTERNAL_STORAGE_DIRECTORY)) {
                    File result = Environment.getExternalStorageDirectory();
                    setResult(SUCCESS_CODE, result.getAbsolutePath(), new Bundle());
                } else if (command.equals(GET_ROOT_DIRECTORY)) {
                    File result = Environment.getRootDirectory();
                    setResult(SUCCESS_CODE, result.getAbsolutePath(), new Bundle());
                } else if (command.equals(GET_DATA_DIRECTORY)) {
                    File result = Environment.getDataDirectory();
                    setResult(SUCCESS_CODE, result.getAbsolutePath(), new Bundle());
                } else if (command.equals(GET_DOWNLOAD_CACHE_DIRECTORY)) {
                    File result = Environment.getDownloadCacheDirectory();
                    setResult(SUCCESS_CODE, result.getAbsolutePath(), new Bundle());
                }

                else if (command.contains(IS_FILE_EXIST)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).exists();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(LIST_FILES)) {
                    String path = command.split(",")[1];
                    File [] result = new File(path).listFiles();
                    setResult(SUCCESS_CODE, Arrays.toString(result), new Bundle());
                }

                else if (command.contains(IS_DIRECTORY)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).isDirectory();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(IS_FILE)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).isFile();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(GET_NAME)) {
                    String path = command.split(",")[1];
                    String result = new File(path).getName();
                    setResult(SUCCESS_CODE, result, new Bundle());
                }

                else if (command.contains(GET_PARENT)) {
                    String path = command.split(",")[1];
                    String result = new File(path).getParentFile().getAbsolutePath();
                    setResult(SUCCESS_CODE, result, new Bundle());
                }
            }
        } catch (Exception e) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
