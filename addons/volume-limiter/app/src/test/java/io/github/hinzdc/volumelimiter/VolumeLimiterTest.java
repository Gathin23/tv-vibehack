package io.github.hinzdc.volumelimiter;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class VolumeLimiterTest {

    @Test
    public void testClampWhenExceedingLimit() {
        // If volume exceeds limit, it must be capped at the limit
        int current = 15;
        int limit = 10;
        int result = Prefs.clamp(current, limit, true);
        assertEquals("Volume exceeding limit must be capped at limit", 10, result);
    }

    @Test
    public void testClampWhenBelowLimit() {
        // If volume is below limit, it must remain unaffected
        int current = 7;
        int limit = 10;
        int result = Prefs.clamp(current, limit, true);
        assertEquals("Volume below limit must remain unaffected", 7, result);
    }

    @Test
    public void testClampWhenExactlyAtLimit() {
        int current = 10;
        int limit = 10;
        int result = Prefs.clamp(current, limit, true);
        assertEquals("Volume exactly at limit must remain at limit", 10, result);
    }

    @Test
    public void testClampWhenDisabled() {
        // When limiter is disabled, volume is never clamped
        int current = 25;
        int limit = 10;
        int result = Prefs.clamp(current, limit, false);
        assertEquals("When disabled, volume must not be altered", 25, result);
    }
}
