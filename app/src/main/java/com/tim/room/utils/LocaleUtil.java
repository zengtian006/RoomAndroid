package com.tim.room.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.StringDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Created by Ryan on 27/4/2016.
 */
public class LocaleUtil {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ENGLISH, SIMP_CHINESE, TRAD_CHINESE})
    public @interface LocaleDef {
        String[] SUPPORTED_LOCALES = {ENGLISH, SIMP_CHINESE, TRAD_CHINESE};
    }

    //language(zh)-country(CN)
    public static final String ENGLISH = "en-US";
    public static final String SIMP_CHINESE = "zh-CN";
    public static final String TRAD_CHINESE = "zh-TW";

    public static final String SELECTED_LANGUAGE = "SELECTED_LANGUAGE";

    public static void initialize(Context context) {
//        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        setLocale(context, ENGLISH);
    }

    public static void initialize(Context context, @LocaleDef String defaultLanguage) {
        @LocaleDef String lang = getPersistedData(context, defaultLanguage);
        setLocale(context, lang);
    }

//    public static String getLanguage(Context context) {
//        return getPersistedData(context, Locale.getDefault().getLanguage());
//    }

    public static boolean setLocale(Context context, @LocaleDef String language) {
Log.v("LocaleUtil", "set locale "+language);
        persist(context, language);
        return updateResources(context, language);
    }

    public static String getLocale(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, ENGLISH);
    }


    private static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    private static boolean updateResources(Context context, String language) {
        //Locale locale = new Locale(language);
        String strArray[] = language.split("-");
        Locale locale = new Locale(strArray[0],strArray[1]);

        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());


        return true;
    }


}
