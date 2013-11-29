package com.telenav.user.model;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class UmServiceStatus extends UserModel {

	private String infoLink = null;

	public UmServiceStatus(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmServiceStatus(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public String getInfoLink() {
		return this.infoLink;
	}

	public void setInfoLink(final String infoLink) {
		this.infoLink = infoLink;
	}

}
