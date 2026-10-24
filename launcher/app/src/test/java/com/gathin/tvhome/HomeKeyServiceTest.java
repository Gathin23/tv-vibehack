package com.gathin.tvhome;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HomeKeyServiceTest {
    private static final long SOON = 45_000L;

    @Test public void aTapIsAShortPress() {
        assertTrue(HomeKeyService.isShortPress(1_000, 1_120, 400));
    }

    @Test public void holdingPastTheTimeoutIsALongPress() {
        assertFalse(HomeKeyService.isShortPress(1_000, 1_400, 400));
        assertFalse(HomeKeyService.isShortPress(1_000, 3_000, 400));
    }

    @Test public void firstBindOfANewBootProbes() {
        assertTrue(HomeKeyService.firstBindAfterBoot(12, 11, SOON));
        assertTrue(HomeKeyService.firstBindAfterBoot(1, -1, SOON));
    }

    @Test public void aRebindInTheSameBootDoesNot() {
        assertFalse(HomeKeyService.firstBindAfterBoot(12, 12, SOON));
    }

    @Test public void aBindLongAfterBootDoesNot() {
        assertFalse(HomeKeyService.firstBindAfterBoot(12, 11, HomeKeyService.BOOT_WINDOW_MS));
    }

    @Test public void noBootCountMeansNoProbe() {
        assertFalse(HomeKeyService.firstBindAfterBoot(-1, -1, SOON));
    }

    private static final String TV = "com.mediatek.wwtv.tvcenter";
    private static final String TURNKEY = "com.mediatek.wwtv.tvcenter.nav.TurnkeyUiMainActivity";
    private static final String HOME = "com.gathin.tvhome.MainActivity";

    private static final long JUST_ARMED = 1_000L;

    private static int verdict(CharSequence pkg, CharSequence cls, long uptimeMs) {
        return HomeKeyService.bootVerdict(TV, TURNKEY, HOME, pkg, cls, uptimeMs, JUST_ARMED);
    }

    @Test public void theBootAppFirstIsSentHome() {
        assertEquals(HomeKeyService.BOOT_REDIRECT, verdict(TV, TURNKEY, SOON));
    }

    @Test public void homeFirstMeansALaterLiveTvIsTheUsers() {
        assertEquals(HomeKeyService.BOOT_DISARM, verdict("com.gathin.tvhome", HOME, SOON));
    }

    @Test public void otherWindowsWait() {
        assertEquals(HomeKeyService.BOOT_WAIT, verdict("com.gathin.tvhome", "com.gathin.tvhome.ProbeActivity", SOON));
        assertEquals(HomeKeyService.BOOT_WAIT, verdict(TV, "com.mediatek.wwtv.tvcenter.nav.inputpanel.InputsPanelActivity", SOON));
        assertEquals(HomeKeyService.BOOT_WAIT, verdict("android", "android.widget.Toast$TN", SOON));
        assertEquals(HomeKeyService.BOOT_WAIT, verdict(null, TURNKEY, SOON));
        assertEquals(HomeKeyService.BOOT_WAIT, verdict(TV, null, SOON));
    }

    @Test public void anotherAppWithTheSameClassNameIsNotTheBootApp() {
        assertEquals(HomeKeyService.BOOT_WAIT, verdict("com.example.other", TURNKEY, SOON));
    }

    @Test public void afterTheBootWindowItIsOver() {
        assertEquals(HomeKeyService.BOOT_DISARM, verdict(TV, TURNKEY, HomeKeyService.BOOT_WINDOW_MS));
    }

    @Test public void aLiveTvOrHdmiWindowLongAfterArmingIsTheUsers() {
        assertEquals(HomeKeyService.BOOT_DISARM, HomeKeyService.bootVerdict(TV, TURNKEY, HOME, TV, TURNKEY, SOON,
                HomeKeyService.BOOT_APP_ARM_MS));
        assertEquals(HomeKeyService.BOOT_REDIRECT, HomeKeyService.bootVerdict(TV, TURNKEY, HOME, TV, TURNKEY, SOON,
                HomeKeyService.BOOT_APP_ARM_MS - 1));
    }
}
