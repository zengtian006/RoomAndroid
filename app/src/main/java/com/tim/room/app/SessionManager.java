package com.tim.room.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.tim.room.model.User;

/**
 * Created by Zeng on 2017/1/5.
 */

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();

    SharedPreferences.Editor editor;
    Context _context;
    // Shared Preferences
    SharedPreferences pref;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    public static final String PREF_NAME = "RMLogin";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_TOKEN = "token";
    private static final String KEY_IS_USER = "user";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn, String token, User user) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_IS_TOKEN, token);
        Gson gson = new Gson();
        String json = gson.toJson(user);
        Log.d(TAG, json);
        editor.putString(KEY_IS_USER, json);

        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getUser() {
        Gson gson = new Gson();
        String json = pref.getString(KEY_IS_USER, "");
        User currentUser = gson.fromJson(json, User.class);
        return currentUser;
    }

    public String getToken() {
        return pref.getString(KEY_IS_TOKEN, "");
    }
}
