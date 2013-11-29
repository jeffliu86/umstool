package com.telenav.user.dao;

import com.telenav.user.resource.RoIdentityTokenSet;

public interface TokenSetDao {

	public abstract RoIdentityTokenSet set(final RoIdentityTokenSet tokenSet) throws DataAccessException;

	public abstract RoIdentityTokenSet getByRefreshToken(final String refreshToken, final String applicationId) throws DataAccessException;

	public abstract RoIdentityTokenSet getByAccessToken(final String accessToken, final String applicationId) throws DataAccessException;

	public abstract RoIdentityTokenSet remove(final RoIdentityTokenSet tokenSet) throws DataAccessException;
}
