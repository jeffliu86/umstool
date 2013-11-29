package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class RoUserProfile extends SyncableResourceObject {

	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_PROFILE_KEY = "key";
	private static final String KEY_PROFILE_VALUE = "value";
	private static final String KEY_PROFILE_UPDATE_TIMESTAMP = "modified_utc_timestamp";
	private static final String KEY_PROFILE_CREATED_BY = "profile_created_by";
	private static final String KEY_PROFILE_CREATED_FROM_DEVICE = "profile_created_from_device";

	public RoUserProfile(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public RoUserProfile(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoUserProfile(final String jsonString) {
		super(jsonString);
	}

	//	private RoUserProfile(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}

	public String getSyncId() {
		return getProfileKey();
	}

	public Long getModifiedTimestamp() {

		return getAttributeAsLong(KEY_PROFILE_UPDATE_TIMESTAMP);

	}

	public String getUserId() {
		return getAttributeAsString(KEY_USER_ID);
	}

	public void setUserId(final String userId) {
		setAttribute(KEY_USER_ID, userId);
	}

	public String getProfileKey() {
		return getAttributeAsString(KEY_PROFILE_KEY);
	}

	public void setProfileKey(final String key) {
		setAttribute(KEY_PROFILE_KEY, key);
	}

	public String getProfileValue() {
		return getAttributeAsString(KEY_PROFILE_VALUE);
	}

	public void setProfileValue(final String value) {
		setAttribute(KEY_PROFILE_VALUE, value);
	}

	public void setModifiedTimestamp(final Long timestamp) {
		setAttribute(KEY_PROFILE_UPDATE_TIMESTAMP, timestamp);
	}

	public String getCreatedBy() {
		return getAttributeAsString(KEY_PROFILE_CREATED_BY);
	}

	public void setCreatedBy(final String createdBy) {
		setAttribute(KEY_PROFILE_CREATED_BY, createdBy);
	}

	public String getCreatedFromDevice() {
		return getAttributeAsString(KEY_PROFILE_CREATED_FROM_DEVICE);
	}

	public void setCreatedFromDevice(final String deviceId) {
		setAttribute(KEY_PROFILE_CREATED_FROM_DEVICE, deviceId);
	}

	public static RoUserProfile buildFromJson(final String jsonString) {
		return new RoUserProfile(jsonString);
	}

	//	public static RoUserProfile buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoUserProfile(jsonObj);
	//	}

	@Override
	public int hashCode() {

		int returnValue = 0;

		final String key = getProfileKey();

		if (key != null) {
			returnValue = key.hashCode();
		}
		return returnValue;
	}

	@Override
	public boolean equals(final Object obj) {

		boolean returnValue = false;

		if (this == obj) {
			returnValue = true;
		}
		else {
			if (obj instanceof RoUserProfile) {
				final RoUserProfile other = (RoUserProfile) obj;

				final String key = getProfileKey();
				final String otherKey = other.getProfileKey();

				if (key == null) {
					if (otherKey == null) {
						returnValue = true;
					}
				}
				else {
					returnValue = key.equals(otherKey);
				}
			}
		}
		return returnValue;
	}

}
