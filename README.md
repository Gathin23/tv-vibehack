<p align="center">
  <a href="https://cleanyour.tv"><img src="https://raw.githubusercontent.com/Gathin23/cleanyourtv/main/docs/banner.png" alt="CleanYourTV. Your TV, minus the ads. Strip the bloatware from any Android TV or Google TV over Wi-Fi and replace the ad-filled home screen with a clean launcher that looks like an Apple TV. No root, no cable, nothing uninstalled, and a one-line restore." width="800"></a>
</p>

<p align="center">
  <a href="https://github.com/Gathin23/cleanyourtv/stargazers"><img src="https://img.shields.io/github/stars/Gathin23/cleanyourtv?style=flat&cacheSeconds=1800&color=cfffcf&labelColor=15181e" alt="Stars"></a>
  <a href="https://github.com/Gathin23/cleanyourtv/pulls?q=is%3Amerged"><img src="https://img.shields.io/github/issues-search?query=repo%3AGathin23%2Fcleanyourtv%20is%3Apr%20is%3Amerged&style=flat&cacheSeconds=1800&label=merged%20PRs&color=cfffcf&labelColor=15181e" alt="Merged PRs"></a>
  <a href="https://github.com/Gathin23/cleanyourtv/network/members"><img src="https://img.shields.io/github/forks/Gathin23/cleanyourtv?style=flat&cacheSeconds=1800&color=cfffcf&labelColor=15181e" alt="Forks"></a>
  <a href="https://github.com/Gathin23/cleanyourtv/graphs/contributors"><img src="https://img.shields.io/github/contributors/Gathin23/cleanyourtv?style=flat&cacheSeconds=1800&color=cfffcf&labelColor=15181e" alt="Contributors"></a>
  <a href="README.zh-CN.md"><img src="https://img.shields.io/badge/README-%E7%AE%80%E4%BD%93%E4%B8%AD%E6%96%87-cfffcf?style=flat&labelColor=15181e" alt="简体中文"></a>
  <a href="README.vi.md"><img src="https://img.shields.io/badge/README-Ti%E1%BA%BFng%20Vi%E1%BB%87t-cfffcf?style=flat&labelColor=15181e" alt="Tiếng Việt"></a>
  <a href="README.ja.md"><img src="https://img.shields.io/badge/README-%E6%97%A5%E6%9C%AC%E8%AA%9E-cfffcf?style=flat&labelColor=15181e" alt="日本語"></a>
  <a href="https://buymeacoffee.com/gathint"><img src="https://img.shields.io/badge/support-%E2%9D%A4-ff5e8a?style=flat&labelColor=15181e" alt="Support, buy me a coffee"></a>
  <a href="https://www.producthunt.com/products/cleanyourtv"><img src="https://img.shields.io/badge/Product%20Hunt-upvote%20%E2%96%B2-da552f?style=flat&logo=producthunt&logoColor=white&labelColor=15181e" alt="CleanYourTV on Product Hunt"></a>
</p>

<p align="center">
  <a href="https://trendshift.io/repositories/310225"><img src="docs/trending-badge.png" alt="#1 trending repo of the week, via trendshift.io" width="360"></a>
</p>

<p align="center">
  <a href="docs/PRESS.md"><img src="docs/card.jpg" alt="280 TV models · 81 brands · 25 languages · 500 KB launcher. Featured in TechCrunch, TechRadar, Tom's Guide, Mashable, Android Authority, XDA, FlatpanelsHD, It's FOSS, Phandroid, GIGAZINE, Xataka, WinFuture, Frandroid, Macitynet, Techaeris, TechPP and 180 more: 196 articles from 32 countries. Before: stock Google TV home with a sponsored ad. After: the Clean Your TV launcher." width="800"></a>
</p>

**The viral tweet:** [x.com/gathint/status/2111379569030939102](https://x.com/gathint/status/2111379569030939102)

On 17 October 2026 I posted a before and after of my parents' TV, cleaned up by Claude Code over Wi-Fi.
It reached 7.4M views, people started doing the same to their own TVs, and their replies turned into
the lists here: 170+ TV models from 200+ contributors in four days. The repo was called tv-vibehack
until the 19th; old links work.

**Themes, switches and add-ons:** [cleanyour.tv/addons](https://cleanyour.tv/addons)

**Official links:** the website is [cleanyour.tv](https://cleanyour.tv) and the code, launcher and
tools live only in this repo. Other domains using the name, such as cleanyourtv.com, are not run by
this project ([#1004](https://github.com/Gathin23/cleanyourtv/issues/1004)).

## How it works

Android TVs have the same debug bridge as Android phones. Switch on USB debugging in the TV's
developer options, allow your computer once, and the rest is standard Android tooling:

| Step | What happens |
|---|---|
| **Back&nbsp;up** | The list of installed packages and settings. |
| **Disable** | The manufacturer's telemetry, ad services, content rows and preloaded junk, with `pm disable-user`. Nothing is uninstalled, so a restore script can undo every step. |
| **Install** | A small native launcher, made the default home screen. |

**No root, no cable, nothing uninstalled, and a one-line restore if you change your mind.**

The bloat differs by brand, so the prompt has your coding assistant discover it on your TV and ask
before touching anything you might use.

## Quick start

| You have | Do this |
|---|---|
| **A computer** | Use the prompt with a coding assistant: the three steps below. |
| **Only an Android phone** | Use the community-built [Clean Your TV app](https://cleanyour.tv/app) by [@udhaykumarbala](https://github.com/udhaykumarbala): connect, analyse, clean, in about five minutes. No laptop, no coding agent. |

With a computer, you need it on the same Wi-Fi as the TV and a coding assistant with shell access.

Any of these work: Claude Code (on [0G Private Computer](https://pc.0g.ai) or an [Anthropic plan](https://claude.com/pricing)), [Codex](https://github.com/openai/codex) or [Gemini CLI](https://github.com/google-gemini/gemini-cli).

| Where | What to do |
|---|---|
| **1. On the TV** | Settings → Device Preferences → About → press OK on **Build** seven times → back → Developer options → **USB debugging** on. If you see a Network or ADB debugging toggle, turn that on too. Keep the TV on. |
| **2. On your computer** | Paste [`PROMPT.md`](PROMPT.md) into your assistant and fill in your TV model. |
| **3. Follow along** | Accept "Allow USB debugging?" on the TV, then answer the checklist: which apps you use, the dock size and app order, whether to keep the voice assistant, Chromecast and updates. |

The assistant backs up, debloats, builds and installs the launcher, and checks each step with
screenshots pulled from the TV. With this repo as context it takes about fifteen minutes.

## What you get

A home screen that looks like an Apple TV and gets out of the way:

| Part | What it looks like |
|---|---|
| **A pitch-black home** | A dock of your four favourite apps in a rounded band, where Apple TV puts it. |
| **A top shelf** | Names the focused app in large type, with an ambient glow in a colour taken from its artwork. |
| **The rest of your apps in a grid** | Slides up when you press Down. |
| **One floating glass highlight** | Springs from tile to tile. The focused tile lifts and shows its name. |
| **Three frosted buttons** | Inputs (HDMI ports under the names you gave them in the TV's settings, AV, Live TV), Wi-Fi or Ethernet with the network name, Settings. |
| **A greeting, a thin clock and the date** | In [Outfit](https://fonts.google.com/specimen/Outfit), and a matching Clock screensaver. Set `show_stats` in config or a theme for one line under the clock: CPU, free memory and free storage. It stays off unless you turn it on, and it refreshes only while Home is on screen. |
| **Themes and module cards** | Themes change colours, wallpaper, dock and columns without a rebuild, and module cards let other apps plug a tile in, only with your say-so. |

And a TV that stops working against you: no sponsored rows, no "Discover" nags, no analytics
phoning home, no apps you never asked for.

| | |
|---|---|
| ![The top shelf names the focused app over an ambient glow in its colour](docs/focus-2-up-netflix.png) | ![The Inputs popup: HDMI ports with what is connected, AV and Live TV](docs/inputs-menu.jpg) |
| *Top shelf and ambient glow* | *Inputs: HDMI with connection state, AV, Live TV* |
| ![The grid with the glass highlight on a tile and its name beneath](docs/focus-1-fancode.png) | ![The Clock screensaver: a thin clock drifting over the glow](docs/clock-screensaver.png) |
| *The grid, one glass highlight, the tile's name* | *Clock screensaver, drifts so nothing burns in* |
| ![The aurora theme: a wallpaper and tinted pills, pushed without a rebuild](themes/aurora/screenshot.jpg) | ![Six columns and a six-app dock from a theme's config.json](themes/six-columns/screenshot.jpg) |
| *Aurora theme, pushed with `tools/theme.sh`* | *Six columns and a six-app dock, same launcher* |
| ![Moving a tile: held OK, chose Move, the tile is picked up and the others dim](docs/tile-move.jpg) | ![The App library: installed apps, one press adds an app back to Home](docs/app-library.jpg) |
| *With `tile_menu`, hold OK on a tile, Move: arrows carry it, Back puts it down* | *App library: one press puts a removed app back on Home* |

## Supported TVs

Anything running **Android TV** or **Google TV**.

| Platform | What works |
|---|---|
| **Android TV, Google TV** | The debloat and the launcher. |
| **Fire TV** | The debloat half only, since Fire OS will not hand Home to a third-party launcher. And on the 2015 Fire TV Box (Fire OS 5) even that half fails: every `pm` route is refused. |
| **A few China-market TCL sets** | They refuse to install any launcher at all; their lists say so in the header. |
| **Samsung (Tizen), LG (webOS)** | Different systems. See [SHOWCASE.md](SHOWCASE.md#other-platforms-fire-tv-and-lg-webos) for three webOS ports, and [docs/devices/samsung-tizen.md](docs/devices/samsung-tizen.md) for what a 2021 Samsung set answers over Wi-Fi (no ADB, so no `pm disable-user`) if you are weighing a Tizen port. |

Tested lists from contributors, one folder per model in [`examples/`](examples/):

<!-- tv-brands:start: generated by tools/gen_tables.py from examples/*/tv.json; edit those, not this -->

**TCL** 62 · **Sony** 34 · **Xiaomi** 30 · **Philips** 15 · **Hisense** 10 · **Skyworth** 9 · **Amazon Fire TV** 7 ·
**OnePlus** 4 · **Panasonic** 4 · **Thomson** 4 · **BenQ** 3 · **Google** 3 · **Haier** 3 · **SHARP** 3 · **Vu** 3 ·
**CHiQ** 2 · **FFalcon** 2 · **Hitachi** 2 · **JVC** 2 · **Kalley** 2 · **Motorola** 2 · **Nokia** 2 ·
**NVIDIA Shield** 2 · **onn** 2 · **RCA** 2 · **Redmi** 2 · **Sankey** 2 · **Toshiba** 2 ·
Acer · AiPro2K · AIWA · AQUA · Artel · Beko · Caxium · Cecotec · Challenger · Compac · Dcolor · Dialog · EKO · Freebox ·
Grundig · INCLO · Insignia Fire TV · Karbonn · Kogan · KTC · Liberton · Lumio · Master-G · MTC · Noblex · OK. · PEAQ ·
Polytron · PRISM+ · RockTek · SANYO · Sber · Super General · Tele System · TiVo · Tornado · Turkcell · Tuvio · Verizon ·
Vidda · Vision ·
Anker Nebula, BYINTEK, Epson, Formovie, Gadnic, HOTACK, JMGO, Magcubic, Portronics, Stellamore, Wanbo and XGIMI projectors

<!-- tv-brands:end -->

<details>
<summary><strong>The full list</strong></summary>

<!-- tv-table:start: generated by tools/gen_tables.py from examples/*/tv.json; edit those, not this -->

| TV | OS |
|---|---|
| Acer AR55AR2851UDPRO 55" (SCBC R4, Realtek rtd2851m), India | Android TV 14 |
| AiPro2K (Expressluck, dongnae; Changhong AI PONT family) | Android TV 14 |
| AIWA AI PONT SA (Changhong, osaka, ISDB86AW) | Google TV 14 |
| Amazon Fire TV 50" 4K smart TV (AFTTIFF43, brandenburg) | Fire OS 7.7.1.7 / Android 9 |
| Amazon Fire TV smart TV (AFTEU014, greta, MediaTek m7322), Germany | Fire OS 7.6.8.5 / Android 9 |
| Amazon Fire TV Stick 4K Max, 1st gen 2021 (AFTKA, kara), Colombia | Fire OS 7.7.1.3 / Android 9 |
| Amazon Fire TV Stick 4K, 1st gen 2018 (AFTMM, mantis) | Fire OS 6.7.1.1 / Android 7.1 |
| Amazon Fire TV Stick 4K, 2nd gen 2023 (AFTKM, karat), Brazil | Fire OS 8.1.8.5 / Android 11 |
| Amazon Fire TV Stick Lite, 1st gen 2020 (AFTSS, sheldon) | Fire OS 7.7.1.7 / Android 9 |
| Amazon Fire TV, AFTDEC012E (ginza), Android 11 | Fire OS / Android 11 |
| Anker Nebula Cosmos projector D2140 (D2140-Cosmos, SDMC/Amlogic, 2 GB), Vietnam | Android TV 9 |
| Anker Nebula Mars 3 Air projector D2325 (Nebula_Mars_2_V2, taihang, MediaTek m7332), Portugal | Google TV 14 |
| Anker Nebula projector D2426 (D2426_CapsuleIIIPro, baekdu, SDMC/MediaTek m7632, 1.7 GB), Brazil | Android TV 14 |
| AQUA MatrixTV EE 50" 4K (Haier, AQ9676EU, omonia, MediaTek), Indonesia | Google TV 14 |
| Artel smart TV (CVTE TP.MT5510S.PB782, cvt_mt5863_eu, SMART_TV), Uzbekistan | Android 7.0 |
| Beko B43B880B (Arçelik, sinchon, MediaTek), Turkey | Android TV 11 |
| BenQ E55-735 (Changhong AI PONT, DVB96BQ) | Google TV 14 |
| BenQ E55-750 (Changhong AI PONT, DVB96BQ) | Google TV (Android 11) |
| BenQ GV32 portable projector (SDMC, BenQ_GV01, himalaya, MediaTek m7332) | Google TV 14 |
| BYINTEK Love U4 projector, 1080P (Skyworth RG_HS352, HiSilicon Hi3751V350, 1 GB), Brazil | Android 9 |
| BYINTEK T5 Max projector (built-in Google TV stick: SkyworthDigital YYT, Amlogic), Brazil | Google TV 14 |
| Caxium 58" 147 cm UHD Smart TV (Realtek RTD285O, RTK2851P, TopTech + Zeasn), Colombia | Android TV 9 |
| Cecotec VQU10065S 65" QLED (Vestel-built, shandao, MediaTek), Spain | Android TV 14 |
| Challenger 32T22 BT (cvt_mt5863_col, board TP.MT5510.PB781, MediaTek MT5659, 1 GB RAM), Colombia | Android 7.0 |
| CHiQ AI PONT (Changhong GTVU65, dangni, MediaTek mt5867) | Google TV 14 |
| CHiQ AI PONT (DVB93CQ) | Android TV 11 |
| Compac ATV R2 (SCBC R2, Realtek RTD285O) | Android TV 9 |
| Dcolor GD2 (YHT) | Android TV 14 (SDK 34) |
| Dialog ViU (SEI Robotics SEI103DL, Amlogic, NES middleware), Sri Lanka | Android TV 9 |
| EKO 2K Google TV (MTC-built, tennoji, MediaTek mt5867 / T56) | Google TV 14 |
| Epson LS800 laser projector (Askey HA90 module, sti6202d101, Amlogic) | Android TV 14 |
| FFalcon (雷鸟) r34a0, China (V8-A920T01-LF1V147, Amlogic T920), 1 GB | Android 5.1.1 |
| FFalcon (雷鸟) 鹤6 PRO 65S585C Pro (China, tcl_mt5879_cn, MediaTek MT9653) | Android TV 11 |
| Formovie / Fengmi projector MiProjM05 (eva, Amlogic) | Android TV 9 |
| Formovie Laser 4K projector (hanlla, MediaTek T31 / m7632, 1.8 GB) | Android TV 14 |
| Freebox Player POP (Free, fbx8am, Amlogic), IPTV/DVB-T box | Android TV 10 |
| Gadnic PROJ100W projector (built-in Google TV stick: SkyworthDigital YYT, Amlogic HP46B), Argentina | Google TV 14 |
| Google Chromecast with Google TV 4K (sabrina) | Google TV 14 |
| Google Chromecast with Google TV HD (boreal) | Google TV 14 |
| Google TV Streamer 4K (kirkwood, GZRNL) | Google TV 14 |
| Grundig Android UHD TV (Arçelik, GxGrNM, kunyang, MediaTek), Turkey | Android TV 14 |
| Haier Android TV DVB2K (broadway, HaierATVT56DVB) | Android TV 11 |
| Haier H32S80EFX (MatrixTV CE, HRT56EU, deto, MediaTek), Thailand | Google TV 14 |
| Haier MatrixTV GE (HR9216EU, haeundae, MediaTek), EU | Google TV 14 |
| Hisense 65D62QH (VIDAA_TV, MediaTek MT9653), China | Android TV 11 |
| Hisense E5Q Pro (VIDAA_TV, MediaTek MT9653), China | Android TV 11 |
| Hisense Fire TV (AFTCIBFBD1, coyote, MediaTek), US | Fire OS 8.0 / Android 11 |
| Hisense HG9 (HiSmart 4K A4FFM) | Android TV 9 |
| Hisense HiSmartTV A4 (xiaoyushan, MStar m7322), Android 11 firmware RTT2.221108.001 | Android TV 11 |
| Hisense HiSmartTV A4 (xiaoyushan, MStar m7322), India | Android TV 9 |
| Hisense SmartTV 4K FFM (huangshan, MediaTek), Latin America; also the US Hisense U9DG | Android TV 10 |
| Hisense SmartTV 4K FFM (lushan, MediaTek MT9676), Mongolia | Google TV 12 |
| Hisense SmartTV 4K FFM (songshan, MediaTek m7632) | Android 11 (SDK 30) |
| Hisense U75 / 75U75N (hengshan, MediaTek), Latin America and US | Google TV 12 |
| Hitachi 65QLEDSM20 (MTC, bangbae, MediaTek m7322), Australia | Android TV 11 |
| Hitachi CDH-LE32SMART21 (TCL-built C06, C06_2K_GB, MediaTek MT9221, 1 GB), Argentina | Android TV 11 |
| HOTACK SMART2KPJT projector, "Whale TV 95C8" (Amlogic am30b8, WhaleOS S8E, Zeasn firmware) | Android 13 |
| INCLO Google TV 4K (KTC, Realtek, khardi, KTC_INCLO_4K_EU_23GV1), Taiwan | Google TV 11 |
| Insignia 55" F30 4K Fire TV (NS-55F301NA25, AFTDCT31, duckie) | Fire OS 7 / Android 9 |
| JMGO N1S projector (K80A, holan, MediaTek m7332), Australia | Google TV 14 |
| JVC Android TV (Vestel-built, BARTU_Jvc, menlo, MediaTek mt5867), Spain | Android TV 11 |
| JVC UK 4K Android TV (MTC, MTCMSD6886UK, sadang, MediaTek m7322), UK | Android TV 11 |
| Kalley 43G200 (device R3_GTV, MOKA, Realtek RTD2841A), Latin America | Google TV 14 |
| Kalley R10G (device R10G, product ACER_FFM, MOKA, Realtek RTD2875P), Latin America | Google TV 14 |
| Karbonn SWTV-20AE (Skyworth OEM, SW4H, MediaTek), India | Android TV 10 |
| Kogan Google TV (Changhong CHiQ AI PONT, GTVU65KG, dangni, MediaTek mt5867), Australia | Google TV 14 |
| KTC 4K Smart TV (device hongkong, MediaTek), Taiwan | Google TV 14 |
| Liberton LTV 24H01AT (Liberton, built by Changhong, device redwood), Ukraine | Android TV 11 |
| Lumio Vision 7 55" (KTC ODM, Realtek, khardi, KTC_LUMIO_4K_EU_23GV1), India | Google TV 14 |
| Magcubic HY300 Pro projector (rk3566_rgo, device adt3, Askey) | Android 11 |
| Master-G MGA3200 (SCBC R1, Realtek; Android reports model `ATV R1`), Chile | Android TV 9 |
| Motorola MT4310 (Newsan; Hisense board, hayward, MediaTek), Argentina | Android TV 11 |
| Motorola SWTV-24AE-FHD (Skyworth OEM, beomil, MediaTek), India/Global | Google TV 14 |
| MTC UK 2K Android TV (tabata, MediaTek m5621; JVC-family firmware, `android.autoinstalls.config.JVC` present), UK | Android TV 11 |
| Noblex NoblexTV (Skyworth-built, shinjuku, MediaTek m5621), Argentina | Android TV 11 |
| Nokia Streaming Box 8000 (Sagemcom/NES, SEI600NK4, HND, Amlogic) | Android TV 14 |
| Nokia Streaming Box 8010 (Sagemcom/NES, SEI804NK, YYJ, Amlogic) | Android TV 14 |
| NVIDIA SHIELD TV 2019 tube (P3430, sif) | Android TV 11 |
| NVIDIA SHIELD TV Pro 2019 (P2897, mdarcy) | Android TV 11 |
| OK. AI PONT (Changhong, ikebukuro, MediaTek), Italy | Android TV 11 |
| OnePlus TV (C00, Cebu, MediaTek m7632), India | Android TV 10 |
| OnePlus TV (D00, Dubai) | Google TV 14 |
| OnePlus TV (Dosa, Oneplus_Dosa_IN) | Android TV 11 |
| OnePlus Y1 43" (Y Series, shibuya) | Android TV 11 |
| onn 4K Streaming Box (2023, Walmart; YOC, Amlogic), US | Google TV 14 |
| onn. 4K Plus Streaming (2025, Walmart; coffey, Amlogic S905X5M), US | Google TV 14 |
| Panasonic 4KTV-0SU (Skyworth-built SW4H, MediaTek m7332), Gulf region | Android TV 10 |
| Panasonic 4KTV-JUP (Jupiter_PA, MediaTek) | Android TV 11 |
| Panasonic Google TV LCD (OZUM_Panasonic, kowloon, MediaTek) | Android 14 |
| Panasonic TH-40HS550K (Skyworth-built, GS655 shibuya, MediaTek) | Android TV 11 |
| PEAQ PTV 43AQU-5023C (Changhong, reports OK. AI PONT, dundas, MediaTek m7332) | Android TV 11 |
| Philips 50PUD7406/43 (TPV, PH0M_EA_T32, MStar/MediaTek), Chile; 55PUS7906/12, Lithuania | Android TV 11 |
| Philips 50PUS8517/12 (TP Vision, PH1M_EA_9970A, MediaTek MT5895), Europe | Android TV 11 |
| Philips 55OLED707/12 (TP Vision, PH1M_EA_9970A, MediaTek MT5895), UK | Android TV 11 |
| Philips 55OLED708/12 (TPV, PH1M_WW_9972, MediaTek), Poland | Google TV 12 |
| Philips 55PUS7503/12 (TP Vision TPM171E, PH7M_EU_5596, MediaTek MT5891), Europe | Android TV 8.0 |
| Philips 65OLED706/12 (TP Vision, PH1M_EA_9970A, MediaTek MT5895), France; 55OLED706/12, Poland | Android TV 11 |
| Philips 65OLED907/12 (TP Vision, PH1M_EA_9970A, MediaTek MT5895), UK | Android TV 11 |
| Philips 65PML9506/12 (TP Vision, PH1M_EA_9970A, MediaTek), Spain | Android TV 11 |
| Philips 65PUS8102/12 (TP Vision TPM171E, PH7M_EU_5596, MediaTek MT5891), Europe | Android TV 8.0 |
| Philips 65PUS9435/12 (TP Vision TPM191E, PH9M_EA_5599, MediaTek MT5887), Germany | Android TV 12 |
| Philips 75PUS8505/12 (TP Vision TPM191E, PH9M_EA_5599, MediaTek MT5887), Europe | Android TV 12 |
| Philips Google TV TA7 (TPV, PH3M_AL_T32, MediaTek), Brazil | Google TV 14 |
| Philips R8G Google TV (MOKA, whitefield, Realtek RTD2851F), US | Google TV 14 |
| Philips Smart 4K TV (Skyworth, eleonas, MediaTek), India | Google TV 14 |
| Philips TPM191E / 50PUS8505 (TP Vision, PH9M_EA_5599, MediaTek MT5887), Europe | Android TV 12 |
| Polytron 2K (WLTV POLYTRON2K, hayward; Hisense/CVTE board, MediaTek mt5867), Indonesia | Android TV 11 |
| Portronics Beem 470 Smart LED Projector (Allwinner H713, 1 GB), India | Android 11 |
| PRISM+ Q65 Ultra 2026 QLED 4K AI Google TV (R10G, PRISMPLUS_FFM, MOKA, Realtek RTD2875 SoC, RTD6748 platform) | Google TV 14 |
| RCA AI PONT SA (Changhong, redwood, ISDB92RC; also sold as realme 40-inch and AIWA AW32B4SMG) | Android 14 |
| RCA RC50A21S-4KSM, Zeasn home (Toptech firmware RTK2851P, Realtek RTD2851P, board mac6p) | Android TV 9 |
| Redmi TV (MiTV-ANTL0, starwars) | Android 5.1.1 (not Android TV) |
| Redmi TV (MiTV-MFTP0, alita), China firmware 23.2.10.6129 | Android TV 9 (CN build) |
| RockTek GB1 | Google TV 14 |
| Sankey SMART2KTVSE (Whale OS, SiliconPlayer am30bm) | Android TV 13 |
| Sankey SMART_TV (CVTE, HiSilicon Hi3751V350, 1 GB) | Android 12 |
| SANYO ATV R2 (SCBC, Realtek RTD285O) | Android TV 9 |
| Sber Salute TV 43" (CVTE TP.T962D4.PC756, Amlogic T962D4, at30a6, 2 GB), Russia | StarOS 1.101 / Android 13 |
| SHARP 2T-C32GH3000I (SHARP 2K Smart TV-A; Skyworth-built, beomil, MediaTek mt5867), Indonesia | Google TV 14 |
| SHARP AQUOS TVE21F (AQUOS-TVE21F, banqiao, CVTE/MediaTek), UK | Android TV 14 |
| SHARP AQUOS TVE23A (AQUOS-TVE23A, sandiego, CVTE/MediaTek mt5889), Denmark | Google TV 14 |
| Skyworth / Coocaa 8R40_K5C (Realtek rtd285x, 1 GB, 32-bit), China domestic firmware | Android 8.0 (not Google TV) |
| Skyworth 65A3F (7T641_A3F, MediaTek MT9633), China | Android 13 (CCOS 10.01.250328) |
| Skyworth 7S60_A5 (sky8S34, 1.9 GB), China market | Coocaa OS / Android 8 |
| Skyworth Coocaa K3 Pro 90P3D Max (8R145_P3DM, Realtek RTD2885N), China market | Android 11 (Coocaa OS) |
| Skyworth SW-22SA (SW6H, MediaTek), ~1.9 GB | Android TV 14 (Google TV home) |
| Skyworth SWTV-21AE (sunnyvale, MediaTek) | Android TV 14 |
| Skyworth SWTV-22AE-FHD (umeda, MediaTek), Vietnam/Global | Google TV 14 |
| Skyworth SWTV-24AE-4K NF (eleonas, MediaTek mt5896), South Africa/Global | Google TV 14 |
| Skyworth SWTV-24NA-4K NF (eleonas, MediaTek mt5896), US | Google TV 14 |
| Sony BRAVIA 4K AE1 (BRAVIA_AE_M6L, MediaTek MT5897), EU | Google TV 12 |
| Sony BRAVIA 4K AE2 (BRAVIA_AE2, MediaTek MT5897), North America | Google TV 14 |
| Sony BRAVIA 4K GB (BRAVIA_ATV2_EU, BRAVIA_ATV2) | Android TV 9 |
| Sony BRAVIA 4K GB (BRAVIA_ATV2_UC, MediaTek mt5891) | Android TV 9 |
| Sony BRAVIA 4K UR2 (BRAVIA_UR2_4K, MediaTek) | Android TV 10 |
| Sony BRAVIA 4K UR3 (BRAVIA_UR3, MediaTek mt5893), EU | Android TV 10 |
| Sony BRAVIA 4K VH1 (KD-55XH9096, BRAVIA_VH1, MediaTek MT5895), EU | Google TV 12 |
| Sony BRAVIA 4K VH2 (KD-55X80J, BRAVIA_VH2, MediaTek), EU, North America and India | Google TV 12 |
| Sony BRAVIA 4K VH21 (KD-65X85K, 2022, BRAVIA_VH21, MediaTek MT5895), Australia | Google TV 12 |
| Sony BRAVIA 4K VH21 (XR-55A80K, 2022, BRAVIA_VH21, MediaTek mt5895), EU | Google TV 12 |
| Sony BRAVIA 9 II (K-65XR90M2, BRAVIA_AE3, MediaTek MT5838) | Google TV 14 |
| Sony BRAVIA BF1 (BRAVIA 2 II class, BRAVIA_BF1_4K_A, MediaTek) | Google TV 12 |
| Sony BRAVIA KD-43X80L (2023, BRAVIA_VH22, MediaTek MT5895), EU | Google TV 12 |
| Sony BRAVIA KD-49X8307C (2015, "BRAVIA 4K 2015", SVP-DTV15, MediaTek), EU firmware | Android TV 7.0 |
| Sony BRAVIA KD-55X75K (BRAVIA_VU3_4K, Realtek rtd2885m) | Google TV 14 |
| Sony BRAVIA KD-55X8000C (2015, "BRAVIA 4K 2015", SVP-DTV15, MediaTek), China firmware | Android TV 7.0 |
| Sony BRAVIA KD-55X8509C (2015, "BRAVIA 4K 2015", SVP-DTV15, MediaTek), EU firmware | Android TV 7.0 |
| Sony BRAVIA KD-55X85J (2021, BRAVIA_VH2, MediaTek MT5895), South America | Google TV 12 |
| Sony BRAVIA KD-55XE8596 (2017, "BRAVIA 4K GB", BRAVIA_ATV2_EU, MediaTek MT5891), UK | Android TV 9 |
| Sony BRAVIA KD-55XF7596 (2018, "BRAVIA 4K GB ATV3", BRAVIA_ATV3_4K_EU, MediaTek MT5891), EU | Android TV 9 |
| Sony BRAVIA KD-55XF8096 (2018, "BRAVIA 4K GB ATV3", BRAVIA_ATV3_4K_EU, MediaTek MT5891), Spain | Android TV 9 |
| Sony BRAVIA KD-65X80J (2021, BRAVIA_VH2, MediaTek), India | Google TV 10 |
| Sony BRAVIA KD-65X82L (2023, BRAVIA_VH22, MediaTek), India | Google TV 12 |
| Sony BRAVIA KD-65X9300D (2016, SVP-DTV15, MediaTek), Asia-Pacific | Android TV 8.0 |
| Sony BRAVIA KDL-43W950D (2015/2016, SVP-DTV15, MediaTek), India ("PA") firmware | Android TV 8.0.0 |
| Sony BRAVIA TL2 (OD0M_EA_T32, TPV-built, MediaTek), Europe | Android TV 11 |
| Sony BRAVIA VU31 4K (BRAVIA_VU31_4K, Realtek), India | Google TV 14 |
| Sony BRAVIA X9000H (2020, BRAVIA_VH1_CN, MediaTek MT5895), China | Android TV 12 |
| Sony BRAVIA XBR-55X850C (2015, "BRAVIA 4K 2015", SVP-DTV15, MediaTek), US firmware | Android TV 8.0 |
| Sony BRAVIA XBR-55X850D (Android model "BRAVIA 4K 2015"), firmware 6.827 | Android TV 8.0.0 |
| Sony BRAVIA XBR-65X900E (2017, BRAVIA_ATV2) | Android TV 9 |
| Sony BRAVIA XBR-65X900F (2018, BRAVIA_ATV3_4K, MediaTek MT5891), US/Canada | Android TV 9 |
| Sony XR-65A80J (65-inch OLED, 2021, BRAVIA_VH2), EU | Google TV 12 |
| Sony XR-65X90J (2021, BRAVIA_VH2, MediaTek MT5895), China | Android TV 10 |
| Stellamore T950S projector (Droidlogic be30a3, Amlogic txhd2) | Android 13 |
| Super General 55" 4K (CVTE TP.SK708D.PC821, Amlogic T972, x30a4, 1 GB), Saudi Arabia | Android 9 |
| TCL / FFalcon China (tcl9655_cn, MediaTek MT9655 "merak", UTT2.250416.001 AU01) | Android 14 |
| TCL 32L5A (C06, model "Smart TV", 1 GB) | Android TV 11 |
| TCL 32S5400A (C06, C06_2K_GB, MediaTek MT9221, 1 GB), 2023 | Android TV 11 |
| TCL 32S5K (C06, C06_2K_GB, MediaTek MT9221, 918 MB), EU | Android TV 11 |
| TCL 40S5400A (C06, C06_2K_GB, MediaTek MT9221, 1 GB) | Android TV 11 |
| TCL 43C645 (Smart TV Pro, Realtek RTD2851 / R51MT05, V8-R51MT05-LF1V652); also TCL 50C635 / 60C635 (G03_4K_GB, Realtek rtd288o), Spain | Google TV launcher on Android TV 11 |
| TCL 43EP660 (Europe, BeyondTV, Realtek rtd285o, V8-R851T02-LF1V378) | Android TV 9 |
| TCL 43P615 (Europe, BeyondTV, Realtek RTD2851, AR06) | Android TV 11 |
| TCL 43P725 (Smart TV Pro, BeyondTV4, Realtek rtd288o), Italy | Android TV 11 |
| TCL 43S450F-CA (S4 Fire TV Edition, AFTDEC012E, ginza, MediaTek), Canada | Fire OS 8.1.8.5 / Android 11 |
| TCL 43S5400A (C06, C06_2K_GB, MediaTek MT9221, 1 GB) | Android TV 11 |
| TCL 43S5K / 32V5C (G13, G13_2K_GB, MediaTek MT9221/MT5867, 1 GB), India | Google TV 14 |
| TCL 50C725K (Smart TV Pro, BeyondTV4/BeyondTV6, RTD2851M, Realtek rtd288o, 2 GB), AR10 build | Android TV 11 |
| TCL 50DP660 (Europe, "Percee TV", MediaTek MT5658 / board mt5891, V8-T658T01-LF1V602) | Android TV 8.0 |
| TCL 50EP680 (Europe, BeyondTV, Realtek rtd285o) | Android TV 11 |
| TCL 50P615 (Greece and Türkiye, BeyondTV, Realtek RTD2851 / rtd285o, AR05) | Android TV 11 |
| TCL 50P735 (BeyondTV4, Smart TV Pro) | Google TV 11 |
| TCL 55" 4K Google TV, 2023 (G10, G10_4K_US_NF); also TCL 55P6K, Spain and TCL 43P69K, Netherlands (G10_4K_GB_NF_32BIT) | Google TV 12 |
| TCL 55C715 QLED (Europe, BeyondTV2, Realtek RTD2851, V8-R851T02-LF1V606) | Android TV 9 |
| TCL 55C725 (Smart TV Pro, BeyondTV4, G03_4K_GB, Realtek R51MT05, V8-R51MT05-LF1V620), Italy; also TCL 65C655 Pro (same build) | Google TV launcher on Android TV 11 |
| TCL 55C745, 2023 (G08, G08_4K_GB, MediaTek tcl9618) | Google TV 14 |
| TCL 55C7K QD-Mini LED, 2025, UK (G08, MediaTek tcl9618) | Google TV 14 |
| TCL 55C815 (2020, Europe/Denmark, BeyondTV2, Realtek rtd285o) | Android TV 11 |
| TCL 55EP640 (Greece, BeyondTV, Realtek RTD2851 / rtd285o, AR04) | Android TV 11 |
| TCL 55P638 (Smart TV, BeyondTV4) | Android TV 11 |
| TCL 55P7K, Middle East (G10, G10_4K_GB, Realtek RTD2875P) | Google TV 12 |
| TCL 55P8LS QD-Mini LED, Vietnam (G10, G10_4K_GB_32BIT, Realtek rtd6748) | Google TV 12 |
| TCL 55Q10G Pro (China, tcl_mt5879_cn, MediaTek MT9952) | Android TV 11 |
| TCL 55S470G (G10, G10_4K_US_NF, Realtek), US | Google TV 12 |
| TCL 55S546 (5-Series, G03_4K_US, BeyondTV4, GTV93), US | Google TV 11 |
| TCL 58S571F (TCL Fire TV, AFTDEC012E, ginza, MediaTek mt5896), US | Fire OS 8 / Android 11 |
| TCL 65C725 (Smart TV Pro, BeyondTV4) | Android TV 11 |
| TCL 65C735 (Smart TV Pro, G05, MediaTek), France; also TCL 55C728, Turkey, TCL 65C935, France, and TCL 65C745, France | Google TV 11 |
| TCL 65C7K (Smart TV Pro, G08, G08_4K_GB, MediaTek mt5896), Brazil | Google TV 14 |
| TCL 65C7L (Smart TV Pro, G08, G08_4K_GB, MediaTek tcl9618), 2025, Australia | Google TV 14 |
| TCL 65EP640 (Finland, BeyondTV, Realtek RTD2851 / rtd285o, AP06) | Android TV 9 |
| TCL 65EP68 (Europe, BeyondTV, Realtek RTD2851 / rtd285o, V8-R851T02-LF1V722) | Android TV 11 |
| TCL 65Q550G (TCL Smart TV, Google TV 11, V8-R51MT08-LF1V095.021344) | Google TV |
| TCL 65Q650G (US Q6 2023, BeyondTV4, Realtek RTD2851 / R51MT08, V8-R51MT08-LF1V095, 2 GB) | Google TV 11 |
| TCL 75C845X2, UK/Europe (G08_4K_GB, MediaTek tcl9618) | Google TV 14 |
| TCL 75P8K, Europe / Nordics (G10, G10_4K_GB / C6KS, Realtek RTD2875P / rtd6748) | Google TV 12 |
| TCL 75T7 (Smart TV Pro, G08, G08_4K_US, MediaTek tcl9618), US firmware | Google TV 14 |
| TCL 85RM7L (G08, G08_4K_GB, MediaTek tcl9618) | Google TV 14 |
| TCL ak30a5 (China, Amlogic) | Android TV 9 |
| TCL BeyondTV (India) | Android TV 9 |
| TCL BeyondTV, EU/Nordic (Realtek RTD2851, V8-R851T02-LF1V606) | Android TV 9 |
| TCL BeyondTV, Poland (Realtek RTD2851, AP03, 2 GB) | Android TV 9 |
| TCL BeyondTV2 (Latin America / Brazil, Realtek RTD2851) | Android TV 9 |
| TCL BeyondTV2, Android 11 build (AR05, Realtek rtd285o) | Android TV 11 |
| TCL C755 QD-Mini LED, 2024, Australia (G08, MediaTek tcl9618) | Google TV 14 |
| TCL C89B 85", 2024, a local variant of the C855 (G08, G08_4K_GB, MediaTek tcl9618) | Google TV 14 |
| TCL L40S66E (C06, C06_2K_GB, MediaTek MT9221, 1 GB), Argentina | Android TV 11 |
| TCL Q7C (Smart TV Pro, G08, G08_4K_GB, MediaTek tcl9618), European firmware | Google TV 14 |
| TCL QM6K Pro, 2025 (G08, MediaTek tcl9618) | Google TV 14 |
| TCL Smart TV (G07, G07_4K_GB_NF, Realtek RTD2851A, 2 GB) | Google TV 11 |
| TCL Smart TV Pro (G03, G03_4K_GB, BeyondTV4, Realtek R51MT05, V8-R51MT05-LF1V620), Middle East firmware | Google TV 11 |
| TCL Smart TV Pro (G05, G05_4K_US, MediaTek MT5889); also TCL 55Q750G | Google TV 11 |
| TCL Smart TV Pro (G08, G08_4K_GB, MediaTek), Australian firmware | Google TV 12 |
| TCL Smart TV Pro (G08, G08_4K_GB, MediaTek), Europe/UK firmware | Android TV 12 |
| TCL Smart TV Pro (G09, G09_4K_GB, Amlogic T5M, 2 GB), UK firmware, Brazil | Google TV 12 |
| TCL tc8000_ay30a2 (China, Amlogic ohm, TC8000, 3.8 GB) | Android 11 |
| TCL UnionTV, Latin America (TCL-built like Thomson 32HE5800, Realtek RT2841) | Android TV 9 |
| Tele System TS ON T2/HD (ONT2HD, SDMC, Amlogic), Italy | Android TV 11 |
| Thomson 32HE5800 (TCL-built UnionTV, Realtek RT2841), Spain | Android TV 9 |
| Thomson 43RT1055 FA Series (SPPL-built, sauve, Realtek RTD2841A), India | Android TV 14 |
| Thomson SPPL 4K Google TV (Skyworth SW6H, MediaTek, same board as `skyworth-sw-22sa/`), India, ~1.9 GB | Android TV 14 (Google TV home) |
| Thomson T32RTL6000 (SCBC, Realtek; Android reports model `ATV R1`) | Android TV 9 |
| TiVo Stream 4K (SEI Robotics IPA1104HDW, SEI400TV, Amlogic S905Y2, NES) | Android TV 12 |
| Tornado 65UA1400E (El Araby, Skyworth-built SW4H, MediaTek m7332), Egypt | Android TV 11 |
| Toshiba (Vestel Cosmos170, yeoksam, MediaTek), Spain | Android TV 11 |
| Toshiba Fire TV (AFTHA004, hazel) | Fire OS 7 / Android 9 |
| Turkcell TV+ 4K Box (SDMC DV6069Y, Amlogic), Turkey, operator streaming box | Android TV 10 |
| Tuvio TD43UFBCV5 (Changhong AI PONT, DVB96, MediaTek m7632), Russia | Google TV 14 |
| Verizon Stream TV, 1st gen (WNC JS8V, Amlogic), US | Android 12 |
| Vidda VIDAA_TV (Hisense sub-brand; device MT9653, board mt5879), China JuUI | Android TV 11 |
| Vision 2024VISION43STV (CVTE TP.T950D4T.PB751, Amlogic T950D4, 1 GB), Europe | Android 13 |
| Vu 4K (HVCL, lushan, MediaTek), India | Google TV 12 |
| Vu Premium FHD 43-inch (Smart 2K ATV4, hamamatsucho), India | Android TV 9 |
| Vu VIBE TV (KONKA, bandra, Realtek), India | Google TV 14 |
| Wanbo X5 Pro projector (built-in Google TV stick: SkyworthDigital YYT, Amlogic), Australia | Google TV 14 |
| XGIMI Horizon S Pro projector (XK03H, xgimigalileo, MediaTek) | Android TV 11 |
| XGIMI MoGo 2 projector (MoGo, xgimiapollo, pokemon) | Android TV 11 |
| XGIMI MoGo 3 Pro projector (G0035, altai, MediaTek) | Google TV 14 |
| XGIMI MoGo 4 projector (WK08K, altai, MediaTek) | Google TV 14 |
| Xiaomi Mi Box 3 (MIBOX3, once), streaming box | Android TV 9 |
| Xiaomi Mi Box S (MIBOX4, oneday), streaming box | Android TV 9 |
| Xiaomi Mi Box S 2nd gen (MiTV-AFKR0, jaws), streaming box | Google TV 11 |
| Xiaomi Mi TV (MiTV-MOOQ0, croods; includes P1 43" EU variant) | Android TV 10 and 11 |
| Xiaomi Mi TV 4A (MiTV-MOOQ1) | Android TV 10 |
| Xiaomi Mi TV P1 43" (MiTV-MOOQ2, croods) | Android TV 11 |
| Xiaomi MiTV-AESP0 stick (aquaman, 1 GB) | Android TV 9 |
| Xiaomi MiTV-ANSP0 (小米电视5 Pro 65", jobs, Amlogic), China firmware 23.6.5.3226, 4 GB RAM | Android TV 9 (CN build) |
| Xiaomi MiTV-ASTP0 (mulan, sold as L55M7-EA), China firmware 22.12.2.1630, 1.5 GB RAM | Android TV 9 (CN build) |
| Xiaomi MiTV-ASTP1 (maverick), China firmware 2.40.641.0514.cn, 1 GB RAM, 720p | Android TV 9 (CN build) |
| Xiaomi MiTV-AXFR0 (rango, Amlogic, 1 GB), India | Android TV 11 |
| Xiaomi MiTV-AXFR1 (rango, Amlogic, 1 GB), India | Android TV 11 |
| Xiaomi MiTV-AXSO0 (dangal; Mi TV 4C Pro 2019 / 4A Pro) | Android TV 9 |
| Xiaomi MiTV-AXSO2 (dangalUHD, India, 2 GB) | Android TV 9 |
| Xiaomi MiTV-MFTR0 (freeguy, MediaTek mt9653), China firmware OS2.0.23.0.RFTMATV (HyperOS TV 2) | Android 11 (HyperOS TV 2, CN build) |
| Xiaomi MiTV-MOOR2 / MOOR4 (venom, MediaTek) | Google TV 11 |
| Xiaomi MiTV-MOSR4 (irobot, MediaTek) | Google TV 11 |
| Xiaomi MiTV-MSSP0 / Xiaomi Mi TV 4A 32 (amelie), firmware PI.5926 | Android TV 9 |
| Xiaomi MiTV-MSSP1 (nino), firmware PI.5925 | Android TV 9 |
| Xiaomi MiTV-MSSP2 (volver, MediaTek), firmware PI.4621, low-RAM | Android TV 9 |
| Xiaomi MiTV-MSSP3 (machuca, MediaTek) | Android TV 11 |
| Xiaomi MiTV-MSSP3 (machuca, MediaTek), Android 9 firmware PI.7524 | Android TV 9 |
| Xiaomi Redmi Smart TV A Pro 55" 2025 Energy-saving (MiTV-ASTR0, blueplanet, Amlogic), China firmware OS3.0.106.0.USTAATV, 3 GB RAM | Android 14 (HyperOS 3, CN build) |
| Xiaomi TV A Pro 75" 2026 (MiTV-MZTU0, river, MediaTek) | Google TV 14 |
| Xiaomi TV Box S 3rd gen (MiTV-AFMU0, twilight, Amlogic), streaming box | Google TV 14 |
| Xiaomi TV EA Pro 55" (MiTV-MTEQ0, baymax, MediaTek), China firmware 23.6.26.4028 | Android TV 10 (CN build) |
| Xiaomi TV S Mini LED 75 2025 (MiTV-MOEU0, ladybird, MediaTek) | Google TV 14 |
| Xiaomi TV S Pro 100 Mini LED 2025 (小米电视 S Pro 100, L100MB-SP, MiTV-MFFU0, finch, MediaTek), China firmware OS3.0.115.0.UFFMATV, 4 GB RAM | Android 14 (HyperOS 3, CN build) |
| Xiaomi TV Stick 4K (MiTV-AYFR0, soul, Amlogic), streaming stick | Android TV 14 |
| Xiaomi TV Stick 4K 2nd gen (MiTV-AFMU1, twilight, adastra, Amlogic), streaming stick | Google TV 14 |

<!-- tv-table:end -->

</details>

Your TV not here? The prompt discovers the bloat itself. Add your list afterwards and the next
person with your model skips that step: [CONTRIBUTING.md](CONTRIBUTING.md).

## Doing it by hand

Everything the assistant does, you can run yourself. The tools need Python 3 and nothing else.

```bash
python3 -m venv tools/venv && tools/venv/bin/pip install -r tools/requirements.txt
export TV_HOST=192.168.1.50                                   # your TV's IP address

tools/venv/bin/python -I tools/tv.py --probe                 # model, Android version, firmware
tools/venv/bin/python -I tools/tv.py 'pm list packages'      # one shell command on the TV
tools/disable.sh examples/xiaomi-mi-tv-4a/debloat.txt        # apply a debloat list
tools/restore.sh examples/xiaomi-mi-tv-4a/debloat.txt        # undo it, stock launcher back
tools/venv/bin/python -I tools/check.py examples/xiaomi-mi-tv-4a/debloat.txt   # what a firmware update undid
TV_HOST=192.168.1.50 launcher/build-and-install.sh           # build the launcher and make it Home
tools/theme.sh themes/aurora                                 # push a theme; --reset removes it
```

| Tool | What it does |
|---|---|
| [`tools/tv.py`](tools/tv.py) | Pure-Python ADB client: shell, batch, push, pull, reboot, and screenshots, optionally pressing remote keys first. Works where the stock `adb` binary is blocked. |
| [`tools/b64push.py`](tools/b64push.py) | Uploads large files such as APKs in base64 chunks and verifies the checksum. |
| [`tools/relay.py`](tools/relay.py) | Forwards a local port to the TV's ADB port, for assistants whose sandbox can't reach your network. Listens on this computer only. [Guide](tools/relay.md). |
| [`tools/disable.sh`](tools/disable.sh), [`restore.sh`](tools/restore.sh) | Apply or undo a debloat list, the undo from the state recorded before the change. |
| [`tools/recovery.py`](tools/recovery.py) | Writes the record `disable.sh` needs to undo itself, and prints the restore batch. |
| [`tools/check.py`](tools/check.py) | Compares the TV with the list that was applied, for what a firmware update undid; `--fix` puts it back. |
| [`tools/adb-closed.py`](tools/adb-closed.py) | Checks that the TV's network ADB port is closed once debugging is off: exits 0 when nothing answers, 1 while it is still open, 2 when the check itself could not run. No `nc` needed. |
| [`tools/privacy.py`](tools/privacy.py) | Audits the TV for voice, ACR and telemetry candidates and prints the evidence for each; `--write` turns the findings into a list `disable.sh` and `restore.sh` already understand. |
| [`tools/theme.sh`](tools/theme.sh) | Push a folder from [`themes/`](themes/) to the launcher and restart Home. |
| [`tools/manage.py`](tools/manage.py) | List every add-on, theme and switch with its screenshot, see what is on the TV, and install, update or remove one by running its creator's own script. |
| [`tools/bench.py`](tools/bench.py) | Measures the launcher that answers HOME: key latency, how far the focus moved, janky frames, cold start, return to Home, and the idle CPU and PSS it costs. Changes nothing on the TV and writes the raw per-key numbers to JSON; no extra install. |
| [`tools/trace.py`](tools/trace.py), [`trace_report.py`](tools/trace_report.py) | Records Perfetto traces of the launcher (return to Home, cold start, D-pad) and reports where its time goes, per run and before against after. Optional: needs [`requirements-perf.txt`](tools/requirements-perf.txt). [Guide](tools/trace.md). |
| [`tools/flamechart.py`](tools/flamechart.py) | Draws a before/after flame chart from those traces, for a pull request: one lane per thread on a shared time scale, the work a change targets in magenta with its total. Optional, like the trace tools. [Guide](tools/flamechart.md). |
| [`tools/screendiff.py`](tools/screendiff.py) | Compares a before and an after screenshot pixel by pixel, leaving out the clock or anything you mask, and writes the pair with your Wi-Fi name blurred. Optional, like the trace tools. [Guide](tools/screendiff.md). |
| [`tools/race_calendar.py`](tools/race_calendar.py) | Writes this season's F1 calendar into a theme for race-weekend mode, from the free Jolpica API. |
| [`tools/web_dashboard.py`](tools/web_dashboard.py) | Real-time web task manager: inspect TV RAM/CPU graphs, view running processes, and close background apps. |
| [`launcher/build-and-install.sh`](launcher/build-and-install.sh) | Builds a shrunk release (about 500 KB), installs it, switches Home, and rolls back if Home does not land on it. |

The scripts never leave the TV without a Home app: they only disable a stock launcher they recognise,
and skip the Home app in a list until another launcher answers HOME. Details below.

<details>
<summary><b>Windows (PowerShell)</b></summary>

Every script has a PowerShell twin next to it. On Windows with Android Studio installed nothing
else is needed:

```powershell
python -m venv tools\venv; tools\venv\Scripts\python.exe -m pip install -r tools\requirements.txt
powershell -ExecutionPolicy Bypass -File launcher\build-and-install.ps1 -TvHost 192.168.1.50
powershell -ExecutionPolicy Bypass -File tools\disable.ps1 -TvHost 192.168.1.50 examples\xiaomi-mi-tv-4a\debloat.txt
```

</details>

<details>
<summary><b>How the scripts protect Home</b></summary>

The install scripts ask the TV which launcher owns Home and only disable one they recognise, the four
in `KNOWN_STOCK_LAUNCHERS`: `com.google.android.tvlauncher`, `com.google.android.apps.tv.launcherx`,
`com.google.android.leanbacklauncher` and Xiaomi's `com.mitv.tvhome` (PatchWall). Pass
`STOCK_LAUNCHER=<package>` (`-StockLauncher`) to override, or `none` to leave it alone.

`tools/disable.sh` (`disable.ps1`) applies the same rule before it disables anything: if the list
contains the app that owns Home and no other app on the TV answers HOME, it skips that entry, says
why, and disables the rest of the list. The setup wizard, the settings apps, a vendor `FallbackHome`
and factory-test apps (Xiaomi's `com.duokan.factorytest`) answer HOME too and don't count. So a list applied before the launcher handover cannot leave
the TV with nothing to start. Run the same list again once `launcher/build-and-install.sh` has made
the new launcher Home and the entry is applied; `FORCE_HOME=1` (`$env:FORCE_HOME`) disables it anyway.

</details>

<details>
<summary><b>Where your lists can live</b></summary>

Debloat list paths passed to `disable.sh` and `restore.sh` are relative to the directory you run
from, so your own lists can live outside the repository. Absolute paths also work.

</details>
## The microphone, and what can be said about it

"Which of these packages is listening?" has no honest one-line answer, and a permission scan makes
it worse: on many sets most packages run as `android.uid.system`, so fifty of them show
`RECORD_AUDIO: granted=true` and none of them record anything. On others the microphone is in the
remote, not the panel, and a package with no audio permission at all still sees everything on screen
— ACR clients such as `tv.inscape.tvclient.inscapeacr` identify content from the picture and need
only `INTERNET`.

`tools/privacy.py` reports what the TV actually says instead of guessing:
`TV_HOST=192.168.1.50 tools/venv/bin/python -I tools/privacy.py` prints voice, ACR and telemetry
candidates in three groups, each with its evidence — named by the firmware's own
`voice_interaction_service`, a known component from [`examples/`](examples/), or nothing but a
matching name — plus whether the package declares `RECORD_AUDIO`, whether it shares the system UID,
and whether the returned AppOps history contains microphone access. A configured mode or a rejected
attempt alone is not evidence of access.

`--write privacy.txt` turns the identified candidates into a normal debloat list, so applying it and
undoing it are the commands you already use (`tools/disable.sh`, `tools/restore.sh`). Packages that
only matched by name stay out unless you pass `--include-low`; they are the ones to check with
`dumpsys package <name>` first.

What the audit cannot do is prove intent. `appops` shows an op was touched, not what happened to the
stream afterwards, and no scan of package names proves a TV is or is not recording. Disabling an
assistant is a trade you make with a specific loss (the remote's mic button, voice search inside
apps), which is why the report names that loss per package instead of calling it a win.

## The launcher

A small native home screen for Android TV: **about 500 KB**, plain Java, built for TVs with 1 GB of RAM.
No blur, no video, no network permission, no background services.

The **TV** needs Android 8.0 (API 26) or newer: `minSdk` is a compile-time floor in
[`launcher/app/build.gradle`](launcher/app/build.gradle), and the install script asks the TV for
`ro.build.version.sdk` before it builds, so an older set is told both numbers instead of failing at
`pm install` (see [Troubleshooting](docs/TROUBLESHOOTING.md) for what is left on such a set).

| What | How it works |
|---|---|
| **Configure** | Dock order, hidden apps, Live TV app, 4 to 7 tiles per row, reduced motion. All in [`config.xml`](launcher/app/src/main/res/values/config.xml) |
| **Settings on the TV** | Hold OK on Settings for launcher settings. Optional `launcher_settings_button` makes a normal click open them directly; TV settings remain on the settings page and their app tile. |
| **Themes** | Background, wallpaper and layout from a `config.json` pushed over Wi-Fi, no rebuild. eight ship in [`themes/`](themes/), the built-in `default/` included |
| **25 languages** | Follows the TV's language, with a fully mirrored right-to-left layout for Hebrew |
| **App library** | Optional: browse every installed app and add or remove Home shortcuts. See [the app library guide](docs/app-library.md) |
| **Tile menu** | Optional (`tile_menu`): hold OK on a tile for Move, Remove from Home and App info. Off, holding OK opens App info |
| **Module cards** | Any TV app can offer its own tile; you approve each one once. See [`docs/modules.md`](docs/modules.md) |
| **Add-ons** | Optional apps that plug in as module cards, kept in [`addons/`](addons/) and never built into the launcher |
| **Clock screensaver** | A drifting clock over the glow, so nothing burns in |
| **Remote keys** | Keeps the Input, YouTube and gear buttons working after the stock launcher is gone |

<details>
<summary><b>Build it yourself</b></summary>

It needs JDK 17 and Android SDK platform 34. On macOS: `brew install openjdk@17 android-commandlinetools`
and `sdkmanager "platforms;android-34" "build-tools;34.0.0"`. On Linux the install script finds
`~/Android/Sdk` and the `java` on your PATH.

</details>

<details>
<summary><b>Configure</b></summary>

[`config.xml`](launcher/app/src/main/res/values/config.xml) sets the dock order, hidden apps, the Live TV
package, columns (tiles shrink from 5 up), rows under the dock, an optional VPN app for the network pill,
and `reduced_motion` for TVs whose GPU cannot keep up. Themes set the same keys plus a background colour
and a wallpaper.

</details>

<details>
<summary><b>Launcher settings and the default home</b></summary>

- Click **Settings** for the TV's own settings. Hold OK on it for its menu: **Launcher settings** (with
  `launcher_settings_button` a click opens those instead and the menu leaves the row out), **Native settings**
  where the TV ships a settings app of its own beside Android's - Xiaomi China, TCL/FFALCON and Fire OS are
  in `settings_fallback_components` - because that app holds pages Android's has no row for, such as
  Xiaomi's Account & Security with the ADB switch; **Module cards** with `module_cards`; **Edit dock** with
  `dock_row`; and **Reorder apps**. The launcher's own page has these rows, top to bottom:
  - **Reorder apps**, only with `launcher_settings_button`: OK picks an app up, the arrows move it, Back
    places it, and Back again finishes.
  - **Apps per row** 4, 5, 6 or 7; **Rows below dock** 0 to 3, how many rows of tiles stay visible under the
    dock band; **Dock band opacity** 100%, 70%, 50% or 30%. The last two only do anything while the dock is
    its own row (`dock_row`, on by default).
  - **Wallpaper**: one picture from the theme's collection (or its `wallpaper.jpg`), **None**, or **Theme
    default**. A pick holds that picture and stops the rotation until you choose Theme default, press
    Follow theme, or push the theme again.
  - **Animations** on or off, and **Advanced effects** (tile lift, icon zoom and smooth ambient-colour
    transitions; they may use more memory) once animations are on.
  - **Open on start**, only with `open_on_start_choice`: Off, or an app that opens instead of Home the first
    time Home comes up after the TV starts. Once per boot, so Home and Back still reach the launcher.
  - **Follow theme (reset on-TV choices)**, which clears every choice above. Pushing or resetting a theme
    clears them too.
  - **Choose default home** opens Android's home picker, and **TV system settings** the TV's own settings
    page.
- **Change the default home:** Launcher settings and the optional Default-home tile
  (`show_home_settings_shortcut`) open Android's home picker. Choose the original launcher to stop using
  CleanYourTV as Home, or CleanYourTV to return. The original launcher must be enabled; if the install
  script disabled it, run the restore script below first. Some TVs provide no home picker.

</details>

<details>
<summary><b>Languages</b></summary>

English, German, Spanish, Catalan, French, Russian, Serbian (Latin), Polish, Czech, Italian, Portuguese,
Turkish, Arabic, Hebrew, Simplified Chinese, Traditional Chinese (Taiwan), Thai, Finnish, Vietnamese,
Bangla, Indonesian, Norwegian Bokmål, Dutch, Uzbek (Latin) and Ukrainian. The launcher follows the TV's language. Android 13+ can set it for the launcher
alone: `tools/tv.py 'cmd locale set-app-locales com.gathin.tvhome --locales ca'`.

Hebrew mirrors the layout: menu buttons on the left, date and clock on the right with the greeting above,
dock and app rows starting on the right with matching remote navigation, the input selector's icons and
labels on the right, and dates with the month prefix ב (for example, 19 באוקטובר). The Clock screensaver
right-aligns its date and time. Russian covers every screen, including race weekends, festival greetings and the Shamsi date. Other
languages keep their original layouts.

</details>

<details>
<summary><b>Module cards</b></summary>

Any TV app can declare its own tile (label, caption, glow). The launcher asks you once per app, keeps it
hidden by default, and remembers the answer for that app's signing key and card name. Hold OK on Settings
and choose Module cards to change your mind. How to make one: [`docs/modules.md`](docs/modules.md); the
contract: [`docs/specs/card.md`](docs/specs/card.md).

</details>

<details>
<summary><b>Clock screensaver</b></summary>

Pick "Clock" in the TV's screensaver settings, or over ADB:

```bash
tools/tv.py 'settings put secure screensaver_components com.gathin.tvhome/.ClockDream; settings put secure screensaver_enabled 1'
```

</details>

<details>
<summary><b>Remote keys</b></summary>

- An optional accessibility service keeps the remote's **Input** button working on Google TV
  (`INPUT_KEY_SERVICE=1` at install).
- It can bring back other buttons only the stock launcher answered, such as the **YouTube** button
  (`app_keys` and `input_key_keycodes`, in config.xml or a theme; `HOME_KEY_SERVICE=1` reads `app_keys` too).
- A `hold:` prefix on an `app_keys` entry gives that button a second app: a normal press opens the
  unprefixed app, holding for half a second opens the hold one (by keycode or `hold:scan:...`).
- In place of an app, an `app_keys` entry can name an action: `back`, `home`, `recents`, `mute`, `sleep`,
  `screensaver`, `settings` or `wifi`, so a spare colour button can mute the TV or, held, put it in standby.
- `HOME_KEY_SERVICE=1` also fixes sticks whose Home key sends ALL_APPS, and opens Settings from the remote's
  **gear** button, even one Android does not map (scancodes or keycodes in config.xml).
- With `settings_key_panel` the gear opens a [small quick settings panel](docs/quick-settings-panel.jpg)
  over Home or any app: TV settings, Display, Network, Apps and Launcher settings.

</details>

<details>
<summary><b>Navigation and artwork</b></summary>

Up and Down move one row. The top buttons never fall through to tiles, and Down from them returns to the
dock tile you came from. The last focused app is remembered, and installing or removing an app refreshes
the tiles while Home is open. App banners load at xhdpi or better, so low-density TV UIs do not downsample
them before the focus zoom.

</details>

## Restoring your TV

Keep the list you applied. One line puts the TV back the way it was:

```bash
TV_HOST=192.168.1.50 tools/restore.sh examples/<your tv>/debloat.txt                                    # Android TV
TV_HOST=192.168.1.50 tools/restore.sh examples/<your tv>/debloat.txt com.google.android.apps.tv.launcherx  # Google TV
```

| What | How |
|---|---|
| **A record comes first** | Before `tools/disable.sh` changes anything it writes down what the TV looked like, in `~/.cleanyourtv/`. |
| **Restore replays it** | A package you had disabled yourself stays disabled, and Home goes back to the component that owned it. |
| **The launcher stays installed** | Remove it from the TV's Settings → Apps if you want it gone. |
| **No computer?** | If the TV is unreachable, its own Settings → Apps page can re-enable packages too. |

<details>
<summary><b>What the record holds, and what happens without one</b></summary>

Before `tools/disable.sh` changes anything it writes down what the TV looked like: for every package
in the list whether it was enabled, already disabled or not installed, which component owned Home,
and the three animation scales. That record lives in `~/.cleanyourtv/`, one file per TV and list, and
a second run of the same list keeps the first record instead of saving the cleaned-up state over it.
If you added packages to the list since, that run adds them to the record before disabling them.

Keep the list you applied. The restore script replays the record, so a package you had disabled
yourself stays disabled and Home goes back to the component that owned it:

The `stock launcher` argument is only a fallback: with a record, the launcher that actually owned Home
is the one that gets it back, and the argument is used only when nothing answered HOME at the time
or this project's launcher owned it. The stock launcher is enabled again either way, unless it was
already disabled when the record was taken.
Without a record (an older run, or a record you deleted) the script says so and falls back to
enabling the whole list and resetting the scales to `1.0`, which is what it used to do.

It leaves the new launcher installed (remove it from the TV's Settings → Apps if you want it gone)
and does not know about packages you disabled outside the list. To switch back later, run the
install script again; it works out which launcher to disable even when Home has fallen back to
Android's chooser. If the TV is unreachable, its own
Settings → Apps page can re-enable packages too.

</details>

## After a firmware update

An update installs over the cleaned-up system: the listed packages come back, Home goes to the
stock launcher, and new preloads appear that no list mentions. Nothing else here notices, so
compare the TV with the list you applied:

```bash
TV_HOST=192.168.1.50 tools/venv/bin/python -I tools/check.py examples/<your tv>/debloat.txt
```

It prints what drifted and exits 1 when anything did, so a weekly job on a home server or a Pi is
enough to hear about it:

```bash
0 9 * * 1 cd ~/cleanyourtv && TV_HOST=192.168.1.50 tools/venv/bin/python -I tools/check.py examples/<your tv>/debloat.txt
```

`--fix` reapplies the list to what drifted: it switches Home to the launcher the list does not
disable (pass a package as the second argument if you run a fork), then disables the listed packages
that came back, and reads the TV back to confirm both. It writes no recovery record, because the
record already holds the state from before the first run: `tools/restore.sh` still returns the TV to
stock afterwards. Home is fixed first; if no launcher is left to take it, `--fix` changes nothing
and says so.

It runs the same safety checks as `tools/disable.sh` before it disables anything, since an update is
the moment they matter again: it leaves setupwraith on while the saved Wi-Fi network belongs to it
(`FORCE_SETUPWRAITH=1` on Ethernet), leaves the packages under the HDMI/AV inputs on
(`TVINPUT_ALLOW=1` if you really want them off), leaves on the apps the TV opens its inputs and
Settings with (`HANDLERS_ALLOW=1`), and never takes Home to a vendor `FallbackHome` or a factory-test
app.
Each package it leaves on comes with the reason and exits 1, because the TV is not back to the list
until you deal with that one by hand.

Lists that also disable the updater get no updates at all. To install one, re-enable the updater on
the TV, let it update, run `check.py --fix`, and disable the updater again.

On Windows the same command is `tools\venv\Scripts\python.exe -I tools\check.py <list>`.

## When something goes wrong

The full list of things that bit us is in [docs/TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md). The
three that matter most:

- 🍎 **macOS blocks `adb connect`** ("No route to host") under Local Network privacy. `tools/tv.py` avoids the binary.
- ⚠️ **Disable the stock launcher only after the new one is installed and running**, or Home lands on a factory test app. The install script orders this for you.
- 🔁 **On Google TV the setup wizard** (`com.google.android.tungsten.setupwraith`) can take Home and crash-loop the system after the stock launcher is disabled. Disable it too, and list it in your `debloat.txt` so restore brings it back. But check `dumpsys wifi | grep -o 'cname=[^ ]*'` first: if the Wi-Fi network was saved during setup it belongs to setupwraith, and disabling it deletes the network (re-join it in Settings first; `tools/disable.sh` skips setupwraith until you do). On Google's own Chromecast and on the Google TV Streamer it also costs phone casting, see [Troubleshooting](docs/TROUBLESHOOTING.md). Stuck already? `tools/tv.py 'pm enable --user 0 com.google.android.apps.tv.launcherx'`.

## Questions people ask

<details>
<summary><b>I don't code and don't have a laptop. Can I still do this?</b></summary>

Yes. [@udhaykumarbala](https://github.com/udhaykumarbala) built a free, open-source [phone app](https://cleanyour.tv/app)
for Android that does the same cleanup over Wi-Fi: it connects to the TV, shows a plan first, saves a restore
point, and only switches apps off, so everything can be put back. It's community-built.

</details>

<details>
<summary><b>Is this safe? Can it brick the TV?</b></summary>

Nothing is flashed, rooted or deleted; packages are disabled
in the user profile. The risk is disabling something the TV leans on, which is why lists are
applied in small batches with a restore script ready. The TV's own Settings → Apps page can
re-enable packages even without the tools.

</details>

<details>
<summary><b>What about firmware updates?</b></summary>

The updater is left on in most lists, so firmware updates arrive as usual. Some lists disable the
TV's own updater, and on those a firmware update arrives only when you re-enable it. When one does
land it installs over the cleaned-up system, so a package comes back and you re-run the script: see
[After a firmware update](#after-a-firmware-update).

</details>

<details>
<summary><b>Won't the apps break?</b></summary>

Streaming apps still update through the Play Store, which is left alone.
The launcher just lists what is installed and launches it with intents that have been stable for a decade.

</details>

<details>
<summary><b>Can I do this for someone in another home?</b></summary>

Yes, over a private [Tailscale](https://tailscale.com)
link instead of the same Wi-Fi: either Tailscale on their TV, shared with you, or a computer or Apple
TV at their home routing to it. Never by opening the ADB port on their router. Step by step in
[docs/REMOTE.md](docs/REMOTE.md).

</details>

<details>
<summary><b>Isn't ADB access a security hole?</b></summary>

It is one explicit "allow this computer" tap on the TV, the
same as developing an Android app. Network ADB is plaintext on port 5555, and on firmware with
`ro.adb.secure=0` any device on the LAN gets a shell, so close it when you are done:

1. On the TV: Developer options → turn **USB debugging** and **Network/ADB debugging** off, and
   revoke the USB debugging authorisations.
2. Reboot the TV, then confirm the port is gone from your computer:
   `python3 tools/adb-closed.py <tv ip>` must say the port is closed. With `nc -vz <tv ip> 5555`,
   only "Connection refused" counts: on macOS, Local Network privacy can block `nc` and make it
   fail with "No route to host" even while the port is open.

The launcher keeps working without debugging.

</details>

<details>
<summary><b>Can it block ads inside apps too?</b></summary>

Partly, with Android's Private DNS (Android 9 and newer). Point
it at an ad-blocking resolver such as [AdGuard DNS](https://adguard-dns.io) and every app's ad and
tracker domains stop resolving, system-wide, surviving reboots. The trade-off: that resolver then
sees every DNS query the TV makes.

```bash
tools/tv.py 'settings put global private_dns_specifier dns.adguard-dns.com'
tools/tv.py 'settings put global private_dns_mode hostname'
```

It does not remove YouTube ads, which come from the same servers as the videos. When the TV reports
"No internet", the resolver is down or unreachable and switching back restores everything:
`tools/tv.py 'settings put global private_dns_mode opportunistic'`. A resolver that answers can still
break one app on its own, so if a single app fails while the rest play, see
[one app refuses to play](docs/TROUBLESHOOTING.md#one-app-refuses-to-play-while-the-rest-of-the-tv-is-online)
before you debug the app.

</details>

<details>
<summary><b>AirPlay?</b></summary>

Not from this repo. Chromecast built-in stays for Android phones. For iPhones,
[jqssun/android-airplay-server](https://github.com/jqssun/android-airplay-server) works; an install
script for it is in [PR #84](https://github.com/Gathin23/cleanyourtv/pull/84).

</details>

<details>
<summary><b>Can I just download the APK?</b></summary>

Build it with the one-line script; it takes a minute and you
control what goes on your TV. No prebuilt binaries.

</details>

## Contributing

The most useful contribution is your TV's debloat list, so the next person with that model does
not have to rediscover it. [CONTRIBUTING.md](CONTRIBUTING.md) has the format and the testing we ask for.
Contributing through a coding agent? [AGENTS.md](AGENTS.md) gives it the same rules.

| How to help | What it means |
|---|---|
| **Your TV's debloat list** | The most useful one: the next person with that model skips the discovery. |
| **Launcher and prompt** | Launcher improvements and sharper prompt wording are welcome too. |
| **Built something on top?** | A launcher, a port, a theme: add it to [SHOWCASE.md](SHOWCASE.md), where ninety-five projects already live, sorted into home screens, forks, add-ons, tools and other platforms, with an index by TV brand. |

The people whose work is in this repo:

<a href="https://github.com/Gathin23/cleanyourtv/graphs/contributors"><img src="https://contrib.rocks/image?repo=Gathin23/cleanyourtv&columns=16&max=400" alt="Contributors"></a>

<details>
<summary><strong>Star history</strong></summary>

<a href="https://star-history.com/#Gathin23/cleanyourtv&Date"><img src="https://api.star-history.com/svg?repos=Gathin23/cleanyourtv&type=Date" alt="Star history chart" width="640"></a>

</details>

## Contact

| For | Where |
|---|---|
| **Questions about your TV** | [GitHub Discussions](https://github.com/Gathin23/cleanyourtv/discussions) |
| **Press and partnerships** | [hello@itsgathin.com](mailto:hello@itsgathin.com) |
| **Security issues** | Report them privately through the [Security tab](https://github.com/Gathin23/cleanyourtv/security), see [`SECURITY.md`](SECURITY.md) |

## Legal

- CleanYourTV is an independent open-source project. It is not affiliated with, endorsed by or sponsored
  by any TV maker or platform. Brand and model names are used only to say which TV a list was tested on,
  and remain the trademarks of their owners.
- It works only through settings the TV itself offers to its owner: Developer options, ADB debugging and
  Android's own `pm disable-user`, the same switch as Settings > Apps > Disable. It does not root the TV,
  flash firmware, or bypass any lock, DRM or copy protection, and the repo contains no manufacturer
  software, firmware or APKs.
- Nothing is uninstalled, and `tools/restore.sh` puts everything back. You run it on a TV you own, at your
  own risk, and changing a TV's software may affect its warranty or support.
- Everything here is provided as is, without warranty of any kind, under the MIT licence below.

## Licence

MIT, see [`LICENSE`](LICENSE). The Outfit typeface is under the SIL Open Font License, see
`launcher/FONT-LICENSE-OFL.txt`.
