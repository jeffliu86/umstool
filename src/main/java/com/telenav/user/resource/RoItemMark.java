package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class RoItemMark extends ResourceObject {

	private static final String KEY_MARKER_ID = "marker_id";
	private static final String KEY_MARKED_TIMESTAMP = "marked_utc_timestamp";
	private static final String KEY_IS_REMOVED = "is_removed";

	public RoItemMark(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
		setAttribute(KEY_MARKED_TIMESTAMP, System.currentTimeMillis());
	}

	public RoItemMark(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoItemMark(final String json) {
		super(json);
	}

	//	private RoItemMark(final UserJsonObject json) {
	//		super(json);
	//	}

	public String getMarkerId() {
		return getAttributeAsString(KEY_MARKER_ID);
	}

	public void setMarkerId(final String markerId) {
		setAttribute(KEY_MARKER_ID, markerId);
	}

	public Long getMarkedTimestamp() {
		return getAttributeAsLong(KEY_MARKED_TIMESTAMP);
	}

	public Boolean isRemoved() {
		return getAttributeAsBoolean(KEY_IS_REMOVED);
	}

	public void setRemoveded(final Boolean deleted) {
		setAttribute(KEY_IS_REMOVED, deleted);
	}

	public static RoItemMark buildFromJson(final String json) {
		return new RoItemMark(json);
	}

	//	public static RoItemMark buildFromJson(final UserJsonObject json) {
	//		return new RoItemMark(json);
	//	}

	@Override
	public int hashCode() {

		final String markerId = getMarkerId();
		if (markerId != null) {
			return markerId.hashCode();
		}
		return super.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof RoItemMark)) {
			return false;
		}

		final RoItemMark other = (RoItemMark) obj;

		final String markerId = getMarkerId();
		final String otherMarkerId = other.getMarkerId();
		if (markerId == null) {
			if (otherMarkerId == null) {
				return true;
			}
			return false;
		}

		return markerId.equals(otherMarkerId);
	}

}
