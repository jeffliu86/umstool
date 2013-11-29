package com.telenav.user.dao;

import java.util.Collection;

import com.telenav.user.resource.RoClientRequestBuffer;

public interface ClientRequestBufferDao {

	public abstract RoClientRequestBuffer save(final RoClientRequestBuffer requestBuffer) throws DataAccessException;

	public abstract Collection<RoClientRequestBuffer> get(final RoClientRequestBuffer requestBuffer) throws DataAccessException;
}
