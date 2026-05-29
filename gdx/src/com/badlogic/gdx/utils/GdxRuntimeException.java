/** <b>libGDX 运行时异常。</b>
* @author Nathan Sweet */

package com.badlogic.gdx.utils;

/** Typed runtime exception used throughout libGDX
 * 
 * @author mzechner */
public class GdxRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 6735854402467673117L;

	public GdxRuntimeException (String message) {
		super(message);
	}

	public GdxRuntimeException (Throwable t) {
		super(t);
	}

	public GdxRuntimeException (String message, Throwable t) {
		super(message, t);
	}
}
