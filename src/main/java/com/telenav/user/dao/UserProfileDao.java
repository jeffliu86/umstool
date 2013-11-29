package com.telenav.user.dao;

import java.util.Collection;

import com.telenav.user.resource.RoUserProfile;

public interface UserProfileDao {

	public RoUserProfile save(RoUserProfile profile) throws DataAccessException;

	public Collection<RoUserProfile> saveBatch(String userId, Collection<RoUserProfile> profiles) throws DataAccessException;

	public Collection<RoUserProfile> listAllProfiles(String userId) throws DataAccessException;

	public Collection<RoUserProfile> listProfilesByKeyPrefix(String userId, String keyPrefix) throws DataAccessException;

	public Collection<RoUserProfile> getChangedProfiles(String userId, Long fromModifiedTimestamps, Long toModifiedTimestamp) throws DataAccessException;
}
