package com.telenav.user.commons;

public class SystemProperty extends UserObject {

	private static final String KEY_DATACENTER = "datacenter";
	private static final String CONFIG_URI_PROPERTY_KEY = "USER_CONFIG_PATH_URI";
	private static final String CONFIG_REFRESH_DELAY_KEY = "CONFIG_REFRESH_DELAY_IN_MILLIS";

	private static final Long DEFAUL_CONFIG_REFRESH_DELAY = new Long(5000);

	private static String datacenter;
	private static String configUrl;
	private static long configRefreshDelay;

	static {

		//datacenter
		datacenter = System.getProperty(KEY_DATACENTER);
		if (datacenter == null) {
			throw new IllegalStateException("System 'datacenter' Setting not Found.  Please provide valid datacenter name.");
		}

		//config URL
		configUrl = System.getProperty(CONFIG_URI_PROPERTY_KEY);
		if (configUrl == null) {
			throw new IllegalStateException("System Config URI Setting not Found.  Please provide valid Config URI.");
		}

		//config refresh delay
		try {
			final String refreshDelayValue = System.getProperty(CONFIG_REFRESH_DELAY_KEY);
			configRefreshDelay = Long.parseLong(refreshDelayValue);
		}
		catch (final Throwable e) {
			System.out.println("[SystemProperty] - 'Config Refresh Delay' is not provided.  Defaulting to " + DEFAUL_CONFIG_REFRESH_DELAY);
			configRefreshDelay = DEFAUL_CONFIG_REFRESH_DELAY;
		}

	}

	private SystemProperty() {

	}

	public static String getDatacenter() {
		return datacenter;
	}

	public static String getConfigUrl() {
		return configUrl;
	}

	public static Long getConfigRefreshDelay() {
		return configRefreshDelay;
	}

}
