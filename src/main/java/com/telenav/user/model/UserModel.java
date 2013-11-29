package com.telenav.user.model;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

abstract class UserModel extends UserDataObject {

	public UserModel(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UserModel(final UserDataObject userDataObject) {
		super(userDataObject);
	}

}
