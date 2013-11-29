package com.telenav.user.commons.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserJsonArray {

	JSONArray jsonArray;

	private boolean isDirty = false;
	private String stringValue = null;

	//

	public UserJsonArray() {
		super();
		this.jsonArray = new JSONArray();
	}

	public UserJsonArray(final String jsonArrayString) {
		super();
		try {
			this.jsonArray = new JSONArray(jsonArrayString);
		}
		catch (final Throwable e) {
			final String errorMessage = String.format("Cannot construct UserJsonArray with [%s]", jsonArrayString);
			throw new UserJsonException(errorMessage, e);
		}
	}

	protected UserJsonArray(final JSONArray jsonArray) {
		super();
		this.jsonArray = jsonArray;
	}

	//

	public UserJsonArray put(final Boolean value) {
		this.jsonArray.put(value);
		this.isDirty = true;
		return this;
	}

	public UserJsonArray put(final Double index) {
		this.jsonArray.put(index);
		this.isDirty = true;
		return this;
	}

	public UserJsonArray put(final Integer value) {
		this.jsonArray.put(value);
		this.isDirty = true;
		return this;
	}

	public UserJsonArray put(final Long value) {
		this.jsonArray.put(value);
		this.isDirty = true;
		return this;
	}

	public UserJsonArray put(final String value) {
		this.jsonArray.put(value);
		this.isDirty = true;
		return this;
	}

	public UserJsonArray put(final UserJsonObject value) {
		this.jsonArray.put((value == null) ? null : value.jsonObject);
		this.isDirty = true;
		return this;
	}

	public UserJsonArray put(final UserJsonArray value) {
		this.jsonArray.put((value == null) ? null : value.jsonArray);
		this.isDirty = true;
		return this;
	}

	public UserJsonArray put(final int index, final Boolean value) {
		try {
			this.jsonArray.put(index, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}

	}

	public UserJsonArray put(final int index, final Double value) {
		try {
			this.jsonArray.put(index, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonArray put(final int index, final Integer value) {
		try {
			this.jsonArray.put(index, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonArray put(final int index, final Long value) {
		try {
			this.jsonArray.put(index, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonArray put(final int index, final String value) {
		try {
			this.jsonArray.put(index, value);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonArray put(final int index, final UserJsonObject value) {
		try {
			this.jsonArray.put(index, (value == null) ? null : value.jsonObject);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}

	}

	public UserJsonArray put(final int index, final UserJsonArray value) {
		try {
			this.jsonArray.put(index, (value == null) ? null : value.jsonArray);
			this.isDirty = true;
			return this;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	//

	public Boolean getBoolean(final int index) {
		try {
			return this.jsonArray.getBoolean(index);
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public Double getDouble(final int index) {
		try {
			return this.jsonArray.getDouble(index);
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public Integer getInt(final int index) {
		try {
			return this.jsonArray.getInt(index);
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonArray getUserJsonArray(final int index) {
		try {
			final JSONArray arrays = this.jsonArray.getJSONArray(index);

			final UserJsonArray returnValue = new UserJsonArray();
			returnValue.jsonArray = arrays;

			return returnValue;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public UserJsonObject getUserJsonObject(final int index) {

		try {
			final JSONObject obj = this.jsonArray.getJSONObject(index);

			final UserJsonObject returnValue = new UserJsonObject(obj);

			return returnValue;
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public Long getLong(final int index) {
		try {
			return this.jsonArray.getLong(index);
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public String getString(final int index) {
		try {
			return this.jsonArray.getString(index);
		}
		catch (final JSONException e) {
			throw new UserJsonException(e);
		}
	}

	public boolean isNull(final int index) {
		return this.jsonArray.isNull(index);
	}

	public int length() {
		return this.jsonArray.length();
	}

	@Override
	public String toString() {
		if ((this.stringValue == null) || this.isDirty) {
			if (this.jsonArray != null) {
				this.stringValue = this.jsonArray.toString();
				this.isDirty = false;
			}
		}

		return this.stringValue;
	}

}
