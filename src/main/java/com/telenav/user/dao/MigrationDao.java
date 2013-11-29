package com.telenav.user.dao;

import com.telenav.user.model.UmMigrationStatus;
import com.telenav.user.model.constant.EnumMigrationPlayer;

public interface MigrationDao {

	public abstract UmMigrationStatus getStatus(String userId, long billingId) throws DataAccessException;

	public abstract boolean updateStatus(String userId, long billingId, EnumMigrationPlayer player, boolean status) throws DataAccessException;

	public abstract Long getCurrentLegacyUserId() throws DataAccessException;

	public abstract Long updateCurrentLegacyUserId(Long legacyUserId) throws DataAccessException;
}
