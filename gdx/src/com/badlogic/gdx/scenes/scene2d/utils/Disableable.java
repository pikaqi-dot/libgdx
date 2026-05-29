/** <b>可禁用接口，支持禁用状态的控件实现此接口。</b>
* @author Nathan Sweet */

package com.badlogic.gdx.scenes.scene2d.utils;

public interface Disableable {
	public void setDisabled (boolean isDisabled);

	public boolean isDisabled ();
}
