/** <b>可绘制对象接口，用于 UI 控件背景/图标绘制。</b>
* @author Nathan Sweet */

package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.graphics.g2d.Batch;

/** A drawable knows how to draw itself at a given rectangular size. It provides padding sizes and a minimum size so that other
 * code can determine how to size and position content.
 * @author Nathan Sweet */
public interface Drawable {
	/** Draws this drawable at the specified bounds. The drawable should be tinted with {@link Batch#getColor()}, possibly by
	 * mixing its own color. */
	public void draw (Batch batch, float x, float y, float width, float height);

	public float getLeftWidth ();

	public void setLeftWidth (float leftWidth);

	public float getRightWidth ();

	public void setRightWidth (float rightWidth);

	public float getTopHeight ();

	public void setTopHeight (float topHeight);

	public float getBottomHeight ();

	public void setBottomHeight (float bottomHeight);

	default public void setPadding (float topHeight, float leftWidth, float bottomHeight, float rightWidth) {
		setTopHeight(topHeight);
		setLeftWidth(leftWidth);
		setBottomHeight(bottomHeight);
		setRightWidth(rightWidth);
	}

	default public void setPadding (float padding) {
		setPadding(padding, padding, padding, padding);
	}

	default public void setPadding (Drawable from) {
		setPadding(from.getTopHeight(), from.getLeftWidth(), from.getBottomHeight(), from.getRightWidth());
	}

	public float getMinWidth ();

	public void setMinWidth (float minWidth);

	public float getMinHeight ();

	public void setMinHeight (float minHeight);

	default public void setMinSize (float minWidth, float minHeight) {
		setMinWidth(minWidth);
		setMinHeight(minHeight);
	}
}
