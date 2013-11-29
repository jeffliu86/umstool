package com.telenav.user.model;

import java.util.HashMap;
import java.util.Map;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.model.constant.EnumMigrationPlayer;

public class UmMigrationStatus extends UserModel {

	private String userId;
	private long billingId;

	private Map<EnumMigrationPlayer, Boolean> playerStatusMap;

	public UmMigrationStatus(final ResponseCode responseCode, final String errorMessage) {

		super(responseCode, errorMessage);
	}

	public UmMigrationStatus(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public long getBillingId() {
		return this.billingId;
	}

	public void setBillingId(final long billingId) {
		this.billingId = billingId;
	}

	public void updatePlayerStatus(final EnumMigrationPlayer player, final boolean status) {
		if (this.playerStatusMap == null) {
			this.playerStatusMap = new HashMap<EnumMigrationPlayer, Boolean>();
		}
		this.playerStatusMap.put(player, status);
	}

	public boolean getPlayerStatus(final EnumMigrationPlayer player) {
		if ((this.playerStatusMap != null) && (this.playerStatusMap.get(player) != null)) {
			return this.playerStatusMap.get(player);
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("userId=").append(this.userId).append(" billingId=").append(this.billingId).append(" playerStatus=").append(this.playerStatusMap);
		return builder.toString();
	}

}
