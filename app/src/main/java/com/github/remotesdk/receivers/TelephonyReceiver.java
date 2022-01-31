package com.github.remotesdk.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.remotesdk.utils.CallHistory;
import com.github.remotesdk.utils.Contact;
import com.github.remotesdk.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import io.qameta.allure.kotlin.util.IOUtils;

public class TelephonyReceiver extends BroadcastReceiver {
    public static final String TELEPHONY_COMMAND = "telephony_command";
    public static final String TELEPHONY_REMOTE = "com.vf_test_automation_framework.TELEPHONY_REMOTE";

    private TelephonyManager adapter;
    private String incomingCallNumber = "";
    private String ussdResponce = "";


    private final String GET_CALL_STATE = "getCallState";
    private final String GET_CALL_HISTORY = "getCallHistory";
    private final String GET_CONTACTS = "getContacts";
    private final String CALL = "callNumber";
    private final String END_CALL = "endCallProgrammatically";
    private final String ANSWER_RINGING_CALL = "answerRingingCall";
    private final String GET_INCOMING_CALL_NUMBER = "getIncomingCallNumber";
    private final String SET_DATA_ENABLED = "setDataEnabled";
    private final String IS_DATA_ENABLED = "isDataEnabled";
    private final String GET_DATA_STATE = "getDataState";
    private final String GET_DATA_NETWORK_TYPE = "getDataNetworkType";
    private final String GET_PHONE_TYPE = "getPhoneType";
    private final String GET_SIM_STATE = "getSimState";
    private final String GET_NETWORK_OPERATOR_NAME = "getNetworkOperatorName";
    private final String SEND_USSD_REQUEST = "sendUssdRequest";
    private final String GET_USSD_RESPONSE = "getUssdResponse";
    private final String GET_MOBILE_PHONE = "getMobilePhone";
    private final String GET_CONTACT_IMAGE = "getContactImage";
    private final String CREATE_CONTACT = "createContactProgrammatically";
    private final String GET_CONTACTS_SIZE = "getContactsSize";
    private final String GET_CALL_HISTORY_SIZE = "getCallHistorySize";
    private final String SEND_SMS = "sendSMS";
    private final String IS_SMS_RECEIVED = "isSMSReceived";
    private final String CONTACT_GENERATOR = "contactGeneratorProgram";
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
                } else if (command.equals(GET_DATA_STATE)) {
                    int result = adapter.getDataState();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_DATA_NETWORK_TYPE)) {
                    int result = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        result = adapter.getDataNetworkType();
                    }
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_PHONE_TYPE)) {
                    int result = adapter.getPhoneType();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_MOBILE_PHONE)) {
                    String result = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        TelecomManager telecomManager = (TelecomManager) context.getApplicationContext().getSystemService(Context.TELECOM_SERVICE);
                        List<PhoneAccountHandle> phoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();
                        if (phoneAccountHandles.size() == 1) {
                            result = telecomManager.getLine1Number(phoneAccountHandles.get(0));
                        }
                    }
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.equals(GET_SIM_STATE)) {
                    int result = adapter.getSimState();
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.equals(GET_NETWORK_OPERATOR_NAME)) {
                    String result = adapter.getNetworkOperatorName();
                    setResult(SUCCESS_CODE, result, new Bundle());
                } else if (command.contains(SEND_USSD_REQUEST)) {
                    String ussd = command.split(",")[1];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        TelephonyManager.UssdResponseCallback ussdResponseCallback = new TelephonyManager.UssdResponseCallback() {
                            @Override
                            public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                                super.onReceiveUssdResponse(telephonyManager, request, response);
                                ussdResponce = (String) response;
                            }

                            @Override
                            public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                                super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                                setResultCode(ERROR_CODE);
                            }
                        };
                        Handler handler = new Handler(Looper.getMainLooper());
                        adapter.sendUssdRequest(ussd, ussdResponseCallback, handler);
                    }
                    setResultCode(SUCCESS_CODE);
                } else if (command.equals(GET_USSD_RESPONSE)) {
                    setResult(SUCCESS_CODE, ussdResponce, new Bundle());
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
                } else if (command.contains(CONTACT_GENERATOR)) {
                    int count = Integer.parseInt(command.split(",")[1]);
                    Utils.generateContacts(context, count);
                    setResult(SUCCESS_CODE, "", new Bundle());
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
                                    int type = cursorInfo.getInt(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                    Contact info = new Contact(id, name, number, type);
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
                } else if (command.equals(GET_CONTACTS_SIZE)) {
                    ContentResolver contentResolver = context.getContentResolver();
                    int count = 0;
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                    count = cursor.getCount();
                    cursor.close();
                    setResult(SUCCESS_CODE, String.valueOf(count), new Bundle());
                } else if (command.equals(GET_CALL_HISTORY_SIZE)) {
                    int count = 0;
                    Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
                    count = cursor.getCount();
                    cursor.close();
                    setResult(SUCCESS_CODE, String.valueOf(count), new Bundle());
                } else if (command.contains(GET_CONTACT_IMAGE)) {
                    long contactId = Long.parseLong(command.split(",")[1]);
                    String result = "";
                    try {
                        InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId));
                        if (inputStream != null) {
                            byte[] bytes = IOUtils.toByteArray(inputStream);
                            result = Base64.getEncoder().encodeToString(bytes);
                        }

                        if (inputStream != null) inputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setResult(SUCCESS_CODE, result, new Bundle());

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
//                    try {
//                        Class c = Class.forName(adapter.getClass().getName());
//                        Method m = c.getDeclaredMethod("getITelephony");
//                        m.setAccessible(true);
//                        Object telephonyService = m.invoke(adapter);
//
//                        c = Class.forName(telephonyService.getClass().getName());
//                        m = c.getDeclaredMethod("endCall");
//                        m.setAccessible(true);
//                        m.invoke(telephonyService);
//                    } catch (Exception e) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                        telecomManager.endCall();
                    }
//                    }
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
                } else if (command.equals(IS_DATA_ENABLED)) {
                    boolean result = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        result = adapter.isDataEnabled();
                    }
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                } else if (command.contains(SEND_SMS)) {
                    String phoneNumber = command.split(",")[1];
                    String text = command.split(",")[2];
                    SmsManager smsManager = SmsManager.getDefault();
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
                }else if (command.equals(GET_LAST_MAP_PROFILE_SMS_NUMBER)) {
                    setResult(SUCCESS_CODE, String.valueOf(mapProfileSenderName), new Bundle());
                }
                else if (command.equals(GET_LAST_MAP_PROFILE_SMS_TEXT)) {
                    setResult(SUCCESS_CODE, String.valueOf(mapProfileSenderText), new Bundle());
                }
                else if (command.equals(GET_LAST_MAP_PROFILE_SMS_URI)) {
                    setResult(SUCCESS_CODE, String.valueOf(mapProfileSenderUri), new Bundle());
                }
                else if (command.contains(CREATE_CONTACT)) {
                    String name = command.split(",")[1];
                    String mobileNumber = command.split(",")[2];
                    int contactType = Integer.parseInt(command.split(",")[3]);
                    String photoBase64 = null;
                    try {
                        photoBase64 = command.split(",")[4];
                    } catch (Exception e) {

                    }
                    boolean result = false;

                    long id = getRawContactIdByName(name, context.getContentResolver());
                    if (id == -1) {
                        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
                        operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                .build());

                        if (!name.isEmpty()) {
                            operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                    .build());
                        }
                        if (!mobileNumber.isEmpty()) {
                            operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, contactType)
                                    .build());
                        }

                        try {
                            byte[] decodeBytes = Base64.getDecoder().decode(photoBase64);
                            operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, decodeBytes)
                                    .build());
                        } catch (Exception e) {

                        }

                        try {
                            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
                            result = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

                        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)

                                .withValue(ContactsContract.Data.RAW_CONTACT_ID, id)

                                // Sets the data row's MIME type to Phone
                                .withValue(ContactsContract.Data.MIMETYPE,
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)

                                // Sets the phone number and type
                                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, contactType)
                                .build());

                        try {
                            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
                            result = true;
                        } catch (Exception e) {

                        }
                    }
                    setResult(SUCCESS_CODE, String.valueOf(result), new Bundle());
                }


            } else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    incomingCallNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
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
            }
            else if (action.equals("android.bluetooth.mapmce.profile.action.MESSAGE_RECEIVED")){
                synchronized (monitor) {
                    mapProfileMessageReceived = true;
                    mapProfileSenderUri = intent.getStringExtra("android.bluetooth.mapmce.profile.extra.SENDER_CONTACT_URI");
                    mapProfileSenderName = intent.getStringExtra("android.bluetooth.mapmce.profile.extra.SENDER_CONTACT_NAME");
                    mapProfileSenderText =  intent.getStringExtra(Intent.EXTRA_TEXT);
                }
            }


        } catch (Exception e) {
            setResult(ERROR_CODE, "error", new Bundle());
        }

    }


    private long getRawContactIdByName(String givenName, ContentResolver contentResolver) {

        // Query raw_contacts table by display name field ( given_name family_name ) to get raw contact id.

        // Create query column array.
        String queryColumnArr[] = {ContactsContract.RawContacts._ID};

        // Create where condition clause.
        String displayName = givenName;
        String whereClause = ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " = '" + displayName + "'";

        // Query raw contact id through RawContacts uri.
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI;

        // Return the query cursor.
        Cursor cursor = contentResolver.query(rawContactUri, queryColumnArr, whereClause, null, null);

        long rawContactId = -1;

        if (cursor != null) {
            // Get contact count that has same display name, generally it should be one.
            int queryResultCount = cursor.getCount();
            // This check is used to avoid cursor index out of bounds exception. android.database.CursorIndexOutOfBoundsException
            if (queryResultCount > 0) {
                // Move to the first row in the result cursor.
                cursor.moveToFirst();
                // Get raw_contact_id.
                rawContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            }
        }

        return rawContactId;
    }
}
