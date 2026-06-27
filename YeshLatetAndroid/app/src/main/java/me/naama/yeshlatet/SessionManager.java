package me.naama.yeshlatet;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "yesh_latet_session"; // where do i save the information
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TYPE = "type";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        // set up the shared prefrences
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); // cant read it without the app
        editor = prefs.edit();
    }

    public void saveLogin(String username, String type) {
        editor.putBoolean(KEY_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_TYPE, type);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getType() {
        return prefs.getString(KEY_TYPE, null);
    }

    public void logout() {
        editor.clear();
        editor.apply(); // save
    }
}