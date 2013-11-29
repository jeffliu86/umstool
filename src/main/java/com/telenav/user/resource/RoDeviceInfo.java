package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;

public class RoDeviceInfo extends ResourceObject {

	private static final String KEY_DEVICE_INSTANCE_ID = "instance_id";
	private static final String KEY_DEVICE_UID = "device_uid";
	private static final String KEY_DEVICE_MAC_ADDRESS = "mac_address";
	private static final String KEY_OS_NAME = "os_name";
	private static final String KEY_OS_VERSION = "os_version";
	private static final String KEY_MAKE = "make";
	private static final String KEY_MODEL = "model";

	public RoDeviceInfo(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	private RoDeviceInfo(final String json) {
		super(json);
	}

	//	private RoDeviceInfo(final UserJsonObject json) {
	//		super(json);
	//	}

	public String getInstanceId() {
		return getAttributeAsString(KEY_DEVICE_INSTANCE_ID);
	}

	public void setInstanceId(final String instanceId) {
		setAttribute(KEY_DEVICE_INSTANCE_ID, instanceId);
	}

	public String getMake() {
		return getAttributeAsString(KEY_MAKE);
	}

	public void setMake(final String make) {
		setAttribute(KEY_MAKE, make);
	}

	public String getModel() {
		return getAttributeAsString(KEY_MODEL);
	}

	public void setModel(final String model) {
		setAttribute(KEY_MODEL, model);
	}

	public String getOsName() {
		return getAttributeAsString(KEY_OS_NAME);
	}

	public void setOsName(final String osName) {
		setAttribute(KEY_OS_NAME, osName);
	}

	public String getOsVersion() {
		return getAttributeAsString(KEY_OS_VERSION);
	}

	public void setOsVersion(final String osVersion) {
		setAttribute(KEY_OS_VERSION, osVersion);
	}

	public String getDeviceUid() {
		return getAttributeAsString(KEY_DEVICE_UID);
	}

	public void setDeviceUid(final String deviceUid) {
		setAttribute(KEY_DEVICE_UID, deviceUid);
	}

	public String getDeviceMacAddress() {
		return getAttributeAsString(KEY_DEVICE_MAC_ADDRESS);
	}

	public void setDeviceMacAddress(final String devcieMacAddress) {
		setAttribute(KEY_DEVICE_MAC_ADDRESS, devcieMacAddress);
	}

	public static RoDeviceInfo buildFromJson(final String jsonString) {
		return new RoDeviceInfo(jsonString);
	}

	//	public static RoDeviceInfo buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoDeviceInfo(jsonObj);
	//	}

}
