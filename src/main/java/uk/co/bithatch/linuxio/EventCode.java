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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Input event codes. https://www.kernel.org/doc/html/v4.17/input/event-codes.html 
 */
public enum EventCode
{

	KEY_RESERVED(0),
	KEY_ESC(1),
	KEY_1(2),
	KEY_2(3),
	KEY_3(4),
	KEY_4(5),
	KEY_5(6),
	KEY_6(7),
	KEY_7(8),
	KEY_8(9),
	KEY_9(10),
	KEY_0(11),
	KEY_MINUS(12),
	KEY_EQUAL(13),
	KEY_BACKSPACE(14),
	KEY_TAB(15),
	KEY_Q(16),
	KEY_W(17),
	KEY_E(18),
	KEY_R(19),
	KEY_T(20),
	KEY_Y(21),
	KEY_U(22),
	KEY_I(23),
	KEY_O(24),
	KEY_P(25),
	KEY_LEFTBRACE(26),
	KEY_RIGHTBRACE(27),
	KEY_ENTER(28),
	KEY_LEFTCTRL(29),
	KEY_A(30),
	KEY_S(31),
	KEY_D(32),
	KEY_F(33),
	KEY_G(34),
	KEY_H(35),
	KEY_J(36),
	KEY_K(37),
	KEY_L(38),
	KEY_SEMICOLON(39),
	KEY_APOSTROPHE(40),
	KEY_GRAVE(41),
	KEY_LEFTSHIFT(42),
	KEY_BACKSLASH(43),
	KEY_Z(44),
	KEY_X(45),
	KEY_C(46),
	KEY_V(47),
	KEY_B(48),
	KEY_N(49),
	KEY_M(50),
	KEY_COMMA(51),
	KEY_DOT(52),
	KEY_SLASH(53),
	KEY_RIGHTSHIFT(54),
	KEY_KPASTERISK(55),
	KEY_LEFTALT(56),
	KEY_SPACE(57),
	KEY_CAPSLOCK(58),
	KEY_F1(59),
	KEY_F2(60),
	KEY_F3(61),
	KEY_F4(62),
	KEY_F5(63),
	KEY_F6(64),
	KEY_F7(65),
	KEY_F8(66),
	KEY_F9(67),
	KEY_F10(68),
	KEY_NUMLOCK(69),
	KEY_SCROLLLOCK(70),
	KEY_KP7(71),
	KEY_KP8(72),
	KEY_KP9(73),
	KEY_KPMINUS(74),
	KEY_KP4(75),
	KEY_KP5(76),
	KEY_KP6(77),
	KEY_KPPLUS(78),
	KEY_KP1(79),
	KEY_KP2(80),
	KEY_KP3(81),
	KEY_KP0(82),
	KEY_KPDOT(83),

	KEY_ZENKAKUHANKAKU(85),
	KEY_102ND(86),
	KEY_F11(87),
	KEY_F12(88),
	KEY_RO(89),
	KEY_KATAKANA(90),
	KEY_HIRAGANA(91),
	KEY_HENKAN(92),
	KEY_KATAKANAHIRAGANA(93),
	KEY_MUHENKAN(94),
	KEY_KPJPCOMMA(95),
	KEY_KPENTER(96),
	KEY_RIGHTCTRL(97),
	KEY_KPSLASH(98),
	KEY_SYSRQ(99),
	KEY_RIGHTALT(100),
	KEY_LINEFEED(101),
	KEY_HOME(102),
	KEY_UP(103),
	KEY_PAGEUP(104),
	KEY_LEFT(105),
	KEY_RIGHT(106),
	KEY_END(107),
	KEY_DOWN(108),
	KEY_PAGEDOWN(109),
	KEY_INSERT(110),
	KEY_DELETE(111),
	KEY_MACRO(112),
	KEY_MUTE(113),
	KEY_VOLUMEDOWN(114),
	KEY_VOLUMEUP(115),
	KEY_POWER(116), /* SC System Power Down */
	KEY_KPEQUAL(117),
	KEY_KPPLUSMINUS(118),
	KEY_PAUSE(119),
	KEY_SCALE(120), /* AL Compiz Scale (Expose) */

	KEY_KPCOMMA(121),
	KEY_HANGEUL(122),
	KEY_HANGUEL(122),
	KEY_HANJA(123),
	KEY_YEN(124),
	KEY_LEFTMETA(125),
	KEY_RIGHTMETA(126),
	KEY_COMPOSE(127),

	KEY_STOP(128), /* AC Stop */
	KEY_AGAIN(129),
	KEY_PROPS(130), /* AC Properties */
	KEY_UNDO(131), /* AC Undo */
	KEY_FRONT(132),
	KEY_COPY(133), /* AC Copy */
	KEY_OPEN(134), /* AC Open */
	KEY_PASTE(135), /* AC Paste */
	KEY_FIND(136), /* AC Search */
	KEY_CUT(137), /* AC Cut */
	KEY_HELP(138), /* AL Integrated Help Center */
	KEY_MENU(139), /* Menu (show menu) */
	KEY_CALC(140), /* AL Calculator */
	KEY_SETUP(141),
	KEY_SLEEP(142), /* SC System Sleep */
	KEY_WAKEUP(143), /* System Wake Up */
	KEY_FILE(144), /* AL Local Machine Browser */
	KEY_SENDFILE(145),
	KEY_DELETEFILE(146),
	KEY_XFER(147),
	KEY_PROG1(148),
	KEY_PROG2(149),
	KEY_WWW(150), /*
					 * AL Internet Browser
					 */
	KEY_MSDOS(151),
	KEY_COFFEE(152), /* AL Terminal Lock/Screensaver */
	KEY_SCREENLOCK(152),
	KEY_ROTATE_DISPLAY(153), /* Display orientation for e.g. tablets */
	KEY_DIRECTION(153),
	KEY_CYCLEWINDOWS(154),
	KEY_MAIL(155),
	KEY_BOOKMARKS(156), /* AC Bookmarks */
	KEY_COMPUTER(157),
	KEY_BACK(158), /* AC Back */
	KEY_FORWARD(159), /* AC Forward */
	KEY_CLOSECD(160),
	KEY_EJECTCD(161),
	KEY_EJECTCLOSECD(162),
	KEY_NEXTSONG(163),
	KEY_PLAYPAUSE(164),
	KEY_PREVIOUSSONG(165),
	KEY_STOPCD(166),
	KEY_RECORD(167),
	KEY_REWIND(168),
	KEY_PHONE(169), /* Media Select Telephone */
	KEY_ISO(170),
	KEY_CONFIG(171), /* AL Consumer Control Configuration */
	KEY_HOMEPAGE(172), /* AC Home */
	KEY_REFRESH(173), /* AC Refresh */
	KEY_EXIT(174), /* AC Exit */
	KEY_MOVE(175),
	KEY_EDIT(176),
	KEY_SCROLLUP(177),
	KEY_SCROLLDOWN(178),
	KEY_KPLEFTPAREN(179),
	KEY_KPRIGHTPAREN(180),
	KEY_NEW(181), /* AC New */
	KEY_REDO(182), /* AC Redo/Repeat */

	KEY_F13(183),
	KEY_F14(184),
	KEY_F15(185),
	KEY_F16(186),
	KEY_F17(187),
	KEY_F18(188),
	KEY_F19(189),
	KEY_F20(190),
	KEY_F21(191),
	KEY_F22(192),
	KEY_F23(193),
	KEY_F24(194),

	KEY_PLAYCD(200),
	KEY_PAUSECD(201),
	KEY_PROG3(202),
	KEY_PROG4(203),
	KEY_DASHBOARD(204), /* AL Dashboard */
	KEY_SUSPEND(205),
	KEY_CLOSE(206), /* AC Close */
	KEY_PLAY(207),
	KEY_FASTFORWARD(208),
	KEY_BASSBOOST(209),
	KEY_PRINT(210), /* AC Print */
	KEY_HP(211),
	KEY_CAMERA(212),
	KEY_SOUND(213),
	KEY_QUESTION(214),
	KEY_EMAIL(215),
	KEY_CHAT(216),
	KEY_SEARCH(217),
	KEY_CONNECT(218),
	KEY_FINANCE(219), /* AL Checkbook/Finance */
	KEY_SPORT(220),
	KEY_SHOP(221),
	KEY_ALTERASE(222),
	KEY_CANCEL(223), /* AC Cancel */
	KEY_BRIGHTNESSDOWN(224),
	KEY_BRIGHTNESSUP(225),
	KEY_MEDIA(226),

	KEY_SWITCHVIDEOMODE(227), /*
								 * Cycle between available video ((( outputs (Monitor/LCD/TV-out/etc)
								 */
	KEY_KBDILLUMTOGGLE(228),
	KEY_KBDILLUMDOWN(229),
	KEY_KBDILLUMUP(230),

	KEY_SEND(231), /* AC Send */
	KEY_REPLY(232), /* AC Reply */
	KEY_FORWARDMAIL(233), /* AC Forward Msg */
	KEY_SAVE(234), /* AC Save */
	KEY_DOCUMENTS(235),

	KEY_BATTERY(236),

	KEY_BLUETOOTH(237),
	KEY_WLAN(238),
	KEY_UWB(239),

	KEY_UNKNOWN(240),

	KEY_VIDEO_NEXT(241), /* drive next video source */
	KEY_VIDEO_PREV(242), /* drive previous video source */
	KEY_BRIGHTNESS_CYCLE(243), /* brightness up, after max is min */
	KEY_BRIGHTNESS_AUTO(244), /*
								 * Set Auto Brightness: manual ((( brightness control is off, ((( rely on
								 * ambient
								 */
	KEY_BRIGHTNESS_ZERO(244),
	KEY_DISPLAY_OFF(245), /* display device to off state */

	KEY_WWAN(246), /* Wireless WAN (LTE, UMTS, GSM, etc.) */
	KEY_WIMAX(246),
	KEY_RFKILL(247), /* Key that controls all radios */

	KEY_MICMUTE(248), /* Mute / unmute the microphone */

	/* Code 255 is reserved for special needs of AT keyboard driver */

	BTN_MISC(0x100),
	BTN_0(0x100),
	BTN_1(0x101),
	BTN_2(0x102),
	BTN_3(0x103),
	BTN_4(0x104),
	BTN_5(0x105),
	BTN_6(0x106),
	BTN_7(0x107),
	BTN_8(0x108),
	BTN_9(0x109),

	BTN_MOUSE(0x110),
	BTN_LEFT(0x110),
	BTN_RIGHT(0x111),
	BTN_MIDDLE(0x112),
	BTN_SIDE(0x113),
	BTN_EXTRA(0x114),
	BTN_FORWARD(0x115),
	BTN_BACK(0x116),
	BTN_TASK(0x117),

	BTN_JOYSTICK(0x120),
	BTN_TRIGGER(0x120),
	BTN_THUMB(0x121),
	BTN_THUMB2(0x122),
	BTN_TOP(0x123),
	BTN_TOP2(0x124),
	BTN_PINKIE(0x125),
	BTN_BASE(0x126),
	BTN_BASE2(0x127),
	BTN_BASE3(0x128),
	BTN_BASE4(0x129),
	BTN_BASE5(0x12a),
	BTN_BASE6(0x12b),
	BTN_DEAD(0x12f),

	BTN_GAMEPAD(0x130),
	BTN_SOUTH(0x130),
	BTN_A(0x130),
	BTN_EAST(0x131),
	BTN_B(0x131),
	BTN_C(0x132),
	BTN_NORTH(0x133),
	BTN_X(0x133),
	BTN_WEST(0x134),
	BTN_Y(0x134),
	BTN_Z(0x135),
	BTN_TL(0x136),
	BTN_TR(0x137),
	BTN_TL2(0x138),
	BTN_TR2(0x139),
	BTN_SELECT(0x13a),
	BTN_START(0x13b),
	BTN_MODE(0x13c),
	BTN_THUMBL(0x13d),
	BTN_THUMBR(0x13e),

	BTN_DIGI(0x140),
	BTN_TOOL_PEN(0x140),
	BTN_TOOL_RUBBER(0x141),
	BTN_TOOL_BRUSH(0x142),
	BTN_TOOL_PENCIL(0x143),
	BTN_TOOL_AIRBRUSH(0x144),
	BTN_TOOL_FINGER(0x145),
	BTN_TOOL_MOUSE(0x146),
	BTN_TOOL_LENS(0x147),
	BTN_TOOL_QUINTTAP(0x148), /* Five fingers on trackpad */
	BTN_STYLUS3(0x149),
	BTN_TOUCH(0x14a),
	BTN_STYLUS(0x14b),
	BTN_STYLUS2(0x14c),
	BTN_TOOL_DOUBLETAP(0x14d),
	BTN_TOOL_TRIPLETAP(0x14e),
	BTN_TOOL_QUADTAP(0x14f), /* Four fingers on trackpad */

	BTN_WHEEL(0x150),
	BTN_GEAR_DOWN(0x150),
	BTN_GEAR_UP(0x151),

	KEY_OK(0x160),
	KEY_SELECT(0x161),
	KEY_GOTO(0x162),
	KEY_CLEAR(0x163),
	KEY_POWER2(0x164),
	KEY_OPTION(0x165),
	KEY_INFO(0x166), /* AL OEM Features/Tips/Tutorial */
	KEY_TIME(0x167),
	KEY_VENDOR(0x168),
	KEY_ARCHIVE(0x169),
	KEY_PROGRAM(0x16a), /* Media Select Program Guide */
	KEY_CHANNEL(0x16b),
	KEY_FAVORITES(0x16c),
	KEY_EPG(0x16d),
	KEY_PVR(0x16e), /* Media Select Home */
	KEY_MHP(0x16f),
	KEY_LANGUAGE(0x170),
	KEY_TITLE(0x171),
	KEY_SUBTITLE(0x172),
	KEY_ANGLE(0x173),
	KEY_FULL_SCREEN(0x174), /* AC View Toggle */
	KEY_ZOOM(0x174),
	KEY_MODE(0x175),
	KEY_KEYBOARD(0x176),
	KEY_ASPECT_RATIO(0x177), /* HUTRR37: Aspect */
	KEY_SCREEN(0x177),
	KEY_PC(0x178), /* Media Select Computer */
	KEY_TV(0x179), /* Media Select TV */
	KEY_TV2(0x17a), /* Media Select Cable */
	KEY_VCR(0x17b), /* Media Select VCR */
	KEY_VCR2(0x17c), /* VCR Plus */
	KEY_SAT(0x17d), /* Media Select Satellite */
	KEY_SAT2(0x17e),
	KEY_CD(0x17f), /* Media Select CD */
	KEY_TAPE(0x180), /* Media Select Tape */
	KEY_RADIO(0x181),
	KEY_TUNER(0x182), /* Media Select Tuner */
	KEY_PLAYER(0x183),
	KEY_TEXT(0x184),
	KEY_DVD(0x185), /* Media Select DVD */
	KEY_AUX(0x186),
	KEY_MP3(0x187),
	KEY_AUDIO(0x188), /* AL Audio Browser */
	KEY_VIDEO(0x189), /* AL Movie Browser */
	KEY_DIRECTORY(0x18a),
	KEY_LIST(0x18b),
	KEY_MEMO(0x18c), /* Media Select Messages */
	KEY_CALENDAR(0x18d),
	KEY_RED(0x18e),
	KEY_GREEN(0x18f),
	KEY_YELLOW(0x190),
	KEY_BLUE(0x191),
	KEY_CHANNELUP(0x192), /* Channel Increment */
	KEY_CHANNELDOWN(0x193), /* Channel Decrement */
	KEY_FIRST(0x194),
	KEY_LAST(0x195), /* Recall Last */
	KEY_AB(0x196),
	KEY_NEXT(0x197),
	KEY_RESTART(0x198),
	KEY_SLOW(0x199),
	KEY_SHUFFLE(0x19a),
	KEY_BREAK(0x19b),
	KEY_PREVIOUS(0x19c),
	KEY_DIGITS(0x19d),
	KEY_TEEN(0x19e),
	KEY_TWEN(0x19f),
	KEY_VIDEOPHONE(0x1a0), /*
							 * Media Select Video Phone
							 */
	KEY_GAMES(0x1a1), /* Media Select Games */
	KEY_ZOOMIN(0x1a2), /* AC Zoom In */
	KEY_ZOOMOUT(0x1a3), /* AC Zoom Out */
	KEY_ZOOMRESET(0x1a4), /* AC Zoom */
	KEY_WORDPROCESSOR(0x1a5), /* AL Word Processor */
	KEY_EDITOR(0x1a6), /* AL Text Editor */
	KEY_SPREADSHEET(0x1a7), /* AL Spreadsheet */
	KEY_GRAPHICSEDITOR(0x1a8), /* AL Graphics Editor */
	KEY_PRESENTATION(0x1a9), /* AL Presentation App */
	KEY_DATABASE(0x1aa), /* AL Database App */
	KEY_NEWS(0x1ab), /* AL Newsreader */
	KEY_VOICEMAIL(0x1ac), /* AL Voicemail */
	KEY_ADDRESSBOOK(0x1ad), /* AL Contacts/Address Book */
	KEY_MESSENGER(0x1ae), /* AL Instant Messaging */
	KEY_DISPLAYTOGGLE(0x1af), /* Turn display (LCD) on and off */
	KEY_BRIGHTNESS_TOGGLE(0x1af),
	KEY_SPELLCHECK(0x1b0), /* AL Spell Check */
	KEY_LOGOFF(0x1b1), /* AL Logoff */

	KEY_DOLLAR(0x1b2),
	KEY_EURO(0x1b3),

	KEY_FRAMEBACK(0x1b4), /* Consumer - transport controls */
	KEY_FRAMEFORWARD(0x1b5),
	KEY_CONTEXT_MENU(0x1b6), /* GenDesc - system context menu */
	KEY_MEDIA_REPEAT(0x1b7), /* Consumer - transport control */
	KEY_10CHANNELSUP(0x1b8), /* 10 channels up (10+) */
	KEY_10CHANNELSDOWN(0x1b9), /* 10 channels down (10-) */
	KEY_IMAGES(0x1ba), /* AL Image Browser */
	KEY_NOTIFICATION_CENTER(0x1bc), /* Show/hide the notification center */
	KEY_PICKUP_PHONE(0x1bd), /* Answer incoming call */
	KEY_HANGUP_PHONE(0x1be), /* Decline incoming call */

	KEY_DEL_EOL(0x1c0),
	KEY_DEL_EOS(0x1c1),
	KEY_INS_LINE(0x1c2),
	KEY_DEL_LINE(0x1c3),

	KEY_FN(0x1d0),
	KEY_FN_ESC(0x1d1),
	KEY_FN_F1(0x1d2),
	KEY_FN_F2(0x1d3),
	KEY_FN_F3(0x1d4),
	KEY_FN_F4(0x1d5),
	KEY_FN_F5(0x1d6),
	KEY_FN_F6(0x1d7),
	KEY_FN_F7(0x1d8),
	KEY_FN_F8(0x1d9),
	KEY_FN_F9(0x1da),
	KEY_FN_F10(0x1db),
	KEY_FN_F11(0x1dc),
	KEY_FN_F12(0x1dd),
	KEY_FN_1(0x1de),
	KEY_FN_2(0x1df),
	KEY_FN_D(0x1e0),
	KEY_FN_E(0x1e1),
	KEY_FN_F(0x1e2),
	KEY_FN_S(0x1e3),
	KEY_FN_B(0x1e4),
	KEY_FN_RIGHT_SHIFT(0x1e5),

	KEY_BRL_DOT1(0x1f1),
	KEY_BRL_DOT2(0x1f2),
	KEY_BRL_DOT3(0x1f3),
	KEY_BRL_DOT4(0x1f4),
	KEY_BRL_DOT5(0x1f5),
	KEY_BRL_DOT6(0x1f6),
	KEY_BRL_DOT7(0x1f7),
	KEY_BRL_DOT8(0x1f8),
	KEY_BRL_DOT9(0x1f9),
	KEY_BRL_DOT10(0x1fa),

	KEY_NUMERIC_0(0x200), /* used by phones, remote controls, */
	KEY_NUMERIC_1(0x201), /* and other keypads */
	KEY_NUMERIC_2(0x202),
	KEY_NUMERIC_3(0x203),
	KEY_NUMERIC_4(0x204),
	KEY_NUMERIC_5(0x205),
	KEY_NUMERIC_6(0x206),
	KEY_NUMERIC_7(0x207),
	KEY_NUMERIC_8(0x208),
	KEY_NUMERIC_9(0x209),
	KEY_NUMERIC_STAR(0x20a),
	KEY_NUMERIC_POUND(0x20b),
	KEY_NUMERIC_A(0x20c), /* Phone key A - HUT Telephony 0xb9 */
	KEY_NUMERIC_B(0x20d),
	KEY_NUMERIC_C(0x20e),
	KEY_NUMERIC_D(0x20f),

	KEY_CAMERA_FOCUS(0x210),
	KEY_WPS_BUTTON(0x211), /* WiFi Protected Setup key */

	KEY_TOUCHPAD_TOGGLE(0x212), /* Request switch touchpad on or off */
	KEY_TOUCHPAD_ON(0x213),
	KEY_TOUCHPAD_OFF(0x214),

	KEY_CAMERA_ZOOMIN(0x215),
	KEY_CAMERA_ZOOMOUT(0x216),
	KEY_CAMERA_UP(0x217),
	KEY_CAMERA_DOWN(0x218),
	KEY_CAMERA_LEFT(0x219),
	KEY_CAMERA_RIGHT(0x21a),

	KEY_ATTENDANT_ON(0x21b),
	KEY_ATTENDANT_OFF(0x21c),
	KEY_ATTENDANT_TOGGLE(0x21d), /* Attendant call on or off */
	KEY_LIGHTS_TOGGLE(0x21e), /* Reading light on or off */

	BTN_DPAD_UP(0x220),
	BTN_DPAD_DOWN(0x221),
	BTN_DPAD_LEFT(0x222),
	BTN_DPAD_RIGHT(0x223),

	KEY_ALS_TOGGLE(0x230), /* Ambient light sensor */
	KEY_ROTATE_LOCK_TOGGLE(0x231), /* Display rotation lock */

	KEY_BUTTONCONFIG(0x240), /* AL Button Configuration */
	KEY_TASKMANAGER(0x241), /* AL Task/Project Manager */
	KEY_JOURNAL(0x242), /* AL Log/Journal/Timecard */
	KEY_CONTROLPANEL(0x243), /* AL Control Panel */
	KEY_APPSELECT(0x244), /* AL Select Task/Application */
	KEY_SCREENSAVER(0x245), /* AL Screen Saver */
	KEY_VOICECOMMAND(0x246), /* Listening Voice Command */
	KEY_ASSISTANT(0x247), /* AL Context-aware desktop assistant */
	KEY_KBD_LAYOUT_NEXT(0x248), /* AC Next Keyboard Layout Select */

	KEY_BRIGHTNESS_MIN(0x250), /* Set Brightness to Minimum */
	KEY_BRIGHTNESS_MAX(0x251), /* Set Brightness to Maximum */

	KEY_KBDINPUTASSIST_PREV(0x260),
	KEY_KBDINPUTASSIST_NEXT(0x261),
	KEY_KBDINPUTASSIST_PREVGROUP(0x262),
	KEY_KBDINPUTASSIST_NEXTGROUP(0x263),
	KEY_KBDINPUTASSIST_ACCEPT(0x264),
	KEY_KBDINPUTASSIST_CANCEL(0x265),

	/* Diagonal movement keys */
	KEY_RIGHT_UP(0x266),
	KEY_RIGHT_DOWN(0x267),
	KEY_LEFT_UP(0x268),
	KEY_LEFT_DOWN(0x269),

	KEY_ROOT_MENU(0x26a), /* Show Device's Root Menu */
	/* Show Top Menu of the Media (e.g. DVD) */
	KEY_MEDIA_TOP_MENU(0x26b),
	KEY_NUMERIC_11(0x26c),
	KEY_NUMERIC_12(0x26d),
	/*
	 * Toggle Audio Description: refers to an audio service that helps blind and
	 * visually impaired consumers understand the action in a program. Note: in some
	 * countries this is referred to as "Video Description".
	 */
	KEY_AUDIO_DESC(0x26e),
	KEY_3D_MODE(0x26f),
	KEY_NEXT_FAVORITE(0x270),
	KEY_STOP_RECORD(0x271),
	KEY_PAUSE_RECORD(0x272),
	KEY_VOD(0x273), /* Video on Demand */
	KEY_UNMUTE(0x274),
	KEY_FASTREVERSE(0x275),
	KEY_SLOWREVERSE(0x276),
	/*
	 * Control a data application associated with the currently viewed channel, e.g.
	 * teletext or data broadcast application (MHEG, MHP, HbbTV, etc.)
	 */
	KEY_DATA(0x277),
	KEY_ONSCREEN_KEYBOARD(0x278),
	/* Electronic privacy screen control */
	KEY_PRIVACY_SCREEN_TOGGLE(0x279),

	/* Select an area of screen to be copied */
	KEY_SELECTIVE_SCREENSHOT(0x27a),

	/*
	 * Some keyboards have keys which do not have a defined meaning, these keys are
	 * intended to be programmed / bound to macros by the user. For most keyboards
	 * with these macro-keys the key-sequence to inject, or action to take, is all
	 * handled by software on the host side. So from the kernel's point of view
	 * these are just normal keys.
	 *
	 * The KEY_MACRO# codes below are intended for such keys, which may be labeled
	 * e.g. G1-G18, or S1 - S30. The KEY_MACRO# codes MUST NOT be used for keys
	 * where the marking on the key does indicate a defined meaning / purpose.
	 *
	 * The KEY_MACRO# codes MUST also NOT be used as fallback for when no existing
	 * KEY_FOO define matches the marking / purpose. In this case a new KEY_FOO
	 * define MUST be added.
	 */
	KEY_MACRO1(0x290),
	KEY_MACRO2(0x291),
	KEY_MACRO3(0x292),
	KEY_MACRO4(0x293),
	KEY_MACRO5(0x294),
	KEY_MACRO6(0x295),
	KEY_MACRO7(0x296),
	KEY_MACRO8(0x297),
	KEY_MACRO9(0x298),
	KEY_MACRO10(0x299),
	KEY_MACRO11(0x29a),
	KEY_MACRO12(0x29b),
	KEY_MACRO13(0x29c),
	KEY_MACRO14(0x29d),
	KEY_MACRO15(0x29e),
	KEY_MACRO16(0x29f),
	KEY_MACRO17(0x2a0),
	KEY_MACRO18(0x2a1),
	KEY_MACRO19(0x2a2),
	KEY_MACRO20(0x2a3),
	KEY_MACRO21(0x2a4),
	KEY_MACRO22(0x2a5),
	KEY_MACRO23(0x2a6),
	KEY_MACRO24(0x2a7),
	KEY_MACRO25(0x2a8),
	KEY_MACRO26(0x2a9),
	KEY_MACRO27(0x2aa),
	KEY_MACRO28(0x2ab),
	KEY_MACRO29(0x2ac),
	KEY_MACRO30(0x2ad),
	/*
	 * Some keyboards with the macro-keys described above have some extra keys for
	 * controlling the host-side software responsible for the macro handling: -A
	 * macro recording start/stop key. Note that not all keyboards which emit
	 * KEY_MACRO_RECORD_START will also emit KEY_MACRO_RECORD_STOP if
	 * KEY_MACRO_RECORD_STOP is not advertised, then KEY_MACRO_RECORD_START should
	 * be interpreted as a recording start/stop toggle; -Keys for switching between
	 * different macro (pre)sets, either a key for cycling through the configured
	 * presets or keys to directly select a preset.
	 */
	KEY_MACRO_RECORD_START(0x2b0),
	KEY_MACRO_RECORD_STOP(0x2b1),
	KEY_MACRO_PRESET_CYCLE(0x2b2),
	KEY_MACRO_PRESET1(0x2b3),
	KEY_MACRO_PRESET2(0x2b4),
	KEY_MACRO_PRESET3(0x2b5),

	/*
	 * Some keyboards have a buildin LCD panel where the contents are controlled by
	 * the host. Often these have a number of keys directly below the LCD intended
	 * for controlling a menu shown on the LCD. These keys often don't have any
	 * labeling so we just name them KEY_KBD_LCD_MENU#
	 */
	KEY_KBD_LCD_MENU1(0x2b8),
	KEY_KBD_LCD_MENU2(0x2b9),
	KEY_KBD_LCD_MENU3(0x2ba),
	KEY_KBD_LCD_MENU4(0x2bb),
	KEY_KBD_LCD_MENU5(0x2bc),

	KEY_MAX(0x2ff),

	BTN_TRIGGER_HAPPY(0x2c0),
	BTN_TRIGGER_HAPPY1(0x2c0),
	BTN_TRIGGER_HAPPY2(0x2c1),
	BTN_TRIGGER_HAPPY3(0x2c2),
	BTN_TRIGGER_HAPPY4(0x2c3),
	BTN_TRIGGER_HAPPY5(0x2c4),
	BTN_TRIGGER_HAPPY6(0x2c5),
	BTN_TRIGGER_HAPPY7(0x2c6),
	BTN_TRIGGER_HAPPY8(0x2c7),
	BTN_TRIGGER_HAPPY9(0x2c8),
	BTN_TRIGGER_HAPPY10(0x2c9),
	BTN_TRIGGER_HAPPY11(0x2ca),
	BTN_TRIGGER_HAPPY12(0x2cb),
	BTN_TRIGGER_HAPPY13(0x2cc),
	BTN_TRIGGER_HAPPY14(0x2cd),
	BTN_TRIGGER_HAPPY15(0x2ce),
	BTN_TRIGGER_HAPPY16(0x2cf),
	BTN_TRIGGER_HAPPY17(0x2d0),
	BTN_TRIGGER_HAPPY18(0x2d1),
	BTN_TRIGGER_HAPPY19(0x2d2),
	BTN_TRIGGER_HAPPY20(0x2d3),
	BTN_TRIGGER_HAPPY21(0x2d4),
	BTN_TRIGGER_HAPPY22(0x2d5),
	BTN_TRIGGER_HAPPY23(0x2d6),
	BTN_TRIGGER_HAPPY24(0x2d7),
	BTN_TRIGGER_HAPPY25(0x2d8),
	BTN_TRIGGER_HAPPY26(0x2d9),
	BTN_TRIGGER_HAPPY27(0x2da),
	BTN_TRIGGER_HAPPY28(0x2db),
	BTN_TRIGGER_HAPPY29(0x2dc),
	BTN_TRIGGER_HAPPY30(0x2dd),
	BTN_TRIGGER_HAPPY31(0x2de),
	BTN_TRIGGER_HAPPY32(0x2df),
	BTN_TRIGGER_HAPPY33(0x2e0),
	BTN_TRIGGER_HAPPY34(0x2e1),
	BTN_TRIGGER_HAPPY35(0x2e2),
	BTN_TRIGGER_HAPPY36(0x2e3),
	BTN_TRIGGER_HAPPY37(0x2e4),
	BTN_TRIGGER_HAPPY38(0x2e5),
	BTN_TRIGGER_HAPPY39(0x2e6),
	BTN_TRIGGER_HAPPY40(0x2e7),

	/*
	 * Synchronization events.
	 */

	SYN_REPORT(Type.EV_SYN, 0),
	SYN_CONFIG(Type.EV_SYN, 1),
	SYN_MT_REPORT(Type.EV_SYN, 2),
	SYN_DROPPED(Type.EV_SYN, 3),
	SYN_MAX(Type.EV_SYN, 0xf),

	ABS_BRAKE(Type.EV_ABS, 0x0a),
	ABS_CNT(Type.EV_ABS, 0x3f + 1),
	ABS_DISTANCE(Type.EV_ABS, 0x19),
	ABS_GAS(Type.EV_ABS, 0x09),
	ABS_HAT0X(Type.EV_ABS, 0x10),
	ABS_HAT0Y(Type.EV_ABS, 0x11),
	ABS_HAT1X(Type.EV_ABS, 0x12),
	ABS_HAT1Y(Type.EV_ABS, 0x13),
	ABS_HAT2X(Type.EV_ABS, 0x14),
	ABS_HAT2Y(Type.EV_ABS, 0x15),
	ABS_HAT3X(Type.EV_ABS, 0x16),
	ABS_HAT3Y(Type.EV_ABS, 0x17),
	ABS_MAX(Type.EV_ABS, 0x3f),
	ABS_MISC(Type.EV_ABS, 0x28),
	ABS_MT_BLOB_ID(Type.EV_ABS, 0x38),
	ABS_MT_DISTANCE(Type.EV_ABS, 0x3b),
	ABS_MT_ORIENTATION(Type.EV_ABS, 0x34),
	ABS_MT_POSITION_X(Type.EV_ABS, 0x35),
	ABS_MT_POSITION_Y(Type.EV_ABS, 0x36),
	ABS_MT_PRESSURE(Type.EV_ABS, 0x3a),
	ABS_MT_SLOT(Type.EV_ABS, 0x2f),
	ABS_MT_TOOL_TYPE(Type.EV_ABS, 0x37),
	ABS_MT_TOOL_X(Type.EV_ABS, 0x3c),
	ABS_MT_TOOL_Y(Type.EV_ABS, 0x3d),
	ABS_MT_TOUCH_MAJOR(Type.EV_ABS, 0x30),
	ABS_MT_TOUCH_MINOR(Type.EV_ABS, 0x31),
	ABS_MT_TRACKING_ID(Type.EV_ABS, 0x39),
	ABS_MT_WIDTH_MAJOR(Type.EV_ABS, 0x32),
	ABS_MT_WIDTH_MINOR(Type.EV_ABS, 0x33),
	ABS_PRESSURE(Type.EV_ABS, 0x18),
	ABS_RUDDER(Type.EV_ABS, 0x07),
	ABS_RX(Type.EV_ABS, 0x03),
	ABS_RY(Type.EV_ABS, 0x04),
	ABS_RZ(Type.EV_ABS, 0x05),
	ABS_THROTTLE(Type.EV_ABS, 0x06),
	ABS_TILT_X(Type.EV_ABS, 0x1a),
	ABS_TILT_Y(Type.EV_ABS, 0x1b),
	ABS_TOOL_WIDTH(Type.EV_ABS, 0x1c),
	ABS_VOLUME(Type.EV_ABS, 0x20),
	ABS_WHEEL(Type.EV_ABS, 0x08),
	ABS_X(Type.EV_ABS, 0x00),
	ABS_Y(Type.EV_ABS, 0x01),
	ABS_Z(Type.EV_ABS, 0x02),

	REL_CNT(Type.EV_REL, (0x0f + 1)),
	REL_DIAL(Type.EV_REL, 0x07),
	REL_HWHEEL(Type.EV_REL, 0x06),
	REL_MAX(Type.EV_REL, 0x0f),
	REL_MISC(Type.EV_REL, 0x09),
	REL_RX(Type.EV_REL, 0x03),
	REL_RY(Type.EV_REL, 0x04),
	REL_RZ(Type.EV_REL, 0x05),
	REL_WHEEL(Type.EV_REL, 0x08),
	REL_X(Type.EV_REL, 0x00),
	REL_Y(Type.EV_REL, 0x01),
	REL_Z(Type.EV_REL, 0x02),

	SW_CAMERA_LENS_COVER(Type.EV_SW, 0x09),
	SW_CNT(Type.EV_SW, (0x0f + 1)),
	SW_DOCK(Type.EV_SW, 0x05),
	SW_FRONT_PROXIMITY(Type.EV_SW, 0x0b),
	SW_HEADPHONE_INSERT(Type.EV_SW, 0x02),
	SW_JACK_PHYSICAL_INSERT(Type.EV_SW, 0x07),
	SW_KEYPAD_SLIDE(Type.EV_SW, 0x0a),
	SW_LID(Type.EV_SW, 0x00),
	SW_LINEIN_INSERT(Type.EV_SW, 0x0d),
	SW_LINEOUT_INSERT(Type.EV_SW, 0x06),
	SW_MAX(Type.EV_SW, 0x0f),
	SW_MICROPHONE_INSERT(Type.EV_SW, 0x04),
	SW_RADIO(Type.EV_SW, 0x03),
	SW_RFKILL_ALL(Type.EV_SW, 0x03),
	SW_ROTATE_LOCK(Type.EV_SW, 0x0c),
	SW_TABLET_MODE(Type.EV_SW, 0x01),
	SW_VIDEOOUT_INSERT(Type.EV_SW, 0x08),

	MSC_CNT(Type.EV_MSC, (0x07 + 1)),
	MSC_GESTURE(Type.EV_MSC, 0x02),
	MSC_MAX(Type.EV_MSC, 0x07),
	MSC_PULSELED(Type.EV_MSC, 0x01),
	MSC_RAW(Type.EV_MSC, 0x03),
	MSC_SCAN(Type.EV_MSC, 0x04),
	MSC_SERIAL(Type.EV_MSC, 0x00),
	MSC_TIMESTAMP(Type.EV_MSC, 0x05),

	LED_CAPSL(Type.EV_LED, 0x01),
	LED_CHARGING(Type.EV_LED, 0x0a),
	LED_CNT(Type.EV_LED, (0x0f + 1)),
	LED_COMPOSE(Type.EV_LED, 0x03),
	LED_KANA(Type.EV_LED, 0x04),
	LED_MAIL(Type.EV_LED, 0x09),
	LED_MAX(Type.EV_LED, 0x0f),
	LED_MISC(Type.EV_LED, 0x08),
	LED_MUTE(Type.EV_LED, 0x07),
	LED_NUML(Type.EV_LED, 0x00),
	LED_SCROLLL(Type.EV_LED, 0x02),
	LED_SLEEP(Type.EV_LED, 0x05),
	LED_SUSPEND(Type.EV_LED, 0x06),

	REP_CNT(Type.EV_REP, (0x01 + 1)),
	REP_DELAY(Type.EV_REP, 0x00),
	REP_MAX(Type.EV_REP, 0x01),
	REP_PERIOD(Type.EV_REP, 0x01),

	SND_BELL(Type.EV_SND, 0x01),
	SND_CLICK(Type.EV_SND, 0x00),
	SND_CNT(Type.EV_SND, (0x07 + 1)),
	SND_MAX(Type.EV_SND, 0x07),
	SND_TONE(Type.EV_SND, 0x02);

	private short code;
	private Type type;

	private EventCode(int code) {
		this(Type.EV_KEY, code);
	}

	private EventCode(Type type, int code) {
		if (code > Short.MAX_VALUE)
			throw new IllegalArgumentException();
		this.code = (short) code;
		this.type = type;
		Map<Integer, EventCode> mmap = Ev.codeToName.get(type);
		if (mmap == null) {
			mmap = new LinkedHashMap<>();
			Ev.codeToName.put(type, mmap);
		}
		mmap.put(code, this);
		String name = name();
		if (name.startsWith("BTN_")) {
			Ev.buttons.put(code, this);
		}
		if (name.startsWith("KEY_")) {
			Ev.keys.put(code, this);
		}
	}

	/**
	 * Type.
	 *
	 * @return the type
	 */
	public Type type() {
		return type;
	}

	/**
	 * Type code.
	 *
	 * @return the int
	 */
	public int typeCode() {
		return type.code();
	}

	/**
	 * Code.
	 *
	 * @return the short
	 */
	public short code() {
		return code;
	}

	/**
	 * Checks if is button.
	 *
	 * @return true, if is button
	 */
	public boolean isButton() {
		return name().startsWith("BTN_");
	}

	/**
	 * Checks if is key.
	 *
	 * @return true, if is key
	 */
	public boolean isKey() {
		return name().startsWith("KEY_");
	}
	
	/**
	 * Parses the.
	 *
	 * @param string the string
	 * @return the event code
	 */
	public static EventCode parse(String string) {
		String[] parts =string.split(":");
		if(parts.length == 1)
			return EventCode.valueOf(parts[0]);
		else if(parts.length == 2)
			return EventCode.fromCode(Type.parse(parts[0]).code(), Integer.parseInt(parts[1]));
		else
			throw new IllegalArgumentException("Unexpected format. Either eventType:eventCode or eventName.");
	}

	/**
	 * Checks if is button.
	 *
	 * @param code the code
	 * @return true, if is button
	 */
	public static boolean isButton(int code) {
		return Ev.buttons.containsKey(code);
	}

	/**
	 * Checks if is key.
	 *
	 * @param code the code
	 * @return true, if is key
	 */
	public static boolean isKey(int code) {
		return Ev.keys.containsKey(code);
	}
	
	/**
	 * Checks for code.
	 *
	 * @param type the type
	 * @param code the code
	 * @return true, if successful
	 */
	public static boolean hasCode(int type, short code) {
		return hasCode(Type.fromCode(type), code);
	}

	/**
	 * Checks for code.
	 *
	 * @param type the type
	 * @param code the code
	 * @return true, if successful
	 */
	public static boolean hasCode(Type type, short code) {
		Map<Integer, EventCode> mmap = Ev.codeToName.get(type);
		if (mmap == null)
			return false;

		EventCode name = mmap.get((int) code);
		if (name == null)
			return false;
		return true;
	}

	/**
	 * From code.
	 *
	 * @param type the type
	 * @param code the code
	 * @return the event code
	 */
	public static EventCode fromCode(int type, int code) {
		return fromCode(Type.fromCode(type), code);
	}
	
	/**
	 * From code.
	 *
	 * @param type the type
	 * @param code the code
	 * @return the event code
	 */
	public static EventCode fromCode(Type type, int code) {
		Map<Integer, EventCode> mmap = Ev.codeToName.get(type);
		if (mmap == null)
			throw new IllegalArgumentException(String.format("No input events with type %d", type));

		EventCode name = mmap.get(code);
		if (name == null)
			throw new IllegalArgumentException(String.format("No input event with code %d", code));
		return name;
	}

	/**
	 * The Enum Type.
	 */
	public enum Type
	{
		EV_SYN(EventCode.Ev.EV_SYN),
		EV_REL(EventCode.Ev.EV_REL),
		EV_MSC(EventCode.Ev.EV_MSC),
		EV_SND(EventCode.Ev.EV_SND),
		EV_FF(EventCode.Ev.EV_FF),
		EV_FF_STATUS(EventCode.Ev.EV_FF_STATUS),
		EV_KEY(EventCode.Ev.EV_KEY),
		EV_ABS(EventCode.Ev.EV_ABS),
		EV_LED(EventCode.Ev.EV_LED),
		EV_REP(EventCode.Ev.EV_REP),
		EV_PWT(EventCode.Ev.EV_PWR),
		EV_SW(EventCode.Ev.EV_SW),
		UNKNOWN(-1);

		private int nativeType;

		Type(int nativeType) {
			this.nativeType = nativeType;
			Ev.types.put(nativeType, this);
		}

		/**
		 * Parses the.
		 *
		 * @param string the string
		 * @return the type
		 */
		public static Type parse(String string) {
			try {
				return Type.fromCode(Integer.parseInt(string));
			}
			catch(NumberFormatException nfe) {
				return Type.valueOf(string);
			}
		}

		/**
		 * Code.
		 *
		 * @return the int
		 */
		public int code() {
			return nativeType;
		}

		/**
		 * From code.
		 *
		 * @param type the type
		 * @return the type
		 */
		public static Type fromCode(int type) {
			return Ev.types.getOrDefault(type, Type.UNKNOWN);
		}

		/**
		 * Gets the.
		 *
		 * @param codes the codes
		 * @return the collection
		 */
		public Collection<EventCode> get(Collection<EventCode> codes) {
			List<EventCode> l = new ArrayList<>();
			for (EventCode e : codes) {
				if (e.typeCode() == nativeType)
					l.add(e);
			}
			return l;
		}
	}
	
	/**
	 * The Enum Property.
	 */
	public enum Property
	{
		INPUT_PROP_POINTER(0x00),	/* needs a pointer */
		INPUT_PROP_DIRECT(0x01),	/* direct input devices */
		INPUT_PROP_BUTTONPAD(0x02),	/* has button(s) under pad */
		INPUT_PROP_SEMI_MT(0x03),	/* touch rectangle only */
		INPUT_PROP_TOPBUTTONPAD(0x04),	/* softbuttons at top of pad */
		INPUT_PROP_POINTING_STICK(0x05),	/* is a pointing stick */
		INPUT_PROP_ACCELEROMETER(0x06),	/* has accelerometer */
		INPUT_PROP_MAX(0x1f),
		INPUT_PROP_UNKNOWN(0xff);
		
		private int nativeType;

		Property(int nativeType) {
			this.nativeType = nativeType;
			Ev.properties.put(nativeType, this);
		}

		/**
		 * Parses the.
		 *
		 * @param string the string
		 * @return the property
		 */
		public static Property parse(String string) {
			try {
				return Property.fromCode(Integer.parseInt(string));
			}
			catch(NumberFormatException nfe) {
				try {
					return Property.valueOf(string);
				}
				catch(IllegalArgumentException iae) {
					return Property.INPUT_PROP_UNKNOWN;
				}
			}
		}

		/**
		 * Code.
		 *
		 * @return the int
		 */
		public int code() {
			return nativeType;
		}

		/**
		 * From code.
		 *
		 * @param type the type
		 * @return the property
		 */
		public static Property fromCode(int type) {
			Property p = Ev.properties.get(type);
			if(p== null)
				return Property.INPUT_PROP_UNKNOWN;
			return p;
		}

		/**
		 * Gets the.
		 *
		 * @param codes the codes
		 * @return the collection
		 */
		public Collection<EventCode> get(Collection<EventCode> codes) {
			List<EventCode> l = new ArrayList<>();
			for (EventCode e : codes) {
				if (e.typeCode() == nativeType)
					l.add(e);
			}
			return l;
		}
	}
	
	/**
	 * The Enum AbsoluteValue.
	 */
	public enum AbsoluteValue {
		VALUE,
		MIN,
		MAX,
		FUZZ,
		FLAT,
		RESOLUTION,
		UNKNOWN
	}

	/**
	 * The Class Ev.
	 */
	public static class Ev {

		/** The Constant EV_VERSION. */
		public static final int EV_VERSION = (int) 0x010001;
		
		/** The Constant EV_ABS. */
		public static final int EV_ABS = (int) 0x03;
		
		/** The Constant EV_CNT. */
		public static final int EV_CNT = (int) (0x1f + 1);
		
		/** The Constant EV_FF. */
		public static final int EV_FF = (int) 0x15;
		
		/** The Constant EV_FF_STATUS. */
		public static final int EV_FF_STATUS = (int) 0x17;
		
		/** The Constant EV_KEY. */
		public static final int EV_KEY = (int) 0x01;
		
		/** The Constant EV_LED. */
		public static final int EV_LED = (int) 0x11;
		
		/** The Constant EV_MAX. */
		public static final int EV_MAX = (int) 0x1f;
		
		/** The Constant EV_MSC. */
		public static final int EV_MSC = (int) 0x04;
		
		/** The Constant EV_PWR. */
		public static final int EV_PWR = (int) 0x16;
		
		/** The Constant EV_REL. */
		public static final int EV_REL = (int) 0x02;
		/** <i>native declaration : bits/fcntl-linux.h</i> */
		public static final int EV_REP = (int) 0x14;
		
		/** The Constant EV_SND. */
		public static final int EV_SND = (int) 0x12;
		
		/** The Constant EV_SW. */
		public static final int EV_SW = (int) 0x05;
		
		/** The Constant EV_SYN. */
		public static final int EV_SYN = (int) 0x00;

		final static Map<Type, Map<Integer, EventCode>> codeToName = new HashMap<>();
		final static Map<Integer, EventCode> buttons = new HashMap<>();
		final static Map<Integer, Type> types = new HashMap<>();
		final static Map<Integer, Property> properties = new HashMap<>();
		final static Map<Integer, EventCode> keys = new HashMap<>();
	}
}
