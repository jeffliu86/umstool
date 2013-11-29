package com.telenav.user.dao.cassandra;

import com.netflix.astyanax.Keyspace;
import com.telenav.user.commons.UserObject;

public abstract class CassandraDao extends UserObject {

	protected static final String KEY_SEPARATOR = "/";
	protected static final String KEY_MIN_UNICODE = "\u00000";
	protected static final String KEY_MAX_UNICODE = "\uffff";

	protected static final long CACHE_TIME_LIMIT_IN_MILLIS = (5 * 60 * 1000); //  5 minutes

	protected final Keyspace keyspace;
	protected final CassandraUserDaoFactory cassandraUserDaoFactory;

	protected CassandraDao(final CassandraKeySpace ksEnum, final CassandraUserDaoFactory factory) {
		this.keyspace = ksEnum.getKeySpace();
		this.cassandraUserDaoFactory = factory;
	}

	protected final String createCompositeKey(final String... keys) {

		final String returnValue;

		final StringBuilder builder = new StringBuilder();

		boolean isFirst = true;

		for (final String key : keys) {

			if (key == null) {
				throw new IllegalArgumentException("Composite Key constituent cannot be null");
			}

			if (!isFirst) {
				builder.append(KEY_SEPARATOR);
			}

			builder.append(key);
			isFirst = false;
		}

		returnValue = builder.toString();

		return returnValue;

	}
}
