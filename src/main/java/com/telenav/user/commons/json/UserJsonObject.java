package com.telenav.user.commons.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class UserJsonObject {

	protected final JSONObject jsonObject;

	private boolean isDirty = false;
	private String stringValue = null;

	//

	public UserJsonObject() {
		super();
		this.jsonObject = new JSONObject();
	}

	public UserJsonObject(final String jsonString) {
		super();
		try {
			this.jsonObject = new JSONObject(jsonString);
		}
		catch (final Throwable e) {
			final String errorMessage = String.format("Cannot construct UserJsonObject with [%s]", jsonString);
			throw new UserJsonException(errorMessage, e);
		}
	}

	//TODO: Probably this is not required at all
	protected UserJsonObject(final JSONObject jsonObj) {
		super();
		this.jsonObject = jsonObj;
	}

	//

	public String getString(final String key) {
		return this.jsonObject.optString(key, null);
	}

	public Boolean getBoolean(final String key) {
		Boolean returnValue = null;

		if (this.jsonObject.has(key)) {
			try {
				returnValue = this.jsonObject.getBoolean(key);
			}
			catch (final JSONException e) {
				throw new UserJsonException(e);
			}
		}

		return returnValue;
	}

	public Double getDouble(final String key) {
		Double returnValue = null;

		if (this.jsonObject.has(key)) {
			try {
				returnValue = this.jsonObject.getDouble(key);
			}
			catch (final JSONException e) {
				throw new UserJsonException(e);
			}
		}

		return returnValue;
	}

	public Integer getInt(final String key) {
		Integer returnValue = null;

		if (this.jsonObject.has(key)) {
			try {
				returnValue = this.jsonObject.getInt(key);
			}
			catch (final JSONException e) {
				throw new UserJsonException(e);
			}
		}

		return returnValue;
	}

	public Long getLong(final String key) {
		Long returnValue = null;

		if (this.jsonObject.has(key)) {
			try {
				returnValue = this.jsonObject.getLong(key);
			}
			catch (final JSONException e) {
				throw new UserJsonException(e);
			}
		}

		return returnValue;
	}

	public UserJsonArray getUserJsonArray(final String key) {
		UserJsonArray returnValue = null;

		if (this.jsonObject.has(key)) {
			try {
				final JSONArray jsonArray = this.jsonObject.getJSONArray(key);
				returnValue = new UserJsonArray(jsonArray);
			}
			catch (final JSONException e) {
				throw new UserJsonException(e);
			}
		}

		return returnValue;
	}

	public UserJsonObject getUserJsonObject(final String key) {
		UserJsonObject returnValue = null;

		if (this.jsonObject.has(key)) {
			try {
				final JSONObject jsonObj = this.jsonObject.getJSONObject(key);

				returnValue = new UserJsonObject(jsonObj); // TODO: Why we need to store the JSONObject, why not UserJSON Object?
			}
			catch (final JSONException e) {
				throw new UserJsonException(e);
			}
		}

		return returnValue;
	}

	//

	public boolean has(final String key) {
		return this.jsonObject.has(key);
	}

	//

	public UserJsonObject put(final String key, final Boolean value) {
		try {
			this.jsonObject.put(key, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonObject put(final String key, final Double value) {
		try {
			this.jsonObject.put(key, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonObject put(final String key, final Integer value) {
		try {
			this.jsonObject.put(key, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonObject put(final String key, final Long value) {
		try {
			this.jsonObject.put(key, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonObject put(final String key, final String value) {
		try {
			this.jsonObject.put(key, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonObject put(final String key, final UserJsonObject value) {
		try {
			this.jsonObject.put(key, (value == null) ? null : value.jsonObject);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonObject put(final String key, final UserJsonArray value) {
		try {
			this.jsonObject.put(key, (value == null) ? null : value.jsonArray);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public void remove(final String key) {
		this.jsonObject.remove(key);
		this.isDirty = true;
	}

	@Override
	public String toString() {

		if ((this.stringValue == null) || this.isDirty) {
			if (this.jsonObject != null) {
				this.stringValue = this.jsonObject.toString();
				this.isDirty = false;
			}
		}

		return this.stringValue;

	}

}
