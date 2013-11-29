package com.telenav.user.dao.cassandra;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.query.ColumnFamilyQuery;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.serializers.StringSerializer;
import com.telenav.user.commons.LogContext;
import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.dao.MigrationDao;
import com.telenav.user.model.UmMigrationStatus;
import com.telenav.user.model.constant.EnumMigrationPlayer;

public class CassandraMigrationDao extends CassandraDao implements MigrationDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraMigrationDao.class);

	private static final String CF_NAME_MIGRATION_STATUS = "migration_status";

	private static final String CF_NAME_LEGACY_USER_ID = "legacy_user_id";

	private static final String ROW_KEY_CURRENT_LEGACY_USER_ID = "CURRENT_ID";
	private static final String COLUMN_CURRENT_LEGACY_USER_ID = "ID";

	private static final ColumnFamily<String, String> CF_MIGRATION_STATUS = new ColumnFamily<String, String>( //
	        CF_NAME_MIGRATION_STATUS, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final ColumnFamily<String, String> CF_LEGACY_BILLING_ID = new ColumnFamily<String, String>( //
	        CF_NAME_LEGACY_USER_ID, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	public CassandraMigrationDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public UmMigrationStatus getStatus(final String userId, final long billingId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMigrationDao.getStatus: userId: %s, billingId: %s", userId, billingId);

		UmMigrationStatus returnValue = null;

		try {

			final UmMigrationStatus status = new UmMigrationStatus(ResponseCode.ALL_OK, null);
			status.setUserId(userId);
			status.setBillingId(billingId);

			final String compositeKey = createCompositeKey(userId, String.valueOf(billingId));
			final ColumnFamilyQuery<String, String> cfQuery = this.keyspace.prepareQuery(CF_MIGRATION_STATUS);
			final RowQuery<String, String> rowQuery = cfQuery.getKey(compositeKey);
			final ColumnList<String> columns = rowQuery.execute().getResult();
			for (final Column<String> s : columns) {
				status.updatePlayerStatus(EnumMigrationPlayer.valueOf(s.getName()), s.getBooleanValue());
			}

			returnValue = status;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException("Get migration status error!", e);
		}

		LOG.exit(logContext, "CassandraMigrationDao.getStatus: %s", returnValue);

		return returnValue;
	}

	@Override
	public boolean updateStatus(final String userId, final long billingId, final EnumMigrationPlayer player, final boolean status) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMigrationDao.updateStatus: userId: %s, billingId: %s, player: %s, status: %s", userId, billingId, player, status);

		boolean returnValue = false;

		try {
			final String compositeKey = createCompositeKey(userId, String.valueOf(billingId));
			final MutationBatch m = this.keyspace.prepareMutationBatch();
			m.withRow(CF_MIGRATION_STATUS, compositeKey).putColumn(player.name(), status);
			m.execute();

			returnValue = true;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException("Update migration status error!", e);
		}

		LOG.exit(logContext, "CassandraMigrationDao.updateStatus: %s", returnValue);

		return returnValue;
	}

	@Override
	public Long getCurrentLegacyUserId() throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMigrationDao.getCurrentLegacyUserId: ");

		Long returnValue = null;

		try {

			final ColumnFamilyQuery<String, String> cfQuery = this.keyspace.prepareQuery(CF_LEGACY_BILLING_ID);
			final RowQuery<String, String> rowQuery = cfQuery.getKey(ROW_KEY_CURRENT_LEGACY_USER_ID);
			final ColumnList<String> columns = rowQuery.execute().getResult();
			final String value = columns.getColumnByName(COLUMN_CURRENT_LEGACY_USER_ID).getStringValue();
			returnValue = Long.valueOf(value);
		}
		catch (final NotFoundException e) {
			throw new DataAccessException("Failed to get current leagcy user id", e);
		}
		catch (final ConnectionException e) {
			throw new DataAccessException("Failed to get current leagcy user id", e);
		}

		LOG.exit(logContext, "CassandraMigrationDao.getCurrentLegacyUserId: %s", returnValue);

		return returnValue;
	}

	@Override
	public Long updateCurrentLegacyUserId(final Long legacyUserId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMigrationDao.updateCurrentLegacyUserId: legacyUserId: %s", legacyUserId);

		Long returnValue = null;

		try {
			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();
			final ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_LEGACY_BILLING_ID, ROW_KEY_CURRENT_LEGACY_USER_ID);
			columnListMutation.putColumn(COLUMN_CURRENT_LEGACY_USER_ID, legacyUserId.toString());
			mutaion.execute();

			returnValue = legacyUserId;
		}
		catch (final NotFoundException e) {
			throw new DataAccessException("Failed to update current legacy user id", e);
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraMigrationDao.updateCurrentLegacyUserId: %s", returnValue);

		return returnValue;
	}

}
