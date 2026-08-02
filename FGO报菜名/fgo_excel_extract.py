"""兼容提取 Excel 的普通浮动图片和“置于单元格中”图片。

浮动图片按中心点归类，单元格图片按其单元格坐标归类；两种模式都会
以 E3 图片为尺寸基准，统一 resize 后导出为 PNG。
"""

from __future__ import annotations

from dataclasses import dataclass
from io import BytesIO
from pathlib import Path
import posixpath
import re
from typing import Iterable
import xml.etree.ElementTree as ET
import zipfile

from PIL import Image


# ===== 可以按需要修改的设置 =====
# 可以填写浮动图片版 v3，也可以填写单元格图片版 vFinal；脚本会自动检测。
INPUT_FILE = Path("看看龙大Master实力报名表_vFinal2_nameCorrected.xlsx")
OUTPUT_DIR = Path("提取结果_vFinal2")
SHEET_NAME: str | None = None  # None = 工作簿第一个工作表
START_COLUMN = "E"

# 初次测试只处理第一名从者（即 E2）。改为 None 后处理所有从者。
MAX_SERVANTS: int | None = None

# None = 提取该从者列中所有图片；设为 1 则只测试第一张。
MAX_IMAGES_PER_SERVANT: int | None = None

# 所有图片都会 resize 为中心点落在这个单元格的图片的像素尺寸。
REFERENCE_IMAGE_CELL = "E3"

# 这些列是统计列，不是职介或从者列。
IGNORED_HEADERS = {"count", "total count"}

# Excel 的列宽单位依赖默认字体。Calibri/Aptos 等常用默认字体为 7 像素；
# 若图片中心恰好落在列边界而被归到相邻列，可将此值调整为 6 或 8 再试。
MAX_DIGIT_WIDTH_PIXELS = 7


CELL_REFERENCE_RE = re.compile(r"^([A-Z]+)([1-9]\d*)$")
EMU_PER_PIXEL = 9_525
EMU_PER_POINT = 12_700
DEFAULT_COLUMN_WIDTH = 8.43
DEFAULT_ROW_HEIGHT_POINTS = 15.0


@dataclass(frozen=True)
class FloatingImage:
    """一张已定位图片，以及它所属的 1 起始列、行。"""

    column: int
    row: int
    internal_path: str


class WorksheetGrid:
    """将工作表的行列尺寸换算为 EMU，用于判断图片中心在哪个单元格。"""

    def __init__(
        self,
        default_column_width: float,
        default_row_height: float,
        column_widths: dict[int, float],
        row_heights: dict[int, float],
        hidden_columns: set[int],
        hidden_rows: set[int],
    ) -> None:
        self.default_column_width = default_column_width
        self.default_row_height = default_row_height
        self.column_widths = column_widths
        self.row_heights = row_heights
        self.hidden_columns = hidden_columns
        self.hidden_rows = hidden_rows

    @classmethod
    def from_worksheet(cls, worksheet: ET.Element) -> "WorksheetGrid":
        format_properties = next(children(worksheet, "sheetFormatPr"), None)
        default_column_width = DEFAULT_COLUMN_WIDTH
        default_row_height = DEFAULT_ROW_HEIGHT_POINTS
        if format_properties is not None:
            default_column_width = float(
                format_properties.get("defaultColWidth", DEFAULT_COLUMN_WIDTH)
            )
            default_row_height = float(
                format_properties.get("defaultRowHeight", DEFAULT_ROW_HEIGHT_POINTS)
            )

        column_widths: dict[int, float] = {}
        hidden_columns: set[int] = set()
        columns = next(children(worksheet, "cols"), None)
        if columns is not None:
            for column in children(columns, "col"):
                first = int(column.get("min", "1")) - 1
                last = int(column.get("max", str(first + 1))) - 1
                width = column.get("width")
                is_hidden = column.get("hidden") in {"1", "true", "True"}
                for index in range(first, last + 1):
                    if width is not None:
                        column_widths[index] = float(width)
                    if is_hidden:
                        hidden_columns.add(index)

        row_heights: dict[int, float] = {}
        hidden_rows: set[int] = set()
        for row in (element for element in worksheet.iter() if local_name(element.tag) == "row"):
            index = int(row.get("r", "1")) - 1
            if row.get("ht") is not None:
                row_heights[index] = float(row.get("ht"))
            if row.get("hidden") in {"1", "true", "True"}:
                hidden_rows.add(index)

        return cls(
            default_column_width,
            default_row_height,
            column_widths,
            row_heights,
            hidden_columns,
            hidden_rows,
        )

    def column_width_emu(self, index: int) -> int:
        if index in self.hidden_columns:
            return 0
        width = self.column_widths.get(index, self.default_column_width)
        # Excel 的列宽是“最大数字字符数”；+5 是单元格左右边距。
        pixels = max(0, int(width * MAX_DIGIT_WIDTH_PIXELS + 5))
        return pixels * EMU_PER_PIXEL

    def row_height_emu(self, index: int) -> int:
        if index in self.hidden_rows:
            return 0
        height = self.row_heights.get(index, self.default_row_height)
        return max(0, round(height * EMU_PER_POINT))

    def column_start_emu(self, index: int) -> int:
        return sum(self.column_width_emu(column) for column in range(index))

    def row_start_emu(self, index: int) -> int:
        return sum(self.row_height_emu(row) for row in range(index))

    def column_at_emu(self, position: float) -> int:
        """返回 0 起始的列号；边界点归到右侧单元格。"""
        accumulated = 0
        index = 0
        while True:
            width = self.column_width_emu(index)
            if width and position < accumulated + width:
                return index
            accumulated += width
            index += 1
            if index > 20_000:
                raise ValueError("图片中心点超出可解析的列范围。")

    def row_at_emu(self, position: float) -> int:
        """返回 0 起始的行号；边界点归到下侧单元格。"""
        accumulated = 0
        index = 0
        while True:
            height = self.row_height_emu(index)
            if height and position < accumulated + height:
                return index
            accumulated += height
            index += 1
            if index > 1_000_000:
                raise ValueError("图片中心点超出可解析的行范围。")

    def centre_cell(
        self,
        start_column: int,
        start_column_offset: int,
        start_row: int,
        start_row_offset: int,
        end_x: int,
        end_y: int,
    ) -> tuple[int, int]:
        start_x = self.column_start_emu(start_column) + start_column_offset
        start_y = self.row_start_emu(start_row) + start_row_offset
        center_x = (start_x + end_x) / 2
        center_y = (start_y + end_y) / 2
        return self.column_at_emu(center_x) + 1, self.row_at_emu(center_y) + 1


def local_name(name: str) -> str:
    """去掉 XML 命名空间，取得标签或属性的本地名称。"""
    return name.rsplit("}", 1)[-1]


def attribute(element: ET.Element, name: str) -> str | None:
    """按本地名称读取属性，兼容带命名空间的 r:embed。"""
    for key, value in element.attrib.items():
        if local_name(key) == name:
            return value
    return None


def children(element: ET.Element, name: str) -> Iterable[ET.Element]:
    return (child for child in element if local_name(child.tag) == name)


def first_descendant(element: ET.Element, name: str) -> ET.Element | None:
    return next((child for child in element.iter() if local_name(child.tag) == name), None)


def read_xml(archive: zipfile.ZipFile, internal_path: str) -> ET.Element:
    try:
        return ET.fromstring(archive.read(internal_path))
    except KeyError as error:
        raise ValueError(f"工作簿中找不到 {internal_path}。") from error


def resolve_internal_path(owner_path: str, target: str) -> str:
    """将 OOXML relationship 的相对 Target 解析为 xlsx 内部路径。"""
    if target.startswith("/"):
        return target.lstrip("/")
    return posixpath.normpath(posixpath.join(posixpath.dirname(owner_path), target))


def relationship_part_path(owner_path: str) -> str:
    return posixpath.join(
        posixpath.dirname(owner_path),
        "_rels",
        f"{posixpath.basename(owner_path)}.rels",
    )


def column_index(column: str) -> int:
    result = 0
    for character in column.upper():
        if not "A" <= character <= "Z":
            raise ValueError(f"无效列名：{column}")
        result = result * 26 + ord(character) - ord("A") + 1
    return result


def column_name(index: int) -> str:
    if index < 1:
        raise ValueError("列序号必须大于 0")
    letters: list[str] = []
    while index:
        index, remainder = divmod(index - 1, 26)
        letters.append(chr(ord("A") + remainder))
    return "".join(reversed(letters))


def split_cell_reference(reference: str) -> tuple[int, int]:
    match = CELL_REFERENCE_RE.fullmatch(reference)
    if not match:
        raise ValueError(f"无效单元格坐标：{reference}")
    return column_index(match.group(1)), int(match.group(2))


def normalise_text(value: str | None) -> str:
    return "" if value is None else str(value).strip()


def safe_component(name: str) -> str:
    """将 Excel 中的名称转换为可作为 Windows 文件/文件夹名的文本。"""
    cleaned = re.sub(r'[<>:"/\\|?*\x00-\x1f]', "_", name).strip().rstrip(". ")
    if not cleaned:
        raise ValueError("文件或文件夹名称为空")
    if cleaned.upper() in {
        "CON", "PRN", "AUX", "NUL",
        *(f"COM{i}" for i in range(1, 10)),
        *(f"LPT{i}" for i in range(1, 10)),
    }:
        cleaned = f"_{cleaned}"
    return cleaned


def sheet_path(archive: zipfile.ZipFile, wanted_sheet_name: str | None) -> str:
    """按工作表名称（或第一个工作表）取得其 xlsx 内部路径。"""
    workbook_path = "xl/workbook.xml"
    workbook = read_xml(archive, workbook_path)
    sheets = [element for element in workbook.iter() if local_name(element.tag) == "sheet"]
    if not sheets:
        raise ValueError("工作簿没有工作表。")

    selected = next(
        (sheet for sheet in sheets if wanted_sheet_name is not None and sheet.get("name") == wanted_sheet_name),
        None,
    )
    if wanted_sheet_name is not None and selected is None:
        available = "、".join(sheet.get("name", "(未命名)") for sheet in sheets)
        raise ValueError(f"找不到工作表 {wanted_sheet_name!r}；可用工作表：{available}")
    if selected is None:
        selected = sheets[0]

    relationships = read_xml(archive, "xl/_rels/workbook.xml.rels")
    targets = {
        relationship.get("Id"): relationship.get("Target")
        for relationship in relationships
        if local_name(relationship.tag) == "Relationship"
    }
    relationship_id = attribute(selected, "id")
    target = targets.get(relationship_id)
    if not target:
        raise ValueError(f"工作表 {selected.get('name')!r} 缺少 relationship。")
    return resolve_internal_path(workbook_path, target)


def read_shared_strings(archive: zipfile.ZipFile) -> list[str]:
    """读取 sharedStrings，并兼容富文本单元格。"""
    if "xl/sharedStrings.xml" not in archive.namelist():
        return []
    root = read_xml(archive, "xl/sharedStrings.xml")
    return [
        "".join(node.text or "" for node in item.iter() if local_name(node.tag) == "t")
        for item in root
        if local_name(item.tag) == "si"
    ]


def cell_text(cell: ET.Element, shared_strings: list[str]) -> str | None:
    cell_type = cell.get("t")
    value = next(children(cell, "v"), None)
    if cell_type == "s":
        if value is None or value.text is None:
            return None
        return shared_strings[int(value.text)]
    if cell_type == "inlineStr":
        return "".join(node.text or "" for node in cell.iter() if local_name(node.tag) == "t")
    return None if value is None else value.text


def read_worksheet(
    archive: zipfile.ZipFile, worksheet_path: str, shared_strings: list[str]
) -> tuple[ET.Element, dict[str, str | None], int]:
    """返回工作表 XML、单元格文本及最大列号。"""
    root = read_xml(archive, worksheet_path)
    values: dict[str, str | None] = {}
    max_column = 0

    for cell in (element for element in root.iter() if local_name(element.tag) == "c"):
        reference = cell.get("r")
        if not reference:
            continue
        column, _ = split_cell_reference(reference)
        max_column = max(max_column, column)
        values[reference] = cell_text(cell, shared_strings)

    return root, values, max_column


def drawing_paths_for_sheet(archive: zipfile.ZipFile, worksheet_path: str) -> list[str]:
    """取得工作表关联的 Drawing XML 路径。"""
    relationships_path = relationship_part_path(worksheet_path)
    if relationships_path not in archive.namelist():
        return []
    relationships = read_xml(archive, relationships_path)
    drawing_paths: list[str] = []
    for relationship in relationships:
        if local_name(relationship.tag) != "Relationship":
            continue
        relationship_type = relationship.get("Type", "")
        target = relationship.get("Target")
        if relationship_type.endswith("/drawing") and target:
            drawing_paths.append(resolve_internal_path(worksheet_path, target))
    return drawing_paths


def marker_values(marker: ET.Element) -> tuple[int, int, int, int]:
    """读取 xdr:from / xdr:to，返回 col、colOff、row、rowOff。"""
    fields = {
        local_name(field.tag): int(field.text or "0")
        for field in marker
        if local_name(field.tag) in {"col", "colOff", "row", "rowOff"}
    }
    try:
        return fields["col"], fields["colOff"], fields["row"], fields["rowOff"]
    except KeyError as error:
        raise ValueError("图片锚点缺少行列坐标。") from error


def floating_images_from_drawing(
    archive: zipfile.ZipFile, drawing_path: str, grid: WorksheetGrid
) -> list[FloatingImage]:
    """读取一份 Drawing XML，将其中图片按中心点映射到单元格。"""
    drawing_relationships_path = relationship_part_path(drawing_path)
    if drawing_relationships_path not in archive.namelist():
        return []

    drawing_relationships = read_xml(archive, drawing_relationships_path)
    relationship_targets = {
        relationship.get("Id"): resolve_internal_path(drawing_path, relationship.get("Target", ""))
        for relationship in drawing_relationships
        if local_name(relationship.tag) == "Relationship"
        and relationship.get("Type", "").endswith("/image")
        and relationship.get("Target")
    }

    drawing = read_xml(archive, drawing_path)
    images: list[FloatingImage] = []
    for anchor in drawing:
        anchor_type = local_name(anchor.tag)
        if anchor_type not in {"oneCellAnchor", "twoCellAnchor"}:
            continue

        from_marker = next(children(anchor, "from"), None)
        blip = first_descendant(anchor, "blip")
        relationship_id = attribute(blip, "embed") if blip is not None else None
        if from_marker is None or not relationship_id:
            continue
        image_path = relationship_targets.get(relationship_id)
        if image_path is None or image_path not in archive.namelist():
            continue

        start_column, start_column_offset, start_row, start_row_offset = marker_values(from_marker)
        start_x = grid.column_start_emu(start_column) + start_column_offset
        start_y = grid.row_start_emu(start_row) + start_row_offset

        if anchor_type == "oneCellAnchor":
            extent = next(children(anchor, "ext"), None)
            if extent is None:
                continue
            end_x = start_x + int(extent.get("cx", "0"))
            end_y = start_y + int(extent.get("cy", "0"))
        else:
            to_marker = next(children(anchor, "to"), None)
            if to_marker is None:
                continue
            end_column, end_column_offset, end_row, end_row_offset = marker_values(to_marker)
            end_x = grid.column_start_emu(end_column) + end_column_offset
            end_y = grid.row_start_emu(end_row) + end_row_offset

        center_column, center_row = grid.centre_cell(
            start_column,
            start_column_offset,
            start_row,
            start_row_offset,
            end_x,
            end_y,
        )
        images.append(FloatingImage(center_column, center_row, image_path))

    return images


def read_floating_images(
    archive: zipfile.ZipFile, worksheet_path: str, grid: WorksheetGrid
) -> list[FloatingImage]:
    drawing_paths = drawing_paths_for_sheet(archive, worksheet_path)
    if not drawing_paths:
        raise ValueError(
            "此工作表没有普通浮动图片（Drawing）。请使用未转换为“置于单元格中”的工作簿。"
        )

    images = [
        image
        for drawing_path in drawing_paths
        for image in floating_images_from_drawing(archive, drawing_path, grid)
    ]
    if not images:
        raise ValueError("在 Drawing 中没有找到可提取的嵌入式图片。")
    return images


RICH_DATA_PATHS = {
    "xl/metadata.xml",
    "xl/richData/rdrichvalue.xml",
    "xl/richData/richValueRel.xml",
    "xl/richData/_rels/richValueRel.xml.rels",
}


def has_cell_images(archive: zipfile.ZipFile, worksheet: ET.Element) -> bool:
    """判断工作表是否含有 Excel 365 的 richData 单元格图片。"""
    if not RICH_DATA_PATHS.issubset(archive.namelist()):
        return False
    return any(
        local_name(element.tag) == "c" and element.get("vm") is not None
        for element in worksheet.iter()
    )


def read_cell_images(
    archive: zipfile.ZipFile, worksheet: ET.Element
) -> list[FloatingImage]:
    """读取 Excel 365“置于单元格中”图片并映射到其单元格坐标。"""
    missing = RICH_DATA_PATHS.difference(archive.namelist())
    if missing:
        raise ValueError(
            "工作簿缺少单元格图片 richData 文件："
            + "、".join(sorted(missing))
        )

    metadata = read_xml(archive, "xl/metadata.xml")
    future_metadata = next(
        (
            element
            for element in metadata
            if local_name(element.tag) == "futureMetadata"
            and element.get("name") == "XLRICHVALUE"
        ),
        None,
    )
    value_metadata = next(
        (element for element in metadata if local_name(element.tag) == "valueMetadata"),
        None,
    )
    if future_metadata is None or value_metadata is None:
        raise ValueError("metadata.xml 中没有 XLRICHVALUE 单元格图片元数据。")

    # valueMetadata -> futureMetadata -> rdrichvalue。
    future_to_rich: list[int] = []
    for book in children(future_metadata, "bk"):
        binding = first_descendant(book, "rvb")
        if binding is None or binding.get("i") is None:
            raise ValueError("无法解析 futureMetadata 中的 rich value 编号。")
        future_to_rich.append(int(binding.get("i")))

    value_to_future: list[int] = []
    for book in children(value_metadata, "bk"):
        reference = first_descendant(book, "rc")
        if reference is None or reference.get("v") is None:
            raise ValueError("无法解析 valueMetadata 中的 rich value 引用。")
        value_to_future.append(int(reference.get("v")))

    rich_values = read_xml(archive, "xl/richData/rdrichvalue.xml")
    rich_to_relation: list[int] = []
    for rich_value in children(rich_values, "rv"):
        fields = [field.text for field in children(rich_value, "v")]
        if not fields or fields[0] is None:
            raise ValueError("无法解析 rdrichvalue.xml 中的图片关系编号。")
        rich_to_relation.append(int(fields[0]))

    rich_value_relations = read_xml(archive, "xl/richData/richValueRel.xml")
    relation_ids = [
        attribute(relation, "id")
        for relation in children(rich_value_relations, "rel")
    ]
    if any(relation_id is None for relation_id in relation_ids):
        raise ValueError("无法解析 richValueRel.xml 中的 relationship id。")

    relationship_owner = "xl/richData/richValueRel.xml"
    relationships = read_xml(
        archive, "xl/richData/_rels/richValueRel.xml.rels"
    )
    targets = {
        relationship.get("Id"): resolve_internal_path(
            relationship_owner, relationship.get("Target", "")
        )
        for relationship in relationships
        if local_name(relationship.tag) == "Relationship"
        and relationship.get("Type", "").endswith("/image")
    }

    images: list[FloatingImage] = []
    for cell in (
        element for element in worksheet.iter() if local_name(element.tag) == "c"
    ):
        cell_reference = cell.get("r")
        metadata_number = cell.get("vm")
        if not cell_reference or metadata_number is None:
            continue

        try:
            # 工作表的 vm 是从 1 开始的 valueMetadata 索引。
            future_index = value_to_future[int(metadata_number) - 1]
            rich_index = future_to_rich[future_index]
            relation_index = rich_to_relation[rich_index]
            relation_id = relation_ids[relation_index]
            image_path = targets[relation_id]
        except (IndexError, KeyError, TypeError) as error:
            raise ValueError(f"无法解析单元格 {cell_reference} 的图片关系。") from error

        if image_path not in archive.namelist():
            raise ValueError(
                f"单元格 {cell_reference} 对应的图片不存在：{image_path}"
            )
        column, row = split_cell_reference(cell_reference)
        images.append(FloatingImage(column, row, image_path))

    if not images:
        raise ValueError("工作簿含有 richData，但没有找到可提取的单元格图片。")
    return images


def read_images_auto(
    archive: zipfile.ZipFile,
    worksheet_path: str,
    worksheet: ET.Element,
) -> tuple[list[FloatingImage], str]:
    """自动选择单元格图片或浮动图片读取方式。"""
    if has_cell_images(archive, worksheet):
        return read_cell_images(archive, worksheet), "单元格图片"

    grid = WorksheetGrid.from_worksheet(worksheet)
    return read_floating_images(archive, worksheet_path, grid), "浮动图片"


def image_size(archive: zipfile.ZipFile, image_path: str) -> tuple[int, int]:
    """读取工作簿内原始图片的像素尺寸。"""
    with Image.open(BytesIO(archive.read(image_path))) as image:
        return image.size


def resized_png_bytes(
    archive: zipfile.ZipFile, image_path: str, target_size: tuple[int, int]
) -> bytes:
    """按指定尺寸缩放，并编码为 PNG。"""
    with Image.open(BytesIO(archive.read(image_path))) as source:
        # RGB 可避免不透明 JPG 被保存为四通道 PNG；带透明通道的 PNG 则保留透明度。
        mode = "RGBA" if "A" in source.getbands() else "RGB"
        converted = source.convert(mode)
        try:
            image = converted
            if image.size != target_size:
                image = converted.resize(target_size, Image.Resampling.LANCZOS)
            try:
                output = BytesIO()
                image.save(output, format="PNG")
                return output.getvalue()
            finally:
                if image is not converted:
                    image.close()
        finally:
            converted.close()


def find_servants(
    values: dict[str, str | None], max_column: int
) -> tuple[list[tuple[int, str, str]], set[str]]:
    """从第 1、2 行找出 (列号、职介、从者名)，并返回全部职介名。"""
    start = column_index(START_COLUMN)
    ignored = {name.casefold() for name in IGNORED_HEADERS}
    current_class: str | None = None
    all_classes: set[str] = set()
    servants: list[tuple[int, str, str]] = []

    for index in range(start, max_column + 1):
        column = column_name(index)
        header = normalise_text(values.get(f"{column}1"))
        if header:
            if header.casefold() in ignored:
                current_class = None
            else:
                current_class = header
                all_classes.add(header)

        servant_name = normalise_text(values.get(f"{column}2"))
        if current_class and servant_name:
            servants.append((index, current_class, servant_name))

    return servants, all_classes


def unique_destination(folder: Path, master_name: str, row: int, used: set[Path]) -> Path:
    """通常输出“御主名.png”；重复名称追加所在行号。"""
    stem = safe_component(master_name)
    destination = folder / f"{stem}.png"
    if destination not in used:
        used.add(destination)
        return destination

    destination = folder / f"{stem}__row{row}.png"
    serial = 2
    while destination in used:
        destination = folder / f"{stem}__row{row}_{serial}.png"
        serial += 1
    used.add(destination)
    return destination


def main() -> None:
    if not INPUT_FILE.is_file():
        raise FileNotFoundError(f"找不到输入文件：{INPUT_FILE.resolve()}")
    if MAX_SERVANTS is not None and MAX_SERVANTS < 1:
        raise ValueError("MAX_SERVANTS 必须为正整数或 None。")
    if MAX_IMAGES_PER_SERVANT is not None and MAX_IMAGES_PER_SERVANT < 1:
        raise ValueError("MAX_IMAGES_PER_SERVANT 必须为正整数或 None。")

    with zipfile.ZipFile(INPUT_FILE) as archive:
        worksheet_path = sheet_path(archive, SHEET_NAME)
        shared_strings = read_shared_strings(archive)
        worksheet, values, max_column = read_worksheet(archive, worksheet_path, shared_strings)
        images, image_mode = read_images_auto(archive, worksheet_path, worksheet)

        reference_column, reference_row = split_cell_reference(REFERENCE_IMAGE_CELL)
        reference_image = next(
            (
                image
                for image in sorted(images, key=lambda item: item.internal_path)
                if image.column == reference_column and image.row == reference_row
            ),
            None,
        )
        if reference_image is None:
            raise ValueError(
                f"找不到中心点位于 {REFERENCE_IMAGE_CELL} 的基准图片；"
                "请检查图片位置或修改 REFERENCE_IMAGE_CELL。"
            )
        target_size = image_size(archive, reference_image.internal_path)

        servants, all_classes = find_servants(values, max_column)
        if not servants:
            raise ValueError("从 E2 起没有找到从者名称。")

        OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
        for class_name in all_classes:
            (OUTPUT_DIR / safe_component(class_name)).mkdir(exist_ok=True)

        selected_servants = servants if MAX_SERVANTS is None else servants[:MAX_SERVANTS]
        if image_mode == "浮动图片":
            print(f"找到 {len(images)} 张浮动图片，按图片中心点归属单元格。")
        else:
            print(f"找到 {len(images)} 张单元格图片，按单元格坐标归属。")
        print(
            f"基准图片：{REFERENCE_IMAGE_CELL}，"
            f"统一导出尺寸：{target_size[0]} x {target_size[1]} 像素。"
        )
        print(f"共找到 {len(servants)} 名从者；本次处理 {len(selected_servants)} 名。")

        written_destinations: set[Path] = set()
        total_images = 0
        for number, (servant_column, class_name, servant_name) in enumerate(selected_servants, start=1):
            servant_folder = OUTPUT_DIR / safe_component(class_name) / safe_component(servant_name)
            servant_folder.mkdir(parents=True, exist_ok=True)

            matching_images = sorted(
                (
                    image
                    for image in images
                    if image.column == servant_column and image.row >= 3
                ),
                key=lambda image: (image.row, image.internal_path),
            )
            if MAX_IMAGES_PER_SERVANT is not None:
                matching_images = matching_images[:MAX_IMAGES_PER_SERVANT]

            saved = 0
            for image in matching_images:
                master_name = normalise_text(values.get(f"A{image.row}"))
                if not master_name:
                    print(
                        f"跳过 {column_name(image.column)}{image.row}："
                        f"A{image.row} 没有御主名称。"
                    )
                    continue

                destination = unique_destination(servant_folder, master_name, image.row, written_destinations)
                destination.write_bytes(resized_png_bytes(archive, image.internal_path, target_size))
                saved += 1
                total_images += 1

            print(f"[{number}/{len(selected_servants)}] {class_name} / {servant_name}：导出 {saved} 张")

    print(f"完成：共导出 {total_images} 张统一尺寸的 PNG 图片到 {OUTPUT_DIR.resolve()}")


if __name__ == "__main__":
    main()
