package com.telenav.user.dao.cassandra;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.query.ColumnFamilyQuery;
import com.netflix.astyanax.query.ColumnQuery;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.serializers.LongSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.telenav.user.commons.LogContext;
import com.telenav.user.commons.UserCommonUtils;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.dao.UserAccountDao;
import com.telenav.user.resource.RoUserMigrationState;
import com.telenav.user.resource.RoUserRegistrationData;

public class CassandraUserAccountDao extends CassandraDao implements UserAccountDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraUserAccountDao.class);

	private static final String KEY_COLUMN_USER_REGISTRATION_DATA = "user_registration_data";
	private static final String KEY_USER_MIGRATION_STATE = "user_migration_state";
	private static final String KEY_USER_ID = "user_id";

	private static final String KEY_USER_ACCOUNT = "user_account";
	private static final String KEY_LEGACY_USER_ID_ACCOUNT_LOOKUP = "legacy_user_id_account_lookup";

	private static final ColumnFamily<String, String> CF_USER_ACCOUNT = new ColumnFamily<String, String>( //
	        KEY_USER_ACCOUNT, //
	        StringSerializer.get(), //
	        StringSerializer.get());
	private static final ColumnFamily<Long, String> CF_LEGACY_USER_ID_ACCOUNT_LOOKUP = new ColumnFamily<Long, String>( //
	        KEY_LEGACY_USER_ID_ACCOUNT_LOOKUP, //
	        LongSerializer.get(), //
	        StringSerializer.get());

	public CassandraUserAccountDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public RoUserRegistrationData add(final RoUserRegistrationData user) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserAccountDao.add: user: %s", user);

		RoUserRegistrationData returnValue = null;

		try {
			final String userId = UserCommonUtils.generateRandomGUID();
			user.setUserId(userId);

			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();
			final ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_USER_ACCOUNT, userId);
			columnListMutation.putColumn(KEY_COLUMN_USER_REGISTRATION_DATA, user.toJsonString());
			mutaion.execute();

			returnValue = user;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserAccountDao.add: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoUserMigrationState setUserMigrationState(final RoUserMigrationState migrationData) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserAccountDao.setUserMigrationState: migrationData: %s", migrationData);

		RoUserMigrationState returnValue = null;

		try {

			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();
			final ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_USER_ACCOUNT, migrationData.getUserId());
			columnListMutation.putColumn(KEY_USER_MIGRATION_STATE, migrationData.toJsonString());

			final ColumnListMutation<String> legacyUserIdLookupMutation = mutaion.withRow(CF_LEGACY_USER_ID_ACCOUNT_LOOKUP, migrationData.getLegacyUserId());
			legacyUserIdLookupMutation.putColumn(KEY_USER_ID, migrationData.getUserId());

			mutaion.execute();

			returnValue = migrationData;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserAccountDao.setUserMigrationState: %s", returnValue);

		return returnValue;
	}

	@Override
	public String getUserIdForLegacyUserId(final Long legacyUserId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserAccountDao.getUserIdForLegacyUserId: legacyUserId: %s", legacyUserId);

		String returnValue = null;

		try {
			if (legacyUserId != null) {
				final Long keyLookup = legacyUserId;

				final ColumnFamilyQuery<Long, String> query = this.keyspace.prepareQuery(CF_LEGACY_USER_ID_ACCOUNT_LOOKUP);
				final RowQuery<Long, String> rowKey = query.getKey(keyLookup);
				final ColumnQuery<String> columnKey = rowKey.getColumn(KEY_USER_ID);
				final OperationResult<Column<String>> executeResult = columnKey.execute();

				final Column<String> migrationStateColumn = executeResult.getResult();
				returnValue = migrationStateColumn.getStringValue();
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserAccountDao.getUserIdForLegacyUserId: %s", returnValue);

		return returnValue;

	}

	@Override
	public RoUserMigrationState getMigrationStateForUser(final String userId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserAccountDao.getMigrationStateForUser: userId: %s", userId);

		RoUserMigrationState returnValue = null;

		try {
			final String keyLookup = userId;
			final String columnLookup = KEY_USER_MIGRATION_STATE;

			final ColumnFamilyQuery<String, String> query = this.keyspace.prepareQuery(CF_USER_ACCOUNT);
			final RowQuery<String, String> rowKey = query.getKey(keyLookup);
			final ColumnQuery<String> columnKey = rowKey.getColumn(columnLookup);
			final OperationResult<Column<String>> executeResult = columnKey.execute();

			final Column<String> migrationStateColumn = executeResult.getResult();
			final String migrationStateJson = migrationStateColumn.getStringValue();

			returnValue = RoUserMigrationState.buildFromJson(migrationStateJson);
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserAccountDao.getMigrationStateForUser: %s", returnValue);

		return returnValue;
	}

	@Override
	public Boolean mergeLegacyUserId(final String fromUserId, final String toUserId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserAccountDao.mergeLegacyUserId: fromUserId: %s, toUserId: %s", fromUserId, toUserId);

		Boolean returnValue = Boolean.FALSE;

		final RoUserMigrationState migrationState = getMigrationStateForUser(fromUserId);

		if (migrationState != null) {

			final Long legacyUserId = migrationState.getLegacyUserId();

			if (legacyUserId != null) {

				try {
					final MutationBatch mutaion = this.keyspace.prepareMutationBatch();

					final ColumnListMutation<String> legacyUserIdLookupMutation = mutaion.withRow(CF_LEGACY_USER_ID_ACCOUNT_LOOKUP, legacyUserId);
					legacyUserIdLookupMutation.putColumn(KEY_USER_ID, toUserId);

					mutaion.execute();

					returnValue = Boolean.TRUE;
				}
				catch (final ConnectionException e) {
					throw new DataAccessException(e);
				}
			}

		}

		LOG.exit(logContext, "CassandraUserAccountDao.mergeLegacyUserId: %s", returnValue);

		return returnValue;
	}

}
