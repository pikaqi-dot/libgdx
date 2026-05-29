/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx;

import com.badlogic.gdx.input.NativeInputConfiguration;
import com.badlogic.gdx.input.NativeInputConfiguration.NativeInputCloseCallback;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.ObjectIntMap;

/**
 * <b>输入模块接口。</b>
 * 提供输入设施的访问接口。可以轮询键盘、触摸屏和加速度计的状态。
 * 在某些后端（桌面、GWT 等），触摸屏被鼠标输入替代。
 * 加速度计在某些后端不可用。
 * </p>
 * 
 * <p>
 * 除了轮询事件，还可以使用 {@link InputProcessor} 处理所有输入事件。
 * 通过 {@link #setInputProcessor(InputProcessor)} 设置后，
 * 处理器会在每帧的 {@link ApplicationListener#render()} 方法之前被调用。
 * </p>
 * 
 * <p>
 * 键盘按键在所有系统上统一映射为 {@link Keys} 中的常量。不要使用系统特定的键常量。
 * </p>
 * 
 * <p>
 * 本接口还提供了使用（和检测）其他输入系统的方法，如振动、指南针、屏幕键盘和鼠标捕获。
 * 同时也提供简单的输入对话框支持。
 * </p>
 * 
 * @author mzechner */
public interface Input {
	/** 文本输入回调接口，用于 {@link Input#getTextInput(TextInputListener, String, String, String)}
	 * 
	 * @author mzechner */
	static public interface TextInputListener {
		public void input (String text);

		public void canceled ();
	}

	/** 鼠标按钮常量。
	 * @author mzechner */
	static public class Buttons {
		public static final int LEFT = 0;
		public static final int RIGHT = 1;
		public static final int MIDDLE = 2;
		public static final int BACK = 3;
		public static final int FORWARD = 4;
	}

	/** 键盘按键常量。所有平台统一映射。
	 * 
	 * @author mzechner */
	static public class Keys {
		public static final int ANY_KEY = -1;
		public static final int NUM_0 = 7;
		public static final int NUM_1 = 8;
		public static final int NUM_2 = 9;
		public static final int NUM_3 = 10;
		public static final int NUM_4 = 11;
		public static final int NUM_5 = 12;
		public static final int NUM_6 = 13;
		public static final int NUM_7 = 14;
		public static final int NUM_8 = 15;
		public static final int NUM_9 = 16;
		public static final int A = 29;
		public static final int ALT_LEFT = 57;
		public static final int ALT_RIGHT = 58;
		public static final int APOSTROPHE = 75;
		public static final int AT = 77;
		public static final int B = 30;
		public static final int BACK = 4;
		public static final int BACKSLASH = 73;
		public static final int C = 31;
		public static final int CALL = 5;
		public static final int CAMERA = 27;
		public static final int CAPS_LOCK = 115;
		public static final int CLEAR = 28;
		public static final int COMMA = 55;
		public static final int D = 32;
		public static final int DEL = 67;
		public static final int BACKSPACE = 67;
		public static final int FORWARD_DEL = 112;
		public static final int DPAD_CENTER = 23;
		public static final int DPAD_DOWN = 20;
		public static final int DPAD_LEFT = 21;
		public static final int DPAD_RIGHT = 22;
		public static final int DPAD_UP = 19;
		public static final int CENTER = 23;
		public static final int DOWN = 20;
		public static final int LEFT = 21;
		public static final int RIGHT = 22;
		public static final int UP = 19;
		public static final int E = 33;
		public static final int ENDCALL = 6;
		public static final int ENTER = 66;
		public static final int ENVELOPE = 65;
		public static final int EQUALS = 70;
		public static final int EXPLORER = 64;
		public static final int F = 34;
		public static final int FOCUS = 80;
		public static final int G = 35;
		public static final int GRAVE = 68;
		public static final int H = 36;
		public static final int HEADSETHOOK = 79;
		public static final int HOME = 3;
		public static final int I = 37;
		public static final int J = 38;
		public static final int K = 39;
		public static final int L = 40;
		public static final int LEFT_BRACKET = 71;
		public static final int M = 41;
		public static final int MEDIA_FAST_FORWARD = 90;
		public static final int MEDIA_NEXT = 87;
		public static final int MEDIA_PLAY_PAUSE = 85;
		public static final int MEDIA_PREVIOUS = 88;
		public static final int MEDIA_REWIND = 89;
		public static final int MEDIA_STOP = 86;
		public static final int MENU = 82;
		public static final int MINUS = 69;
		public static final int MUTE = 91;
		public static final int N = 42;
		public static final int NOTIFICATION = 83;
		public static final int NUM = 78;
		public static final int O = 43;
		public static final int P = 44;
		public static final int PAUSE = 121; // aka break
		public static final int PERIOD = 56;
		public static final int PLUS = 81;
		public static final int POUND = 18;
		public static final int POWER = 26;
		public static final int PRINT_SCREEN = 120; // aka SYSRQ
		public static final int Q = 45;
		public static final int R = 46;
		public static final int RIGHT_BRACKET = 72;
		public static final int S = 47;
		public static final int SCROLL_LOCK = 116;
		public static final int SEARCH = 84;
		public static final int SEMICOLON = 74;
		public static final int SHIFT_LEFT = 59;
		public static final int SHIFT_RIGHT = 60;
		public static final int SLASH = 76;
		public static final int SOFT_LEFT = 1;
		public static final int SOFT_RIGHT = 2;
		public static final int SPACE = 62;
		public static final int STAR = 17;
		public static final int SYM = 63; // on MacOS, this is Command (⌘)
		public static final int T = 48;
		public static final int TAB = 61;
		public static final int U = 49;
		public static final int UNKNOWN = 0;
		public static final int V = 50;
		public static final int VOLUME_DOWN = 25;
		public static final int VOLUME_UP = 24;
		public static final int W = 51;
		public static final int X = 52;
		public static final int Y = 53;
		public static final int Z = 54;
		public static final int META_ALT_LEFT_ON = 16;
		public static final int META_ALT_ON = 2;
		public static final int META_ALT_RIGHT_ON = 32;
		public static final int META_SHIFT_LEFT_ON = 64;
		public static final int META_SHIFT_ON = 1;
		public static final int META_SHIFT_RIGHT_ON = 128;
		public static final int META_SYM_ON = 4;
		public static final int CONTROL_LEFT = 129;
		public static final int CONTROL_RIGHT = 130;
		public static final int ESCAPE = 111;
		public static final int END = 123;
		public static final int INSERT = 124;
		public static final int PAGE_UP = 92;
		public static final int PAGE_DOWN = 93;
		public static final int PICTSYMBOLS = 94;
		public static final int SWITCH_CHARSET = 95;
		public static final int BUTTON_CIRCLE = 255;
		public static final int BUTTON_A = 96;
		public static final int BUTTON_B = 97;
		public static final int BUTTON_C = 98;
		public static final int BUTTON_X = 99;
		public static final int BUTTON_Y = 100;
		public static final int BUTTON_Z = 101;
		public static final int BUTTON_L1 = 102;
		public static final int BUTTON_R1 = 103;
		public static final int BUTTON_L2 = 104;
		public static final int BUTTON_R2 = 105;
		public static final int BUTTON_THUMBL = 106;
		public static final int BUTTON_THUMBR = 107;
		public static final int BUTTON_START = 108;
		public static final int BUTTON_SELECT = 109;
		public static final int BUTTON_MODE = 110;

		public static final int NUMPAD_0 = 144;
		public static final int NUMPAD_1 = 145;
		public static final int NUMPAD_2 = 146;
		public static final int NUMPAD_3 = 147;
		public static final int NUMPAD_4 = 148;
		public static final int NUMPAD_5 = 149;
		public static final int NUMPAD_6 = 150;
		public static final int NUMPAD_7 = 151;
		public static final int NUMPAD_8 = 152;
		public static final int NUMPAD_9 = 153;

		public static final int NUMPAD_DIVIDE = 154;
		public static final int NUMPAD_MULTIPLY = 155;
		public static final int NUMPAD_SUBTRACT = 156;
		public static final int NUMPAD_ADD = 157;
		public static final int NUMPAD_DOT = 158;
		public static final int NUMPAD_COMMA = 159;
		public static final int NUMPAD_ENTER = 160;
		public static final int NUMPAD_EQUALS = 161;
		public static final int NUMPAD_LEFT_PAREN = 162;
		public static final int NUMPAD_RIGHT_PAREN = 163;
		public static final int NUM_LOCK = 143;

		public static final int WORLD_1 = 240;
		public static final int WORLD_2 = 241;

// public static final int BACKTICK = 0;
// public static final int TILDE = 0;
// public static final int UNDERSCORE = 0;
// public static final int DOT = 0;
// public static final int BREAK = 0;
// public static final int PIPE = 0;
// public static final int EXCLAMATION = 0;
// public static final int QUESTIONMARK = 0;

// ` | VK_BACKTICK
// ~ | VK_TILDE
// : | VK_COLON
// _ | VK_UNDERSCORE
// . | VK_DOT
// (break) | VK_BREAK
// | | VK_PIPE
// ! | VK_EXCLAMATION
// ? | VK_QUESTION
		public static final int COLON = 243;
		public static final int F1 = 131;
		public static final int F2 = 132;
		public static final int F3 = 133;
		public static final int F4 = 134;
		public static final int F5 = 135;
		public static final int F6 = 136;
		public static final int F7 = 137;
		public static final int F8 = 138;
		public static final int F9 = 139;
		public static final int F10 = 140;
		public static final int F11 = 141;
		public static final int F12 = 142;
		public static final int F13 = 183;
		public static final int F14 = 184;
		public static final int F15 = 185;
		public static final int F16 = 186;
		public static final int F17 = 187;
		public static final int F18 = 188;
		public static final int F19 = 189;
		public static final int F20 = 190;
		public static final int F21 = 191;
		public static final int F22 = 192;
		public static final int F23 = 193;
		public static final int F24 = 194;

		public static final int MAX_KEYCODE = 255;

		/** 将键码转换为人类可读的字符串表示。返回的值可用于 {@link Input.Keys#valueOf(String)}。
		 * @return 键码对应的字符串名称 */
		public static String toString (int keycode) {
			if (keycode < 0) throw new IllegalArgumentException("keycode cannot be negative, keycode: " + keycode);
			if (keycode > MAX_KEYCODE) throw new IllegalArgumentException("keycode cannot be greater than 255, keycode: " + keycode);
			switch (keycode) {
			// META* variables should not be used with this method.
			case UNKNOWN:
				return "Unknown";
			case SOFT_LEFT:
				return "Soft Left";
			case SOFT_RIGHT:
				return "Soft Right";
			case HOME:
				return "Home";
			case BACK:
				return "Back";
			case CALL:
				return "Call";
			case ENDCALL:
				return "End Call";
			case NUM_0:
				return "0";
			case NUM_1:
				return "1";
			case NUM_2:
				return "2";
			case NUM_3:
				return "3";
			case NUM_4:
				return "4";
			case NUM_5:
				return "5";
			case NUM_6:
				return "6";
			case NUM_7:
				return "7";
			case NUM_8:
				return "8";
			case NUM_9:
				return "9";
			case STAR:
				return "*";
			case POUND:
				return "#";
			case UP:
				return "Up";
			case DOWN:
				return "Down";
			case LEFT:
				return "Left";
			case RIGHT:
				return "Right";
			case CENTER:
				return "Center";
			case VOLUME_UP:
				return "Volume Up";
			case VOLUME_DOWN:
				return "Volume Down";
			case POWER:
				return "Power";
			case CAMERA:
				return "Camera";
			case CLEAR:
				return "Clear";
			case A:
				return "A";
			case B:
				return "B";
			case C:
				return "C";
			case D:
				return "D";
			case E:
				return "E";
			case F:
				return "F";
			case G:
				return "G";
			case H:
				return "H";
			case I:
				return "I";
			case J:
				return "J";
			case K:
				return "K";
			case L:
				return "L";
			case M:
				return "M";
			case N:
				return "N";
			case O:
				return "O";
			case P:
				return "P";
			case Q:
				return "Q";
			case R:
				return "R";
			case S:
				return "S";
			case T:
				return "T";
			case U:
				return "U";
			case V:
				return "V";
			case W:
				return "W";
			case X:
				return "X";
			case Y:
				return "Y";
			case Z:
				return "Z";
			case COMMA:
				return ",";
			case PERIOD:
				return ".";
			case ALT_LEFT:
				return "L-Alt";
			case ALT_RIGHT:
				return "R-Alt";
			case SHIFT_LEFT:
				return "L-Shift";
			case SHIFT_RIGHT:
				return "R-Shift";
			case TAB:
				return "Tab";
			case SPACE:
				return "Space";
			case SYM:
				return "SYM";
			case EXPLORER:
				return "Explorer";
			case ENVELOPE:
				return "Envelope";
			case ENTER:
				return "Enter";
			case DEL:
				return "Delete"; // also BACKSPACE
			case GRAVE:
				return "`";
			case MINUS:
				return "-";
			case EQUALS:
				return "=";
			case LEFT_BRACKET:
				return "[";
			case RIGHT_BRACKET:
				return "]";
			case BACKSLASH:
				return "\\";
			case SEMICOLON:
				return ";";
			case APOSTROPHE:
				return "'";
			case SLASH:
				return "/";
			case AT:
				return "@";
			case NUM:
				return "Num";
			case HEADSETHOOK:
				return "Headset Hook";
			case FOCUS:
				return "Focus";
			case PLUS:
				return "Plus";
			case MENU:
				return "Menu";
			case NOTIFICATION:
				return "Notification";
			case SEARCH:
				return "Search";
			case MEDIA_PLAY_PAUSE:
				return "Play/Pause";
			case MEDIA_STOP:
				return "Stop Media";
			case MEDIA_NEXT:
				return "Next Media";
			case MEDIA_PREVIOUS:
				return "Prev Media";
			case MEDIA_REWIND:
				return "Rewind";
			case MEDIA_FAST_FORWARD:
				return "Fast Forward";
			case MUTE:
				return "Mute";
			case PAGE_UP:
				return "Page Up";
			case PAGE_DOWN:
				return "Page Down";
			case PICTSYMBOLS:
				return "PICTSYMBOLS";
			case SWITCH_CHARSET:
				return "SWITCH_CHARSET";
			case BUTTON_A:
				return "A Button";
			case BUTTON_B:
				return "B Button";
			case BUTTON_C:
				return "C Button";
			case BUTTON_X:
				return "X Button";
			case BUTTON_Y:
				return "Y Button";
			case BUTTON_Z:
				return "Z Button";
			case BUTTON_L1:
				return "L1 Button";
			case BUTTON_R1:
				return "R1 Button";
			case BUTTON_L2:
				return "L2 Button";
			case BUTTON_R2:
				return "R2 Button";
			case BUTTON_THUMBL:
				return "Left Thumb";
			case BUTTON_THUMBR:
				return "Right Thumb";
			case BUTTON_START:
				return "Start";
			case BUTTON_SELECT:
				return "Select";
			case BUTTON_MODE:
				return "Button Mode";
			case FORWARD_DEL:
				return "Forward Delete";
			case CONTROL_LEFT:
				return "L-Ctrl";
			case CONTROL_RIGHT:
				return "R-Ctrl";
			case ESCAPE:
				return "Escape";
			case END:
				return "End";
			case INSERT:
				return "Insert";
			case NUMPAD_0:
				return "Numpad 0";
			case NUMPAD_1:
				return "Numpad 1";
			case NUMPAD_2:
				return "Numpad 2";
			case NUMPAD_3:
				return "Numpad 3";
			case NUMPAD_4:
				return "Numpad 4";
			case NUMPAD_5:
				return "Numpad 5";
			case NUMPAD_6:
				return "Numpad 6";
			case NUMPAD_7:
				return "Numpad 7";
			case NUMPAD_8:
				return "Numpad 8";
			case NUMPAD_9:
				return "Numpad 9";
			case COLON:
				return ":";
			case F1:
				return "F1";
			case F2:
				return "F2";
			case F3:
				return "F3";
			case F4:
				return "F4";
			case F5:
				return "F5";
			case F6:
				return "F6";
			case F7:
				return "F7";
			case F8:
				return "F8";
			case F9:
				return "F9";
			case F10:
				return "F10";
			case F11:
				return "F11";
			case F12:
				return "F12";
			case F13:
				return "F13";
			case F14:
				return "F14";
			case F15:
				return "F15";
			case F16:
				return "F16";
			case F17:
				return "F17";
			case F18:
				return "F18";
			case F19:
				return "F19";
			case F20:
				return "F20";
			case F21:
				return "F21";
			case F22:
				return "F22";
			case F23:
				return "F23";
			case F24:
				return "F24";
			case NUMPAD_DIVIDE:
				return "Num /";
			case NUMPAD_MULTIPLY:
				return "Num *";
			case NUMPAD_SUBTRACT:
				return "Num -";
			case NUMPAD_ADD:
				return "Num +";
			case NUMPAD_DOT:
				return "Num .";
			case NUMPAD_COMMA:
				return "Num ,";
			case NUMPAD_ENTER:
				return "Num Enter";
			case NUMPAD_EQUALS:
				return "Num =";
			case NUMPAD_LEFT_PAREN:
				return "Num (";
			case NUMPAD_RIGHT_PAREN:
				return "Num )";
			case NUM_LOCK:
				return "Num Lock";
			case CAPS_LOCK:
				return "Caps Lock";
			case SCROLL_LOCK:
				return "Scroll Lock";
			case PAUSE:
				return "Pause";
			case PRINT_SCREEN:
				return "Print";
			// BUTTON_CIRCLE unhandled, as it conflicts with the more likely to be pressed F12
			default:
				// key name not found
				return null;
			}
		}

		private static ObjectIntMap<String> keyNames;

		/** 根据 {@link Keys#toString(int)} 返回的键名获取对应的键码
		 * @param keyname 键名称
		 * @return 键码整数值 */
		public static int valueOf (String keyname) {
			if (keyNames == null) initializeKeyNames();
			return keyNames.get(keyname, -1);
		}

		/** 在 {@link Keys#valueOf(String)} 中延迟初始化 */
		private static void initializeKeyNames () {
			keyNames = new ObjectIntMap<String>();
			for (int i = 0; i < 256; i++) {
				String name = toString(i);
				if (name != null) keyNames.put(name, i);
			}
		}
	}

	/** 可用外设枚举。配合 {@link Input#isPeripheralAvailable(Peripheral)} 使用。
	 * @author mzechner */
	public enum Peripheral {
		HardwareKeyboard, OnscreenKeyboard, MultitouchScreen, Accelerometer, Compass, Vibrator, HapticFeedback, Gyroscope, RotationVector, Pressure
	}

	/** @return 作用在设备 X 轴上的加速度力（m/s²），包含重力 */
	public float getAccelerometerX ();

	/** @return 作用在设备 Y 轴上的加速度力（m/s²），包含重力 */
	public float getAccelerometerY ();

	/** @return 作用在设备 Z 轴上的加速度力（m/s²），包含重力 */
	public float getAccelerometerZ ();

	/** @return 绕 X 轴的旋转角速度（rad/s） */
	public float getGyroscopeX ();

	/** @return 绕 Y 轴的旋转角速度（rad/s） */
	public float getGyroscopeY ();

	/** @return 绕 Z 轴的旋转角速度（rad/s） */
	public float getGyroscopeZ ();

	/** @return 支持的最大触摸点数 */
	public int getMaxPointers ();

	/** @return 最后触摸点的 X 坐标（触摸屏设备）或鼠标的 X 坐标（桌面），屏幕原点为左上角 */
	public int getX ();

	/** 返回指定触摸点的 X 坐标（屏幕坐标）。触摸点从 0 开始索引。
	 * 触摸点 ID 标识手指按下屏幕的顺序，例如 0 是第一根手指，1 是第二根，以此类推。
	 * 当两根手指按下且第一根抬起时，第二根保持其索引。
	 * 如果有新手指触摸屏幕，将使用第一个空闲索引。
	 * 
	 * @param pointer 触摸点 ID
	 * @return X 坐标 */
	public int getX (int pointer);

	/** @return 当前指针位置与上一指针位置在 X 轴上的差值 */
	public int getDeltaX ();

	/** @return 当前指针位置与上一指针位置在 X 轴上的差值 */
	public int getDeltaX (int pointer);

	/** @return 最后触摸点的 Y 坐标（触摸屏设备）或鼠标的 Y 坐标（桌面），屏幕原点为左上角 */
	public int getY ();

	/** 返回指定触摸点的 Y 坐标（屏幕坐标）。触摸点从 0 开始索引。
	 * 触摸点 ID 标识手指按下屏幕的顺序，例如 0 是第一根手指，1 是第二根，以此类推。
	 * 当两根手指按下且第一根抬起时，第二根保持其索引。
	 * 如果有新手指触摸屏幕，将使用第一个空闲索引。
	 * 
	 * @param pointer 触摸点 ID
	 * @return Y 坐标 */
	public int getY (int pointer);

	/** @return 当前指针位置与上一指针位置在 Y 轴上的差值 */
	public int getDeltaY ();

	/** @return 当前指针位置与上一指针位置在 Y 轴上的差值 */
	public int getDeltaY (int pointer);

	/** @return 屏幕当前是否被触摸 */
	public boolean isTouched ();

	/** @return 是否刚刚发生了触摸按下事件 */
	public boolean justTouched ();

	/** 检查指定索引的触摸点当前是否正在触摸屏幕。触摸点从 0 开始索引。
	 *
	 * @param pointer 触摸点 ID
	 * @return 该触摸点是否正在触摸屏幕 */
	public boolean isTouched (int pointer);

	/** @return 第一个触摸点的压力值 */
	public float getPressure ();

	/** 返回指定触摸点的压力值，0 表示未触摸。
	 * Android 上最高约为 1.0，但可能略高且不同设备不一致。
	 * iOS 上 1.0 为正常触摸。
	 * 可用性可通过 {@link Input#isPeripheralAvailable(Peripheral)} 检查。
	 * 如果不支持，触摸时返回 1.0。
	 *
	 * @param pointer 触摸点 ID
	 * @return 压力值 */
	public float getPressure (int pointer);

	/** 检查指定的鼠标按钮是否按下。按钮常量见 {@link Buttons}。
	 * Android 4.0 之前仅有 Buttons#LEFT 有意义。
	 * @param button 要检查的按钮
	 * @return 是否按下 */
	public boolean isButtonPressed (int button);

	/** 检查指定的鼠标按钮是否刚刚被按下。按钮常量见 {@link Buttons}。
	 * Android 4.0 之前仅有 Buttons#LEFT 有意义。
	 * WebGL（GWT）上仅支持 LEFT、RIGHT 和 MIDDLE。
	 *
	 * @param button 要检查的按钮
	 * @return true 或 false */
	public boolean isButtonJustPressed (int button);

	/** 检查键是否被按下。
	 * 
	 * @param key 键码，参见 {@link Input.Keys}
	 * @return true 或 false */
	public boolean isKeyPressed (int key);

	/** 检查键是否刚刚被按下（仅触发一次）。
	 * 
	 * @param key 键码，参见 {@link Input.Keys}
	 * @return true 或 false */
	public boolean isKeyJustPressed (int key);

	/** 系统相关的文本输入方法。将创建一个带有指定标题和消息的对话框。
	 * 使用默认键盘类型。对话框关闭后，提供的 {@link TextInputListener} 将在渲染线程上被调用。
	 * 
	 * @param listener 文本输入监听器
	 * @param title 文本输入对话框的标题
	 * @param text 向用户显示的提示文本 */
	public void getTextInput (TextInputListener listener, String title, String text, String hint);

	/** 系统相关的文本输入方法。将创建一个带有指定标题和消息的对话框。
	 * 对话框关闭后，提供的 {@link TextInputListener} 将在渲染线程上被调用。
	 *
	 * @param listener 文本输入监听器
	 * @param title 文本输入对话框的标题
	 * @param text 向用户显示的提示文本
	 * @param type 要显示的键盘类型 */
	public void getTextInput (TextInputListener listener, String title, String text, String hint, OnscreenKeyboardType type);

	/** 设置屏幕键盘是否可见（如果可用）。使用默认键盘类型。
	 * 
	 * @param visible 是否可见 */
	public void setOnscreenKeyboardVisible (boolean visible);

	/** 设置屏幕键盘是否可见（如果可用）。
	 *
	 * @param visible 是否可见
	 * @param type 要显示的键盘类型。隐藏时可传入 null */
	public void setOnscreenKeyboardVisible (boolean visible, OnscreenKeyboardType type);

	static interface InputStringValidator {
		/** @param toCheck 要验证的字符串
		 * @return true 表示可接受，false 表示不可接受 */
		boolean validate (String toCheck);
	}

	/** 使用原生输入配置打开屏幕键盘（如果可用）。
	 *
	 * @param configuration 原生输入字段的配置 */
	public void openTextInputField (NativeInputConfiguration configuration);

	/** 关闭原生输入字段并将结果应用到输入包装器。
	 * @param isConfirmative 关闭是否可视为确认。将传递给 {@link NativeInputCloseCallback} */
	public default void closeTextInputField (boolean isConfirmative) {
		closeTextInputField(isConfirmative, null);
	}

	/** 关闭原生输入字段并将结果应用到输入包装器。
	 * @param isConfirmative 关闭是否可视为确认。将传递给 {@link NativeInputCloseCallback}
	 * @param callback 可选的额外回调，在关闭处理完成后于主线程调用 */
	public default void closeTextInputField (boolean isConfirmative, @Null NativeInputCloseCallback callback) {

	}

	/** @return 原生输入字段当前是否已打开 */
	public default boolean isTextInputFieldOpened () {
		return false;
	}

	static interface KeyboardHeightObserver {
		/** 当操作系统检测到键盘高度变化时调用。在使用 {@link Input#openTextInputField} 时会包含编辑框高度。
		 * 在 {@link KeyboardHeightObserver#onKeyboardShow} 或
		 * {@link KeyboardHeightObserver#onKeyboardHide} 之后调用 */
		void onKeyboardHeightChanged (int height);

		/** 当键盘可见时调用，报告可见高度。在使用 {@link Input#openTextInputField} 时会包含编辑框高度。
		 * 如果键盘调整形状，可能会被多次调用而无需关闭。
		 * 在 Android SDK < 30 和浮动键盘上，即使键盘不可见也总是被调用。 */
		void onKeyboardShow (int height);

		/** 当键盘不可见时调用。在 Android SDK 30 之前为尽力而为。
		 * 在 Android SDK < 30 和浮动键盘上永远不会被调用。 */
		void onKeyboardHide ();
	}

	/** 设置键盘高度回调。每当键盘高度变化时被调用。
	 * 注意：使用 openTextInputField 时，也会报告原生输入字段的高度。 */
	public void setKeyboardHeightObserver (KeyboardHeightObserver observer);

	public enum OnscreenKeyboardType {
		Default, NumberPad, PhonePad, Email, Password, URI
	}

	/** 生成指定持续时间的简单触觉效果，或在没有触觉能力的设备上产生振动效果。
	 * 注意在 Android 上需要在 manifest 中添加
	 * <code> <uses-permission android:name="android.permission.VIBRATE" /></code> 权限。
	 * 在 iOS 上需要设置 <code>useHaptics = true</code>。
	 * 
	 * @param milliseconds 振动持续时间（毫秒） */
	public void vibrate (int milliseconds);

	/** 生成指定持续时间和默认振幅的简单触觉效果。
	 *
	 * @param milliseconds 触觉效果持续时间（毫秒）
	 * @param fallback 在没有触觉能力的设备上是否使用非触觉振动器作为备选 */
	public void vibrate (int milliseconds, boolean fallback);

	/** 生成指定持续时间、振幅的简单触觉效果。
	 *
	 * @param milliseconds 触觉效果持续时间（毫秒）
	 * @param amplitude 触觉效果的强度。有效范围 [0, 255]
	 * @param fallback 是否在没有触觉能力的设备上使用非触觉振动器备选 */
	public void vibrate (int milliseconds, int amplitude, boolean fallback);

	/** 生成指定类型的简单触觉效果。VibrationTypes 是长度/振幅预设效果，因设备而异。
	 *
	 * @param vibrationType 振动类型 */
	public void vibrate (VibrationType vibrationType);

	public enum VibrationType {
		LIGHT, MEDIUM, HEAVY;
	}

	/** 获取设备的方位角（绕 z 轴的角度）。正 z 轴指向地心。
	 * 
	 * @return 方位角（度） */
	public float getAzimuth ();

	/** 获取设备的俯仰角（绕 x 轴的角度）。正 x 轴大致指向西方。
	 * @return 俯仰角（度） */
	public float getPitch ();

	/** 获取设备的横滚角（绕 y 轴的角度）。正 y 轴指向地磁北极。
	 * @return 横滚角（度） */
	public float getRoll ();

	/** 返回描述设备旋转的旋转矩阵。如果平台没有加速度计，则不修改矩阵。
	 * @param matrix 输出旋转矩阵的 float 数组 */
	public void getRotationMatrix (float[] matrix);

	/** @return 当前报告给 {@link InputProcessor} 的事件时间戳 */
	public long getCurrentEventTime ();

	/** 设置在 Android 或 GWT 上是否捕获指定按键。在其他平台上无效果。
	 * 未被捕获的按键可能被其他应用或后台进程处理（Android），或触发浏览器默认行为（GWT）。
	 * 例如，媒体键或音量键会被后台媒体播放器处理，空格键可能触发滚动。
	 * 游戏所需的所有按键都应该被捕获，以防止意外行为。
	 *
	 * @param keycode 要捕获的键码
	 * @param catchKey 是否捕获该键码 */
	public void setCatchKey (int keycode, boolean catchKey);

	/** @param keycode 要检查的键码
	 * @return 该键码是否被配置为捕获 */
	public boolean isCatchKey (int keycode);

	/** 设置 {@link InputProcessor}，它将接收所有触摸和键盘输入事件。
	 * 它会在每帧的 {@link ApplicationListener#render()} 之前被调用。
	 * 
	 * @param processor InputProcessor */
	public void setInputProcessor (InputProcessor processor);

	/** @return 当前设置的 {@link InputProcessor}，或 null */
	public InputProcessor getInputProcessor ();

	/** 查询 {@link Peripheral} 是否可用。
	 * Android 上的 {@link Peripheral#HardwareKeyboard} 返回键盘当前是否滑出。
	 * 
	 * @param peripheral 外设类型
	 * @return 是否可用 */
	public boolean isPeripheralAvailable (Peripheral peripheral);

	/** @return 设备相对于其原始方向的旋转角度 */
	public int getRotation ();

	/** @return 设备的原始方向 */
	public Orientation getNativeOrientation ();

	public enum Orientation {
		Landscape, Portrait
	}

	/** 仅在桌面上有效。将鼠标光标限制在窗口内并隐藏鼠标光标。
	 * 仍然报告 X/Y 坐标，就像鼠标未被捕获一样。
	 * @param catched 是否捕获鼠标 */
	public void setCursorCatched (boolean catched);

	/** @return 鼠标光标当前是否被捕获 */
	public boolean isCursorCatched ();

	/** 仅在桌面上有效。将鼠标光标位置设置到指定的窗口坐标（原点为左上角）。
	 * @param x X 位置
	 * @param y Y 位置 */
	public void setCursorPosition (int x, int y);
}
