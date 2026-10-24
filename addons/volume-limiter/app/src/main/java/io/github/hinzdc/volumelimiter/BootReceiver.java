package io.github.hinzdc.volumelimiter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Starts the VolumeLimiterService after boot or after the app package is updated.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
            if (Prefs.isEnabled(context)) {
                VolumeLimiterService.start(context);
            }
        }
    }
}
