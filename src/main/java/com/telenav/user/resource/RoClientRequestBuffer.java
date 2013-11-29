package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;

public class RoClientRequestBuffer extends ResourceObject {

	private static final String KEY_APPLICATION_ID = "application_id";
	private static final String KEY_TRANSACTION_ID = "transaction_id";
	private static final String KEY_REQUEST_ID = "request_id";
	private static final String KEY_PAYLOAD = "payload";

	public RoClientRequestBuffer(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	private RoClientRequestBuffer(final String jsonString) {
		super(jsonString);
	}

	public static RoClientRequestBuffer buildFromJson(final String jsonString) {
		return new RoClientRequestBuffer(jsonString);
	}

	public String getApplicationId() {
		return getAttributeAsString(KEY_APPLICATION_ID);
	}

	public void setApplicationId(final String applicationId) {
		setAttribute(KEY_APPLICATION_ID, applicationId);
	}

	public String getTransactionId() {
		return getAttributeAsString(KEY_TRANSACTION_ID);
	}

	public void setTransactionId(final String transactionId) {
		setAttribute(KEY_TRANSACTION_ID, transactionId);
	}

	public String getRequestId() {
		return getAttributeAsString(KEY_REQUEST_ID);
	}

	public void setRequestId(final String requestId) {
		setAttribute(KEY_REQUEST_ID, requestId);
	}

	public String getPayload() {
		return getAttributeAsString(KEY_PAYLOAD);
	}

	public void setPayload(final String payload) {
		setAttribute(KEY_PAYLOAD, payload);
	}

}
