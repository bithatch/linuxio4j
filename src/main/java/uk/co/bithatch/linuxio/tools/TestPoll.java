package uk.co.bithatch.linuxio.tools;

import uk.co.bithatch.linuxio.UInputController;
import uk.co.bithatch.linuxio.UInputDevice;
import uk.co.bithatch.linuxio.UInputController.Callback;
import uk.co.bithatch.linuxio.UInputDevice.Event;

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
