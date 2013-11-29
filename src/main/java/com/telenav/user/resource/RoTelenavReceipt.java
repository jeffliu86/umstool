package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.commons.json.UserJsonObject;

public class RoTelenavReceipt extends ResourceObject implements Comparable<RoTelenavReceipt> {

	private static final String USER_ID = "user_id";
	private static final String DEVICE_INSTANCE_ID = "device_instance_id";
	private static final String DEVICE_UID = "device_uid";
	private static final String DEVICE_MAC_ADDRESS = "device_mac_address";
	private static final String APPLICATION_ID = "application_id";
	private static final String RECEIPT_ID = "receipt_id";
	private static final String TRANSACTION_ID = "transaction_id";
	private static final String OFFER_CODE = "offer_code";
	private static final String OFFER_VERSION = "offer_version";
	private static final String PURCHASE_UTC_TIMESTAMP = "purchase_utc_timestamp";
	private static final String OFFER_EXPIRY_UTC_TIMESTAMP = "offer_expiry_utc_timestamp";
	private static final String RECEIPT_ARCHIVABLE = "receipt_archivable";
	private static final String DESCRIPTION = "description";
	private static final String PAYMENT_PROCESSOR = "payment_processor";
	private static final String RECEIPT_DATA = "receipt_data";
	private static final String ADDITIONAL_INFO = "additional_info";
	private static final String SKU = "sku";

	public RoTelenavReceipt(final ResponseCode responseCode, final String errorMessage) {

		super(responseCode, errorMessage);
	}

	public RoTelenavReceipt(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoTelenavReceipt(final String json) {
		super(json);
	}

	//	private RoTelenavReceipt(final UserJsonObject json) {
	//		super(json);
	//	}

	public String getUserId() {
		return getAttributeAsString(USER_ID);
	}

	public void setUserId(final String userId) {
		setAttribute(USER_ID, userId);
	}

	public String getDeviceInstanceId() {
		return getAttributeAsString(DEVICE_INSTANCE_ID);
	}

	public void setDeviceInstanceId(final String deviceInstanceId) {
		setAttribute(DEVICE_INSTANCE_ID, deviceInstanceId);
	}

	public String getDeviceUid() {
		return getAttributeAsString(DEVICE_UID);
	}

	public void setDeviceUid(final String deviceUid) {
		setAttribute(DEVICE_UID, deviceUid);
	}

	public String getDeviceMacAddress() {
		return getAttributeAsString(DEVICE_MAC_ADDRESS);
	}

	public void setDeviceMacAddress(final String deviceMacAddress) {
		setAttribute(DEVICE_MAC_ADDRESS, deviceMacAddress);
	}

	public String getApplicationId() {
		return getAttributeAsString(APPLICATION_ID);
	}

	public void setApplicationId(final String applicationId) {
		setAttribute(APPLICATION_ID, applicationId);
	}

	public String getReceiptId() {
		return getAttributeAsString(RECEIPT_ID);
	}

	public void setReceiptId(final String receiptId) {
		setAttribute(RECEIPT_ID, receiptId);
	}

	public String getTransactionId() {
		return getAttributeAsString(TRANSACTION_ID);
	}

	public void setTransactionId(final String transactionId) {
		setAttribute(TRANSACTION_ID, transactionId);
	}

	public String getOfferCode() {
		return getAttributeAsString(OFFER_CODE);
	}

	public void setOfferCode(final String offerCode) {
		setAttribute(OFFER_CODE, offerCode);
	}

	public String getOfferVersion() {
		return getAttributeAsString(OFFER_VERSION);
	}

	public void setOfferVersion(final String offerVersion) {
		setAttribute(OFFER_VERSION, offerVersion);
	}

	public Long getPurchaseTimestamp() {
		return getAttributeAsLong(PURCHASE_UTC_TIMESTAMP);
	}

	public void setPurchaseTimestamp(final Long purchaseUTCTimestamp) {
		setAttribute(PURCHASE_UTC_TIMESTAMP, purchaseUTCTimestamp);
	}

	public Long getOfferExpiryTimestamp() {
		return getAttributeAsLong(OFFER_EXPIRY_UTC_TIMESTAMP);
	}

	public void setOfferExpiryTimestamp(final Long offerExpiryTimestamp) {
		setAttribute(OFFER_EXPIRY_UTC_TIMESTAMP, offerExpiryTimestamp);
	}

	public Boolean isReceiptArchivable() {
		return getAttributeAsBoolean(RECEIPT_ARCHIVABLE);
	}

	public void setReceiptArchivable(final Boolean receiptArchivable) {
		setAttribute(RECEIPT_ARCHIVABLE, receiptArchivable);
	}

	public String getDescription() {
		return getAttributeAsString(RoTelenavReceipt.DESCRIPTION);
	}

	public void setDescription(final String description) {
		setAttribute(DESCRIPTION, description);
	}

	public String getPaymentProcessor() {
		return getAttributeAsString(PAYMENT_PROCESSOR);
	}

	public void setPaymentProcessor(final String paymentProcessor) {
		setAttribute(PAYMENT_PROCESSOR, paymentProcessor);
	}

	public String getReceiptData() {
		return getAttributeAsString(RECEIPT_DATA);
	}

	public void setReceiptData(final String receiptData) {
		setAttribute(RECEIPT_DATA, receiptData);
	}

	public UserJsonObject getAdditionalInfo() {
		return getAttributeAsUserJsonObject(ADDITIONAL_INFO);
	}

	public void setAdditionalInfo(final UserJsonObject additionalInfo) {
		if (additionalInfo != null) {
			setAttribute(ADDITIONAL_INFO, additionalInfo);
		}
	}

	public void setSku(final String sku) {
		setAttribute(SKU, sku);
	}

	public String getSku() {
		return getAttributeAsString(SKU);
	}

	public static RoTelenavReceipt buildFromJson(final String jsonString) {
		return new RoTelenavReceipt(jsonString);
	}

	//	public static RoTelenavReceipt buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoTelenavReceipt(jsonObj);
	//	}

	@Override
	public int compareTo(final RoTelenavReceipt other) {

		int returnValue = 1;

		if (other != null) {
			returnValue = getReceiptId().compareTo(other.getReceiptId());
		}

		return returnValue;
	}
}
