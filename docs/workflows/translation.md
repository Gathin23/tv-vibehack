# Workflow: Add or fix a launcher translation

**When to use this:** a new launcher language, or strings missing from existing ones.

1. Create `launcher/app/src/main/res/values-<qualifier>/strings.xml` and translate every string in
   `launcher/app/src/main/res/values/strings.xml`, skipping the ones marked `translatable="false"`. Keep
   placeholders (`%1$s`, `%d`) and XML escapes intact. The folder suffix is the qualifier Android reads:
   `de` for a language, `zh-rCN` for a language with a region, or the BCP-47 form the tree also uses,
   `b+sr+Latn`, for a language with a script (`values-sr-Latn` is not a folder it reads).
2. A language may also need one of the other files `values/` carries. Add it only when `values/` has the
   same file, and only the names that file defines: `tests/test_translation_recipe_files.py` holds both.
   - `bools.xml`: `date_single_line` false when the language's weekday and month names do not fit on one
     line (German, Spanish, Portuguese, Russian and Ukrainian set it), and `rtl_app_grid`/`rtl_menu_bar`
     true for a right-to-left language (`values-iw`).
   - `fonts.xml`: point `font_body`, `font_regular` and the other `font_*` at the system families when the
     bundled Outfit has no glyphs for the language's letters (`values-vi`, #833).
   - `lunar.xml`: the lunar date format, the leap-month wording and the stem and branch names, for a
     language that names them itself (`values-zh-rCN`, `values-vi`). Keep `lunar_stems` at ten items and
     `lunar_branches` at twelve: `Lunar.java` indexes both by position, so a short list crashes the
     launcher on a TV running that language and nothing anywhere else.
3. Add the locale to `launcher/app/src/main/res/xml/locales_config.xml`, in the tag form: a language
   folder that is not registered there cannot be chosen for the launcher alone on Android 13+.
4. Add the language to the Languages list in `README.md`.
5. To fix a missing string in existing locales, add it to every `values-*` folder you can, in the same order as
   the default `strings.xml`. A missing string falls back to English (`launcher/app/lint.xml`), but a new switch
   needs its strings in every locale.
6. Moving hard-coded text into resources is welcome; do not bundle unrelated build changes with it.
7. Check the size gate before you push. A language's strings ship in the release APK, and that APK has to
   stay under 524,288 B (512 KB): `launcher/app/build/outputs/apk/release/app-release.apk`, measured with
   `stat -c %s` on Linux or `stat -f %z` on macOS, and checked by the "Launcher build and unit tests" job
   on every pull request. `README.md` quotes the launcher at about 500 KB, so a full language can be the
   difference between green and a failed build. A language that does not fit is a maintainer decision, not
   something to land by dropping strings ([#1271](https://github.com/Gathin23/cleanyourtv/issues/1271)).
8. Build the launcher, set the TV's language (Android 13+: `tools/tv.py 'cmd locale set-app-locales com.gathin.tvhome --locales <tag>'`)
   and attach a screenshot. Right-to-left languages mirror the layout like Hebrew; `tests/test_hebrew_resources.py`
   checks that `layout-iw` keeps every view.

Then run the tests: `python3 -m unittest discover -s tests -v`.
