# Switches on a TV

Every new key a theme can set (the Keys table in [`themes/README.md`](../themes/README.md)) gets a
screenshot of what it does on a real TV, and this table is where they are listed: the repo and
[cleanyour.tv/addons#switches](https://cleanyour.tv/addons#switches) both show them from here. Most
keys from before this rule still have none; a screenshot for one of them is welcome.

## Adding a key

A new key needs a screenshot. Put it in `docs/switches/<key>.jpg` (an existing screenshot in the
repo that shows the key at work also counts) and add a row below:

- From a real TV, with the key set, as a JPEG or PNG, 16:9 and no wider than 1920 px (1280×720 is
  plenty), under 300 KB.
- Show where the key changes something you can see. For a key that maps a remote button, show the
  screen the button opens; for one that changes behaviour only, show the setting's effect, such as
  the panel, the menu or the app that comes up.
- Blur anything personal: Wi-Fi names, people, addresses.

`tests/test_switches.py` checks that every new key has a row here with an image that exists and
fits the rules; keys from before the rule sit on its wanted list until a screenshot arrives.

## Screenshots

| Key | Screenshot |
|---|---|
| `columns` | <img src="../themes/six-columns/screenshot.jpg" width="320" alt="columns on a TV"> |
| `glass_style` | <img src="switches/glass_style.jpg" width="320" alt="glass_style halo on a TV"> |
| `sky_intensity` | <img src="sky-sunset.jpg" width="320" alt="sky_intensity on a TV"> |
| `wallpaper_rotation_minutes` | <img src="../themes/scenes/screenshot.jpg" width="320" alt="wallpaper_rotation_minutes on a TV"> |
| `festival` | <img src="../themes/dussehra/screenshot.jpg" width="320" alt="festival on a TV"> |
| `settings_key_panel` | <img src="quick-settings-panel.jpg" width="320" alt="settings_key_panel on a TV"> |
| `home_idle_clock_minutes` | <img src="clock-screensaver.png" width="320" alt="home_idle_clock_minutes on a TV"> |
| `gamepad_passthrough` | <img src="switches/gamepad_passthrough.jpg" width="320" alt="gamepad_passthrough on a TV"> |
| `consume_volume_keys` | <img src="switches/consume_volume_keys.jpg" width="320" alt="consume_volume_keys on a TV"> |
| `wifi_status_warnings` | <img src="switches/wifi_status_warnings.jpg" width="320" alt="wifi_status_warnings on a TV"> |
| `top_bar_apps` | <img src="switches/top_bar_apps.jpg" width="320" alt="top_bar_apps on a TV"> |
| `show_greeting` | <img src="switches/show_greeting.jpg" width="320" alt="show_greeting on a TV"> |
| `header_white` | <img src="switches/header_white.jpg" width="320" alt="header_white on a TV"> |
| `clock_wallpaper` | <img src="switches/clock_wallpaper.jpg" width="320" alt="clock_wallpaper on a TV"> |
| `clock_wallpaper_dim` | <img src="switches/clock_wallpaper.jpg" width="320" alt="clock_wallpaper_dim on a TV"> |
| `clock_glow` | <img src="switches/clock_glow.jpg" width="320" alt="clock_glow on a TV"> |
| `open_on_start_choice` | <img src="switches/open_on_start_choice.jpg" width="320" alt="open_on_start_choice on a TV"> |
| `single_row` | <img src="switches/single_row.jpg" width="320" alt="single_row on a TV"> |
| `dock_band_all` | <img src="switches/single_row.jpg" width="320" alt="dock_band_all on a TV"> |
| `uniform_tiles` | <img src="switches/uniform_tiles.jpg" width="320" alt="uniform_tiles on a TV"> |
| `lunar_date` | <img src="switches/lunar_date.jpg" width="320" alt="lunar_date on a TV"> |
| `boot_app_redirect` | <img src="switches/boot_app_redirect.jpg" width="320" alt="boot_app_redirect on a TV"> |
