package com.telenav.user.dao;

import com.telenav.user.resource.RoLoginSession;

public interface LoginSessionDao {

	public abstract RoLoginSession add(RoLoginSession session) throws DataAccessException;

	public abstract RoLoginSession update(RoLoginSession session) throws DataAccessException;

	public abstract RoLoginSession get(String sessionId) throws DataAccessException;

	public abstract Boolean remove(RoLoginSession session) throws DataAccessException;

	public abstract Boolean removeAllForUser(String userId) throws DataAccessException;
}
