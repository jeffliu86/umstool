package com.telenav.user.resource;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserConstants;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.model.constant.EnumMarkerType;

public class RoMarker extends SyncableResourceObject {

	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_MARKER_ID = "marker_id";
	private static final String KEY_LABEL = "label";
	private static final String KEY_MARKER_TYPE = "type";
	private static final String KEY_MODIFIED_TIMESTAMP = "modified_utc_timestamp";
	private static final String KEY_CREATED_BY = "created_by";
	private static final String KEY_CREATED_FROM_DEVICE = "created_from_device";
	private static final String KEY_IS_DELETED = "is_deleted";

	public RoMarker(final ResponseCode responseCode, final String errorMessage, final EnumMarkerType markerType) {

		super(responseCode, errorMessage);
		setAttribute(KEY_MARKER_TYPE, markerType.name());
	}

	public RoMarker(final UserDataObject userDataObject, final EnumMarkerType markerType) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage(), markerType);
	}

	private RoMarker(final String json) {
		super(json);
	}

	//	private RoMarker(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}

	public String getMarkerId() {
		return getAttributeAsString(RoMarker.KEY_MARKER_ID);
	}

	public void setMarkerId(final String markerId) {
		setAttribute(RoMarker.KEY_MARKER_ID, markerId);
	}

	public String getUserId() {
		return getAttributeAsString(RoMarker.KEY_USER_ID);
	}

	public void setUserId(final String userId) {
		setAttribute(RoMarker.KEY_USER_ID, userId);
	}

	public EnumMarkerType getMarkerType() {
		return EnumMarkerType.valueOf(getAttributeAsString(KEY_MARKER_TYPE));
	}

	public String getLabel() {
		return getAttributeAsString(RoMarker.KEY_LABEL);
	}

	public void setLabel(final String label) {
		setAttribute(RoMarker.KEY_LABEL, label);
	}

	public Long getModifiedTimestamp() {
		return getAttributeAsLong(KEY_MODIFIED_TIMESTAMP);
	}
	
	public String getModifiedTimeDate() {
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		return df.format(
				new Date(getModifiedTimestamp())
				)+" "+UserConstants.TIMEZONE;
	}

	public void setModifiedTimestamp(final Long timestamp) {
		setAttribute(KEY_MODIFIED_TIMESTAMP, timestamp);
	}

	public Boolean getIsDeleted() {
		return getAttributeAsBoolean(KEY_IS_DELETED);
	}

	public void setIsDeleted(final Boolean value) {
		setAttribute(KEY_IS_DELETED, value);
	}

	public String getCreatedBy() {
		return getAttributeAsString(KEY_CREATED_BY);
	}

	public void setCreatedBy(final String createdBy) {
		setAttribute(KEY_CREATED_BY, createdBy);
	}

	public String getCreatedFromDevice() {
		return getAttributeAsString(KEY_CREATED_FROM_DEVICE);
	}

	public void setCreatedFromDevice(final String deviceId) {
		setAttribute(KEY_CREATED_FROM_DEVICE, deviceId);
	}

	public static RoMarker buildFromJson(final String jsonString) {
		return new RoMarker(jsonString);
	}

	//	public static RoMarker buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoMarker(jsonObj);
	//	}

	public String getSyncId() {
		return getMarkerId();
	}

	@Override
	public int hashCode() {
		final String label = getLabel();
		final EnumMarkerType type = getMarkerType();

		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((label == null) ? 0 : label.hashCode());
		result = (prime * result) + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final RoMarker other = (RoMarker) obj;

		final String label = getLabel();
		final EnumMarkerType type = getMarkerType();

		if (label == null) {
			if (other.getLabel() != null) {
				return false;
			}
		}
		else if (!label.equals(other.getLabel())) {
			return false;
		}
		if (type == null) {
			if (other.getMarkerType() != null) {
				return false;
			}
		}
		else if (!type.equals(other.getMarkerType())) {
			return false;
		}

		return true;
	}

}
