package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.commons.json.UserJsonArray;
import com.telenav.user.commons.json.UserJsonObject;

abstract class ResourceObject extends UserDataObject {

	protected static final UserServiceLogger LOG = UserServiceLogger.getLogger(ResourceObject.class);

	protected final UserJsonObject attributes;

	//

	protected ResourceObject(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
		this.attributes = new UserJsonObject();
	}

	protected ResourceObject(final UserDataObject userDataObject) {
		super(userDataObject);
		this.attributes = new UserJsonObject();
	}

	protected ResourceObject(final String json) {
		super(ResponseCode.GENERAL_INTERNAL_ERROR, null); // default set to error
		this.attributes = new UserJsonObject(json);
		setResponseCode(ResponseCode.ALL_OK, null); // set OK if nothing failed
	}

	//	protected ResourceObject(final UserJsonObject jsonObj) {
	//		super(ResponseCode.GENERAL_INTERNAL_ERROR, null); // default set to error
	//		this.attributes = jsonObj;
	//		setResponseCode(ResponseCode.ALL_OK, null); // set OK if nothing failed
	//	}

	//

	public final void setAttribute(final String key, final String value) {
		this.attributes.put(key, value);
	}

	public final void setAttribute(final String key, final Boolean value) {
		this.attributes.put(key, value);
	}

	public final void setAttribute(final String key, final Double value) {
		this.attributes.put(key, value);
	}

	public final void setAttribute(final String key, final Integer value) {
		this.attributes.put(key, value);
	}

	public final void setAttribute(final String key, final Long value) {
		this.attributes.put(key, value);
	}

	public final void setAttribute(final String key, final UserJsonObject value) {
		this.attributes.put(key, value);
	}

	public final void setAttribute(final String key, final UserJsonArray value) {
		this.attributes.put(key, value);
	}

	//

	public final String getAttributeAsString(final String key) {
		return this.attributes.getString(key);
	}

	public final Boolean getAttributeAsBoolean(final String key) {
		return this.attributes.getBoolean(key);
	}

	public final Long getAttributeAsLong(final String key) {
		return this.attributes.getLong(key);
	}

	public final Integer getAttributeAsInteger(final String key) {
		return this.attributes.getInt(key);
	}

	public final Double getAttributeAsDouble(final String key) {
		return this.attributes.getDouble(key);
	}

	public final UserJsonObject getAttributeAsUserJsonObject(final String key) {
		return this.attributes.getUserJsonObject(key);
	}

	public final UserJsonArray getAttributeAsUserJsonArray(final String key) {
		return this.attributes.getUserJsonArray(key);
	}

	//

	public final UserJsonObject toUserJsonObject() {
		return this.attributes;
	}

	public final String toJsonString() {
		return toUserJsonObject().toString();
	}

	@Override
	public String toString() {
		return toUserJsonObject().toString();
	}

}
