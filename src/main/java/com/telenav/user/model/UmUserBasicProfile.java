package com.telenav.user.model;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class UmUserBasicProfile extends UserModel {

	private String firstName = null;
	private String lastName = null;

	public UmUserBasicProfile(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmUserBasicProfile(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

}
