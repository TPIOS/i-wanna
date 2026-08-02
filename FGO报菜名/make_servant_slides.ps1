<#
.SYNOPSIS
    根据 PPT 第一页模板生成从者统计页。

.DESCRIPTION
    - 自动读取模板第一页的 20 个御主名和对应图片框；
    - 从“提取结果_vFinal2\职介\从者名”中按御主名寻找 PNG；
    - 七个常规职介使用各自空白图，其余职介统一使用 Extra.png；
    - 可用 -AllServants 自动遍历并生成全部从者页面；
    - 图片保持原始纵横比，在模板图片框内居中；
    - 第一名从者使用模板第一页，后续从者复制该页；
    - 模板中第一页之后的其它页面会保留并顺延。

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File .\make_servant_slides.ps1

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File .\make_servant_slides.ps1 `
        -Targets 'Saber|阿尔托莉雅·潘德拉贡','Saber|阿蒂拉' `
        -OutputPath '.\FGO报菜名_v2_从者页示例.pptx'

.EXAMPLE
    powershell -ExecutionPolicy Bypass -File .\make_servant_slides.ps1 `
        -AllServants -ServantPagesOnly `
        -OutputPath '.\FGO报菜名_v2_全部从者.pptx'
#>

[CmdletBinding()]
param(
    [Parameter()]
    [string]$TemplatePath = ".\FGO报菜名_v2.pptx",

    [Parameter()]
    [string]$ImageRoot = ".\提取结果_vFinal2",

    [Parameter()]
    [string]$BlankRoot = ".\空白从者",

    [Parameter()]
    [string[]]$Targets = @(
        "Saber|阿尔托莉雅·潘德拉贡",
        "Saber|阿蒂拉"
    ),

    [Parameter()]
    [switch]$AllServants,

    [Parameter()]
    [string]$OutputPath = ".\FGO报菜名_v2_从者页示例.pptx",

    [Parameter()]
    [string]$PreviewDir = "",

    [Parameter()]
    [switch]$ServantPagesOnly,

    [Parameter()]
    [switch]$DetailedReport,

    [Parameter()]
    [switch]$Force
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Get-AbsolutePath {
    param(
        [Parameter(Mandatory)]
        [string]$Path,

        [Parameter()]
        [switch]$AllowMissing
    )

    if (Test-Path -LiteralPath $Path) {
        return (Resolve-Path -LiteralPath $Path).Path
    }

    if (-not $AllowMissing) {
        throw "找不到路径：$Path"
    }

    $parent = Split-Path -Parent $Path
    $leaf = Split-Path -Leaf $Path
    if ([string]::IsNullOrWhiteSpace($parent)) {
        $parent = "."
    }
    $resolvedParent = (Resolve-Path -LiteralPath $parent).Path
    return [IO.Path]::Combine($resolvedParent, $leaf)
}

function ConvertTo-MatchKey {
    param(
        [AllowEmptyString()]
        [string]$Text
    )

    if ($null -eq $Text) {
        return ""
    }

    # FormKC 会统一全角/兼容字符；随后只保留字母和数字。
    # 这样可自动匹配：
    #   “Shiro 团子”          -> “Shiro团子.png”
    #   “夜晚者”              -> “∀￠∃_夜晚者.png”
    #   “Napoléon rey”        -> “nap_Napoléon rey.png”
    $normalized = $Text.Normalize([Text.NormalizationForm]::FormKC).ToLowerInvariant()
    return [Text.RegularExpressions.Regex]::Replace($normalized, "[^\p{L}\p{Nd}]", "")
}

function Get-AllServantTargets {
    param(
        [Parameter(Mandatory)]
        [string]$ResolvedImageRoot
    )

    # 先排列七个常规职介，再排列 Extra 职介。
    $classOrder = @(
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
        "Beast"
    )

    $availableClasses = @(
        Get-ChildItem -LiteralPath $ResolvedImageRoot -Directory |
            Select-Object -ExpandProperty Name
    )
    $unknownClasses = @($availableClasses | Where-Object { $_ -notin $classOrder })
    if ($unknownClasses.Count -gt 0) {
        # 新增职介不会被遗漏，统一接在已知职介之后。
        $classOrder += @($unknownClasses | Sort-Object)
    }

    $result = [Collections.Generic.List[object]]::new()
    foreach ($className in $classOrder) {
        $classDir = Join-Path $ResolvedImageRoot $className
        if (-not (Test-Path -LiteralPath $classDir -PathType Container)) {
            continue
        }

        # 提取脚本按原 Excel 列顺序创建目录；CreationTime 可保留该顺序。
        $servantDirs = @(
            Get-ChildItem -LiteralPath $classDir -Directory |
                Sort-Object CreationTime, Name
        )
        foreach ($servantDir in $servantDirs) {
            $result.Add([pscustomobject]@{
                ClassName   = $className
                ServantName = $servantDir.Name
            })
        }
    }

    return @($result)
}

function Get-BlankImagePath {
    param(
        [Parameter(Mandatory)]
        [string]$ClassName,

        [Parameter(Mandatory)]
        [string]$ResolvedBlankRoot
    )

    $standardClasses = @(
        "Saber",
        "Archer",
        "Lancer",
        "Rider",
        "Caster",
        "Berserker",
        "Assassin"
    )
    $blankName = if ($ClassName -in $standardClasses) {
        "$ClassName.png"
    }
    else {
        "Extra.png"
    }

    $blankPath = Join-Path $ResolvedBlankRoot $blankName
    if (-not (Test-Path -LiteralPath $blankPath -PathType Leaf)) {
        throw "找不到 $ClassName 职介所需的空白图：$blankPath"
    }
    return $blankPath
}

function Get-ShapeText {
    param(
        [Parameter(Mandatory)]
        $Shape
    )

    try {
        if ($Shape.HasTextFrame -eq -1 -and $Shape.TextFrame2.HasText -eq -1) {
            return [string]$Shape.TextFrame2.TextRange.Text
        }
    }
    catch {
        # 某些特殊形状没有可读取的 TextFrame2，直接按无文字处理。
    }
    return ""
}

function Get-SlideSlots {
    param(
        [Parameter(Mandatory)]
        $Slide
    )

    $pictures = [Collections.Generic.List[object]]::new()
    $textShapes = [Collections.Generic.List[object]]::new()

    for ($i = 1; $i -le $Slide.Shapes.Count; $i++) {
        $shape = $Slide.Shapes.Item($i)
        $text = Get-ShapeText -Shape $shape
        if (-not [string]::IsNullOrWhiteSpace($text)) {
            $textShapes.Add($shape)
        }

        # msoPicture = 13。尺寸过滤会排除铺满整页的背景图。
        if (
            $shape.Type -eq 13 -and
            $shape.Top -gt 50 -and
            $shape.Width -lt 250 -and
            $shape.Height -gt 100 -and
            $shape.Height -lt 400
        ) {
            $pictures.Add($shape)
        }
    }

    if ($pictures.Count -ne 20) {
        throw "模板页应有 20 个小图片框，实际识别到 $($pictures.Count) 个。"
    }

    $slots = [Collections.Generic.List[object]]::new()
    foreach ($picture in $pictures) {
        $pictureCenter = [double]$picture.Left + ([double]$picture.Width / 2)
        $pictureBottom = [double]$picture.Top + [double]$picture.Height

        $bestLabel = $null
        $bestDistance = [double]::PositiveInfinity
        foreach ($textShape in $textShapes) {
            $textTop = [double]$textShape.Top
            if ($textTop -lt ($pictureBottom - 5) -or $textTop -gt ($pictureBottom + 70)) {
                continue
            }

            $textCenter = [double]$textShape.Left + ([double]$textShape.Width / 2)
            $distance = [Math]::Abs($textCenter - $pictureCenter)
            if ($distance -lt $bestDistance) {
                $bestDistance = $distance
                $bestLabel = $textShape
            }
        }

        if ($null -eq $bestLabel -or $bestDistance -gt 80) {
            throw "无法为图片框（Left=$($picture.Left), Top=$($picture.Top)）找到下方御主名。"
        }

        $masterName = (Get-ShapeText -Shape $bestLabel).Trim()
        $slots.Add([pscustomobject]@{
            Picture    = $picture
            MasterName = $masterName
            Left       = [double]$picture.Left
            Top        = [double]$picture.Top
            Width      = [double]$picture.Width
            Height     = [double]$picture.Height
        })
    }

    # 模板顺序：第一排从左到右，再到第二排从左到右。
    return @($slots | Sort-Object Top, Left)
}

function Find-MasterImage {
    param(
        [Parameter(Mandatory)]
        [AllowEmptyCollection()]
        [IO.FileInfo[]]$Files,

        [Parameter(Mandatory)]
        [string]$MasterName
    )

    $masterKey = ConvertTo-MatchKey -Text $MasterName
    $exact = @($Files | Where-Object {
        (ConvertTo-MatchKey -Text $_.BaseName) -eq $masterKey
    })
    if ($exact.Count -eq 1) {
        return $exact[0]
    }
    if ($exact.Count -gt 1) {
        throw "御主【$($MasterName)】匹配到多个同名文件：$($exact.Name -join '、')"
    }

    # 个别导出文件名带昵称前缀；模板显示名会是文件名规范化后的后缀。
    $suffix = @($Files | Where-Object {
        (ConvertTo-MatchKey -Text $_.BaseName).EndsWith($masterKey, [StringComparison]::Ordinal)
    })
    if ($suffix.Count -eq 1) {
        return $suffix[0]
    }
    if ($suffix.Count -gt 1) {
        throw "御主【$($MasterName)】按后缀匹配到多个文件：$($suffix.Name -join '、')"
    }

    return $null
}

function Set-SlideTitle {
    param(
        [Parameter(Mandatory)]
        $Slide,

        [Parameter(Mandatory)]
        [string]$ServantName
    )

    $candidate = $null
    $candidateWidth = 0.0
    for ($i = 1; $i -le $Slide.Shapes.Count; $i++) {
        $shape = $Slide.Shapes.Item($i)
        $text = Get-ShapeText -Shape $shape
        if (
            -not [string]::IsNullOrWhiteSpace($text) -and
            $shape.Top -lt 100 -and
            $shape.Width -gt $candidateWidth
        ) {
            $candidate = $shape
            $candidateWidth = [double]$shape.Width
        }
    }

    if ($null -eq $candidate) {
        throw "找不到模板顶部的从者标题框。"
    }

    $candidate.TextFrame2.TextRange.Text = "★$ServantName★"
}

function Set-ServantSlide {
    param(
        [Parameter(Mandatory)]
        $Slide,

        [Parameter(Mandatory)]
        [string]$ClassName,

        [Parameter(Mandatory)]
        [string]$ServantName,

        [Parameter(Mandatory)]
        [string]$ResolvedImageRoot,

        [Parameter(Mandatory)]
        [string]$ResolvedBlankRoot,

        [Parameter()]
        [bool]$IncludeRows = $false
    )

    $servantDir = Join-Path (Join-Path $ResolvedImageRoot $ClassName) $ServantName
    if (-not (Test-Path -LiteralPath $servantDir -PathType Container)) {
        throw "找不到从者图片目录：$servantDir"
    }

    $blankPath = Get-BlankImagePath `
        -ClassName $ClassName `
        -ResolvedBlankRoot $ResolvedBlankRoot

    $files = @(Get-ChildItem -LiteralPath $servantDir -File -Filter "*.png")
    $slots = @(Get-SlideSlots -Slide $Slide)
    Set-SlideTitle -Slide $Slide -ServantName $ServantName

    $ownedCount = 0
    $blankCount = 0
    $resultRows = [Collections.Generic.List[object]]::new()

    for ($i = 0; $i -lt $slots.Count; $i++) {
        $slot = $slots[$i]
        $matchedFile = Find-MasterImage -Files $files -MasterName $slot.MasterName
        if ($null -eq $matchedFile) {
            $sourcePath = $blankPath
            $status = "空白"
            $blankCount++
        }
        else {
            $sourcePath = $matchedFile.FullName
            $status = "持有"
            $ownedCount++
        }

        # 删除模板占位图，再嵌入实际 PNG。AddPicture 的 -1/-1 使用图片原始尺寸。
        $slot.Picture.Delete()
        $newPicture = $Slide.Shapes.AddPicture(
            $sourcePath,
            0,   # LinkToFile = msoFalse
            -1,  # SaveWithDocument = msoTrue
            $slot.Left,
            $slot.Top,
            -1,
            -1
        )

        # 等比缩放到原图片框以内并居中；不会裁切卡面底部数据。
        $newPicture.LockAspectRatio = -1
        $newPicture.Height = $slot.Height
        if ($newPicture.Width -gt $slot.Width) {
            $newPicture.Width = $slot.Width
        }
        $newPicture.Left = $slot.Left + (($slot.Width - $newPicture.Width) / 2)
        $newPicture.Top = $slot.Top + (($slot.Height - $newPicture.Height) / 2)
        $newPicture.Name = "ServantImage_$($i + 1)"
        $newPicture.AlternativeText = "$($slot.MasterName) - $ServantName - $status"

        if ($IncludeRows) {
            $resultRows.Add([pscustomobject]@{
                Master = ($slot.MasterName -replace "[\r\n]+", " ")
                Status = $status
                File   = [IO.Path]::GetFileName($sourcePath)
            })
        }
    }

    [pscustomobject]@{
        ClassName   = $ClassName
        ServantName = $ServantName
        OwnedCount  = $ownedCount
        BlankCount  = $blankCount
        Rows        = @($resultRows)
    }
}

$resolvedTemplate = Get-AbsolutePath -Path $TemplatePath
$resolvedImageRoot = Get-AbsolutePath -Path $ImageRoot
$resolvedBlankRoot = Get-AbsolutePath -Path $BlankRoot
$resolvedOutput = Get-AbsolutePath -Path $OutputPath -AllowMissing
$resolvedPreviewDir = ""
if (-not [string]::IsNullOrWhiteSpace($PreviewDir)) {
    $resolvedPreviewDir = Get-AbsolutePath -Path $PreviewDir -AllowMissing
    if (-not (Test-Path -LiteralPath $resolvedPreviewDir)) {
        [void](New-Item -ItemType Directory -Path $resolvedPreviewDir)
    }
}

if ($resolvedTemplate -eq $resolvedOutput) {
    throw "输出文件不能覆盖模板文件。"
}
$parsedTargets = [Collections.Generic.List[object]]::new()
if ($AllServants) {
    foreach ($target in (Get-AllServantTargets -ResolvedImageRoot $resolvedImageRoot)) {
        $parsedTargets.Add($target)
    }
}
else {
    if ($Targets.Count -lt 1) {
        throw "Targets 至少需要一个【职介|从者名】。"
    }
    foreach ($target in $Targets) {
        $parts = $target.Split('|', 2)
        if ($parts.Count -ne 2 -or [string]::IsNullOrWhiteSpace($parts[0]) -or [string]::IsNullOrWhiteSpace($parts[1])) {
            throw "目标格式错误：【$($target)】。正确格式为【职介|从者名】。"
        }
        $parsedTargets.Add([pscustomobject]@{
            ClassName   = $parts[0].Trim()
            ServantName = $parts[1].Trim()
        })
    }
}

if ($parsedTargets.Count -lt 1) {
    throw "没有找到任何待生成的从者目录。"
}
if (Test-Path -LiteralPath $resolvedOutput) {
    if (-not $Force) {
        throw "输出文件已存在：$resolvedOutput。若要覆盖，请添加 -Force。"
    }
    Remove-Item -LiteralPath $resolvedOutput -Force
}

$powerPoint = $null
$presentation = $null
$reports = [Collections.Generic.List[object]]::new()
Write-Host "准备生成 $($parsedTargets.Count) 张从者页……"
try {
    $powerPoint = New-Object -ComObject PowerPoint.Application
    # ReadOnly = msoTrue, Untitled = msoFalse, WithWindow = msoFalse
    $presentation = $powerPoint.Presentations.Open($resolvedTemplate, -1, 0, 0)

    # 每次复制刚生成的最后一个模板页，可避免反复跨越大量页面移动。
    for ($i = 2; $i -le $parsedTargets.Count; $i++) {
        $sourceSlide = $presentation.Slides.Item($i - 1)
        try {
            $duplicate = $sourceSlide.Duplicate()
            [void][Runtime.InteropServices.Marshal]::ReleaseComObject($duplicate)
        }
        finally {
            [void][Runtime.InteropServices.Marshal]::ReleaseComObject($sourceSlide)
        }
        if ($i % 10 -eq 0 -or $i -eq $parsedTargets.Count) {
            Write-Host "已准备模板页：$i / $($parsedTargets.Count)"
        }
    }

    if ($ServantPagesOnly) {
        while ($presentation.Slides.Count -gt $parsedTargets.Count) {
            $extraSlide = $presentation.Slides.Item($presentation.Slides.Count)
            try {
                $extraSlide.Delete()
            }
            finally {
                [void][Runtime.InteropServices.Marshal]::ReleaseComObject($extraSlide)
            }
        }
    }

    for ($i = 1; $i -le $parsedTargets.Count; $i++) {
        $target = $parsedTargets[$i - 1]
        $slide = $presentation.Slides.Item($i)
        try {
            $report = Set-ServantSlide `
                -Slide $slide `
                -ClassName $target.ClassName `
                -ServantName $target.ServantName `
                -ResolvedImageRoot $resolvedImageRoot `
                -ResolvedBlankRoot $resolvedBlankRoot `
                -IncludeRows ([bool]$DetailedReport)
            $reports.Add($report)
            Write-Host (
                "[{0:D3}/{1:D3}] {2} / {3}：持有 {4}，空白 {5}" -f `
                    $i,
                    $parsedTargets.Count,
                    $report.ClassName,
                    $report.ServantName,
                    $report.OwnedCount,
                    $report.BlankCount
            )
        }
        finally {
            if ($null -ne $slide) {
                [void][Runtime.InteropServices.Marshal]::ReleaseComObject($slide)
            }
        }
    }

    # ppSaveAsOpenXMLPresentation = 24
    $presentation.SaveAs($resolvedOutput, 24)

    if (-not [string]::IsNullOrWhiteSpace($resolvedPreviewDir)) {
        # 重新打开已保存文件再渲染。PowerPoint 对刚复制的页面偶尔会使用旧的
        # 文本渲染缓存，重新打开后导出的预览才与用户实际打开 PPT 时一致。
        $presentation.Close()
        [void][Runtime.InteropServices.Marshal]::ReleaseComObject($presentation)
        $presentation = $powerPoint.Presentations.Open($resolvedOutput, -1, 0, 0)

        $previewIndexes = [Collections.Generic.List[int]]::new()
        if ($AllServants) {
            $seenClasses = [Collections.Generic.HashSet[string]]::new()
            for ($i = 1; $i -le $parsedTargets.Count; $i++) {
                if ($seenClasses.Add($parsedTargets[$i - 1].ClassName)) {
                    $previewIndexes.Add($i)
                }
            }
        }
        else {
            for ($i = 1; $i -le $parsedTargets.Count; $i++) {
                $previewIndexes.Add($i)
            }
        }

        foreach ($i in $previewIndexes) {
            $target = $parsedTargets[$i - 1]
            $safeName = [Text.RegularExpressions.Regex]::Replace(
                $target.ServantName,
                '[\\/:*?"<>|]',
                '_'
            )
            $previewPath = Join-Path $resolvedPreviewDir ("{0:D3}_{1}_{2}.png" -f $i, $target.ClassName, $safeName)
            $slide = $presentation.Slides.Item($i)
            try {
                $slide.Export($previewPath, "PNG", 1920, 1080)
            }
            finally {
                [void][Runtime.InteropServices.Marshal]::ReleaseComObject($slide)
            }
            Write-Host "已导出抽样预览：第 $i 页 / $($target.ClassName) / $($target.ServantName)"
        }
    }
}
finally {
    if ($null -ne $presentation) {
        try { $presentation.Close() } catch { }
        [void][Runtime.InteropServices.Marshal]::ReleaseComObject($presentation)
    }
    if ($null -ne $powerPoint) {
        try { $powerPoint.Quit() } catch { }
        [void][Runtime.InteropServices.Marshal]::ReleaseComObject($powerPoint)
    }
    [GC]::Collect()
    [GC]::WaitForPendingFinalizers()
}

Write-Host "`n已生成：$resolvedOutput"
$reportGroups = @($reports | Group-Object ClassName)
foreach ($group in $reportGroups) {
    $owned = ($group.Group | Measure-Object OwnedCount -Sum).Sum
    $blank = ($group.Group | Measure-Object BlankCount -Sum).Sum
    Write-Host "- $($group.Name)：$($group.Count) 页，持有图 $owned，空白图 $blank"
}
if ($DetailedReport) {
    foreach ($report in $reports) {
        Write-Host "`n$($report.ClassName) / $($report.ServantName)"
        foreach ($row in $report.Rows) {
            Write-Host ("    {0,-18} {1,-4} {2}" -f $row.Master, $row.Status, $row.File)
        }
    }
}
