package com.gathin.tvhome;

import android.app.Activity;
import android.os.Bundle;

/**
 * An invisible activity that closes right away. HomeKeyService opens it once at boot (stock_home_redirect,
 * boot_app_redirect): whatever was on screen comes back to the front when it closes and sends a window event, so
 * the service can tell which window that is without reading the screen (#1116).
 */
public class ProbeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().post(this::close);
    }

    private void close() {
        finish();
        overridePendingTransition(0, 0);
    }
}
