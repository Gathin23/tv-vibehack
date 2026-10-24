package io.github.hinzdc.volumelimiter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * TV interface for configuring the Volume Limiter.
 * Fully navigable using the TV remote D-pad.
 */
public class MainActivity extends Activity {
    private AudioManager audioManager;
    private Row statusRow;
    private Row limitRow;
    private Row currentVolumeRow;

    private final BroadcastReceiver volumeUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateValues();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        float dp = getResources().getDisplayMetrics().density;
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackgroundColor(0xFF0F141C);
        int pad = Math.round(36 * dp);
        page.setPadding(pad, pad, pad, pad);

        // Header Title
        TextView title = new TextView(this);
        title.setText(R.string.app_name);
        title.setTextSize(26);
        title.setTextColor(0xFFFFFFFF);
        page.addView(title);

        // Header Subtitle
        TextView subtitle = new TextView(this);
        subtitle.setText("Caps the maximum volume. Raising volume above the limit pulls it back down.");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF8B949E);
        subtitle.setPadding(0, Math.round(4 * dp), 0, Math.round(20 * dp));
        page.addView(subtitle);

        ScrollView scroll = new ScrollView(this);
        LinearLayout rows = new LinearLayout(this);
        rows.setOrientation(LinearLayout.VERTICAL);

        // Status Row (Toggle Limiter)
        statusRow = new Row(this, "Limiter", "When enabled, volume cannot exceed the set limit.", false,
                this::toggleEnabled, d -> toggleEnabled());
        rows.addView(statusRow);

        // Limit Setting Row (Adjust limit with Left/Right)
        limitRow = new Row(this, "Volume Limit", "Maximum volume allowed (1 to 100). ◀ ▶ adjusts limit.", true,
                null, this::stepLimit);
        rows.addView(limitRow);

        // Current TV Volume Row
        currentVolumeRow = new Row(this, "Current TV Volume", "Live volume reading from the TV media stream.", true,
                null, null);
        rows.addView(currentVolumeRow);

        // Quick Presets Container
        TextView presetsHeader = new TextView(this);
        presetsHeader.setText("Quick Presets");
        presetsHeader.setTextSize(15);
        presetsHeader.setTextColor(0xFFC9D1D9);
        presetsHeader.setPadding(Math.round(18 * dp), Math.round(16 * dp), 0, Math.round(8 * dp));
        rows.addView(presetsHeader);

        LinearLayout presetsLayout = new LinearLayout(this);
        presetsLayout.setOrientation(LinearLayout.HORIZONTAL);
        presetsLayout.setPadding(Math.round(18 * dp), 0, 0, Math.round(12 * dp));

        int[] presets = {5, 10, 15, 20, 25, 30};
        for (int p : presets) {
            Button btn = new Button(this);
            btn.setText(String.valueOf(p));
            btn.setTextSize(15);
            btn.setTextColor(0xFFFFFFFF);
            btn.setBackgroundResource(R.drawable.button_background);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(Math.round(64 * dp), Math.round(44 * dp));
            lp.rightMargin = Math.round(10 * dp);
            btn.setLayoutParams(lp);
            final int presetValue = p;
            btn.setOnClickListener(v -> setLimitValue(presetValue));
            presetsLayout.addView(btn);
        }
        rows.addView(presetsLayout);

        scroll.addView(rows);
        page.addView(scroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        // Footer Navigation Hint
        TextView hint = new TextView(this);
        hint.setText(R.string.hint_nav);
        hint.setTextSize(12);
        hint.setTextColor(0xFF8B949E);
        hint.setPadding(Math.round(18 * dp), Math.round(12 * dp), 0, 0);
        page.addView(hint);

        setContentView(page);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateValues();
        VolumeLimiterService.start(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(VolumeLimiterService.ACTION_VOLUME_UPDATED);
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(volumeUpdateReceiver, filter);
    }

    @Override
    protected void onPause() {
        try {
            unregisterReceiver(volumeUpdateReceiver);
        } catch (Exception ignored) {}
        super.onPause();
    }

    private void toggleEnabled() {
        boolean next = !Prefs.isEnabled(this);
        Prefs.setEnabled(this, next);
        updateValues();
    }

    private void stepLimit(int direction) {
        int current = Prefs.getLimit(this);
        setLimitValue(current + direction);
    }

    private void setLimitValue(int value) {
        Prefs.setLimit(this, value);
        updateValues();
    }

    private void updateValues() {
        boolean enabled = Prefs.isEnabled(this);
        int limit = Prefs.getLimit(this);

        int maxVol = 100;
        int currentVol = 0;
        if (audioManager != null) {
            try {
                maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            } catch (Exception ignored) {}
        }
        if (maxVol <= 0) maxVol = 100;

        statusRow.setValue(enabled ? "Enabled" : "Disabled");

        limitRow.setValue(String.valueOf(limit));
        limitRow.setBar((float) limit / (float) maxVol);

        currentVolumeRow.setValue(currentVol + " / " + maxVol);
        currentVolumeRow.setBar((float) currentVol / (float) maxVol);
    }
}
