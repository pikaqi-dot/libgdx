/** <b>事件监听器接口。</b>
* 所有 Scene2D 事件监听器的基接口。
* @author Nathan Sweet */

package com.badlogic.gdx.scenes.scene2d;

/** Low level interface for receiving events. Typically there is a listener class for each specific event class.
 * @see InputListener
 * @see InputEvent
 * @author Nathan Sweet */
public interface EventListener {
	/** Try to handle the given event, if it is applicable.
	 * @return true if the event should be considered {@link Event#handle() handled} by scene2d. */
	public boolean handle (Event event);
}
