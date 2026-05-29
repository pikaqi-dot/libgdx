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

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.graphics.glutils.IndexBufferObject;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexArray;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;

/**
 * <b>图形模块接口。</b>
 * 封装了与图形处理器（GPU）的通信。根据硬件和当前 {@link Application} 配置，
 * 提供对 {@link GL20}、{@link GL30}、{@link GL31} 和 {@link GL32} 的访问。
 * <p>
 * 如果后端支持，可以通过此接口查询可用的显示模式（分辨率和色深）并切换。
 * <p>
 * 此接口可用于切换连续渲染和非连续渲染（见 {@link #setContinuousRendering(boolean)}），
 * 以及显式 {@link #requestRendering()} 请求渲染。
 * <p>
 * libGDX 还有许多不直接由 Graphics 接口生成的实用图形类，参见 {@link VertexArray}、
 * {@link VertexBufferObject}、{@link IndexBufferObject}、{@link Mesh}、{@link ShaderProgram}、
 * {@link FrameBuffer}、{@link BitmapFont}、{@link Batch} 等。
 * 所有这些都是托管资源，在上下文丢失时无需重新加载。
 * 
 * @author mzechner */
public interface Graphics {
	/** Graphics 实现类型枚举
	 *
	 * @author mzechner */
	enum GraphicsType {
		AndroidGL, LWJGL, WebGL, iOSGL, JGLFW, Mock, LWJGL3
	}

	/** 描述全屏显示模式
	 *
	 * @author mzechner */
	class DisplayMode {
		/** 物理像素宽度 **/
		public final int width;
		/** 物理像素高度 **/
		public final int height;
		/** 刷新率（赫兹） **/
		public final int refreshRate;
		/** 每像素位数（可能不包含 alpha 通道） **/
		public final int bitsPerPixel;

		protected DisplayMode (int width, int height, int refreshRate, int bitsPerPixel) {
			this.width = width;
			this.height = height;
			this.refreshRate = refreshRate;
			this.bitsPerPixel = bitsPerPixel;
		}

		public String toString () {
			return width + "x" + height + ", bpp: " + bitsPerPixel + ", hz: " + refreshRate;
		}
	}

	/** 描述显示器
	 *
	 * @author badlogic */
	class Monitor {
		/** 虚拟坐标 X（多显示器布局中的位置） */
		public final int virtualX;
		/** 虚拟坐标 Y（多显示器布局中的位置） */
		public final int virtualY;
		/** 显示器名称 */
		public final String name;

		protected Monitor (int virtualX, int virtualY, String name) {
			this.virtualX = virtualX;
			this.virtualY = virtualY;
			this.name = name;
		}
	}

	/** 描述缓冲格式：每像素位数、深度缓冲精度、模板缓冲精度和 MSAA 采样数。 */
	class BufferFormat {
		/** 每个颜色通道的位数（红、绿、蓝、透明度） */
		public final int r, g, b, a;
		/** 深度缓冲和模板缓冲的位数 */
		public final int depth, stencil;
		/** 多重采样抗锯齿（MSAA）的采样数 **/
		public final int samples;
		/** 是否使用覆盖采样抗锯齿（CSAA）。如果是，还需要清除覆盖缓冲！ */
		public final boolean coverageSampling;

		public BufferFormat (int r, int g, int b, int a, int depth, int stencil, int samples, boolean coverageSampling) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
			this.depth = depth;
			this.stencil = stencil;
			this.samples = samples;
			this.coverageSampling = coverageSampling;
		}

		public String toString () {
			return "r: " + r + ", g: " + g + ", b: " + b + ", a: " + a + ", depth: " + depth + ", stencil: " + stencil
				+ ", num samples: " + samples + ", coverage sampling: " + coverageSampling;
		}
	}

	/** 检查 OpenGL ES 3.0 是否可用。
	 * 如果可用，可以通过 {@link #getGL30()} 获取 {@link GL30} 实例来使用 OpenGL ES 3.0 功能。
	 * 注意：这仅在 Application 配置为使用 OpenGL ES 3.0 时才有效！
	 *
	 * @return OpenGL ES 3.0 是否可用 */
	boolean isGL30Available ();

	/** 检查 OpenGL ES 3.1 是否可用。
	 * 如果可用，可以通过 {@link #getGL31()} 获取 {@link GL31} 实例来使用 OpenGL ES 3.1 功能。
	 * 注意：这仅在 Application 配置为使用 OpenGL ES 3.1 时才有效！
	 *
	 * @return OpenGL ES 3.1 是否可用 */
	boolean isGL31Available ();

	/** 检查 OpenGL ES 3.2 是否可用。
	 * 如果可用，可以通过 {@link #getGL32()} 获取 {@link GL32} 实例来使用 OpenGL ES 3.2 功能。
	 * 注意：这仅在 Application 配置为使用 OpenGL ES 3.2 时才有效！
	 *
	 * @return OpenGL ES 3.2 是否可用 */
	boolean isGL32Available ();

	/** @return {@link GL20} 实例 */
	GL20 getGL20 ();

	/** @return {@link GL30} 实例，如果不支持则返回 null */
	GL30 getGL30 ();

	/** @return {@link GL31} 实例，如果不支持则返回 null */
	GL31 getGL31 ();

	/** @return {@link GL32} 实例，如果不支持则返回 null */
	GL32 getGL32 ();

	/** 设置 GL20 实例 **/
	void setGL20 (GL20 gl20);

	/** 设置 GL30 实例 **/
	void setGL30 (GL30 gl30);

	/** 设置 GL31 实例 **/
	void setGL31 (GL31 gl31);

	/** 设置 GL32 实例 **/
	void setGL32 (GL32 gl32);

	/** @return 客户区宽度（逻辑像素） */
	int getWidth ();

	/** @return 客户区高度（逻辑像素） */
	int getHeight ();

	/** @return 帧缓冲宽度（物理像素） */
	int getBackBufferWidth ();

	/** @return 帧缓冲高度（物理像素） */
	int getBackBufferHeight ();

	/** @return 每个逻辑像素（点）对应的物理像素数 */
	float getBackBufferScale ();

	/** @return 屏幕左侧避开显示屏切口的边距（逻辑像素） */
	int getSafeInsetLeft ();

	/** @return 屏幕顶部避开显示屏切口的边距（逻辑像素） */
	int getSafeInsetTop ();

	/** @return 屏幕底部避开显示屏切口或手势导航栏的边距（逻辑像素） */
	int getSafeInsetBottom ();

	/** @return 屏幕右侧避开显示屏切口的边距（逻辑像素） */
	int getSafeInsetRight ();

	/** 返回当前帧的 ID。规则是：ID 只在应用处于运行状态时、调用
	 * {@link ApplicationListener#render()} 之前递增。第一帧的 ID 为 0，
	 * 后续帧的 ID 在 2<sup>63</sup>-1 个渲染周期内保证递增。
	 * @return 当前帧的 ID */
	long getFrameId ();

	/** @return 当前帧与上一帧之间的时间间隔（秒） */
	float getDeltaTime ();

	/** @return 当前帧与上一帧之间的原始时间间隔（秒），不做平滑处理
	 * @deprecated 推荐使用 {@link #getDeltaTime()} 替代 */
	@Deprecated
	float getRawDeltaTime ();

	/** @return 每秒平均帧数（FPS） */
	int getFramesPerSecond ();

	/** @return 此 Graphics 实例的 {@link GraphicsType} */
	GraphicsType getType ();

	/** @return 此 Graphics 实例的 OpenGL 版本信息 {@link GLVersion} */
	GLVersion getGLVersion ();

	/** @return X 轴上每英寸的像素数（PPI） */
	float getPpiX ();

	/** @return Y 轴上每英寸的像素数（PPI） */
	float getPpiY ();

	/** @return X 轴上每厘米的像素数 */
	float getPpcX ();

	/** @return Y 轴上每厘米的像素数 */
	float getPpcY ();

	/** 返回密度独立像素（DIP）的缩放因子，遵循与 android.util.DisplayMetrics#density 相同的约定。
	 * 一个 DIP 在约 160 dpi 的屏幕上相当于一个物理像素。
	 * 因此在 160dpi 屏幕上密度值为 1；在 120dpi 屏幕上为 0.75；以此类推。
	 *
	 * 如果无法确定密度，则返回默认值 1。
	 *
	 * 注意：根据底层平台实现，这可能是一个相对昂贵的操作，不应在每帧连续调用。
	 *
	 * @return 显示屏的密度独立像素因子 */
	float getDensity ();

	/** 检查当前后端是否支持通过 {@link Graphics#setFullscreenMode(DisplayMode)} 更改显示模式
	 *
	 * @return 是否支持显示模式更改 */
	boolean supportsDisplayModeChange ();

	/** @return 主显示器 **/
	Monitor getPrimaryMonitor ();

	/** @return 应用程序窗口所在的显示器 */
	Monitor getMonitor ();

	/** @return 当前连接的 {@link Monitor} 列表 */
	Monitor[] getMonitors ();

	/** @return 窗口所在显示器支持的 {@link DisplayMode}（全屏显示模式）列表 */
	DisplayMode[] getDisplayModes ();

	/** @return 指定 {@link Monitor} 支持的 {@link DisplayMode} 列表 */
	DisplayMode[] getDisplayModes (Monitor monitor);

	/** @return 窗口所在显示器的当前 {@link DisplayMode} */
	DisplayMode getDisplayMode ();

	/** @return 指定 {@link Monitor} 的当前 {@link DisplayMode} */
	DisplayMode getDisplayMode (Monitor monitor);

	/** 设置窗口为全屏模式。
	 *
	 * @param displayMode 显示模式
	 * @return 操作是否成功 */
	boolean setFullscreenMode (DisplayMode displayMode);

	/** 设置窗口为窗口模式。
	 *
	 * @param width 窗口宽度（像素）
	 * @param height 窗口高度（像素）
	 * @return 操作是否成功 */
	boolean setWindowedMode (int width, int height);

	/** 设置窗口标题。Android 上忽略此设置。
	 *
	 * @param title 标题 */
	void setTitle (String title);

	/** 启用或禁用窗口装饰（边框）。在 Android 上用于启用/禁用菜单栏。
	 *
	 * 注意：此方法的即时行为可能因具体实现而异。可能需要重新创建窗口才能使更改生效。
	 *
	 * 所有桌面后端和 Android（用于禁用菜单栏）均支持此功能。
	 *
	 * @param undecorated true 隐藏窗口边框或状态栏，false 显示 */
	void setUndecorated (boolean undecorated);

	/** 设置窗口是否可调整大小。Android 上忽略此设置。
	 *
	 * 注意：此方法的即时行为可能因具体实现而异。可能需要重新创建窗口才能使更改生效。
	 *
	 * 所有桌面后端均支持此功能。
	 *
	 * @param resizable 是否可调整大小 */
	void setResizable (boolean resizable);

	/** 启用/禁用垂直同步（VSync）。这是尽力而为的尝试，可能并非在所有平台上都有效。
	 *
	 * @param vsync 是否启用垂直同步 */
	void setVSync (boolean vsync);

	/** 设置连续渲染时的目标帧率。可能并非在所有平台上都有效。
	 * 通常不建议在移动平台上使用此方法。
	 *
	 * @param fps 目标帧率；默认值因平台而异 */
	public void setForegroundFPS (int fps);

	/** @return 颜色、深度和模板缓冲区的 {@link BufferFormat} 实例 */
	BufferFormat getBufferFormat ();

	/** @param extension OpenGL 扩展名
	 * @return 是否支持该扩展 */
	boolean supportsExtension (String extension);

	/** 设置是否连续渲染。在非连续渲染模式下，以下事件会触发重绘：
	 *
	 * <ul>
	 * <li>调用 {@link #requestRendering()}</li>
	 * <li>触摸屏/鼠标或键盘的输入事件</li>
	 * <li>通过 {@link Application#postRunnable(Runnable)} 向渲染线程投递任务</li>
	 * </ul>
	 *
	 * 生命周期事件也会照常报告，参见 {@link ApplicationListener}。
	 * 此方法可以从任何线程调用。
	 *
	 * @param isContinuous 是否连续渲染 */
	void setContinuousRendering (boolean isContinuous);

	/** @return 当前是否为连续渲染模式 */
	boolean isContinuousRendering ();

	/** 在非连续渲染模式下请求渲染新帧。此方法可以从任何线程调用。 */
	void requestRendering ();

	/** @return 应用是否为全屏模式 */
	boolean isFullscreen ();

	/** 通过 {@link Pixmap} 创建新的鼠标光标。
	 * Pixmap 必须为 RGBA8888 格式，宽高必须为大于零的 2 的幂（不一定相等），
	 * 且 alpha 透明度必须为单比特（即仅 0x00 或 0xFF）。
	 * 返回的 Cursor 对象可通过 {@link #setCursor(Cursor)} 设置为系统光标。
	 *
	 * @param pixmap 鼠标光标图像
	 * @param xHotspot 光标图像内热点像素的 x 位置（左上角为原点）
	 * @param yHotspot 光标图像内热点像素的 y 位置（左上角为原点）
	 * @return Cursor 对象，可用于 {@link #setCursor(Cursor)}，不支持时返回 null */
	Cursor newCursor (Pixmap pixmap, int xHotspot, int yHotspot);

	/** 设置鼠标光标图像为指定的 {@link Cursor}。
	 * 建议在主渲染线程中调用此函数，每帧最多一次。
	 *
	 * @param cursor 鼠标光标 */
	void setCursor (Cursor cursor);

	/** 设置预定义的 {@link SystemCursor} 系统光标 */
	void setSystemCursor (SystemCursor systemCursor);
}
