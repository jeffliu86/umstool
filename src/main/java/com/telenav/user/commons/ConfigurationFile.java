package com.telenav.user.commons;

public enum ConfigurationFile {

	USER_SERVICE("user-service"), //
	PASSWORD_RESET_EMAIL("password-reset-email"), //
	DATA_MIGRATION("data-migration"), //
	DATA_IMPORT("data-import"); //

	private final String configId;

	private ConfigurationFile(final String cId) {
		this.configId = cId;
	}

	String getConfigId() {
		return this.configId;
	}
}
