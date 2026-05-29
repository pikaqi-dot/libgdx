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

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.GL32;

/**
 * <b>全局环境类。</b>
 * 持有 {@link Application}、{@link Graphics}、{@link Audio}、{@link Files}、{@link Input} 和 {@link Net} 实例的引用。
 * 所有引用均为 public static 字段，允许对所有子系统进行静态访问。
 * <p>
 * <b>注意：</b>不要在非渲染线程中使用 Graphics。
 * </p>
 * <p>
 * 这种全局静态访问方式通常被视为设计上的"坏味道"，但在 libGDX 的上下文中，这比替代方案更好。
 * 
 * @author mzechner */
public class Gdx {
	/** 应用程序实例，提供应用级别的方法（生命周期、日志、偏好设置等） */
	public static Application app;
	/** 图形模块，用于渲染操作、设置窗口大小、帧率控制等 */
	public static Graphics graphics;
	/** 音频模块，用于播放音乐和音效 */
	public static Audio audio;
	/** 输入模块，用于处理键盘、鼠标、触摸等输入 */
	public static Input input;
	/** 文件模块，用于读写内部和外部文件 */
	public static Files files;
	/** 网络模块，用于 HTTP 请求和 Socket 通信 */
	public static Net net;

	/** OpenGL 2.0 API */
	public static GL20 gl;
	/** OpenGL 2.0 API（与 gl 相同） */
	public static GL20 gl20;
	/** OpenGL 3.0 API */
	public static GL30 gl30;
	/** OpenGL 3.1 API */
	public static GL31 gl31;
	/** OpenGL 3.2 API */
	public static GL32 gl32;
}
