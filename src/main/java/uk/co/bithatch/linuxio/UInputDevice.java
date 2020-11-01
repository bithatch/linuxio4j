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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import uk.co.bithatch.linuxio.CLib.pollfd;
import uk.co.bithatch.linuxio.UInput.uinput_setup;
import uk.co.bithatch.linuxio.UInputDevice.Event;

/**
 * Provides access to the Linux linux user input system (uinput). All input
 * devices such as keyboards, mice, touch pads and others supported by the
 * kernel are accessible as special files in /dev/input.
 * <p>
 * To read capabilities and events from a device, construct a new
 * {@link UInputDevice}, passing it the filename of the device.
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
 * the {@link UInputDevice#UInputDevice(String, short, short)} constructor and
 * the {@link #emit(Event)} methods.
 * 
 */
public class UInputDevice implements Closeable {

	private static final String SYSPROP_LINUXIO_POINTER_TYPES = "linuxio.pointer.types";
	private static final String INPUT_DEVICES = "linuxio.input.devices";

	final static Logger LOG = System.getLogger(UInputDevice.class.getName());

	public enum Type {
		EV_SYN(InputEventCodes.EV_SYN), EV_REL(InputEventCodes.EV_REL), EV_MSC(InputEventCodes.EV_MSC),
		EV_SND(InputEventCodes.EV_SND), EV_FF(InputEventCodes.EV_FF), EV_FF_STATUS(InputEventCodes.EV_FF_STATUS),
		EV_KEY(InputEventCodes.EV_KEY), EV_ABS(InputEventCodes.EV_ABS), EV_LED(InputEventCodes.EV_LED),
		EV_REP(InputEventCodes.EV_REP), EV_PWT(InputEventCodes.EV_PWR), EV_SW(InputEventCodes.EV_SW), UNKNOWN(-1);

		private int nativeType;

		Type(int nativeType) {
			this.nativeType = nativeType;
		}

		public int getNativeType() {
			return nativeType;
		}

		public static Type fromNative(int type) {
			for (Type t : values()) {
				if (t.nativeType == type) {
					return t;
				}
			}
			return UNKNOWN;
		}
	}

	public static class Event {
		private long utime;
		private Type type;
		private short code;
		private int value;

		private Event(Input.input_event ev) {
			this(ev.time.tv_usec.longValue() * 1000, Type.fromNative(ev.type), ev.code, ev.value);
		}

		public Event(Type type, int code) {
			this(type, code, 0);
		}

		public Event(Type type, int code, int value) {
			this(0, type, code, value);
		}

		public Event(long utime, Type type, int code, int value) {
			super();
			this.utime = utime;
			this.type = type;
			this.code = (short) code;
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

		public Type getType() {
			return type;
		}

		public short getCode() {
			return code;
		}

		public int getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "Event [time=" + utime + ", type=" + type + ", code=" + code + ", value=" + value + "]";
		}

	}

	public final static Event SYN = new Event(Type.EV_SYN, InputEventCodes.SYN_REPORT, 0);

	public final static Map<Type, Map<Integer, String>> NAMES = new HashMap<Type, Map<Integer, String>>();
	public final static Map<Integer, String> KEYSANDBUTTONS = new HashMap<Integer, String>();
	public final static Map<Integer, String> KEYS = new HashMap<Integer, String>();
	public final static Map<Integer, String> BUTTONS = new HashMap<Integer, String>();
	public final static Map<Integer, String> SOUNDS = new HashMap<Integer, String>();
	public final static Map<Integer, String> REPEATS = new HashMap<Integer, String>();
	public final static Map<Integer, String> LEDS = new HashMap<Integer, String>();
	public final static Map<Integer, String> MISC = new HashMap<Integer, String>();
	public final static Map<Integer, String> SWITCHES = new HashMap<Integer, String>();
	public final static Map<Integer, String> ABSOLUTES = new HashMap<Integer, String>();
	public final static Map<Integer, String> RELATIVES = new HashMap<Integer, String>();
	public final static String[] ABSVAL = new String[] { "Value", "Min", "Max", "Fuzz", "Flat" };

	static {
		// I would have used reflection here on the KEY_, BUTTON_ etc constants,
		// but I guess it's possible this library may be obfuscated

		BUTTONS.put(InputEventCodes.BTN_0, "Btn0");
		BUTTONS.put(InputEventCodes.BTN_1, "Btn1");
		BUTTONS.put(InputEventCodes.BTN_2, "Btn2");
		BUTTONS.put(InputEventCodes.BTN_3, "Btn3");
		BUTTONS.put(InputEventCodes.BTN_4, "Btn4");
		BUTTONS.put(InputEventCodes.BTN_5, "Btn5");
		BUTTONS.put(InputEventCodes.BTN_6, "Btn6");
		BUTTONS.put(InputEventCodes.BTN_7, "Btn7");
		BUTTONS.put(InputEventCodes.BTN_8, "Btn8");
		BUTTONS.put(InputEventCodes.BTN_9, "Btn9");
		BUTTONS.put(InputEventCodes.BTN_LEFT, "LeftBtn");
		BUTTONS.put(InputEventCodes.BTN_RIGHT, "RightBtn");
		BUTTONS.put(InputEventCodes.BTN_MIDDLE, "MiddleBtn");
		BUTTONS.put(InputEventCodes.BTN_SIDE, "SideBtn");
		BUTTONS.put(InputEventCodes.BTN_EXTRA, "ExtraBtn");
		BUTTONS.put(InputEventCodes.BTN_FORWARD, "ForwardBtn");
		BUTTONS.put(InputEventCodes.BTN_BACK, "BackBtn");
		BUTTONS.put(InputEventCodes.BTN_TASK, "TaskBtn");
		BUTTONS.put(InputEventCodes.BTN_TRIGGER, "Trigger");
		BUTTONS.put(InputEventCodes.BTN_THUMB, "ThumbBtn");
		BUTTONS.put(InputEventCodes.BTN_THUMB2, "ThumbBtn2");
		BUTTONS.put(InputEventCodes.BTN_TOP, "TopBtn");
		BUTTONS.put(InputEventCodes.BTN_TOP2, "TopBtn2");
		BUTTONS.put(InputEventCodes.BTN_PINKIE, "PinkieBtn");
		BUTTONS.put(InputEventCodes.BTN_BASE, "BaseBtn");
		BUTTONS.put(InputEventCodes.BTN_BASE2, "BaseBtn2");
		BUTTONS.put(InputEventCodes.BTN_BASE3, "BaseBtn3");
		BUTTONS.put(InputEventCodes.BTN_BASE4, "BaseBtn4");
		BUTTONS.put(InputEventCodes.BTN_BASE5, "BaseBtn5");
		BUTTONS.put(InputEventCodes.BTN_BASE6, "BaseBtn6");
		BUTTONS.put(InputEventCodes.BTN_DEAD, "BtnDead");
		BUTTONS.put(InputEventCodes.BTN_A, "BtnA");
		BUTTONS.put(InputEventCodes.BTN_B, "BtnB");
		BUTTONS.put(InputEventCodes.BTN_C, "BtnC");
		BUTTONS.put(InputEventCodes.BTN_X, "BtnX");
		BUTTONS.put(InputEventCodes.BTN_Y, "BtnY");
		BUTTONS.put(InputEventCodes.BTN_Z, "BtnZ");
		BUTTONS.put(InputEventCodes.BTN_TL, "BtnTL");
		BUTTONS.put(InputEventCodes.BTN_TR, "BtnTR");
		BUTTONS.put(InputEventCodes.BTN_TL2, "BtnTL2");
		BUTTONS.put(InputEventCodes.BTN_TR2, "BtnTR2");
		BUTTONS.put(InputEventCodes.BTN_SELECT, "BtnSelect");
		BUTTONS.put(InputEventCodes.BTN_START, "BtnStart");
		BUTTONS.put(InputEventCodes.BTN_MODE, "BtnMode");
		BUTTONS.put(InputEventCodes.BTN_THUMBL, "BtnThumbL");
		BUTTONS.put(InputEventCodes.BTN_THUMBR, "BtnThumbR");
		BUTTONS.put(InputEventCodes.BTN_TOOL_PEN, "ToolPen");
		BUTTONS.put(InputEventCodes.BTN_TOOL_RUBBER, "ToolRubber");
		BUTTONS.put(InputEventCodes.BTN_TOOL_BRUSH, "ToolBrush");
		BUTTONS.put(InputEventCodes.BTN_TOOL_PENCIL, "ToolPencil");
		BUTTONS.put(InputEventCodes.BTN_TOOL_AIRBRUSH, "ToolAirbrush");
		BUTTONS.put(InputEventCodes.BTN_TOOL_FINGER, "ToolFinger");
		BUTTONS.put(InputEventCodes.BTN_TOOL_MOUSE, "ToolMouse");
		BUTTONS.put(InputEventCodes.BTN_TOOL_LENS, "ToolLens");
		BUTTONS.put(InputEventCodes.BTN_TOUCH, "Touch");
		BUTTONS.put(InputEventCodes.BTN_STYLUS, "Stylus");
		BUTTONS.put(InputEventCodes.BTN_STYLUS2, "Stylus2");
		BUTTONS.put(InputEventCodes.BTN_TOOL_DOUBLETAP, "Tool Doubletap");
		BUTTONS.put(InputEventCodes.BTN_TOOL_TRIPLETAP, "Tool Tripletap");
		BUTTONS.put(InputEventCodes.BTN_GEAR_DOWN, "WheelBtn");
		BUTTONS.put(InputEventCodes.BTN_GEAR_UP, "Gear up");

		KEYS.put(InputEventCodes.KEY_RESERVED, "Reserved");
		KEYS.put(InputEventCodes.KEY_ESC, "Esc");
		KEYS.put(InputEventCodes.KEY_1, "1");
		KEYS.put(InputEventCodes.KEY_2, "2");
		KEYS.put(InputEventCodes.KEY_3, "3");
		KEYS.put(InputEventCodes.KEY_4, "4");
		KEYS.put(InputEventCodes.KEY_5, "5");
		KEYS.put(InputEventCodes.KEY_6, "6");
		KEYS.put(InputEventCodes.KEY_7, "7");
		KEYS.put(InputEventCodes.KEY_8, "8");
		KEYS.put(InputEventCodes.KEY_9, "9");
		KEYS.put(InputEventCodes.KEY_0, "0");
		KEYS.put(InputEventCodes.KEY_A, "A");
		KEYS.put(InputEventCodes.KEY_B, "B");
		KEYS.put(InputEventCodes.KEY_C, "C");
		KEYS.put(InputEventCodes.KEY_E, "D");
		KEYS.put(InputEventCodes.KEY_E, "E");
		KEYS.put(InputEventCodes.KEY_F, "F");
		KEYS.put(InputEventCodes.KEY_G, "G");
		KEYS.put(InputEventCodes.KEY_H, "H");
		KEYS.put(InputEventCodes.KEY_I, "I");
		KEYS.put(InputEventCodes.KEY_J, "J");
		KEYS.put(InputEventCodes.KEY_K, "K");
		KEYS.put(InputEventCodes.KEY_L, "L");
		KEYS.put(InputEventCodes.KEY_M, "M");
		KEYS.put(InputEventCodes.KEY_N, "N");
		KEYS.put(InputEventCodes.KEY_O, "O");
		KEYS.put(InputEventCodes.KEY_P, "P");
		KEYS.put(InputEventCodes.KEY_Q, "Q");
		KEYS.put(InputEventCodes.KEY_R, "R");
		KEYS.put(InputEventCodes.KEY_S, "S");
		KEYS.put(InputEventCodes.KEY_T, "T");
		KEYS.put(InputEventCodes.KEY_U, "U");
		KEYS.put(InputEventCodes.KEY_V, "V");
		KEYS.put(InputEventCodes.KEY_W, "W");
		KEYS.put(InputEventCodes.KEY_X, "X");
		KEYS.put(InputEventCodes.KEY_Y, "Y");
		KEYS.put(InputEventCodes.KEY_Z, "Z");
		KEYS.put(InputEventCodes.KEY_MINUS, "Minus");
		KEYS.put(InputEventCodes.KEY_EQUAL, "Equal");
		KEYS.put(InputEventCodes.KEY_BACKSPACE, "Backspace");
		KEYS.put(InputEventCodes.KEY_TAB, "Tab");
		KEYS.put(InputEventCodes.KEY_LEFTBRACE, "LeftBrace");
		KEYS.put(InputEventCodes.KEY_RIGHTBRACE, "RightBrace");
		KEYS.put(InputEventCodes.KEY_ENTER, "Enter");
		KEYS.put(InputEventCodes.KEY_LEFTCTRL, "LeftControl");
		KEYS.put(InputEventCodes.KEY_SEMICOLON, "Semicolon");
		KEYS.put(InputEventCodes.KEY_APOSTROPHE, "Apostrophe");
		KEYS.put(InputEventCodes.KEY_GRAVE, "Grave");
		KEYS.put(InputEventCodes.KEY_LEFTSHIFT, "LeftShift");
		KEYS.put(InputEventCodes.KEY_BACKSLASH, "BackSlash");
		KEYS.put(InputEventCodes.KEY_COMMA, "Comma");
		KEYS.put(InputEventCodes.KEY_DOT, "Dot");
		KEYS.put(InputEventCodes.KEY_RIGHTSHIFT, "RightShift");
		KEYS.put(InputEventCodes.KEY_LEFTALT, "LeftAlt");
		KEYS.put(InputEventCodes.KEY_CAPSLOCK, "CapsLock");
		KEYS.put(InputEventCodes.KEY_SLASH, "Slash");
		KEYS.put(InputEventCodes.KEY_KPASTERISK, "KPAsterisk");
		KEYS.put(InputEventCodes.KEY_SPACE, "Space");
		KEYS.put(InputEventCodes.KEY_F1, "F1");
		KEYS.put(InputEventCodes.KEY_F2, "F2");
		KEYS.put(InputEventCodes.KEY_F3, "F3");
		KEYS.put(InputEventCodes.KEY_F4, "F4");
		KEYS.put(InputEventCodes.KEY_F5, "F5");
		KEYS.put(InputEventCodes.KEY_F6, "F6");
		KEYS.put(InputEventCodes.KEY_F7, "F7");
		KEYS.put(InputEventCodes.KEY_F8, "F8");
		KEYS.put(InputEventCodes.KEY_F9, "F9");
		KEYS.put(InputEventCodes.KEY_F10, "F10");

		KEYS.put(InputEventCodes.KEY_KPDOT, "KPDot");
		KEYS.put(InputEventCodes.KEY_ZENKAKUHANKAKU, "Zenkaku/Hankaku");
		KEYS.put(InputEventCodes.KEY_102ND, "102nd");
		KEYS.put(InputEventCodes.KEY_F11, "F11");
		KEYS.put(InputEventCodes.KEY_F12, "F12");
		KEYS.put(InputEventCodes.KEY_RO, "RO");
		KEYS.put(InputEventCodes.KEY_KATAKANA, "Katakana");
		KEYS.put(InputEventCodes.KEY_HIRAGANA, "HIRAGANA");
		KEYS.put(InputEventCodes.KEY_HENKAN, "Henkan");
		KEYS.put(InputEventCodes.KEY_KATAKANAHIRAGANA, "Katakana/Hiragana");
		KEYS.put(InputEventCodes.KEY_MUHENKAN, "Muhenkan");
		KEYS.put(InputEventCodes.KEY_KPJPCOMMA, "KPJpComma");
		KEYS.put(InputEventCodes.KEY_KPENTER, "KPEnter");
		KEYS.put(InputEventCodes.KEY_RIGHTCTRL, "RightCtrl");
		KEYS.put(InputEventCodes.KEY_KPSLASH, "KPSlash");
		KEYS.put(InputEventCodes.KEY_SYSRQ, "SysRq");
		KEYS.put(InputEventCodes.KEY_RIGHTALT, "RightAlt");
		KEYS.put(InputEventCodes.KEY_LINEFEED, "LineFeed");
		KEYS.put(InputEventCodes.KEY_HOME, "Home");
		KEYS.put(InputEventCodes.KEY_UP, "Up");
		KEYS.put(InputEventCodes.KEY_PAGEUP, "PageUp");
		KEYS.put(InputEventCodes.KEY_LEFT, "Left");
		KEYS.put(InputEventCodes.KEY_RIGHT, "Right");
		KEYS.put(InputEventCodes.KEY_END, "End");
		KEYS.put(InputEventCodes.KEY_DOWN, "Down");
		KEYS.put(InputEventCodes.KEY_PAGEDOWN, "PageDown");
		KEYS.put(InputEventCodes.KEY_INSERT, "Insert");
		KEYS.put(InputEventCodes.KEY_DELETE, "Delete");
		KEYS.put(InputEventCodes.KEY_MACRO, "Macro");
		KEYS.put(InputEventCodes.KEY_MUTE, "Mute");
		KEYS.put(InputEventCodes.KEY_VOLUMEDOWN, "VolumeDown");
		KEYS.put(InputEventCodes.KEY_VOLUMEUP, "VolumeUp");
		KEYS.put(InputEventCodes.KEY_POWER, "Power");
		KEYS.put(InputEventCodes.KEY_KPEQUAL, "KPEqual");
		KEYS.put(InputEventCodes.KEY_KPPLUSMINUS, "KPPlusMinus");
		KEYS.put(InputEventCodes.KEY_PAUSE, "Pause");
		KEYS.put(InputEventCodes.KEY_KPCOMMA, "KPComma");
		KEYS.put(InputEventCodes.KEY_HANGUEL, "Hanguel");
		KEYS.put(InputEventCodes.KEY_HANJA, "Hanja");
		KEYS.put(InputEventCodes.KEY_YEN, "Yen");
		KEYS.put(InputEventCodes.KEY_LEFTMETA, "LeftMeta");
		KEYS.put(InputEventCodes.KEY_RIGHTMETA, "RightMeta");
		KEYS.put(InputEventCodes.KEY_COMPOSE, "Compose");
		KEYS.put(InputEventCodes.KEY_STOP, "Stop");
		KEYS.put(InputEventCodes.KEY_AGAIN, "Again");
		KEYS.put(InputEventCodes.KEY_PROPS, "Props");
		KEYS.put(InputEventCodes.KEY_UNDO, "Undo");
		KEYS.put(InputEventCodes.KEY_FRONT, "Front");
		KEYS.put(InputEventCodes.KEY_COPY, "Copy");
		KEYS.put(InputEventCodes.KEY_OPEN, "Open");
		KEYS.put(InputEventCodes.KEY_PASTE, "Paste");
		KEYS.put(InputEventCodes.KEY_FIND, "Find");
		KEYS.put(InputEventCodes.KEY_CUT, "Cut");
		KEYS.put(InputEventCodes.KEY_HELP, "Help");
		KEYS.put(InputEventCodes.KEY_MENU, "Menu");
		KEYS.put(InputEventCodes.KEY_CALC, "Calc");
		KEYS.put(InputEventCodes.KEY_SETUP, "Setup");
		KEYS.put(InputEventCodes.KEY_SLEEP, "Sleep");
		KEYS.put(InputEventCodes.KEY_WAKEUP, "WakeUp");
		KEYS.put(InputEventCodes.KEY_FILE, "File");
		KEYS.put(InputEventCodes.KEY_SENDFILE, "SendFile");
		KEYS.put(InputEventCodes.KEY_DELETEFILE, "DeleteFile");
		KEYS.put(InputEventCodes.KEY_XFER, "X-fer");
		KEYS.put(InputEventCodes.KEY_PROG1, "Prog1");
		KEYS.put(InputEventCodes.KEY_PROG2, "Prog2");
		KEYS.put(InputEventCodes.KEY_WWW, "WWW");
		KEYS.put(InputEventCodes.KEY_MSDOS, "MSDOS");
		KEYS.put(InputEventCodes.KEY_COFFEE, "Coffee");
		KEYS.put(InputEventCodes.KEY_DIRECTION, "Direction");
		KEYS.put(InputEventCodes.KEY_CYCLEWINDOWS, "CycleWindows");
		KEYS.put(InputEventCodes.KEY_MAIL, "Mail");
		KEYS.put(InputEventCodes.KEY_BOOKMARKS, "Bookmarks");
		KEYS.put(InputEventCodes.KEY_COMPUTER, "Computer");
		KEYS.put(InputEventCodes.KEY_BACK, "Back");
		KEYS.put(InputEventCodes.KEY_FORWARD, "Forward");
		KEYS.put(InputEventCodes.KEY_CLOSECD, "CloseCD");
		KEYS.put(InputEventCodes.KEY_EJECTCD, "EjectCD");
		KEYS.put(InputEventCodes.KEY_EJECTCLOSECD, "EjectCloseCD");
		KEYS.put(InputEventCodes.KEY_NEXTSONG, "NextSong");
		KEYS.put(InputEventCodes.KEY_PLAYPAUSE, "PlayPause");
		KEYS.put(InputEventCodes.KEY_PREVIOUSSONG, "PreviousSong");
		KEYS.put(InputEventCodes.KEY_STOPCD, "StopCD");
		KEYS.put(InputEventCodes.KEY_RECORD, "Record");
		KEYS.put(InputEventCodes.KEY_REWIND, "Rewind");
		KEYS.put(InputEventCodes.KEY_PHONE, "Phone");
		KEYS.put(InputEventCodes.KEY_ISO, "ISOKey");
		KEYS.put(InputEventCodes.KEY_CONFIG, "Config");
		KEYS.put(InputEventCodes.KEY_HOMEPAGE, "HomePage");
		KEYS.put(InputEventCodes.KEY_REFRESH, "Refresh");
		KEYS.put(InputEventCodes.KEY_EXIT, "Exit");
		KEYS.put(InputEventCodes.KEY_MOVE, "Move");
		KEYS.put(InputEventCodes.KEY_EDIT, "Edit");
		KEYS.put(InputEventCodes.KEY_SCROLLUP, "ScrollUp");
		KEYS.put(InputEventCodes.KEY_SCROLLDOWN, "ScrollDown");
		KEYS.put(InputEventCodes.KEY_KPLEFTPAREN, "KPLeftParenthesis");
		KEYS.put(InputEventCodes.KEY_KPRIGHTPAREN, "KPRightParenthesis");
		KEYS.put(InputEventCodes.KEY_F13, "F13");
		KEYS.put(InputEventCodes.KEY_F14, "F14");
		KEYS.put(InputEventCodes.KEY_F15, "F15");
		KEYS.put(InputEventCodes.KEY_F16, "F16");
		KEYS.put(InputEventCodes.KEY_F17, "F17");
		KEYS.put(InputEventCodes.KEY_F18, "F18");
		KEYS.put(InputEventCodes.KEY_F19, "F19");
		KEYS.put(InputEventCodes.KEY_F20, "F20");
		KEYS.put(InputEventCodes.KEY_F21, "F21");
		KEYS.put(InputEventCodes.KEY_F22, "F22");
		KEYS.put(InputEventCodes.KEY_F23, "F23");
		KEYS.put(InputEventCodes.KEY_F24, "F24");
		KEYS.put(InputEventCodes.KEY_PLAYCD, "PlayCD");
		KEYS.put(InputEventCodes.KEY_PAUSECD, "PauseCD");
		KEYS.put(InputEventCodes.KEY_PROG3, "Prog3");
		KEYS.put(InputEventCodes.KEY_PROG4, "Prog4");
		KEYS.put(InputEventCodes.KEY_SUSPEND, "Suspend");
		KEYS.put(InputEventCodes.KEY_CLOSE, "Close");
		KEYS.put(InputEventCodes.KEY_PLAY, "Play");
		KEYS.put(InputEventCodes.KEY_FASTFORWARD, "Fast Forward");
		KEYS.put(InputEventCodes.KEY_BASSBOOST, "Bass Boost");
		KEYS.put(InputEventCodes.KEY_PRINT, "Print");
		KEYS.put(InputEventCodes.KEY_HP, "HP");
		KEYS.put(InputEventCodes.KEY_CAMERA, "Camera");
		KEYS.put(InputEventCodes.KEY_SOUND, "Sound");
		KEYS.put(InputEventCodes.KEY_QUESTION, "Question");
		KEYS.put(InputEventCodes.KEY_EMAIL, "Email");
		KEYS.put(InputEventCodes.KEY_CHAT, "Chat");
		KEYS.put(InputEventCodes.KEY_SEARCH, "Search");
		KEYS.put(InputEventCodes.KEY_CONNECT, "Connect");
		KEYS.put(InputEventCodes.KEY_FINANCE, "Finance");
		KEYS.put(InputEventCodes.KEY_SPORT, "Sport");
		KEYS.put(InputEventCodes.KEY_SHOP, "Shop");
		KEYS.put(InputEventCodes.KEY_ALTERASE, "Alternate Erase");
		KEYS.put(InputEventCodes.KEY_CANCEL, "Cancel");
		KEYS.put(InputEventCodes.KEY_BRIGHTNESSDOWN, "Brightness down");
		KEYS.put(InputEventCodes.KEY_BRIGHTNESSUP, "Brightness up");
		KEYS.put(InputEventCodes.KEY_MEDIA, "Media");
		KEYS.put(InputEventCodes.KEY_UNKNOWN, "Unknown");
		KEYS.put(InputEventCodes.KEY_OK, "Ok");
		KEYS.put(InputEventCodes.KEY_SELECT, "Select");
		KEYS.put(InputEventCodes.KEY_GOTO, "Goto");
		KEYS.put(InputEventCodes.KEY_CLEAR, "Clear");
		KEYS.put(InputEventCodes.KEY_POWER2, "Power2");
		KEYS.put(InputEventCodes.KEY_OPTION, "Option");
		KEYS.put(InputEventCodes.KEY_INFO, "Info");
		KEYS.put(InputEventCodes.KEY_TIME, "Time");
		KEYS.put(InputEventCodes.KEY_VENDOR, "Vendor");
		KEYS.put(InputEventCodes.KEY_ARCHIVE, "Archive");
		KEYS.put(InputEventCodes.KEY_PROGRAM, "Program");
		KEYS.put(InputEventCodes.KEY_CHANNEL, "Channel");
		KEYS.put(InputEventCodes.KEY_FAVORITES, "Favorites");
		KEYS.put(InputEventCodes.KEY_EPG, "EPG");
		KEYS.put(InputEventCodes.KEY_PVR, "PVR");
		KEYS.put(InputEventCodes.KEY_MHP, "MHP");
		KEYS.put(InputEventCodes.KEY_LANGUAGE, "Language");
		KEYS.put(InputEventCodes.KEY_TITLE, "Title");
		KEYS.put(InputEventCodes.KEY_SUBTITLE, "Subtitle");
		KEYS.put(InputEventCodes.KEY_ANGLE, "Angle");
		KEYS.put(InputEventCodes.KEY_ZOOM, "Zoom");
		KEYS.put(InputEventCodes.KEY_MODE, "Mode");
		KEYS.put(InputEventCodes.KEY_KEYBOARD, "Keyboard");
		KEYS.put(InputEventCodes.KEY_SCREEN, "Screen");
		KEYS.put(InputEventCodes.KEY_PC, "PC");
		KEYS.put(InputEventCodes.KEY_TV, "TV");
		KEYS.put(InputEventCodes.KEY_TV2, "TV2");
		KEYS.put(InputEventCodes.KEY_VCR, "VCR");
		KEYS.put(InputEventCodes.KEY_VCR2, "VCR2");
		KEYS.put(InputEventCodes.KEY_SAT, "Sat");
		KEYS.put(InputEventCodes.KEY_SAT2, "Sat2");
		KEYS.put(InputEventCodes.KEY_CD, "CD");
		KEYS.put(InputEventCodes.KEY_TAPE, "Tape");
		KEYS.put(InputEventCodes.KEY_RADIO, "Radio");
		KEYS.put(InputEventCodes.KEY_TUNER, "Tuner");
		KEYS.put(InputEventCodes.KEY_PLAYER, "Player");
		KEYS.put(InputEventCodes.KEY_TEXT, "Text");
		KEYS.put(InputEventCodes.KEY_DVD, "DVD");
		KEYS.put(InputEventCodes.KEY_AUX, "Aux");
		KEYS.put(InputEventCodes.KEY_MP3, "MP3");
		KEYS.put(InputEventCodes.KEY_AUDIO, "Audio");
		KEYS.put(InputEventCodes.KEY_VIDEO, "Video");
		KEYS.put(InputEventCodes.KEY_DIRECTORY, "Directory");
		KEYS.put(InputEventCodes.KEY_LIST, "List");
		KEYS.put(InputEventCodes.KEY_MEMO, "Memo");
		KEYS.put(InputEventCodes.KEY_CALENDAR, "Calendar");
		KEYS.put(InputEventCodes.KEY_RED, "Red");
		KEYS.put(InputEventCodes.KEY_GREEN, "Green");
		KEYS.put(InputEventCodes.KEY_YELLOW, "Yellow");
		KEYS.put(InputEventCodes.KEY_BLUE, "Blue");
		KEYS.put(InputEventCodes.KEY_CHANNELUP, "ChannelUp");
		KEYS.put(InputEventCodes.KEY_CHANNELDOWN, "ChannelDown");
		KEYS.put(InputEventCodes.KEY_FIRST, "First");
		KEYS.put(InputEventCodes.KEY_LAST, "Last");
		KEYS.put(InputEventCodes.KEY_AB, "AB");
		KEYS.put(InputEventCodes.KEY_NEXT, "Next");
		KEYS.put(InputEventCodes.KEY_RESTART, "Restart");
		KEYS.put(InputEventCodes.KEY_SLOW, "Slow");
		KEYS.put(InputEventCodes.KEY_SHUFFLE, "Shuffle");
		KEYS.put(InputEventCodes.KEY_BREAK, "Break");
		KEYS.put(InputEventCodes.KEY_PREVIOUS, "Previous");
		KEYS.put(InputEventCodes.KEY_DIGITS, "Digits");
		KEYS.put(InputEventCodes.KEY_TEEN, "TEEN");
		KEYS.put(InputEventCodes.KEY_TWEN, "TWEN");
		KEYS.put(InputEventCodes.KEY_DEL_EOL, "Delete EOL");
		KEYS.put(InputEventCodes.KEY_DEL_EOS, "Delete EOS");
		KEYS.put(InputEventCodes.KEY_INS_LINE, "Insert line");
		KEYS.put(InputEventCodes.KEY_DEL_LINE, "Delete line");

		// Keys and buttons
		KEYSANDBUTTONS.putAll(KEYS);
		KEYSANDBUTTONS.putAll(BUTTONS);

		// Relative events
		RELATIVES.put(InputEventCodes.REL_X, "X");
		RELATIVES.put(InputEventCodes.REL_Y, "Y");
		RELATIVES.put(InputEventCodes.REL_Z, "Z");
		RELATIVES.put(InputEventCodes.REL_DIAL, "Dial");
		RELATIVES.put(InputEventCodes.REL_MISC, "Misc");
		RELATIVES.put(InputEventCodes.REL_HWHEEL, "HWheel");
		RELATIVES.put(InputEventCodes.REL_WHEEL, "Wheel");

		// Absolutes
		ABSOLUTES.put(InputEventCodes.ABS_X, "X");
		ABSOLUTES.put(InputEventCodes.ABS_Y, "Y");
		ABSOLUTES.put(InputEventCodes.ABS_Z, "Z");
		ABSOLUTES.put(InputEventCodes.ABS_RX, "Rx");
		ABSOLUTES.put(InputEventCodes.ABS_RY, "Ry");
		ABSOLUTES.put(InputEventCodes.ABS_RZ, "Rz");
		ABSOLUTES.put(InputEventCodes.ABS_THROTTLE, "Throttle");
		ABSOLUTES.put(InputEventCodes.ABS_RUDDER, "Rudder");
		ABSOLUTES.put(InputEventCodes.ABS_WHEEL, "Wheel");
		ABSOLUTES.put(InputEventCodes.ABS_GAS, "Gas");
		ABSOLUTES.put(InputEventCodes.ABS_BRAKE, "Brake");
		ABSOLUTES.put(InputEventCodes.ABS_HAT0X, "Hat0X");
		ABSOLUTES.put(InputEventCodes.ABS_HAT0Y, "Hat0Y");
		ABSOLUTES.put(InputEventCodes.ABS_HAT1X, "Hat1X");
		ABSOLUTES.put(InputEventCodes.ABS_HAT1Y, "Hat1Y");
		ABSOLUTES.put(InputEventCodes.ABS_HAT2X, "Hat2X");
		ABSOLUTES.put(InputEventCodes.ABS_HAT2Y, "Hat2Y");
		ABSOLUTES.put(InputEventCodes.ABS_HAT3X, "Hat3X");
		ABSOLUTES.put(InputEventCodes.ABS_HAT3Y, "Hat 3Y");
		ABSOLUTES.put(InputEventCodes.ABS_PRESSURE, "Pressure");
		ABSOLUTES.put(InputEventCodes.ABS_DISTANCE, "Distance");
		ABSOLUTES.put(InputEventCodes.ABS_TILT_X, "XTilt");
		ABSOLUTES.put(InputEventCodes.ABS_TILT_Y, "YTilt");
		ABSOLUTES.put(InputEventCodes.ABS_TOOL_WIDTH, "Tool Width");
		ABSOLUTES.put(InputEventCodes.ABS_VOLUME, "Volume");
		ABSOLUTES.put(InputEventCodes.ABS_MISC, "Misc");

		// Misc
		MISC.put(InputEventCodes.MSC_SERIAL, "Serial");
		MISC.put(InputEventCodes.MSC_PULSELED, "Pulseled");
		MISC.put(InputEventCodes.MSC_RAW, "RawData");
		MISC.put(InputEventCodes.MSC_GESTURE, "Gesture");
		MISC.put(InputEventCodes.MSC_SCAN, "ScanCode");

		// LEDS
		LEDS.put(InputEventCodes.LED_NUML, "NumLock");
		LEDS.put(InputEventCodes.LED_CAPSL, "CapsLock");
		LEDS.put(InputEventCodes.LED_SCROLLL, "ScrollLock");
		LEDS.put(InputEventCodes.LED_COMPOSE, "Compose");
		LEDS.put(InputEventCodes.LED_KANA, "Kana");
		LEDS.put(InputEventCodes.LED_SLEEP, "Sleep");
		LEDS.put(InputEventCodes.LED_SUSPEND, "Suspend");
		LEDS.put(InputEventCodes.LED_MUTE, "Mute");
		LEDS.put(InputEventCodes.LED_MISC, "Misc");

		// Repeats
		REPEATS.put(InputEventCodes.REP_DELAY, "Delay");
		REPEATS.put(InputEventCodes.REP_PERIOD, "Period");

		// Sounds
		SOUNDS.put(InputEventCodes.SND_CLICK, "Click");
		SOUNDS.put(InputEventCodes.SND_TONE, "Tone");
		SOUNDS.put(InputEventCodes.SND_BELL, "Bell");

		// Switches
		SWITCHES.put(InputEventCodes.SW_KEYPAD_SLIDE, "KeypadSlide");
		SWITCHES.put(InputEventCodes.SW_FRONT_PROXIMITY, "FrontProximity");
		SWITCHES.put(InputEventCodes.SW_CAMERA_LENS_COVER, "CameraLensCover");
		SWITCHES.put(InputEventCodes.SW_LINEIN_INSERT, "LineInInsert");
		SWITCHES.put(InputEventCodes.SW_ROTATE_LOCK, "RotateLock");
		SWITCHES.put(InputEventCodes.SW_HEADPHONE_INSERT, "HeadphoneInsert");
		SWITCHES.put(InputEventCodes.SW_DOCK, "Dock");
		SWITCHES.put(InputEventCodes.SW_MICROPHONE_INSERT, "MicrophoneInsert");
		SWITCHES.put(InputEventCodes.SW_RFKILL_ALL, "RFKillAll");
		SWITCHES.put(InputEventCodes.SW_LINEOUT_INSERT, "LineoutInsert");
		SWITCHES.put(InputEventCodes.SW_LID, "Lid");
		SWITCHES.put(InputEventCodes.SW_MAX, "Max");
		SWITCHES.put(InputEventCodes.SW_VIDEOOUT_INSERT, "VideoOutInsert");
		SWITCHES.put(InputEventCodes.SW_JACK_PHYSICAL_INSERT, "JackPhysicalInsert");
		SWITCHES.put(InputEventCodes.SW_CNT, "Cnt");
		SWITCHES.put(InputEventCodes.SW_RADIO, "Radio");
		SWITCHES.put(InputEventCodes.SW_TABLET_MODE, "TableMode");

		// Names - A map of
		NAMES.put(Type.EV_REL, RELATIVES);
		NAMES.put(Type.EV_MSC, MISC);
		NAMES.put(Type.EV_SND, SOUNDS);
		NAMES.put(Type.EV_SW, SWITCHES);
		NAMES.put(Type.EV_KEY, KEYSANDBUTTONS);
		NAMES.put(Type.EV_ABS, ABSOLUTES);
		NAMES.put(Type.EV_LED, LEDS);
		NAMES.put(Type.EV_REP, REPEATS);
	}

	private Path file;
	private int fd;
	private boolean grabbed;
	private String name;
	private String inputDriverVersion;
	private short[] deviceId = new short[4];
	private boolean open;
	private Map<Type, Set<Integer>> caps = new HashMap<Type, Set<Integer>>();
	private pollfd pollFd;
	private pollfd[] pollFds;
	private boolean read;
	private int vendor;
	private int product;

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
	 */
	public final static UInputDevice getFirstPointerDevice() throws IOException {
		Set<Integer> codes = null;
		//
		for (String typeName : System
				.getProperty(SYSPROP_LINUXIO_POINTER_TYPES, Type.EV_ABS.nativeType + "," + Type.EV_REL.nativeType)
				.split(",")) {

			// Parse the type name either by its
			Type t = null;
			try {
				t = Type.fromNative(Integer.parseInt(typeName));
			} catch (NumberFormatException nfe) {
				t = Type.valueOf(typeName);
			}

			if (t == null) {
				LOG.log(Level.WARNING,
						"Unknown event type in " + SYSPROP_LINUXIO_POINTER_TYPES + " property, '" + typeName + "'");
			} else {
				List<UInputDevice> availableDevices = getAvailableDevices();
				UInputDevice theDevice = null;
				try {
					for (UInputDevice dev : availableDevices) {
						if (LOG.isLoggable(Level.DEBUG))
							LOG.log(Level.DEBUG, dev.getName());
						Map<Type, Set<Integer>> capabilties = dev.getCapabilities();
						codes = capabilties.get(t);
						if (t == Type.EV_REL) {
							if (codes == null || (!codes.contains(InputEventCodes.REL_X)
									&& !codes.contains(InputEventCodes.REL_Y))) {
								continue;
							}
						} else if (t == Type.EV_ABS) {
							if (codes == null || (!codes.contains(InputEventCodes.ABS_X)
									&& !codes.contains(InputEventCodes.ABS_Y))) {
								continue;
							}
						}
						LOG.log(Level.TRACE, "Device has some " + t + " caps");
						codes = capabilties.get(Type.EV_KEY);
						int buttons = 0;
						for (Integer s : codes) {
							if (BUTTONS.containsKey(s)) {
								buttons++;
							}
						}
						if (buttons > 0) {
							theDevice = dev;
							break;
						}

					}
				} finally {
					for (UInputDevice d : availableDevices) {
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
	 */
	public final static List<UInputDevice> getAllPointerDevices() throws IOException {
		Set<Integer> codes = null;
		List<UInputDevice> pointerDevices = new ArrayList<UInputDevice>();
		//
		for (String typeName : System
				.getProperty(SYSPROP_LINUXIO_POINTER_TYPES, Type.EV_ABS.nativeType + "," + Type.EV_REL.nativeType)
				.split(",")) {

			// Parse the type name either by its
			Type t = null;
			try {
				t = Type.fromNative(Integer.parseInt(typeName));
			} catch (NumberFormatException nfe) {
				t = Type.valueOf(typeName);
			}

			if (t == null) {
				LOG.log(Level.WARNING,
						"Unknown event type in " + SYSPROP_LINUXIO_POINTER_TYPES + " property, '" + typeName + "'");
			} else {
				List<UInputDevice> availableDevices = getAvailableDevices();
				for (UInputDevice dev : availableDevices) {
					if (LOG.isLoggable(Level.DEBUG))
						LOG.log(Level.DEBUG, dev.getName());
					Map<Type, Set<Integer>> capabilties = dev.getCapabilities();
					codes = capabilties.get(t);
					if (t == Type.EV_REL) {
						if (codes == null
								|| (!codes.contains(InputEventCodes.REL_X) && !codes.contains(InputEventCodes.REL_Y))) {
							continue;
						}
					} else if (t == Type.EV_ABS) {
						if (codes == null
								|| (!codes.contains(InputEventCodes.ABS_X) && !codes.contains(InputEventCodes.ABS_Y))) {
							continue;
						}
					}
					if (LOG.isLoggable(Level.DEBUG))
						LOG.log(Level.DEBUG, "Device has some " + t + " caps");
					codes = capabilties.get(Type.EV_KEY);
					int buttons = 0;
					for (Integer s : codes) {
						if (BUTTONS.containsKey(s)) {
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
	 */
	public final static List<String> getAllKeyboardDeviceNames() throws IOException {
		List<String> keyboardDeviceNames = new ArrayList<String>();
		List<UInputDevice> availableDevices = getAvailableDevices();
		try {
			for (UInputDevice dev : availableDevices) {
				if (LOG.isLoggable(Level.DEBUG))
					LOG.log(Level.DEBUG, dev.getName());
				Set<Integer> codes = dev.getCapabilities().get(Type.EV_KEY);
				if (codes == null) {
					continue;
				}
				int keys = 0;
				for (Integer s : codes) {
					if (BUTTONS.containsKey(s)) {
						// Has buttons, presumably not a keyboard
						continue;
					} else if (KEYS.containsKey(s)) {
						keys++;
					}
				}
				if (keys > 5) {
					keyboardDeviceNames.add(dev.getFile().getFileName().toString());
				}
			}
		} finally {
			for (UInputDevice d : availableDevices) {
				d.close();
			}
		}
		return keyboardDeviceNames;
	}

	/**
	 * Helper to get what appears to be all keyboard devices. If no devices could be
	 * found, an exception will be thrown. NOTE, all {@link UInputDevice} returned
	 * will be <b>open</b>, so should be closed if you do not intend to carry on
	 * using them.
	 * 
	 * @return keyboard devices
	 */
	public final static List<UInputDevice> getAllKeyboardDevices() throws IOException {
		List<UInputDevice> keyboardDevices = new ArrayList<UInputDevice>();
		for (UInputDevice dev : getAvailableDevices()) {
			if (LOG.isLoggable(Level.DEBUG))
				LOG.log(Level.DEBUG, dev.getName());
			Set<Integer> codes = dev.getCapabilities().get(Type.EV_KEY);
			if (codes == null) {
				continue;
			}
			int keys = 0;
			for (Integer s : codes) {
				if (BUTTONS.containsKey(s)) {
					// Has buttons, presumably not a keyboard
					continue;
				} else if (KEYS.containsKey(s)) {
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
	 * @param name
	 * @return device
	 */
	public final static UInputDevice getDeviceByName(String name) throws IOException {

		List<UInputDevice> availableDevices = getAvailableDevices();
		for (UInputDevice dev : availableDevices) {
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
	 */
	public final static UInputDevice getFirstKeyboardDevice() throws IOException {

		List<UInputDevice> availableDevices = getAvailableDevices();
		UInputDevice theDevice = null;
		try {
			for (UInputDevice dev : availableDevices) {
				if (LOG.isLoggable(Level.DEBUG))
					LOG.log(Level.DEBUG, dev.getName());
				Set<Integer> codes = dev.getCapabilities().get(Type.EV_KEY);
				if (codes == null) {
					continue;
				}
				int keys = 0;
				for (Integer s : codes) {
					if (BUTTONS.containsKey(s)) {
						// Has buttons, presumably not a keyboard
						continue;
					} else if (KEYS.containsKey(s)) {
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
			for (UInputDevice d : availableDevices) {
				if (d != theDevice) {
					d.close();
				}
			}
		}

		throw new IOException("No devices that look like a keyboard could be found.");
	}

	/**
	 * Get a list of all available devices;
	 * 
	 * @return
	 */
	public final static List<UInputDevice> getAvailableDevices() throws IOException {
		final List<UInputDevice> d = new ArrayList<UInputDevice>();
		File dir = getInputDeviceDirectory();
		if (dir.exists()) {
			if (dir.canRead()) {
				for (File f : dir.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.canRead() && pathname.getName().startsWith("event");
					}
				})) {
					d.add(new UInputDevice(f));
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
	public UInputDevice(String path) throws IOException {
		this(path.startsWith("/") ? new File(path)
				: (new File(path).exists() ? new File(path) : new File(getInputDeviceDirectory(), path)));
	}

	/**
	 * Open an existing device given it's file for reading events
	 * 
	 * @param file device file
	 * @throws IOException if device file cannot be opened.
	 */
	public UInputDevice(File file) throws IOException {
		this(file.toPath());
	}

	/**
	 * Open an existing device given it's file for reading events
	 * 
	 * @param file device file
	 * @throws IOException if device file cannot be opened.
	 */
	public UInputDevice(Path file) throws IOException {
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
		inputDriverVersion = String.format("%d.%d.%d", vv >> 16, vv >> 8, vv & 0xff);

		// Get the device IDs
		CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGID, deviceId);

		// Get the bits
		NativeLong[][] bit = new NativeLong[InputEventCodes.EV_MAX][NBITS(InputEventCodes.KEY_MAX)];
		CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGBIT(0, InputEventCodes.EV_MAX), bit[0]);

		int[] abs = new int[5];
		for (int i = 0; i < InputEventCodes.EV_MAX; i++) {
			Type t = Type.fromNative(i);
			Set<Integer> c = caps.get(t);
			if (test_bit(i, bit[0])) {
				if (LOG.isLoggable(Level.DEBUG))
					LOG.log(Level.DEBUG, String.format("  Event type %d (%s)\n", i, Type.fromNative((short) i)));
				// if (!i) continue;
				CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGBIT(i, InputEventCodes.KEY_MAX), bit[i]);
				for (int j = 0; j < InputEventCodes.KEY_MAX; j++) {
					if (test_bit(j, bit[i])) {
						if (LOG.isLoggable(Level.DEBUG))
							LOG.log(Level.DEBUG, String.format("    Event code %d (%s)", j,
									NAMES.containsKey(t) && NAMES.get(t).containsKey(j) ? NAMES.get(t).get(j) : ""));
						if (c == null) {
							c = new HashSet<Integer>();
							caps.put(t, c);
						}
						c.add(j);
						if (t.equals(Type.EV_ABS)) {
							CLib.INSTANCE.ioctl(fd, Input.Macros.EVIOCGABS(j), abs);
							for (int k = 0; k < 5; k++) {
								if ((k < 3) || abs[k] > 0) {
									if (LOG.isLoggable(Level.DEBUG))
										LOG.log(Level.DEBUG, String.format("      %s %6d", ABSVAL[k], abs[k]));
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Create a new virtual device for emitting events.
	 * 
	 * @param name    virtual device name
	 * @param vendor  USB vendor code
	 * @param product USB prodcut code
	 * 
	 */
	public UInputDevice(String name, int vendor, int product) {
		this.read = false;
		this.vendor = vendor;
		this.product = product;
		file = Paths.get("/dev/uinput");
		this.name = name;
	}

	public void open() throws IOException {
		if (read)
			openForRead(file);
		else {
			openForWrite();
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

		IntByReference version = new IntByReference();
		int rc = CLib.INSTANCE.ioctl(fd, UInput.UI_GET_VERSION, version.getPointer());
		int v = version.getValue();
		if (rc == 0 && v >= 5) {

			for (Map.Entry<Type, Set<Integer>> en : caps.entrySet()) {
				checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_SET_EVBIT, en.getKey().getNativeType()));
				for (Integer c : en.getValue()) {
					switch (en.getKey()) {
					case EV_KEY:
						checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_SET_KEYBIT, c));
						break;
					case EV_REL:
						checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_SET_RELBIT, c));
						break;
					default:
						throw new UnsupportedOperationException("TODO!");
					}
				}
			}

			uinput_setup setup = new uinput_setup();
			setup.id.bustype = UInput.BUS_USB;
			setup.id.product = (short)product;
			setup.id.vendor = (short)vendor;
			System.arraycopy(name.getBytes(), 0, setup.name, 0,
					Math.min(setup.name.length - 1, Math.min(name.getBytes().length, setup.name.length)));

			checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_DEV_SETUP, setup));
			checkIoctl(CLib.INSTANCE.ioctl(fd, UInput.UI_DEV_CREATE));
		} else {
			// TODO
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
	 * Get the driver version
	 * 
	 * @return driver version
	 */
	public String getDriverVersion() {
		return inputDriverVersion;
	}

	/**
	 * Get the name for this device
	 * 
	 * @return device name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Add a capability to an event type. Only relevant when creating a new virtual
	 * device, and must be done before the call is made to {@link #open()}.
	 */
	public void addCapability(Type eventType, Integer... keys) {
		Map<Type, Set<Integer>> caps = getCapabilities();
		Set<Integer> current = caps.get(eventType);
		if (current == null) {
			current = new LinkedHashSet<>();
			caps.put(eventType, current);
		}
		current.addAll(Arrays.asList(keys));
	}

	/**
	 * Either get the capbilities the device has, or set the capabilities should
	 * have, depending on whether the device is being created as a virtual device or
	 * opened as an existing device.
	 * 
	 * @return capabilities
	 */
	public Map<Type, Set<Integer>> getCapabilities() {
		if (read || open)
			return Collections.unmodifiableMap(caps);
		else
			return caps;
	}

	/**
	 * Get the path to file this input device is accessed by.
	 * 
	 * @return
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
		emit(new Event(UInputDevice.Type.EV_KEY, code, 1));
	}

	/**
	 * Convenience method to press a key (SYN will be emitted).
	 * 
	 * @param code code of key to release
	 * @throws IOException on error
	 */
	public void releaseKey(int code) throws IOException {
		emit(new Event(UInputDevice.Type.EV_KEY, code, 0));
	}

	/**
	 * Convenience method to type a key. The key will be pressed and released, with SYN
	 * after each state, with a 1 millisecond delay.
	 * 
	 * @param code code of key to type
	 * @throws IOException on error
	 */
	public void typeKey(int code) throws IOException {
		typeKey(code, 1);
	}

	/**
	 * Convenience method to type a key. The key will be pressed and released, with SYN
	 * after each state, with an optional delay.
	 * 
	 * @param code code of key to type
	 * @param delay delay in milliseconds
	 * @throws IOException on error
	 */
	public void typeKey(int code, long delay) throws IOException {
		pressKey(code);
		if(delay >0)
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// If interrupted, don't leave the key pressed if at all possible
			}
		releaseKey(code);
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
		ie.code = evt.code;
		ie.type = (short) evt.type.getNativeType();
		ie.value = evt.value;
		ie.time.tv_sec = new NativeLong(0);
		ie.time.tv_usec = new NativeLong(0);

		if (LOG.isLoggable(Level.DEBUG))
			LOG.log(Level.DEBUG, "Sending event " + evt);
		
		if (CLib.INSTANCE.write(fd, ie, new NativeLong(ie.size()))
				.longValue() == -1)
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
		return new Event(ev);
	}

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

	@Override
	public String toString() {
		return "UInputDevice [file=" + file + ", grabbed=" + grabbed + ", name=" + name + ", inputDriverVersion="
				+ inputDriverVersion + ", caps=" + caps + ",deviceId=" + Arrays.toString(deviceId) + "]";
	}

	// Macros

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

	public final static void main(String[] args) throws Exception {
		// for (UInputDevice d : getAvailableDevices()) {
		// LOG.info(d);
		// }
		LOG.log(Level.INFO, "Mouse: " + UInputDevice.getFirstPointerDevice());
		LOG.log(Level.INFO, "Keyboard: " + UInputDevice.getFirstKeyboardDevice());
	}

	public boolean isOpen() {
		return open;
	}

	public int getFD() {
		return fd;
	}
}
