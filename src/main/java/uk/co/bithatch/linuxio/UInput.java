package uk.co.bithatch.linuxio;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

import uk.co.bithatch.linuxio.Input.ff_effect;
import uk.co.bithatch.linuxio.Input.input_absinfo;
import uk.co.bithatch.linuxio.Input.input_id;

public class UInput {

	public final static int UINPUT_VERSION = 5;
	public final static int UINPUT_MAX_NAME_SIZE = 80;

	public static class uinput_ff_upload extends Structure {
		public int request_id;
		public int retval;
		public ff_effect effect;
		public ff_effect old;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("request_id", "retval", "effect", "old");
		}
	}

	public static class uinput_ff_erase extends Structure {
		public int request_id;
		public int retval;
		public int effect_id;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("request_id", "retval", "effect_id");
		}
	}

	/* ioctl */
	public final static byte UINPUT_IOCTL_BASE = 'U';
	public final static int UI_DEV_CREATE = Ioctl.INSTANCE.IO(UINPUT_IOCTL_BASE, 1);
	public final static int UI_DEV_DESTROY = Ioctl.INSTANCE.IO(UINPUT_IOCTL_BASE, 2);

	public static class uinput_setup extends Structure {
		public input_id id = new input_id();
		public byte[] name = new byte[UINPUT_MAX_NAME_SIZE];
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

	public final static int UI_SET_EVBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 100, int.class);
	public final static int UI_SET_KEYBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 101, int.class);
	public final static int UI_SET_RELBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 102, int.class);
	public final static int UI_SET_ABSBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 103, int.class);
	public final static int UI_SET_MSCBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 104, int.class);
	public final static int UI_SET_LEDBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 105, int.class);
	public final static int UI_SET_SNDBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 106, int.class);
	public final static int UI_SET_FFBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 107, int.class);
	public final static int UI_SET_PHYS = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 108, int.class);
	public final static int UI_SET_SWBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 109, int.class);
	public final static int UI_SET_PROPBIT = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 110, int.class);
	public final static int UI_BEGIN_FF_UPLOAD = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 200, uinput_ff_upload.class);
	public final static int UI_END_FF_UPLOAD = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 201, uinput_ff_upload.class);
	public final static int UI_BEGIN_FF_ERASE = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 202, uinput_ff_erase.class);
	public final static int UI_END_FF_ERASE = Ioctl.INSTANCE.IOW(UINPUT_IOCTL_BASE, 203, uinput_ff_erase.class);

	/**
	 * UI_GET_SYSNAME - get the sysfs name of the created uinput device
	 *
	 * @return the sysfs name of the created virtual input device. The complete
	 *         sysfs path is then /sys/devices/virtual/input/--NAME-- Usually, it is
	 *         in the form "inputN"
	 */
	public final static int UI_GET_SYSNAME(int len) {
		return Ioctl.INSTANCE.IOC("R", UINPUT_IOCTL_BASE, 44, len);
	}

	public final static int UI_GET_VERSION	= Ioctl.INSTANCE.IOR(UINPUT_IOCTL_BASE, 45, int.class);

	/*
	 * IDs.
	 */

	public final static int ID_BUS = 0;
	public final static int ID_VENDOR = 1;
	public final static int ID_PRODUCT = 2;
	public final static int ID_VERSION = 3;

	public final static int BUS_PCI = 0x01;
	public final static int BUS_ISAPNP = 0x02;
	public final static int BUS_USB = 0x03;
	public final static int BUS_HIL = 0x04;
	public final static int BUS_BLUETOOTH = 0x05;
	public final static int BUS_VIRTUAL = 0x06;

	public final static int BUS_ISA = 0x10;
	public final static int BUS_I8042 = 0x11;
	public final static int BUS_XTKBD = 0x12;
	public final static int BUS_RS232 = 0x13;
	public final static int BUS_GAMEPORT = 0x14;
	public final static int BUS_PARPORT = 0x15;
	public final static int BUS_AMIGA = 0x16;
	public final static int BUS_ADB = 0x17;
	public final static int BUS_I2C = 0x18;
	public final static int BUS_HOST = 0x19;
	public final static int BUS_GSC = 0x1A;
	public final static int BUS_ATARI = 0x1B;
	public final static int BUS_SPI = 0x1C;
	public final static int BUS_RMI = 0x1D;
	public final static int BUS_CEC = 0x1E;
	public final static int BUS_INTEL_ISHTP = 0x1F;

	/*
	 * MT_TOOL types
	 */
	public final static int MT_TOOL_FINGER = 0x00;
	public final static int MT_TOOL_PEN = 0x01;
	public final static int MT_TOOL_PALM = 0x02;
	public final static int MT_TOOL_DIAL = 0x0a;
	public final static int MT_TOOL_MAX = 0x0f;

	/*
	 * Values describing the status of a force-feedback effect
	 */
	public final static int FF_STATUS_STOPPED = 0x00;
	public final static int FF_STATUS_PLAYING = 0x01;
	public final static int FF_STATUS_MAX = 0x01;
}
