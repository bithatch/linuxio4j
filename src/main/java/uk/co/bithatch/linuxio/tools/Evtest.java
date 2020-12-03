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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import uk.co.bithatch.linuxio.EventCode;
import uk.co.bithatch.linuxio.InputDevice;
import uk.co.bithatch.linuxio.InputDevice.Event;

/**
 * The Class Evtest.
 */
public class Evtest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public final static void main(String[] args) throws Exception {
		List<String> argList = new ArrayList<>(Arrays.asList(args));
		boolean grab = false;
		String path = null;
		while (!argList.isEmpty()) {
			String arg = argList.remove(0);
			if (arg.equals("--grab"))
				grab = true;
			else if (arg.equals("--query")) {
				System.err.println("WARNING: --query option is not yet implemented.");
			}
			else if (arg.startsWith("-")) {
				throw new IllegalArgumentException(String.format("invalid option -- ''", arg));
			} else if (path == null) {
				path = arg;
			}
		}

		InputDevice device = null;

		if (path == null) {
			System.out.println("No device specified, trying to scan all of /dev/input/event*");
			System.out.println("Available devices:");
			List<InputDevice> availableDevices = InputDevice.getAvailableDevices();
			for (InputDevice dev : availableDevices) {
				System.out.println(String.format("%-24s %s", dev.getFile() + ":", dev.getName()));
			}
			System.out.print(String.format("Select the device event number [0-%d]:", availableDevices.size()));
			while (true) {
				String line = new BufferedReader(new InputStreamReader(System.in)).readLine();
				if (!line.equals("")) {
					int idx = Integer.parseInt(line);
					device = availableDevices.get(idx); 
					break;
				}
			}
		} else
			device = new InputDevice(new File(path));

		device.open();
		if(grab)
			device.grab();
		try {
			System.out.println(String.format("Input driver version is %s", device.getDriverVersion()));
			System.out.println(String.format("Input device ID: bus 0x%02x vendor 0x%02x product 0x%02x vendor 0x%02x", 
							device.getBus(), device.getVendor(), device.getProduct(), device.getVersion()));
			System.out.println(String.format("Input device name: \"%s\"", device.getName()));
			System.out.println("Supported events:");
			for(EventCode.Type type : device.getSupportedTypes()) {
				System.out.println(String.format("  Event type %d (%s)", type.code(), type.name()));
				for(EventCode eventCode : device.getCapabilities(type)) {
					System.out.println(String.format("    Event code %d (%s)", eventCode.code(), eventCode.name()));
					if(type == EventCode.Type.EV_ABS) {
						for(Map.Entry<EventCode.AbsoluteValue, Integer> en : device.getAbsoluteValues().get(eventCode).entrySet()) {
							System.out.println(String.format("      %-15s %5d", en.getKey(), en.getValue()));
						}
					}
				}
			}
			System.out.println("Properties:");
			for(EventCode.Property type : device.getProperties()) {
				System.out.println(String.format("  Property type %d (%s)", type.code(), type.name()));
			}
			
			Event event;
			while((event = device.nextEvent()) != null) {
				System.out.println(event);
			};
			
		} finally {
			device.close();
		}
	}
}
