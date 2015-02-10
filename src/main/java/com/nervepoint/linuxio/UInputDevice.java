package com.nervepoint.linuxio;

import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nervepoint.linuxio.CLib.pollfd;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

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
 * 
 * @ *
 */
public class UInputDevice implements Closeable {

    private static final String SYSPROP_LINUXIO_POINTER_TYPES = "linuxio.pointer.types";
    private static final String INPUT_DEVICES = "linuxio.input.devices";

    public enum Type {
        EV_SYN(CLib.EV_SYN), EV_REL(CLib.EV_REL), EV_MSC(CLib.EV_MSC), EV_SND(CLib.EV_SND), EV_FF(CLib.EV_FF), EV_FF_STATUS(
                        CLib.EV_FF_STATUS), EV_KEY(CLib.EV_KEY), EV_ABS(CLib.EV_ABS), EV_LED(CLib.EV_LED), EV_REP(CLib.EV_REP), EV_PWT(
                        CLib.EV_PWR), EV_SW(CLib.EV_SW), UNKNOWN(-1);

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

    public class Event {
        private long utime;
        private Type type;
        private short code;
        private int value;

        private Event(CLib.input_event ev) {
            this(ev.time.tv_usec.longValue() * 1000, Type.fromNative(ev.type), ev.code, ev.value);
        }

        private Event(long utime, Type type, short code, int value) {
            super();
            this.utime = utime;
            this.type = type;
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

        BUTTONS.put(CLib.BTN_0, "Btn0");
        BUTTONS.put(CLib.BTN_1, "Btn1");
        BUTTONS.put(CLib.BTN_2, "Btn2");
        BUTTONS.put(CLib.BTN_3, "Btn3");
        BUTTONS.put(CLib.BTN_4, "Btn4");
        BUTTONS.put(CLib.BTN_5, "Btn5");
        BUTTONS.put(CLib.BTN_6, "Btn6");
        BUTTONS.put(CLib.BTN_7, "Btn7");
        BUTTONS.put(CLib.BTN_8, "Btn8");
        BUTTONS.put(CLib.BTN_9, "Btn9");
        BUTTONS.put(CLib.BTN_LEFT, "LeftBtn");
        BUTTONS.put(CLib.BTN_RIGHT, "RightBtn");
        BUTTONS.put(CLib.BTN_MIDDLE, "MiddleBtn");
        BUTTONS.put(CLib.BTN_SIDE, "SideBtn");
        BUTTONS.put(CLib.BTN_EXTRA, "ExtraBtn");
        BUTTONS.put(CLib.BTN_FORWARD, "ForwardBtn");
        BUTTONS.put(CLib.BTN_BACK, "BackBtn");
        BUTTONS.put(CLib.BTN_TASK, "TaskBtn");
        BUTTONS.put(CLib.BTN_TRIGGER, "Trigger");
        BUTTONS.put(CLib.BTN_THUMB, "ThumbBtn");
        BUTTONS.put(CLib.BTN_THUMB2, "ThumbBtn2");
        BUTTONS.put(CLib.BTN_TOP, "TopBtn");
        BUTTONS.put(CLib.BTN_TOP2, "TopBtn2");
        BUTTONS.put(CLib.BTN_PINKIE, "PinkieBtn");
        BUTTONS.put(CLib.BTN_BASE, "BaseBtn");
        BUTTONS.put(CLib.BTN_BASE2, "BaseBtn2");
        BUTTONS.put(CLib.BTN_BASE3, "BaseBtn3");
        BUTTONS.put(CLib.BTN_BASE4, "BaseBtn4");
        BUTTONS.put(CLib.BTN_BASE5, "BaseBtn5");
        BUTTONS.put(CLib.BTN_BASE6, "BaseBtn6");
        BUTTONS.put(CLib.BTN_DEAD, "BtnDead");
        BUTTONS.put(CLib.BTN_A, "BtnA");
        BUTTONS.put(CLib.BTN_B, "BtnB");
        BUTTONS.put(CLib.BTN_C, "BtnC");
        BUTTONS.put(CLib.BTN_X, "BtnX");
        BUTTONS.put(CLib.BTN_Y, "BtnY");
        BUTTONS.put(CLib.BTN_Z, "BtnZ");
        BUTTONS.put(CLib.BTN_TL, "BtnTL");
        BUTTONS.put(CLib.BTN_TR, "BtnTR");
        BUTTONS.put(CLib.BTN_TL2, "BtnTL2");
        BUTTONS.put(CLib.BTN_TR2, "BtnTR2");
        BUTTONS.put(CLib.BTN_SELECT, "BtnSelect");
        BUTTONS.put(CLib.BTN_START, "BtnStart");
        BUTTONS.put(CLib.BTN_MODE, "BtnMode");
        BUTTONS.put(CLib.BTN_THUMBL, "BtnThumbL");
        BUTTONS.put(CLib.BTN_THUMBR, "BtnThumbR");
        BUTTONS.put(CLib.BTN_TOOL_PEN, "ToolPen");
        BUTTONS.put(CLib.BTN_TOOL_RUBBER, "ToolRubber");
        BUTTONS.put(CLib.BTN_TOOL_BRUSH, "ToolBrush");
        BUTTONS.put(CLib.BTN_TOOL_PENCIL, "ToolPencil");
        BUTTONS.put(CLib.BTN_TOOL_AIRBRUSH, "ToolAirbrush");
        BUTTONS.put(CLib.BTN_TOOL_FINGER, "ToolFinger");
        BUTTONS.put(CLib.BTN_TOOL_MOUSE, "ToolMouse");
        BUTTONS.put(CLib.BTN_TOOL_LENS, "ToolLens");
        BUTTONS.put(CLib.BTN_TOUCH, "Touch");
        BUTTONS.put(CLib.BTN_STYLUS, "Stylus");
        BUTTONS.put(CLib.BTN_STYLUS2, "Stylus2");
        BUTTONS.put(CLib.BTN_TOOL_DOUBLETAP, "Tool Doubletap");
        BUTTONS.put(CLib.BTN_TOOL_TRIPLETAP, "Tool Tripletap");
        BUTTONS.put(CLib.BTN_GEAR_DOWN, "WheelBtn");
        BUTTONS.put(CLib.BTN_GEAR_UP, "Gear up");

        KEYS.put(CLib.KEY_RESERVED, "Reserved");
        KEYS.put(CLib.KEY_ESC, "Esc");
        KEYS.put(CLib.KEY_1, "1");
        KEYS.put(CLib.KEY_2, "2");
        KEYS.put(CLib.KEY_3, "3");
        KEYS.put(CLib.KEY_4, "4");
        KEYS.put(CLib.KEY_5, "5");
        KEYS.put(CLib.KEY_6, "6");
        KEYS.put(CLib.KEY_7, "7");
        KEYS.put(CLib.KEY_8, "8");
        KEYS.put(CLib.KEY_9, "9");
        KEYS.put(CLib.KEY_0, "0");
        KEYS.put(CLib.KEY_A, "A");
        KEYS.put(CLib.KEY_B, "B");
        KEYS.put(CLib.KEY_C, "C");
        KEYS.put(CLib.KEY_E, "D");
        KEYS.put(CLib.KEY_E, "E");
        KEYS.put(CLib.KEY_F, "F");
        KEYS.put(CLib.KEY_G, "G");
        KEYS.put(CLib.KEY_H, "H");
        KEYS.put(CLib.KEY_I, "I");
        KEYS.put(CLib.KEY_J, "J");
        KEYS.put(CLib.KEY_K, "K");
        KEYS.put(CLib.KEY_L, "L");
        KEYS.put(CLib.KEY_M, "M");
        KEYS.put(CLib.KEY_N, "N");
        KEYS.put(CLib.KEY_O, "O");
        KEYS.put(CLib.KEY_P, "P");
        KEYS.put(CLib.KEY_Q, "Q");
        KEYS.put(CLib.KEY_R, "R");
        KEYS.put(CLib.KEY_S, "S");
        KEYS.put(CLib.KEY_T, "T");
        KEYS.put(CLib.KEY_U, "U");
        KEYS.put(CLib.KEY_V, "V");
        KEYS.put(CLib.KEY_W, "W");
        KEYS.put(CLib.KEY_X, "X");
        KEYS.put(CLib.KEY_Y, "Y");
        KEYS.put(CLib.KEY_Z, "Z");
        KEYS.put(CLib.KEY_MINUS, "Minus");
        KEYS.put(CLib.KEY_EQUAL, "Equal");
        KEYS.put(CLib.KEY_BACKSPACE, "Backspace");
        KEYS.put(CLib.KEY_TAB, "Tab");
        KEYS.put(CLib.KEY_LEFTBRACE, "LeftBrace");
        KEYS.put(CLib.KEY_RIGHTBRACE, "RightBrace");
        KEYS.put(CLib.KEY_ENTER, "Enter");
        KEYS.put(CLib.KEY_LEFTCTRL, "LeftControl");
        KEYS.put(CLib.KEY_SEMICOLON, "Semicolon");
        KEYS.put(CLib.KEY_APOSTROPHE, "Apostrophe");
        KEYS.put(CLib.KEY_GRAVE, "Grave");
        KEYS.put(CLib.KEY_LEFTSHIFT, "LeftShift");
        KEYS.put(CLib.KEY_BACKSLASH, "BackSlash");
        KEYS.put(CLib.KEY_COMMA, "Comma");
        KEYS.put(CLib.KEY_DOT, "Dot");
        KEYS.put(CLib.KEY_RIGHTSHIFT, "RightShift");
        KEYS.put(CLib.KEY_LEFTALT, "LeftAlt");
        KEYS.put(CLib.KEY_CAPSLOCK, "CapsLock");
        KEYS.put(CLib.KEY_SLASH, "Slash");
        KEYS.put(CLib.KEY_KPASTERISK, "KPAsterisk");
        KEYS.put(CLib.KEY_SPACE, "Space");
        KEYS.put(CLib.KEY_F1, "F1");
        KEYS.put(CLib.KEY_F2, "F2");
        KEYS.put(CLib.KEY_F3, "F3");
        KEYS.put(CLib.KEY_F4, "F4");
        KEYS.put(CLib.KEY_F5, "F5");
        KEYS.put(CLib.KEY_F6, "F6");
        KEYS.put(CLib.KEY_F7, "F7");
        KEYS.put(CLib.KEY_F8, "F8");
        KEYS.put(CLib.KEY_F9, "F9");
        KEYS.put(CLib.KEY_F10, "F10");

        KEYS.put(CLib.KEY_KPDOT, "KPDot");
        KEYS.put(CLib.KEY_ZENKAKUHANKAKU, "Zenkaku/Hankaku");
        KEYS.put(CLib.KEY_102ND, "102nd");
        KEYS.put(CLib.KEY_F11, "F11");
        KEYS.put(CLib.KEY_F12, "F12");
        KEYS.put(CLib.KEY_RO, "RO");
        KEYS.put(CLib.KEY_KATAKANA, "Katakana");
        KEYS.put(CLib.KEY_HIRAGANA, "HIRAGANA");
        KEYS.put(CLib.KEY_HENKAN, "Henkan");
        KEYS.put(CLib.KEY_KATAKANAHIRAGANA, "Katakana/Hiragana");
        KEYS.put(CLib.KEY_MUHENKAN, "Muhenkan");
        KEYS.put(CLib.KEY_KPJPCOMMA, "KPJpComma");
        KEYS.put(CLib.KEY_KPENTER, "KPEnter");
        KEYS.put(CLib.KEY_RIGHTCTRL, "RightCtrl");
        KEYS.put(CLib.KEY_KPSLASH, "KPSlash");
        KEYS.put(CLib.KEY_SYSRQ, "SysRq");
        KEYS.put(CLib.KEY_RIGHTALT, "RightAlt");
        KEYS.put(CLib.KEY_LINEFEED, "LineFeed");
        KEYS.put(CLib.KEY_HOME, "Home");
        KEYS.put(CLib.KEY_UP, "Up");
        KEYS.put(CLib.KEY_PAGEUP, "PageUp");
        KEYS.put(CLib.KEY_LEFT, "Left");
        KEYS.put(CLib.KEY_RIGHT, "Right");
        KEYS.put(CLib.KEY_END, "End");
        KEYS.put(CLib.KEY_DOWN, "Down");
        KEYS.put(CLib.KEY_PAGEDOWN, "PageDown");
        KEYS.put(CLib.KEY_INSERT, "Insert");
        KEYS.put(CLib.KEY_DELETE, "Delete");
        KEYS.put(CLib.KEY_MACRO, "Macro");
        KEYS.put(CLib.KEY_MUTE, "Mute");
        KEYS.put(CLib.KEY_VOLUMEDOWN, "VolumeDown");
        KEYS.put(CLib.KEY_VOLUMEUP, "VolumeUp");
        KEYS.put(CLib.KEY_POWER, "Power");
        KEYS.put(CLib.KEY_KPEQUAL, "KPEqual");
        KEYS.put(CLib.KEY_KPPLUSMINUS, "KPPlusMinus");
        KEYS.put(CLib.KEY_PAUSE, "Pause");
        KEYS.put(CLib.KEY_KPCOMMA, "KPComma");
        KEYS.put(CLib.KEY_HANGUEL, "Hanguel");
        KEYS.put(CLib.KEY_HANJA, "Hanja");
        KEYS.put(CLib.KEY_YEN, "Yen");
        KEYS.put(CLib.KEY_LEFTMETA, "LeftMeta");
        KEYS.put(CLib.KEY_RIGHTMETA, "RightMeta");
        KEYS.put(CLib.KEY_COMPOSE, "Compose");
        KEYS.put(CLib.KEY_STOP, "Stop");
        KEYS.put(CLib.KEY_AGAIN, "Again");
        KEYS.put(CLib.KEY_PROPS, "Props");
        KEYS.put(CLib.KEY_UNDO, "Undo");
        KEYS.put(CLib.KEY_FRONT, "Front");
        KEYS.put(CLib.KEY_COPY, "Copy");
        KEYS.put(CLib.KEY_OPEN, "Open");
        KEYS.put(CLib.KEY_PASTE, "Paste");
        KEYS.put(CLib.KEY_FIND, "Find");
        KEYS.put(CLib.KEY_CUT, "Cut");
        KEYS.put(CLib.KEY_HELP, "Help");
        KEYS.put(CLib.KEY_MENU, "Menu");
        KEYS.put(CLib.KEY_CALC, "Calc");
        KEYS.put(CLib.KEY_SETUP, "Setup");
        KEYS.put(CLib.KEY_SLEEP, "Sleep");
        KEYS.put(CLib.KEY_WAKEUP, "WakeUp");
        KEYS.put(CLib.KEY_FILE, "File");
        KEYS.put(CLib.KEY_SENDFILE, "SendFile");
        KEYS.put(CLib.KEY_DELETEFILE, "DeleteFile");
        KEYS.put(CLib.KEY_XFER, "X-fer");
        KEYS.put(CLib.KEY_PROG1, "Prog1");
        KEYS.put(CLib.KEY_PROG2, "Prog2");
        KEYS.put(CLib.KEY_WWW, "WWW");
        KEYS.put(CLib.KEY_MSDOS, "MSDOS");
        KEYS.put(CLib.KEY_COFFEE, "Coffee");
        KEYS.put(CLib.KEY_DIRECTION, "Direction");
        KEYS.put(CLib.KEY_CYCLEWINDOWS, "CycleWindows");
        KEYS.put(CLib.KEY_MAIL, "Mail");
        KEYS.put(CLib.KEY_BOOKMARKS, "Bookmarks");
        KEYS.put(CLib.KEY_COMPUTER, "Computer");
        KEYS.put(CLib.KEY_BACK, "Back");
        KEYS.put(CLib.KEY_FORWARD, "Forward");
        KEYS.put(CLib.KEY_CLOSECD, "CloseCD");
        KEYS.put(CLib.KEY_EJECTCD, "EjectCD");
        KEYS.put(CLib.KEY_EJECTCLOSECD, "EjectCloseCD");
        KEYS.put(CLib.KEY_NEXTSONG, "NextSong");
        KEYS.put(CLib.KEY_PLAYPAUSE, "PlayPause");
        KEYS.put(CLib.KEY_PREVIOUSSONG, "PreviousSong");
        KEYS.put(CLib.KEY_STOPCD, "StopCD");
        KEYS.put(CLib.KEY_RECORD, "Record");
        KEYS.put(CLib.KEY_REWIND, "Rewind");
        KEYS.put(CLib.KEY_PHONE, "Phone");
        KEYS.put(CLib.KEY_ISO, "ISOKey");
        KEYS.put(CLib.KEY_CONFIG, "Config");
        KEYS.put(CLib.KEY_HOMEPAGE, "HomePage");
        KEYS.put(CLib.KEY_REFRESH, "Refresh");
        KEYS.put(CLib.KEY_EXIT, "Exit");
        KEYS.put(CLib.KEY_MOVE, "Move");
        KEYS.put(CLib.KEY_EDIT, "Edit");
        KEYS.put(CLib.KEY_SCROLLUP, "ScrollUp");
        KEYS.put(CLib.KEY_SCROLLDOWN, "ScrollDown");
        KEYS.put(CLib.KEY_KPLEFTPAREN, "KPLeftParenthesis");
        KEYS.put(CLib.KEY_KPRIGHTPAREN, "KPRightParenthesis");
        KEYS.put(CLib.KEY_F13, "F13");
        KEYS.put(CLib.KEY_F14, "F14");
        KEYS.put(CLib.KEY_F15, "F15");
        KEYS.put(CLib.KEY_F16, "F16");
        KEYS.put(CLib.KEY_F17, "F17");
        KEYS.put(CLib.KEY_F18, "F18");
        KEYS.put(CLib.KEY_F19, "F19");
        KEYS.put(CLib.KEY_F20, "F20");
        KEYS.put(CLib.KEY_F21, "F21");
        KEYS.put(CLib.KEY_F22, "F22");
        KEYS.put(CLib.KEY_F23, "F23");
        KEYS.put(CLib.KEY_F24, "F24");
        KEYS.put(CLib.KEY_PLAYCD, "PlayCD");
        KEYS.put(CLib.KEY_PAUSECD, "PauseCD");
        KEYS.put(CLib.KEY_PROG3, "Prog3");
        KEYS.put(CLib.KEY_PROG4, "Prog4");
        KEYS.put(CLib.KEY_SUSPEND, "Suspend");
        KEYS.put(CLib.KEY_CLOSE, "Close");
        KEYS.put(CLib.KEY_PLAY, "Play");
        KEYS.put(CLib.KEY_FASTFORWARD, "Fast Forward");
        KEYS.put(CLib.KEY_BASSBOOST, "Bass Boost");
        KEYS.put(CLib.KEY_PRINT, "Print");
        KEYS.put(CLib.KEY_HP, "HP");
        KEYS.put(CLib.KEY_CAMERA, "Camera");
        KEYS.put(CLib.KEY_SOUND, "Sound");
        KEYS.put(CLib.KEY_QUESTION, "Question");
        KEYS.put(CLib.KEY_EMAIL, "Email");
        KEYS.put(CLib.KEY_CHAT, "Chat");
        KEYS.put(CLib.KEY_SEARCH, "Search");
        KEYS.put(CLib.KEY_CONNECT, "Connect");
        KEYS.put(CLib.KEY_FINANCE, "Finance");
        KEYS.put(CLib.KEY_SPORT, "Sport");
        KEYS.put(CLib.KEY_SHOP, "Shop");
        KEYS.put(CLib.KEY_ALTERASE, "Alternate Erase");
        KEYS.put(CLib.KEY_CANCEL, "Cancel");
        KEYS.put(CLib.KEY_BRIGHTNESSDOWN, "Brightness down");
        KEYS.put(CLib.KEY_BRIGHTNESSUP, "Brightness up");
        KEYS.put(CLib.KEY_MEDIA, "Media");
        KEYS.put(CLib.KEY_UNKNOWN, "Unknown");
        KEYS.put(CLib.KEY_OK, "Ok");
        KEYS.put(CLib.KEY_SELECT, "Select");
        KEYS.put(CLib.KEY_GOTO, "Goto");
        KEYS.put(CLib.KEY_CLEAR, "Clear");
        KEYS.put(CLib.KEY_POWER2, "Power2");
        KEYS.put(CLib.KEY_OPTION, "Option");
        KEYS.put(CLib.KEY_INFO, "Info");
        KEYS.put(CLib.KEY_TIME, "Time");
        KEYS.put(CLib.KEY_VENDOR, "Vendor");
        KEYS.put(CLib.KEY_ARCHIVE, "Archive");
        KEYS.put(CLib.KEY_PROGRAM, "Program");
        KEYS.put(CLib.KEY_CHANNEL, "Channel");
        KEYS.put(CLib.KEY_FAVORITES, "Favorites");
        KEYS.put(CLib.KEY_EPG, "EPG");
        KEYS.put(CLib.KEY_PVR, "PVR");
        KEYS.put(CLib.KEY_MHP, "MHP");
        KEYS.put(CLib.KEY_LANGUAGE, "Language");
        KEYS.put(CLib.KEY_TITLE, "Title");
        KEYS.put(CLib.KEY_SUBTITLE, "Subtitle");
        KEYS.put(CLib.KEY_ANGLE, "Angle");
        KEYS.put(CLib.KEY_ZOOM, "Zoom");
        KEYS.put(CLib.KEY_MODE, "Mode");
        KEYS.put(CLib.KEY_KEYBOARD, "Keyboard");
        KEYS.put(CLib.KEY_SCREEN, "Screen");
        KEYS.put(CLib.KEY_PC, "PC");
        KEYS.put(CLib.KEY_TV, "TV");
        KEYS.put(CLib.KEY_TV2, "TV2");
        KEYS.put(CLib.KEY_VCR, "VCR");
        KEYS.put(CLib.KEY_VCR2, "VCR2");
        KEYS.put(CLib.KEY_SAT, "Sat");
        KEYS.put(CLib.KEY_SAT2, "Sat2");
        KEYS.put(CLib.KEY_CD, "CD");
        KEYS.put(CLib.KEY_TAPE, "Tape");
        KEYS.put(CLib.KEY_RADIO, "Radio");
        KEYS.put(CLib.KEY_TUNER, "Tuner");
        KEYS.put(CLib.KEY_PLAYER, "Player");
        KEYS.put(CLib.KEY_TEXT, "Text");
        KEYS.put(CLib.KEY_DVD, "DVD");
        KEYS.put(CLib.KEY_AUX, "Aux");
        KEYS.put(CLib.KEY_MP3, "MP3");
        KEYS.put(CLib.KEY_AUDIO, "Audio");
        KEYS.put(CLib.KEY_VIDEO, "Video");
        KEYS.put(CLib.KEY_DIRECTORY, "Directory");
        KEYS.put(CLib.KEY_LIST, "List");
        KEYS.put(CLib.KEY_MEMO, "Memo");
        KEYS.put(CLib.KEY_CALENDAR, "Calendar");
        KEYS.put(CLib.KEY_RED, "Red");
        KEYS.put(CLib.KEY_GREEN, "Green");
        KEYS.put(CLib.KEY_YELLOW, "Yellow");
        KEYS.put(CLib.KEY_BLUE, "Blue");
        KEYS.put(CLib.KEY_CHANNELUP, "ChannelUp");
        KEYS.put(CLib.KEY_CHANNELDOWN, "ChannelDown");
        KEYS.put(CLib.KEY_FIRST, "First");
        KEYS.put(CLib.KEY_LAST, "Last");
        KEYS.put(CLib.KEY_AB, "AB");
        KEYS.put(CLib.KEY_NEXT, "Next");
        KEYS.put(CLib.KEY_RESTART, "Restart");
        KEYS.put(CLib.KEY_SLOW, "Slow");
        KEYS.put(CLib.KEY_SHUFFLE, "Shuffle");
        KEYS.put(CLib.KEY_BREAK, "Break");
        KEYS.put(CLib.KEY_PREVIOUS, "Previous");
        KEYS.put(CLib.KEY_DIGITS, "Digits");
        KEYS.put(CLib.KEY_TEEN, "TEEN");
        KEYS.put(CLib.KEY_TWEN, "TWEN");
        KEYS.put(CLib.KEY_DEL_EOL, "Delete EOL");
        KEYS.put(CLib.KEY_DEL_EOS, "Delete EOS");
        KEYS.put(CLib.KEY_INS_LINE, "Insert line");
        KEYS.put(CLib.KEY_DEL_LINE, "Delete line");

        // Keys and buttons
        KEYSANDBUTTONS.putAll(KEYS);
        KEYSANDBUTTONS.putAll(BUTTONS);

        // Relative events
        RELATIVES.put(CLib.REL_X, "X");
        RELATIVES.put(CLib.REL_Y, "Y");
        RELATIVES.put(CLib.REL_Z, "Z");
        RELATIVES.put(CLib.REL_DIAL, "Dial");
        RELATIVES.put(CLib.REL_MISC, "Misc");
        RELATIVES.put(CLib.REL_HWHEEL, "HWheel");
        RELATIVES.put(CLib.REL_WHEEL, "Wheel");

        // Absolutes
        ABSOLUTES.put(CLib.ABS_X, "X");
        ABSOLUTES.put(CLib.ABS_Y, "Y");
        ABSOLUTES.put(CLib.ABS_Z, "Z");
        ABSOLUTES.put(CLib.ABS_RX, "Rx");
        ABSOLUTES.put(CLib.ABS_RY, "Ry");
        ABSOLUTES.put(CLib.ABS_RZ, "Rz");
        ABSOLUTES.put(CLib.ABS_THROTTLE, "Throttle");
        ABSOLUTES.put(CLib.ABS_RUDDER, "Rudder");
        ABSOLUTES.put(CLib.ABS_WHEEL, "Wheel");
        ABSOLUTES.put(CLib.ABS_GAS, "Gas");
        ABSOLUTES.put(CLib.ABS_BRAKE, "Brake");
        ABSOLUTES.put(CLib.ABS_HAT0X, "Hat0X");
        ABSOLUTES.put(CLib.ABS_HAT0Y, "Hat0Y");
        ABSOLUTES.put(CLib.ABS_HAT1X, "Hat1X");
        ABSOLUTES.put(CLib.ABS_HAT1Y, "Hat1Y");
        ABSOLUTES.put(CLib.ABS_HAT2X, "Hat2X");
        ABSOLUTES.put(CLib.ABS_HAT2Y, "Hat2Y");
        ABSOLUTES.put(CLib.ABS_HAT3X, "Hat3X");
        ABSOLUTES.put(CLib.ABS_HAT3Y, "Hat 3Y");
        ABSOLUTES.put(CLib.ABS_PRESSURE, "Pressure");
        ABSOLUTES.put(CLib.ABS_DISTANCE, "Distance");
        ABSOLUTES.put(CLib.ABS_TILT_X, "XTilt");
        ABSOLUTES.put(CLib.ABS_TILT_Y, "YTilt");
        ABSOLUTES.put(CLib.ABS_TOOL_WIDTH, "Tool Width");
        ABSOLUTES.put(CLib.ABS_VOLUME, "Volume");
        ABSOLUTES.put(CLib.ABS_MISC, "Misc");

        // Misc
        MISC.put(CLib.MSC_SERIAL, "Serial");
        MISC.put(CLib.MSC_PULSELED, "Pulseled");
        MISC.put(CLib.MSC_RAW, "RawData");
        MISC.put(CLib.MSC_GESTURE, "Gesture");
        MISC.put(CLib.MSC_SCAN, "ScanCode");

        // LEDS
        LEDS.put(CLib.LED_NUML, "NumLock");
        LEDS.put(CLib.LED_CAPSL, "CapsLock");
        LEDS.put(CLib.LED_SCROLLL, "ScrollLock");
        LEDS.put(CLib.LED_COMPOSE, "Compose");
        LEDS.put(CLib.LED_KANA, "Kana");
        LEDS.put(CLib.LED_SLEEP, "Sleep");
        LEDS.put(CLib.LED_SUSPEND, "Suspend");
        LEDS.put(CLib.LED_MUTE, "Mute");
        LEDS.put(CLib.LED_MISC, "Misc");

        // Repeats
        REPEATS.put(CLib.REP_DELAY, "Delay");
        REPEATS.put(CLib.REP_PERIOD, "Period");

        // Sounds
        SOUNDS.put(CLib.SND_CLICK, "Click");
        SOUNDS.put(CLib.SND_TONE, "Tone");
        SOUNDS.put(CLib.SND_BELL, "Bell");

        // Switches
        SWITCHES.put(CLib.SW_KEYPAD_SLIDE, "KeypadSlide");
        SWITCHES.put(CLib.SW_FRONT_PROXIMITY, "FrontProximity");
        SWITCHES.put(CLib.SW_CAMERA_LENS_COVER, "CameraLensCover");
        SWITCHES.put(CLib.SW_LINEIN_INSERT, "LineInInsert");
        SWITCHES.put(CLib.SW_ROTATE_LOCK, "RotateLock");
        SWITCHES.put(CLib.SW_HEADPHONE_INSERT, "HeadphoneInsert");
        SWITCHES.put(CLib.SW_DOCK, "Dock");
        SWITCHES.put(CLib.SW_MICROPHONE_INSERT, "MicrophoneInsert");
        SWITCHES.put(CLib.SW_RFKILL_ALL, "RFKillAll");
        SWITCHES.put(CLib.SW_LINEOUT_INSERT, "LineoutInsert");
        SWITCHES.put(CLib.SW_LID, "Lid");
        SWITCHES.put(CLib.SW_MAX, "Max");
        SWITCHES.put(CLib.SW_VIDEOOUT_INSERT, "VideoOutInsert");
        SWITCHES.put(CLib.SW_JACK_PHYSICAL_INSERT, "JackPhysicalInsert");
        SWITCHES.put(CLib.SW_CNT, "Cnt");
        SWITCHES.put(CLib.SW_RADIO, "Radio");
        SWITCHES.put(CLib.SW_TABLET_MODE, "TableMode");

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

    final static Logger LOG = LoggerFactory.getLogger(UInputDevice.class);

    private File file;
    private int fd;
    private boolean grabbed;
    private String name;
    private String inputDriverVersion;
    private short[] deviceId = new short[4];
    private boolean open;
    private Map<Type, Set<Integer>> caps = new HashMap<Type, Set<Integer>>();
    private pollfd pollFd;
    private pollfd[] pollFds;

    /**
     * Helper to get what appears to be the first pointer device (e.g. a mouse).
     * If no devices could be found, an exception will be thrown.
     * <p>
     * This method will search for both EV_ABS (absolute) and EV_REL (relative)
     * devices, in that order by default. If you wish to search in a different
     * order, set the system property <b>linuxio.pointer.types</b> to a comma
     * separated string of the type codes (3 for absolute, 2 for relative), or
     * use the {@link Type} constant names EV_ABS and EV_REL.
     * 
     * @return pointer device
     */
    public final static UInputDevice getFirstPointerDevice() throws IOException {
        Set<Integer> codes = null;
        //
        for (String typeName : System.getProperty(SYSPROP_LINUXIO_POINTER_TYPES,
            Type.EV_ABS.nativeType + "," + Type.EV_REL.nativeType).split(",")) {

            // Parse the type name either by its
            Type t = null;
            try {
                t = Type.fromNative(Integer.parseInt(typeName));
            } catch (NumberFormatException nfe) {
                t = Type.valueOf(typeName);
            }

            if (t == null) {
                LOG.warn("Unknown event type in " + SYSPROP_LINUXIO_POINTER_TYPES + " property, '" + typeName + "'");
            } else {
                List<UInputDevice> availableDevices = getAvailableDevices();
                UInputDevice theDevice = null;
                try {
                    for (UInputDevice dev : availableDevices) {
                        LOG.debug(dev.getName());
                        Map<Type, Set<Integer>> capabilties = dev.getCapabilties();
                        codes = capabilties.get(t);
                        if (t == Type.EV_REL) {
                            if (codes == null || (!codes.contains(CLib.REL_X) && !codes.contains(CLib.REL_Y))) {
                                continue;
                            }
                        } else if (t == Type.EV_ABS) {
                            if (codes == null || (!codes.contains(CLib.ABS_X) && !codes.contains(CLib.ABS_Y))) {
                                continue;
                            }
                        }
                        LOG.debug("Device has some " + t + " caps");
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
     * separated string of the type codes (3 for absolute, 2 for relative), or
     * use the {@link Type} constant names EV_ABS and EV_REL.
     * 
     * @return pointer devices
     */
    public final static List<UInputDevice> getAllPointerDevices() throws IOException {
        Set<Integer> codes = null;
        List<UInputDevice> pointerDevices = new ArrayList<>();
        //
        for (String typeName : System.getProperty(SYSPROP_LINUXIO_POINTER_TYPES,
            Type.EV_ABS.nativeType + "," + Type.EV_REL.nativeType).split(",")) {

            // Parse the type name either by its
            Type t = null;
            try {
                t = Type.fromNative(Integer.parseInt(typeName));
            } catch (NumberFormatException nfe) {
                t = Type.valueOf(typeName);
            }

            if (t == null) {
                LOG.warn("Unknown event type in " + SYSPROP_LINUXIO_POINTER_TYPES + " property, '" + typeName + "'");
            } else {
                List<UInputDevice> availableDevices = getAvailableDevices();
                for (UInputDevice dev : availableDevices) {
                    LOG.debug(dev.getName());
                    Map<Type, Set<Integer>> capabilties = dev.getCapabilties();
                    codes = capabilties.get(t);
                    if (t == Type.EV_REL) {
                        if (codes == null || (!codes.contains(CLib.REL_X) && !codes.contains(CLib.REL_Y))) {
                            continue;
                        }
                    } else if (t == Type.EV_ABS) {
                        if (codes == null || (!codes.contains(CLib.ABS_X) && !codes.contains(CLib.ABS_Y))) {
                            continue;
                        }
                    }
                    LOG.debug("Device has some " + t + " caps");
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
        List<String> keyboardDeviceNames = new ArrayList<>();
        List<UInputDevice> availableDevices = getAvailableDevices();
        try {
            for (UInputDevice dev : availableDevices) {
                LOG.info(dev.getName());
                Set<Integer> codes = dev.getCapabilties().get(Type.EV_KEY);
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
                    keyboardDeviceNames.add(dev.getFile().getName());
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
     * Helper to get what appears to be all keyboard devices. If no devices
     * could be found, an exception will be thrown. NOTE, all
     * {@link UInputDevice} returned will be <b>open</b>, so should be closed if
     * you do not intend to carry on using them.
     * 
     * @return keyboard devices
     */
    public final static List<UInputDevice> getAllKeyboardDevices() throws IOException {
        List<UInputDevice> keyboardDevices = new ArrayList<>();
        for (UInputDevice dev : getAvailableDevices()) {
            LOG.info(dev.getName());
            Set<Integer> codes = dev.getCapabilties().get(Type.EV_KEY);
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
                LOG.info(dev.getName());
                Set<Integer> codes = dev.getCapabilties().get(Type.EV_KEY);
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
        final List<UInputDevice> d = new ArrayList<>();
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
                    throw new IOException("The directory '" + dir + "' specified by the system property " + INPUT_DEVICES
                                    + " for uinput devices cannot be read.");
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
     * Open a device given the path for it's file. If the path starts with a
     * slash, that exact path will be used. If it doesn't, and the path relative
     * to the current directory exists, that will be used. Otherwise, it will be
     * assumed to be relative to the default input device directory.
     * <p>
     * For example, all the following are valid :-
     * <ul>
     * <li><b>event0</b> The path /dev/input/event0 will be used</li>
     * <li><b>input/event0</b> The path /dev/input/event0 will be used, assuming
     * the current directory is /dev</li>
     * <li><b>/dev/input/event0</b> The path will be used as is.
     * </ul>
     * 
     * 
     * @param path device file path
     * @throws IOException if device file cannot be opened.
     */
    public UInputDevice(String path) throws IOException {
        this(path.startsWith("/") ? new File(path) : (new File(path).exists() ? new File(path) : new File(
                        getInputDeviceDirectory(), path)));
    }

    /**
     * Open a device given it's file.
     * 
     * @param file device file
     * @throws IOException if device file cannot be opened.
     */
    public UInputDevice(File file) throws IOException {
        this.file = file;
        open(file);

        // Get the name
        byte[] nameBytes = new byte[256];
        CLib.INSTANCE.ioctl(fd, CLib.Macros.EVIOCGNAME(nameBytes.length), nameBytes);
        name = Native.toString(nameBytes);

        // Get the driver version
        IntByReference v = new IntByReference();
        CLib.INSTANCE.ioctl(fd, CLib.Macros.EVIOCGVERSION(), v);
        int vv = v.getValue();
        inputDriverVersion = String.format("%d.%d.%d", vv >> 16, vv >> 8, vv & 0xff);

        // Get the device IDs
        CLib.INSTANCE.ioctl(fd, CLib.Macros.EVIOCGID(), deviceId);

        // Get the bits
        NativeLong[][] bit = new NativeLong[CLib.EV_MAX][NBITS(CLib.KEY_MAX)];
        CLib.INSTANCE.ioctl(fd, CLib.Macros.EVIOCGBIT(0, CLib.EV_MAX), bit[0]);

        int[] abs = new int[5];
        for (int i = 0; i < CLib.EV_MAX; i++) {
            Type t = Type.fromNative(i);
            Set<Integer> c = caps.get(t);
            if (test_bit(i, bit[0])) {
                LOG.debug(String.format("  Event type %d (%s)\n", i, Type.fromNative((short) i)));
                // if (!i) continue;
                CLib.INSTANCE.ioctl(fd, CLib.Macros.EVIOCGBIT(i, CLib.KEY_MAX), bit[i]);
                for (int j = 0; j < CLib.KEY_MAX; j++) {
                    if (test_bit(j, bit[i])) {
                        LOG.debug(String.format("    Event code %d (%s)", j,
                            NAMES.containsKey(t) && NAMES.get(t).containsKey(j) ? NAMES.get(t).get(j) : ""));
                        if (c == null) {
                            c = new HashSet<Integer>();
                            caps.put(t, c);
                        }
                        c.add(j);
                        if (t.equals(Type.EV_ABS)) {
                            CLib.INSTANCE.ioctl(fd, CLib.Macros.EVIOCGABS(j), abs);
                            for (int k = 0; k < 5; k++) {
                                if ((k < 3) || abs[k] > 0) {
                                    LOG.debug(String.format("      %s %6d", ABSVAL[k], abs[k]));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void open(File file) {
        LOG.debug("Opening device " + file + " for " + getClass());
        fd = CLib.INSTANCE.open(file.getAbsolutePath(), CLib.O_RDWR | CLib.O_NOCTTY);
        if (fd == -1) {
            throw new RuntimeException(file + " is not a valid input device for " + getClass());
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
     * Get the valid codes for each event type (used to determine capabilities)
     * 
     * @return capabilities
     */
    public Map<Type, Set<Integer>> getCapabilties() {
        return Collections.unmodifiableMap(caps);
    }

    /**
     * Get the file this input device is accessed by.
     * 
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * Grab the device for exclusive use.
     * 
     * @throws IOException if device cannot be grabbed.
     */
    public void grab() throws IOException {
        if (grabbed) {
            throw new IllegalStateException("Already grabbed " + file + ".");
        }
        LOG.debug("Grabbing " + file);

        // EVIOCGRAB = 0x40044590
        if (CLib.INSTANCE.ioctl(fd, 0x40044590, 1) == -1) {
            // if(CLib.INSTANCE.ioctl(fd, new
            // NativeLong(CLib.Macros.EVIOCGRAB()), 1) == -1) {
            if ("true".equals(System.getProperty("linuxio.exceptionOnGrabFail", "true"))) {
                throw new IOException("Failed to grab.");
            }
        } else {
            LOG.debug("Grabbed " + file);
            grabbed = true;
        }
    }

    /**
     * Release the device from exclusive use.
     * 
     * @throws IOException if device cannot be ungrabbed
     */
    public void ungrab() throws IOException {
        if (!grabbed) {
            throw new IllegalStateException("Not grabbed " + file + ".");
        }
        LOG.debug("Ungrabbing " + file);
        CLib.INSTANCE.ioctl(fd, 0x40044590, 0);
        LOG.debug("Ungrabbed " + file);
        grabbed = false;
    }

    /**
     * Get if the device is currently grabbed for exclusive use.
     * 
     * @return grabbed
     */
    public boolean isGrabbed() {
        return grabbed;
    }

    /**
     * Read the next event, blocking if there are none. <code>null</code> will
     * be returned the device closes.
     * 
     * @return next input event or <code>null</code>
     */
    public Event nextEvent() throws IOException {
        CLib.input_event ev = new CLib.input_event();
        int size = ev.size();
        LOG.debug("Waiting for event (" + size + " bytes)");
        Pointer pointer = ev.getPointer();
        NativeLong read = CLib.INSTANCE.read(fd, pointer, new NativeLong(size));
        // ev.read();
        if (read.longValue() < 1) {
            throw new EOFException();
        } else if (read.longValue() < size) {
            throw new RuntimeException("Error reading input event (read only " + read.longValue() + " of " + size + ").");
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
            if (grabbed) {
                ungrab();
            }
            LOG.debug("Closing device " + file);
            CLib.INSTANCE.close(fd);
            LOG.debug("Closed device " + file);
        } finally {
            open = false;
        }
    }

    @Override
    public String toString() {
        return "UInputDevice [file=" + file + ", grabbed=" + grabbed + ", name=" + name + ", inputDriverVersion="
                        + inputDriverVersion + ", deviceId=" + Arrays.toString(deviceId) + "]";
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
        LOG.info("Mouse: " + UInputDevice.getFirstPointerDevice());
        LOG.info("Keyboard: " + UInputDevice.getFirstKeyboardDevice());
    }

    public boolean isOpen() {
        return open;
    }

    public int getFD() {
        return fd;
    }
}
