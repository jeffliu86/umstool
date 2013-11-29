package com.telenav.user.commons;

import java.math.BigInteger;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

public final class UserCommonUtils extends UserObject {

	private static final UserServiceLogger LOG = UserServiceLogger.getLogger(UserCommonUtils.class);
	private final static String[] SECRET_JSON_KEY = { "secret", "password", "application_secret", "fresh_password", "existing_secret" };

	// To block any accidental instance invocation
	private UserCommonUtils() {
	}

	public static String generateRandomGUID() {

		final UUID randomUUID = UUID.randomUUID();
		final String uuidString = randomUUID.toString();
		final String uuidStringWithoutDashes = uuidString.replaceAll("-", "");
		final BigInteger bigInt = new BigInteger(uuidStringWithoutDashes, 16);
		return bigInt.toString(Character.MAX_RADIX).toUpperCase();
	}

	public static String maskJsonInput(final String jsonStr) {
		return maskJsonInputByKeys(jsonStr, SECRET_JSON_KEY);
	}

	public static String maskJsonInputByKey(final String jsonStr, final String key) {
		if (StringUtils.isNotBlank(jsonStr) && StringUtils.isNotBlank(key)) {
			try {
				//TODO: if password has "," or "}, it's still not able to mask all content
				return jsonStr.replaceAll("\"" + key + "\":\"(.*?)((\",\")|(\"}))", "\"" + key + "\":\"****$2");
			}
			catch (final Exception e) {
				LOG.error(e, "UserCommonUtils.maskJsonInputByKey originalJSON:%s key:%s", jsonStr, key);
			}
		}
		return jsonStr;
	}

	public static String maskJsonInputByKeys(final String jsonStr, final String[] keys) {
		String upatedJSONStr = jsonStr;
		if (StringUtils.isNotBlank(jsonStr) && (keys != null) && (keys.length > 0)) {
			for (final String key : keys) {
				upatedJSONStr = maskJsonInputByKey(upatedJSONStr, key);
			}
		}
		return upatedJSONStr;
	}

}
