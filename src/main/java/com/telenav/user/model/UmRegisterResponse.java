package com.telenav.user.model;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class UmRegisterResponse extends UserModel {

	private String userId = null;

	public UmRegisterResponse(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmRegisterResponse(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}
}
