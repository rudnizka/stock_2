package com.dpcsa.compon.single;

import android.content.Context;
import android.content.SharedPreferences;

public class ComponPrefTool {
    private static final String PREFERENCES_NAME = "simple_app_prefs";
    private static final String TUTORIAL = "tutorial";
    private static final String AUTH = "auth";
    private static final String USER_KEY = "user_key";
    private static final String COOKIE = "cookie";
    private static final String TOKEN = "token";
    private static final String STATUS_COLOR = "STATUS_COLOR";
    private static final String LOCALE = "locale";
    private static final String UPDATE_DB_DATE = "UpdateDBDate";

    public void setUpdateDBDate(String value) {
        getEditor().putString(UPDATE_DB_DATE, value).commit();
    }

    public String getUpdateDBDate() {
        return getSharedPreferences().getString(UPDATE_DB_DATE, "");
    }

    public void setLocale(String value) {
        getEditor().putString(LOCALE, value).commit();
    }

    public String getLocale() {
        return getSharedPreferences().getString(LOCALE, "");
    }

    public void setStatusBarColor(int value) {
        getEditor().putInt(STATUS_COLOR, value).commit();
    }

    public int getStatusBarColor() {
        return getSharedPreferences().getInt(STATUS_COLOR, 0);
    }

    public void setNameBoolean(String name, boolean value) {
        getEditor().putBoolean(name, value).commit();
    }

    public void setNameString(String name, String value) {
        getEditor().putString(name, value).commit();
    }

    public void setNameInt(String name, int value) {
        getEditor().putInt(name, value).commit();
    }

    public boolean getNameBoolean(String name) {
        return getSharedPreferences().getBoolean(name, false);
    }

    public String getNameString(String name) {
        return getSharedPreferences().getString(name, "");
    }

    public int getNameInt(String name, int def) {
        return getSharedPreferences().getInt(name, def);
    }

    public void setTutorial(boolean value) {
        getEditor().putBoolean(TUTORIAL, value).commit();
    }

    public boolean getTutorial() {
        return getSharedPreferences().getBoolean(TUTORIAL, false);
    }

    public void setAuth(boolean value) {
        getEditor().putBoolean(AUTH, value).commit();
    }

    public boolean getAuth() {
        return getSharedPreferences().getBoolean(AUTH, false);
    }

    public void setSessionToken(String token) {
        getEditor().putString(TOKEN, token).commit();
    }

    public String getSessionToken() {
        return getSharedPreferences().getString(TOKEN, "");
    }

    public void setSessionCookie(String cookie) {
        getEditor().putString(COOKIE, cookie).commit();
    }

    public String getSessionCookie() {
        return getSharedPreferences().getString(COOKIE, null);
    }

    public void setUserKey(String user_key) {
        getEditor().putString(USER_KEY, user_key).commit();
    }

    public String getUserKey() {
        return getSharedPreferences().getString(USER_KEY, "");
    }

    //  *************************************************

    private Context context;

    public ComponPrefTool(Context context) {
        this.context = context;
    }

    private SharedPreferences.Editor getEditor() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
