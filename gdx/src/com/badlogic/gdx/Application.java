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

import com.badlogic.gdx.utils.Clipboard;

/**
 * <p>
 * <b>应用程序主入口接口。</b>
 * <code>Application</code> 是项目的核心入口点。它创建一个窗口和渲染表面，并管理应用程序的不同模块，包括
 * {@link Graphics}（图形）、{@link Audio}（音频）、{@link Input}（输入）和 {@link Files}（文件）。
 * 可以将其理解为 Swing 中的 <code>JFrame</code> 或 Android 中的 <code>Activity</code>。
 * </p>
 * 
 * <p>
 * Application 可以有以下几种具体实现：
 * <ul>
 * <li>桌面应用（参见 gdx-backends-jglfw.jar 中的 <code>JglfwApplication</code>）</li>
 * <li>Android 应用（参见 gdx-backends-android.jar 中的 <code>AndroidApplication</code>）</li>
 * <li>HTML5 应用（参见 gdx-backends-gwt.jar 中的 <code>GwtApplication</code>）</li>
 * <li>iOS 应用（参见 gdx-backends-robovm.jar 中的 <code>IOSApplication</code>）</li>
 * </ul>
 * 每种实现都有各自的启动和初始化方法。
 * </p>
 * 
 * <p>
 * 虽然游戏开发者习惯使用主循环（main loop），但 libGDX 采用了不同的概念以更好地适应 Android 的事件驱动特性。
 * 你的应用逻辑需要实现 {@link ApplicationListener} 接口，该接口包含由 Application 在创建、恢复、暂停、
 * 销毁或渲染时调用的方法。作为开发者，你只需实现 ApplicationListener 接口并填充相应的功能。
 * ApplicationListener 作为参数传递给具体的 Application 构造函数。这意味着你只需编写一次程序逻辑，
 * 然后通过将其传递给不同的 Application 实现，即可在多个平台上运行。
 * </p>
 * 
 * <p>
 * Application 接口提供以下模块：
 * </p>
 * 
 * <p>
 * {@link Graphics} 提供各种将视觉内容输出到屏幕的方法。通过 OpenGL ES 2.0 或 3.0 实现。
 * 在桌面上使用桌面 OpenGL 模拟 OpenGL ES 2.0/3.0 的功能。Android 上使用 Java OpenGL ES 绑定。
 * </p>
 * 
 * <p>
 * {@link Audio} 提供播放和录制声音/音乐的方法。桌面上通过 Java Sound API 实现，Android 上使用 Android 媒体框架。
 * </p>
 * 
 * <p>
 * {@link Input} 提供从键盘、触摸屏、鼠标和加速度计轮询用户输入的方法。
 * 此外，你也可以实现 {@link InputProcessor} 并通过 {@link Input#setInputProcessor(InputProcessor)} 接收输入事件。
 * </p>
 * 
 * <p>
 * {@link Files} 提供访问内部和外部文件的方法。内部文件存储在应用附近；Android 上内部文件等同于 assets。
 * 桌面上首先在 classpath 中查找，失败后搜索应用根目录。外部文件存储在外部存储（SD卡或用户主目录）中。
 * 也可以指定绝对路径（不具可移植性，请谨慎使用）。
 * </p>
 * 
 * <p>
 * {@link Net} 提供网络操作方法，如执行 HTTP 请求、创建服务器和客户端 Socket。
 * </p>
 * 
 * <p>
 * Application 还提供查询操作系统信息的方法，以及简单的日志记录功能（桌面输出到标准输出，Android 输出到 logcat）。
 * </p>
 *
 * @author mzechner */
public interface Application {
	/** 可能的 {@link Application} 类型枚举
	 * 
	 * @author mzechner */
	public enum ApplicationType {
		Android, Desktop, HeadlessDesktop, Applet, WebGL, iOS
	}

	/** 不输出任何日志 */
	public static final int LOG_NONE = 0;
	/** 输出所有日志（包括调试信息） */
	public static final int LOG_DEBUG = 3;
	/** 输出信息和错误日志，不输出调试日志 */
	public static final int LOG_INFO = 2;
	/** 仅输出错误日志 */
	public static final int LOG_ERROR = 1;

	/** @return {@link ApplicationListener} 实例 */
	public ApplicationListener getApplicationListener ();

	/** @return {@link Graphics} 实例，用于图形渲染操作 */
	public Graphics getGraphics ();

	/** @return {@link Audio} 实例，用于音频播放和录制 */
	public Audio getAudio ();

	/** @return {@link Input} 实例，用于处理用户输入 */
	public Input getInput ();

	/** @return {@link Files} 实例，用于文件读写操作 */
	public Files getFiles ();

	/** @return {@link Net} 实例，用于网络操作 */
	public Net getNet ();

	/** 向控制台或 logcat 输出日志信息 */
	public void log (String tag, String message);

	/** 向控制台或 logcat 输出日志信息（带异常堆栈） */
	public void log (String tag, String message, Throwable exception);

	/** 向控制台或 logcat 输出错误信息 */
	public void error (String tag, String message);

	/** 向控制台或 logcat 输出错误信息（带异常堆栈） */
	public void error (String tag, String message, Throwable exception);

	/** 向控制台或 logcat 输出调试信息 */
	public void debug (String tag, String message);

	/** 向控制台或 logcat 输出调试信息（带异常堆栈） */
	public void debug (String tag, String message, Throwable exception);

	/**
	 * 设置日志级别。
	 * {@link #LOG_NONE} 关闭所有日志输出；
	 * {@link #LOG_ERROR} 仅允许错误信息；
	 * {@link #LOG_INFO} 允许非调试信息通过；
	 * {@link #LOG_DEBUG} 允许所有信息通过。
	 * @param logLevel 日志级别：{@link #LOG_NONE}、{@link #LOG_ERROR}、{@link #LOG_INFO} 或 {@link #LOG_DEBUG}
	 */
	public void setLogLevel (int logLevel);

	/** 获取当前日志级别 */
	public int getLogLevel ();

	/** 设置当前的 ApplicationLogger。此后对 {@link #log(String, String)} 的调用将委托给此 {@link ApplicationLogger} */
	public void setApplicationLogger (ApplicationLogger applicationLogger);

	/** @return 当前使用的 {@link ApplicationLogger} */
	public ApplicationLogger getApplicationLogger ();

	/** @return 当前应用的 {@link ApplicationType} 类型，例如 Android 或 Desktop */
	public ApplicationType getType ();

	/** @return Android 上返回 API 级别，iOS 上返回主版本号（5, 6, 7...），桌面上返回 0 */
	public int getVersion ();

	/** @return Java 堆内存使用量（字节） */
	public long getJavaHeap ();

	/** @return 本地堆内存使用量（字节） */
	public long getNativeHeap ();

	/** 返回该应用的 {@link Preferences} 实例，用于跨运行存储应用设置。
	 * @param name 偏好设置名称，必须可用作文件名
	 * @return 偏好设置对象 */
	public Preferences getPreferences (String name);

	public Clipboard getClipboard ();

	/** 在主循环线程上投递一个 {@link Runnable} 任务。
	 * 
	 * 在多窗口应用中，Runnable 执行时 {@linkplain Gdx#graphics} 和 {@linkplain Gdx#input} 的值可能不可预测。
	 * 如果需要图形或输入，请将其复制到局部变量中再在 Runnable 内使用。
	 * <p>
	 * <code> final Graphics graphics = Gdx.graphics;
	 * 
	 * @param runnable 要执行的任务 */
	public void postRunnable (Runnable runnable);

	/** 安排应用退出。在 Android 上，这将导致稍后调用 pause() 和 dispose()，但不会立即结束应用。
	 * 在 iOS 上应避免在生产环境中使用，因为违反 Apple 的指南。 */
	public void exit ();

	/** 向应用添加新的 {@link LifecycleListener}。
	 * 扩展可通过此方法更轻松地接入生命周期。对于应用级开发，{@link ApplicationListener} 的方法已经足够。
	 * @param listener 生命周期监听器 */
	public void addLifecycleListener (LifecycleListener listener);

	/** 移除 {@link LifecycleListener}。
	 * @param listener 生命周期监听器 */
	public void removeLifecycleListener (LifecycleListener listener);
}
