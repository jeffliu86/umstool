package com.telenav.user.resource;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserConstants;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.commons.json.UserJsonArray;

public class RoUserItem extends SyncableResourceObject {

	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_ITEM_ID = "item_id";
	private static final String KEY_ITEM_TYPE = "type";
	private static final String KEY_ITEM_CORRELATION_ID = "correlation_id";
	private static final String KEY_ITEM_NAME = "name";
	private static final String KEY_ITEM_METADATA = "metadata";
	private static final String KEY_ITEM_UPDATE_TIMESTAMP = "modified_utc_timestamp";
	private static final String KEY_ITEM_CREATED_BY = "item_created_by";
	private static final String KEY_ITEM_CREATED_FROM_DEVICE = "item_created_from_device";
	private static final String KEY_SYSTEM_MARKS = "system_marks";
	private static final String KEY_USER_MARKS = "user_marks";
	private static final String KEY_IS_DELETED = "is_deleted";

	public RoUserItem(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public RoUserItem(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoUserItem(final String jsonString) {
		super(jsonString);
	}

	//	private RoUserItem(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}
	


	public String getSyncId() {
		return getItemId();
	}

	public String getUserId() {
		return getAttributeAsString(KEY_USER_ID);
	}

	public void setUserId(final String userId) {
		setAttribute(KEY_USER_ID, userId);
	}

	public String getItemId() {
		return getAttributeAsString(KEY_ITEM_ID);
	}

	public void setItemId(final String itemId) {
		setAttribute(KEY_ITEM_ID, itemId);
	}

	public String getItemType() {
		return getAttributeAsString(KEY_ITEM_TYPE);
	}

	public void setItemType(final String type) {
		setAttribute(KEY_ITEM_TYPE, type);
	}

	public String getCorrelationId() {
		return getAttributeAsString(KEY_ITEM_CORRELATION_ID);
	}

	public void setCorrelationId(final String correlationId) {
		setAttribute(KEY_ITEM_CORRELATION_ID, correlationId);
	}

	public String getName() {
		return getAttributeAsString(KEY_ITEM_NAME);
	}

	public void setName(final String name) {
		setAttribute(KEY_ITEM_NAME, name);
	}

	public String getMetadata() {
		return getAttributeAsString(KEY_ITEM_METADATA);
	}

	public void setMetadata(final String meta) {
		setAttribute(KEY_ITEM_METADATA, meta);
	}

	public Long getModifiedTimestamp() {
		return getAttributeAsLong(KEY_ITEM_UPDATE_TIMESTAMP);
	}
	
	public String getModifiedTimeDate() {
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		return df.format(
				new Date(getModifiedTimestamp())
				)+" "+UserConstants.TIMEZONE;
	}

	public void setModifiedTimestamp(final Long modifiedTimestamp) {
		setAttribute(KEY_ITEM_UPDATE_TIMESTAMP, modifiedTimestamp);
	}

	public String getCreatedBy() {
		return getAttributeAsString(KEY_ITEM_CREATED_BY);
	}

	public void setCreatedBy(final String createdBy) {
		setAttribute(KEY_ITEM_CREATED_BY, createdBy);
	}

	public String getCreatedFromDevice() {
		return getAttributeAsString(KEY_ITEM_CREATED_FROM_DEVICE);
	}

	public void setCreatedFromDevice(final String deviceId) {
		setAttribute(KEY_ITEM_CREATED_FROM_DEVICE, deviceId);
	}

	public void updateSystemMarks(final Collection<RoItemMark> marks) {

		final Collection<RoItemMark> existingMarks = getSystemMarks();

		for (final RoItemMark newMark : marks) {
			existingMarks.remove(newMark);
			existingMarks.add(newMark);
		}
		final UserJsonArray jsonArray = new UserJsonArray();
		for (final RoItemMark itemMark : existingMarks) {
			jsonArray.put(itemMark.toUserJsonObject());
		}

		setAttribute(KEY_SYSTEM_MARKS, jsonArray);
	}

	public Collection<RoItemMark> getSystemMarks() {
		final Collection<RoItemMark> returnValue = new HashSet<RoItemMark>();
		final UserJsonArray jsonArray = getAttributeAsUserJsonArray(KEY_SYSTEM_MARKS);
		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				final RoItemMark mark = RoItemMark.buildFromJson(jsonArray.getString(i));
				returnValue.add(mark);
			}
		}
		return returnValue;
	}
	
	public String getAllMarksName(){
		String results="";
		for ( RoItemMark each: getSystemMarks()) {
			results+=each.getMarkerId()+", ";
		}
		for ( RoItemMark each: getUserMarks()) {
			results+=each.getMarkerId()+", ";
		}
		return results;
	}

	public void updateUserMarks(final Collection<RoItemMark> marks) {

		final Collection<RoItemMark> existingMarks = getUserMarks();

		for (final RoItemMark newMark : marks) {
			existingMarks.remove(newMark);
			existingMarks.add(newMark);
		}

		final UserJsonArray jsonArray = new UserJsonArray();
		for (final RoItemMark itemMark : existingMarks) {
			jsonArray.put(itemMark.toUserJsonObject());
		}

		setAttribute(KEY_USER_MARKS, jsonArray);
	}

	public Collection<RoItemMark> getUserMarks() {
		final Collection<RoItemMark> returnValue = new HashSet<RoItemMark>();
		final UserJsonArray jsonArray = getAttributeAsUserJsonArray(KEY_USER_MARKS);
		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				final RoItemMark marker = RoItemMark.buildFromJson(jsonArray.getString(i));
				returnValue.add(marker);
			}
		}
		return returnValue;
	}

	public Boolean getIsDeleted() {
		return getAttributeAsBoolean(KEY_IS_DELETED);
	}

	public void setDeleted(final Boolean deleted) {
		setAttribute(KEY_IS_DELETED, deleted.toString());
	}

	//

	public static RoUserItem buildFromJson(final String jsonString) {
		return new RoUserItem(jsonString);
	}

	//	public static RoUserItem buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoUserItem(jsonObj);
	//	}

	@Override
	public int hashCode() {
		int returnValue = super.hashCode();
		final String itemId = getItemId();
		if (itemId != null) {
			returnValue = itemId.hashCode();
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
			if (obj instanceof RoUserItem) {
				final RoUserItem other = (RoUserItem) obj;

				final String itemId = getItemId();
				final String otherItemId = other.getItemId();

				if (itemId == null) {
					if (otherItemId == null) {
						returnValue = true;
					}
				}
				else {
					returnValue = itemId.equals(otherItemId);
				}
			}
		}
		return returnValue;
	}

}
