package com.telenav.user.model;

import java.util.Collection;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.resource.RoTelenavReceipt;

public class UmTelenavReceiptsCollection extends UserModel {

	private Collection<RoTelenavReceipt> receipts = null;

	public UmTelenavReceiptsCollection(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmTelenavReceiptsCollection(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public Collection<RoTelenavReceipt> getReceipts() {
		return this.receipts;
	}

	public void setReceipts(final Collection<RoTelenavReceipt> receipts) {
		this.receipts = receipts;
	}

	@Override
	public String toString() {

		int size = 0;
		if (this.receipts != null) {
			size = this.receipts.size();
		}

		return String.format("%s [receiptCount: %d]", super.toString(), size);
	}
}
