package com.app.searchimages;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	
	/* Key for the search text being passed to an activity */
	public static final String SEARCH_TEXT = "SEARCH_TEXT";
	/* Key for the bitmap being passed to an activity in bytes */
	public static final String BITMAP_IN_BYTES = "BITMAP_IN_BYTES";
	/* Key for the Search history pref */
	public static final String SEARCH_HISTORY_PREF = "image_search_history_pref";
	/* Location of the shared preferences for this app */
	public static final String SHARED_PREFERENCES = "com.app.searchimages.SharedPreferences";
	/* The base url for the Google Image Search API that requests for medium sized JPEG images. The result size is 8 images per call */
	public static final String BASE_URL = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8&imgsz=medium&as_fileType=jpg&q=";

	/**
	 * Check to see if the phone is connected to the Internet
	 * @param context Context of the activity
	 * @return boolean true if connected, false otherwise
	 */
	public static boolean isConnected(final Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

		return (ni == null || !ni.isConnectedOrConnecting() ? false : true);

	}
	
	/**
	 * Get the shared prefs for this app
	 * @param context The context of the app
	 * @return Reference to the SharedPreference
	 */
	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(Utils.SHARED_PREFERENCES, Context.MODE_PRIVATE);
	}
	
	/**
	 * Add a string value to the shared prefs Set<String>
	 * @param context The context of the app
	 * @param key Key for the Set<String> pref
	 * @param value String value to be added to the Set<String> pref
	 */
	public static void addToSharedPrefsSet(Context context, String key, String value) {
		SharedPreferences sharedPrefs = getSharedPreferences(context);
		
		Set<String> searchSet = sharedPrefs.getStringSet(key, new HashSet<String>());
		
		Set<String> tempSet = new HashSet<String>();
		for (String searchStr : searchSet) {
			tempSet.add(searchStr);
		}
		tempSet.add(value);

		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putStringSet(key, tempSet);
		editor.commit();
	}
	
	/**
	 * Get the Set<String> in the shared prefs
	 * @param context The context of the app
	 * @param key Key for the Set<String> in the prefs
	 * @return The Set<String>
	 */
	public static Set<String> getSharedPrefsSet(Context context, String key) {
		SharedPreferences prefs = getSharedPreferences(context);
		return prefs.getStringSet(Utils.SEARCH_HISTORY_PREF, new HashSet<String>());
	}
	
	public static void removeSharedPrefsItem(Context context, String key) {
		SharedPreferences sharedPrefs = getSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.remove(key);
		editor.commit();
	}
}
