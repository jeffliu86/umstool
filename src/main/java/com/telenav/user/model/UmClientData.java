package com.telenav.user.model;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class UmClientData extends UserModel {

	private String dataFilesJson = null;
	private String translatedDataJson = null;

	public UmClientData(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmClientData(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public String getDataFilesJson() {
		return this.dataFilesJson;
	}

	public void setDataFilesJson(final String dataFilesJson) {
		this.dataFilesJson = dataFilesJson;
	}

	public String getTranslatedDataJson() {
		return this.translatedDataJson;
	}

	public void setTranslatedDataJson(final String translatedDataJson) {
		this.translatedDataJson = translatedDataJson;
	}

}
