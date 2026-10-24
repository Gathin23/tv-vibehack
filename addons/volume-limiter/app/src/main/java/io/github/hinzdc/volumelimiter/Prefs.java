package io.github.hinzdc.volumelimiter;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores volume limiter preferences.
 */
public final class Prefs {
    public static final String PREFS_NAME = "volume_limiter_prefs";
    public static final String KEY_ENABLED = "limiter_enabled";
    public static final String KEY_LIMIT = "volume_limit";

    public static final int DEFAULT_LIMIT = 10;
    public static final int MIN_LIMIT = 1;
    public static final int MAX_LIMIT = 100;

    private Prefs() {}

    public static SharedPreferences of(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isEnabled(Context context) {
        return of(context).getBoolean(KEY_ENABLED, true);
    }

    public static void setEnabled(Context context, boolean enabled) {
        of(context).edit().putBoolean(KEY_ENABLED, enabled).apply();
    }

    public static int getLimit(Context context) {
        return of(context).getInt(KEY_LIMIT, DEFAULT_LIMIT);
    }

    public static void setLimit(Context context, int limit) {
        int clamped = Math.max(MIN_LIMIT, Math.min(MAX_LIMIT, limit));
        of(context).edit().putInt(KEY_LIMIT, clamped).apply();
    }

    /**
     * Clamps a volume level against the limit if enabled.
     * Returns the capped volume if current > limit, or current unchanged if current <= limit.
     */
    public static int clamp(int currentVolume, int limit, boolean enabled) {
        if (!enabled || limit <= 0) {
            return currentVolume;
        }
        return Math.min(currentVolume, limit);
    }
}
