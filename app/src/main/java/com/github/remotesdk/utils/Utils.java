package com.github.remotesdk.utils;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.provider.ContactsContract;

import com.github.remotesdk.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {
    public static String getRandomPhone() {
        return String.format("+38(0%02d)-%03d-%04d",
                (int) Math.floor(99 * Math.random()),
                (int) Math.floor(999 * Math.random()),
                (int) Math.floor(9999 * Math.random()));
    }


    public static void generateContacts(Context context, int count) throws IOException {
        InputStream stream = context.getResources().openRawResource(R.raw.names);
        BufferedReader bR = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        List<String> names = new ArrayList<>();
        while ((line = bR.readLine()) != null) {

            names.add(line);
        }
        stream.close();

        InputStream lastStream = context.getResources().openRawResource(R.raw.lastname);
        BufferedReader lastBr = new BufferedReader(new InputStreamReader(lastStream));
        List<String> lastNames = new ArrayList<>();
        while ((line = lastBr.readLine()) != null) {
            lastNames.add(line);
        }
        lastStream.close();

        System.out.println(names);
        System.out.println(lastNames);
        Random random = new Random();
        for (int x = 0; x < count; x++) {
            String name = names.get(random.nextInt(names.size() - 1)) + " " + lastNames.get(random.nextInt(lastNames.size() - 1));
            String mobileNumber = Utils.getRandomPhone();

            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());


            operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build());


            operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 2)
                    .build());

            try {
                ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
