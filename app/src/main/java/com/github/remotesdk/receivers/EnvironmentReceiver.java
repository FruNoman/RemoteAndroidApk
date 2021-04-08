package com.github.remotesdk.receivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnvironmentReceiver extends BroadcastReceiver {
    public static final String ENVIRONMENT_COMMAND = "environment_remote";
    public static final String ENVIRONMENT_REMOTE = "com.github.remotesdk.ENVIRONMENT_REMOTE";
    private StorageManager storageManager;

    public EnvironmentReceiver(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

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

    private final String CAN_EXECUTE = "canExecute";
    private final String CAN_READ = "canRead";
    private final String CAN_WRITE = "canWrite";
    private final String IS_ABSOLUTE = "isAbsolute";
    private final String IS_HIDDEN = "isHidden";
    private final String DELETE = "deleteFile";
    private final String CREATE_NEW_FILE = "createNewFile";
    private final String MAKE_DIR = "makeDir";
    private final String MAKE_DIRS = "createDirs";
    private final String RENAME_TO = "renameTo";
    private final String SET_READABLE = "setReadable";
    private final String SET_WRITABLE = "setWritable";
    private final String SET_EXECUTABLE = "setExecutable";
    private final String GET_TOTAL_SPACE = "getTotalSpace";
    private final String LAST_MODIFIED = "lastModified";
    private final String GET_STORAGE_VOLUMES = "getStorageVolumes";


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

                else if (command.contains(CAN_EXECUTE)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).canExecute();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(CAN_READ)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).canRead();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(CAN_WRITE)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).canWrite();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(IS_ABSOLUTE)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).isAbsolute();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(IS_HIDDEN)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).isHidden();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(DELETE)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).delete();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(CREATE_NEW_FILE)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).createNewFile();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(MAKE_DIR)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).mkdir();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(MAKE_DIRS)) {
                    String path = command.split(",")[1];
                    boolean result = new File(path).mkdirs();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(RENAME_TO)) {
                    String path = command.split(",")[1];
                    String rename = command.split(",")[2];
                    boolean result = new File(path).renameTo(new File(rename));
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(SET_READABLE)) {
                    String path = command.split(",")[1];
                    boolean state = Boolean.parseBoolean(command.split(",")[2]);
                    boolean result = new File(path).setReadable(state);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(SET_WRITABLE)) {
                    String path = command.split(",")[1];
                    boolean state = Boolean.parseBoolean(command.split(",")[2]);
                    boolean result = new File(path).setWritable(state);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(SET_EXECUTABLE)) {
                    String path = command.split(",")[1];
                    boolean state = Boolean.parseBoolean(command.split(",")[2]);
                    boolean result = new File(path).setExecutable(state);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(GET_TOTAL_SPACE)) {
                    String path = command.split(",")[1];
                    long result = new File(path).getTotalSpace();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(LAST_MODIFIED)) {
                    String path = command.split(",")[1];
                    long result = new File(path).lastModified();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }

                else if (command.contains(GET_STORAGE_VOLUMES)) {
                    List<StorageVolume> storageVolumes = new ArrayList<>();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        storageVolumes = storageManager.getStorageVolumes();
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    String result = mapper.writeValueAsString(storageVolumes);
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
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
