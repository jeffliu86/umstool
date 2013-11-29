package com.telenav.user.commons;

import com.telenav.user.commons.json.UserJsonObject;

public abstract class AbstractEventLogger extends UserObject{

	protected static final String KEY_LOGSHED_HTTP_CONNECTIONS_MAX_COUNT = "logshed.http.connections.max.count";
	protected static final String KEY_LOGSHED_HTTP_CONNECTIONS_TIMEOUT = "logshed.http.connections.timeout";
	protected static final String KEY_LOGSHED_COLLECTOR_ENDPOINT = "logshed.collector.endpoint";
	protected static final String KEY_LOGSHED_COLLECTOR_CONTENT_TYPE = "logshed.collector.content.type";
	protected static UserServiceLogger LOG = UserServiceLogger.getLogger(AbstractEventLogger.class);
	protected static final int DEFAULT_CONTENT_LENGTH = 200;
	
	public abstract UserDataObject writeLogshedEvent(final UserJsonObject eventJson);
}
