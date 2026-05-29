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

import com.badlogic.gdx.utils.Disposable;

/**
 * <p>
 * <b>屏幕接口。</b>
 * 表示应用程序的多个屏幕之一，例如主菜单、设置菜单、游戏画面等。
 * 配合 {@link Game} 类使用，通过 {@link Game#setScreen(Screen)} 实现屏幕切换。
 * </p>
 * <p>
 * <b>注意：</b>{@link #dispose()} 不会自动调用，需要在切换屏幕时手动释放资源。
 * </p>
 * <p>
 * <b>生命周期：</b><br>
 * - {@link #show()} — 屏幕成为当前活跃屏幕时调用<br>
 * - {@link #render(float)} — 每帧渲染时调用<br>
 * - {@link #resize(int, int)} — 窗口大小改变时调用<br>
 * - {@link #pause()} — 应用暂停时调用<br>
 * - {@link #resume()} — 应用恢复时调用<br>
 * - {@link #hide()} — 屏幕不再是当前屏幕时调用<br>
 * - {@link #dispose()} — 释放资源
 * </p>
 * 
 * @see Game */
public interface Screen extends Disposable {

	/** 当此屏幕成为 {@link Game} 的当前屏幕时调用。在此进行初始化操作。 */
	public void show ();

	/** 当屏幕需要渲染自身时调用。
	 * @param delta 自上次渲染以来的时间（秒） */
	public void render (float delta);

	/** @see ApplicationListener#resize(int, int) */
	public void resize (int width, int height);

	/** @see ApplicationListener#pause() */
	public void pause ();

	/** @see ApplicationListener#resume() */
	public void resume ();

	/** 当此屏幕不再是 {@link Game} 的当前屏幕时调用。在此进行清理操作。 */
	public void hide ();

	/** 当此屏幕需要释放所有资源时调用。 */
	public void dispose ();
}
