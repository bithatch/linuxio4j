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
package uk.co.bithatch.linuxio;

import java.io.EOFException;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import uk.co.bithatch.linuxio.CLib.pollfd;
import uk.co.bithatch.linuxio.InputDevice.Event;

/**
 * Manages keyboard and mouse input devices. This is the recommended way to
 * captures events from {@link InputDevice} instances. There is one
 * {@link InputController} per runtime, the instance of which is obtained using
 * {@link #getInstance()}. Then {@link InputDevice} instances are then
 * registered using {@link #add(InputDevice, Callback)}. The callback argument
 * will have it's {@link Callback#event(InputDevice, Event)} method invoked
 * whenever events from that device are received.
 * <p>
 * When {@link #add(InputDevice, Callback)} is used for the first time, a
 * thread is started to handle the polling. The same thread is then used for
 * subsequent devices.
 * <p>
 * Devices may be de-registered using {@link #remove(InputDevice)}. When the
 * last device is removed, the polling thread is also shutdown.
 *
 */
public class InputController {

	final static Logger LOG = System.getLogger(InputController.class.getName());

	/**
	 * The Interface Callback.
	 */
	public interface Callback {
		
		/**
		 * Event.
		 *
		 * @param device the device
		 * @param event the event
		 */
		void event(InputDevice device, Event event);
	}

	private Map<InputDevice, Callback> devices = new HashMap<InputDevice, InputController.Callback>();
	private Map<Integer, InputDevice> devicesByFd = new HashMap<Integer, InputDevice>();
	private pollfd[] pollFds;
	private Semaphore semaphore = new Semaphore(1);

	private final static InputController INSTANCE = new InputController();

	/**
	 * Get the static instance of the controller.
	 * 
	 * @return controller instance
	 */
	public final static InputController getInstance() {
		return INSTANCE;
	}

	/**
	 * Remove a device. The callback registered will no longer receive events, and
	 * if this is the last device being removed, the polling thread will be
	 * shutdown.
	 * 
	 * @param device device to remove.
	 */
	public void remove(InputDevice device) {
		try {
			synchronized (devices) {
				if (!devices.containsKey(device)) {
					throw new IllegalArgumentException("No such device.");
				}

				if (LOG.isLoggable(Level.DEBUG))
					LOG.log(Level.DEBUG, "Removing device " + device + " from UInput polling");

				if (devices.size() == 1) {
					// We will be closing
					semaphore.acquire();
				}

				devicesByFd.remove(device.getFD());
				devices.remove(device);

				if (devices.isEmpty()) {
					// Wait for the first acquire to be released indicating
					// thread
					// is now gone
					semaphore.acquire();

					// Release out own acquire
					semaphore.release();
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to remove UInput device from polling.");
		}

		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Removed device " + device + " from UInput polling");
	}

	/**
	 * Add a new device to be monitored for events, calling the
	 * {@link Callback#event(InputDevice, Event)} method of the provided callback.
	 * <p>
	 * If this is the first device to be added, the polling thread will also be
	 * started.
	 * 
	 * @param device   device to monitor
	 * @param callback callback invoked when event arrives for this device
	 */
	public void add(InputDevice device, Callback callback) {
		synchronized (devices) {
			devices.put(device, callback);
			devicesByFd.put(device.getFD(), device);
			if (devices.size() == 1) {
				if (LOG.isLoggable(Level.DEBUG))
					LOG.log(Level.DEBUG, "Starting UInput polling");
				Thread t = new Thread("UInput") {
					public void run() {
						try {
							poll();
						} catch (IOException e) {
							LOG.log(Level.ERROR, "Failed to poll.", e);
						}
					}
				};
				t.setPriority(Thread.MAX_PRIORITY);
//				t.setDaemon(true);
				t.start();
			}
			if (LOG.isLoggable(Level.DEBUG))
				LOG.log(Level.DEBUG, "Added " + device + " to polling");
		}
	}

	private void poll() throws IOException {
		try {
			while (devices.size() > 0) {
				if (pollFds == null || pollFds.length != devices.size()) {
					synchronized (devices) {
						pollFds = (CLib.pollfd[]) new CLib.pollfd().toArray(devices.size());
						int i = 0;
						for (InputDevice dev : devices.keySet()) {
							// pollfd pfd = new CLib.pollfd();
							pollfd pfd = pollFds[i];
							pfd.fd = dev.getFD();
							pfd.events = CLib.POLLIN | CLib.POLLPRI;
							pollFds[i] = pfd;
							i++;
						}

					}
				}

				int rel = CLib.INSTANCE.poll(pollFds, pollFds.length, 1000);
				if (rel == 0) {
					// Timeout, no data, just loop
					if (LOG.isLoggable(Level.TRACE)) {
						LOG.log(Level.TRACE, "No data, waiting");
					}
				} else if (rel < 0) {
					// Error!
					throw new RuntimeException("Poll returned " + rel);
				} else {
					// Success, have data, get the events
					for (pollfd pfd : pollFds) {
						if (pfd.revents != 0) {
							InputDevice dev = devicesByFd.get(pfd.fd);
							if (dev == null) {
								LOG.log(Level.WARNING, "Could not find device for FD " + pfd.fd);
							} else {
								try {
									Event event = dev.nextEvent();
									if (event != null) {
										devices.get(dev).event(dev, event);
									}
								} catch (EOFException eof) {
									//
								} catch(IllegalArgumentException iae) {
									LOG.log(Level.WARNING, "Failed to process key.", iae);
								}
							}
						}
					}
				}
			}
		} finally {
			semaphore.release();
			if (LOG.isLoggable(Level.DEBUG))
				LOG.log(Level.DEBUG, "No long polling for UInput events");
		}
	}

	/**
	 * Stop.
	 */
	public void stop() {
		for (InputDevice d : new ArrayList<InputDevice>(devices.keySet())) {
			remove(d);
		}
	}
}
