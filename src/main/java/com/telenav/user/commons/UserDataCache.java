package com.telenav.user.commons;

public class UserDataCache<Data extends UserDataObject> extends UserObject {

	private long lastRefreshTime = 0;
	private Data data;

	public long getLastRefreshTime() {
		return this.lastRefreshTime;
	}

	public void setLastRefreshTime(final long lastRefreshTime) {
		this.lastRefreshTime = lastRefreshTime;
	}

	public Data getData() {
		return this.data;
	}

	public void setData(final Data data) {
		this.data = data;
	}
}
