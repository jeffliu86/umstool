package com.telenav.user.dao;

import java.util.Collection;

import com.telenav.user.resource.RoMarker;

public interface MarkerDao {

	public RoMarker saveUserMarker(String userId, RoMarker roMarker) throws DataAccessException;

	public Collection<RoMarker> saveUserMarkers(String userId, Collection<RoMarker> roMarkers) throws DataAccessException;

	public Collection<RoMarker> ListAllMarkers(String userId) throws DataAccessException;

	public Collection<RoMarker> ListUserMarkers(String userId) throws DataAccessException;

	public Collection<RoMarker> ListSystemMarkers() throws DataAccessException;

	public Collection<RoMarker> listAllMarkersByLabel(String userId, String label) throws DataAccessException;

	public RoMarker getUserMarkerByLabel(String userId, String label) throws DataAccessException;

	public RoMarker getSystemMarkerByLabel(String label) throws DataAccessException;

	public RoMarker getMarkerById(String markerId) throws DataAccessException;

	public RoMarker getUserMarker(String userId, String markerId) throws DataAccessException;

	public Collection<RoMarker> getChangedMarkers(String userId, Long fromModifiedTimestamps, Long toModifiedTimestamp) throws DataAccessException;

}
