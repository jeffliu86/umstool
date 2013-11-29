package com.telenav.user.commons;

import java.nio.charset.Charset;

public final class UserConstants extends UserObject {

	// To block any accidental instance invocation
	private UserConstants() {
	}

	public static final String REQUEST_ATTRIBUTE_LOG_EVENT = "attribute_request_log_event";
	public static final String REQUEST_ATTRIBUTE_SERVICE_API = "attribute_request_service_api";

	public static final String REQUEST_ATTRIBUTE_REQUESTBODY = "attribute_request_body";
	public static final String REQUEST_ATTRIBUTE_SESSION = "attribute_request_session";

	public static Charset API_DEFAULT_CHARSET = Charset.forName("UTF8");

}
