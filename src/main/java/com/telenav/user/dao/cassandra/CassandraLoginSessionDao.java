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
import com.netflix.astyanax.serializers.StringSerializer;
import com.telenav.user.commons.LogContext;
import com.telenav.user.commons.UserCommonUtils;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.dao.LoginSessionDao;
import com.telenav.user.resource.RoLoginSession;

public class CassandraLoginSessionDao extends CassandraDao implements LoginSessionDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraLoginSessionDao.class);

	private static final String KEY_USER_LOGIN_SESSION = "user_login_session";
	private static final ColumnFamily<String, String> CF_USER_LOGIN_SESSION = new ColumnFamily<String, String>( //
	        KEY_USER_LOGIN_SESSION, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	public CassandraLoginSessionDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public RoLoginSession add(final RoLoginSession session) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraLoginSessionDao.add: session: %s", session);

		RoLoginSession returnValue = null;
		try {
			final String userId = session.getUserId();
			final String columnKeyId = UserCommonUtils.generateRandomGUID();
			final String sessionId = createCompositeKey(userId, columnKeyId);

			session.setSessionId(sessionId);

			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();
			final ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_USER_LOGIN_SESSION, userId);
			columnListMutation.putColumn(columnKeyId, session.toJsonString());
			mutaion.execute();

			returnValue = session;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraLoginSessionDao.add: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoLoginSession update(final RoLoginSession session) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraLoginSessionDao.update: session: %s", session);

		RoLoginSession returnValue = null;
		try {

			final String[] decomposedKeys = session.getSessionId().split(KEY_SEPARATOR);
			final String userId = decomposedKeys[0];
			final String sessionColumnId = decomposedKeys[1];

			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();
			final ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_USER_LOGIN_SESSION, userId);
			columnListMutation.putColumn(sessionColumnId, session.toJsonString());
			mutaion.execute();

			returnValue = session;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraLoginSessionDao.update: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoLoginSession get(final String sessionId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraLoginSessionDao.get: sessionId: %s", sessionId);

		RoLoginSession returnValue = null;

		try {

			final String[] decomposedKeys = sessionId.split(KEY_SEPARATOR);
			final String userId = decomposedKeys[0];
			final String sessionColumnId = decomposedKeys[1];

			final ColumnFamilyQuery<String, String> query = this.keyspace.prepareQuery(CF_USER_LOGIN_SESSION);
			final RowQuery<String, String> rowKey = query.getKey(userId);
			final ColumnQuery<String> columnKey = rowKey.getColumn(sessionColumnId);
			final OperationResult<Column<String>> executeResult = columnKey.execute();
			final Column<String> sessionJsonColumn = executeResult.getResult();
			final String sessionJson = sessionJsonColumn.getStringValue();
			returnValue = RoLoginSession.buildFromJson(sessionJson);
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraLoginSessionDao.get: %s", returnValue);

		return returnValue;
	}

	@Override
	public Boolean remove(final RoLoginSession session) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraLoginSessionDao.remove: session: %s", session);

		Boolean returnValue = Boolean.FALSE;

		try {

			final String[] decomposedKeys = session.getSessionId().split(KEY_SEPARATOR);
			final String keyLookup = decomposedKeys[0];
			final String columnLookup = decomposedKeys[1];

			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();
			final ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_USER_LOGIN_SESSION, keyLookup);
			columnListMutation.deleteColumn(columnLookup);
			mutaion.execute();

			returnValue = Boolean.TRUE;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraLoginSessionDao.remove: %s", returnValue);

		return returnValue;
	}

	@Override
	public Boolean removeAllForUser(final String userId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraLoginSessionDao.removeAllForUser: userId: %s", userId);

		Boolean returnValue = Boolean.FALSE;

		try {

			final String keyLookup = userId;
			final MutationBatch mutaion = this.keyspace.prepareMutationBatch();
			final ColumnListMutation<String> columnListMutation = mutaion.withRow(CF_USER_LOGIN_SESSION, keyLookup);
			columnListMutation.delete();
			mutaion.execute();

			returnValue = Boolean.TRUE;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraLoginSessionDao.removeAllForUser: %s", returnValue);

		return returnValue;
	}
}
