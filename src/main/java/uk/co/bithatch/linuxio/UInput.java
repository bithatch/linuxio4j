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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

import uk.co.bithatch.linuxio.Input.ff_effect;
import uk.co.bithatch.linuxio.Input.input_absinfo;
import uk.co.bithatch.linuxio.Input.input_id;

/**
 * The Class UInput.
 */
public class UInput {

	/** The Constant UINPUT_VERSION. */
	public final static int UINPUT_VERSION = 5;
	
	/** The Constant UINPUT_MAX_NAME_SIZE. */
	public final static int UINPUT_MAX_NAME_SIZE = 80;

	/**
	 * The Class uinput_ff_upload.
	 */
	public static class uinput_ff_upload extends Structure {
		
		/** The request id. */
		public int request_id;
		
		/** The retval. */
		public int retval;
		
		/** The effect. */
		public ff_effect effect;
		
		/** The old. */
		public ff_effect old;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("request_id", "retval", "effect", "old");
		}
	}

	/**
	 * The Class uinput_ff_erase.
	 */
	public static class uinput_ff_erase extends Structure {
		
		/** The request id. */
		public int request_id;
		
		/** The retval. */
		public int retval;
		
		/** The effect id. */
		public int effect_id;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("request_id", "retval", "effect_id");
		}
	}

	/** The Constant UINPUT_IOCTL_BASE. */
	/* ioctl */
	public final static byte UINPUT_IOCTL_BASE = 'U';
	
	/** The Constant UI_DEV_CREATE. */
	public final static int UI_DEV_CREATE = Ioctl.INSTANCE.IO(UINPUT_IOCTL_BASE, 1);
	
	/** The Constant UI_DEV_DESTROY. */
	public final static int UI_DEV_DESTROY = Ioctl.INSTANCE.IO(UINPUT_IOCTL_BASE, 2);

	/**
	 * The Class uinput_setup.
	 */
	public static class uinput_setup extends Structure {
		
		/** The id. */
		public input_id id = new input_id();
		
		/** The name. */
		public byte[] name = new byte[UINPUT_MAX_NAME_SIZE];
		
		/** The ff effects max. */
		public int ff_effects_max;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("id", "name", "ff_effects_max");
		}
	}

	/**
	 * UI_DEV_SETUP - Set device parameters for setup
	 *
	 * This ioctl sets parameters for the input device to be created. It supersedes
	 * the old "struct uinput_user_dev" method, which wrote this data via write().
	 * To actually set the absolute axes UI_ABS_SETUP should be used.
	 *
	 * The ioctl takes a "struct uinput_setup" object as argument. The fields of
	 * this object are as follows: id: See the description of "struct input_id".
	 * This field is copied unchanged into the new device. name: This is used
	 * unchanged as name for the new device. ff_effects_max: This limits the maximum
	 * numbers of force-feedback effects. See below for a description of FF with
	 * uinput.
	 *
	 * This ioctl can be called multiple times and will overwrite previous values.
	 * If this ioctl fails with -EINVAL, it is recommended to use the old
	 * "uinput_user_dev" method via write() as a fallback, in case you run on an old
	 * kernel that does not support this ioctl.
	 *
	 * This ioctl may fail with -EINVAL if it is not supported or if you passed
	 * incorrect values, -ENOMEM if the kernel runs out of memory or -EFAULT if the
	 * passed uinput_setup object cannot be read/written. If this call fails,
	 * partial data may have already been applied to the internal device.
	 */
	public final static int UI_DEV_SETUP = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 3, new uinput_setup());

	static class uinput_abs_setup extends Structure {
		public short code;
		public input_absinfo absinfo;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("code", "absinfo");
		}
	}

	/**
	 * UI_ABS_SETUP - Set absolute axis information for the device to setup
	 *
	 * This ioctl sets one absolute axis information for the input device to be
	 * created. It supersedes the old "struct uinput_user_dev" method, which wrote
	 * part of this data and the content of UI_DEV_SETUP via write().
	 *
	 * The ioctl takes a "struct uinput_abs_setup" object as argument. The fields of
	 * this object are as follows: code: The corresponding input code associated
	 * with this axis (ABS_X, ABS_Y, etc...) absinfo: See "struct input_absinfo" for
	 * a description of this field. This field is copied unchanged into the kernel
	 * for the specified axis. If the axis is not enabled via UI_SET_ABSBIT, this
	 * ioctl will enable it.
	 *
	 * This ioctl can be called multiple times and will overwrite previous values.
	 * If this ioctl fails with -EINVAL, it is recommended to use the old
	 * "uinput_user_dev" method via write() as a fallback, in case you run on an old
	 * kernel that does not support this ioctl.
	 *
	 * This ioctl may fail with -EINVAL if it is not supported or if you passed
	 * incorrect values, -ENOMEM if the kernel runs out of memory or -EFAULT if the
	 * passed uinput_setup object cannot be read/written. If this call fails,
	 * partial data may have already been applied to the internal device.
	 */
	public final static int UI_ABS_SETUP = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 4, uinput_abs_setup.class);

	/** The Constant UI_SET_EVBIT. */
	public final static int UI_SET_EVBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 100, int.class);
	
	/** The Constant UI_SET_KEYBIT. */
	public final static int UI_SET_KEYBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 101, int.class);
	
	/** The Constant UI_SET_RELBIT. */
	public final static int UI_SET_RELBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 102, int.class);
	
	/** The Constant UI_SET_ABSBIT. */
	public final static int UI_SET_ABSBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 103, int.class);
	
	/** The Constant UI_SET_MSCBIT. */
	public final static int UI_SET_MSCBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 104, int.class);
	
	/** The Constant UI_SET_LEDBIT. */
	public final static int UI_SET_LEDBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 105, int.class);
	
	/** The Constant UI_SET_SNDBIT. */
	public final static int UI_SET_SNDBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 106, int.class);
	
	/** The Constant UI_SET_FFBIT. */
	public final static int UI_SET_FFBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 107, int.class);
	
	/** The Constant UI_SET_PHYS. */
	public final static int UI_SET_PHYS = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 108, int.class);
	
	/** The Constant UI_SET_SWBIT. */
	public final static int UI_SET_SWBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 109, int.class);
	
	/** The Constant UI_SET_PROPBIT. */
	public final static int UI_SET_PROPBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 110, int.class);
	
	/** The Constant UI_BEGIN_FF_UPLOAD. */
	public final static int UI_BEGIN_FF_UPLOAD = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 200, uinput_ff_upload.class);
	
	/** The Constant UI_END_FF_UPLOAD. */
	public final static int UI_END_FF_UPLOAD = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 201, uinput_ff_upload.class);
	
	/** The Constant UI_BEGIN_FF_ERASE. */
	public final static int UI_BEGIN_FF_ERASE = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 202, uinput_ff_erase.class);
	
	/** The Constant UI_END_FF_ERASE. */
	public final static int UI_END_FF_ERASE = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 203, uinput_ff_erase.class);

	/**
	 * UI_GET_SYSNAME - get the sysfs name of the created uinput device.
	 *
	 * @param len the len
	 * @return the sysfs name of the created virtual input device. The complete
	 *         sysfs path is then /sys/devices/virtual/input/--NAME-- Usually, it is
	 *         in the form "inputN"
	 */
	public final static int UI_GET_SYSNAME(int len) {
		return Ioctl.INSTANCE.IOC("R", UINPUT_IOCTL_BASE, 44, len);
	}

	/** The Constant UI_GET_VERSION. */
	public final static int UI_GET_VERSION	= Ioctl.INSTANCE.IOR(UINPUT_IOCTL_BASE, 45, int.class);

	/*
	 * IDs.
	 */

	/** The Constant ID_BUS. */
	public final static int ID_BUS = 0;
	
	/** The Constant ID_VENDOR. */
	public final static int ID_VENDOR = 1;
	
	/** The Constant ID_PRODUCT. */
	public final static int ID_PRODUCT = 2;
	
	/** The Constant ID_VERSION. */
	public final static int ID_VERSION = 3;

	/** The Constant BUS_PCI. */
	public final static int BUS_PCI = 0x01;
	
	/** The Constant BUS_ISAPNP. */
	public final static int BUS_ISAPNP = 0x02;
	
	/** The Constant BUS_USB. */
	public final static int BUS_USB = 0x03;
	
	/** The Constant BUS_HIL. */
	public final static int BUS_HIL = 0x04;
	
	/** The Constant BUS_BLUETOOTH. */
	public final static int BUS_BLUETOOTH = 0x05;
	
	/** The Constant BUS_VIRTUAL. */
	public final static int BUS_VIRTUAL = 0x06;

	/** The Constant BUS_ISA. */
	public final static int BUS_ISA = 0x10;
	
	/** The Constant BUS_I8042. */
	public final static int BUS_I8042 = 0x11;
	
	/** The Constant BUS_XTKBD. */
	public final static int BUS_XTKBD = 0x12;
	
	/** The Constant BUS_RS232. */
	public final static int BUS_RS232 = 0x13;
	
	/** The Constant BUS_GAMEPORT. */
	public final static int BUS_GAMEPORT = 0x14;
	
	/** The Constant BUS_PARPORT. */
	public final static int BUS_PARPORT = 0x15;
	
	/** The Constant BUS_AMIGA. */
	public final static int BUS_AMIGA = 0x16;
	
	/** The Constant BUS_ADB. */
	public final static int BUS_ADB = 0x17;
	
	/** The Constant BUS_I2C. */
	public final static int BUS_I2C = 0x18;
	
	/** The Constant BUS_HOST. */
	public final static int BUS_HOST = 0x19;
	
	/** The Constant BUS_GSC. */
	public final static int BUS_GSC = 0x1A;
	
	/** The Constant BUS_ATARI. */
	public final static int BUS_ATARI = 0x1B;
	
	/** The Constant BUS_SPI. */
	public final static int BUS_SPI = 0x1C;
	
	/** The Constant BUS_RMI. */
	public final static int BUS_RMI = 0x1D;
	
	/** The Constant BUS_CEC. */
	public final static int BUS_CEC = 0x1E;
	
	/** The Constant BUS_INTEL_ISHTP. */
	public final static int BUS_INTEL_ISHTP = 0x1F;

	/** The Constant MT_TOOL_FINGER. */
	/*
	 * MT_TOOL types
	 */
	public final static int MT_TOOL_FINGER = 0x00;
	
	/** The Constant MT_TOOL_PEN. */
	public final static int MT_TOOL_PEN = 0x01;
	
	/** The Constant MT_TOOL_PALM. */
	public final static int MT_TOOL_PALM = 0x02;
	
	/** The Constant MT_TOOL_DIAL. */
	public final static int MT_TOOL_DIAL = 0x0a;
	
	/** The Constant MT_TOOL_MAX. */
	public final static int MT_TOOL_MAX = 0x0f;

	/** The Constant FF_STATUS_STOPPED. */
	/*
	 * Values describing the status of a force-feedback effect
	 */
	public final static int FF_STATUS_STOPPED = 0x00;
	
	/** The Constant FF_STATUS_PLAYING. */
	public final static int FF_STATUS_PLAYING = 0x01;
	
	/** The Constant FF_STATUS_MAX. */
	public final static int FF_STATUS_MAX = 0x01;
}
