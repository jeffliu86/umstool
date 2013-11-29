package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.model.constant.EnumUserCredentialsType;

public class RoPasswordResetToken extends ResourceObject {

	private static final String KEY_TOKEN_ID = "token_id";
	private static final String KEY_CREDENTIALS_TYPE = "credentials_type";
	private static final String KEY_CREDENTIALS_KEY = "credentials_key";
	private static final String KEY_CREATED_TIMESTAMP = "created_timestamp";
	private static final String KEY_EXPIRY_TIMESTAMP = "expiry_timestamp";
	private static final String KEY_SEND_TO_EMAIL = "send_to_email";

	public RoPasswordResetToken(final ResponseCode responseCode, final String errorMessage) {

		super(responseCode, errorMessage);
	}

	public RoPasswordResetToken(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoPasswordResetToken(final String jsonString) {
		super(jsonString);
	}

	//	private RoPasswordResetToken(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}

	public String getTokenId() {
		return getAttributeAsString(KEY_TOKEN_ID);
	}

	public void setTokenId(final String tokenId) {
		setAttribute(KEY_TOKEN_ID, tokenId);
	}

	public EnumUserCredentialsType getCredentialsType() {
		return EnumUserCredentialsType.valueOf(getAttributeAsString(KEY_CREDENTIALS_TYPE));
	}

	public void setCredentialsType(final EnumUserCredentialsType type) {
		setAttribute(KEY_CREDENTIALS_TYPE, type.name());
	}

	public String getCredentialsKey() {
		return getAttributeAsString(KEY_CREDENTIALS_KEY);
	}

	public void setCredentialsKey(final String key) {
		setAttribute(KEY_CREDENTIALS_KEY, key);
	}

	public Long getCreatedTimestamp() {
		return getAttributeAsLong(KEY_CREATED_TIMESTAMP);
	}

	public void setCreatedTimestamp(final Long timestamp) {
		setAttribute(KEY_CREATED_TIMESTAMP, timestamp);
	}

	public Long getExpiryTimestamp() {
		return getAttributeAsLong(KEY_EXPIRY_TIMESTAMP);
	}

	public void setExpiryTimestamp(final Long timestamp) {
		setAttribute(KEY_EXPIRY_TIMESTAMP, timestamp);
	}

	public String getSendToEmail() {
		return getAttributeAsString(KEY_SEND_TO_EMAIL);
	}

	public void setSendToEmail(final String email) {
		setAttribute(KEY_SEND_TO_EMAIL, email);
	}

	public static RoPasswordResetToken buildFromJson(final String json) {
		return new RoPasswordResetToken(json);
	}

	//	public static RoPasswordResetToken buildFromJson(final UserJsonObject json) {
	//		return new RoPasswordResetToken(json);
	//	}

}
