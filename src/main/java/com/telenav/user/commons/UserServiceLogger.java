package com.telenav.user.commons;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.telenav.user.commons.json.UserJsonObject;

public final class UserServiceLogger {

	private static final String KEY_USER_SERVICE_LOGGER_CONFIG_FILE_VERSION = "user.service.logger.config.file.version";
	private static final String KEY_USER_SERVICE_LOGGER_CONFIG_FILE = "user.service.logger.config.file";

	private static final Configuration LOGGER_CONFIG = Configuration.getInstance(ConfigurationFile.USER_SERVICE);
	private static String loggerConfigFile = null;
	private static String loggerConfigVersion = null;

	private final Logger logger;

	private static void configLog4J() {

		final String currentConfigVersion = LOGGER_CONFIG.getString(KEY_USER_SERVICE_LOGGER_CONFIG_FILE_VERSION);

		if (!currentConfigVersion.equals(loggerConfigVersion)) {

			synchronized (LOGGER_CONFIG) {

				if (!currentConfigVersion.equals(loggerConfigVersion)) {

					loggerConfigFile = LOGGER_CONFIG.getString(KEY_USER_SERVICE_LOGGER_CONFIG_FILE);

					System.out.println("[CONFIG] - Configuring Log4J to read from: " + loggerConfigFile);

					DOMConfigurator.configure(UserServiceLogger.class.getClassLoader()
							.getResource(loggerConfigFile));

					final Logger logger = Logger.getLogger(UserServiceLogger.class);
					logger.info("************************************************************************************");
					logger.info("*                  Log4j is configured, ready and working now                      *");
					logger.info("************************************************************************************");

					System.out.println("[CONFIG] - Log4J config done!");

					loggerConfigVersion = currentConfigVersion;
				}
			}
		}
	}

	private UserServiceLogger(final Logger l) {

		configLog4J();

		this.logger = l;
	}

	public final static UserServiceLogger getLogger(final Class<? extends BaseUserObject> clazz) {

		final Logger logger = Logger.getLogger(clazz);

		final UserServiceLogger returnValue = new UserServiceLogger(logger);

		return returnValue;
	}

	public final static void LOG_API(final UserJsonObject apiCall) {

		final UserServiceLogger logger = new UserServiceLogger(Logger.getLogger("API-CALL"));
		logger.info(String.format("[API-CALL] - %s", apiCall));
	}

	public final static void ERROR(final Class<?> clazz, final Throwable exception, final String message, final Object... params) {
		final String statement = String.format(message, params);
		Logger.getLogger(clazz).error(statement, exception);
	}

	public final LogContext enter(final String message, final Object... params) {

		LogContext returnValue = null;

		if (this.logger.isTraceEnabled()) {
			returnValue = new LogContext();
			returnValue.setMethodEntryTime(System.currentTimeMillis());

			final String statement = String.format(message, params);
			this.logger.trace("[ENTER] - " + statement);
		}

		return returnValue;
	}

	public final void exit(final LogContext traceContext, final String message, final Object... params) {
		if (this.logger.isTraceEnabled()) {
			final long difference = System.currentTimeMillis() - traceContext.getMethodEntryTime();
			final String statement = String.format(message, params);
			this.logger.trace("[EXIT] - [" + difference + " ms] - " + statement);
		}
	}

	public final void info(final String message, final Object... params) {
		if (this.logger.isInfoEnabled()) {
			final String statement = String.format(message, params);
			this.logger.info(statement);
		}
	}

	public final void error(final String message, final Object... params) {
		final String statement = String.format(message, params);
		this.logger.error(String.format("[%s] - %s", this.logger.getName(), statement));
	}

	public final void error(final Throwable exception, final String message, final Object... params) {
		final String statement = String.format(message, params);
		this.logger.error(String.format("[%s] - %s", this.logger.getName(), statement), exception);
	}

	public final void error(final Throwable exception) {
		this.logger.error(String.format("[%s] - %s", this.logger.getName(), exception.getMessage()), exception);
	}

}
