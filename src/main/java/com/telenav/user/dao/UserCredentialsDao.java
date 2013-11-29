package com.telenav.user.dao;

import java.util.Collection;

import com.telenav.user.model.constant.EnumUserCredentialsType;
import com.telenav.user.resource.RoPasswordResetToken;
import com.telenav.user.resource.RoUserCredentials;

public interface UserCredentialsDao {

	public abstract Collection<RoUserCredentials> set(Collection<RoUserCredentials> credentials) throws DataAccessException;

	/**
	 * Do whatever set() did and add one more entry with TELENAV_SSO_TOKEN/<email> in credentials_key_lookup CF for each credentials, refer to JIRA: UMS-437
	 * It's only for use identity data migration.
	 */
	public Collection<RoUserCredentials> setForDataMigration(final Collection<RoUserCredentials> credentials) throws DataAccessException;

	public abstract Collection<RoUserCredentials> getAllCredentialsForUser(String userId) throws DataAccessException;

	public abstract RoUserCredentials getCredentialsByKeyForUser(EnumUserCredentialsType type, String key, String userId) throws DataAccessException;

	public abstract String lookupUserId(EnumUserCredentialsType type, String credentialsKey) throws DataAccessException;

	public abstract RoPasswordResetToken addPasswordResetToken(RoPasswordResetToken token) throws DataAccessException;

	// Token will be destroyed at fetch
	public abstract RoPasswordResetToken getPasswordResetToken(String tokenId) throws DataAccessException;

}
