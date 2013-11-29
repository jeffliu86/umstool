package com.telenav.user.dao.cassandra;

import java.util.HashMap;
import java.util.Map;

import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.query.ColumnFamilyQuery;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.serializers.StringSerializer;
import com.telenav.user.commons.LogContext;
import com.telenav.user.commons.UserDataCache;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.ApplicationDao;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.resource.RoApplication;

public class CassandraApplicationDao extends CassandraDao implements ApplicationDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraApplicationDao.class);

	private static final String KEY_APPLICATION_CONFIGURATION = "application_configuration";

	private static final ColumnFamily<String, String> CF_APPLICATION_CONFIG = new ColumnFamily<String, String>( //
	        KEY_APPLICATION_CONFIGURATION, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final Map<String, UserDataCache<RoApplication>> APPLICATION_CACHE = new HashMap<String, UserDataCache<RoApplication>>();

	public CassandraApplicationDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public RoApplication get(final String applicationId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraApplicationDao.get: applicationId: %s", applicationId);

		RoApplication returnValue = null;

		boolean refreshCachedData = false;

		UserDataCache<RoApplication> cachedApplication = APPLICATION_CACHE.get(applicationId);
		final long currentTimestamp = System.currentTimeMillis();

		if (cachedApplication == null) {
			refreshCachedData = true;
		}
		else {
			final long difference = currentTimestamp - cachedApplication.getLastRefreshTime();

			if (difference > CACHE_TIME_LIMIT_IN_MILLIS) {
				refreshCachedData = true;
			}
			else {
				returnValue = cachedApplication.getData();
			}
		}

		if (refreshCachedData == true) {

			try {

				final ColumnFamilyQuery<String, String> query = this.keyspace.prepareQuery(CF_APPLICATION_CONFIG);
				final RowQuery<String, String> rowKey = query.getKey(applicationId);
				final OperationResult<ColumnList<String>> executeResult = rowKey.execute();
				final ColumnList<String> result = executeResult.getResult();
				if (!result.isEmpty()) {
					final Column<String> applicationJsonColumn = result.getColumnByIndex(0);
					final String applicationJson = applicationJsonColumn.getStringValue();
					returnValue = RoApplication.buildFromJson(applicationJson);

					if (cachedApplication == null) {
						cachedApplication = new UserDataCache<RoApplication>();
					}

					cachedApplication.setLastRefreshTime(currentTimestamp);
					cachedApplication.setData(returnValue);

					APPLICATION_CACHE.put(applicationId, cachedApplication);
				}
			}
			catch (final NotFoundException e) {
				// Silencing the exception, because it is OK to not have any data
			}
			catch (final ConnectionException e) {
				throw new DataAccessException("Get applicaiton error!", e);
			}
		}

		LOG.exit(logContext, "CassandraApplicationDao.get: %s", returnValue);

		return returnValue;
	}
}
