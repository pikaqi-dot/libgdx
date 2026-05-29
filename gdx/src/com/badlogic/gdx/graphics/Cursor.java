
package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.utils.Disposable;

/** <b>鼠标光标接口。</b>
* 表示一个自定义鼠标光标。
* @author mzechner */
public interface Cursor extends Disposable {

	public static enum SystemCursor {
		Arrow, Ibeam, Crosshair, Hand, HorizontalResize, VerticalResize, NWSEResize, NESWResize, AllResize, NotAllowed, None
	}
}
