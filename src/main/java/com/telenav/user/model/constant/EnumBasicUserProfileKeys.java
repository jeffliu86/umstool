package com.telenav.user.model.constant;

public enum EnumBasicUserProfileKeys {
	USER_PROFILE_FIRSTNAME("user_profile_firstName"), //
	USER_PROFILE_LASTNAME("user_profile_lastName"), //
	USER_PROFILE_CONTACT_EMAIL("user_profile_contact_email"), //
	USER_PROFILE_EMAILIDCONFIRMED("user_profile_emailIDConfirmed");

	private final String profileKey;

	private EnumBasicUserProfileKeys(final String profileKey) {
		this.profileKey = profileKey;
	}

	public String getProfileKey() {
		return this.profileKey;
	}

}
