package jmediator;

/**
 * 
 *
 */
public class NoHandlerForRequestException extends RuntimeException {

	private static final long serialVersionUID = -3378587911169482266L;

	public NoHandlerForRequestException(String message) {
		super(message);
	}
	
	public NoHandlerForRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
