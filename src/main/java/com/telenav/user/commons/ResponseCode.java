package com.telenav.user.commons;

import org.apache.http.HttpStatus;

public enum ResponseCode {

	ALL_OK(13200, "OK", HttpStatus.SC_OK), //
	GENERAL_INTERNAL_ERROR(13500, "INTERNAL_ERROR", HttpStatus.SC_INTERNAL_SERVER_ERROR), //
	INVALID_PARAMETER(13400, "INVALID_REQUEST", HttpStatus.SC_BAD_REQUEST), //
	AUTHENTICATION_FAILED(13401, "NEED_AUTHENTICATION", HttpStatus.SC_UNAUTHORIZED), //
	INVALID_ACCESS_TOKEN(13401, "NEED_AUTHENTICATION", HttpStatus.SC_UNAUTHORIZED), //
	INVALID_APPLICATION_SIGNATURE(13401, "NEED_AUTHENTICATION", HttpStatus.SC_UNAUTHORIZED), //
	USER_NOT_FOUND(13401, "FORBIDDEN", HttpStatus.SC_FORBIDDEN), //
	RESOURCE_MISSING(13405, "BAD_OPERATION", HttpStatus.SC_METHOD_NOT_ALLOWED), //
	USER_EXIST(13803, "USER_ALREADY_EXIST", HttpStatus.SC_FORBIDDEN), //
	TOKEN_EXPIRED(13801, "AUTHENTICATION_EXPIRED", HttpStatus.SC_UNAUTHORIZED), //
	DATA_ACCESS_ERROR(13500, "INTERNAL_ERROR", HttpStatus.SC_INTERNAL_SERVER_ERROR), //
	SERIALIZATION_ERROR(13500, "INTERNAL_ERROR", HttpStatus.SC_INTERNAL_SERVER_ERROR), //
	PARTIAL_OK(13206, "PARTIAL_OK", HttpStatus.SC_PARTIAL_CONTENT);

	private final int serviceStatusCode;
	private final String serviceStatus;
	private final int httpStatusCode;

	private ResponseCode(final int code, final String status, final int hsc) {

		this.serviceStatusCode = code;
		this.serviceStatus = status;
		this.httpStatusCode = hsc;
	}

	public int getServiceStatusCode() {
		return this.serviceStatusCode;
	}

	public String getServiceStatus() {
		return this.serviceStatus;
	}

	public int getHttpStatusCode() {
		return this.httpStatusCode;
	}

}
