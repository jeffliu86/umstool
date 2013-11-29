package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.commons.json.UserJsonObject;

abstract class SyncableResourceObject extends ResourceObject {//implements SyncableObject {

	private static final String KEY_AUIT_TIMESTAMP = "audit_utc_timestamp";

	//

	protected SyncableResourceObject(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	protected SyncableResourceObject(final UserDataObject userDataObject) {
		super(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	protected SyncableResourceObject(final String json) {
		super(json);
	}

	//

	public Long getAuditTimestamp() {
		return getAttributeAsLong(KEY_AUIT_TIMESTAMP);
	}

	public void setAuditTimestamp(final Long modifiedTimestamp) {
		setAttribute(KEY_AUIT_TIMESTAMP, modifiedTimestamp);
	}

	public UserJsonObject getObjectData() {
		return this.attributes;
	}

}
