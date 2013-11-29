package com.telenav.user.dao;

import com.telenav.user.model.UmTelenavReceiptsCollection;
import com.telenav.user.resource.RoDeviceInfo;
import com.telenav.user.resource.RoTelenavReceipt;

public interface ReceiptDao {

	public abstract RoTelenavReceipt put(RoTelenavReceipt receipt) throws DataAccessException;

	public abstract UmTelenavReceiptsCollection getActiveReceipts(String userId, RoDeviceInfo deviceInfo) throws DataAccessException;

	public abstract UmTelenavReceiptsCollection getArchiveReceipts(String userId, RoDeviceInfo deviceInfo) throws DataAccessException;

}
