"""docs/devices/xiaomi-mitv-mftr0-debloat-home.md carries the OS3.0 report of #1355.

The note was written on `OS2.0.23.0.RFTMATV` / Android 11 and its Home section is the
answer a reader of this model acts on: install under the allowlist identity, disable
PatchWall, HOME follows. #1355 is a unit of the same model after a silent OTA to
`OS3.0.28.0.UFTMATV` / Android 14, where both standard commands report success and Home
stays on PatchWall, and where disabling PatchWall left HOME with no window at all. A reader
of the OS2 half has no way to know the OTA exists, so the OS3 section is required, and the
intro has to link it.

What this test pins is what a reader acts on: the model and both build strings, the commands
that report success, the preferred-activity record that explains them, the command Android 14
no longer has, the second unit's opposite account, and that nothing in the note hands out an
uninstall step - this repository disables and re-enables, and `pm uninstall` is the one thing
a note here must never suggest. The prose between those identifiers is left free.

The account itself is not verified here: it is one owner's session, recorded as such.

Run from the repository root: python3 -m unittest discover -s tests -v
"""
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
NOTE = ROOT / "docs" / "devices" / "xiaomi-mitv-mftr0-debloat-home.md"

OS2_HEADING = "## Home: the allowlist trampoline works on OS2 too"
OS3_HEADING = "## After the OS3.0.28 OTA"

# What the report of #1355 actually ran and read, as it appears in the note.
COMMANDS = (
    "cmd role add-role-holder --user 0 android.app.role.HOME",
    "cmd package set-home-activity --user 0",
    "cmd package resolve-activity --brief",
    "cmd package query-activities --brief",
    "dumpsys package com.mitv.tvhome",
)
RECORD = (
    "PreferredActivities",
    "mAlways=true",
    "mitv.intent.action.PW_CUSTOMIZATION",
    "com.mitv.tvhome/.MainActivityUserMode",
    "com.xiaomi.mitv.settings/.entry.FallbackHome",
)
SECOND_UNIT = ("MT5879", "L55MA-SPL", "com.spocky.projengmenu")
# This repo's way onto CN Xiaomi Home, named so the OS3 half still points a reader at it.
ALLOWLIST = "com.ts.launcher"


def section(text, heading):
    """The lines below the line starting with `heading`, up to the next top-level heading."""
    out, inside = [], False
    for line in text.splitlines():
        if not inside:
            inside = line.startswith(heading)
            continue
        if line.startswith("## "):
            break
        out.append(line)
    return "\n".join(out)


def slug(heading_text):
    """GitHub's anchor for a heading: lower case, punctuation dropped, spaces to hyphens."""
    text = re.sub(r"!?\[([^\]]*)\]\([^)]*\)", r"\1", heading_text).strip().lower()
    return re.sub(r"[^\w\- ]", "", text, flags=re.U).replace(" ", "-")


class Os3NoteTest(unittest.TestCase):
    def setUp(self):
        self.text = NOTE.read_text(encoding="utf-8")
        self.os3 = section(self.text, OS3_HEADING)
        self.os2 = section(self.text, OS2_HEADING)

    def test_the_section_is_there_and_not_a_stub(self):
        self.assertGreaterEqual(len(self.os3.splitlines()), 40, "the OS3.0 section was not read")

    def test_the_note_keeps_the_build_it_was_written_on(self):
        # The OS2 half is the part a reader of this model already relies on: the OS3 section
        # has to be an addition to it, not a replacement.
        self.assertIn("OS2.0.23.0.RFTMATV", self.text)
        self.assertIn("Android 11", self.text)
        self.assertIn(ALLOWLIST, self.os2)

    def test_the_section_names_the_build_that_changed_it(self):
        self.assertIn("OS3.0.28.0.UFTMATV", self.os3)
        self.assertIn("Android 14", self.os3)

    def test_the_section_names_the_commands_that_report_success(self):
        for command in COMMANDS:
            with self.subTest(command=command):
                self.assertIn(command, self.os3)

    def test_the_section_names_the_record_that_outranks_them(self):
        for token in RECORD:
            with self.subTest(token=token):
                self.assertIn(token, self.os3)

    def test_the_section_records_the_command_android_14_no_longer_has(self):
        self.assertIn("clear-package-preferred-activities", self.os3)
        self.assertIn("Unknown command", self.os3)

    def test_the_section_still_points_at_the_allowlist_route(self):
        # The OS3 unit did not use it, so the note has to keep the route in view rather than
        # let a reader conclude that CN Xiaomi Home is finished.
        self.assertIn(ALLOWLIST, self.os3)

    def test_the_section_records_the_second_unit(self):
        for token in SECOND_UNIT:
            with self.subTest(token=token):
                self.assertIn(token, self.os3)

    def test_the_note_offers_no_uninstall_step(self):
        self.assertNotRegex(self.text, r"\bpm\s+uninstall\b",
                            "this note must not hand out an uninstall: the tools disable and re-enable")

    def test_the_os2_half_links_the_os3_section(self):
        # A reader who stops at the OS2 Home section has to be able to find out about the OTA.
        heading = next(line for line in self.text.splitlines() if line.startswith(OS3_HEADING))
        anchor = "#" + slug(heading[3:])
        self.assertIn(f"]({anchor})", self.text, "the OS2 half does not link the OS3 section")


class SectionReaderTest(unittest.TestCase):
    """The splitter above has to be able to fail on pages written here."""

    def test_a_page_without_the_heading_reads_as_empty(self):
        self.assertEqual(section("## Other\nbody\n", OS3_HEADING), "")

    def test_a_deeper_heading_stays_inside_the_section(self):
        text = "## After the OS3.0.28 OTA: x\nfirst\n### a sub-heading\nsecond\n## Next\nafter\n"
        self.assertEqual(section(text, OS3_HEADING), "first\n### a sub-heading\nsecond")

    def test_the_next_top_level_heading_ends_the_section(self):
        text = "intro\n## After the OS3.0.28 OTA: x\ninside\n## Another\noutside\n"
        self.assertEqual(section(text, OS3_HEADING), "inside")

    def test_the_heading_itself_is_not_part_of_the_section(self):
        self.assertNotIn(OS3_HEADING, section(f"{OS3_HEADING}\nx\n", OS3_HEADING))

    def test_a_slug_drops_punctuation_and_keeps_digits(self):
        self.assertEqual(slug("After the OS3.0.28 OTA: Home does not move"),
                         "after-the-os3028-ota-home-does-not-move")


if __name__ == "__main__":
    unittest.main()
