package be.rubengerits.speed.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.rubengerits.speed.R;

public class SpeedUtils {

    public static float calculateLocalSpeed(Float speed, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String locality = preferences.getString("pref_locality", "kmh");
        if ("kmh".equals(locality)) {
            return speed * 3.6f;
        } else {
            return speed * 2.23694f;
        }
    }

    public static String getTranslatedLocality(String locality, Context context) {
        String[] localityValues = getTranslationArray(R.array.pref_locality_entry_values, context);
        String[] localityTranslations = getTranslationArray(R.array.pref_locality_entries, context);

        for (int i = 0; i < localityValues.length; ++i) {
            if (localityValues[i].equals(locality)) {
                return localityTranslations[i];
            }
        }

        return "";
    }

    private static String[] getTranslationArray(int key, Context context) {
        return context.getResources().getStringArray(key);
    }
}
