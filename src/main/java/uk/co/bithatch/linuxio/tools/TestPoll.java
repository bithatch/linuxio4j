/**
 * Linux I/O For Java - A JNA based library providing access to some low-level Linux subsystems
 * Copyright © 2012 Bithatch (tanktarta@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
