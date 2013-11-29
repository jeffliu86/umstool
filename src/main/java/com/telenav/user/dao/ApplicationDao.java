package com.telenav.user.dao;

import com.telenav.user.resource.RoApplication;

public interface ApplicationDao {

	public abstract RoApplication get(String applicationId) throws DataAccessException;

}
