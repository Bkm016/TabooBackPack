package me.skymc.taboobackpack.exception;

/**
 * 背包识别异常
 * 
 * @author sky
 * @sine 2018-1-29
 */
public class BackPackCastException extends Error {

	private static final long serialVersionUID = 3907181437461911201L;

	public BackPackCastException(String message) {
		super(message);
	}
}
