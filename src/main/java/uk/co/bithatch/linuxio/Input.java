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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.Union;

import uk.co.bithatch.linuxio.CLib.timeval;

// TODO: Auto-generated Javadoc
/**
 * The Class Input.
 */
public class Input {

	/**
	 * The Class input_event.
	 */
	@FieldOrder({ "time", "type", "code", "value" })
	public static class input_event extends Structure {
		
		/**  C type : timeval. */
		public timeval time;
		
		/**  C type : __u16. */
		public short type;
		
		/**  C type : __u16. */
		public short code;
		
		/**  C type : __s32. */
		public int value;

		/**
		 * Instantiates a new input event.
		 */
		public input_event() {
			super();
		}

		/**
		 * Instantiates a new input event.
		 *
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

		/**
		 * Instantiates a new input event.
		 *
		 * @param peer the peer
		 */
		public input_event(Pointer peer) {
			super(peer);
		}

		/**
		 * The Class ByReference.
		 */
		public static class ByReference extends input_event implements Structure.ByReference {

		};

		/**
		 * The Class ByValue.
		 */
		public static class ByValue extends input_event implements Structure.ByValue {

		};
	}
	
	/**
	 * The Class input_id.
	 */
	public static class input_id extends Structure {
		
		/** The bustype. */
		public short bustype;
		
		/** The vendor. */
		public short vendor;
		
		/** The product. */
		public short product;
		
		/** The version. */
		public short version;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("bustype", "vendor", "product", "version");
		}
	}

	/**
	 * The Class input_absinfo.
	 */
	public static class input_absinfo extends Structure {
		
		/** The value. */
		public int value;
		
		/** The minimum. */
		public int minimum;
		
		/** The maximum. */
		public int maximum;
		
		/** The fuzz. */
		public int fuzz;
		
		/** The flat. */
		public int flat;
		
		/** The resolution. */
		public int resolution;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("value", "minimum", "maximum", "fuzz", "flat", "resolution");
		}
	};

	/**
	 * The Class input_keymap_entry.
	 */
	public static class input_keymap_entry extends Structure {
		
		/** The flags. */
		public byte flags;
		
		/** The len. */
		public byte len;
		
		/** The index. */
		public short index;
		
		/** The keycode. */
		public int keycode;
		
		/** The scancode. */
		public byte[] scancode = new byte[32];

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("flags", "len", "index", "keycode", "scancode");
		}
	};

	/**
	 * The Class ff_rumble_effect.
	 */
	public static class ff_rumble_effect extends Structure {
		
		/** The strong magnitude. */
		public short strong_magnitude;
		
		/** The weak magnitude. */
		public short weak_magnitude;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("strong_magnitude", "weak_magnitude");
		}
	}

	/**
	 * The Class ff_condition_effect.
	 */
	public static class ff_condition_effect extends Structure {
		
		/** The right saturation. */
		public short right_saturation;
		
		/** The left saturation. */
		public short left_saturation;
		
		/** The right coeff. */
		public short right_coeff;
		
		/** The left coeff. */
		public short left_coeff;
		
		/** The deadband. */
		public short deadband;
		
		/** The center. */
		public short center;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("right_saturation", "left_saturation", "right_coeff", "left_coeff", "deadband",
					"center");
		}
	}

	/**
	 * The Class ff_periodic_effect.
	 */
	public static class ff_periodic_effect extends Structure {
		
		/** The waveform. */
		public short waveform;
		
		/** The period. */
		public short period;
		
		/** The magnitude. */
		public short magnitude;
		
		/** The offset. */
		public short offset;
		
		/** The phase. */
		public short phase;
		
		/** The envelope. */
		public ff_envelope envelope;
		
		/** The custom len. */
		public int custom_len;
		
		/** The custom data. */
		public byte[] custom_data;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("waveform", "period", "magnitude", "offset", "phase", "envelope", "custom_len",
					"custom_data");
		}
	}

	/**
	 * The Class ff_ramp_effect.
	 */
	public static class ff_ramp_effect extends Structure {
		
		/** The start level. */
		public short start_level;
		
		/** The end level. */
		public short end_level;
		
		/** The envelope. */
		public ff_envelope envelope;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("start_level", "end_level", "envelope");
		}
	}

	/**
	 * The Class ff_envelope.
	 */
	public static class ff_envelope extends Structure {
		
		/** The attack length. */
		public short attack_length;
		
		/** The attack level. */
		public short attack_level;
		
		/** The fade length. */
		public short fade_length;
		
		/** The fade level. */
		public short fade_level;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("attack_length", "attack_level", "fade_length", "fade_level");
		}
	}

	/**
	 * The Class ff_constant_effect.
	 */
	public static class ff_constant_effect extends Structure {
		
		/** The level. */
		public short level;
		
		/** The envelope. */
		public ff_envelope envelope;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("level", "envelope");
		}
	}

	/**
	 * The Class ff_trigger.
	 */
	public static class ff_trigger extends Structure {
		
		/** The button. */
		public short button;
		
		/** The interval. */
		public short interval;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("button", "interval");
		}
	}

	/**
	 * The Class ff_replay.
	 */
	public static class ff_replay extends Structure {
		
		/** The length. */
		public short length;
		
		/** The delay. */
		public short delay;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("length", "delay");
		}
	}

	/**
	 * The Class ff_effect.
	 */
	public static class ff_effect extends Structure {

		/**
		 * The Class ff_effect_union.
		 */
		public static class ff_effect_union extends Union {
			
			/** The constant. */
			public ff_constant_effect constant;
			
			/** The ramp. */
			public ff_ramp_effect ramp;
			
			/** The periodic. */
			public ff_periodic_effect periodic;
			
			/** The condition. */
			public ff_condition_effect[] condition = new ff_condition_effect[2];
			
			/** The rumble. */
			public ff_rumble_effect rumble;

			@Override
			protected List<String> getFieldOrder() {
				return Arrays.asList("constant", "ramp", "periodic", "condition", "rumble");
			}
		}

		/** The type. */
		public short type;
		
		/** The id. */
		public short id;
		
		/** The direction. */
		public short direction;
		
		/** The trigger. */
		public ff_trigger trigger;
		
		/** The replay. */
		public ff_replay replay;
		
		/** The ffu 1. */
		public ff_effect_union ffu1 = new ff_effect_union();

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("type", "id", "direction", "trigger", "replay");
		}
	}

	/**
	 * The Class Macros.
	 */
	public static class Macros {

		/** The Constant EVIOCGVERSION. */
		public final static int EVIOCGVERSION = Ioctl.INSTANCE.IOR('E', 0x01, int.class); /* get driver version */
		
		/** The Constant EVIOCGID. */
		public final static int EVIOCGID = Ioctl.INSTANCE.IOR('E', 0x02, input_id.class); /* get device ID */
		
		/** The Constant EVIOCGREP. */
		public final static int EVIOCGREP = Ioctl.INSTANCE.IOR('E', 0x03, new int[2]); /* get repeat settings */
		
		/** The Constant EVIOCSREP. */
		public final static int EVIOCSREP = Ioctl.INSTANCE.IOW('E', 0x03, new int[2]); /* set repeat settings */

		/** The Constant EVIOCGKEYCODE. */
		public final static int EVIOCGKEYCODE = Ioctl.INSTANCE.IOR('E', 0x04, new int[2]); /* get keycode */
		
		/** The Constant EVIOCGKEYCODE_V2. */
		public final static int EVIOCGKEYCODE_V2 = Ioctl.INSTANCE.IOR('E', 0x04, new input_keymap_entry());
		
		/** The Constant EVIOCSKEYCODE. */
		public final static int EVIOCSKEYCODE = Ioctl.INSTANCE.IOW('E', 0x04, new int[2]); /* set keycode */

		/** The Constant EVIOCSKEYCODE_V2. */
		public final static int EVIOCSKEYCODE_V2 = Ioctl.INSTANCE.IOW('E', 0x04, new input_keymap_entry());

		/**
		 * Eviocgname.
		 *
		 * @param len the len
		 * @return the int
		 */
		public static int EVIOCGNAME(int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x06, len); /* get device name */
		}

		/**
		 * Eviocgphys.
		 *
		 * @param len the len
		 * @return the int
		 */
		public static int EVIOCGPHYS(int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x07, len); /* get physical location */
		}

		/**
		 * Eviocguniq.
		 *
		 * @param len the len
		 * @return the int
		 */
		public static int EVIOCGUNIQ(int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x08, len); /* get unique identifier */
		}

		/**
		 * Eviocgprop.
		 *
		 * @param len the len
		 * @return the int
		 */
		public static int EVIOCGPROP(int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x09, len); /* get device properties */
		}	
		
		/**
		 * Eviocgbit.
		 *
		 * @param ev the ev
		 * @param len the len
		 * @return the int
		 */
		public static int EVIOCGBIT(int ev, int len) {
			return Ioctl.INSTANCE.IOC("R", 'E', 0x20 + ev, len); /* get event bits */
		}
		
		/**
		 * Eviocgabs.
		 *
		 * @param abs the abs
		 * @return the int
		 */
		public static int EVIOCGABS(int abs) {
			return Ioctl.INSTANCE.IOR("E", 0x40 + abs, new input_absinfo());  /* get abs value/limits */
		}
		
		/**
		 * Eviocsabs.
		 *
		 * @param abs the abs
		 * @return the int
		 */
		public static int EVIOCSABS(int abs) {
			return Ioctl.INSTANCE.IOW("E", 0xc0 + abs, new input_absinfo());  /* set abs value/limits */
		}
	}
}
