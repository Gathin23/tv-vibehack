package com.gathin.tvhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The launcher's settings: the values in res/values/config.xml, with an optional overlay read from
 * {@code config.json} in the app's external files directory
 * ({@code /sdcard/Android/data/com.gathin.tvhome/files/}). Every key in the JSON is optional and an
 * unknown or badly typed key is ignored, so a theme can change the dock and the look without a rebuild:
 * {@code tools/theme.sh themes/<name>} pushes a theme folder there and restarts Home.
 *
 * Read once in {@link MainActivity#onCreate}; nothing here touches the network.
 */
final class Config {
    private static final String TAG = "TvHome.Config";
    static final String FILE_NAME = "config.json";
    static final String WALLPAPER_NAME = "wallpaper.jpg";
    static final String SETTINGS_NAME = "launcher_settings";
    static final String ROWS_BELOW_DOCK_KEY = "rows_below_dock";
    static final String WALLPAPER_CHOICE_KEY = "wallpaper_choice";
    static final String DOCK_OPACITY_KEY = "dock_opacity";
    /**
     * Counts the changes made in Launcher settings. Home remembers the count it was built with and rebuilds on
     * resume when it differs, however the settings were left (see MainActivity#onResume).
     */
    static final String SETTINGS_REVISION_KEY = "settings_revision";

    /** True when Launcher settings changed something since Home was built at revision {@code seen}. */
    static boolean settingsChanged(long seen, long current) {
        return seen != current;
    }
    private static final String THEME_REVISION_NAME = "theme_revision";
    private static final String THEME_SIGNATURE_KEY = "theme_signature";

    final List<String> dockApps;
    /** Grid apps fill the dock slots dock_apps leaves free (config.xml, fill_dock); on by default, false leaves the slots empty. */
    final boolean fillDock;
    final List<String> hiddenApps;
    /** Packages pre-seeded into the App library's "added" set (config.xml, library_apps); empty by default. */
    final List<String> libraryApps;
    final String liveTvPackage;
    final String liveTvAction;
    final String liveTvInputId;
    /** Package to the action its tile sends instead of opening the start screen (config.xml, launch_actions). */
    final Map<String, String> launchActions;
    /** Package to the name its tile shows instead of the app's label (config.xml, app_labels). */
    final Map<String, String> appLabels;
    /** Package to a bundled banner_* drawable its tile shows instead of the app's art (config.xml, app_banners). */
    final Map<String, String> appBanners;
    final String sourcePanelService;
    final String sourcePanelAction;
    /** One int extra for the source panel, "name=value" (config.xml source_panel_extra); empty for none. */
    final String sourcePanelExtra;
    /** List Coocaa OS sources from com.skyworth.tv in the Inputs menu (config.xml coocaa_sources). */
    final boolean coocaaSources;
    final String inputAction;
    /** Scheme an Inputs row's passthrough URI is sent with instead of content:// (config.xml input_uri_scheme). */
    final String inputUriScheme;
    final int columns;
    final boolean animations;
    final boolean advancedEffects;
    final int rowsBelowDock;
    final boolean shamsiDate;
    /** Lunisolar date under the date (config.json lunar_date), computed for lunarUtcOffset hours east of UTC (-1: by locale). */
    final boolean lunarDate;
    final int lunarUtcOffset;
    /** Name after the greeting ("Good morning, Mr. Trịnh"); empty for the greeting alone. */
    final String greetingName;
    final boolean moduleCards;
    /** Calmer motion for weak GPUs (config.xml, reduced_motion): stiff spring, short fades, no replay. */
    final boolean reducedMotion;
    /** Open the Live TV app on the first Home after the TV starts (config.xml, boot_to_live_tv). */
    final boolean bootToLiveTv;
    /** Show the Open on start row in Launcher settings (config.xml, open_on_start_choice). */
    final boolean openOnStartChoice;
    /** The focused dock app's name in large type above the dock (config.xml, dock_title). */
    final boolean dockTitle;
    /** The dock as its own row: band, no names, parked low (config.xml dock_row). false: one plain grid. */
    final boolean dockRow;
    /** Inputs, Wi-Fi and Settings folded into one gear button with a dropdown, like Google TV (config.xml top_menu). */
    final boolean topMenu;
    /** Direct launcher-settings header action; off by default (launcher_settings_button). */
    final boolean launcherSettingsButton;
    /** Names under every tile, dimmed until focused (config.xml always_show_names); the dock band grows for them. */
    final boolean alwaysShowNames;
    /** Reorder button in the top bar (config.xml reorder_button); the Settings long-press menu offers it either way. */
    final boolean reorderButton;
    /** Show the Wi-Fi/Ethernet pill in the top bar (config.xml, show_network_pill). */
    final boolean showNetworkPill;
    /** Show the Inputs pill when the TV has inputs to list (config.xml, show_inputs_pill). */
    final boolean showInputsPill;
    /** Wi-Fi and Settings pills as round icon buttons, Inputs keeps its label (config.xml icon_only_buttons). */
    final boolean iconOnlyButtons;
    /** The Inputs pill as a round icon button too (config.xml icon_only_inputs). */
    final boolean iconOnlyInputs;
    /** App library as a round button in the top bar (config.xml library_button); needs app_library. */
    final boolean libraryButton;
    /** With icon_only_buttons, the Wi-Fi pill keeps the network name up to this width in dp (0: icon only). */
    final int wifiLabelWidth;
    /** Decode wallpapers in 32-bit colour instead of RGB_565 (config.xml wallpaper_true_color). */
    final boolean wallpaperTrueColor;
    /** The focused tile and the glass highlight swing past their target before settling (config.xml, focus_overshoot). */
    final boolean focusOvershoot;
    final String vpnApp;
    final boolean showHomeSettingsShortcut;
    /** One line under the clock: CPU, free memory, free storage. Off unless config or a theme turns it on. */
    final boolean showStats;
    /** App library pill after the last Home row (config.xml app_library). */
    final boolean appLibrary;
    /** OK directly toggles Home membership in the library (app_library_selection). */
    final boolean appLibrarySelection;
    /** Icon on a card tinted from that icon, instead of the app's banner (config.xml uniform_tiles). */
    final boolean uniformTiles;
    /** Background colour behind everything, #RRGGBB; black unless a theme says otherwise. */
    final int backgroundColor;
    /** Time-of-day backdrop, 0 to 100 (config.xml, sky_intensity); 0 keeps the plain background. */
    final int skyIntensity;
    /** Remote keycodes that open Settings through HomeKeyService (config.xml, settings_key_keycodes). */
    final int[] settingsKeycodes;
    /** Extra keycodes that open the input switcher in InputKeyService (config.xml input_key_keycodes). */
    final int[] inputKeycodes;
    /** Remote buttons that open an app, "keycode:package" or "KEYCODE_NAME:package" (config.xml app_keys). */
    final List<String> appKeys;
    /** Whether the key services request key filtering (config.xml or theme key_filter); false keeps key
     *  auto-repeat working on firmware whose AccessibilityInputFilter drops repeats (TCL Android 12). */
    final boolean keyFilter;
    /** With keyFilter on, the key services stop filtering while a game controller is connected (gamepad_passthrough). */
    final boolean gamepadPassthrough;
    /** With keyFilter on, the key services swallow Volume Up/Down and repeat a held one themselves (consume_volume_keys). */
    final boolean consumeVolumeKeys;
    /** Stock launcher whose home screen is sent back here if the firmware revives it (config.xml stock_home_redirect). */
    final String stockHomeRedirect;
    /** "package/activity" the firmware opens at power-on, sent back here once per boot (config.xml boot_app_redirect). */
    final String bootAppRedirect;
    /** The Settings button opens the quick settings panel instead of the TV's Settings (settings_key_panel). */
    final boolean settingsKeyPanel;
    /** Home puts the remote's accessibility helpers back after a force stop (restore_key_services; ServiceRepair). */
    final boolean restoreKeyServices;
    /** Whether themes may show a wallpaper (config.json, wallpaper). */
    final boolean wallpaperEnabled;
    /** Full-screen picture behind the launcher, or null for the plain colour. */
    final File wallpaper;
    /** Rotating theme images, or the single wallpaper when no collection is configured. */
    final List<File> wallpapers;
    final int wallpaperRotationMinutes;
    final int wallpaperDim;
    /** The dock band's opacity in percent (config.json dock_opacity, 30-100): below 100 the wallpaper shows through. */
    final int dockOpacity;
    /**
     * Playback order for a wallpaper collection: {@code sequential} or {@code shuffle}. Missing or
     * invalid values keep the historical shuffle so existing themes rotate exactly as before.
     */
    final String wallpaperOrder;
    /** Whether the overlay file was present and parsed. */
    final boolean overlayApplied;
    /** Festive skin from a theme (festival, festival_until, greeting, accent_color); {@link Festival#NONE} if none. */
    final Festival festival;
    /** F1 weekends from a theme's race_calendar (see RaceWeekend); empty when the theme has none. */
    final List<RaceWeekend.Race> raceCalendar;
    /** Cinematic top shelf (config.xml, cinematic_home): the focused dock app's artwork behind a larger shelf. */
    final boolean cinematicHome;
    /** The glow in the focused app's colour behind the dock (config.xml, ambient_glow); false keeps the background plain. */
    final boolean ambientGlow;
    /** The focus highlight in the focused app's colour on every tile (config.xml, app_glow), as cinematic_home does for the dock. */
    final boolean appGlow;
    /** The rows fade out under the clock instead of being cut off (config.xml, fade_rows); TopFade. */
    final boolean fadeRows;
    /** Minutes Home may sit untouched before the clock screensaver (config.xml, home_idle_clock_minutes); 0 is off. */
    final int homeIdleClockMinutes;
    /** The dock band as light frosted glass like the pills (config.xml, dock_glass); false keeps the solid band. */
    final boolean dockGlass;
    /** The dock band's padding equals the gap between its tiles (config.xml, dock_even_padding); false keeps the fixed padding. */
    final boolean dockEvenPadding;
    /** The dock position dots under the cinematic shelf's name (config.xml, cinematic_dots); false leaves them out. */
    final boolean cinematicDots;
    /** The cinematic shelf keeps the white focus highlight instead of the app's colour (config.xml, cinematic_white_focus). */
    final boolean cinematicWhiteFocus;
    /** The Wi-Fi pill's icon green while the TV is online (config.xml, wifi_status_color); false keeps it white. */
    final boolean wifiStatusColor;
    /** Opacity of the app's artwork behind the cinematic shelf, 0 to 100 (config.xml, cinematic_art); 50 as before. */
    final int cinematicArt;
    /** Strength of the cinematic shelf's dark scrim, 0 to 100 percent (config.xml, cinematic_scrim); 100 as before. */
    final int cinematicScrim;
    /** A rotating collection's groups by time of day, as file names (config.xml, wallpaper_moods); empty rotates through all of it. */
    final Map<String, Set<String>> wallpaperMoods;
    /** Shape of the glow behind the focused app (config.xml, glow_style): {@link #GLOW_RADIAL} or {@link #GLOW_HORIZON}. */
    final String glowStyle;
    /** The focus highlight's look (config.xml, glass_style): {@link #GLASS_DEFAULT}, {@link #GLASS_RIM} or {@link #GLASS_HALO}. */
    final String glassStyle;

    static final String GLOW_RADIAL = "radial", GLOW_HORIZON = "horizon";
    static final String GLASS_DEFAULT = "glass", GLASS_RIM = "rim", GLASS_HALO = "halo";
    /** Show each rotating wallpaper's name from wallpapers/captions.txt (config.xml / theme wallpaper_captions). */
    final boolean wallpaperCaptions;
    /** Answer VIEW_INPUTS with the Inputs menu (handle_view_inputs); see InputsRequestActivity. */
    final boolean handleViewInputs;
    /** Hold OK on a tile opens a menu (Move, Remove from Home, App info) instead of App info (config.xml / theme tile_menu). */
    final boolean tileMenu;
    /** With wifi_status_color, the icon orange while connected without internet (a captive portal) and red while offline (config.xml, wifi_status_warnings). */
    final boolean wifiStatusWarnings;
    /** Apps shown as round logo buttons in the top bar, left of the network pill, and no longer as tiles (config.xml, top_bar_apps). */
    final List<String> topBarApps;
    /** The greeting above the clock (Good evening); false hides it (config.xml, show_greeting). */
    final boolean showGreeting;
    /** The date and the line between it and the clock in the clock's full white instead of dimmed (config.xml, header_white). */
    final boolean headerWhite;
    /** The clock screensaver over Home's wallpaper instead of the black glow (config.xml, clock_wallpaper). */
    final boolean clockWallpaper;
    /** How dark the clock screensaver's wallpaper is, 0 to 90 % (config.xml, clock_wallpaper_dim). */
    final int clockWallpaperDim;
    /** The clock screensaver's coloured glow; false leaves the clock on plain black, e.g. for an OLED (config.xml, clock_glow). */
    final boolean clockGlow;
    /** Every app in the dock's row, which scrolls sideways, and no grid below it (config.xml, single_row); needs dock_row. */
    final boolean singleRow;
    /** With single_row, the band runs behind every app in the row, not only the dock apps (config.xml, dock_band_all). */
    final boolean dockBandAll;

    private Config(List<String> dockApps, boolean fillDock, List<String> hiddenApps, List<String> libraryApps, String liveTvPackage,
                   String liveTvAction, String liveTvInputId, Map<String, String> launchActions, Map<String, String> appLabels,
                   Map<String, String> appBanners, String sourcePanelService, String sourcePanelAction,
                   String sourcePanelExtra, boolean coocaaSources, String inputAction,
                   String inputUriScheme, int columns, int rowsBelowDock, boolean animations, boolean advancedEffects,
                   boolean shamsiDate, String greetingName, boolean moduleCards, boolean reducedMotion, boolean bootToLiveTv, boolean openOnStartChoice, boolean dockTitle, boolean dockRow, boolean topMenu, boolean launcherSettingsButton, boolean reorderButton, boolean showNetworkPill, boolean showInputsPill, boolean iconOnlyButtons, boolean iconOnlyInputs, boolean libraryButton, int wifiLabelWidth, boolean alwaysShowNames, boolean wallpaperTrueColor, boolean focusOvershoot, String vpnApp,
                   boolean showHomeSettingsShortcut, boolean showStats, boolean appLibrary, boolean appLibrarySelection, boolean uniformTiles, int backgroundColor, int skyIntensity,
                   int[] settingsKeycodes, boolean settingsKeyPanel, boolean restoreKeyServices, boolean wallpaperEnabled, File wallpaper, List<File> wallpapers,
                   int wallpaperRotationMinutes, int wallpaperDim, int dockOpacity, String wallpaperOrder, boolean overlayApplied,
                   Festival festival, int[] inputKeycodes, List<String> appKeys, String stockHomeRedirect, String bootAppRedirect,
                   List<RaceWeekend.Race> raceCalendar, boolean cinematicHome, boolean ambientGlow, boolean appGlow, boolean fadeRows, int homeIdleClockMinutes,
                   boolean keyFilter, boolean dockGlass, String glowStyle, boolean wallpaperCaptions,
                   boolean handleViewInputs, boolean tileMenu, boolean dockEvenPadding, boolean cinematicDots, boolean cinematicWhiteFocus, boolean wifiStatusColor, int cinematicScrim, int cinematicArt,
                   Map<String, Set<String>> wallpaperMoods, boolean gamepadPassthrough, boolean consumeVolumeKeys, boolean wifiStatusWarnings, List<String> topBarApps, boolean showGreeting, boolean headerWhite, boolean clockWallpaper, int clockWallpaperDim, boolean clockGlow, String glassStyle, boolean singleRow, boolean dockBandAll, boolean lunarDate, int lunarUtcOffset) {
        this.handleViewInputs = handleViewInputs;
        this.tileMenu = tileMenu;
        this.inputKeycodes = inputKeycodes;
        this.appKeys = Collections.unmodifiableList(appKeys);
        this.keyFilter = keyFilter;
        this.stockHomeRedirect = stockHomeRedirect;
        this.bootAppRedirect = bootAppRedirect;
        this.dockApps = Collections.unmodifiableList(dockApps);
        this.fillDock = fillDock;
        this.hiddenApps = Collections.unmodifiableList(hiddenApps);
        this.libraryApps = Collections.unmodifiableList(libraryApps);
        this.liveTvPackage = liveTvPackage;
        this.liveTvAction = liveTvAction;
        this.liveTvInputId = liveTvInputId;
        this.launchActions = Collections.unmodifiableMap(launchActions);
        this.appLabels = Collections.unmodifiableMap(appLabels);
        this.appBanners = Collections.unmodifiableMap(appBanners);
        this.sourcePanelService = sourcePanelService;
        this.sourcePanelAction = sourcePanelAction;
        this.sourcePanelExtra = sourcePanelExtra;
        this.coocaaSources = coocaaSources;
        this.inputAction = inputAction;
        this.inputUriScheme = inputUriScheme;
        this.columns = columns;
        this.rowsBelowDock = rowsBelowDock;
        this.animations = animations;
        this.advancedEffects = advancedEffects;
        this.shamsiDate = shamsiDate;
        this.lunarDate = lunarDate;
        this.lunarUtcOffset = lunarUtcOffset;
        this.greetingName = greetingName;
        this.moduleCards = moduleCards;
        this.reducedMotion = reducedMotion;
        this.bootToLiveTv = bootToLiveTv;
        this.openOnStartChoice = openOnStartChoice;
        this.dockTitle = dockTitle;
        this.dockRow = dockRow;
        this.topMenu = topMenu;
        this.launcherSettingsButton = launcherSettingsButton;
        this.reorderButton = reorderButton;
        this.showNetworkPill = showNetworkPill;
        this.showInputsPill = showInputsPill;
        this.iconOnlyButtons = iconOnlyButtons;
        this.iconOnlyInputs = iconOnlyInputs;
        this.libraryButton = libraryButton;
        this.wifiLabelWidth = wifiLabelWidth;
        this.alwaysShowNames = alwaysShowNames;
        this.wallpaperTrueColor = wallpaperTrueColor;
        this.focusOvershoot = focusOvershoot;
        this.vpnApp = vpnApp;
        this.showHomeSettingsShortcut = showHomeSettingsShortcut;
        this.showStats = showStats;
        this.appLibrary = appLibrary;
        this.appLibrarySelection = appLibrarySelection;
        this.uniformTiles = uniformTiles;
        this.backgroundColor = backgroundColor;
        this.skyIntensity = skyIntensity;
        this.settingsKeycodes = settingsKeycodes;
        this.settingsKeyPanel = settingsKeyPanel;
        this.restoreKeyServices = restoreKeyServices;
        this.wallpaperEnabled = wallpaperEnabled;
        this.wallpaper = wallpaper;
        this.wallpapers = Collections.unmodifiableList(wallpapers);
        this.wallpaperRotationMinutes = wallpaperRotationMinutes;
        this.wallpaperDim = wallpaperDim;
        this.dockOpacity = dockOpacity;
        this.wallpaperOrder = wallpaperOrder;
        this.overlayApplied = overlayApplied;
        this.festival = festival;
        this.raceCalendar = Collections.unmodifiableList(raceCalendar);
        this.cinematicHome = cinematicHome;
        this.ambientGlow = ambientGlow;
        this.appGlow = appGlow;
        this.fadeRows = fadeRows;
        this.homeIdleClockMinutes = homeIdleClockMinutes;
        this.dockGlass = dockGlass;
        this.dockEvenPadding = dockEvenPadding;
        this.cinematicDots = cinematicDots;
        this.cinematicWhiteFocus = cinematicWhiteFocus;
        this.wifiStatusColor = wifiStatusColor;
        this.cinematicArt = cinematicArt;
        this.cinematicScrim = cinematicScrim;
        this.wallpaperMoods = wallpaperMoods;
        this.gamepadPassthrough = gamepadPassthrough;
        this.consumeVolumeKeys = consumeVolumeKeys;
        this.wifiStatusWarnings = wifiStatusWarnings;
        this.topBarApps = Collections.unmodifiableList(topBarApps);
        this.showGreeting = showGreeting;
        this.headerWhite = headerWhite;
        this.clockWallpaper = clockWallpaper;
        this.clockWallpaperDim = clockWallpaperDim;
        this.clockGlow = clockGlow;
        this.singleRow = singleRow;
        this.dockBandAll = dockBandAll;
        this.glowStyle = glowStyle(glowStyle);
        this.wallpaperCaptions = wallpaperCaptions;
        this.glassStyle = glassStyle(glassStyle);
    }

    /** A known glow_style, or radial (the default) for anything else. */
    static String glowStyle(String value) {
        return value != null && GLOW_HORIZON.equalsIgnoreCase(value.trim()) ? GLOW_HORIZON : GLOW_RADIAL;
    }

    /** A known glass_style, or glass (the default) for anything else. */
    static String glassStyle(String value) {
        if (value == null) return GLASS_DEFAULT;
        String v = value.trim();
        if (GLASS_RIM.equalsIgnoreCase(v)) return GLASS_RIM;
        return GLASS_HALO.equalsIgnoreCase(v) ? GLASS_HALO : GLASS_DEFAULT;
    }

    /** The folder the theme tools push into; null when external storage is unavailable. */
    static File dir(Context ctx) {
        return ctx.getExternalFilesDir(null);
    }

    static Config load(Context ctx) {
        Resources res = ctx.getResources();
        List<String> dock = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.dock_apps)));
        boolean fillDock = res.getBoolean(R.bool.fill_dock);
        List<String> hidden = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.hidden_apps)));
        List<String> libraryApps = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.library_apps)));
        String liveTv = res.getString(R.string.live_tv_package).trim();
        String liveTvAction = res.getString(R.string.live_tv_action).trim();
        String liveTvInputId = res.getString(R.string.live_tv_input_id).trim();
        List<String> launchActions = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.launch_actions)));
        List<String> appLabels = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.app_labels)));
        List<String> appBanners = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.app_banners)));
        String panelService = res.getString(R.string.source_panel_service).trim();
        String panelAction = res.getString(R.string.source_panel_action).trim();
        String panelExtra = res.getString(R.string.source_panel_extra).trim();
        boolean coocaaSources = res.getBoolean(R.bool.coocaa_sources);
        String inputAction = res.getString(R.string.input_action).trim();
        String inputUriScheme = res.getString(R.string.input_uri_scheme).trim();
        int columns = res.getInteger(R.integer.columns);
        int rowsBelow = res.getInteger(R.integer.rows_below_dock);
        boolean animations = res.getBoolean(R.bool.animations);
        boolean advancedEffects = res.getBoolean(R.bool.advanced_effects);
        boolean shamsi = res.getBoolean(R.bool.shamsi_date);
        boolean lunar = res.getBoolean(R.bool.lunar_date);
        int lunarOffset = -1;
        String greetingName = res.getString(R.string.greeting_name).trim();
        boolean moduleCards = res.getBoolean(R.bool.module_cards);
        boolean reducedMotion = res.getBoolean(R.bool.reduced_motion);
        boolean bootToLiveTv = res.getBoolean(R.bool.boot_to_live_tv);
        boolean openOnStartChoice = res.getBoolean(R.bool.open_on_start_choice);
        boolean dockTitle = res.getBoolean(R.bool.dock_title);
        boolean dockRow = res.getBoolean(R.bool.dock_row);
        boolean topMenu = res.getBoolean(R.bool.top_menu);
        boolean launcherSettingsButton = res.getBoolean(R.bool.launcher_settings_button);
        boolean reorderButton = res.getBoolean(R.bool.reorder_button);
        boolean showNetworkPill = res.getBoolean(R.bool.show_network_pill);
        boolean showInputsPill = res.getBoolean(R.bool.show_inputs_pill);
        boolean iconOnlyButtons = res.getBoolean(R.bool.icon_only_buttons);
        boolean iconOnlyInputs = res.getBoolean(R.bool.icon_only_inputs);
        boolean libraryButton = res.getBoolean(R.bool.library_button);
        int wifiLabelWidth = res.getInteger(R.integer.wifi_label_width);
        boolean alwaysShowNames = res.getBoolean(R.bool.always_show_names);
        boolean wallpaperTrueColor = res.getBoolean(R.bool.wallpaper_true_color);
        boolean focusOvershoot = res.getBoolean(R.bool.focus_overshoot);
        boolean wallpaperCaptions = res.getBoolean(R.bool.wallpaper_captions);
        boolean tileMenu = res.getBoolean(R.bool.tile_menu);
        String vpn = res.getString(R.string.vpn_app).trim();
        boolean homeSettings = res.getBoolean(R.bool.show_home_settings_shortcut);
        boolean showStats = res.getBoolean(R.bool.show_stats);
        boolean library = res.getBoolean(R.bool.app_library);
        boolean librarySelection = res.getBoolean(R.bool.app_library_selection);
        boolean uniformTiles = res.getBoolean(R.bool.uniform_tiles);
        int background = 0xFF000000;
        int skyIntensity = res.getInteger(R.integer.sky_intensity);
        int[] settingsKeycodes = res.getIntArray(R.array.settings_key_keycodes);
        boolean settingsKeyPanel = res.getBoolean(R.bool.settings_key_panel);
        boolean restoreKeyServices = res.getBoolean(R.bool.restore_key_services);
        int[] inputKeycodes = res.getIntArray(R.array.input_key_keycodes);
        boolean handleViewInputs = res.getBoolean(R.bool.handle_view_inputs);
        List<String> appKeys = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.app_keys)));
        String stockHomeRedirect = res.getString(R.string.stock_home_redirect).trim();
        String bootAppRedirect = res.getString(R.string.boot_app_redirect).trim();
        boolean cinematic = res.getBoolean(R.bool.cinematic_home);
        boolean ambientGlow = res.getBoolean(R.bool.ambient_glow);
        boolean appGlow = res.getBoolean(R.bool.app_glow);
        boolean fadeRows = res.getBoolean(R.bool.fade_rows);
        int idleClockMinutes = res.getInteger(R.integer.home_idle_clock_minutes);
        boolean keyFilter = res.getBoolean(R.bool.key_filter);
        boolean gamepadPassthrough = res.getBoolean(R.bool.gamepad_passthrough);
        boolean consumeVolumeKeys = res.getBoolean(R.bool.consume_volume_keys);
        boolean dockGlass = res.getBoolean(R.bool.dock_glass);
        boolean dockEvenPadding = res.getBoolean(R.bool.dock_even_padding);
        boolean cinematicDots = res.getBoolean(R.bool.cinematic_dots);
        boolean cinematicWhiteFocus = res.getBoolean(R.bool.cinematic_white_focus);
        boolean wifiStatusColor = res.getBoolean(R.bool.wifi_status_color);
        int cinematicArt = res.getInteger(R.integer.cinematic_art);
        int cinematicScrim = res.getInteger(R.integer.cinematic_scrim);
        Map<String, Set<String>> wallpaperMoods = moods(res.getString(R.string.wallpaper_moods));
        String glowStyle = res.getString(R.string.glow_style);
        boolean wifiStatusWarnings = res.getBoolean(R.bool.wifi_status_warnings);
        List<String> topBarApps = new ArrayList<>(Arrays.asList(res.getStringArray(R.array.top_bar_apps)));
        boolean showGreeting = res.getBoolean(R.bool.show_greeting);
        boolean headerWhite = res.getBoolean(R.bool.header_white);
        boolean clockWallpaper = res.getBoolean(R.bool.clock_wallpaper);
        int clockWallpaperDim = res.getInteger(R.integer.clock_wallpaper_dim);
        boolean clockGlow = res.getBoolean(R.bool.clock_glow);
        String glassStyle = res.getString(R.string.glass_style);
        boolean singleRow = res.getBoolean(R.bool.single_row);
        boolean dockBandAll = res.getBoolean(R.bool.dock_band_all);
        boolean applied = false;

        File dir = dir(ctx);
        File file = dir != null ? new File(dir, FILE_NAME) : null;
        boolean mounted = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        JSONObject json = readOverlay(ctx, file, mounted);
        if (json != null) {
            applied = true;
            dock = stringList(json, "dock_apps", dock);
            fillDock = bool(json, "fill_dock", fillDock);
            hidden = stringList(json, "hidden_apps", hidden);
            libraryApps = stringList(json, "library_apps", libraryApps);
            liveTv = string(json, "live_tv_package", liveTv);
            liveTvAction = string(json, "live_tv_action", liveTvAction);
            liveTvInputId = string(json, "live_tv_input_id", liveTvInputId);
            launchActions = stringList(json, "launch_actions", launchActions);
            appLabels = stringList(json, "app_labels", appLabels);
            appBanners = stringList(json, "app_banners", appBanners);
            panelService = string(json, "source_panel_service", panelService);
            panelAction = string(json, "source_panel_action", panelAction);
            panelExtra = string(json, "source_panel_extra", panelExtra).trim();
            coocaaSources = bool(json, "coocaa_sources", coocaaSources);
            inputAction = string(json, "input_action", inputAction);
            inputUriScheme = string(json, "input_uri_scheme", inputUriScheme).trim();
            columns = integer(json, "columns", columns, 1, 8);
            rowsBelow = integer(json, "rows_below_dock", rowsBelow, 0, 3);
            animations = bool(json, "animations", animations);
            advancedEffects = bool(json, "advanced_effects", advancedEffects);
            shamsi = bool(json, "shamsi_date", shamsi);
            lunar = bool(json, "lunar_date", lunar);
            lunarOffset = integer(json, "lunar_utc_offset", lunarOffset, -1, 14);
            greetingName = string(json, "greeting_name", greetingName).trim();
            moduleCards = bool(json, "module_cards", moduleCards);
            reducedMotion = bool(json, "reduced_motion", reducedMotion);
            bootToLiveTv = bool(json, "boot_to_live_tv", bootToLiveTv);
            openOnStartChoice = bool(json, "open_on_start_choice", openOnStartChoice);
            dockTitle = bool(json, "dock_title", dockTitle);
            dockRow = bool(json, "dock_row", dockRow);
            topMenu = bool(json, "top_menu", topMenu);
            launcherSettingsButton = bool(json, "launcher_settings_button", launcherSettingsButton);
            reorderButton = bool(json, "reorder_button", reorderButton);
            showNetworkPill = bool(json, "show_network_pill", showNetworkPill);
            showInputsPill = bool(json, "show_inputs_pill", showInputsPill);
            iconOnlyButtons = bool(json, "icon_only_buttons", iconOnlyButtons);
            iconOnlyInputs = bool(json, "icon_only_inputs", iconOnlyInputs);
            libraryButton = bool(json, "library_button", libraryButton);
            wifiLabelWidth = integer(json, "wifi_label_width", wifiLabelWidth, 0, 300);
            alwaysShowNames = bool(json, "always_show_names", alwaysShowNames);
            wallpaperTrueColor = bool(json, "wallpaper_true_color", wallpaperTrueColor);
            focusOvershoot = bool(json, "focus_overshoot", focusOvershoot);
            wallpaperCaptions = bool(json, "wallpaper_captions", wallpaperCaptions);
            tileMenu = bool(json, "tile_menu", tileMenu);
            vpn = string(json, "vpn_app", vpn);
            homeSettings = bool(json, "show_home_settings_shortcut", homeSettings);
            showStats = bool(json, "show_stats", showStats);
            library = bool(json, "app_library", library);
            librarySelection = bool(json, "app_library_selection", librarySelection);
            uniformTiles = bool(json, "uniform_tiles", uniformTiles);
            background = color(json, "background_color", background);
            skyIntensity = integer(json, "sky_intensity", skyIntensity, 0, 100);
            settingsKeycodes = intArray(json, "settings_key_keycodes", settingsKeycodes);
            settingsKeyPanel = bool(json, "settings_key_panel", settingsKeyPanel);
            restoreKeyServices = bool(json, "restore_key_services", restoreKeyServices);
            inputKeycodes = intArray(json, "input_key_keycodes", inputKeycodes);
            handleViewInputs = bool(json, "handle_view_inputs", handleViewInputs);
            appKeys = stringList(json, "app_keys", appKeys);
            stockHomeRedirect = string(json, "stock_home_redirect", stockHomeRedirect);
            bootAppRedirect = string(json, "boot_app_redirect", bootAppRedirect).trim();
            cinematic = bool(json, "cinematic_home", cinematic);
            ambientGlow = bool(json, "ambient_glow", ambientGlow);
            appGlow = bool(json, "app_glow", appGlow);
            fadeRows = bool(json, "fade_rows", fadeRows);
            idleClockMinutes = integer(json, "home_idle_clock_minutes", idleClockMinutes, 0, 1440);
            keyFilter = bool(json, "key_filter", keyFilter);
            gamepadPassthrough = bool(json, "gamepad_passthrough", gamepadPassthrough);
            consumeVolumeKeys = bool(json, "consume_volume_keys", consumeVolumeKeys);
            dockGlass = bool(json, "dock_glass", dockGlass);
            dockEvenPadding = bool(json, "dock_even_padding", dockEvenPadding);
            cinematicDots = bool(json, "cinematic_dots", cinematicDots);
            cinematicWhiteFocus = bool(json, "cinematic_white_focus", cinematicWhiteFocus);
            wifiStatusColor = bool(json, "wifi_status_color", wifiStatusColor);
            cinematicArt = integer(json, "cinematic_art", cinematicArt, 0, 100);
            cinematicScrim = integer(json, "cinematic_scrim", cinematicScrim, 0, 100);
            JSONObject moods = json.optJSONObject("wallpaper_moods");
            if (moods != null) wallpaperMoods = moods(moods);
            wifiStatusWarnings = bool(json, "wifi_status_warnings", wifiStatusWarnings);
            topBarApps = stringList(json, "top_bar_apps", topBarApps);
            showGreeting = bool(json, "show_greeting", showGreeting);
            headerWhite = bool(json, "header_white", headerWhite);
            clockWallpaper = bool(json, "clock_wallpaper", clockWallpaper);
            clockWallpaperDim = integer(json, "clock_wallpaper_dim", clockWallpaperDim, 0, 90);
            clockGlow = bool(json, "clock_glow", clockGlow);
            singleRow = bool(json, "single_row", singleRow);
            dockBandAll = bool(json, "dock_band_all", dockBandAll);
            glowStyle = string(json, "glow_style", glowStyle);
            glassStyle = string(json, "glass_style", glassStyle);
        }
        // A top-bar app is moved there, not copied: it leaves the grid.
        for (String pkg : topBarApps) if (!hidden.contains(pkg)) hidden.add(pkg);
        // A theme push or reset invalidates choices made on the TV. On first use, remember the
        // current theme without clearing choices from an older launcher version.
        SharedPreferences choices = ctx.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE);
        // Unmounted storage looks like a theme reset; compare only once the file is readable.
        if (mounted) syncThemeChoices(choices, dir, file);
        int chosenColumns = choices.getInt("columns", columns);
        if (chosenColumns >= 4 && chosenColumns <= 7) columns = chosenColumns;
        int chosenRows = choices.getInt(ROWS_BELOW_DOCK_KEY, rowsBelow);
        if (chosenRows >= 0 && chosenRows <= 3) rowsBelow = chosenRows;
        animations = choices.getBoolean("animations", animations);
        advancedEffects = choices.getBoolean("advanced_effects", advancedEffects);
        File wallpaper = dir != null ? new File(dir, WALLPAPER_NAME) : null;
        if (wallpaper != null && !(wallpaper.isFile() && wallpaper.length() > 0)) wallpaper = null;
        // A theme can also switch the wallpaper off while the file is still there.
        boolean wallpaperEnabled = json == null || bool(json, "wallpaper", true);
        if (!wallpaperEnabled) wallpaper = null;
        int rotationMinutes = json == null ? 0 : integer(json, "wallpaper_rotation_minutes", 0, 0, 1440);
        int wallpaperDim = json == null ? 0 : integer(json, "wallpaper_dim", 0, 0, 90);
        int dockOpacity = json == null ? 100 : integer(json, "dock_opacity", 100, 30, 100);
        dockOpacity = chosenDockOpacity(choices.getInt(DOCK_OPACITY_KEY, -1), dockOpacity);
        String wallpaperOrder = json == null ? WallpaperRotation.ORDER_SHUFFLE
                : string(json, "wallpaper_order", WallpaperRotation.ORDER_SHUFFLE);
        // An unknown order must not silently change how an existing collection rotates.
        if (!WallpaperRotation.validOrder(wallpaperOrder)) wallpaperOrder = WallpaperRotation.ORDER_SHUFFLE;
        List<File> wallpapers = new ArrayList<>();
        if (rotationMinutes > 0 && dir != null && wallpaperEnabled) {
            wallpapers.addAll(WallpaperRotation.images(new File(dir, "wallpapers")));
        }
        if (wallpapers.isEmpty() && wallpaper != null) wallpapers.add(wallpaper);
        // An on-TV wallpaper pick overrides the theme: a filename from the wallpapers/
        // collection, or "none". Unset means the theme decides (off by default).
        String wallpaperChoice = choices.getString(WALLPAPER_CHOICE_KEY, null);
        List<File> chosen = WallpaperRotation.chosen(dir == null ? Collections.emptyList()
                : WallpaperRotation.images(new File(dir, "wallpapers")), wallpaper, wallpaperChoice);
        if (chosen != null) {
            wallpapers.clear();
            wallpapers.addAll(chosen);
            wallpaper = chosen.isEmpty() ? null : chosen.get(0);
            wallpaperEnabled = !chosen.isEmpty();
        }
        // A festive skin comes only from a theme; config.xml has no festival.
        Festival festival = json == null ? Festival.NONE : Festival.parse(string(json, "festival", ""),
                string(json, "festival_until", ""), string(json, "greeting", ""), string(json, "accent_color", ""));

        return new Config(dock, fillDock, hidden, libraryApps, liveTv, liveTvAction, liveTvInputId, packageMap(launchActions), packageMap(appLabels),
                packageMap(appBanners), panelService,
                panelAction, panelExtra, coocaaSources, inputAction, inputUriScheme,
                Math.max(1, columns), Math.max(0, rowsBelow), animations, advancedEffects,
                shamsi, greetingName, moduleCards, reducedMotion, bootToLiveTv, openOnStartChoice, dockTitle, dockRow, topMenu, launcherSettingsButton, reorderButton, showNetworkPill, showInputsPill, iconOnlyButtons, iconOnlyInputs, libraryButton, wifiLabelWidth, alwaysShowNames, wallpaperTrueColor, focusOvershoot, vpn, homeSettings, showStats, library, librarySelection, uniformTiles, background,
                Math.max(0, Math.min(100, skyIntensity)), settingsKeycodes, settingsKeyPanel, restoreKeyServices, wallpaperEnabled, wallpaper,
                wallpapers, rotationMinutes, wallpaperDim, dockOpacity, wallpaperOrder, applied,
                festival, inputKeycodes, appKeys, stockHomeRedirect, bootAppRedirect, json == null ? Collections.emptyList() : raceCalendar(json), cinematic, ambientGlow, appGlow, fadeRows,
                Math.max(0, idleClockMinutes), keyFilter, dockGlass, glowStyle, wallpaperCaptions, handleViewInputs, tileMenu, dockEvenPadding, cinematicDots, cinematicWhiteFocus, wifiStatusColor, cinematicScrim, cinematicArt, wallpaperMoods, gamepadPassthrough, consumeVolumeKeys, wifiStatusWarnings, topBarApps, showGreeting, headerWhite, clockWallpaper, Math.max(0, Math.min(90, clockWallpaperDim)), clockGlow, glassStyle, singleRow && dockRow, dockBandAll, lunar, lunarOffset);
    }

    /**
     * {@code "race_calendar": [{"round": 18, "name": "United States Grand Prix", "place": "Austin",
     * "sessions": [{"name": "FP1", "start": "2026-10-23T17:30:00Z"}, ...]}, ...]}. A race or session that does
     * not parse is left out on its own; the rest of the calendar still counts.
     */
    private static List<RaceWeekend.Race> raceCalendar(JSONObject json) {
        List<RaceWeekend.Race> races = new ArrayList<>();
        JSONArray list = json.optJSONArray("race_calendar");
        if (list == null) return races;
        for (int i = 0; i < list.length(); i++) {
            JSONObject r = list.optJSONObject(i);
            JSONArray sessions = r == null ? null : r.optJSONArray("sessions");
            if (sessions == null) continue;
            List<RaceWeekend.Session> parsed = new ArrayList<>();
            for (int j = 0; j < sessions.length(); j++) {
                JSONObject s = sessions.optJSONObject(j);
                if (s == null) continue;
                String name = s.optString("name", "").trim();
                try {
                    if (!name.isEmpty()) parsed.add(new RaceWeekend.Session(name, java.time.Instant.parse(s.optString("start", ""))));
                } catch (java.time.format.DateTimeParseException ignored) {
                    // a session without a readable start time cannot be counted down
                }
            }
            if (!parsed.isEmpty()) races.add(new RaceWeekend.Race(r.optInt("round", 0),
                    r.optString("name", "Grand Prix").trim(), r.optString("place", "").trim(), parsed));
        }
        return races;
    }

    private static void syncThemeChoices(SharedPreferences choices, File dir, File config) {
        File revision = dir == null ? null : new File(dir, THEME_REVISION_NAME);
        String token = revisionToken(revision);
        String signature = fileStamp(config) + ":" + token;
        String seen = choices.getString(THEME_SIGNATURE_KEY, null);
        if (seen == null) {
            // Existing preferences survive an app upgrade, unless the first launch follows an
            // explicit theme push or reset by the updated tools.
            SharedPreferences.Editor editor = choices.edit();
            if (!"absent".equals(token)) editor.remove("columns").remove("animations")
                    .remove("advanced_effects").remove(ROWS_BELOW_DOCK_KEY).remove(WALLPAPER_CHOICE_KEY)
                    .remove(DOCK_OPACITY_KEY);
            editor.putString(THEME_SIGNATURE_KEY, signature).commit();
        } else if (!seen.equals(signature)) {
            choices.edit().remove("columns").remove("animations").remove("advanced_effects")
                    .remove(ROWS_BELOW_DOCK_KEY).remove(WALLPAPER_CHOICE_KEY).remove(DOCK_OPACITY_KEY)
                    .putString(THEME_SIGNATURE_KEY, signature).commit();
        }
    }

    /** The dock band opacity chosen in Launcher settings, or the theme's when none (or an out-of-range one) is saved. */
    static int chosenDockOpacity(int chosen, int theme) {
        return chosen >= 30 && chosen <= 100 ? chosen : theme;
    }

    private static String fileStamp(File file) {
        return file != null && file.isFile() ? file.length() + "@" + file.lastModified() : "absent";
    }

    private static String revisionToken(File file) {
        if (file == null || !file.isFile() || file.length() > 64) return "absent";
        try (InputStream in = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            int n = in.read(bytes);
            return n > 0 ? new String(bytes, 0, n, StandardCharsets.US_ASCII) : "absent";
        } catch (Exception ignored) {
            return "absent";
        }
    }

    /** Reads and parses the overlay; any problem means "no overlay", never a crash on the home screen. */
    /**
     * The overlay, or before external storage is mounted, the copy kept from the last time it was read. Home can
     * start before the card is mounted (seen at every boot on a Xiaomi MiTV-MTEQ0), and without the copy it comes
     * up with the built-in dock order. A missing file on mounted storage still means no overlay.
     */
    private static JSONObject readOverlay(Context ctx, File file, boolean mounted) {
        File copy = new File(ctx.getFilesDir(), FILE_NAME);
        if (!mounted) return read(copy);
        JSONObject json = read(file);
        if (json == null) {
            copy.delete();
        } else {
            try (FileOutputStream out = new FileOutputStream(copy)) {
                out.write(json.toString().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                Log.w(TAG, "could not keep a copy of " + file + ": " + e.getMessage());
            }
        }
        return json;
    }

    private static JSONObject read(File file) {
        if (file == null || !file.isFile()) return null;
        if (file.length() > 64 * 1024) {
            Log.w(TAG, "ignoring " + file + ": larger than 64 KB");
            return null;
        }
        try (InputStream in = new FileInputStream(file)) {
            byte[] buf = new byte[(int) file.length()];
            int n = 0, r;
            while (n < buf.length && (r = in.read(buf, n, buf.length - n)) > 0) n += r;
            String text = new String(buf, 0, n, StandardCharsets.UTF_8);
            if (text.startsWith("\uFEFF")) text = text.substring(1);   // Windows editors add a BOM
            return new JSONObject(text);
        } catch (Exception e) {
            Log.w(TAG, "ignoring " + file + ": " + e.getMessage());
            return null;
        }
    }

    /** wallpaper_moods as JSON text (config.xml); empty or unreadable is no groups. */
    private static Map<String, Set<String>> moods(String text) {
        if (text.trim().isEmpty()) return Collections.emptyMap();
        try {
            return moods(new JSONObject(text));
        } catch (JSONException e) {
            return Collections.emptyMap();
        }
    }

    /** {"night": ["03-moon-lake.jpg", ...], ...}: each mood's picture names; other keys and names are left out. */
    private static Map<String, Set<String>> moods(JSONObject o) {
        Map<String, Set<String>> groups = new HashMap<>();
        for (String mood : WallpaperMood.ALL) {
            JSONArray names = o.optJSONArray(mood);
            if (names == null) continue;
            Set<String> group = new HashSet<>();
            for (int i = 0; i < names.length(); i++) {
                String name = names.optString(i, "").trim();
                if (ThemeFiles.collectionImage(name)) group.add(name);
            }
            if (!group.isEmpty()) groups.put(mood, Collections.unmodifiableSet(group));
        }
        return Collections.unmodifiableMap(groups);
    }

    private static String string(JSONObject o, String key, String fallback) {
        Object v = o.opt(key);
        return v instanceof String ? ((String) v).trim() : fallback;
    }

    private static boolean bool(JSONObject o, String key, boolean fallback) {
        Object v = o.opt(key);
        return v instanceof Boolean ? (Boolean) v : fallback;
    }

    private static int integer(JSONObject o, String key, int fallback, int min, int max) {
        Object v = o.opt(key);
        if (!(v instanceof Number)) return fallback;
        int i = ((Number) v).intValue();
        return i < min || i > max ? fallback : i;
    }

    private static int color(JSONObject o, String key, int fallback) {
        Object v = o.opt(key);
        if (!(v instanceof String)) return fallback;
        try {
            int c = Color.parseColor(((String) v).trim());
            return c | 0xFF000000;   // always opaque: the window behind is black anyway
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    /** Positive keycodes only (vendor remotes use codes in the thousands); a list with anything else in it is ignored as a whole. */
    private static int[] intArray(JSONObject o, String key, int[] fallback) {
        Object v = o.opt(key);
        if (!(v instanceof JSONArray)) return fallback;
        JSONArray a = (JSONArray) v;
        int[] out = new int[a.length()];
        for (int i = 0; i < a.length(); i++) {
            Object item = a.opt(i);
            if (!(item instanceof Number)) return fallback;
            out[i] = ((Number) item).intValue();
            if (out[i] < 1) return fallback;
        }
        return out;
    }

    /** "package=value" entries; an entry without both halves is skipped, and a later entry for a package wins. */
    static Map<String, String> packageMap(List<String> entries) {
        Map<String, String> out = new HashMap<>();
        for (String entry : entries) {
            int eq = entry.indexOf('=');
            if (eq <= 0) continue;
            String pkg = entry.substring(0, eq).trim(), value = entry.substring(eq + 1).trim();
            if (!pkg.isEmpty() && !value.isEmpty()) out.put(pkg, value);
        }
        return out;
    }

    private static List<String> stringList(JSONObject o, String key, List<String> fallback) {
        Object v = o.opt(key);
        if (!(v instanceof JSONArray)) return fallback;
        JSONArray a = (JSONArray) v;
        List<String> out = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) {
            Object item = a.opt(i);
            if (item instanceof String && !((String) item).trim().isEmpty()) out.add(((String) item).trim());
        }
        return out;
    }
}
