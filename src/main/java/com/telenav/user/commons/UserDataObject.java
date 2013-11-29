package com.telenav.user.commons;

public abstract class UserDataObject extends UserObject {

	private ResponseCode responseCode = null;
	private String errorMessage = null;

	public UserDataObject(final ResponseCode responseCode, final String errorMessage) {
		this.responseCode = responseCode;
		this.errorMessage = errorMessage;
	}

	public UserDataObject(final UserDataObject userDataObject) {
		this(userDataObject.responseCode, userDataObject.errorMessage);
	}

	public final void setResponseCode(final UserDataObject dataObject) {
		setResponseCode(dataObject.getResponseCode(), dataObject.getErrorMessage());
	}

	public final void setResponseCode(final ResponseCode responseCode, final String errorMessage) {
		this.responseCode = responseCode;
		this.errorMessage = errorMessage;
	}

	public final boolean isSuccess() {
		return (this.responseCode == ResponseCode.ALL_OK);
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public final ResponseCode getResponseCode() {
		return this.responseCode;
	}

	@Override
	public String toString() {
		return String.format("responseCode: [%s], errorMessage: [%s]", this.responseCode, this.errorMessage);
	}

}
