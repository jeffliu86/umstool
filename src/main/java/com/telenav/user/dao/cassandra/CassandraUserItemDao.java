package com.telenav.user.dao.cassandra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.util.RangeBuilder;
import com.telenav.user.commons.LogContext;
import com.telenav.user.commons.UserCommonUtils;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.dao.MarkerDao;
import com.telenav.user.dao.UserItemDao;
import com.telenav.user.model.constant.EnumMarkerType;
import com.telenav.user.resource.RoItemMark;
import com.telenav.user.resource.RoMarker;
import com.telenav.user.resource.RoUserItem;

public class CassandraUserItemDao extends CassandraDao implements UserItemDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraUserItemDao.class);

	private static final String KEY_USER_ITEM = "user_item";
	private static final String KEY_USER_ITEM_LOOKUP = "user_item_lookup";
	private static final String KEY_USER_ITEM_CORRELATION_LOOKUP = "user_item_correlation_id_lookup";
	private static final String KEY_USER_ITEM_TYPE_LOOKUP = "user_item_type_lookup";
	private static final String KEY_USER_ITEM_MARKER_ID_LOOKUP = "user_item_marker_id_lookup";
	private static final String KEY_USER_ITEM_MARKER_TYPE_LOOKUP = "user_item_marker_type_lookup";
	private static final String KEY_USER_ITEM_AUDIT = "user_item_audit";

	private static final String KEY_COLUMN_ITEM_DATA = "item_data";

	private static final ColumnFamily<String, String> CF_USER_ITEM = new ColumnFamily<String, String>( //
	        KEY_USER_ITEM, //
	        StringSerializer.get(), //
	        StringSerializer.get());
	private static final ColumnFamily<String, String> CF_USER_ITEM_LOOKUP = new ColumnFamily<String, String>( //
	        KEY_USER_ITEM_LOOKUP, //
	        StringSerializer.get(), //
	        StringSerializer.get());
	private static final ColumnFamily<String, String> CF_USER_ITEM_CORRELATION_LOOKUP = new ColumnFamily<String, String>( //
	        KEY_USER_ITEM_CORRELATION_LOOKUP, //
	        StringSerializer.get(), //
	        StringSerializer.get());
	private static final ColumnFamily<String, String> CF_USER_ITEM_TYPE_LOOKUP = new ColumnFamily<String, String>( //
	        KEY_USER_ITEM_TYPE_LOOKUP, //
	        StringSerializer.get(), //
	        StringSerializer.get());
	private static final ColumnFamily<String, String> CF_USER_ITEM_MARKER_ID_LOOKUP = new ColumnFamily<String, String>( //
	        KEY_USER_ITEM_MARKER_ID_LOOKUP, //
	        StringSerializer.get(), //
	        StringSerializer.get());
	private static final ColumnFamily<String, String> CF_USER_ITEM_MARKER_TYPE_LOOKUP = new ColumnFamily<String, String>( //
	        KEY_USER_ITEM_MARKER_TYPE_LOOKUP, //
	        StringSerializer.get(), //
	        StringSerializer.get());
	private static final ColumnFamily<String, String> CF_USER_ITEM_AUDIT = new ColumnFamily<String, String>( //
	        KEY_USER_ITEM_AUDIT, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	public CassandraUserItemDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public RoUserItem saveUserItem(final RoUserItem roUserItem) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.saveUserItem: roUserItem: %s", roUserItem);

		RoUserItem returnValue = null;

		final Collection<RoUserItem> roUserItems = new ArrayList<RoUserItem>(1);
		roUserItems.add(roUserItem);

		final Collection<RoUserItem> savedItems = saveUserItems(roUserItem.getUserId(), roUserItems);

		if (savedItems != null) {
			returnValue = savedItems.iterator().next();
		}

		LOG.exit(logContext, "CassandraUserItemDao.saveUserItem: %s", returnValue);

		return returnValue;
	}

	private void processExistingItemAudit(final String userId, final Collection<RoUserItem> items) {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.processExistingItemAudit: userId: %s, items: %s", userId, items);

		try {

			final ColumnList<String> itemColumns = this.keyspace.prepareQuery(CF_USER_ITEM_LOOKUP).getKey(userId).execute().getResult();

			for (final RoUserItem item : items) {

				final String lookupKey = item.getItemId();

				if (StringUtils.isEmpty(lookupKey)) {

					item.setItemId(UserCommonUtils.generateRandomGUID());
					item.setAuditTimestamp(null); // explicitly set it to null.  It will be assigned value in saveBatch method
				}
				else {

					final Column<String> itemColumn = itemColumns.getColumnByName(lookupKey);

					if (itemColumn != null) {

						final RoUserItem existedItem = RoUserItem.buildFromJson(itemColumn.getStringValue());

						if (existedItem != null) {
							Long lastAuditTs = existedItem.getAuditTimestamp();

							if (lastAuditTs == null) {
								lastAuditTs = existedItem.getModifiedTimestamp();
							}

							item.setAuditTimestamp(lastAuditTs); // set the value to be purged in write batch operation
						}
					}
					else {
						item.setAuditTimestamp(null); // explicitly set it to null.  It will be assigned value in saveBatch method
					}
				}
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserItemDao.processExistingItemAudit");
	}

	@Override
	public Collection<RoUserItem> saveUserItems(final String userId, final Collection<RoUserItem> roUserItems) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.saveUserItema: roUserItems: %s", roUserItems);

		Collection<RoUserItem> returnValue = null;

		try {

			final Long currentTimeMillis = System.currentTimeMillis();
			final MutationBatch mutation = this.keyspace.prepareMutationBatch();

			processExistingItemAudit(userId, roUserItems);

			for (final RoUserItem roUserItem : roUserItems) {

				final String itemId = roUserItem.getItemId();

				//save user item data
				mutation.withRow(CF_USER_ITEM, itemId).putColumn(KEY_COLUMN_ITEM_DATA, roUserItem.toJsonString());

				//add user item lookup
				mutation.withRow(CF_USER_ITEM_LOOKUP, userId).putColumn(itemId, roUserItem.toJsonString());

				//add correlation id lookup
				final String correlationId = roUserItem.getCorrelationId();
				if (StringUtils.isNotBlank(correlationId)) {
					final String lookupKey = createCompositeKey(correlationId, itemId);
					mutation.withRow(CF_USER_ITEM_CORRELATION_LOOKUP, userId).putColumn(lookupKey, roUserItem.toJsonString());
				}

				//add item type lookup
				final String itemType = roUserItem.getItemType();
				if (StringUtils.isNotBlank(itemType)) {
					final String lookupKey = createCompositeKey(itemType, itemId);
					mutation.withRow(CF_USER_ITEM_TYPE_LOOKUP, userId).putColumn(lookupKey, roUserItem.toJsonString());
				}

				//update marker lookup: marker id and marker type
				final Collection<RoItemMark> allMarks = new LinkedList<RoItemMark>();

				final Collection<RoItemMark> systemMarks = roUserItem.getSystemMarks();
				final Collection<RoItemMark> userMarks = roUserItem.getUserMarks();

				allMarks.addAll(systemMarks);
				allMarks.addAll(userMarks);

				final MarkerDao markerDao = this.cassandraUserDaoFactory.getMarkerDao();

				for (final RoItemMark itemMark : allMarks) {
					final String markerId = itemMark.getMarkerId();

					final RoMarker marker = markerDao.getMarkerById(markerId);

					final String cLookupKey = createCompositeKey(markerId, itemId);
					final String rLookupKey = createCompositeKey(userId, marker.getMarkerType().name());

					if (itemMark.isRemoved()) {
						mutation.withRow(CF_USER_ITEM_MARKER_ID_LOOKUP, userId).deleteColumn(cLookupKey);
						mutation.withRow(CF_USER_ITEM_MARKER_TYPE_LOOKUP, rLookupKey).deleteColumn(itemId);
					}
					else {
						mutation.withRow(CF_USER_ITEM_MARKER_ID_LOOKUP, userId).putColumn(cLookupKey, roUserItem.toJsonString());
						mutation.withRow(CF_USER_ITEM_MARKER_TYPE_LOOKUP, rLookupKey).putColumn(itemId, roUserItem.toJsonString());
					}
				}

				//update audit
				final Long lastAuditTs = roUserItem.getAuditTimestamp();
				if (lastAuditTs != null) {
					final String lastAuditKey = createCompositeKey(String.valueOf(lastAuditTs), itemId);
					mutation.withRow(CF_USER_ITEM_AUDIT, userId).deleteColumn(lastAuditKey);
				}

				roUserItem.setAuditTimestamp(currentTimeMillis);

				final String newAuditKey = createCompositeKey(String.valueOf(roUserItem.getAuditTimestamp()), itemId);
				mutation.withRow(CF_USER_ITEM_AUDIT, userId).putColumn(newAuditKey, roUserItem.toJsonString());
			}

			if (mutation.getRowCount() > 0) {
				mutation.execute();
				returnValue = roUserItems;
			}
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserItemDao.saveUserItema: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoUserItem getUserItem(final String userId, final String itemId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.getUserItem: userId: %s, itemId: %s", userId, itemId);

		RoUserItem returnValue = null;
		try {
			final Column<String> itemColumn = this.keyspace.prepareQuery(CF_USER_ITEM_LOOKUP).getKey(userId).getColumn(itemId).execute().getResult();
			if (itemColumn != null) {
				final String itemJson = itemColumn.getStringValue();
				returnValue = RoUserItem.buildFromJson(itemJson);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserItemDao.getUserItem: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserItem> listUserItems(final String userId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.listUserItems: userId: %s", userId);

		final Collection<RoUserItem> returnValue = new LinkedList<RoUserItem>();

		try {
			final ColumnList<String> itemColumns = this.keyspace.prepareQuery(CF_USER_ITEM_LOOKUP).getKey(userId).execute().getResult();

			for (final Column<String> column : itemColumns) {
				final String itemJson = column.getStringValue();
				final RoUserItem item = RoUserItem.buildFromJson(itemJson);
				returnValue.add(item);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserItemDao.listUserItems: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserItem> listUserItemsByItemType(final String userId, final String type) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.listUserItemsByItemType: userId: %s, type: %s", userId, type);

		final Collection<RoUserItem> returnValue = new LinkedList<RoUserItem>();

		try {
			final RangeBuilder rangBuilder = new RangeBuilder();
			rangBuilder.setStart(type + KEY_MIN_UNICODE);
			rangBuilder.setEnd(type + KEY_MAX_UNICODE);
			rangBuilder.setLimit(Integer.MAX_VALUE);

			final ColumnList<String> itemColumns = this.keyspace.prepareQuery(CF_USER_ITEM_TYPE_LOOKUP).getKey(userId).withColumnRange(rangBuilder.build()).execute().getResult();

			for (final Column<String> column : itemColumns) {
				final String itemJson = column.getStringValue();
				final RoUserItem item = RoUserItem.buildFromJson(itemJson);
				returnValue.add(item);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserItemDao.listUserItemsByItemType: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserItem> listUserItemsByItemCorrelationId(final String userId, final String correlationId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.listUserItemsByItemCorrelationId: userId: %s, correlationId: %s", userId, correlationId);

		final Collection<RoUserItem> returnValue = new LinkedList<RoUserItem>();

		try {
			final RangeBuilder rangBuilder = new RangeBuilder();
			rangBuilder.setStart(correlationId + KEY_MIN_UNICODE);
			rangBuilder.setEnd(correlationId + KEY_MAX_UNICODE);
			rangBuilder.setLimit(Integer.MAX_VALUE);

			final ColumnList<String> itemColumns = this.keyspace.prepareQuery(CF_USER_ITEM_CORRELATION_LOOKUP).getKey(userId).withColumnRange(rangBuilder.build()).execute().getResult();

			for (final Column<String> column : itemColumns) {
				final String itemJson = column.getStringValue();
				final RoUserItem item = RoUserItem.buildFromJson(itemJson);
				returnValue.add(item);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserItemDao.listUserItemsByItemCorrelationId: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserItem> listUserItemsByMarkerId(final String userId, final String markerId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.listUserItemsByMarkerId: userId: %s, markerId: %s", userId, markerId);

		final Collection<RoUserItem> returnValue = new LinkedList<RoUserItem>();

		try {
			final RangeBuilder rangBuilder = new RangeBuilder();
			rangBuilder.setStart(markerId + KEY_MIN_UNICODE);
			rangBuilder.setEnd(markerId + KEY_MAX_UNICODE);
			rangBuilder.setLimit(Integer.MAX_VALUE);

			final ColumnList<String> itemColumns = this.keyspace.prepareQuery(CF_USER_ITEM_MARKER_ID_LOOKUP).getKey(userId).withColumnRange(rangBuilder.build()).execute().getResult();

			for (final Column<String> column : itemColumns) {
				final String itemJson = column.getStringValue();
				final RoUserItem item = RoUserItem.buildFromJson(itemJson);
				returnValue.add(item);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserItemDao.listUserItemsByMarkerId: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserItem> listUserItemsByMarkerType(final String userId, final EnumMarkerType markerType) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.listUserItemsByMarkerType: userId: %s, markerType: %s", userId, markerType);

		final Collection<RoUserItem> returnValue = new LinkedList<RoUserItem>();

		try {
			final String rowLookupKey = createCompositeKey(userId, markerType.name());

			final ColumnList<String> itemColumns = this.keyspace.prepareQuery(CF_USER_ITEM_MARKER_TYPE_LOOKUP).getKey(rowLookupKey).execute().getResult();

			for (final Column<String> column : itemColumns) {
				final String itemJson = column.getStringValue();
				final RoUserItem item = RoUserItem.buildFromJson(itemJson);
				returnValue.add(item);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserItemDao.listUserItemsByMarkerType: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserItem> getChangedUserItems(final String userId, final Long fromAuditTimestamp, final Long toAuditTimestamp) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserItemDao.getChangedUserItems: userId: %s, fromAuditTimestamp: %s, toAuditTimestamp: %s", userId, fromAuditTimestamp, toAuditTimestamp);

		final Collection<RoUserItem> returnValue = new LinkedList<RoUserItem>();

		try {

			final RangeBuilder rangBuilder = new RangeBuilder();
			rangBuilder.setStart(String.valueOf(fromAuditTimestamp) + KEY_MIN_UNICODE);
			rangBuilder.setEnd(String.valueOf(toAuditTimestamp) + KEY_MAX_UNICODE);
			rangBuilder.setLimit(Integer.MAX_VALUE);

			final ColumnList<String> itemsColumns = this.keyspace.prepareQuery(CF_USER_ITEM_AUDIT).getKey(userId).withColumnRange(rangBuilder.build()).execute().getResult();

			for (final Column<String> itemColumn : itemsColumns) {
				final RoUserItem item = RoUserItem.buildFromJson(itemColumn.getStringValue());
				returnValue.add(item);
			}

		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserItemDao.getChangedUserItems: %s", returnValue);

		return returnValue;
	}

}
