package com.telenav.user.model;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;

public class UmLoginResponse extends UserModel {

	private String userId = null;
	private String secureToken = null;
	private String refreshToken = null;
	private Integer expiresIn = null;
	private Long legacyUserId = null;
	private String csrId = null;

	public UmLoginResponse(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmLoginResponse(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public Long getLegacyUserId() {
		return this.legacyUserId;
	}

	public void setLegacyUserId(final Long legacyUserId) {
		this.legacyUserId = legacyUserId;
	}

	public String getSecureToken() {
		return this.secureToken;
	}

	public void setSecureToken(final String secureToken) {
		this.secureToken = secureToken;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public void setRefreshToken(final String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Integer getExpiresIn() {
		return this.expiresIn;
	}

	public void setExpiresIn(final Integer expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getCsrId() {
		return this.csrId;
	}

	public void setCsrId(final String csrId) {
		this.csrId = csrId;
	}

}
