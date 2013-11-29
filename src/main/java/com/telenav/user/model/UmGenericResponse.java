package com.telenav.user.model;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class UmGenericResponse extends UserModel {

	public UmGenericResponse(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmGenericResponse(final UserDataObject userDataObject) {
		super(userDataObject);
	}
}
