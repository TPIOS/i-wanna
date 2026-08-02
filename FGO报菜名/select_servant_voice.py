#!/usr/bin/env python3
"""为每名从者选择时长最接近目标秒数的一条 MP3。"""

from __future__ import annotations

import argparse
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime
import json
import os
from pathlib import Path
import subprocess
import sys
from typing import Iterable


CLASS_ORDER = (
    "Saber",
    "Archer",
    "Lancer",
    "Rider",
    "Caster",
    "Berserker",
    "Assassin",
    "Ruler",
    "Avenger",
    "MoonCancer",
    "AlterEgo",
    "Foreigner",
    "Pretender",
    "Beast",
)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--voice-root", type=Path, default=Path("从者语音"))
    parser.add_argument("--image-root", type=Path, default=Path("提取结果_vFinal2"))
    parser.add_argument("--ffprobe", type=Path, required=True)
    parser.add_argument("--output", type=Path, default=Path("从者语音选择.json"))
    parser.add_argument("--target-seconds", type=float, default=6.0)
    parser.add_argument("--workers", type=int, default=min(12, (os.cpu_count() or 4)))
    return parser.parse_args()


def configure_console() -> None:
    for stream in (sys.stdout, sys.stderr):
        reconfigure = getattr(stream, "reconfigure", None)
        if reconfigure is not None:
            reconfigure(encoding="utf-8", errors="replace")


def servant_directories(image_root: Path) -> Iterable[tuple[str, Path]]:
    known = set(CLASS_ORDER)
    extra_classes = sorted(
        path.name
        for path in image_root.iterdir()
        if path.is_dir() and path.name not in known
    )
    for class_name in (*CLASS_ORDER, *extra_classes):
        class_dir = image_root / class_name
        if not class_dir.is_dir():
            continue
        for servant_dir in class_dir.iterdir():
            if servant_dir.is_dir():
                yield class_name, servant_dir


def probe_duration(ffprobe: Path, audio_file: Path) -> float:
    creationflags = subprocess.CREATE_NO_WINDOW if os.name == "nt" else 0
    completed = subprocess.run(
        [
            str(ffprobe),
            "-v",
            "error",
            "-show_entries",
            "format=duration",
            "-of",
            "default=noprint_wrappers=1:nokey=1",
            "--",
            str(audio_file),
        ],
        check=True,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        timeout=30,
        creationflags=creationflags,
    )
    value = completed.stdout.strip().splitlines()
    if not value:
        raise ValueError("ffprobe 没有返回时长")
    duration = float(value[-1])
    if duration <= 0:
        raise ValueError(f"无效时长：{duration}")
    return duration


def main() -> int:
    configure_console()
    args = parse_args()
    voice_root = args.voice_root.resolve()
    image_root = args.image_root.resolve()
    ffprobe = args.ffprobe.resolve()
    output = args.output.resolve()

    for required in (voice_root, image_root, ffprobe):
        if not required.exists():
            raise FileNotFoundError(required)

    servant_rows: list[dict[str, object]] = []
    tasks: list[tuple[str, str, Path]] = []
    seen_names: dict[str, str] = {}
    for class_name, servant_dir in servant_directories(image_root):
        servant_name = servant_dir.name
        previous_class = seen_names.get(servant_name)
        if previous_class is not None:
            raise ValueError(
                f"从者名在多个职介重复，无法只按页面标题映射："
                f"{servant_name} ({previous_class}, {class_name})"
            )
        seen_names[servant_name] = class_name

        voice_dir = voice_root / class_name / servant_name
        clips = sorted(voice_dir.glob("*.mp3"), key=lambda path: path.name.casefold())
        if not clips:
            raise FileNotFoundError(f"没有 MP3：{voice_dir}")
        servant_rows.append(
            {
                "class": class_name,
                "servant": servant_name,
                "voice_dir": str(voice_dir),
                "clip_count": len(clips),
            }
        )
        tasks.extend((class_name, servant_name, clip) for clip in clips)

    print(f"开始测量 {len(tasks)} 条 MP3（{len(servant_rows)} 名从者）……")
    durations: dict[Path, float] = {}
    failures: list[str] = []
    with ThreadPoolExecutor(max_workers=max(1, args.workers)) as executor:
        futures = {
            executor.submit(probe_duration, ffprobe, audio_file): audio_file
            for _, _, audio_file in tasks
        }
        completed_count = 0
        for future in as_completed(futures):
            audio_file = futures[future]
            completed_count += 1
            try:
                durations[audio_file] = future.result()
            except Exception as exc:  # noqa: BLE001 - 汇总所有坏文件后统一失败
                failures.append(f"{audio_file}: {exc}")
            if completed_count % 100 == 0 or completed_count == len(futures):
                print(f"已测量：{completed_count} / {len(futures)}")

    if failures:
        print("以下音频无法读取：", file=sys.stderr)
        for failure in failures:
            print(f"- {failure}", file=sys.stderr)
        return 1

    clips_by_servant: dict[tuple[str, str], list[tuple[Path, float]]] = {}
    for class_name, servant_name, audio_file in tasks:
        clips_by_servant.setdefault((class_name, servant_name), []).append(
            (audio_file, durations[audio_file])
        )

    selections: list[dict[str, object]] = []
    for row in servant_rows:
        key = (str(row["class"]), str(row["servant"]))
        candidates = clips_by_servant[key]
        selected_file, selected_duration = min(
            candidates,
            key=lambda item: (
                abs(item[1] - args.target_seconds),
                item[0].name.casefold(),
            ),
        )
        selections.append(
            {
                **row,
                "selected_file": str(selected_file.resolve()),
                "selected_filename": selected_file.name,
                "duration_seconds": round(selected_duration, 6),
                "difference_from_target": round(
                    abs(selected_duration - args.target_seconds), 6
                ),
                "slide_seconds": round(
                    max(
                        6.0,
                        int((selected_duration + 2.5) * 10 + 0.999999) / 10,
                    ),
                    1,
                ),
            }
        )

    document = {
        "generated_at": datetime.now().isoformat(timespec="seconds"),
        "voice_root": str(voice_root),
        "image_root": str(image_root),
        "ffprobe": str(ffprobe),
        "target_seconds": args.target_seconds,
        "minimum_slide_seconds": 6.0,
        "post_audio_seconds": 2.5,
        "total_servants": len(selections),
        "total_clips_measured": len(tasks),
        "selections": selections,
    }
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(
        json.dumps(document, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    dynamic = sum(float(row["slide_seconds"]) > 6.0 for row in selections)
    print(f"已写入：{output}")
    print(f"选择数：{len(selections)}；最低 6 秒：{len(selections) - dynamic}；动态延长：{dynamic}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
