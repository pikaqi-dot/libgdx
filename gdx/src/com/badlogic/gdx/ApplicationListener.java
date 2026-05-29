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

/**
 * <p>
 * <b>应用生命周期监听器。</b>
 * <code>ApplicationListener</code> 在 {@link Application} 被创建、恢复、渲染、暂停或销毁时被调用。
 * 所有方法都在拥有 OpenGL 上下文的线程中调用，因此你可以安全地创建和操作图形资源。
 * </p>
 * 
 * <p>
 * <code>ApplicationListener</code> 接口遵循标准的 Android Activity 生命周期，并在桌面上相应模拟。
 * </p>
 * 
 * <p>
 * <b>生命周期顺序：</b><br>
 * 1. {@link #create()} — 应用首次创建时调用<br>
 * 2. {@link #resize(int, int)} — 窗口大小改变时调用<br>
 * 3. {@link #render()} — 每帧渲染时重复调用<br>
 * 4. {@link #pause()} — 应用暂停时调用（失去焦点/后台运行）<br>
 * 5. {@link #resume()} — 应用从暂停恢复时调用<br>
 * 6. {@link #dispose()} — 应用销毁时调用（伴随 pause()）<br>
 * </p>
 *
 * @author mzechner */
public interface ApplicationListener {
	/** 当 {@link Application} 首次创建时调用。在此方法中进行初始化操作，如加载纹理、创建精灵等。 */
	public void create ();

	/** 当 {@link Application} 调整大小时调用。可在非暂停状态的任意时刻发生，但一定在 {@link #create()} 之后。
	 * 
	 * @param width 新的宽度（像素）
	 * @param height 新的高度（像素） */
	public void resize (int width, int height);

	/** 当 {@link Application} 需要渲染自身时调用。这是游戏主循环的核心方法，每帧都会调用。 */
	public void render ();

	/** 当 {@link Application} 暂停时调用，通常是在应用不活跃或不可见时。应用在销毁前也会先被暂停。 */
	public void pause ();

	/** 当 {@link Application} 从暂停状态恢复时调用，通常是在重新获得焦点时。 */
	public void resume ();

	/** 当 {@link Application} 被销毁时调用。在此之前会先调用 {@link #pause()}。在此方法中释放所有资源。 */
	public void dispose ();
}
