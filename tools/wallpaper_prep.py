"""Turns a folder of your own photos into wallpapers the launcher can show on a small TV.

    python tools/wallpaper_prep.py ~/Pictures/trip /tmp/my-theme/wallpapers

Every JPEG, PNG, WebP and (with pillow-heif) HEIC file in the first folder is centre-cropped to 16:9, shrunk to
1280x720, darkened so the clock, the grid and the dock stay readable, and saved as 01.jpg, 02.jpg, ... in the
second folder. The originals are never touched. Each picture is kept under --max-kb (300 by default, what
themes/README.md asks for) by lowering the JPEG quality a step at a time.

The darkening is painted into the picture, so wallpaper_dim can stay at 0 and the launcher draws no shade
layer. The numbers that worked on a Xiaomi Mi Box S (2 GB) are the defaults: brightness 0.25 and a gradient
that adds up to 60% black toward the bottom, where the dock sits.

Needs Pillow; HEIC (iPhone and many Android cameras) also needs pillow-heif:

    python -m pip install -r tools/requirements-photos.txt

Use pictures you have the right to show.
"""
import argparse
import os
import sys

EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp", ".heic", ".heif"}


def parse_size(text):
    try:
        w, h = text.lower().split("x")
        w, h = int(w), int(h)
    except ValueError:
        raise argparse.ArgumentTypeError("size must look like 1280x720")
    if w < 16 or h < 9 or w > 7680 or h > 4320:
        raise argparse.ArgumentTypeError("size must be between 16x9 and 7680x4320")
    return w, h


def source_files(folder):
    """Picture files directly inside folder, in name order (so the numbering is stable)."""
    names = sorted(n for n in os.listdir(folder) if os.path.splitext(n)[1].lower() in EXTENSIONS)
    return [os.path.join(folder, n) for n in names if os.path.isfile(os.path.join(folder, n))]


def darken(image, brightness, gradient):
    """Scale brightness, then blend toward black from top (0) to bottom (gradient)."""
    from PIL import Image, ImageEnhance
    image = ImageEnhance.Brightness(image).enhance(brightness)
    if gradient > 0:
        mask = Image.linear_gradient("L").resize(image.size).point(lambda v: int(v * gradient))
        image = Image.composite(Image.new("RGB", image.size, (0, 0, 0)), image, mask)
    return image


def prepare(path, out_path, size, brightness, gradient, quality, max_kb):
    """Writes one wallpaper and returns (size in KB, JPEG quality used)."""
    from PIL import Image, ImageOps
    with Image.open(path) as opened:
        image = ImageOps.exif_transpose(opened).convert("RGB")
    image = ImageOps.fit(image, size, Image.LANCZOS, centering=(0.5, 0.5))
    image = darken(image, brightness, gradient)
    q = quality
    while True:
        image.save(out_path, "JPEG", quality=q, optimize=True)
        kb = os.path.getsize(out_path) / 1024
        if kb <= max_kb or q <= 50:
            return round(kb), q
        q -= 6


def main(argv=None):
    parser = argparse.ArgumentParser(description="Resize and darken photos for the launcher's wallpaper folder.")
    parser.add_argument("source", help="folder with your photos")
    parser.add_argument("target", help="folder to write 01.jpg, 02.jpg, ... into (created if missing)")
    parser.add_argument("--size", type=parse_size, default=(1280, 720), help="default 1280x720")
    parser.add_argument("--brightness", type=float, default=0.25, help="0.1 to 1.0, default 0.25")
    parser.add_argument("--gradient", type=float, default=0.6, help="extra black toward the bottom, 0 to 0.9, default 0.6")
    parser.add_argument("--quality", type=int, default=84, help="starting JPEG quality, default 84")
    parser.add_argument("--max-kb", type=int, default=300, help="size limit per picture, default 300")
    args = parser.parse_args(argv)

    if not 0.1 <= args.brightness <= 1.0:
        parser.error("--brightness must be between 0.1 and 1.0")
    if not 0 <= args.gradient <= 0.9:
        parser.error("--gradient must be between 0 and 0.9")
    if not 30 <= args.quality <= 95:
        parser.error("--quality must be between 30 and 95")
    if not os.path.isdir(args.source):
        parser.error(f"{args.source} is not a folder")
    if os.path.abspath(args.source) == os.path.abspath(args.target):
        parser.error("the target must be a different folder: the originals are never overwritten")

    try:
        import PIL  # noqa: F401
    except ImportError:
        sys.exit("Pillow is missing: python -m pip install -r tools/requirements-photos.txt")
    try:
        import pillow_heif
        pillow_heif.register_heif_opener()
    except ImportError:
        pillow_heif = None

    files = source_files(args.source)
    if not files:
        sys.exit(f"no JPEG, PNG, WebP or HEIC pictures in {args.source}")
    os.makedirs(args.target, exist_ok=True)

    written = 0
    for number, path in enumerate(files, 1):
        name = os.path.basename(path)
        if os.path.splitext(name)[1].lower() in {".heic", ".heif"} and pillow_heif is None:
            print(f"skipped {name}: HEIC needs pillow-heif (python -m pip install -r tools/requirements-photos.txt)")
            continue
        out_path = os.path.join(args.target, "%02d.jpg" % number)
        try:
            kb, quality = prepare(path, out_path, args.size, args.brightness, args.gradient, args.quality, args.max_kb)
        except Exception as error:  # a corrupt picture must not stop the rest
            print(f"skipped {name}: {error}")
            continue
        written += 1
        note = "" if kb <= args.max_kb else f"  (over {args.max_kb} KB even at quality {quality})"
        print(f"{os.path.basename(out_path)}  <- {name}  {kb} KB, quality {quality}{note}")
    print(f"{written} of {len(files)} pictures written to {args.target}")
    return 0 if written else 1


if __name__ == "__main__":
    sys.exit(main())
