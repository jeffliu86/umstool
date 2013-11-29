package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class RoIdentityTokenSet extends ResourceObject {

	private static final String KEY_ACCESS_TOKEN = "access_token";
	private static final String KEY_ACESS_TOKEN_EXPIRES_IN = "access_token_expires_in";
	private static final String KEY_REFRESH_TOKEN = "refresh_token";
	private static final String KEY_REFRESH_TOKEN_EXPIRES_IN = "refresh_token_expires_in";
	private static final String KEY_APPLICATION_ID = "application_id";
	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_CREATED_TIMESTAMP = "created_timestamp";

	public RoIdentityTokenSet(final ResponseCode responseCode, final String errorMessage) {

		super(responseCode, errorMessage);

		setAttribute(KEY_CREATED_TIMESTAMP, System.currentTimeMillis());
	}

	public RoIdentityTokenSet(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoIdentityTokenSet(final String jsonString) {
		super(jsonString);
	}

	//	private RoIdentityTokenSet(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}

	public String getAccessToken() {
		return getAttributeAsString(KEY_ACCESS_TOKEN);
	}

	public void setAccessToken(final String accessToken) {
		setAttribute(KEY_ACCESS_TOKEN, accessToken);
	}

	public String getRefreshToken() {
		return getAttributeAsString(KEY_REFRESH_TOKEN);
	}

	public void setRefreshToken(final String refreshToken) {
		setAttribute(KEY_REFRESH_TOKEN, refreshToken);
	}

	public Integer getAccessTokenExpiresInSecs() {
		return getAttributeAsInteger(KEY_ACESS_TOKEN_EXPIRES_IN);
	}

	public void setAccessTokenExpiresInSecs(final Integer expiresIn) {
		setAttribute(KEY_ACESS_TOKEN_EXPIRES_IN, expiresIn.toString());
	}

	public Integer getRefreshTokenExpiresInSecs() {
		return getAttributeAsInteger(KEY_REFRESH_TOKEN_EXPIRES_IN);
	}

	public void setRefreshTokenExpiresInSecs(final Integer expiresIn) {
		setAttribute(KEY_REFRESH_TOKEN_EXPIRES_IN, expiresIn.toString());
	}

	public String getApplicationId() {
		return getAttributeAsString(KEY_APPLICATION_ID);
	}

	public void setApplicationId(final String applicationId) {
		setAttribute(KEY_APPLICATION_ID, applicationId);
	}

	public String getUserId() {
		return getAttributeAsString(KEY_USER_ID);
	}

	public void setUserId(final String applicationId) {
		setAttribute(KEY_USER_ID, applicationId);
	}

	public Long getCreatedTimestamp() {
		return getAttributeAsLong(KEY_CREATED_TIMESTAMP);
	}

	public static RoIdentityTokenSet buildFromJson(final String jsonString) {
		return new RoIdentityTokenSet(jsonString);
	}

	//	public static RoIdentityTokenSet buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoIdentityTokenSet(jsonObj);
	//	}
}
