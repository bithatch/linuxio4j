package com.nervepoint.linuxio.tools;

import com.nervepoint.linuxio.UInputController;
import com.nervepoint.linuxio.UInputController.Callback;
import com.nervepoint.linuxio.UInputDevice.Event;
import com.nervepoint.linuxio.UInputDevice;

public class TestPoll {

    public static void main(String[] args) throws Exception {
        Callback callback = new Callback() {
            @Override
            public void event(UInputDevice device, Event event) {
                System.err.println(device + " = " + event);
            }
        };
        
        for(UInputDevice dev : new UInputDevice[] { UInputDevice.getFirstKeyboardDevice(), UInputDevice.getFirstPointerDevice() }) {
            System.err.println("Open " + dev);
            UInputController.getInstance().add(dev, callback);
        }
    }
}
