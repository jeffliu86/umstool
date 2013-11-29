package com.telenav.user.dao.cassandra;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.connectionpool.exceptions.BadRequestException;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.query.ColumnFamilyQuery;
import com.netflix.astyanax.query.ColumnQuery;
import com.netflix.astyanax.query.RowQuery;
import com.netflix.astyanax.serializers.StringSerializer;
import com.telenav.user.commons.LogContext;
import com.telenav.user.commons.UserCommonUtils;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.dao.UserCredentialsDao;
import com.telenav.user.model.constant.EnumUserCredentialsType;
import com.telenav.user.resource.RoPasswordResetToken;
import com.telenav.user.resource.RoUserCredentials;

public class CassandraUserCredentialsDao extends CassandraDao implements UserCredentialsDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraUserCredentialsDao.class);

	private static final String KEY_USER_CREDENTIALS = "user_credentials";
	private static final ColumnFamily<String, String> CF_USER_CREDENTIALS = new ColumnFamily<String, String>( //
	        KEY_USER_CREDENTIALS, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final String KEY_CREDENTIALS_KEY_LOOKUP = "credentials_key_lookup";
	private static final ColumnFamily<String, String> CF_CREDENTIALS_KEY_LOOKUP = new ColumnFamily<String, String>( //
	        KEY_CREDENTIALS_KEY_LOOKUP, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final String KEY_PASSWORD_RESET_TOKEN = "password_reset_token";
	private static final ColumnFamily<String, String> CF_PASSWORD_RESET_TOKEN = new ColumnFamily<String, String>( //
	        KEY_PASSWORD_RESET_TOKEN, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	protected CassandraUserCredentialsDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public Collection<RoUserCredentials> set(final Collection<RoUserCredentials> credentials) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserCredentialsDao.set: credentials: %s", credentials);

		Collection<RoUserCredentials> returnValue = null;

		try {
			final MutationBatch mutation = this.keyspace.prepareMutationBatch();

			for (final RoUserCredentials creds : credentials) {

				final String userId = creds.getUserId();

				final ColumnListMutation<String> userCredentials = mutation.withRow(CF_USER_CREDENTIALS, userId);

				final String credentialsIdKey = createCompositeKey(creds.getType().name(), creds.getKey().toUpperCase());
				userCredentials.putColumn(credentialsIdKey, creds.toJsonString());

				final ColumnListMutation<String> credentialsLookup = mutation.withRow(CF_CREDENTIALS_KEY_LOOKUP, credentialsIdKey);
				credentialsLookup.putColumn(String.valueOf(System.currentTimeMillis()), userId);
			}

			mutation.execute();

			returnValue = credentials;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserCredentialsDao.set: %s", returnValue);

		return returnValue;
	}

	/**
	 * Do whatever set() did and add one more entry with TELENAV_SSO_TOKEN/<email> in credentials_key_lookup CF, refer to JIRA: UMS-437
	 * It's only for use identity data migration.
	 */
	@Override
	public Collection<RoUserCredentials> setForDataMigration(final Collection<RoUserCredentials> credentials) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserCredentialsDao.setForDataMigration: credentials: %s", credentials);

		Collection<RoUserCredentials> returnValue = null;

		try {
			final MutationBatch mutation = this.keyspace.prepareMutationBatch();

			for (final RoUserCredentials creds : credentials) {

				final String userId = creds.getUserId();

				final ColumnListMutation<String> userCredentials = mutation.withRow(CF_USER_CREDENTIALS, userId);

				final String credentialsIdKey = createCompositeKey(creds.getType().name(), creds.getKey().toUpperCase());
				userCredentials.putColumn(credentialsIdKey, creds.toJsonString());

				final ColumnListMutation<String> credentialsLookup = mutation.withRow(CF_CREDENTIALS_KEY_LOOKUP, credentialsIdKey);
				credentialsLookup.putColumn(String.valueOf(System.currentTimeMillis()), userId);

				final String ssoTokenCredentialsIdKey = createCompositeKey(EnumUserCredentialsType.TELENAV_SSO_TOKEN.name(), creds.getKey().toUpperCase());
				final ColumnListMutation<String> ssoTokenCredentialsIdKeyLookup = mutation.withRow(CF_CREDENTIALS_KEY_LOOKUP, ssoTokenCredentialsIdKey);
				ssoTokenCredentialsIdKeyLookup.putColumn(String.valueOf(System.currentTimeMillis()), userId);
			}

			mutation.execute();

			returnValue = credentials;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserCredentialsDao.setForDataMigration: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoUserCredentials getCredentialsByKeyForUser(final EnumUserCredentialsType type, final String key, final String userId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserCredentialsDao.getCredentialsByKeyForUser: type: %s, key: %s, userId: %s", type, key, userId);

		RoUserCredentials returnValue = null;

		try {

			final String columnKey = createCompositeKey(type.name(), key);

			final ColumnFamilyQuery<String, String> cfQuery = this.keyspace.prepareQuery(CF_USER_CREDENTIALS);
			final RowQuery<String, String> rowQuery = cfQuery.getKey(userId);
			final ColumnQuery<String> columnQuery = rowQuery.getColumn(columnKey);
			final OperationResult<Column<String>> operationResult = columnQuery.execute();
			final Column<String> column = operationResult.getResult();

			final String credentialsJson = column.getStringValue();
			returnValue = RoUserCredentials.buildFromJson(credentialsJson);

		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserCredentialsDao.getCredentialsByKeyForUser: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserCredentials> getAllCredentialsForUser(final String userId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserCredentialsDao.getAllCredentialsForUser: userId: %s", userId);

		final Collection<RoUserCredentials> returnValue = new LinkedList<RoUserCredentials>();

		try {

			final ColumnFamilyQuery<String, String> cfQuery = this.keyspace.prepareQuery(CF_USER_CREDENTIALS);
			final RowQuery<String, String> rowQuery = cfQuery.getKey(userId);
			final OperationResult<ColumnList<String>> operationResult = rowQuery.execute();
			final ColumnList<String> columnList = operationResult.getResult();

			// ignore the column name and just focus on the value
			for (final Column<String> column : columnList) {
				final String jsonString = column.getStringValue();
				final RoUserCredentials credentials = RoUserCredentials.buildFromJson(jsonString);

				returnValue.add(credentials);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserCredentialsDao.getAllCredentialsForUser: %s", returnValue);

		return returnValue;
	}

	@Override
	public String lookupUserId(final EnumUserCredentialsType type, final String credentialsKey) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserCredentialsDao.lookupUserId: type: %s, credentialsKey: %s", type, credentialsKey);

		String returnValue = null;

		try {

			final String rowKey = createCompositeKey(type.name(), credentialsKey.toUpperCase());

			final ColumnFamilyQuery<String, String> cfQuery = this.keyspace.prepareQuery(CF_CREDENTIALS_KEY_LOOKUP);
			final RowQuery<String, String> rowQuery = cfQuery.getKey(rowKey);
			final OperationResult<ColumnList<String>> operationResult = rowQuery.execute();
			final ColumnList<String> columnList = operationResult.getResult();

			if (columnList.size() > 0) {
				final int lastIndex = columnList.size() - 1;
				returnValue = columnList.getColumnByIndex(lastIndex).getStringValue();
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserCredentialsDao.lookupUserId: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoPasswordResetToken addPasswordResetToken(final RoPasswordResetToken token) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserCredentialsDao.addPasswordResetToken: token: %s", token);

		RoPasswordResetToken returnValue = null;

		try {
			// Always create a new token ... token is immutable
			final String tokenId = UserCommonUtils.generateRandomGUID();
			token.setTokenId(tokenId);

			final long expiryTimeframe = token.getExpiryTimestamp() - token.getCreatedTimestamp();
			final int ttl = (int) (expiryTimeframe / 1000);

			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();
			final ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_PASSWORD_RESET_TOKEN, tokenId);
			columnListMutation.putEmptyColumn(token.toString(), ttl);
			mutaion.execute();

			returnValue = token;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserCredentialsDao.addPasswordResetToken: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoPasswordResetToken getPasswordResetToken(final String tokenId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserCredentialsDao.getPasswordResetToken: tokenId: %s", tokenId);

		RoPasswordResetToken returnValue = null;

		if (StringUtils.isNotEmpty(tokenId)) {
			try {

				final String rowKey = tokenId;

				final ColumnFamilyQuery<String, String> cfQuery = this.keyspace.prepareQuery(CF_PASSWORD_RESET_TOKEN);
				final RowQuery<String, String> rowQuery = cfQuery.getKey(rowKey);
				final OperationResult<ColumnList<String>> operationResult = rowQuery.execute();
				final ColumnList<String> columnList = operationResult.getResult();

				if (columnList.size() == 1) {

					final String tokenJson = columnList.getColumnByIndex(0).getName();
					final RoPasswordResetToken token = RoPasswordResetToken.buildFromJson(tokenJson);

					// Remove the token ... it can only be used once, and is lost after retrieval
					final MutationBatch mutaion = this.keyspace.prepareMutationBatch();
					final ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_PASSWORD_RESET_TOKEN, tokenId);
					columnListMutation.delete();
					mutaion.execute();

					returnValue = token;
				}
			}
			catch (final NotFoundException | BadRequestException e) {
				// Silencing the exception, because it is OK to not have any data
			}
			catch (final ConnectionException e) {
				throw new DataAccessException(e);
			}
		}

		LOG.exit(logContext, "CassandraUserCredentialsDao.getPasswordResetToken: %s", returnValue);

		return returnValue;
	}
}
