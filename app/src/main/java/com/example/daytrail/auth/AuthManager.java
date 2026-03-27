package com.example.daytrail.auth;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USERNAME = "username";
    
    private static AuthManager instance;
    private final SharedPreferences prefs;
    
    private AuthManager(Application application) {
        prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized AuthManager getInstance(Application application) {
        if (instance == null) {
            instance = new AuthManager(application);
        }
        return instance;
    }
    
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public void login(String username, String password) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }
    
    public void logout() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
    
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }
}
