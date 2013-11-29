package com.telenav.user.dao.cassandra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.util.RangeBuilder;
import com.telenav.user.commons.LogContext;
import com.telenav.user.commons.UserServiceLogger;
import com.telenav.user.dao.DataAccessException;
import com.telenav.user.dao.UserProfileDao;
import com.telenav.user.resource.RoUserProfile;

public class CassandraUserProfileDao extends CassandraDao implements UserProfileDao {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(CassandraUserProfileDao.class);

	private static final String KEY_USER_PROFILE = "user_profile";
	private static final String KEY_USER_PROFILE_AUDIT = "user_profile_audit";

	private static final ColumnFamily<String, String> CF_USER_PROFILE = new ColumnFamily<String, String>( //
	        KEY_USER_PROFILE, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	private static final ColumnFamily<String, String> CF_USER_PROFILE_AUDIT = new ColumnFamily<String, String>( //
	        KEY_USER_PROFILE_AUDIT, //
	        StringSerializer.get(), //
	        StringSerializer.get());

	public CassandraUserProfileDao(final CassandraUserDaoFactory factory) {
		super(CassandraKeySpace.USER_DATA, factory);
	}

	@Override
	public Collection<RoUserProfile> saveBatch(final String userId, final Collection<RoUserProfile> profiles) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserProfileDao.saveBatch: profiles: %s", profiles);

		Collection<RoUserProfile> returnValue = null;

		try {

			final Long currentTimeMillis = System.currentTimeMillis();

			final MutationBatch mutation = this.keyspace.prepareMutationBatch();

			processExistingProfileAudit(userId, profiles);

			for (final RoUserProfile profile : profiles) {

				profile.setUserId(userId);

				final String profileKey = profile.getProfileKey().toUpperCase();

				final ColumnListMutation<String> rowMutation = mutation.withRow(CF_USER_PROFILE, userId);
				rowMutation.putColumn(profileKey, profile.toJsonString());

				final Long lastAuditTs = profile.getAuditTimestamp();

				if (lastAuditTs != null) {
					final String lastAuditKey = createCompositeKey(String.valueOf(lastAuditTs), profileKey);
					mutation.withRow(CF_USER_PROFILE_AUDIT, userId).deleteColumn(lastAuditKey);

				}

				profile.setAuditTimestamp(currentTimeMillis);

				final String newAuditKey = createCompositeKey(String.valueOf(profile.getAuditTimestamp()), profileKey);
				mutation.withRow(CF_USER_PROFILE_AUDIT, userId).putColumn(newAuditKey, profile.toJsonString());
			}

			if (mutation.getRowCount() > 0) {
				mutation.execute();
				returnValue = profiles;
			}
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserProfileDao.saveBatch: %s", returnValue);

		return returnValue;
	}

	private void processExistingProfileAudit(final String userId, final Collection<RoUserProfile> profileSet) {

		final LogContext logContext = LOG.enter("CassandraUserProfileDao.processExistingProfileAudit: userId: %s, profileSet: %s", userId, profileSet);

		try {

			final ColumnList<String> profileColumns = this.keyspace.prepareQuery(CF_USER_PROFILE).getKey(userId).execute().getResult();

			for (final RoUserProfile profile : profileSet) {

				final String lookupKey = profile.getProfileKey().toUpperCase();
				final Column<String> profileColumn = profileColumns.getColumnByName(lookupKey);

				if (profileColumn != null) {

					final RoUserProfile existedProfile = RoUserProfile.buildFromJson(profileColumn.getStringValue());

					if (existedProfile != null) {
						Long lastAuditTs = existedProfile.getAuditTimestamp();

						if (lastAuditTs == null) {
							lastAuditTs = existedProfile.getModifiedTimestamp();
						}

						profile.setAuditTimestamp(lastAuditTs); // set the value to be purged in write batch operation
					}
				}
				else {
					profile.setAuditTimestamp(null); // explicitly set it to null.  It will be assigned value in saveBatch method
				}
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserProfileDao.processExistingProfileAudit");
	}

	@Override
	public RoUserProfile save(final RoUserProfile profile) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserProfileDao.save: profile: %s", profile);

		RoUserProfile returnValue = null;

		final Collection<RoUserProfile> profiles = new ArrayList<RoUserProfile>(1);
		profiles.add(profile);

		final Collection<RoUserProfile> savedProfiles = saveBatch(profile.getUserId(), profiles);

		if (savedProfiles != null) {
			returnValue = savedProfiles.iterator().next();
		}

		LOG.exit(logContext, "CassandraUserProfileDao.save: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserProfile> listAllProfiles(final String userId) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserProfileDao.listAllProfiles: userId: %s", userId);

		final Collection<RoUserProfile> returnValue = new LinkedList<RoUserProfile>();

		try {

			final ColumnList<String> profileColumns = this.keyspace.prepareQuery(CF_USER_PROFILE).getKey(userId).execute().getResult();

			for (final Column<String> profileColumn : profileColumns) {
				final RoUserProfile profile = RoUserProfile.buildFromJson(profileColumn.getStringValue());
				returnValue.add(profile);
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserProfileDao.listAllProfiles: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserProfile> listProfilesByKeyPrefix(final String userId, final String keyPrefix) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserProfileDao.listProfilesByKeyPrefix: userId: %s, keyPrefix: %s", userId, keyPrefix);

		final Collection<RoUserProfile> returnValue = new LinkedList<RoUserProfile>();

		try {
			final String lookupKey = keyPrefix.toUpperCase();
			final RangeBuilder rangBuilder = new RangeBuilder();
			rangBuilder.setStart(lookupKey + KEY_MIN_UNICODE);
			rangBuilder.setEnd(lookupKey + KEY_MAX_UNICODE);
			rangBuilder.setLimit(Integer.MAX_VALUE);

			final ColumnList<String> profileColumns = this.keyspace.prepareQuery(CF_USER_PROFILE).getKey(userId).withColumnRange(rangBuilder.build()).execute().getResult();

			if (profileColumns.isEmpty()) {
				final RoUserProfile profile = getUserProfileByKey(userId, keyPrefix);
				if (profile != null) {
					returnValue.add(profile);
				}
			}
			else {
				for (final Column<String> profileColumn : profileColumns) {
					final RoUserProfile profile = RoUserProfile.buildFromJson(profileColumn.getStringValue());
					returnValue.add(profile);
				}
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserProfileDao.listProfilesByKeyPrefix: %s", returnValue);

		return returnValue;
	}

	@Override
	public Collection<RoUserProfile> getChangedProfiles(final String userId, final Long fromAuditTimestamp, final Long toAuditTimestamp) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserProfileDao.getChangedProfiles: userId: %s, fromAuditTimestamp: %s, toAuditTimestamp: %s", userId, fromAuditTimestamp, toAuditTimestamp);

		final Collection<RoUserProfile> returnValue = new LinkedList<RoUserProfile>();

		try {

			final RangeBuilder rangBuilder = new RangeBuilder();
			rangBuilder.setStart(String.valueOf(fromAuditTimestamp) + KEY_MIN_UNICODE);
			rangBuilder.setEnd(String.valueOf(toAuditTimestamp) + KEY_MAX_UNICODE);
			rangBuilder.setLimit(Integer.MAX_VALUE);

			final ColumnList<String> profileColumns = this.keyspace.prepareQuery(CF_USER_PROFILE_AUDIT).getKey(userId).withColumnRange(rangBuilder.build()).execute().getResult();

			for (final Column<String> profileColumn : profileColumns) {
				final RoUserProfile profile = RoUserProfile.buildFromJson(profileColumn.getStringValue());
				returnValue.add(profile);
			}

		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserProfileDao.getChangedProfiles: %s", returnValue);

		return returnValue;

	}

	private RoUserProfile getUserProfileByKey(final String userId, final String key) throws DataAccessException {

		final LogContext logContext = LOG.enter("CassandraUserProfileDao.getUserProfileByKey: userId: %s, key: %s", userId, key);

		RoUserProfile returnValue = null;
		try {
			final String lookupKey = key.toUpperCase();
			final Column<String> profileColumn = this.keyspace.prepareQuery(CF_USER_PROFILE).getKey(userId).getColumn(lookupKey).execute().getResult();

			if (profileColumn != null) {
				final RoUserProfile profile = RoUserProfile.buildFromJson(profileColumn.getStringValue());
				returnValue = profile;
			}
		}
		catch (final NotFoundException e) {
			// Silencing the exception, because it is OK to not have any data
		}
		catch (final ConnectionException e) {
			throw new DataAccessException(e);
		}

		LOG.exit(logContext, "CassandraUserProfileDao.getUserProfileByKey: %s", returnValue);

		return returnValue;

	}

}
