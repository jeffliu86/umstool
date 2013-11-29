package com.telenav.user.commons;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.load.uri.URITransformer;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.telenav.user.model.UmGenericResponse;

public class JsonSchemaValidator extends UserObject {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(JsonSchemaValidator.class);

	//

	private static Map<String, JsonSchemaValidator> validators = new ConcurrentHashMap<String, JsonSchemaValidator>();

	public static JsonSchemaValidator getInstance(final String version) {

		JsonSchemaValidator returnValue;

		if (validators.containsKey(version)) {
			returnValue = validators.get(version);
		}
		else {
			returnValue = new JsonSchemaValidator(version);
			validators.put(version, returnValue);
		}

		return returnValue;
	}

	//

	private JsonSchemaFactory jsonSchemaFactory;
	private String namespace;
	private String fileVersion;

	private final String namespaceKey;
	private final String version;

	private final static String NAMESPACE_KEY_PREFIX = "json.schema.validation.namespace.";
	private final static String FILEVERSION_KEY = "json.schema.file.version";

	private JsonSchemaValidator(final String version) {

		this.version = version;
		this.namespaceKey = NAMESPACE_KEY_PREFIX + this.version;

		this.namespace = null;
		this.fileVersion = null;

		try {
			initialize();
		}
		catch (final Throwable e) {
			throw new UserException(ResponseCode.GENERAL_INTERNAL_ERROR, e.getMessage(), e);
		}
	}

	private void initialize() throws IllegalArgumentException {

		final Configuration config = Configuration.getInstance(ConfigurationFile.USER_SERVICE);
		final String namespaceConfig = config.getString(this.namespaceKey);
		final String fileVersionConfig = config.getString(FILEVERSION_KEY);

		if ((namespaceConfig != null) && (fileVersionConfig != null)) {

			if (!namespaceConfig.equals(this.namespace) && !fileVersionConfig.equals(this.fileVersion)) {

				LOG.info("Loading %s JSON schema with file version %s", this.version, fileVersionConfig);

				this.namespace = namespaceConfig;
				this.fileVersion = fileVersionConfig;

				final URITransformer transformer = URITransformer.newBuilder().setNamespace(this.namespace).freeze();
				final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setURITransformer(transformer).freeze();

				this.jsonSchemaFactory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
			}
		}
		else {
			throw new IllegalArgumentException("Json Schema Validator loading error!");
		}
	}

	public UserDataObject validate(final String jsonInput, final String schemaName) {

		final LogContext logContext = LOG.enter("JsonSchemaValidator.validate: schemaName: %s", schemaName);

		final UmGenericResponse returnValue = new UmGenericResponse(ResponseCode.GENERAL_INTERNAL_ERROR, null);

		try {

			initialize(); // Check every time if there is any change in configuration

			final JsonNode input = JsonLoader.fromString(jsonInput);

			final JsonSchema schema = this.jsonSchemaFactory.getJsonSchema(schemaName);

			final ProcessingReport report = schema.validate(input);

			if (report.isSuccess()) {
				returnValue.setResponseCode(ResponseCode.ALL_OK, "Valid Request!");
			}
			else {

				final StringBuilder sb = new StringBuilder();
				final String newline = System.getProperty("line.separator");

				final Iterator<ProcessingMessage> badMsgs = report.iterator();

				while (badMsgs.hasNext()) {

					final ProcessingMessage msg = badMsgs.next();
					sb.append(msg.getLogLevel()).append(" : ").append(msg.getMessage()).append(newline);
				}

				returnValue.setResponseCode(ResponseCode.INVALID_PARAMETER, sb.toString());
			}
		}
		catch (final IOException e) {
			throw new UserException(ResponseCode.INVALID_PARAMETER, e.getMessage(), e);
		}
		catch (final Throwable e) {
			throw new UserException(ResponseCode.GENERAL_INTERNAL_ERROR, e.getMessage(), e);
		}

		LOG.exit(logContext, "JsonSchemaValidator.validate: %s", returnValue);

		return returnValue;
	}

}
