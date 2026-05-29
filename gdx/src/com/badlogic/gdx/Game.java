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
 * <b>游戏类，屏幕管理器。</b>
 * 一个 {@link ApplicationListener} 的抽象实现，将生命周期方法委托给 {@link Screen}。
 * 这使得应用程序可以轻松管理多个屏幕（如：菜单屏幕、游戏屏幕、设置屏幕等）。
 * </p>
 * <p>
 * <b>注意：</b>Screen 不会自动释放。你需要自行决定切换屏幕时是保留还是释放旧的 Screen。
 * </p>
 * 
 * <p>
 * <b>使用示例：</b><br>
 * <code>
 * public class MyGame extends Game {<br>
 * &nbsp;&nbsp;public void create() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;setScreen(new MainMenuScreen(this));<br>
 * &nbsp;&nbsp;}<br>
 * }
 * </code>
 * </p>
 */
public abstract class Game implements ApplicationListener {
	/** 当前活跃的屏幕 */
	protected Screen screen;

	@Override
	public void dispose () {
		if (screen != null) screen.hide();
	}

	@Override
	public void pause () {
		if (screen != null) screen.pause();
	}

	@Override
	public void resume () {
		if (screen != null) screen.resume();
	}

	@Override
	public void render () {
		if (screen != null) screen.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void resize (int width, int height) {
		if (screen != null) screen.resize(width, height);
	}

	/** 设置当前屏幕。会调用旧屏幕的 {@link Screen#hide()} 和新屏幕的 {@link Screen#show()}。
	 * @param screen 新屏幕，可以为 {@code null} */
	public void setScreen (Screen screen) {
		if (this.screen != null) this.screen.hide();
		this.screen = screen;
		if (this.screen != null) {
			this.screen.show();
			this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

	/** @return 当前活跃的 {@link Screen} */
	public Screen getScreen () {
		return screen;
	}
}
