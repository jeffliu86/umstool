package com.telenav.user.dao;

import com.telenav.user.resource.RoUserMigrationState;
import com.telenav.user.resource.RoUserRegistrationData;

public interface UserAccountDao {

	public abstract RoUserRegistrationData add(RoUserRegistrationData user) throws DataAccessException;

	public abstract RoUserMigrationState setUserMigrationState(RoUserMigrationState migrationData) throws DataAccessException;

	public abstract RoUserMigrationState getMigrationStateForUser(String user) throws DataAccessException;

	public abstract String getUserIdForLegacyUserId(Long legacyUserId) throws DataAccessException;

	public abstract Boolean mergeLegacyUserId(String fromUserId, String toUserId) throws DataAccessException;

}
