package com.telenav.user.model;

import java.util.Collection;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.resource.RoUserItem;

public class UmUserItemList extends UserModel {

	private Collection<RoUserItem> items = null;

	public UmUserItemList(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public UmUserItemList(final UserDataObject userDataObject) {
		super(userDataObject);
	}

	public Collection<RoUserItem> getItems() {
		return this.items;
	}

	public void setItems(final Collection<RoUserItem> items) {
		this.items = items;
	}

	@Override
	public String toString() {

		int size = 0;
		if (this.items != null) {
			size = this.items.size();
		}

		return String.format("%s [itemsCount: %d]", super.toString(), size);
	}
}
