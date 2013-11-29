package com.telenav.user.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.reloading.ReloadingStrategy;

public class Configuration extends UserObject {

	private static final Map<ConfigurationFile, Configuration> configurations = new HashMap<ConfigurationFile, Configuration>();

	public static Configuration getInstance(final ConfigurationFile component) {

		final Configuration returnValue;

		if (configurations.containsKey(component)) {
			returnValue = configurations.get(component);
		}
		else {
			returnValue = new Configuration(component);
			configurations.put(component, returnValue);
		}

		return returnValue;
	}

	private org.apache.commons.configuration.Configuration config = null;

	private Configuration(final ConfigurationFile component) {

		try {
			final String configName = component.getConfigId() + ".properties";
			final String userConfigUrl = SystemProperty.getConfigUrl() + configName;
			final PropertiesConfiguration propConfig = new PropertiesConfiguration(userConfigUrl);

			System.out.println("[CONFIG] - Loading Configuration: " + propConfig.getURL());

			final ReloadingStrategy reloadingStrategy;
			{
				final FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
				fileChangedReloadingStrategy.setRefreshDelay(SystemProperty.getConfigRefreshDelay());
				reloadingStrategy = fileChangedReloadingStrategy;
			}
			propConfig.setReloadingStrategy(reloadingStrategy);

			this.config = propConfig;
		}
		catch (final ConfigurationException e) {
			System.err.println("[CONFIG] - " + e);
			e.printStackTrace();
		}
	}

	public boolean isEmpty() {
		return this.config.isEmpty();
	}

	public static boolean isEmpty(final ConfigurationFile component) {
		return getInstance(component).isEmpty();
	}

	public String getString(final String key) {
		return this.config.getString(key);
	}

	public static String getString(final ConfigurationFile component, final String key) {
		return getInstance(component).config.getString(key);
	}

	public Long getLong(final String key) {
		return this.config.getLong(key);
	}

	public static Long getLong(final ConfigurationFile component, final String key) {
		return getInstance(component).config.getLong(key);
	}

	public Integer getInteger(final String key) {
		return this.config.getInt(key);
	}

	public static Integer getInteger(final ConfigurationFile component, final String key) {
		return getInstance(component).config.getInt(key);
	}

	public Boolean getBoolean(final String key) {
		return this.config.getBoolean(key);
	}

	public static Boolean getBoolean(final ConfigurationFile component, final String key) {
		return getInstance(component).config.getBoolean(key);
	}

	public Float getFloat(final String key) {
		return this.config.getFloat(key);
	}

	public static Float getFloat(final ConfigurationFile component, final String key) {
		return getInstance(component).config.getFloat(key);
	}

	public Double getDouble(final String key) {
		return this.config.getDouble(key);
	}

	public static Double getDouble(final ConfigurationFile component, final String key) {
		return getInstance(component).config.getDouble(key);
	}

	public String[] getStringArray(final String key) {
		return this.config.getStringArray(key);
	}

	public static String[] getStringArray(final ConfigurationFile component, final String key) {
		return getInstance(component).config.getStringArray(key);
	}

	public Properties getProperties(final String key) {
		return this.config.getProperties(key);
	}

	public static Properties getProperties(final ConfigurationFile component, final String key) {
		return getInstance(component).config.getProperties(key);
	}
}
