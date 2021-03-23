package com.github.remotesdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.enable();
        Thread.sleep(1000);
        adapter.setName("Papa");
        Thread.sleep(1000);
        System.out.println(adapter.getName());
       BluetoothDevice device =  adapter.getRemoteDevice("C0:10:B1:36:68:47");
        Method method = device.getClass().getMethod("cancelPairing");
        method.setAccessible(true);
         method.invoke(device);

    }
}