package com.telenav.user.dao;

import java.util.Collection;

import com.telenav.user.model.constant.EnumMarkerType;
import com.telenav.user.resource.RoUserItem;

public interface UserItemDao {

	public RoUserItem saveUserItem(RoUserItem roUserItem) throws DataAccessException;

	public Collection<RoUserItem> saveUserItems(String userId, Collection<RoUserItem> roUserItems) throws DataAccessException;

	public RoUserItem getUserItem(String userId, String itemId) throws DataAccessException;

	public Collection<RoUserItem> listUserItems(String userId) throws DataAccessException;

	public Collection<RoUserItem> listUserItemsByItemType(String userId, String type) throws DataAccessException;

	public Collection<RoUserItem> listUserItemsByItemCorrelationId(String userId, String correlationId) throws DataAccessException;

	public Collection<RoUserItem> listUserItemsByMarkerId(String userId, String markerId) throws DataAccessException;

	public Collection<RoUserItem> listUserItemsByMarkerType(String userId, EnumMarkerType markerType) throws DataAccessException;

	public Collection<RoUserItem> getChangedUserItems(String userId, Long fromModifiedTimestamps, Long toModifiedTimestamp) throws DataAccessException;

}
