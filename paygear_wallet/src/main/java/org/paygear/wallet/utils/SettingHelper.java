package org.paygear.wallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SettingHelper {

	private static final String PREF_NAME = "Settings";

	public static String TOKEN_SENT_TO_SERVER = "is_token_sent";
	public static String DEVICE_TOKEN = "device_token";
	public static String LOGIN_STEP = "login_step";
	public static String SCANNER_TIPS = "scanner_tips";
	public static String APP_LANGUAGE = "app_language";
	public static String SERVER_ADDRESS = "server_address";


	public static void putString(Context context, String name, String value) {

		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(name, (String) value);
		editor.commit();
	}

	public static void putStringSet(Context context, String name, Set<String> value) {

		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putStringSet(name, value);
		editor.commit();
	}

	public static void putInt(Context context, String name, int value) {

		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(name, value);
		editor.commit();
	}

	public static void putLong(Context context, String name, long value) {

		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(name, value);
		editor.commit();
	}

	public static void putBoolean(Context context, String name, boolean value) {

		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	public static String getString(Context context, String name, String defaultValue) {
		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		String value;
		try {
			value = prefs.getString(name, defaultValue);
		} catch (Exception e) {
			value = defaultValue;
		}
		return value;
	}

	public static Set<String> getStringSet(Context context, String name, Set<String> defaultValue) {
		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		Set<String> value;
		try {
			value = prefs.getStringSet(name, defaultValue);
		} catch (Exception e) {
			value = defaultValue;
		}
		return value;
	}

	public static int getInt(Context context, String name, int defaultValue) {
		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		int value;
		try {
			value = prefs.getInt(name, defaultValue);
		} catch (Exception e) {
			value = defaultValue;
		}
		return value;
	}

	public static long getLong(Context context, String name, long defaultValue) {
		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		long value;
		try {
			value = prefs.getLong(name, defaultValue);
		} catch (Exception e) {
			value = defaultValue;
		}
		return value;
	}

	public static boolean getBoolean(Context context, String name, boolean defaultValue) {
		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		boolean value;
		try {
			value = prefs.getBoolean(name, defaultValue);
		} catch (Exception e) {
			value = defaultValue;
		}
		return value;
	}

	public static void remove(Context context, String name) {
		SharedPreferences prefs;
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(name);
		editor.commit();
	}

}
