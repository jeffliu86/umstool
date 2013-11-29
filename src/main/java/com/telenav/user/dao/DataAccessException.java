package com.telenav.user.dao;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserException;

public class DataAccessException extends UserException {

	private static final long serialVersionUID = 6389984486722974293L;

	public DataAccessException(final Throwable throwable) {
		super(ResponseCode.DATA_ACCESS_ERROR, throwable);
	}

	public DataAccessException(final String message, final Throwable throwable) {
		super(ResponseCode.DATA_ACCESS_ERROR, message, throwable);
	}

}
