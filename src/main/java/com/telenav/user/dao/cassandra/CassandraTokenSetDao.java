package com.telenav.user.dao.cassandra;

import java.util.Collection;
import java.util.LinkedList;

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
import com.netflix.astyanax.serializers.StringSerializer;
import com.telenav.user.commons.LogContext;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.dao.TokenSetDao;
import com.telenav.user.resource.RoIdentityTokenSet;

public class CassandraTokenSetDao extends CassandraDao implements TokenSetDao {

	private static UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraTokenSetDao.class);

	private static int GRACE_EXPIRY_TIME_IN_SECS = 180;

	private static final String KEY_IDENTITY_ACCESS_TOKEN = "identity_access_token";
	private static final ColumnFamily<String, String> CF_IDENTITY_ACCESS_TOKEN = new ColumnFamily<String, String>( //
	        KEY_IDENTITY_ACCESS_TOKEN, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final String KEY_IDENTITY_REFRESH_TOKEN = "identity_refresh_token";
	private static final ColumnFamily<String, String> CF_IDENTITY_REFRESH_TOKEN = new ColumnFamily<String, String>( //
	        KEY_IDENTITY_REFRESH_TOKEN, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	protected CassandraTokenSetDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public RoIdentityTokenSet set(final RoIdentityTokenSet tokenSet) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraTokenSetDao.set: tokenSet: %s", tokenSet);

		RoIdentityTokenSet returnValue = null;

		try {

			final String accessToken = tokenSet.getAccessToken();
			final String refreshToken = tokenSet.getRefreshToken();

			final MutationBatch mutation = this.keyspace.prepareMutationBatch();

			final ColumnListMutation<String> accessTokenMutation = mutation.withRow(CF_IDENTITY_ACCESS_TOKEN, accessToken);
			accessTokenMutation.putColumn(tokenSet.getApplicationId(), tokenSet.toJsonString(), (tokenSet.getAccessTokenExpiresInSecs() + GRACE_EXPIRY_TIME_IN_SECS));

			final ColumnListMutation<String> refreshTokenMutation = mutation.withRow(CF_IDENTITY_REFRESH_TOKEN, refreshToken);
			refreshTokenMutation.putColumn(tokenSet.getApplicationId(), tokenSet.toJsonString(), (tokenSet.getRefreshTokenExpiresInSecs() + GRACE_EXPIRY_TIME_IN_SECS));
			mutation.execute();

			returnValue = tokenSet;
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraTokenSetDao.set: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoIdentityTokenSet getByRefreshToken(final String refreshToken, final String applicationId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraTokenSetDao.getByRefreshToken: refreshToken: %s, applicationId: %s", refreshToken, applicationId);

		RoIdentityTokenSet returnValue = null;

		try {

			final ColumnFamilyQuery<String, String> cfQuery = this.keyspace.prepareQuery(CF_IDENTITY_REFRESH_TOKEN);
			final RowQuery<String, String> rowQuery = cfQuery.getKey(refreshToken);
			final ColumnQuery<String> columnQuery = rowQuery.getColumn(applicationId);
			final OperationResult<Column<String>> operationResult = columnQuery.execute();
			final Column<String> column = operationResult.getResult();

			returnValue = RoIdentityTokenSet.buildFromJson(column.getStringValue());
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraTokenSetDao.getByRefreshToken: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoIdentityTokenSet getByAccessToken(final String accessToken, final String applicationId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraTokenSetDao.getByAccessToken: accessToken: %s, applicationId: %s", accessToken, applicationId);

		RoIdentityTokenSet returnValue = null;

		try {

			final ColumnFamilyQuery<String, String> cfQuery = this.keyspace.prepareQuery(CF_IDENTITY_ACCESS_TOKEN);
			final RowQuery<String, String> rowQuery = cfQuery.getKey(accessToken);
			final ColumnQuery<String> columnQuery = rowQuery.getColumn(applicationId);
			final OperationResult<Column<String>> operationResult = columnQuery.execute();
			final Column<String> column = operationResult.getResult();

			returnValue = RoIdentityTokenSet.buildFromJson(column.getStringValue());

		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraTokenSetDao.getByAccessToken: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoIdentityTokenSet remove(final RoIdentityTokenSet tokenSet) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraTokenSetDao.remove: tokenSet: %s", tokenSet);

		RoIdentityTokenSet returnValue = null;

		try {

			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();

			{
				final Collection<ColumnFamily<String, String>> rowsToDelete = new LinkedList<ColumnFamily<String, String>>();
				rowsToDelete.add(CF_IDENTITY_ACCESS_TOKEN);
				mutaion.deleteRow(rowsToDelete, tokenSet.getAccessToken());
			}
			{
				final Collection<ColumnFamily<String, String>> rowsToDelete = new LinkedList<ColumnFamily<String, String>>();
				rowsToDelete.add(CF_IDENTITY_REFRESH_TOKEN);
				mutaion.deleteRow(rowsToDelete, tokenSet.getRefreshToken());
			}
			mutaion.execute();

			returnValue = tokenSet;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraTokenSetDao.remove: %s", returnValue);

		return returnValue;
	}

}
