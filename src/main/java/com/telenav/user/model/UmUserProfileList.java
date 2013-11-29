package com.telenav.user.model;

import java.util.Collection;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.resource.RoUserProfile;

public class UmUserProfileList extends UserModel {

	private Collection<RoUserProfile> profileList = null;

	public UmUserProfileList(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmUserProfileList(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public Collection<RoUserProfile> getProfileList() {
		return this.profileList;
	}

	public void setProfileList(final Collection<RoUserProfile> profileList) {
		this.profileList = profileList;
	}

	@Override
	public String toString() {

		int size = 0;
		if (this.profileList != null) {
			size = this.profileList.size();
		}

		return String.format("%s [profileCount: %d]", super.toString(), size);
	}
}
