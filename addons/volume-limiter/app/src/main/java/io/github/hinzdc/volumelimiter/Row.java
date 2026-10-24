package io.github.hinzdc.volumelimiter;

import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Settings row designed for TV D-pad navigation:
 * - OK key triggers onClick
 * - Left/Right keys trigger onStep (-1 or +1)
 * - Optional progress bar to visualize the current value
 */
final class Row extends LinearLayout {
    interface Step {
        void step(int direction);
    }

    private final TextView value;
    private final TextView desc;
    private final View fill;
    private final View rest;

    Row(Context context, String name, String description, boolean hasBar, Runnable onClick, Step onStep) {
        super(context);
        float dp = context.getResources().getDisplayMetrics().density;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int ph = Math.round(18 * dp);
        int pv = Math.round(12 * dp);
        setPadding(ph, pv, ph, pv);
        setBackgroundResource(R.drawable.row_background);

        boolean active = onClick != null || onStep != null;
        setFocusable(active);
        setClickable(onClick != null);
        if (onClick != null) {
            setOnClickListener(v -> onClick.run());
        }

        if (onStep != null) {
            setOnKeyListener((v, code, e) -> {
                if (code != KeyEvent.KEYCODE_DPAD_LEFT && code != KeyEvent.KEYCODE_DPAD_RIGHT) {
                    return false;
                }
                if (e.getAction() == KeyEvent.ACTION_DOWN) {
                    onStep.step(code == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1);
                }
                return true;
            });
        }

        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(VERTICAL);
        TextView label = new TextView(context);
        label.setText(name);
        label.setTextSize(17);
        label.setTextColor(0xFFFFFFFF);
        textLayout.addView(label);

        desc = new TextView(context);
        desc.setTextSize(12);
        desc.setTextColor(0xFF8B949E);
        desc.setPadding(0, Math.round(3 * dp), Math.round(16 * dp), 0);
        textLayout.addView(desc);
        setDetail(description);
        addView(textLayout, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));

        if (hasBar) {
            LinearLayout track = new LinearLayout(context);
            track.setOrientation(HORIZONTAL);
            track.setBackgroundColor(0x33FFFFFF);

            fill = new View(context);
            fill.setBackgroundColor(0xFF34C759);

            rest = new View(context);
            track.addView(fill, new LayoutParams(0, LayoutParams.MATCH_PARENT, 0f));
            track.addView(rest, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));

            FrameLayout holder = new FrameLayout(context);
            holder.addView(track, new FrameLayout.LayoutParams(Math.round(200 * dp), Math.round(6 * dp), Gravity.CENTER_VERTICAL));
            LayoutParams hp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            hp.rightMargin = Math.round(16 * dp);
            addView(holder, hp);
        } else {
            fill = null;
            rest = null;
        }

        value = new TextView(context);
        value.setTextSize(17);
        value.setTextColor(0xFFC9D1D9);
        value.setGravity(Gravity.END);
        value.setMinWidth(Math.round(80 * dp));
        addView(value, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    void setDetail(String d) {
        desc.setText(d == null ? "" : d);
        desc.setVisibility(d == null || d.isEmpty() ? GONE : VISIBLE);
    }

    void setValue(String v) {
        value.setText(v);
    }

    void setBar(float fraction) {
        if (fill == null || rest == null) return;
        float f = Math.max(0f, Math.min(1f, fraction));
        ((LayoutParams) fill.getLayoutParams()).weight = f;
        ((LayoutParams) rest.getLayoutParams()).weight = 1f - f;
        fill.requestLayout();
    }
}
