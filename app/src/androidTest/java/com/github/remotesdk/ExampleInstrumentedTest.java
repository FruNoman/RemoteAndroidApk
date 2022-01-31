package com.github.remotesdk;

import android.content.Context;
import android.hardware.usb.UsbManager;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws InterruptedException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException, NoSuchFieldException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        UserManager userManager = (UserManager) appContext.getSystemService(Context.USER_SERVICE);
//        Method method = userManager.getClass().getDeclaredMethod("getUsers");
//        ObjectMapper mapper = new ObjectMapper();
//        Object object = method.invoke(userManager);
//        mapper.writeValueAsString(object);
        UsbManager usbManager = (UsbManager) appContext.getSystemService(Context.USB_SERVICE);
        Method method = usbManager.getClass().getDeclaredMethod("getPorts");
        method.invoke(usbManager);


    }
}