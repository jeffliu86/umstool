package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserCommonUtils;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.model.constant.EnumUserCredentialsType;

public class RoUserCredentials extends ResourceObject {

	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_TYPE = "type";
	private static final String KEY_KEY = "key";
	private static final String KEY_SECRET = "secret";

	public RoUserCredentials(final ResponseCode responseCode, final String errorMessage) {

		super(responseCode, errorMessage);
	}

	public RoUserCredentials(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoUserCredentials(final String jsonString) {
		super(jsonString);
	}

	//	private RoUserCredentials(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}

	public String getUserId() {
		return getAttributeAsString(KEY_USER_ID);
	}

	public void setUserId(final String userId) {
		setAttribute(KEY_USER_ID, userId);
	}

	public EnumUserCredentialsType getType() {
		return EnumUserCredentialsType.valueOf(getAttributeAsString(KEY_TYPE));
	}

	public void setType(final EnumUserCredentialsType type) {
		setAttribute(KEY_TYPE, type.name());
	}

	public String getKey() {
		return getAttributeAsString(KEY_KEY);
	}

	public void setKey(final String key) {
		setAttribute(KEY_KEY, key);
	}

	public String getSecret() {
		return getAttributeAsString(KEY_SECRET);
	}

	public void setSecret(final String secret) {
		setAttribute(KEY_SECRET, secret);
	}

	public static RoUserCredentials buildFromJson(final String jsonString) {
		return new RoUserCredentials(jsonString);
	}

	//	public static RoUserCredentials buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoUserCredentials(jsonObj);
	//	}

	@Override
	public final String toString() {
		String returnValue = null;

		if (this != null) {
			final String credentialStr = super.toString();
			returnValue = UserCommonUtils.maskJsonInputByKey(credentialStr, KEY_SECRET);
		}

		return returnValue;
	}

}
