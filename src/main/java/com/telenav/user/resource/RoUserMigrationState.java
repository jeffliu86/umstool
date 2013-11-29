package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class RoUserMigrationState extends ResourceObject {

	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_IS_LEGACY_USER = "is_legacy_user";
	private static final String KEY_LEGACY_USER_ID = "legacy_user_id";

	public RoUserMigrationState(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public RoUserMigrationState(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoUserMigrationState(final String jsonString) {
		super(jsonString);
	}

	//	private RoUserMigrationState(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}

	public String getUserId() {
		return getAttributeAsString(KEY_USER_ID);
	}

	public void setUserId(final String userId) {
		setAttribute(KEY_USER_ID, userId);
	}

	public Boolean isLegacyUser() {
		return getAttributeAsBoolean(KEY_IS_LEGACY_USER);
	}

	public void setLegacyUser(final Boolean value) {
		setAttribute(KEY_IS_LEGACY_USER, value.toString());
	}

	public Long getLegacyUserId() {
		return getAttributeAsLong(KEY_LEGACY_USER_ID);
	}

	public void setLegacyUserId(final Long legacyUserId) {
		setAttribute(KEY_LEGACY_USER_ID, legacyUserId.toString());
	}

	public static RoUserMigrationState buildFromJson(final String jsonString) {
		return new RoUserMigrationState(jsonString);
	}

	//	public static RoUserMigrationState buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoUserMigrationState(jsonObj);
	//	}

}
