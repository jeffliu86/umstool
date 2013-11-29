package com.telenav.user.resource;

import com.telenav.user.commons.ResponseCode;
import com.telenav.user.commons.UserCommonUtils;
import com.telenav.user.commons.json.UserJsonArray;

public class RoApplication extends ResourceObject {

	private static final String KEY_APPLICATION_ID = "application_id";
	private static final String KEY_APPLICATION_SECRET = "application_secret";
	private static final String KEY_APPLICATION_NAME = "application_name";
	private static final String KEY_IS_LEGACY_SUPPORTED = "is_legacy_supported";
	private static final String KEY_IS_LEGACY_ID_NEEDED = "is_legacy_id_needed";
	private static final String KEY_IMPORT_DATA_IF_LEGACY_USER = "import_data_if_legacy_user";
	private static final String KEY_IS_GENERATE_LEGACY_ID_ON_REGISTER = "is_generate_legacy_id_on_register";
	private static final String KEY_SERVICE_LIST_WITH_ADMIN_ACCESS = "service_list_with_admin_access";

	/*
	app      | action performed | is_legacy_supported | is_legacy_id_needed | imprt_data_if_legacy | is_generate_legacy_id_on_register
	scout.me | registered       | true                | false               | -					   | true
	scout.me | logged-in        | true                | false               | false                | -
	c-server | registered       | true                | true                | -                    | true
	c-server | logged-in        | true                | true                | false                | -
	concrod  | registered       | false               | false               | -                    | -
	concord  | logged-in        | false               | false               | true                 | -
	
	is_legacy_supported => Return the legacyUserId if available
	is_legacy_id_needed => Return the legacyUserId.  Generate new if not available
	*/

	public RoApplication(final ResponseCode responseCode, final String errorMessage) {
		super(responseCode, errorMessage);
	}

	private RoApplication(final String jsonString) {
		super(jsonString);
	}

	//	private RoApplication(final UserJsonObject jsonObj) {
	//		super(jsonObj);
	//	}

	public String getApplicationId() {
		return getAttributeAsString(KEY_APPLICATION_ID);
	}

	public String getApplicationSecret() {
		return getAttributeAsString(KEY_APPLICATION_SECRET);
	}

	public String getName() {
		return getAttributeAsString(KEY_APPLICATION_NAME);
	}

	public Boolean importDataIfLegacyUser() {
		return getAttributeAsBoolean(KEY_IMPORT_DATA_IF_LEGACY_USER);
	}

	public Boolean isLegacySupported() {
		return getAttributeAsBoolean(KEY_IS_LEGACY_SUPPORTED);
	}

	public Boolean isLegacyIdNeeded() {
		return getAttributeAsBoolean(KEY_IS_LEGACY_ID_NEEDED);
	}

	public Boolean isGenerateLegacyIdOnRegister() {
		return getAttributeAsBoolean(KEY_IS_GENERATE_LEGACY_ID_ON_REGISTER);
	}

	public UserJsonArray getServiceListWithAdminAccess() {
		return getAttributeAsUserJsonArray(KEY_SERVICE_LIST_WITH_ADMIN_ACCESS);
	}

	public static RoApplication buildFromJson(final String jsonString) {
		return new RoApplication(jsonString);
	}

	//	public static RoApplication buildFromJson(final UserJsonObject jsonObj) {
	//		return new RoApplication(jsonObj);
	//	}

	@Override
	public final String toString() {
		String returnValue = null;

		if (this != null) {
			final String credentialStr = super.toString();
			returnValue = UserCommonUtils.maskJsonInputByKey(credentialStr, KEY_APPLICATION_SECRET);
		}

		return returnValue;
	}

}
