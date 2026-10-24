"""tools/wallpaper_prep.py: size, darkening, numbering, the size limit and the refusals."""
import importlib.util
import os
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
SPEC = importlib.util.spec_from_file_location("wallpaper_prep", ROOT / "tools" / "wallpaper_prep.py")
prep = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(prep)

try:
    from PIL import Image
except ImportError:  # the tool needs Pillow; the rest of the repo does not
    Image = None


def make_photo(path, size=(400, 300), colour=(220, 200, 180)):
    Image.new("RGB", size, colour).save(path)


class ParseSizeTest(unittest.TestCase):
    def test_accepts_width_x_height(self):
        self.assertEqual(prep.parse_size("1280x720"), (1280, 720))
        self.assertEqual(prep.parse_size("1920X1080"), (1920, 1080))

    def test_refuses_nonsense(self):
        for bad in ("1280", "axb", "10x5", "99999x720", ""):
            with self.subTest(bad=bad):
                with self.assertRaises(Exception):
                    prep.parse_size(bad)


class SourceFilesTest(unittest.TestCase):
    def test_only_pictures_in_name_order(self):
        with tempfile.TemporaryDirectory() as d:
            for name in ("b.JPG", "a.png", "notes.txt", "c.webp", "d.heic"):
                Path(d, name).write_bytes(b"x")
            os.mkdir(Path(d, "sub.jpg"))
            names = [os.path.basename(p) for p in prep.source_files(d)]
            self.assertEqual(names, ["a.png", "b.JPG", "c.webp", "d.heic"])


@unittest.skipIf(Image is None, "Pillow not installed")
class PrepareTest(unittest.TestCase):
    def test_output_is_1280x720_jpeg_and_darker(self):
        with tempfile.TemporaryDirectory() as d:
            src, out = Path(d, "in.png"), Path(d, "out.jpg")
            make_photo(src)
            kb, quality = prep.prepare(str(src), str(out), (1280, 720), 0.25, 0.6, 84, 300)
            with Image.open(out) as result:
                self.assertEqual(result.size, (1280, 720))
                self.assertEqual(result.format, "JPEG")
                top = result.getpixel((640, 5))
                bottom = result.getpixel((640, 715))
            self.assertLess(sum(top), sum((220, 200, 180)) * 0.35, "top should be much darker than the source")
            self.assertLess(sum(bottom), sum(top), "bottom should be darker than top")
            self.assertLessEqual(kb, 300)

    def test_quality_drops_until_the_limit_fits(self):
        with tempfile.TemporaryDirectory() as d:
            src, out = Path(d, "noise.png"), Path(d, "out.jpg")
            Image.effect_noise((1280, 720), 80).convert("RGB").save(src)
            full_kb, full_quality = prep.prepare(str(src), str(out), (1280, 720), 1.0, 0.0, 90, 10_000)
            self.assertEqual(full_quality, 90, "a limit it already meets keeps the starting quality")
            limit = int(full_kb * 0.8)
            kb, quality = prep.prepare(str(src), str(out), (1280, 720), 1.0, 0.0, 90, limit)
            self.assertLess(quality, 90)
            self.assertLessEqual(kb, limit)

    def test_an_impossible_limit_stops_at_the_quality_floor(self):
        with tempfile.TemporaryDirectory() as d:
            src, out = Path(d, "noise.png"), Path(d, "out.jpg")
            Image.effect_noise((1280, 720), 80).convert("RGB").save(src)
            kb, quality = prep.prepare(str(src), str(out), (1280, 720), 1.0, 0.0, 90, 5)
            self.assertLessEqual(quality, 50)
            self.assertGreater(kb, 5, "it reports the size it could reach instead of looping forever")

    def test_originals_are_never_overwritten_and_target_must_differ(self):
        with tempfile.TemporaryDirectory() as d:
            make_photo(Path(d, "a.png"))
            with self.assertRaises(SystemExit):
                prep.main([d, d])

    def test_main_numbers_the_outputs_and_skips_a_corrupt_file(self):
        with tempfile.TemporaryDirectory() as d:
            src, out = Path(d, "src"), Path(d, "out")
            src.mkdir()
            make_photo(src / "a.png")
            (src / "b.jpg").write_bytes(b"not a picture")
            make_photo(src / "c.png", colour=(10, 20, 30))
            self.assertEqual(prep.main([str(src), str(out)]), 0)
            self.assertEqual(sorted(os.listdir(out)), ["01.jpg", "03.jpg"])

    def test_empty_folder_exits_with_a_message(self):
        with tempfile.TemporaryDirectory() as d:
            src = Path(d, "src")
            src.mkdir()
            with self.assertRaises(SystemExit):
                prep.main([str(src), str(Path(d, "out"))])


if __name__ == "__main__":
    unittest.main()
