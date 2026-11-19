import sys
from PIL import Image, ImageDraw, ImageFont, ImageFilter

W, H = 1024, 500
SRC = "/home/gaston/StudioProjects/MiLinterna/Imagenes/screenshots-2.2.0/luzen_screenshot_1_{}.png"
OUT = "/home/gaston/StudioProjects/MiLinterna/Imagenes/feature-graphic-2.2.0/luzen_feature_{}.png"
BOLD = "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
REG = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"

TEXT = {
    "en": ("Dimmable flashlight", "& screen dimmer", "Right in your notification bar"),
    "es": ("Linterna regulable", "y atenuador de pantalla", "Desde la barra de notificaciones"),
    "pt": ("Lanterna regulável", "e redutor de brilho", "Direto na barra de notificação"),
}


def gradient():
    top, bot = (14, 20, 38), (30, 40, 78)
    g = Image.new("RGB", (W, H))
    d = ImageDraw.Draw(g)
    for y in range(H):
        t = y / (H - 1)
        d.line([(0, y), (W, y)], fill=tuple(int(a + (b - a) * t) for a, b in zip(top, bot)))
    return g


def make(lang):
    im = gradient().convert("RGBA")
    # warm glow behind the phone
    glow = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ImageDraw.Draw(glow).ellipse([640, 40, 1000, 460], fill=(255, 196, 64, 110))
    im = Image.alpha_composite(im, glow.filter(ImageFilter.GaussianBlur(70)))

    # phone card: app screenshot, rounded, with a light border
    shot = Image.open(SRC.format(lang)).convert("RGB")
    ph = 440
    pw = int(shot.width * ph / shot.height)
    shot = shot.resize((pw, ph), Image.LANCZOS)
    mask = Image.new("L", (pw, ph), 0)
    ImageDraw.Draw(mask).rounded_rectangle([0, 0, pw - 1, ph - 1], radius=26, fill=255)
    px, py = 820 - pw // 2, (H - ph) // 2
    frame = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ImageDraw.Draw(frame).rounded_rectangle([px - 6, py - 6, px + pw + 5, py + ph + 5], radius=31, fill=(230, 233, 240, 255))
    im = Image.alpha_composite(im, frame)
    im.paste(shot, (px, py), mask)

    d = ImageDraw.Draw(im)
    l1, l2, l3 = TEXT[lang]
    x = 64
    d.text((x, 70), "Luzen", font=ImageFont.truetype(BOLD, 104), fill=(255, 255, 255))
    d.text((x, 205), l1, font=ImageFont.truetype(BOLD, 44), fill=(255, 205, 80))
    d.text((x, 258), l2, font=ImageFont.truetype(BOLD, 36), fill=(255, 255, 255))
    d.text((x, 330), l3, font=ImageFont.truetype(REG, 27), fill=(190, 200, 225))
    # check the text never runs into the phone
    for txt, f in ((l1, ImageFont.truetype(BOLD, 44)), (l2, ImageFont.truetype(BOLD, 36)), (l3, ImageFont.truetype(REG, 27))):
        right = x + d.textlength(txt, font=f)
        assert right < px - 20, (lang, txt, right, px)
    im.convert("RGB").save(OUT.format(lang), optimize=True)
    print(OUT.format(lang))


import os
os.makedirs(os.path.dirname(OUT.format("x")), exist_ok=True)
for lang in sys.argv[1:] or ["en", "es", "pt"]:
    make(lang)
