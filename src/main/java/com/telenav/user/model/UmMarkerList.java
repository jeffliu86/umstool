package com.telenav.user.model;

import java.util.Collection;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.resource.RoMarker;

public class UmMarkerList extends UserModel {

	private Collection<RoMarker> markerList = null;

	public UmMarkerList(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmMarkerList(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public Collection<RoMarker> getMarkerList() {
		return this.markerList;
	}

	public void setMarkerList(final Collection<RoMarker> markerList) {
		this.markerList = markerList;
	}

	@Override
	public String toString() {

		int size = 0;
		if (this.markerList != null) {
			size = this.markerList.size();
		}

		return String.format("%s [markerCount: %d]", super.toString(), size);
	}
}
