package com.telenav.user.commons;

public class UserException extends RuntimeException {
	private static final long serialVersionUID = 5921194295372345863L;

	private final ResponseCode responseCode;

	public UserException(final ResponseCode code, final Throwable e) {
		super(e);
		this.responseCode = code;
	}

	public UserException(final ResponseCode code, final String message, final Throwable cause) {
		super(message, cause);
		this.responseCode = code;
	}

	public ResponseCode getResponseCode() {
		return this.responseCode;
	}

}
