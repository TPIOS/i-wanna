"""从 fgo.wiki 下载职介统计页所需的满破卡面和金色职介图标。"""

from __future__ import annotations

import argparse
from concurrent.futures import ThreadPoolExecutor, as_completed
import html as html_module
from io import BytesIO
import json
from pathlib import Path
import re
import subprocess
from urllib.parse import quote, unquote, urlparse

from PIL import Image


BASE_URL = "https://fgo.wiki/w/"
USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) FGO-statistics-slide-builder/1.0"
CLASS_ICON_FILE_NAMES = {
    "Saber": "Saber",
    "Archer": "Archer",
    "Lancer": "Lancer",
    "Rider": "Rider",
    "Caster": "Caster",
    "Assassin": "Assassin",
    "Berserker": "Berserker",
    "Ruler": "Ruler",
    "Avenger": "Avenger",
    "MoonCancer": "MoonCancer",
    "AlterEgo": "Alterego",
    "Foreigner": "Foreigner",
    "Pretender": "Pretender",
    "Beast": "Beast",
}


def safe_name(value: str) -> str:
    value = re.sub(r'[<>:"/\\|?*\x00-\x1f]', "_", value).strip().rstrip(". ")
    return value or "unnamed"


def fetch(url: str) -> bytes:
    # fgo.wiki 对 Windows 的 urllib 连接偶有长时间等待；curl 的重试与超时更稳定。
    command = [
        "curl.exe",
        "--max-time", "30",
        "--retry", "2",
        "--retry-delay", "1",
        "--retry-all-errors",
        "--compressed",
        "-sS",
        "-L",
        "-A", USER_AGENT,
        url,
    ]
    try:
        completed = subprocess.run(command, capture_output=True, timeout=100, check=False)
    except subprocess.TimeoutExpired as error:
        raise RuntimeError(f"下载超时：{url}") from error
    if completed.returncode != 0:
        detail = completed.stderr.decode("utf-8", errors="replace").strip()
        raise RuntimeError(f"下载失败：{url}；curl={completed.returncode}；{detail}")
    if not completed.stdout:
        raise RuntimeError(f"下载结果为空：{url}")
    return completed.stdout


def original_from_srcset(srcset: str) -> str:
    candidates = []
    for item in html_module.unescape(srcset).split(","):
        item = item.strip()
        if not item:
            continue
        candidates.append(item.rsplit(" ", 1)[0])
    if not candidates:
        raise ValueError(f"无法解析 srcset：{srcset}")
    return candidates[-1]


def servant_card_url(page_html: str, servant: str) -> str:
    # 第一组卡面选择器中 item-3 对应卡面4，即满破卡面。
    marker = page_html.find("卡面为游戏内原始资源")
    graph_start = page_html.find('<div class="graphpicker"', marker)
    graph_end = page_html.find('<button class="graphpicker-prev"', graph_start)
    if marker < 0 or graph_start < 0 or graph_end < 0:
        raise ValueError(f"{servant} 页面没有找到从者卡面选择器")
    card_graph = page_html[graph_start:graph_end]
    match = re.search(
        r'class="picker-item-3".*?<img\b[^>]*\bsrcset="([^"]+)"',
        card_graph,
        flags=re.DOTALL,
    )
    if not match:
        raise ValueError(f"{servant} 页面没有找到 picker-item-3 满破卡面")
    url = original_from_srcset(match.group(1))
    return url


def class_icon_urls(page_html: str) -> dict[str, str]:
    result: dict[str, str] = {}
    for class_name, file_name in CLASS_ICON_FILE_NAMES.items():
        encoded = quote(f"金卡{file_name}.png")
        matches = re.findall(
            rf'<img\b[^>]*\bsrcset="([^"]*{re.escape(encoded)}[^"]*)"',
            page_html,
            flags=re.IGNORECASE,
        )
        if not matches:
            raise ValueError(f"Saber 职阶页面没有找到金卡{file_name}.png")
        result[class_name] = original_from_srcset(matches[-1])
    return result


def validate_and_write(data: bytes, destination: Path, kind: str) -> tuple[int, int]:
    with Image.open(BytesIO(data)) as image:
        image.verify()
    with Image.open(BytesIO(data)) as image:
        width, height = image.size
        if kind == "servant" and not (width >= 400 and height >= 500 and height > width):
            raise ValueError(f"满破卡面尺寸异常：{width}x{height}")
        if kind == "icon" and not (width == height and width >= 60):
            raise ValueError(f"职阶图标尺寸异常：{width}x{height}")
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_bytes(data)
    return width, height


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("statistics", type=Path)
    parser.add_argument("--asset-root", type=Path, default=Path("职介统计素材"))
    parser.add_argument("--output", type=Path, default=Path("职介统计_含素材.json"))
    parser.add_argument("--workers", type=int, default=6)
    args = parser.parse_args()

    statistics = json.loads(args.statistics.read_text(encoding="utf-8-sig"))
    servant_names = sorted(
        {
            entry[key]["name"]
            for entry in statistics["classes"]
            for key in ("highest", "lowest")
        }
    )

    print(f"读取 {len(servant_names)} 名从者的 fgo.wiki 页面……")
    page_results: dict[str, tuple[str, str]] = {}

    def fetch_servant_page(name: str) -> tuple[str, str, str]:
        page_url = BASE_URL + quote(name, safe="")
        page_html = fetch(page_url).decode("utf-8", errors="replace")
        return name, page_url, servant_card_url(page_html, name)

    with ThreadPoolExecutor(max_workers=args.workers) as executor:
        futures = {executor.submit(fetch_servant_page, name): name for name in servant_names}
        for index, future in enumerate(as_completed(futures), start=1):
            name, page_url, image_url = future.result()
            page_results[name] = (page_url, image_url)
            print(f"页面 [{index:02d}/{len(servant_names):02d}] {name}")

    print("读取 fgo.wiki 金色职阶图标地址……")
    saber_html = fetch(BASE_URL + "Saber").decode("utf-8", errors="replace")
    icon_urls = class_icon_urls(saber_html)

    downloads: list[tuple[str, str, Path, str]] = []
    for name, (_, image_url) in page_results.items():
        downloads.append(
            ("servant", name, args.asset_root / "从者满破" / f"{safe_name(name)}.png", image_url)
        )
    for class_name, image_url in icon_urls.items():
        downloads.append(
            ("icon", class_name, args.asset_root / "职阶图标" / f"{class_name}.png", image_url)
        )

    asset_results: dict[tuple[str, str], dict] = {}

    def download_asset(item: tuple[str, str, Path, str]) -> tuple[str, str, Path, str, int, int]:
        kind, name, destination, image_url = item
        width, height = validate_and_write(fetch(image_url), destination, kind)
        return kind, name, destination, image_url, width, height

    print(f"下载并验证 {len(downloads)} 张图片……")
    with ThreadPoolExecutor(max_workers=args.workers) as executor:
        futures = {executor.submit(download_asset, item): item for item in downloads}
        for index, future in enumerate(as_completed(futures), start=1):
            kind, name, destination, image_url, width, height = future.result()
            asset_results[(kind, name)] = {
                "path": str(destination.resolve()),
                "source_url": image_url,
                "width": width,
                "height": height,
            }
            print(f"素材 [{index:02d}/{len(downloads):02d}] {kind} / {name} / {width}x{height}")

    for entry in statistics["classes"]:
        class_name = entry["class"]
        entry["icon"] = asset_results[("icon", class_name)]
        for key in ("highest", "lowest"):
            name = entry[key]["name"]
            entry[key]["wiki_page"] = page_results[name][0]
            entry[key]["image"] = asset_results[("servant", name)]

    statistics["asset_source"] = "fgo.wiki"
    args.output.write_text(json.dumps(statistics, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"已写入：{args.output.resolve()}")


if __name__ == "__main__":
    main()
