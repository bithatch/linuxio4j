package uk.co.bithatch.linuxio;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;

import uk.co.bithatch.linuxio.CLib.timeval;

public class Input {

	@FieldOrder({ "time", "type", "code", "value" })
	public static class input_event extends Structure {
		/** C type : timeval */
		public timeval time;
		/** C type : __u16 */
		public short type;
		/** C type : __u16 */
		public short code;
		/** C type : __s32 */
		public int value;

		public input_event() {
			super();
		}

		/**
		 * @param time  C type : timeval<br>
		 * @param type  C type : __u16<br>
		 * @param code  C type : __u16<br>
		 * @param value C type : __s32
		 */
		public input_event(timeval time, short type, short code, int value) {
			super();
			this.time = time;
			this.type = type;
			this.code = code;
			this.value = value;
		}

		public input_event(Pointer peer) {
			super(peer);
		}

		public static class ByReference extends input_event implements Structure.ByReference {

		};

		public static class ByValue extends input_event implements Structure.ByValue {

		};
	}
	
	public static class input_id extends Structure {
		public short bustype;
		public short vendor;
		public short product;
		public short version;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("bustype", "vendor", "product", "version");
		}
	}

	public static class input_absinfo extends Structure {
		public int value;
		public int minimum;
		public int maximum;
		public int fuzz;
		public int flat;
		public int resolution;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("value", "minimum", "maximum", "fuzz", "flat", "resolution");
		}
	};

	public static class input_keymap_entry extends Structure {
		public byte flags;
		public byte len;
		public short index;
		public int keycode;
		public byte[] scancode = new byte[32];

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("flags", "len", "index", "keycode", "scancode");
		}
	};

	public static class ff_rumble_effect extends Structure {
		public short strong_magnitude;
		public short weak_magnitude;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("strong_magnitude", "weak_magnitude");
		}
	}

	public static class ff_condition_effect extends Structure {
		public short right_saturation;
		public short left_saturation;
		public short right_coeff;
		public short left_coeff;
		public short deadband;
		public short center;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("right_saturation", "left_saturation", "right_coeff", "left_coeff", "deadband",
					"center");
		}
	}

	public static class ff_periodic_effect extends Structure {
		public short waveform;
		public short period;
		public short magnitude;
		public short offset;
		public short phase;
		public ff_envelope envelope;
		public int custom_len;
		public byte[] custom_data;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("waveform", "period", "magnitude", "offset", "phase", "envelope", "custom_len",
					"custom_data");
		}
	}

	public static class ff_ramp_effect extends Structure {
		public short start_level;
		public short end_level;
		public ff_envelope envelope;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("start_level", "end_level", "envelope");
		}
	}

	public static class ff_envelope extends Structure {
		public short attack_length;
		public short attack_level;
		public short fade_length;
		public short fade_level;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("attack_length", "attack_level", "fade_length", "fade_level");
		}
	}

	public static class ff_constant_effect extends Structure {
		public short level;
		public ff_envelope envelope;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("level", "envelope");
		}
	}

	public static class ff_trigger extends Structure {
		public short button;
		public short interval;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("button", "interval");
		}
	}

	public static class ff_replay extends Structure {
		public short length;
		public short delay;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("length", "delay");
		}
	}

	public static class ff_effect extends Structure {

		public static class ff_effect_union extends Union {
			public ff_constant_effect constant;
			public ff_ramp_effect ramp;
			public ff_periodic_effect periodic;
			public ff_condition_effect[] condition = new ff_condition_effect[2];
			public ff_rumble_effect rumble;

			@Override
			protected List<String> getFieldOrder() {
				return Arrays.asList("constant", "ramp", "periodic", "condition", "rumble");
			}
		}

		public short type;
		public short id;
		public short direction;
		public ff_trigger trigger;
		public ff_replay replay;
		public ff_effect_union ffu1 = new ff_effect_union();

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("type", "id", "direction", "trigger", "replay");
		}
	}

	public static class Macros {

		public final static int EVIOCGVERSION = Ioctl.INSTANCE.IOR('E', 0x01, int.class); /* get driver version */
		public final static int EVIOCGID = Ioctl.INSTANCE.IOR('E', 0x02, input_id.class); /* get device ID */
		public final static int EVIOCGREP = Ioctl.INSTANCE.IOR('E', 0x03, new int[2]); /* get repeat settings */
		public final static int EVIOCSREP = Ioctl.INSTANCE.IOW('E', 0x03, new int[2]); /* set repeat settings */

		public final static int EVIOCGKEYCODE = Ioctl.INSTANCE.IOR('E', 0x04, new int[2]); /* get keycode */
		public final static int EVIOCGKEYCODE_V2 = Ioctl.INSTANCE.IOR('E', 0x04, new input_keymap_entry());
		public final static int EVIOCSKEYCODE = Ioctl.INSTANCE.IOW('E', 0x04, new int[2]); /* set keycode */

		public final static int EVIOCSKEYCODE_V2 = Ioctl.INSTANCE.IOW('E', 0x04, new input_keymap_entry());

		public static int EVIOCGNAME(int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x06, len); /* get device name */
		}

		public static int EVIOCGPHYS(int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x07, len); /* get physical location */
		}

		public static int EVIOCGUNIQ(int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x08, len); /* get unique identifier */
		}

		public static int EVIOCGPROP(int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x09, len); /* get device properties */
		}	
		
		public static int EVIOCGBIT(int ev, int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x20 + ev, len); /* get event bits */
		}
		
		public static int EVIOCGABS(int abs) {
			return Ioctl.INSTANCE.IOR("E", 0x40 + abs, input_absinfo.class);  /* get abs value/limits */
		}
		
		public static int EVIOCSABS(int abs) {
			return Ioctl.INSTANCE.IOR("E", 0x40 + abs, input_absinfo.class);  /* set abs value/limits */
		}
	}
}
