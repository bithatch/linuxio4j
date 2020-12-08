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

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import uk.co.bithatch.linuxio.CLib.pollfd;
import uk.co.bithatch.linuxio.EventCode.AbsoluteValue;
import uk.co.bithatch.linuxio.EventCode.Ev;
import uk.co.bithatch.linuxio.EventCode.Property;
import uk.co.bithatch.linuxio.EventCode.Type;
import uk.co.bithatch.linuxio.Input.input_absinfo;
import uk.co.bithatch.linuxio.UInput.uinput_setup;

/**
 * Provides access to the Linux linux user input system (uinput). All input
 * devices such as keyboards, mice, touch pads and others supported by the
 * kernel are accessible as special files in /dev/input.
 * <p>
 * To read capabilities and events from a device, construct a new
 * {@link InputDevice}, passing it the filename of the device.
 * <p>
 * You may then read various capabilities or events using the methods provided
 * in this class.
 * <p>
 * There are a couple of helper methods to get the first available mouse or
 * keyboard device. See {@link #getFirstKeyboardDevice()} and
 * {@link #getFirstPointerDevice()}.
 * <p>
 * If your uinput device files are somewhere other that <i>/dev/input</i>, you
 * may set the system property <b>linuxio.input.deviceS</b> to the path that
 * contains them.
 * <p>
 * You can also create virtual devices and emit events from them. For this use
 * the {@link InputDevice(String, short, short)} constructor and
 * the {@link #emit(Event)} methods.
 * 
 */
public class InputDevice implements Closeable {

	private static final String SYSPROP_LINUXIO_POINTER_TYPES = "linuxio.pointer.types";
	private static final String INPUT_DEVICES = "linuxio.input.devices";

	final static Logger LOG = System.getLogger(InputDevice.class.getName());

	/**
	 * The Class Event.
	 */
	public static class Event {
		private long utime;
		private EventCode code;
		private int value;

		private Event(Input.input_event ev) {
			this(ev.time.tv_usec.longValue() * 1000, EventCode.fromCode(ev.type, ev.code), ev.value);
		}

		/**
		 * Instantiates a new event.
		 *
		 * @param code the code
		 */
		public Event(EventCode code) {
			this(code, 0);
		}

		/**
		 * Instantiates a new event.
		 *
		 * @param code the code
		 * @param value the value
		 */
		public Event(EventCode code, int value) {
			this(0, code, value);
		}

		/**
		 * Instantiates a new event.
		 *
		 * @param utime the utime
		 * @param code the code
		 * @param value the value
		 */
		public Event(long utime, EventCode code, int value) {
			super();
			this.utime = utime;
			this.code = code;
			this.value = value;
		}

		/**
		 * Get the time in milliseconds since 1st Jan 1970 12:00.
		 * 
		 * @return time in milliseconds
		 */
		public long getTime() {
			return utime / 1000;
		}

		/**
		 * Get the time in microseconds since 1st Jan 1970 12:00.
		 * 
		 * @return time in microseconds
		 */
		public long getUTime() {
			return utime;
		}

		/**
		 * Gets the code.
		 *
		 * @return the code
		 */
		public EventCode getCode() {
			return code;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public int getValue() {
			return value;
		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return "Event [time=" + utime + ", code=" + code + ", value=" + value + "]";
		}

	}

	/** The Constant SYN. */
	public final static Event SYN = new Event(EventCode.SYN_REPORT, 0);

	/** The Constant ABSVAL. */
	public final static String[] ABSVAL = new String[] { "Value", "Min", "Max", "Fuzz", "Flat" };

	private Path file;
	private int fd;
	private boolean grabbed;
	private String name;
	private String inputDriverVersion;
	private boolean open;
	private Set<EventCode> caps = new LinkedHashSet<>();
	private Set<Property> props = new LinkedHashSet<>();
	private Map<EventCode, Map<EventCode.AbsoluteValue, Integer>> absoluteValues = new TreeMap<>();
	private pollfd pollFd;
	private pollfd[] pollFds;
	private boolean read;
	private int vendor;
	private int product;
	private int bus = UInput.BUS_USB;
	private int version;
	private boolean capsRead;

	/**
	 * Helper to get what appears to be the first pointer device (e.g. a mouse). If
	 * no devices could be found, an exception will be thrown.
	 * <p>
	 * This method will search for both EV_ABS (absolute) and EV_REL (relative)
	 * devices, in that order by default. If you wish to search in a different
	 * order, set the system property <b>linuxio.pointer.types</b> to a comma
	 * separated string of the type codes (3 for absolute, 2 for relative), or use
	 * the {@link Type} constant names EV_ABS and EV_REL.
	 *
	 * @return pointer device
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final static InputDevice getFirstPointerDevice() throws IOException {
		//
		for (String typeName : System
				.getProperty(SYSPROP_LINUXIO_POINTER_TYPES, Type.EV_ABS.code() + "," + Type.EV_REL.code()).split(",")) {

			// Parse the type name either by its
			Type t = null;
			try {
				t = Type.fromCode(Integer.parseInt(typeName));
			} catch (NumberFormatException nfe) {
				t = Type.valueOf(typeName);
			}

			if (t == null) {
				LOG.log(Level.WARNING,
						"Unknown event type in " + SYSPROP_LINUXIO_POINTER_TYPES + " property, '" + typeName + "'");
			} else {
				List<InputDevice> availableDevices = getAvailableDevices();
				InputDevice theDevice = null;
				try {
					for (InputDevice dev : availableDevices) {
						if (LOG.isLoggable(Level.DEBUG))
							LOG.log(Level.DEBUG, dev.getName());
						Collection<EventCode> codes = dev.getCapabilities();
						if (t == Type.EV_REL) {
							if (codes == null
									|| (!codes.contains(EventCode.REL_X) && !codes.contains(EventCode.REL_Y))) {
								continue;
							}
						} else if (t == Type.EV_ABS) {
							if (codes == null
									|| (!codes.contains(EventCode.ABS_X) && !codes.contains(EventCode.ABS_Y))) {
								continue;
							}
						}
						LOG.log(Level.TRACE, "Device has some " + t + " caps");
						codes = Type.EV_KEY.get(codes);
						int buttons = 0;
						for (EventCode s : codes) {
							if (s.isButton()) {
								buttons++;
							}
						}
						if (buttons > 0) {
							theDevice = dev;
							break;
						}

					}
				} finally {
					for (InputDevice d : availableDevices) {
						if (d != theDevice) {
							d.close();
						}
					}
				}

				if (theDevice != null)
					return theDevice;
			}
		}
		throw new IOException("No devices that look like a pointer could be found.");
	}

	/**
	 * Helper to get what appears to be all pointer devices (e.g. a mouse).
	 * <p>
	 * This method will search for both EV_ABS (absolute) and EV_REL (relative)
	 * devices, in that order by default. If you wish to search in a different
	 * order, set the system property <b>linuxio.pointer.types</b> to a comma
	 * separated string of the type codes (3 for absolute, 2 for relative), or use
	 * the {@link Type} constant names EV_ABS and EV_REL.
	 *
	 * @return pointer devices
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final static List<InputDevice> getAllPointerDevices() throws IOException {
		List<InputDevice> pointerDevices = new ArrayList<InputDevice>();
		//
		for (String typeName : System
				.getProperty(SYSPROP_LINUXIO_POINTER_TYPES, Type.EV_ABS.code() + "," + Type.EV_REL.code()).split(",")) {

			// Parse the type name either by its
			Type t = null;
			try {
				t = Type.fromCode(Integer.parseInt(typeName));
			} catch (NumberFormatException nfe) {
				t = Type.valueOf(typeName);
			}

			if (t == null) {
				LOG.log(Level.WARNING,
						"Unknown event type in " + SYSPROP_LINUXIO_POINTER_TYPES + " property, '" + typeName + "'");
			} else {
				List<InputDevice> availableDevices = getAvailableDevices();
				for (InputDevice dev : availableDevices) {
					if (LOG.isLoggable(Level.DEBUG))
						LOG.log(Level.DEBUG, dev.getName());
					Collection<EventCode> codes = dev.getCapabilities();
					if (t == Type.EV_REL) {
						if (codes == null || (!codes.contains(EventCode.REL_X) && !codes.contains(EventCode.REL_Y))) {
							continue;
						}
					} else if (t == Type.EV_ABS) {
						if (codes == null || (!codes.contains(EventCode.ABS_X) && !codes.contains(EventCode.ABS_Y))) {
							continue;
						}
					}
					if (LOG.isLoggable(Level.DEBUG))
						LOG.log(Level.DEBUG, "Device has some " + t + " caps");
					codes = Type.EV_KEY.get(codes);
					int buttons = 0;
					for (EventCode s : codes) {
						if (s.isButton()) {
							buttons++;
						}
					}
					if (buttons > 0) {
						pointerDevices.add(dev);
					} else {
						dev.close();
					}

				}
			}
		}
		return pointerDevices;
	}

	/**
	 * Helper to get what appears to be all keyboard device names.
	 *
	 * @return keyboard device names
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final static List<String> getAllKeyboardDeviceNames() throws IOException {
		List<String> keyboardDeviceNames = new ArrayList<String>();
		List<InputDevice> availableDevices = getAvailableDevices();
		try {
			for (InputDevice dev : availableDevices) {
				if (LOG.isLoggable(Level.DEBUG))
					LOG.log(Level.DEBUG, dev.getName());
				Collection<EventCode> codes = Type.EV_KEY.get(dev.getCapabilities());
				if (codes == null) {
					continue;
				}
				int keys = 0;
				for (EventCode s : codes) {
					if (s.isButton()) {
						// Has buttons, presumably not a keyboard
						continue;
					} else if (s.isKey()) {
						keys++;
					}
				}
				if (keys > 5) {
					keyboardDeviceNames.add(dev.getFile().getFileName().toString());
				}
			}
		} finally {
			for (InputDevice d : availableDevices) {
				d.close();
			}
		}
		return keyboardDeviceNames;
	}

	/**
	 * Helper to get what appears to be all keyboard devices. If no devices could be
	 * found, an exception will be thrown. NOTE, all {@link InputDevice} returned
	 * will be <b>open</b>, so should be closed if you do not intend to carry on
	 * using them.
	 *
	 * @return keyboard devices
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final static List<InputDevice> getAllKeyboardDevices() throws IOException {
		List<InputDevice> keyboardDevices = new ArrayList<InputDevice>();
		for (InputDevice dev : getAvailableDevices()) {
			if (LOG.isLoggable(Level.DEBUG))
				LOG.log(Level.DEBUG, dev.getName());
			Collection<EventCode> codes = Type.EV_KEY.get(dev.getCapabilities());
			if (codes == null) {
				continue;
			}
			int keys = 0;
			for (EventCode s : codes) {
				if (s.isButton()) {
					// Has buttons, presumably not a keyboard
					continue;
				} else if (s.isKey()) {
					keys++;
				}
			}
			if (keys > 5) {
				keyboardDevices.add(dev);
			} else {
				dev.close();
			}
		}
		return keyboardDevices;
	}

	/**
	 * Helper to get an open device given its name.
	 *
	 * @param name the name
	 * @return device
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final static InputDevice getDeviceByName(String name) throws IOException {

		List<InputDevice> availableDevices = getAvailableDevices();
		for (InputDevice dev : availableDevices) {
			if (dev.getName().equals(name)) {
				return dev;
			} else {
				dev.close();
			}
		}

		throw new IllegalArgumentException("No devices with name " + name);
	}

	/**
	 * Helper to get what appears to be the first keyboard device. If no devices
	 * could be found, an exception will be thrown.
	 *
	 * @return keyboard device
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final static InputDevice getFirstKeyboardDevice() throws IOException {

		List<InputDevice> availableDevices = getAvailableDevices();
		InputDevice theDevice = null;
		try {
			for (InputDevice dev : availableDevices) {
				if (LOG.isLoggable(Level.DEBUG))
					LOG.log(Level.DEBUG, dev.getName());
				Collection<EventCode> codes = Type.EV_KEY.get(dev.getCapabilities());
				if (codes == null) {
					continue;
				}
				int keys = 0;
				for (EventCode s : codes) {
					if (s.isButton()) {
						// Has buttons, presumably not a keyboard
						continue;
					} else if (s.isKey()) {
						keys++;
					}
				}
				if (keys > 5) {
					theDevice = dev;
					break;
				}
			}
			if (theDevice != null) {
				return theDevice;
			}
		} finally {
			for (InputDevice d : availableDevices) {
				if (d != theDevice) {
					d.close();
				}
			}
		}

		throw new IOException("No devices that look like a keyboard could be found.");
	}

	/**
	 * Get a list of all available devices;.
	 *
	 * @return the available devices
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final static List<InputDevice> getAvailableDevices() throws IOException {
		final List<InputDevice> d = new ArrayList<InputDevice>();
		File dir = getInputDeviceDirectory();
		if (dir.exists()) {
			if (dir.canRead()) {
				for (File f : dir.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.canRead() && pathname.getName().startsWith("event");
					}
				})) {
					d.add(new InputDevice(f));
				}
			} else {

				if (System.getProperties().containsKey(INPUT_DEVICES)) {
					throw new IOException("The directory '" + dir + "' specified by the system property "
							+ INPUT_DEVICES + " for uinput devices cannot be read.");
				} else {
					throw new IOException("Directory '" + dir + "' for uinput devices cannot be read.");
				}
			}
		} else {
			if (System.getProperties().containsKey(INPUT_DEVICES)) {
				throw new IOException("The directory '" + dir + "' specified by the system property " + INPUT_DEVICES
						+ " for uinput devices does not exist.");
			} else {
				throw new IOException("Directory for uinput devices does not exist. Try setting the system property "
						+ INPUT_DEVICES + " to the correct location.");
			}
		}
		Collections.sort(d, (a, b) -> Integer.valueOf(a.file.getFileName().toString().replaceAll("[^\\d.]", ""))
				.compareTo(Integer.valueOf(b.file.getFileName().toString().replaceAll("[^\\d.]", ""))));
		return d;
	}

	private static File getInputDeviceDirectory() {
		return new File(System.getProperty(INPUT_DEVICES, "/dev/input"));
	}

	/**
	 * Open an existing device given the path for it's file. If the path starts with
	 * a slash, that exact path will be used. If it doesn't, and the path relative
	 * to the current directory exists, that will be used. Otherwise, it will be
	 * assumed to be relative to the default input device directory.
	 * <p>
	 * For example, all the following are valid :-
	 * <ul>
	 * <li><b>event0</b> The path /dev/input/event0 will be used</li>
	 * <li><b>input/event0</b> The path /dev/input/event0 will be used, assuming the
	 * current directory is /dev</li>
	 * <li><b>/dev/input/event0</b> The path will be used as is.
	 * </ul>
	 * 
	 * 
	 * @param path device file path
	 * @throws IOException if device file cannot be opened.
	 */
	public InputDevice(String path) throws IOException {
		this(path.startsWith("/") ? new File(path)
				: (new File(path).exists() ? new File(path) : new File(getInputDeviceDirectory(), path)));
	}

	/**
	 * Open an existing device given it's file for reading events.
	 *
	 * @param file device file
	 * @throws IOException if device file cannot be opened.
	 */
	public InputDevice(File file) throws IOException {
		this(file.toPath());
	}

	/**
	 * Open an existing device given it's file for reading events.
	 *
	 * @param file device file
	 * @throws IOException if device file cannot be opened.
	 */
	public InputDevice(Path file) throws IOException {

		this.file = file;
		this.read = true;
		openForRead(file);

		// Get the name
		byte[] nameBytes = new byte[256];
		CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGNAME(nameBytes.length), nameBytes);
		name = Native.toString(nameBytes);

		// Get the driver version
		IntByReference v = new IntByReference();
		CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGVERSION, v);
		int vv = v.getValue();
		inputDriverVersion = String.format("%d.%d.%d", vv >> 16, vv >> 8 & 0xff, vv & 0xff);

		// Get the device IDs
		short[] deviceId = new short[4];
		CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGID, deviceId);
		bus = deviceId[0];
		vendor = deviceId[1];
		product = deviceId[2];
		version = deviceId[3];

	}

	/**
	 * Create a new virtual device for emitting events.
	 * 
	 * @param name    virtual device name
	 * @param vendor  USB vendor code
	 * @param product USB prodcut code
	 * 
	 */
	public InputDevice(String name, int vendor, int product) {
		this.read = false;
		this.vendor = vendor;
		this.product = product;
		file = Paths.get("/dev/uinput");
		this.name = name;
	}

	/**
	 * Open.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void open() throws IOException {
		if (read) {
			openForRead(file);
		} else {
			openForWrite();
		}
	}

	protected void readCaps() throws IOException {
		// Get the supported events
		NativeLong[][] bit = new NativeLong[EventCode.Ev.EV_MAX][NBITS(EventCode.KEY_MAX.code())];
		CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGBIT(0, EventCode.Ev.EV_MAX), bit[0]);

		for (int i = 0; i < EventCode.Ev.EV_MAX; i++) {
			Type t = Type.fromCode(i);
			if (test_bit(i, bit[0])) {
				if (LOG.isLoggable(Level.DEBUG))
					LOG.log(Level.DEBUG, String.format("  Event type %d (%s)\n", i, Type.fromCode((short) i)));
				// if (!i) continue;
				CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGBIT(i, EventCode.KEY_MAX.code()), bit[i]);
				for (int j = 0; j < EventCode.KEY_MAX.code(); j++) {
					if (test_bit(j, bit[i])) {
						if (LOG.isLoggable(Level.DEBUG))
							LOG.log(Level.DEBUG, String.format("    Event code %d (%s)", j,
									EventCode.hasCode(i, (short) j) ? EventCode.fromCode(i, j) : ""));
						try {
							EventCode eventCode = EventCode.fromCode(t.code(), j);
							addCapability(eventCode);
							if (t.equals(Type.EV_ABS)) {
								input_absinfo abs = new input_absinfo();
								checkIoctl(CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGABS(j), abs));
								Map<EventCode.AbsoluteValue, Integer> map = absoluteValues.get(eventCode);
								if (map == null) {
									map = new TreeMap<>();
									absoluteValues.put(eventCode, map);
								}
								map.put(EventCode.AbsoluteValue.MIN, abs.minimum);
								map.put(EventCode.AbsoluteValue.MAX, abs.maximum);
								map.put(EventCode.AbsoluteValue.VALUE, abs.value);
								if (abs.fuzz > 0)
									map.put(EventCode.AbsoluteValue.FUZZ, abs.fuzz);
								if (abs.flat > 0)
									map.put(EventCode.AbsoluteValue.FLAT, abs.flat);
								if (abs.resolution > 0)
									map.put(EventCode.AbsoluteValue.FLAT, abs.resolution);
							}
						} catch (IllegalArgumentException iae) {
							LOG.log(Level.DEBUG, String.format(
									"Missing UInput constant for type %d and code %d for device %s", i, j, file));
						}
					}
				}
			}
		}

		// Get properties
		NativeLong[] props = new NativeLong[EventCode.Property.INPUT_PROP_MAX.code()];
		CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGPROP(EventCode.Property.INPUT_PROP_MAX.code()), props);
		for (int i = 0; i < EventCode.Property.INPUT_PROP_MAX.code(); i++) {
			if (test_bit(i, props))
				this.props.add(Property.fromCode(i));
		}
	}

	private void openForWrite() throws IOException {
		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Opening uinput " + file + " for " + getClass());
		fd = CLib.INSTANCE.open(file.toAbsolutePath().toString(), CLib.O_WRONLY | CLib.O_NONBLOCK);
		if (fd == -1) {
			throw new IOException(file + " cannot be opened for " + getClass());
		}
		open = true;

		IntByReference driverVersion = new IntByReference();
		int rc = CLib.INSTANCE.ioctl(fd, UInput.UI_GET_VERSION, driverVersion.getPointer());
		int v = driverVersion.getValue();
		if (rc == 0 && v >= 5) {

			for (EventCode en : caps) {
				checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_SET_EVBIT, en.type().code()));
				switch (en.type()) {
				case EV_KEY:
					checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_SET_KEYBIT, en.code()));
					break;
				case EV_REL:
					checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_SET_RELBIT, en.code()));
					break;
				case EV_ABS:
					checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_SET_ABSBIT, en.code()));
					break;
				default:
					throw new UnsupportedOperationException(String.format("Unsupported event type %d.", en.type()));
				}
			}

			uinput_setup setup = new uinput_setup();
			setup.id.bustype = (short) bus;
			setup.id.product = (short) product;
			setup.id.vendor = (short) vendor;
			setup.id.version = (short) this.version;
			System.arraycopy(name.getBytes(), 0, setup.name, 0,
					Math.min(setup.name.length - 1, Math.min(name.getBytes().length, setup.name.length)));

			checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_DEV_SETUP, setup));
			checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_DEV_CREATE));

			// TODO set properties

			for (Map.Entry<EventCode, Map<EventCode.AbsoluteValue, Integer>> en : absoluteValues.entrySet()) {
				input_absinfo absinfo = new input_absinfo();
				absinfo.minimum = en.getValue().getOrDefault(AbsoluteValue.MIN, 0);
				absinfo.maximum = en.getValue().getOrDefault(AbsoluteValue.MAX, 0);
				absinfo.value = en.getValue().getOrDefault(AbsoluteValue.VALUE, 0);
				absinfo.fuzz = en.getValue().getOrDefault(AbsoluteValue.FUZZ, 0);
				absinfo.resolution = en.getValue().getOrDefault(AbsoluteValue.RESOLUTION, 0);
				absinfo.flat = en.getValue().getOrDefault(AbsoluteValue.FLAT, 0);
				checkIoctl(CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCSABS(en.getKey().code()), absinfo));
			}
		} else {
			throw new UnsupportedOperationException();
		}

	}

	private void checkIoctl(int status) throws IOException {
		if (status != 0) {
			throw new IOException(String.format("ioctl failed with %d.", Native.getLastError()));
		}
	}

	private void openForRead(Path file) throws IOException {
		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Opening existing device " + file + " for " + getClass());
		fd = CLib.INSTANCE.open(file.toAbsolutePath().toString(), CLib.O_RDWR | CLib.O_NOCTTY);
		if (fd == -1) {
			throw new IOException(file + " is not a valid input device for " + getClass());
		}
		open = true;
	}

	/**
	 * Get the driver version.
	 *
	 * @return driver version
	 */
	public String getDriverVersion() {
		return inputDriverVersion;
	}

	/**
	 * Get the name for this device.
	 *
	 * @return device name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Add a capability to an event type. Only relevant when creating a new virtual
	 * device, and must be done before the call is made to {@link #open()}.
	 *
	 * @param codes the codes
	 */
	public void addCapability(EventCode... codes) {
		caps.addAll(Arrays.asList(codes));
	}

	/**
	 * Either get the capabilities the device has, or set the capabilities should
	 * have, depending on whether the device is being created as a virtual device or
	 * opened as an existing device.
	 * 
	 * @return capabilities
	 */
	public Set<EventCode> getCapabilities() {
		if (read || open) {
			checkCapsRead();
			return Collections.unmodifiableSet(caps);
		} else
			return caps;
	}

	/**
	 * Get the absolute values this device has, or set them for creating a new
	 * virtual device.
	 * 
	 * @return absolute data
	 */
	public Map<EventCode, Map<AbsoluteValue, Integer>> getAbsoluteValues() {
		if (read || open) {
			checkCapsRead();
			return Collections.unmodifiableMap(absoluteValues);
		} else
			return absoluteValues;
	}

	/**
	 * Get the properties (quirks) this device has, or set them when creating a
	 * virtual device.
	 * 
	 * @return properties
	 */
	public Set<Property> getProperties() {
		if (read || open) {
			checkCapsRead();
			return Collections.unmodifiableSet(props);
		} else
			return props;
	}

	protected void checkCapsRead() {
		if (read && !capsRead) {
			try {
				readCaps();
			} catch (IOException e) {
				throw new IllegalStateException("Failed to read capabilities.", e);
			}
			capsRead = true;
		}
	}

	/**
	 * Get the capabilities the device has (or should have) for a particular event
	 * type. This set may not be notified.
	 *
	 * @param type the type
	 * @return capabilities
	 */
	public Set<EventCode> getCapabilities(EventCode.Type type) {
		Set<EventCode> l = new LinkedHashSet<>();
		for (EventCode code : getCapabilities()) {
			if (code.type() == type)
				l.add(code);
		}
		return l;
	}

	/**
	 * Get the path to file this input device is accessed by.
	 *
	 * @return the file
	 */
	public Path getFile() {
		return file;
	}

	/**
	 * Grab the device for exclusive use.
	 * 
	 * @throws IOException if device cannot be grabbed.
	 */
	public void grab() throws IOException {
		if (!read) {
			throw new IllegalStateException("This device is not reading.");
		}
		if (grabbed) {
			throw new IllegalStateException("Already grabbed " + file + ".");
		}
		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Grabbing " + file);

		// EVIOCGRAB = 0x40044590
		if (CLib.INSTANCE.ioctl(fd, 0x40044590, 1) == -1) {
			// if(CLib.INSTANCE.ioctl(fd, new
			// NativeLong(CLib.Macros.EVIOCGRAB()), 1) == -1) {
			if ("true".equals(System.getProperty("linuxio.exceptionOnGrabFail", "true"))) {
				throw new IOException("Failed to grab.");
			}
		} else {
			if (LOG.isLoggable(Level.DEBUG))
				LOG.log(Level.DEBUG, "Grabbed " + file);
			grabbed = true;
		}
	}

	/**
	 * Release the device from exclusive use.
	 * 
	 * @throws IOException if device cannot be ungrabbed
	 */
	public void ungrab() throws IOException {
		if (!read) {
			throw new IllegalStateException("This device is not a reading.");
		}
		if (!grabbed) {
			throw new IllegalStateException("Not grabbed " + file + ".");
		}
		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Ungrabbing " + file);
		CLib.INSTANCE.ioctl(fd, 0x40044590, 0);
		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Ungrabbed " + file);
		grabbed = false;
	}

	/**
	 * Get if the device is currently grabbed for exclusive use.
	 * 
	 * @return grabbed
	 */
	public boolean isGrabbed() {
		if (!read) {
			throw new IllegalStateException("This device is not a reading.");
		}
		return grabbed;
	}

	/**
	 * Convenience method to press a key (SYN will be emitted).
	 * 
	 * @param code code of key to press
	 * @throws IOException on error
	 */
	public void pressKey(int code) throws IOException {
		emit(new Event(EventCode.fromCode(Ev.EV_KEY, code), 1));
	}

	/**
	 * Convenience method to press a key (SYN will be emitted).
	 * 
	 * @param code code of key to press
	 * @throws IOException on error
	 */
	public void pressKey(EventCode code) throws IOException {
		emit(new Event(code, 1));
	}

	/**
	 * Convenience method to press a key (SYN will be emitted).
	 * 
	 * @param code code of key to release
	 * @throws IOException on error
	 */
	public void releaseKey(int code) throws IOException {
		emit(new Event(EventCode.fromCode(Ev.EV_KEY, code), 0));
	}

	/**
	 * Convenience method to press a key (SYN will be emitted).
	 * 
	 * @param code code of key to release
	 * @throws IOException on error
	 */
	public void releaseKey(EventCode code) throws IOException {
		emit(new Event(code, 0));
	}

	/**
	 * Convenience method to type a key. The key will be pressed and released, with
	 * SYN after each state, with a 1 millisecond delay.
	 * 
	 * @param code code of key to type
	 * @throws IOException on error
	 */
	public void typeKey(EventCode code) throws IOException {
		typeKey(code, 1);
	}

	/**
	 * Convenience method to type a a sequence of keys. Each key will be pressed and released, with
	 * SYN after each state, with a 1 millisecond delay.
	 * 
	 * @param codes codes of key to type
	 * @throws IOException on error
	 */
	public void typeKeys(EventCode... codes) throws IOException {
		typeKeys(1, codes);
	}

	/**
	 * Convenience method to type a key. The key will be pressed and released, with
	 * SYN after each state, with a 1 millisecond delay.
	 * 
	 * @param code code of key to type
	 * @throws IOException on error
	 */
	public void typeKey(int code) throws IOException {
		typeKey(code, 1);
	}

	/**
	 * Convenience method to type a key. The key will be pressed and released, with
	 * SYN after each state, with an optional delay.
	 * 
	 * @param code  code of key to type
	 * @param delay delay in milliseconds
	 * @throws IOException on error
	 */
	public void typeKey(int code, long delay) throws IOException {
		pressKey(code);
		if (delay > 0)
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// If interrupted, don't leave the key pressed if at all possible
			}
		releaseKey(code);
	}

	/**
	 * Convenience method to type a key. The key will be pressed and released, with
	 * SYN after each state, with an optional delay.
	 * 
	 * @param code  code of key to type
	 * @param delay delay in milliseconds
	 * @throws IOException on error
	 */
	public void typeKey(EventCode code, long delay) throws IOException {
		pressKey(code);
		if (delay > 0)
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// If interrupted, don't leave the key pressed if at all possible
			}
		releaseKey(code);
	}

	/**
	 * Convenience method to type a sequence of  keys. Each key will be pressed and released, with
	 * SYN after each state, with an optional delay.
	 * 
	 * @param delay delay in milliseconds
	 * @param codes codes of keys to type
	 * @throws IOException on error
	 */
	public void typeKeys(long delay, EventCode... codes) throws IOException {
		int x = 0;
		for (EventCode code : codes) {
			if(x > 0) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					return;
				}
			}
			pressKey(code);
			if (delay > 0)
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					// If interrupted, don't leave the key pressed if at all possible
				}
			releaseKey(code);
			x++;
		}
	}

	/**
	 * Emit an event followed by a SYN event.
	 * 
	 * @param evt event
	 * @throws IOException on error
	 */
	public void emit(Event evt) throws IOException {
		emit(evt, true);
	}

	/**
	 * Emit an event.
	 * 
	 * @param evt event
	 * @param syn send SYN as well
	 * @throws IOException on error
	 */
	public void emit(Event evt, boolean syn) throws IOException {
		Input.input_event ie = new Input.input_event();
		ie.code = (short) evt.getCode().code();
		ie.type = (short) evt.getCode().typeCode();
		ie.value = evt.value;
		ie.time.tv_sec = new NativeLong(0);
		ie.time.tv_usec = new NativeLong(0);

		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Sending event " + evt);

		if (CLib.INSTANCE.write(fd, ie, new NativeLong(ie.size())).longValue() == -1)
			throw new IOException(String.format("Failed to emit to %s.", name));

		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Sent event " + evt);

		if (syn) {
			syn();
		}
	}

	/**
	 * Send a SYNC.
	 * 
	 * @throws IOException on error
	 */
	public void syn() throws IOException {
		emit(SYN, false);
	}

	/**
	 * Read the next event, blocking if there are none. <code>null</code> will be
	 * returned the device closes.
	 *
	 * @return next input event or <code>null</code>
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Event nextEvent() throws IOException {
		if (!read) {
			throw new IllegalStateException("This device is not a reading.");
		}
		Input.input_event ev = new Input.input_event();
		int size = ev.size();
		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Waiting for event (" + size + " bytes)");
		Pointer pointer = ev.getPointer();
		NativeLong read = CLib.INSTANCE.read(fd, pointer, new NativeLong(size));
		// ev.read();
		if (read.longValue() < 1) {
			throw new EOFException();
		} else if (read.longValue() < size) {
			throw new RuntimeException(
					"Error reading input event (read only " + read.longValue() + " of " + size + ").");
		}
		ev.read();
		ev.time.read();
		try {
			return new Event(ev);
		}
		catch(IllegalArgumentException iae) {
			throw new IllegalArgumentException(String.format("Unknown event code %d for type %d (value %d)", ev.code, ev.type, ev.value));
		}
	}

	/**
	 * Close.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void close() throws IOException {
		if (!open) {
			throw new IllegalArgumentException("Not open.");
		}
		try {
			if (read) {
				if (grabbed) {
					ungrab();
				}
			} else {
				CLib.INSTANCE.ioctl(fd, UInput.UI_DEV_DESTROY);
			}
			if (LOG.isLoggable(Level.DEBUG))
				LOG.log(Level.DEBUG, "Closing device " + file);
			CLib.INSTANCE.close(fd);
			if (LOG.isLoggable(Level.DEBUG))
				LOG.log(Level.DEBUG, "Closed device " + file);
		} finally {
			open = false;
		}
	}

	// Macros

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "InputDevice [file=" + file + ", fd=" + fd + ", grabbed=" + grabbed + ", name=" + name
				+ ", inputDriverVersion=" + inputDriverVersion + ", open=" + open + ", caps=" + caps + ", pollFd="
				+ pollFd + ", pollFds=" + Arrays.toString(pollFds) + ", read=" + read + ", vendor=" + vendor
				+ ", product=" + product + ", bus=" + bus + ", version=" + version + "]";
	}

	private final static int BITS_PER_LONG() {
		return NativeLong.SIZE * 8;
	}

	private final static int LONG(int x) {
		return (x / BITS_PER_LONG());
	}

	private final static int OFF(int x) {
		return x % BITS_PER_LONG();
	}

	private final static boolean test_bit(int bit, NativeLong[] array) {
		return ((array[LONG(bit)].longValue() >> OFF(bit)) & 1) > 0;
	}

	private final static int NBITS(int x) {
		return ((((x) - 1) / BITS_PER_LONG()) + 1);
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public final static void main(String[] args) throws Exception {
		// for (InputDevice d : getAvailableDevices()) {
		// LOG.info(d);
		// }
		LOG.log(Level.INFO, "Mouse: " + InputDevice.getFirstPointerDevice());
		LOG.log(Level.INFO, "Keyboard: " + InputDevice.getFirstKeyboardDevice());
	}

	/**
	 * Checks if is open.
	 *
	 * @return true, if is open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Gets the fd.
	 *
	 * @return the fd
	 */
	public int getFD() {
		return fd;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Gets the bus.
	 *
	 * @return the bus
	 */
	public int getBus() {
		return bus;
	}

	/**
	 * Gets the vendor.
	 *
	 * @return the vendor
	 */
	public int getVendor() {
		return vendor;
	}

	/**
	 * Gets the product.
	 *
	 * @return the product
	 */
	public int getProduct() {
		return product;
	}

	/**
	 * Sets the vendor.
	 *
	 * @param vendor the new vendor
	 */
	public void setVendor(int vendor) {
		this.vendor = vendor;
	}

	/**
	 * Sets the product.
	 *
	 * @param product the new product
	 */
	public void setProduct(int product) {
		this.product = product;
	}

	/**
	 * Sets the bus.
	 *
	 * @param bus the new bus
	 */
	public void setBus(int bus) {
		this.bus = bus;
	}

	/**
	 * Sets the version.
	 *
	 * @param version the new version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * Gets the supported types.
	 *
	 * @return the supported types
	 */
	public Set<EventCode.Type> getSupportedTypes() {
		Set<EventCode.Type> l = new LinkedHashSet<>();
		for (EventCode code : getCapabilities()) {
			l.add(code.type());
		}
		return l;
	}
}
