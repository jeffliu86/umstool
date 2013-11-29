package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.commons.json.UserJsonObject;
import com.telenav.user.model.constant.EnumUserCredentialsType;

public class RoLoginSession extends ResourceObject {

	private static final String KEY_SESSION_ID = "session_id";
	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_LOGIN_TIMESTAMP = "login_timestamp";
	private static final String KEY_CREDENTIALS_KEY = "credentials_key";
	private static final String KEY_CREDENTIALS_TYPE = "credentials_type";
	private static final String KEY_APPLICATION_ID = "application_id";
	private static final String KEY_DEVICE_INFO = "device_info";
	private static final String KEY_IS_LEGACY_USER = "is_legacy_user";
	private static final String KEY_LEGACY_USER_ID = "legacy_user_id";
	private static final String KEY_CSRID = "csr_id";
	private static final String KEY_SESSION_ATTRIBUTES = "session_attributes";

	//extra keys
	private static final String ATTRIBUTE_KEY_IS_NEED_ON_THE_FLY_DATA_MIGRATION = "NEED_BACKEND_DATA_MIGRATION";

	//

	public RoLoginSession(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public RoLoginSession(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoLoginSession(final String jsonString) {
		super(jsonString);
	}

	//	private RoLoginSession(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}

	public void setDeviceInfo(final RoDeviceInfo deviceInfo) {
		setAttribute(KEY_DEVICE_INFO, deviceInfo.toUserJsonObject());
	}

	public RoDeviceInfo getDeviceInfo() {
		return RoDeviceInfo.buildFromJson(getAttributeAsUserJsonObject(KEY_DEVICE_INFO).toString());
	}

	public String getSessionId() {
		return getAttributeAsString(KEY_SESSION_ID);
	}

	public void setSessionId(final String sessionId) {
		setAttribute(KEY_SESSION_ID, sessionId);
	}

	public String getUserId() {
		return getAttributeAsString(KEY_USER_ID);
	}

	public void setUserId(final String userId) {
		setAttribute(KEY_USER_ID, userId);
	}

	public String getLoginTimestamp() {
		return getAttributeAsString(KEY_LOGIN_TIMESTAMP);
	}

	public void setLoginTimestamp(final String loginTimestamp) {
		setAttribute(KEY_LOGIN_TIMESTAMP, loginTimestamp);
	}

	public String getCredentialsKey() {
		return getAttributeAsString(KEY_CREDENTIALS_KEY);
	}

	public void setCredentialsKey(final String credentialKey) {
		setAttribute(KEY_CREDENTIALS_KEY, credentialKey);
	}

	public EnumUserCredentialsType getCredentialsType() {
		EnumUserCredentialsType returnValue = null;
		final String value = getAttributeAsString(KEY_CREDENTIALS_TYPE);
		if (value != null) {
			returnValue = EnumUserCredentialsType.valueOf(value);
		}
		return returnValue;
	}

	public void setCredentialsType(final EnumUserCredentialsType credentialType) {
		setAttribute(KEY_CREDENTIALS_TYPE, credentialType.name());
	}

	public String getApplicationId() {
		return getAttributeAsString(KEY_APPLICATION_ID);
	}

	public void setApplicationId(final String applicationId) {
		setAttribute(KEY_APPLICATION_ID, applicationId);
	}

	public String getCsrId() {
		return getAttributeAsString(KEY_CSRID);
	}

	public void setCsrId(final String csrId) {
		setAttribute(KEY_CSRID, csrId);
	}

	public Boolean isLegacyUser() {
		return getAttributeAsBoolean(KEY_IS_LEGACY_USER);
	}

	public void setLegacyUser(final Boolean isLegacy) {
		setAttribute(KEY_IS_LEGACY_USER, isLegacy);
	}

	public Long getLegacyUserId() {
		return getAttributeAsLong(KEY_LEGACY_USER_ID);
	}

	public void setLegacyUserId(final Long billingId) {
		setAttribute(KEY_LEGACY_USER_ID, billingId);
	}

	public void setSessionAttributes(final UserJsonObject sessionAttributes) {
		setAttribute(KEY_SESSION_ATTRIBUTES, sessionAttributes);
	}

	public UserJsonObject getSessionAttributes() {
		return getAttributeAsUserJsonObject(KEY_SESSION_ATTRIBUTES);
	}

	public boolean isNeedOnTheFlyDataMigration() {
		boolean returnValue = true;

		final UserJsonObject sessionAttributes = getSessionAttributes();
		if (sessionAttributes != null) {
			final Boolean flag = sessionAttributes.getBoolean(ATTRIBUTE_KEY_IS_NEED_ON_THE_FLY_DATA_MIGRATION);

			if (flag != null) {
				returnValue = flag;
			}

		}
		return returnValue;
	}

	public static RoLoginSession buildFromJson(final String jsonString) {
		return new RoLoginSession(jsonString);
	}

	//	public static RoLoginSession buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoLoginSession(jsonObj);
	//	}

}
