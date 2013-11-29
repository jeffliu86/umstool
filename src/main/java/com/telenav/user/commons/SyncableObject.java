package com.telenav.user.commons;

import com.telenav.user.commons.json.UserJsonObject;

public class SyncableObject extends UserObject implements Comparable<SyncableObject> {

	private String originalSyncId;
	private final String id;
	private final UserJsonObject data;
	private final String checksum;
	private final long modifiedTs;

	public SyncableObject(final String id, final UserJsonObject data, final String checksum, final long modifiedTimestamp) {
		this.id = id;
		this.data = data;
		this.checksum = checksum;
		this.modifiedTs = modifiedTimestamp;
	}

	public Long getModifiedTimestamp() {
		return this.modifiedTs;
	}

	public String getChecksum() {
		return this.checksum;
	}

	public String getSyncId() {
		return this.id;
	}

	public UserJsonObject getObjectData() {
		return this.data;
	}

	public String getOriginalSyncId() {
		return this.originalSyncId;
	}

	public void setOriginalSyncId(final String originalSyncId) {
		this.originalSyncId = originalSyncId;
	}

	@Override
	public String toString() {
		// TODO: Fix this toString method
		return new StringBuilder().append("SyncRecord [")//
		        .append("id=").append(this.id).append(",")//
		        .append("data=").append(this.data).append(",")//
		        .append("checksum=").append(this.checksum).append(",")//
		        .append("modifiedTs=").append(this.modifiedTs).append(",")//
		        .append("originalSyncId=").append(this.originalSyncId)//
		        .append("]").toString();

	}

	@Override
	public int compareTo(final SyncableObject o) {

		int returnValue = 1;

		if (o != null) {
			returnValue = this.id.compareTo(o.id);
		}
		return returnValue;
	}
}
