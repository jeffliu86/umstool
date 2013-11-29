package com.telenav.user.dao.cassandra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
import com.telenav.user.commons.UserDataCache;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.dao.MarkerDao;
import com.telenav.user.model.constant.EnumMarkerType;
import com.telenav.user.resource.RoMarker;

public class CassandraMarkerDao extends CassandraDao implements MarkerDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraMarkerDao.class);

	private static final String KEY_SYSTEM_USER = "SYSTEM_USER";

	private static final String KEY_USER_MARKER = "marker";
	private static final String KEY_USER_MARKER_LOOKUP = "user_marker_lookup";
	private static final String KEY_USER_MARKER_LABEL_LOOKUP = "user_marker_label_lookup";
	private static final String KEY_USER_MARKER_AUDIT = "user_marker_audit";

	private static final String KEY_COLUMN_MARKER_DATA = "marker_data";

	private static final ColumnFamily<String, String> CF_USER_MARKER = new ColumnFamily<String, String>( //
	        KEY_USER_MARKER, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final ColumnFamily<String, String> CF_USER_MARKER_LOOKUP = new ColumnFamily<String, String>( //
	        KEY_USER_MARKER_LOOKUP, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final ColumnFamily<String, String> CF_USER_MARKER_LABEL_LOOKUP = new ColumnFamily<String, String>( //
	        KEY_USER_MARKER_LABEL_LOOKUP, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final ColumnFamily<String, String> CF_USER_MARKER_AUDIT = new ColumnFamily<String, String>( //
	        KEY_USER_MARKER_AUDIT, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static Collection<RoMarker> ALL_SYSTEM_MARKERS_CACHE = null;
	private static final Map<String, UserDataCache<RoMarker>> SYSTEM_MARKER_CACHE = new HashMap<String, UserDataCache<RoMarker>>();

	//TODO: Use these lookup caching
	@SuppressWarnings("unused")
	private static final Map<String, UserDataCache<RoMarker>> SYSTEM_MARKER_USER_LOOKUP_CACHE = new HashMap<String, UserDataCache<RoMarker>>();
	@SuppressWarnings("unused")
	private static final Map<String, UserDataCache<RoMarker>> SYSTEM_MARKER_LABEL_LOOKUP_CACHE = new HashMap<String, UserDataCache<RoMarker>>();

	public CassandraMarkerDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public RoMarker getMarkerById(final String markerId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.getMarkerById: markerId: %s", markerId);

		RoMarker returnValue = null;
		UserDataCache<RoMarker> cachedSystemMarker = SYSTEM_MARKER_CACHE.get(markerId);

		if (cachedSystemMarker != null) {
			returnValue = cachedSystemMarker.getData();
		}
		else {
			try {
				final Column<String> markerColumn = this.keyspace.prepareQuery(CF_USER_MARKER).getKey(markerId).getColumn(KEY_COLUMN_MARKER_DATA).execute().getResult();
				if (markerColumn != null) {
					final String markerJson = markerColumn.getStringValue();
					returnValue = RoMarker.buildFromJson(markerJson);

					if (returnValue.getMarkerType() == EnumMarkerType.SYSTEM) {
						cachedSystemMarker = new UserDataCache<>();
						cachedSystemMarker.setData(returnValue);
						cachedSystemMarker.setLastRefreshTime(System.currentTimeMillis());
						SYSTEM_MARKER_CACHE.put(markerId, cachedSystemMarker);
					}
				}
			}
			catch (final NotFoundException e) {
				// Silencing the exception
			}
			catch (final ConnectionException e) {
				throw new DataAccessException(e);
			}
		}

		LOG.exit(logContext, "CassandraMarkerDao.getMarkerById: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoMarker getUserMarker(final String userId, final String markerId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.getUserMarker: userId: %s, markerId: %s", userId, markerId);

		RoMarker returnValue = null;
		try {
			final Column<String> markerColumn = this.keyspace.prepareQuery(CF_USER_MARKER_LOOKUP).getKey(userId).getColumn(markerId).execute().getResult();

			if (markerColumn != null) {
				final String markerJson = markerColumn.getStringValue();
				returnValue = RoMarker.buildFromJson(markerJson);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraMarkerDao.getUserMarker: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoMarker> saveUserMarkers(final String userId, final Collection<RoMarker> roMarkers) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.saveUserMarkers: userId: %s, roMarkers: %s", userId, roMarkers);

		Collection<RoMarker> returnValue = null;

		try {

			final Long currentTimeMillis = System.currentTimeMillis();
			final MutationBatch mutation = this.keyspace.prepareMutationBatch();

			int maxSavedSize = 0;
			if (roMarkers != null) {
				maxSavedSize = roMarkers.size();
			}

			final Collection<RoMarker> savedMarkers = new ArrayList<RoMarker>(maxSavedSize);

			for (final RoMarker roMarker : roMarkers) {

				if (roMarker.getMarkerType() != EnumMarkerType.SYSTEM) { // Save/Update non-system markers only

					roMarker.setAuditTimestamp(currentTimeMillis);

					String markerId = roMarker.getMarkerId();
					Long lastAuditTs = null;
					String formerLabel = null;

					boolean generateNewMarkerId = false;

					if (StringUtils.isEmpty(markerId)) {
						generateNewMarkerId = true;
					}
					else if (((roMarker.getMarkerType() != EnumMarkerType.SYSTEM) && roMarker.getLabel().equals(markerId))) {
						// just in case if somehow user marker has id as label
						generateNewMarkerId = true;
					}

					if (generateNewMarkerId == true) {
						markerId = UserCommonUtils.generateRandomGUID();
						roMarker.setMarkerId(markerId);
					}
					else {
						final RoMarker existedMarker = getMarkerById(markerId);
						if (existedMarker != null) {
							lastAuditTs = existedMarker.getAuditTimestamp();
							if (lastAuditTs == null) {
								lastAuditTs = existedMarker.getModifiedTimestamp();
							}
							formerLabel = existedMarker.getLabel();
						}
					}

					// save data
					mutation.withRow(CF_USER_MARKER, markerId).putColumn(KEY_COLUMN_MARKER_DATA, roMarker.toJsonString());

					//add lookup
					mutation.withRow(CF_USER_MARKER_LOOKUP, userId).putColumn(markerId, roMarker.toJsonString());

					//update label lookup
					if (formerLabel != null) {
						mutation.withRow(CF_USER_MARKER_LABEL_LOOKUP, userId).deleteColumn(formerLabel.toUpperCase());
					}
					final String columnKey = roMarker.getLabel().toUpperCase();
					mutation.withRow(CF_USER_MARKER_LABEL_LOOKUP, userId).putColumn(columnKey, roMarker.toJsonString());

					//update audit
					if (lastAuditTs != null) {
						final String lastAuditKey = createCompositeKey(String.valueOf(lastAuditTs), markerId);
						mutation.withRow(CF_USER_MARKER_AUDIT, userId).deleteColumn(lastAuditKey);

					}
					final String newAuditKey = createCompositeKey(String.valueOf(roMarker.getAuditTimestamp()), markerId);
					mutation.withRow(CF_USER_MARKER_AUDIT, userId).putColumn(newAuditKey, roMarker.toJsonString());

					savedMarkers.add(roMarker);
				}
			}

			if (mutation.getRowCount() > 0) {
				mutation.execute();
				returnValue = savedMarkers;
			}
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraMarkerDao.saveUserMarkers: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoMarker saveUserMarker(final String userId, final RoMarker roMarker) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.saveUserMarker: userId: %s, roMarker: %s", userId, roMarker);

		RoMarker returnValue = null;

		if (roMarker.getMarkerType() != EnumMarkerType.SYSTEM) { // Save/Update non-system markers only

			final Collection<RoMarker> markers = new ArrayList<RoMarker>(1);
			markers.add(roMarker);

			final Collection<RoMarker> savedMarkers = saveUserMarkers(userId, markers);

			if (savedMarkers != null) {
				returnValue = savedMarkers.iterator().next();
			}
		}

		LOG.exit(logContext, "CassandraMarkerDao.saveUserMarker: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoMarker> ListUserMarkers(final String userId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.ListUserMarkers: userId: %s", userId);

		final Collection<RoMarker> returnValue = new LinkedList<RoMarker>();

		try {
			final ColumnList<String> markerColumns = this.keyspace.prepareQuery(CF_USER_MARKER_LOOKUP).getKey(userId).execute().getResult();

			for (final Column<String> column : markerColumns) {
				final String markerJson = column.getStringValue();
				final RoMarker marker = RoMarker.buildFromJson(markerJson);
				returnValue.add(marker);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraMarkerDao.ListUserMarkers: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoMarker> getChangedMarkers(final String userId, final Long fromAuditTimestamp, final Long toAuditTimestamp) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.getChangedMarkers: userId: %s, fromAuditTimestamp: %s, toAuditTimestamp: %s", userId, fromAuditTimestamp, toAuditTimestamp);

		final Collection<RoMarker> returnValue = new LinkedList<RoMarker>();

		try {

			final RangeBuilder rangBuilder = new RangeBuilder();
			rangBuilder.setStart(String.valueOf(fromAuditTimestamp) + KEY_MIN_UNICODE);
			rangBuilder.setEnd(String.valueOf(toAuditTimestamp) + KEY_MAX_UNICODE);
			rangBuilder.setLimit(Integer.MAX_VALUE);

			final ColumnList<String> markerColumns = this.keyspace.prepareQuery(CF_USER_MARKER_AUDIT).getKey(userId).withColumnRange(rangBuilder.build()).execute().getResult();

			for (final Column<String> markerColumn : markerColumns) {
				final RoMarker marker = RoMarker.buildFromJson(markerColumn.getStringValue());
				returnValue.add(marker);
			}

		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraMarkerDao.getChangedMarkers: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoMarker> ListSystemMarkers() throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.ListSystemMarkers: ");

		Collection<RoMarker> returnValue = null;

		if (ALL_SYSTEM_MARKERS_CACHE == null) {

			final Collection<RoMarker> markers = new LinkedList<RoMarker>();

			try {
				final ColumnList<String> markerColumns = this.keyspace.prepareQuery(CF_USER_MARKER_LOOKUP).getKey(KEY_SYSTEM_USER).execute().getResult();

				for (final Column<String> column : markerColumns) {
					final String markerJson = column.getStringValue();
					final RoMarker marker = RoMarker.buildFromJson(markerJson);
					markers.add(marker);
				}
			}
			catch (final NotFoundException e) {
				// Silencing the exception
			}
			catch (final ConnectionException e) {
				throw new DataAccessException(e);
			}

			returnValue = markers;
			ALL_SYSTEM_MARKERS_CACHE = returnValue;
		}

		LOG.exit(logContext, "CassandraMarkerDao.ListSystemMarkers: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoMarker getUserMarkerByLabel(final String userId, final String label) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.getUserMarkerByLabel: userId: %s, label: %s", userId, label);

		RoMarker returnValue = null;

		try {
			final String lookupColumnKey = label.toUpperCase();
			final Column<String> markerColumn = this.keyspace.prepareQuery(CF_USER_MARKER_LABEL_LOOKUP).getKey(userId).getColumn(lookupColumnKey).execute().getResult();

			if (markerColumn != null) {
				final String markerJson = markerColumn.getStringValue();
				returnValue = RoMarker.buildFromJson(markerJson);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraMarkerDao.getUserMarkerByLabel: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoMarker> ListAllMarkers(final String userId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.ListAllMarkers: userId: %s", userId);

		final Collection<RoMarker> returnValue = new LinkedList<RoMarker>();

		final Collection<RoMarker> userMarkers = ListUserMarkers(userId);
		final Collection<RoMarker> sytemMarkers = ListSystemMarkers();

		returnValue.addAll(userMarkers);
		returnValue.addAll(sytemMarkers);

		LOG.exit(logContext, "CassandraMarkerDao.ListAllMarkers: %s", returnValue);

		return returnValue;
	}

	@Override
	public RoMarker getSystemMarkerByLabel(final String label) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.getSystemMarkerByLabel: label: %s", label);

		RoMarker returnValue = null;

		try {
			final String lookupColumnKey = label.toUpperCase();
			final Column<String> markerColumn = this.keyspace.prepareQuery(CF_USER_MARKER_LABEL_LOOKUP).getKey(KEY_SYSTEM_USER).getColumn(lookupColumnKey).execute().getResult();

			if (markerColumn != null) {
				final String markerJson = markerColumn.getStringValue();
				returnValue = RoMarker.buildFromJson(markerJson);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraMarkerDao.getSystemMarkerByLabel: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoMarker> listAllMarkersByLabel(final String userId, final String label) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraMarkerDao.listAllMarkersByLabel: userId: %s, label: %s", userId, label);

		final Collection<RoMarker> returnValue = new LinkedList<RoMarker>();

		final RoMarker userMarker = getUserMarkerByLabel(userId, label);
		final RoMarker systemMarker = getSystemMarkerByLabel(label);

		if (userMarker != null) {
			returnValue.add(userMarker);
		}

		if (systemMarker != null) {
			returnValue.add(systemMarker);
		}

		LOG.exit(logContext, "CassandraMarkerDao.listAllMarkersByLabel: %s", returnValue);

		return returnValue;
	}

}
