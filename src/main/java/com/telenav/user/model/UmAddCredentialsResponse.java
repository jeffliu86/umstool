package com.telenav.user.model;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class UmAddCredentialsResponse extends UserModel {

	private String loggedInUserId = null;
	private String newCredentialsOwnerId = null;
	private boolean accountMergeRequired = false;
	private boolean dataMergeRequired = false;
	private boolean reloginRequired = false;

	public UmAddCredentialsResponse(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmAddCredentialsResponse(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public String getLoggedInUserId() {
		return this.loggedInUserId;
	}

	public void setLoggedInUserId(final String loggedInUserId) {
		this.loggedInUserId = loggedInUserId;
	}

	public String getNewCredentialsOwnerId() {
		return this.newCredentialsOwnerId;
	}

	public void setNewCredentialsOwnerId(final String newCredentialsOwnerId) {
		this.newCredentialsOwnerId = newCredentialsOwnerId;
	}

	public boolean isAccountMergeRequired() {
		return this.accountMergeRequired;
	}

	public void setAccountMergeRequired(final boolean accountMergeRequired) {
		this.accountMergeRequired = accountMergeRequired;
	}

	public boolean isDataMergeRequired() {
		return this.dataMergeRequired;
	}

	public void setDataMergeRequired(final boolean dataMergeRequired) {
		this.dataMergeRequired = dataMergeRequired;
	}

	public boolean isReloginRequired() {
		return this.reloginRequired;
	}

	public void setReloginRequired(final boolean reloginRequired) {
		this.reloginRequired = reloginRequired;
	}

}
