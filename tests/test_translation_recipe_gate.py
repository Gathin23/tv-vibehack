"""The translation recipe names the release APK's size gate, which a new language can fail.

`docs/workflows/translation.md` is the only page a translator reads. Its step 1 asks for every string
in `values/strings.xml`, so a full language is by far the largest thing a translation pull request
adds to `launcher/app/src/main/res/` - and the release APK has a hard budget:
`.github/workflows/ci.yml`'s `launcher` job, which runs on every pull request in `launcher/`, measures
`launcher/app/build/outputs/apk/release/app-release.apk` and fails the build at 524,288 B. The launcher
ships just under that (README.md quotes about 500 KB), and #1271 is a full Korean translation that did
not fit. Until now the page said nothing about it, so a translator could follow the recipe exactly and
land a red build at a step the page never mentions.

The numbers are not copied into the test: the APK path, the limit and the command it is measured with
are read out of `ci.yml`, and the job is checked to still run on pull requests rather than only on
`main` - a gate moved behind `if: github.event_name == 'push'` would make the page's advice wrong, so
that fails here instead of quietly letting the recipe drift.
"""
from pathlib import Path
import re
import unittest


ROOT = Path(__file__).resolve().parents[1]
PAGE = ROOT / "docs/workflows/translation.md"
CI = ROOT / ".github/workflows/ci.yml"

# The launcher job's size check, in the form ci.yml writes it:
#   size=$(stat -c %s launcher/app/build/outputs/apk/release/app-release.apk)
#   test "$size" -lt 524288
STAT_LINE = re.compile(r"size=\$\(stat -c %s ([^)\s]+)\)")
LIMIT_LINE = re.compile(r'test "\$size" -lt (\d+)')
JOB_HEAD = re.compile(r"^  ([a-z][a-z0-9-]*):\s*$", re.M)


def job_block(ci_text, name):
    """The lines of one job in ci.yml, so a condition on that job can be read."""
    head = None
    for m in JOB_HEAD.finditer(ci_text):
        if head is not None:
            return ci_text[head:m.start()]
        if m.group(1) == name:
            head = m.start()
    return ci_text[head:] if head is not None else ""


def gate(ci_text):
    """(APK path, limit in bytes) from ci.yml; raises when the size check is gone."""
    stat = STAT_LINE.search(ci_text)
    limit = LIMIT_LINE.search(ci_text)
    if not stat or not limit:
        raise AssertionError("ci.yml no longer measures the release APK against a size limit: "
                             "the recipe points a translator at a check that does not run")
    return stat.group(1), int(limit.group(1))


def runs_on_pull_requests(ci_text):
    """False when the launcher job carries an `if:` that would keep it off a pull request."""
    block = job_block(ci_text, "launcher")
    if not block:
        raise AssertionError("ci.yml has no `launcher:` job, so the gate the recipe names is not run")
    for line in block.splitlines():
        if re.match(r"^    if:", line):
            return "pull_request" in line
    return True


def steps(text):
    """[(number, text)] for the recipe's numbered steps, wrapped lines joined; prose is not a step."""
    out = []
    for line in text.splitlines():
        m = re.match(r"^(\d+)\.\s+(.*)$", line)
        if m:
            out.append([int(m.group(1)), m.group(2).strip()])
        elif not line.strip():
            continue
        elif line.startswith((" ", "\t")) and out:
            out[-1][1] += " " + line.strip()
        else:
            out.append([0, line.strip()])  # a paragraph: kept out of the steps below
    return [(n, t) for n, t in out if n]


def size_forms(limit):
    """How a page may write the limit: the byte count, the same with a comma, and the KB it is."""
    return {str(limit), f"{limit:,}", f"{limit // 1024} KB", f"{limit // 1024}KB"}


def gate_steps(page_steps, apk, limit):
    """[(number, [what the step still misses])] for the steps that name the release APK."""
    found = []
    for number, text in page_steps:
        if apk not in text:
            continue
        missing = []
        if not any(form in text for form in size_forms(limit)):
            missing.append("the limit")
        for command in ("stat -c %s", "stat -f %z"):
            if command not in text:
                missing.append(command)
        found.append((number, missing))
    return found


class TranslationRecipeGateTest(unittest.TestCase):
    def test_the_size_gate_still_runs_on_a_pull_request(self):
        ci = CI.read_text(encoding="utf-8")
        apk, limit = gate(ci)
        self.assertEqual("launcher/app/build/outputs/apk/release/app-release.apk", apk)
        self.assertEqual(524288, limit)
        self.assertTrue(runs_on_pull_requests(ci),
                        "ci.yml's launcher job no longer runs on a pull request, so a translation PR "
                        "would not meet the gate docs/workflows/translation.md now points at")

    def test_the_recipe_states_the_gate_in_one_step(self):
        ci = CI.read_text(encoding="utf-8")
        apk, limit = gate(ci)
        found = gate_steps(steps(PAGE.read_text(encoding="utf-8")), apk, limit)
        self.assertEqual(1, len(found),
                         f"docs/workflows/translation.md should name {apk} and the {limit} B gate in "
                         f"exactly one numbered step, found {found}")
        number, missing = found[0]
        self.assertEqual([], missing,
                         f"step {number} of docs/workflows/translation.md still has to give {missing}")

    def test_the_recipe_keeps_its_numbering(self):
        read = steps(PAGE.read_text(encoding="utf-8"))
        self.assertGreaterEqual(len(read), 8, "read too few steps to prove anything")
        self.assertEqual(list(range(1, len(read) + 1)), [n for n, _ in read],
                         "the recipe's steps are not numbered 1..N, so a renumbering lost a step")


class TranslationRecipeGateReaderTest(unittest.TestCase):
    """The readers above, against a page and a ci.yml written here, so none can pass by reading nothing."""

    def test_a_gate_in_prose_is_not_a_step(self):
        page = ("# Workflow\n\nBuild it and check the size gate first.\n\n"
                "1. Translate every string.\n2. Push.\n")
        self.assertEqual([], gate_steps(steps(page), "launcher/app.apk", 524288))

    def test_a_wrapped_step_is_joined(self):
        page = ("1. Translate every string.\n"
                "2. Check the gate: `launcher/app.apk` has to stay\n"
                "   under 524,288 B, with `stat -c %s` on Linux or `stat -f %z` on macOS.\n"
                "3. Push.\n")
        self.assertEqual([(2, [])], gate_steps(steps(page), "launcher/app.apk", 524288))

    def test_a_step_that_names_the_apk_but_not_the_limit_is_reported(self):
        page = "1. Build `launcher/app.apk` and check the gate.\n"
        self.assertEqual([(1, ["the limit", "stat -c %s", "stat -f %z"])],
                         gate_steps(steps(page), "launcher/app.apk", 524288))

    def test_the_limit_may_be_written_in_bytes_or_kb(self):
        for written in ("524288", "524,288", "512 KB", "512KB"):
            with self.subTest(written=written):
                page = (f"1. `launcher/app.apk` under {written}, `stat -c %s` or "
                        f"`stat -f %z`.\n")
                self.assertEqual([(1, [])], gate_steps(steps(page), "launcher/app.apk", 524288))

    def test_a_page_without_steps_reads_as_none(self):
        self.assertEqual([], steps("No numbered steps here.\n\n- a bullet\n"))

    def test_a_main_only_gate_is_not_a_pull_request_gate(self):
        push_only = ("jobs:\n  launcher:\n    name: Launcher\n    if: github.event_name == 'push'\n"
                     "    steps:\n      - run: |\n"
                     "          size=$(stat -c %s launcher/app/build/outputs/apk/release/app-release.apk)\n"
                     "          test \"$size\" -lt 524288\n")
        self.assertFalse(runs_on_pull_requests(push_only))
        always = push_only.replace("    if: github.event_name == 'push'\n", "")
        self.assertTrue(runs_on_pull_requests(always))

    def test_the_gate_is_read_out_of_ci_yml(self):
        ci = ("jobs:\n  launcher:\n    steps:\n      - run: |\n"
              "          size=$(stat -c %s launcher/app/build/outputs/apk/release/app-release.apk)\n"
              "          test \"$size\" -lt 409600\n")
        self.assertEqual(("launcher/app/build/outputs/apk/release/app-release.apk", 409600), gate(ci))

    def test_a_ci_yml_without_the_check_is_refused(self):
        with self.assertRaises(AssertionError):
            gate("jobs:\n  launcher:\n    steps:\n      - run: ./gradlew assembleRelease\n")


if __name__ == "__main__":
    unittest.main()
