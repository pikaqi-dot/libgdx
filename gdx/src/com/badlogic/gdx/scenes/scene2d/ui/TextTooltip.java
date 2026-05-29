/** <b>文本提示框，悬停时显示文本提示。</b>
* @author Nathan Sweet */

package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Null;

/** A tooltip that shows a label.
 * @author Nathan Sweet */
public class TextTooltip extends Tooltip<Label> implements Styleable<TextTooltip.TextTooltipStyle> {
	private TextTooltipStyle style;

	public TextTooltip (@Null String text, Skin skin) {
		this(text, TooltipManager.getInstance(), skin.get(TextTooltipStyle.class));
	}

	public TextTooltip (@Null String text, Skin skin, String styleName) {
		this(text, TooltipManager.getInstance(), skin.get(styleName, TextTooltipStyle.class));
	}

	public TextTooltip (@Null String text, TextTooltipStyle style) {
		this(text, TooltipManager.getInstance(), style);
	}

	public TextTooltip (@Null String text, TooltipManager manager, Skin skin) {
		this(text, manager, skin.get(TextTooltipStyle.class));
	}

	public TextTooltip (@Null String text, TooltipManager manager, Skin skin, String styleName) {
		this(text, manager, skin.get(styleName, TextTooltipStyle.class));
	}

	public TextTooltip (@Null String text, final TooltipManager manager, TextTooltipStyle style) {
		super(null, manager);

		container.setActor(newLabel(text, style.label));

		setStyle(style);
	}

	protected Label newLabel (String text, LabelStyle style) {
		return new Label(text, style);
	}

	public void setStyle (TextTooltipStyle style) {
		if (style == null) throw new NullPointerException("style cannot be null");
		this.style = style;
		container.setBackground(style.background);
		container.maxWidth(style.wrapWidth);

		boolean wrap = style.wrapWidth != 0;
		container.fill(wrap);

		Label label = container.getActor();
		label.setStyle(style.label);
		label.setWrap(wrap);
	}

	public TextTooltipStyle getStyle () {
		return style;
	}

	/** The style for a text tooltip, see {@link TextTooltip}.
	 * @author Nathan Sweet */
	static public class TextTooltipStyle {
		public LabelStyle label;
		public @Null Drawable background;
		/** 0 means don't wrap. */
		public float wrapWidth;

		public TextTooltipStyle () {
		}

		public TextTooltipStyle (LabelStyle label, @Null Drawable background) {
			this.label = label;
			this.background = background;
		}

		public TextTooltipStyle (TextTooltipStyle style) {
			label = new LabelStyle(style.label);
			background = style.background;
			wrapWidth = style.wrapWidth;
		}
	}
}
