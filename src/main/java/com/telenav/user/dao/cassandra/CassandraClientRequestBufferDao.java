package com.telenav.user.dao.cassandra;

import java.util.ArrayList;
import java.util.Collection;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.MutationBatch;
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
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.ClientRequestBufferDao;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.resource.RoClientRequestBuffer;

public class CassandraClientRequestBufferDao extends CassandraDao implements ClientRequestBufferDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraClientRequestBufferDao.class);

	private static final String KEY_CLIENT_REQUEST_BUFFER = "client_request_buffer";
	private static final ColumnFamily<String, String> CF_CLIENT_REQUEST_BUFFER = new ColumnFamily<String, String>( //
	        KEY_CLIENT_REQUEST_BUFFER, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final Integer REQUEST_TIMEOUT_IN_SECS = new Integer(300);

	protected CassandraClientRequestBufferDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public RoClientRequestBuffer save(final RoClientRequestBuffer requestBuffer) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraClientRequestBufferDao.save: requestBuffer: %s", requestBuffer);

		RoClientRequestBuffer returnValue = null;

		try {

			final MutationBatch mutation = this.keyspace.prepareMutationBatch();
			final String rowKey = createCompositeKey(requestBuffer.getApplicationId(), requestBuffer.getTransactionId());

			final ColumnListMutation<String> bufferRowMutation = mutation.withRow(CF_CLIENT_REQUEST_BUFFER, rowKey);
			bufferRowMutation.putColumn(requestBuffer.getRequestId(), requestBuffer.toJsonString(), REQUEST_TIMEOUT_IN_SECS);

			mutation.execute();

			returnValue = requestBuffer;
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraClientRequestBufferDao.save: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoClientRequestBuffer> get(final RoClientRequestBuffer requestBuffer) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraClientRequestBufferDao.get: requestBuffer: %s", requestBuffer);

		Collection<RoClientRequestBuffer> returnValue = null;

		try {

			final ColumnFamilyQuery<String, String> cfQuery = this.keyspace.prepareQuery(CF_CLIENT_REQUEST_BUFFER);
			final String rowKey = createCompositeKey(requestBuffer.getApplicationId(), requestBuffer.getTransactionId());
			final RowQuery<String, String> rowQuery = cfQuery.getKey(rowKey);
			final OperationResult<ColumnList<String>> operationResult = rowQuery.execute();
			final ColumnList<String> columnList = operationResult.getResult();

			if (columnList != null) {

				returnValue = new ArrayList<RoClientRequestBuffer>(columnList.size());

				// ignore the column name and just focus on the value
				for (final Column<String> column : columnList) {
					final String jsonString = column.getStringValue();
					final RoClientRequestBuffer buffer = RoClientRequestBuffer.buildFromJson(jsonString);

					returnValue.add(buffer);
				}
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraClientRequestBufferDao.get: %s", returnValue);

		return returnValue;
	}

}
