package com.telenav.user.resource;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserDataObject;
import com.telenav.user.commons.json.UserJsonObject;
import com.telenav.user.model.constant.EnumUserCredentialsType;

public class RoUserRegistrationData extends ResourceObject {

	private static final String KEY_USER_ID = "user_id";
	private static final String KEY_USER_CREDENTIALS_TYPE = "user_credentials_type";
	private static final String KEY_USER_CREDENTIALS_KEY = "user_credentials_key";
	private static final String KEY_APPLICATION_ID = "application_id";
	private static final String KEY_DEVICE_INFO = "device_info";
	private static final String KEY_FIRST_NAME = "first_name";
	private static final String KEY_LAST_NAME = "last_name";
	private static final String KEY_REGISTERED_TIMESTAMP = "registered_timestamp";
	private static final String KEY_UPDATE_TIMESTAMP = "update_timestamp";
	private static final String KEY_ADDITIONAL_INFO = "additional_info";

	public RoUserRegistrationData(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	public RoUserRegistrationData(final UserDataObject userDataObject) {
		this(userDataObject.getResponseCode(), userDataObject.getErrorMessage());
	}

	private RoUserRegistrationData(final String jsonString) {
		super(jsonString);
	}

	//	private RoUserRegistrationData(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}

	public void setDeviceInfo(final String deviceInfoJson) {
		setAttribute(KEY_DEVICE_INFO, deviceInfoJson);
	}

	public String getDeviceInfo() {
		return getAttributeAsString(KEY_DEVICE_INFO);
	}

	public String getUserId() {
		return getAttributeAsString(KEY_USER_ID);
	}

	public void setUserId(final String userId) {
		setAttribute(KEY_USER_ID, userId);
	}

	public EnumUserCredentialsType getUserCredentialsType() {
		return EnumUserCredentialsType.valueOf(getAttributeAsString(KEY_USER_CREDENTIALS_TYPE));
	}

	public void setUserCredentialsType(final EnumUserCredentialsType credentialsType) {
		setAttribute(KEY_USER_CREDENTIALS_TYPE, credentialsType.name());
	}

	public String getUserCredentialsKey() {
		return getAttributeAsString(KEY_USER_CREDENTIALS_KEY);
	}

	public void setUserCredentialsKey(final String credentialsKey) {
		setAttribute(KEY_USER_CREDENTIALS_KEY, credentialsKey);
	}

	public String getApplicationId() {
		return getAttributeAsString(KEY_APPLICATION_ID);
	}

	public void setApplicationId(final String appId) {
		setAttribute(KEY_APPLICATION_ID, appId);
	}

	public String getFirstName() {
		return getAttributeAsString(KEY_FIRST_NAME);
	}

	public void setFirstName(final String firstName) {
		setAttribute(KEY_FIRST_NAME, firstName);
	}

	public String getLastName() {
		return getAttributeAsString(KEY_LAST_NAME);
	}

	public void setLastName(final String lastName) {
		setAttribute(KEY_LAST_NAME, lastName);
	}

	public Long getRegisteredTimestamp() {
		return getAttributeAsLong(KEY_REGISTERED_TIMESTAMP);
	}
	
	public String getRegisteredTimeDate(){
		
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		return df.format(
				new Date(getRegisteredTimestamp())
				);
	}

	public void setRegisteredTimestamp(final Long timsetamp) {
		setAttribute(KEY_REGISTERED_TIMESTAMP, timsetamp);
	}

	public Long getUpdateTimestamp() {
		return getAttributeAsLong(KEY_UPDATE_TIMESTAMP);
	}

	public void setUpdateTimestamp(final Long timsetamp) {
		setAttribute(KEY_UPDATE_TIMESTAMP, timsetamp);
	}

	public void setAdditionalInfo(final UserJsonObject additionalInfo) {
		setAttribute(KEY_ADDITIONAL_INFO, additionalInfo);
	}

	public UserJsonObject getAdditionalInfo() {
		return getAttributeAsUserJsonObject(KEY_ADDITIONAL_INFO);
	}

	public static RoUserRegistrationData buildFromJson(final String jsonString) {
		return new RoUserRegistrationData(jsonString);
	}

	//	public static RoUserRegistrationData buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoUserRegistrationData(jsonObj);
	//	}
}
