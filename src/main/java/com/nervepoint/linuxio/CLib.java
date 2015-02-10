package com.nervepoint.linuxio;

/*
 * LinuxIO4J - A Java library for working with Linux I/O systems.
 * 
 * Copyright (C) 2015 - Nervepoint Technologies
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;

/**
 * This is the JNA interface to the native libraries required for interaction
 * with UInput and the Framebuffer.
 */
public interface CLib extends com.sun.jna.Library {

	public static final String JNA_LIBRARY_NAME = (com.sun.jna.Platform
			.isWindows() ? "msvcrt" : "c");
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary
			.getInstance(CLib.JNA_LIBRARY_NAME);
	public static final CLib INSTANCE = (CLib) Native.loadLibrary(
			CLib.JNA_LIBRARY_NAME, CLib.class);

	public static final int PROT_WRITE = (int) 0x2;
	public static final int PROT_READ = (int) 0x1;
	public static final int MAP_SHARED = (int) 0x01;

	public static final int BTN_0 = (int) 0x100;
	public static final int BTN_1 = (int) 0x101;
	public static final int BTN_2 = (int) 0x102;
	public static final int BTN_3 = (int) 0x103;
	public static final int BTN_4 = (int) 0x104;
	public static final int BTN_5 = (int) 0x105;
	public static final int BTN_6 = (int) 0x106;
	public static final int BTN_7 = (int) 0x107;
	public static final int BTN_8 = (int) 0x108;
	public static final int BTN_9 = (int) 0x109;
	public static final int BTN_A = (int) 0x130;
	public static final int BTN_B = (int) 0x131;
	public static final int BTN_BACK = (int) 0x116;
	public static final int BTN_BASE = (int) 0x126;
	public static final int BTN_BASE2 = (int) 0x127;
	public static final int BTN_BASE3 = (int) 0x128;
	public static final int BTN_BASE4 = (int) 0x129;
	public static final int BTN_BASE5 = (int) 0x12a;
	public static final int BTN_BASE6 = (int) 0x12b;
	public static final int BTN_C = (int) 0x132;
	public static final int BTN_DEAD = (int) 0x12f;
	public static final int BTN_DIGI = (int) 0x140;
	public static final int BTN_DPAD_DOWN = (int) 0x221;
	public static final int BTN_DPAD_LEFT = (int) 0x222;
	public static final int BTN_DPAD_RIGHT = (int) 0x223;
	public static final int BTN_DPAD_UP = (int) 0x220;
	public static final int BTN_EAST = (int) 0x131;
	public static final int BTN_EXTRA = (int) 0x114;
	public static final int BTN_FORWARD = (int) 0x115;
	public static final int BTN_GAMEPAD = (int) 0x130;
	public static final int BTN_GEAR_DOWN = (int) 0x150;
	public static final int BTN_GEAR_UP = (int) 0x151;
	public static final int BTN_JOYSTICK = (int) 0x120;
	public static final int BTN_LEFT = (int) 0x110;
	public static final int BTN_MIDDLE = (int) 0x112;
	public static final int BTN_MISC = (int) 0x100;
	public static final int BTN_MODE = (int) 0x13c;
	public static final int BTN_MOUSE = (int) 0x110;
	public static final int BTN_NORTH = (int) 0x133;
	public static final int BTN_PINKIE = (int) 0x125;
	public static final int BTN_RIGHT = (int) 0x111;
	public static final int BTN_SELECT = (int) 0x13a;
	public static final int BTN_SIDE = (int) 0x113;
	public static final int BTN_SOUTH = (int) 0x130;
	public static final int BTN_START = (int) 0x13b;
	public static final int BTN_STYLUS = (int) 0x14b;
	public static final int BTN_STYLUS2 = (int) 0x14c;
	public static final int BTN_TASK = (int) 0x117;
	public static final int BTN_THUMB = (int) 0x121;
	public static final int BTN_THUMB2 = (int) 0x122;
	public static final int BTN_THUMBL = (int) 0x13d;
	public static final int BTN_THUMBR = (int) 0x13e;
	public static final int BTN_TL = (int) 0x136;
	public static final int BTN_TL2 = (int) 0x138;
	public static final int BTN_TOOL_AIRBRUSH = (int) 0x144;
	public static final int BTN_TOOL_BRUSH = (int) 0x142;
	public static final int BTN_TOOL_DOUBLETAP = (int) 0x14d;
	public static final int BTN_TOOL_FINGER = (int) 0x145;
	public static final int BTN_TOOL_LENS = (int) 0x147;
	public static final int BTN_TOOL_MOUSE = (int) 0x146;
	public static final int BTN_TOOL_PEN = (int) 0x140;
	public static final int BTN_TOOL_PENCIL = (int) 0x143;
	public static final int BTN_TOOL_QUADTAP = (int) 0x14f;
	public static final int BTN_TOOL_QUINTTAP = (int) 0x148;
	public static final int BTN_TOOL_RUBBER = (int) 0x141;
	public static final int BTN_TOOL_TRIPLETAP = (int) 0x14e;
	public static final int BTN_TOP = (int) 0x123;
	public static final int BTN_TOP2 = (int) 0x124;
	public static final int BTN_TOUCH = (int) 0x14a;
	public static final int BTN_TR = (int) 0x137;
	public static final int BTN_TR2 = (int) 0x139;
	public static final int BTN_TRIGGER = (int) 0x120;
	public static final int BTN_TRIGGER_HAPPY = (int) 0x2c0;
	public static final int BTN_TRIGGER_HAPPY1 = (int) 0x2c0;
	public static final int BTN_TRIGGER_HAPPY10 = (int) 0x2c9;
	public static final int BTN_TRIGGER_HAPPY11 = (int) 0x2ca;
	public static final int BTN_TRIGGER_HAPPY12 = (int) 0x2cb;
	public static final int BTN_TRIGGER_HAPPY13 = (int) 0x2cc;
	public static final int BTN_TRIGGER_HAPPY14 = (int) 0x2cd;
	public static final int BTN_TRIGGER_HAPPY15 = (int) 0x2ce;
	public static final int BTN_TRIGGER_HAPPY16 = (int) 0x2cf;
	public static final int BTN_TRIGGER_HAPPY17 = (int) 0x2d0;
	public static final int BTN_TRIGGER_HAPPY18 = (int) 0x2d1;
	public static final int BTN_TRIGGER_HAPPY19 = (int) 0x2d2;
	public static final int BTN_TRIGGER_HAPPY2 = (int) 0x2c1;
	public static final int BTN_TRIGGER_HAPPY20 = (int) 0x2d3;
	public static final int BTN_TRIGGER_HAPPY21 = (int) 0x2d4;
	public static final int BTN_TRIGGER_HAPPY22 = (int) 0x2d5;
	public static final int BTN_TRIGGER_HAPPY23 = (int) 0x2d6;
	public static final int BTN_TRIGGER_HAPPY24 = (int) 0x2d7;
	public static final int BTN_TRIGGER_HAPPY25 = (int) 0x2d8;
	public static final int BTN_TRIGGER_HAPPY26 = (int) 0x2d9;
	public static final int BTN_TRIGGER_HAPPY27 = (int) 0x2da;
	public static final int BTN_TRIGGER_HAPPY28 = (int) 0x2db;
	public static final int BTN_TRIGGER_HAPPY29 = (int) 0x2dc;
	public static final int BTN_TRIGGER_HAPPY3 = (int) 0x2c2;
	public static final int BTN_TRIGGER_HAPPY30 = (int) 0x2dd;
	public static final int BTN_TRIGGER_HAPPY31 = (int) 0x2de;
	public static final int BTN_TRIGGER_HAPPY32 = (int) 0x2df;
	public static final int BTN_TRIGGER_HAPPY33 = (int) 0x2e0;
	public static final int BTN_TRIGGER_HAPPY34 = (int) 0x2e1;
	public static final int BTN_TRIGGER_HAPPY35 = (int) 0x2e2;
	public static final int BTN_TRIGGER_HAPPY36 = (int) 0x2e3;
	public static final int BTN_TRIGGER_HAPPY37 = (int) 0x2e4;
	public static final int BTN_TRIGGER_HAPPY38 = (int) 0x2e5;
	public static final int BTN_TRIGGER_HAPPY39 = (int) 0x2e6;
	public static final int BTN_TRIGGER_HAPPY4 = (int) 0x2c3;
	public static final int BTN_TRIGGER_HAPPY40 = (int) 0x2e7;
	public static final int BTN_TRIGGER_HAPPY5 = (int) 0x2c4;
	public static final int BTN_TRIGGER_HAPPY6 = (int) 0x2c5;
	public static final int BTN_TRIGGER_HAPPY7 = (int) 0x2c6;
	/** <i>native declaration : bits/termios.h</i> */
	// public static final int CMSPAR = (int)10000000000;
	public static final int BTN_TRIGGER_HAPPY8 = (int) 0x2c7;
	public static final int BTN_TRIGGER_HAPPY9 = (int) 0x2c8;
	public static final int BTN_WEST = (int) 0x134;
	public static final int BTN_WHEEL = (int) 0x150;
	public static final int BTN_X = (int) 0x133;
	public static final int BTN_Y = (int) 0x134;
	public static final int BTN_Z = (int) 0x135;
	public static final int KEY_0 = (int) 11;
	public static final int KEY_1 = (int) 2;
	public static final int KEY_102ND = (int) 86;
	public static final int KEY_10CHANNELSDOWN = (int) 0x1b9;
	public static final int KEY_10CHANNELSUP = (int) 0x1b8;
	public static final int KEY_2 = (int) 3;
	public static final int KEY_3 = (int) 4;
	public static final int KEY_4 = (int) 5;
	public static final int KEY_5 = (int) 6;
	public static final int KEY_6 = (int) 7;
	public static final int KEY_7 = (int) 8;
	public static final int KEY_8 = (int) 9;
	public static final int KEY_9 = (int) 10;
	public static final int KEY_A = (int) 30;
	public static final int KEY_AB = (int) 0x196;
	public static final int KEY_ADDRESSBOOK = (int) 0x1ad;
	public static final int KEY_AGAIN = (int) 129;
	public static final int KEY_ALTERASE = (int) 222;
	public static final int KEY_ANGLE = (int) 0x173;
	public static final int KEY_APOSTROPHE = (int) 40;
	public static final int KEY_ARCHIVE = (int) 0x169;
	public static final int KEY_ATTENDANT_OFF = (int) 0x21c;
	public static final int KEY_ATTENDANT_ON = (int) 0x21b;
	public static final int KEY_ATTENDANT_TOGGLE = (int) 0x21d;
	public static final int KEY_AUDIO = (int) 0x188;
	public static final int KEY_AUX = (int) 0x186;
	public static final int KEY_B = (int) 48;
	public static final int KEY_BACK = (int) 158;
	public static final int KEY_BACKSLASH = (int) 43;
	public static final int KEY_BACKSPACE = (int) 14;
	public static final int KEY_BASSBOOST = (int) 209;
	public static final int KEY_BATTERY = (int) 236;
	public static final int KEY_BLUE = (int) 0x191;
	public static final int KEY_BLUETOOTH = (int) 237;
	public static final int KEY_BOOKMARKS = (int) 156;
	public static final int KEY_BREAK = (int) 0x19b;
	public static final int KEY_BRIGHTNESS_CYCLE = (int) 243;
	public static final int KEY_BRIGHTNESS_ZERO = (int) 244;
	public static final int KEY_BRIGHTNESSDOWN = (int) 224;
	public static final int KEY_BRIGHTNESSUP = (int) 225;
	public static final int KEY_BRL_DOT1 = (int) 0x1f1;
	public static final int KEY_BRL_DOT10 = (int) 0x1fa;
	public static final int KEY_BRL_DOT2 = (int) 0x1f2;
	public static final int KEY_BRL_DOT3 = (int) 0x1f3;
	public static final int KEY_BRL_DOT4 = (int) 0x1f4;
	public static final int KEY_BRL_DOT5 = (int) 0x1f5;
	public static final int KEY_BRL_DOT6 = (int) 0x1f6;
	public static final int KEY_BRL_DOT7 = (int) 0x1f7;
	public static final int KEY_BRL_DOT8 = (int) 0x1f8;
	public static final int KEY_BRL_DOT9 = (int) 0x1f9;
	public static final int KEY_C = (int) 46;
	public static final int KEY_CALC = (int) 140;
	public static final int KEY_CALENDAR = (int) 0x18d;
	public static final int KEY_CAMERA = (int) 212;
	public static final int KEY_CAMERA_DOWN = (int) 0x218;
	public static final int KEY_CAMERA_FOCUS = (int) 0x210;
	public static final int KEY_CAMERA_LEFT = (int) 0x219;
	public static final int KEY_CAMERA_RIGHT = (int) 0x21a;
	public static final int KEY_CAMERA_UP = (int) 0x217;
	public static final int KEY_CAMERA_ZOOMIN = (int) 0x215;
	public static final int KEY_CAMERA_ZOOMOUT = (int) 0x216;
	public static final int KEY_CANCEL = (int) 223;
	public static final int KEY_CAPSLOCK = (int) 58;
	public static final int KEY_CD = (int) 0x17f;
	public static final int KEY_CHANNEL = (int) 0x16b;
	public static final int KEY_CHANNELDOWN = (int) 0x193;
	public static final int KEY_CHANNELUP = (int) 0x192;
	public static final int KEY_CHAT = (int) 216;
	public static final int KEY_CLEAR = (int) 0x163;
	public static final int KEY_CLOSE = (int) 206;
	public static final int KEY_CLOSECD = (int) 160;
	public static final int KEY_CNT = (int) (0x2ff + 1);
	public static final int KEY_COFFEE = (int) 152;
	public static final int KEY_COMMA = (int) 51;
	public static final int KEY_COMPOSE = (int) 127;
	public static final int KEY_COMPUTER = (int) 157;
	public static final int KEY_CONFIG = (int) 171;
	public static final int KEY_CONNECT = (int) 218;
	public static final int KEY_CONTEXT_MENU = (int) 0x1b6;
	public static final int KEY_COPY = (int) 133;
	public static final int KEY_CUT = (int) 137;
	public static final int KEY_CYCLEWINDOWS = (int) 154;
	public static final int KEY_D = (int) 32;
	public static final int KEY_DASHBOARD = (int) 204;
	public static final int KEY_DATABASE = (int) 0x1aa;
	public static final int KEY_DEL_EOL = (int) 0x1c0;
	public static final int KEY_DEL_EOS = (int) 0x1c1;
	public static final int KEY_DEL_LINE = (int) 0x1c3;
	public static final int KEY_DELETE = (int) 111;
	public static final int KEY_DELETEFILE = (int) 146;
	public static final int KEY_DIGITS = (int) 0x19d;
	public static final int KEY_DIRECTION = (int) 153;
	public static final int KEY_DIRECTORY = (int) 0x18a;
	public static final int KEY_DISPLAY_OFF = (int) 245;
	public static final int KEY_DISPLAYTOGGLE = (int) 0x1af;
	public static final int KEY_DOCUMENTS = (int) 235;
	public static final int KEY_DOLLAR = (int) 0x1b2;
	public static final int KEY_DOT = (int) 52;
	public static final int KEY_DOWN = (int) 108;
	public static final int KEY_DVD = (int) 0x185;
	public static final int KEY_E = (int) 18;
	public static final int KEY_EDIT = (int) 176;
	public static final int KEY_EDITOR = (int) 0x1a6;
	public static final int KEY_EJECTCD = (int) 161;
	public static final int KEY_EJECTCLOSECD = (int) 162;
	public static final int KEY_EMAIL = (int) 215;
	public static final int KEY_END = (int) 107;
	public static final int KEY_ENTER = (int) 28;
	public static final int KEY_EPG = (int) 0x16d;
	public static final int KEY_EQUAL = (int) 13;
	public static final int KEY_ESC = (int) 1;
	public static final int KEY_EURO = (int) 0x1b3;
	public static final int KEY_EXIT = (int) 174;
	public static final int KEY_F = (int) 33;
	public static final int KEY_F1 = (int) 59;
	public static final int KEY_F10 = (int) 68;
	public static final int KEY_F11 = (int) 87;
	public static final int KEY_F12 = (int) 88;
	public static final int KEY_F13 = (int) 183;
	public static final int KEY_F14 = (int) 184;
	public static final int KEY_F15 = (int) 185;
	public static final int KEY_F16 = (int) 186;
	public static final int KEY_F17 = (int) 187;
	public static final int KEY_F18 = (int) 188;
	public static final int KEY_F19 = (int) 189;
	public static final int KEY_F2 = (int) 60;
	public static final int KEY_F20 = (int) 190;
	public static final int KEY_F21 = (int) 191;
	public static final int KEY_F22 = (int) 192;
	public static final int KEY_F23 = (int) 193;
	public static final int KEY_F24 = (int) 194;
	public static final int KEY_F3 = (int) 61;
	public static final int KEY_F4 = (int) 62;
	public static final int KEY_F5 = (int) 63;
	public static final int KEY_F6 = (int) 64;
	public static final int KEY_F7 = (int) 65;
	public static final int KEY_F8 = (int) 66;
	public static final int KEY_F9 = (int) 67;
	public static final int KEY_FASTFORWARD = (int) 208;
	public static final int KEY_FAVORITES = (int) 0x16c;
	public static final int KEY_FILE = (int) 144;
	public static final int KEY_FINANCE = (int) 219;
	public static final int KEY_FIND = (int) 136;
	public static final int KEY_FIRST = (int) 0x194;
	public static final int KEY_FN = (int) 0x1d0;
	public static final int KEY_FN_1 = (int) 0x1de;
	public static final int KEY_FN_2 = (int) 0x1df;
	public static final int KEY_FN_B = (int) 0x1e4;
	public static final int KEY_FN_D = (int) 0x1e0;
	public static final int KEY_FN_E = (int) 0x1e1;
	public static final int KEY_FN_ESC = (int) 0x1d1;
	public static final int KEY_FN_F = (int) 0x1e2;
	public static final int KEY_FN_F1 = (int) 0x1d2;
	public static final int KEY_FN_F10 = (int) 0x1db;
	public static final int KEY_FN_F11 = (int) 0x1dc;
	public static final int KEY_FN_F12 = (int) 0x1dd;
	public static final int KEY_FN_F2 = (int) 0x1d3;
	public static final int KEY_FN_F3 = (int) 0x1d4;
	public static final int KEY_FN_F4 = (int) 0x1d5;
	public static final int KEY_FN_F5 = (int) 0x1d6;
	public static final int KEY_FN_F6 = (int) 0x1d7;
	public static final int KEY_FN_F7 = (int) 0x1d8;
	public static final int KEY_FN_F8 = (int) 0x1d9;
	public static final int KEY_FN_F9 = (int) 0x1da;
	public static final int KEY_FN_S = (int) 0x1e3;
	public static final int KEY_FORWARD = (int) 159;
	public static final int KEY_FORWARDMAIL = (int) 233;
	public static final int KEY_FRAMEBACK = (int) 0x1b4;
	public static final int KEY_FRAMEFORWARD = (int) 0x1b5;
	public static final int KEY_FRONT = (int) 132;
	public static final int KEY_G = (int) 34;
	public static final int KEY_GAMES = (int) 0x1a1;
	public static final int KEY_GOTO = (int) 0x162;
	public static final int KEY_GRAPHICSEDITOR = (int) 0x1a8;
	public static final int KEY_GRAVE = (int) 41;
	public static final int KEY_GREEN = (int) 0x18f;
	public static final int KEY_H = (int) 35;
	public static final int KEY_HANGEUL = (int) 122;
	public static final int KEY_HANGUEL = (int) 122;
	public static final int KEY_HANJA = (int) 123;
	public static final int KEY_HELP = (int) 138;
	public static final int KEY_HENKAN = (int) 92;
	public static final int KEY_HIRAGANA = (int) 91;
	public static final int KEY_HOME = (int) 102;
	public static final int KEY_HOMEPAGE = (int) 172;
	public static final int KEY_HP = (int) 211;
	public static final int KEY_I = (int) 23;
	public static final int KEY_IMAGES = (int) 0x1ba;
	public static final int KEY_INFO = (int) 0x166;
	public static final int KEY_INS_LINE = (int) 0x1c2;
	public static final int KEY_INSERT = (int) 110;
	public static final int KEY_ISO = (int) 170;
	public static final int KEY_J = (int) 36;
	public static final int KEY_K = (int) 37;
	public static final int KEY_KATAKANA = (int) 90;
	public static final int KEY_KATAKANAHIRAGANA = (int) 93;
	public static final int KEY_KBDILLUMDOWN = (int) 229;
	public static final int KEY_KBDILLUMTOGGLE = (int) 228;
	public static final int KEY_KBDILLUMUP = (int) 230;
	public static final int KEY_KEYBOARD = (int) 0x176;
	public static final int KEY_KP0 = (int) 82;
	public static final int KEY_KP1 = (int) 79;
	public static final int KEY_KP2 = (int) 80;
	public static final int KEY_KP3 = (int) 81;
	public static final int KEY_KP4 = (int) 75;
	public static final int KEY_KP5 = (int) 76;
	public static final int KEY_KP6 = (int) 77;
	public static final int KEY_KP7 = (int) 71;
	public static final int KEY_KP8 = (int) 72;
	public static final int KEY_KP9 = (int) 73;
	public static final int KEY_KPASTERISK = (int) 55;
	public static final int KEY_KPCOMMA = (int) 121;
	public static final int KEY_KPDOT = (int) 83;
	public static final int KEY_KPENTER = (int) 96;
	public static final int KEY_KPEQUAL = (int) 117;
	public static final int KEY_KPJPCOMMA = (int) 95;
	public static final int KEY_KPLEFTPAREN = (int) 179;
	public static final int KEY_KPMINUS = (int) 74;
	public static final int KEY_KPPLUS = (int) 78;
	public static final int KEY_KPPLUSMINUS = (int) 118;
	public static final int KEY_KPRIGHTPAREN = (int) 180;
	public static final int KEY_KPSLASH = (int) 98;
	public static final int KEY_L = (int) 38;
	public static final int KEY_LANGUAGE = (int) 0x170;
	public static final int KEY_LAST = (int) 0x195;
	public static final int KEY_LEFT = (int) 105;
	public static final int KEY_LEFTALT = (int) 56;
	public static final int KEY_LEFTBRACE = (int) 26;
	public static final int KEY_LEFTCTRL = (int) 29;
	public static final int KEY_LEFTMETA = (int) 125;
	public static final int KEY_LEFTSHIFT = (int) 42;
	public static final int KEY_LIGHTS_TOGGLE = (int) 0x21e;
	public static final int KEY_LINEFEED = (int) 101;
	public static final int KEY_LIST = (int) 0x18b;
	public static final int KEY_LOGOFF = (int) 0x1b1;
	public static final int KEY_M = (int) 50;
	public static final int KEY_MACRO = (int) 112;
	public static final int KEY_MAIL = (int) 155;
	public static final int KEY_MAX = (int) 0x2ff;
	public static final int KEY_MEDIA = (int) 226;
	public static final int KEY_MEDIA_REPEAT = (int) 0x1b7;
	public static final int KEY_MEMO = (int) 0x18c;
	public static final int KEY_MENU = (int) 139;
	public static final int KEY_MESSENGER = (int) 0x1ae;
	public static final int KEY_MHP = (int) 0x16f;
	public static final int KEY_MICMUTE = (int) 248;
	public static final int KEY_MIN_INTERESTING = (int) 113;
	public static final int KEY_MINUS = (int) 12;
	public static final int KEY_MODE = (int) 0x175;
	public static final int KEY_MOVE = (int) 175;
	public static final int KEY_MP3 = (int) 0x187;
	public static final int KEY_MSDOS = (int) 151;
	public static final int KEY_MUHENKAN = (int) 94;
	public static final int KEY_MUTE = (int) 113;
	public static final int KEY_N = (int) 49;
	public static final int KEY_NEW = (int) 181;
	public static final int KEY_NEWS = (int) 0x1ab;
	public static final int KEY_NEXT = (int) 0x197;
	public static final int KEY_NEXTSONG = (int) 163;
	public static final int KEY_NUMERIC_0 = (int) 0x200;
	public static final int KEY_NUMERIC_1 = (int) 0x201;
	public static final int KEY_NUMERIC_2 = (int) 0x202;
	public static final int KEY_NUMERIC_3 = (int) 0x203;
	public static final int KEY_NUMERIC_4 = (int) 0x204;
	public static final int KEY_NUMERIC_5 = (int) 0x205;
	public static final int KEY_NUMERIC_6 = (int) 0x206;
	public static final int KEY_NUMERIC_7 = (int) 0x207;
	public static final int KEY_NUMERIC_8 = (int) 0x208;
	public static final int KEY_NUMERIC_9 = (int) 0x209;
	public static final int KEY_NUMERIC_POUND = (int) 0x20b;
	public static final int KEY_NUMERIC_STAR = (int) 0x20a;
	public static final int KEY_NUMLOCK = (int) 69;
	public static final int KEY_O = (int) 24;
	public static final int KEY_OK = (int) 0x160;
	public static final int KEY_OPEN = (int) 134;
	public static final int KEY_OPTION = (int) 0x165;
	public static final int KEY_P = (int) 25;
	public static final int KEY_PAGEDOWN = (int) 109;
	public static final int KEY_PAGEUP = (int) 104;
	public static final int KEY_PASTE = (int) 135;
	public static final int KEY_PAUSE = (int) 119;
	public static final int KEY_PAUSECD = (int) 201;
	public static final int KEY_PC = (int) 0x178;
	public static final int KEY_PHONE = (int) 169;
	public static final int KEY_PLAY = (int) 207;
	public static final int KEY_PLAYCD = (int) 200;
	public static final int KEY_PLAYER = (int) 0x183;
	public static final int KEY_PLAYPAUSE = (int) 164;
	public static final int KEY_POWER = (int) 116;
	public static final int KEY_POWER2 = (int) 0x164;
	public static final int KEY_PRESENTATION = (int) 0x1a9;
	public static final int KEY_PREVIOUS = (int) 0x19c;
	public static final int KEY_PREVIOUSSONG = (int) 165;
	public static final int KEY_PRINT = (int) 210;
	public static final int KEY_PROG1 = (int) 148;
	public static final int KEY_PROG2 = (int) 149;
	public static final int KEY_PROG3 = (int) 202;
	public static final int KEY_PROG4 = (int) 203;
	public static final int KEY_PROGRAM = (int) 0x16a;
	public static final int KEY_PROPS = (int) 130;
	public static final int KEY_PVR = (int) 0x16e;
	public static final int KEY_Q = (int) 16;
	public static final int KEY_QUESTION = (int) 214;
	public static final int KEY_R = (int) 19;
	public static final int KEY_RADIO = (int) 0x181;
	public static final int KEY_RECORD = (int) 167;
	public static final int KEY_RED = (int) 0x18e;
	public static final int KEY_REDO = (int) 182;
	public static final int KEY_REFRESH = (int) 173;
	public static final int KEY_REPLY = (int) 232;
	public static final int KEY_RESERVED = (int) 0;
	public static final int KEY_RESTART = (int) 0x198;
	public static final int KEY_REWIND = (int) 168;
	public static final int KEY_RFKILL = (int) 247;
	public static final int KEY_RIGHT = (int) 106;
	public static final int KEY_RIGHTALT = (int) 100;
	public static final int KEY_RIGHTBRACE = (int) 27;
	public static final int KEY_RIGHTCTRL = (int) 97;
	public static final int KEY_RIGHTMETA = (int) 126;
	public static final int KEY_RIGHTSHIFT = (int) 54;
	public static final int KEY_RO = (int) 89;
	public static final int KEY_S = (int) 31;
	public static final int KEY_SAT = (int) 0x17d;
	public static final int KEY_SAT2 = (int) 0x17e;
	public static final int KEY_SAVE = (int) 234;
	public static final int KEY_SCALE = (int) 120;
	public static final int KEY_SCREEN = (int) 0x177;
	public static final int KEY_SCREENLOCK = (int) 152;
	public static final int KEY_SCROLLDOWN = (int) 178;
	public static final int KEY_SCROLLLOCK = (int) 70;
	public static final int KEY_SCROLLUP = (int) 177;
	public static final int KEY_SEARCH = (int) 217;
	public static final int KEY_SELECT = (int) 0x161;
	public static final int KEY_SEMICOLON = (int) 39;
	public static final int KEY_SEND = (int) 231;
	public static final int KEY_SENDFILE = (int) 145;
	public static final int KEY_SETUP = (int) 141;
	public static final int KEY_SHOP = (int) 221;
	public static final int KEY_SHUFFLE = (int) 0x19a;
	public static final int KEY_SLASH = (int) 53;
	public static final int KEY_SLEEP = (int) 142;
	public static final int KEY_SLOW = (int) 0x199;
	public static final int KEY_SOUND = (int) 213;
	public static final int KEY_SPACE = (int) 57;
	public static final int KEY_SPELLCHECK = (int) 0x1b0;
	public static final int KEY_SPORT = (int) 220;
	public static final int KEY_SPREADSHEET = (int) 0x1a7;
	public static final int KEY_STOP = (int) 128;
	public static final int KEY_STOPCD = (int) 166;
	public static final int KEY_SUBTITLE = (int) 0x172;
	public static final int KEY_SUSPEND = (int) 205;
	public static final int KEY_SWITCHVIDEOMODE = (int) 227;
	public static final int KEY_SYSRQ = (int) 99;
	public static final int KEY_T = (int) 20;
	public static final int KEY_TAB = (int) 15;
	public static final int KEY_TAPE = (int) 0x180;
	public static final int KEY_TEEN = (int) 0x19e;
	public static final int KEY_TEXT = (int) 0x184;
	public static final int KEY_TIME = (int) 0x167;
	public static final int KEY_TITLE = (int) 0x171;
	public static final int KEY_TOUCHPAD_OFF = (int) 0x214;
	public static final int KEY_TOUCHPAD_ON = (int) 0x213;
	public static final int KEY_TOUCHPAD_TOGGLE = (int) 0x212;
	public static final int KEY_TUNER = (int) 0x182;
	public static final int KEY_TV = (int) 0x179;
	public static final int KEY_TV2 = (int) 0x17a;
	public static final int KEY_TWEN = (int) 0x19f;
	public static final int KEY_U = (int) 22;
	public static final int KEY_UNDO = (int) 131;
	public static final int KEY_UNKNOWN = (int) 240;
	public static final int KEY_UP = (int) 103;
	public static final int KEY_UWB = (int) 239;
	public static final int KEY_V = (int) 47;
	public static final int KEY_VCR = (int) 0x17b;
	public static final int KEY_VCR2 = (int) 0x17c;
	public static final int KEY_VENDOR = (int) 0x168;
	public static final int KEY_VIDEO = (int) 0x189;
	public static final int KEY_VIDEO_NEXT = (int) 241;
	public static final int KEY_VIDEO_PREV = (int) 242;
	public static final int KEY_VIDEOPHONE = (int) 0x1a0;
	public static final int KEY_VOICEMAIL = (int) 0x1ac;
	public static final int KEY_VOLUMEDOWN = (int) 114;
	public static final int KEY_VOLUMEUP = (int) 115;
	public static final int KEY_W = (int) 17;
	public static final int KEY_WAKEUP = (int) 143;
	public static final int KEY_WIMAX = (int) 246;
	public static final int KEY_WLAN = (int) 238;
	public static final int KEY_WORDPROCESSOR = (int) 0x1a5;
	public static final int KEY_WPS_BUTTON = (int) 0x211;
	public static final int KEY_WWW = (int) 150;
	public static final int KEY_X = (int) 45;
	public static final int KEY_XFER = (int) 147;
	public static final int KEY_Y = (int) 21;
	public static final int KEY_YELLOW = (int) 0x190;
	public static final int KEY_YEN = (int) 124;
	public static final int KEY_Z = (int) 44;
	public static final int KEY_ZENKAKUHANKAKU = (int) 85;
	public static final int KEY_ZOOM = (int) 0x174;
	public static final int KEY_ZOOMIN = (int) 0x1a2;
	public static final int KEY_ZOOMOUT = (int) 0x1a3;
	public static final int KEY_ZOOMRESET = (int) 0x1a4;
	public static final int ABS_BRAKE = (int) 0x0a;
	public static final int ABS_CNT = (int) (0x3f + 1);
	public static final int ABS_DISTANCE = (int) 0x19;
	public static final int ABS_GAS = (int) 0x09;
	public static final int ABS_HAT0X = (int) 0x10;
	public static final int ABS_HAT0Y = (int) 0x11;
	public static final int ABS_HAT1X = (int) 0x12;
	public static final int ABS_HAT1Y = (int) 0x13;
	public static final int ABS_HAT2X = (int) 0x14;
	public static final int ABS_HAT2Y = (int) 0x15;
	public static final int ABS_HAT3X = (int) 0x16;
	public static final int ABS_HAT3Y = (int) 0x17;
	public static final int ABS_MAX = (int) 0x3f;
	public static final int ABS_MISC = (int) 0x28;
	public static final int ABS_MT_BLOB_ID = (int) 0x38;
	public static final int ABS_MT_DISTANCE = (int) 0x3b;
	public static final int ABS_MT_ORIENTATION = (int) 0x34;
	public static final int ABS_MT_POSITION_X = (int) 0x35;
	public static final int ABS_MT_POSITION_Y = (int) 0x36;
	public static final int ABS_MT_PRESSURE = (int) 0x3a;
	public static final int ABS_MT_SLOT = (int) 0x2f;
	public static final int ABS_MT_TOOL_TYPE = (int) 0x37;
	public static final int ABS_MT_TOOL_X = (int) 0x3c;
	public static final int ABS_MT_TOOL_Y = (int) 0x3d;
	public static final int ABS_MT_TOUCH_MAJOR = (int) 0x30;
	public static final int ABS_MT_TOUCH_MINOR = (int) 0x31;
	public static final int ABS_MT_TRACKING_ID = (int) 0x39;
	public static final int ABS_MT_WIDTH_MAJOR = (int) 0x32;
	public static final int ABS_MT_WIDTH_MINOR = (int) 0x33;
	public static final int ABS_PRESSURE = (int) 0x18;
	public static final int ABS_RUDDER = (int) 0x07;
	public static final int ABS_RX = (int) 0x03;
	public static final int ABS_RY = (int) 0x04;
	public static final int ABS_RZ = (int) 0x05;
	public static final int ABS_THROTTLE = (int) 0x06;
	public static final int ABS_TILT_X = (int) 0x1a;
	public static final int ABS_TILT_Y = (int) 0x1b;
	public static final int ABS_TOOL_WIDTH = (int) 0x1c;
	public static final int ABS_VOLUME = (int) 0x20;
	public static final int ABS_WHEEL = (int) 0x08;
	public static final int ABS_X = (int) 0x00;
	public static final int ABS_Y = (int) 0x01;
	public static final int ABS_Z = (int) 0x02;
	public static final int EV_ABS = (int) 0x03;
	public static final int EV_CNT = (int) (0x1f + 1);
	public static final int EV_FF = (int) 0x15;
	public static final int EV_FF_STATUS = (int) 0x17;
	public static final int EV_KEY = (int) 0x01;
	public static final int EV_LED = (int) 0x11;
	public static final int EV_MAX = (int) 0x1f;
	public static final int EV_MSC = (int) 0x04;
	public static final int EV_PWR = (int) 0x16;
	public static final int EV_REL = (int) 0x02;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int EV_REP = (int) 0x14;
	public static final int EV_SND = (int) 0x12;
	public static final int EV_SW = (int) 0x05;
	public static final int EV_SYN = (int) 0x00;
	public static final int EV_VERSION = (int) 0x010001;
	public static final int EVIOCGKEYCODE = (int) (((2) << (((0 + 8) + 8) + 14))
			| (('E') << (0 + 8)) | ((0x04) << 0));
	public static final int EVIOCGREP = (int) (((2) << (((0 + 8) + 8) + 14))
			| (('E') << (0 + 8)) | ((0x03) << 0));
	public static final int EVIOCSKEYCODE = (int) (((1) << (((0 + 8) + 8) + 14))
			| (('E') << (0 + 8)) | ((0x04) << 0));
	public static final int EVIOCSREP = (int) (((1) << (((0 + 8) + 8) + 14))
			| (('E') << (0 + 8)) | ((0x03) << 0));
	public static final int LED_CAPSL = (int) 0x01;
	public static final int LED_CHARGING = (int) 0x0a;
	public static final int LED_CNT = (int) (0x0f + 1);
	public static final int LED_COMPOSE = (int) 0x03;
	public static final int LED_KANA = (int) 0x04;
	public static final int LED_MAIL = (int) 0x09;
	public static final int LED_MAX = (int) 0x0f;
	public static final int LED_MISC = (int) 0x08;
	public static final int LED_MUTE = (int) 0x07;
	public static final int LED_NUML = (int) 0x00;
	public static final int LED_SCROLLL = (int) 0x02;
	public static final int LED_SLEEP = (int) 0x05;
	public static final int LED_SUSPEND = (int) 0x06;
	public static final int MSC_CNT = (int) (0x07 + 1);
	public static final int MSC_GESTURE = (int) 0x02;
	public static final int MSC_MAX = (int) 0x07;
	public static final int MSC_PULSELED = (int) 0x01;
	public static final int MSC_RAW = (int) 0x03;
	public static final int MSC_SCAN = (int) 0x04;
	public static final int MSC_SERIAL = (int) 0x00;
	public static final int MSC_TIMESTAMP = (int) 0x05;
	public static final int REP_CNT = (int) (0x01 + 1);
	public static final int REP_DELAY = (int) 0x00;
	public static final int REP_MAX = (int) 0x01;
	public static final int REP_PERIOD = (int) 0x01;
	public static final int SW_CAMERA_LENS_COVER = (int) 0x09;
	public static final int SW_CNT = (int) (0x0f + 1);
	public static final int SW_DOCK = (int) 0x05;
	public static final int SW_FRONT_PROXIMITY = (int) 0x0b;
	public static final int SW_HEADPHONE_INSERT = (int) 0x02;
	public static final int SW_JACK_PHYSICAL_INSERT = (int) 0x07;
	public static final int SW_KEYPAD_SLIDE = (int) 0x0a;
	public static final int SW_LID = (int) 0x00;
	public static final int SW_LINEIN_INSERT = (int) 0x0d;
	public static final int SW_LINEOUT_INSERT = (int) 0x06;
	public static final int SW_MAX = (int) 0x0f;
	public static final int SW_MICROPHONE_INSERT = (int) 0x04;
	public static final int SW_RADIO = (int) 0x03;
	public static final int SW_RFKILL_ALL = (int) 0x03;
	public static final int SW_ROTATE_LOCK = (int) 0x0c;
	public static final int SW_TABLET_MODE = (int) 0x01;
	public static final int SW_VIDEOOUT_INSERT = (int) 0x08;
	public static final int REL_CNT = (int) (0x0f + 1);
	public static final int REL_DIAL = (int) 0x07;
	public static final int REL_HWHEEL = (int) 0x06;
	public static final int REL_MAX = (int) 0x0f;
	public static final int REL_MISC = (int) 0x09;
	public static final int REL_RX = (int) 0x03;
	public static final int REL_RY = (int) 0x04;
	public static final int REL_RZ = (int) 0x05;
	public static final int REL_WHEEL = (int) 0x08;
	public static final int REL_X = (int) 0x00;
	public static final int REL_Y = (int) 0x01;
	public static final int REL_Z = (int) 0x02;
	public static final int SND_BELL = (int) 0x01;
	public static final int SND_CLICK = (int) 0x00;
	public static final int SND_CNT = (int) (0x07 + 1);
	public static final int SND_MAX = (int) 0x07;
	public static final int SND_TONE = (int) 0x02;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_ACCMODE = (int) 003;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_APPEND = (int) 2000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_ASYNC = (int) 20000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_CLOEXEC = (int) 2000000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_CREAT = (int) 100;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_DIRECTORY = (int) 200000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_DSYNC = (int) 10000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_EXCL = (int) 200;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_FSYNC = (int) 4010000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_NDELAY = (int) 4000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_NOCTTY = (int) 400;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_NOFOLLOW = (int) 400000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_NONBLOCK = (int) 4000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_RDONLY = (int) 0;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_RDWR = (int) 2;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_RSYNC = (int) 4010000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_SYNC = (int) 4010000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_TRUNC = (int) 1000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_WRONLY = (int) 1;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_DIRBITS = (int) 2;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_DIRMASK = (int) ((1 << 2) - 1);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_DIRSHIFT = (int) (((0 + 8) + 8) + 14);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_NONE = (int) 0;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_NRBITS = (int) 8;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_NRMASK = (int) ((1 << 8) - 1);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_NRSHIFT = (int) 0;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_READ = (int) 2;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_SIZEBITS = (int) 14;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_SIZEMASK = (int) ((1 << 14) - 1);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_SIZESHIFT = (int) ((0 + 8) + 8);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_TYPEBITS = (int) 8;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_TYPEMASK = (int) ((1 << 8) - 1);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_TYPESHIFT = (int) (0 + 8);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_WRITE = (int) 1;

	public static final int KDMKTONE = 0x4B30;

	public static short POLLIN = 0x0001;
	public static short POLLPRI = 0x0002;

	public int ioctl(int fd, int cmd, Pointer val);

	public int ioctl(int fd, int cmd, NativeLong[] val);

	public int ioctl(int fd, int cmd, int val);

	public int ioctl(int fd, int cmd, FbFixedScreenInfo map);

	public int ioctl(int fd, int cmd, FbVariableScreenInfo map);

	public int ioctl(int fd, int cmd, FbColorMap map);

	public int ioctl(int fd, int cmd, int[] arg);

	public int ioctl(int fd, int cmd, short[] arg);

	public int ioctl(int fd, int cmd, IntByReference arg);

	public int ioctl(int fd, int cmd, byte[] arg);

	public int poll(pollfd[] fds, int nfds, int timeout);

	public int open(String path, int flags);

	public int close(int fd);

	public NativeLong read(int fd, Pointer pointer, NativeLong nativeLong);

	Pointer mmap(Pointer __addr, NativeLong __len, int __prot, int __flags,
			int __fd, NativeLong __offset);

	int munmap(Pointer __addr, NativeLong __len);

	String ttyname(int filedes);

	// int fb_register_client (struct notifier_block * nb);
	// 179 extern int fb_unregister_client(struct notifier_block *nb);
	// 180 extern int fb_notifier_call_chain(unsigned long val, void *v);
	int fb_register_client();

	int fb_unregister_client();

	int fb_notifier_call_chain(NativeLong val);

	class pollfd extends Structure {
		public int fd;
		public short events;
		public short revents;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("fd", "events", "revents");
		}
	}

	class timeval extends Structure {
		/**
		 * Seconds.<br>
		 * C type : __time_t
		 */
		public NativeLong tv_sec;
		/**
		 * Microseconds.<br>
		 * C type : __suseconds_t
		 */
		public NativeLong tv_usec;

		public timeval() {
			super();
		}

		protected List<?> getFieldOrder() {
			return Arrays.asList("tv_sec", "tv_usec");
		}

		/**
		 * @param tv_sec
		 *            Seconds.<br>
		 *            C type : __time_t<br>
		 * @param tv_usec
		 *            Microseconds.<br>
		 *            C type : __suseconds_t
		 */
		public timeval(NativeLong tv_sec, NativeLong tv_usec) {
			super();
			this.tv_sec = tv_sec;
			this.tv_usec = tv_usec;
		}

		public timeval(Pointer peer) {
			super(peer);
		}

		public static class ByReference extends timeval implements
				Structure.ByReference {

		};

		public static class ByValue extends timeval implements
				Structure.ByValue {

		};
	}

	class input_event extends Structure {
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

		protected List<?> getFieldOrder() {
			return Arrays.asList("time", "type", "code", "value");
		}

		/**
		 * @param time
		 *            C type : timeval<br>
		 * @param type
		 *            C type : __u16<br>
		 * @param code
		 *            C type : __u16<br>
		 * @param value
		 *            C type : __s32
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

		public static class ByReference extends input_event implements
				Structure.ByReference {

		};

		public static class ByValue extends input_event implements
				Structure.ByValue {

		};
	}

	class Macros {
		public static int EVIOCGABS(int abs) {
			return _IOR('E', 0x40 + abs, 0);
		}

		public static int EVIOCGBIT(int ev, int len) {
			return _IOC(_IOC_READ, 'E', 0x20 + ev, len);
		}

		public static int EVIOCGVERSION() {
			return _IOR('E', 0x01, 0);
		}

		public static int EVIOCGID() {
			return _IOR('E', 0x02, 0);
		}

		public static int EVIOCGNAME(int len) {
			return _IOC(_IOC_READ, 'E', 0x06, len);
		}

		public static int EVIOCGRAB() {
			return _IOW('E', 0x90, 0);
		}

		public static int _IOR(int x, int y, int z) {
			return ((((x) << 8) & 0xff00) | y);
		}

		public static int _IOW(int x, int y, int z) {
			return ((((x) << 8) & 0xff00) | y);
		}

		public static int _IOC(int dir, int type, int nr, int size) {
			return (((dir) << _IOC_DIRSHIFT) | ((type) << _IOC_TYPESHIFT)
					| ((nr) << _IOC_NRSHIFT) | ((size) << _IOC_SIZESHIFT));
		}
	}

}
