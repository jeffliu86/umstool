package com.telenav.user.commons.json;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserException;

public class UserJsonException extends UserException {

	private static final long serialVersionUID = -639452638868629299L;

	public UserJsonException(final Throwable cause) {
		super(ResponseCode.SERIALIZATION_ERROR, cause);
	}

	public UserJsonException(final String message, final Throwable cause) {
		super(ResponseCode.SERIALIZATION_ERROR, message, cause);
	}

}
