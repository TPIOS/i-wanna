<#
.SYNOPSIS
    在带御主资料的全语音 PPT 中，为全部职介插入总起页和统计总结页。

.DESCRIPTION
    - 第 21 页作为职介总起页模板，第 42 页作为统计总结页模板；
    - 保留原有 176 张从者页及其自动播放语音、动态翻页时间；
    - 使用 analyze_class_statistics.py 与 fgo.wiki 素材下载器生成的 JSON；
    - 新增总起页自动停留 3.5 秒，总结页自动停留 8 秒；
    - 最高/最低持有率并列时，统计 JSON 已按工作簿实装顺序选取最早者。
#>

param(
    [string]$InputPath = ".\FGO报菜名_v2_全部从者_全语音_御主资料.pptx",
    [string]$StatisticsPath = ".\职介统计_含素材.json",
    [string]$OutputPath = ".\FGO报菜名_v2_全部从者_全语音_御主资料_全统计.pptx",
    [switch]$Force,
    [string]$PreviewDirectory = ".\职介统计预览"
)

$ErrorActionPreference = "Stop"

$msoFalse = 0
$msoTrue = -1
$msoPicture = 13
$ppAdvanceOnTime = 2
$ppSaveAsOpenXMLPresentation = 24

$classOrder = @(
    "Saber", "Archer", "Lancer", "Rider", "Caster", "Assassin", "Berserker",
    "Ruler", "Avenger", "MoonCancer", "AlterEgo", "Foreigner", "Pretender", "Beast"
)

function Get-FullPath([string]$Path) {
    return [IO.Path]::GetFullPath((Join-Path (Get-Location) $Path))
}

function Release-ComObject($Object) {
    if ($null -ne $Object -and [Runtime.InteropServices.Marshal]::IsComObject($Object)) {
        [void][Runtime.InteropServices.Marshal]::ReleaseComObject($Object)
    }
}

function Get-ShapeText($Shape) {
    try {
        if ($Shape.HasTextFrame -eq $msoTrue -and $Shape.TextFrame.HasText -eq $msoTrue) {
            return [string]$Shape.TextFrame.TextRange.Text
        }
    }
    catch {
        return ""
    }
    return ""
}

function Get-ServantName($Slide) {
    for ($i = 1; $i -le $Slide.Shapes.Count; $i++) {
        $shape = $Slide.Shapes.Item($i)
        try {
            $text = (Get-ShapeText $shape) -replace "[\r\n]", ""
            if ($text -match '^★(.+)★$') {
                return $Matches[1]
            }
        }
        finally {
            Release-ComObject $shape
        }
    }
    return $null
}

function Find-TextShape($Slide, [string]$Needle) {
    for ($i = 1; $i -le $Slide.Shapes.Count; $i++) {
        $shape = $Slide.Shapes.Item($i)
        $text = Get-ShapeText $shape
        if ($text.Contains($Needle)) {
            return $shape
        }
        Release-ComObject $shape
    }
    throw "第 $($Slide.SlideIndex) 页找不到包含【$Needle】的文本框。"
}

function Find-TemplatePicture($Slide, [ValidateScript({ $_ -in @("Intro", "Highest", "Lowest") })][string]$Role) {
    $candidates = @()
    for ($i = 1; $i -le $Slide.Shapes.Count; $i++) {
        $shape = $Slide.Shapes.Item($i)
        if ($shape.Type -eq $msoPicture) {
            $area = [double]$shape.Width * [double]$shape.Height
            $candidates += [pscustomobject]@{
                Shape = $shape
                Area  = $area
                Left  = [double]$shape.Left
                Width = [double]$shape.Width
            }
        }
        else {
            Release-ComObject $shape
        }
    }

    try {
        if ($Role -eq "Intro") {
            $match = @($candidates | Where-Object { $_.Width -lt 300 } | Sort-Object Area | Select-Object -First 1)
        }
        else {
            $portraits = @($candidates | Where-Object { $_.Width -gt 250 -and $_.Width -lt 400 } | Sort-Object Left)
            if ($Role -eq "Highest") { $match = @($portraits | Select-Object -First 1) }
            else { $match = @($portraits | Select-Object -Last 1) }
        }
        if ($match.Count -ne 1) {
            throw "第 $($Slide.SlideIndex) 页无法唯一定位 $Role 图片框。"
        }
        $selected = $match[0].Shape
        foreach ($candidate in $candidates) {
            if ($candidate.Shape -ne $selected) { Release-ComObject $candidate.Shape }
        }
        return $selected
    }
    catch {
        foreach ($candidate in $candidates) { Release-ComObject $candidate.Shape }
        throw
    }
}

function Replace-PictureCover($Slide, $OldPicture, [string]$ImagePath, [string]$Name, [string]$AlternativeText) {
    $left = [double]$OldPicture.Left
    $top = [double]$OldPicture.Top
    $width = [double]$OldPicture.Width
    $height = [double]$OldPicture.Height
    $OldPicture.Delete()
    Release-ComObject $OldPicture

    $picture = $Slide.Shapes.AddPicture($ImagePath, $msoFalse, $msoTrue, $left, $top, -1, -1)
    try {
        $naturalWidth = [double]$picture.Width
        $naturalHeight = [double]$picture.Height
        $crop = $picture.PictureFormat.Crop
        try {
            $scale = [Math]::Max($width / $naturalWidth, $height / $naturalHeight)
            $crop.ShapeWidth = $width
            $crop.ShapeHeight = $height
            $crop.PictureWidth = $naturalWidth * $scale
            $crop.PictureHeight = $naturalHeight * $scale
            $crop.PictureOffsetX = 0
            $crop.PictureOffsetY = 0
        }
        finally {
            Release-ComObject $crop
        }
        $picture.Left = $left
        $picture.Top = $top
        $picture.Name = $Name
        $picture.AlternativeText = $AlternativeText
        return $picture
    }
    catch {
        Release-ComObject $picture
        throw
    }
}

function Set-AutomaticAdvance($Slide, [double]$Seconds) {
    $transition = $Slide.SlideShowTransition
    try {
        $transition.AdvanceOnClick = $msoFalse
        $transition.AdvanceOnTime = $msoTrue
        $transition.AdvanceTime = $Seconds
    }
    finally {
        Release-ComObject $transition
    }
}

function Update-IntroSlide($Slide, $ClassStatistics) {
    $className = [string]$ClassStatistics.class
    $title = Find-TextShape $Slide "Saber"
    try {
        $title.TextFrame.TextRange.Text = $className
        $title.AlternativeText = "$className 职介总起页"
    }
    finally {
        Release-ComObject $title
    }

    $oldIcon = Find-TemplatePicture $Slide "Intro"
    $newIcon = Replace-PictureCover `
        -Slide $Slide `
        -OldPicture $oldIcon `
        -ImagePath ([string]$ClassStatistics.icon.path) `
        -Name "StatsClassIcon" `
        -AlternativeText "$className 职阶图标；来源：$($ClassStatistics.icon.source_url)"
    Release-ComObject $newIcon

    $Slide.Tags.Add("StatisticsRole", "Intro")
    $Slide.Tags.Add("StatisticsClass", $className)
    Set-AutomaticAdvance $Slide 3.5
}

function Update-SummarySlide($Slide, $ClassStatistics) {
    $className = [string]$ClassStatistics.class

    $title = Find-TextShape $Slide "资料统计一览"
    try {
        $title.TextFrame.TextRange.Text = "$className 资料统计一览"
        $title.AlternativeText = "$className 职介统计总结"
    }
    finally {
        Release-ComObject $title
    }

    $masterBox = Find-TextShape $Slide "职介最契合"
    try {
        $masterNames = @($ClassStatistics.best_masters | ForEach-Object { [string]$_ })
        $masterText = $masterNames -join "、"
        $masterBox.TextFrame.TextRange.Text = "与$($className)职介最契合`r御主：$masterText"
        $masterBox.TextFrame.AutoSize = 0
        $length = $masterText.Length
        if ($length -le 14) { $fontSize = 25 }
        elseif ($length -le 32) { $fontSize = 20 }
        elseif ($length -le 58) { $fontSize = 16 }
        else { $fontSize = 12.5 }
        $masterBox.TextFrame.TextRange.Font.Size = [single]$fontSize
        $masterBox.AlternativeText = "最契合御主；持有本职介 $($ClassStatistics.best_master_count)/$($ClassStatistics.servant_count)；并列：$masterText"
    }
    finally {
        Release-ComObject $masterBox
    }

    $highestOld = Find-TemplatePicture $Slide "Highest"
    $highestNew = Replace-PictureCover `
        -Slide $Slide `
        -OldPicture $highestOld `
        -ImagePath ([string]$ClassStatistics.highest.image.path) `
        -Name "StatsHighestServant" `
        -AlternativeText "持有率最高：$($ClassStatistics.highest.name)，$($ClassStatistics.highest.holding_count)/$($ClassStatistics.master_count)；来源：$($ClassStatistics.highest.image.source_url)"
    Release-ComObject $highestNew

    $lowestOld = Find-TemplatePicture $Slide "Lowest"
    $lowestNew = Replace-PictureCover `
        -Slide $Slide `
        -OldPicture $lowestOld `
        -ImagePath ([string]$ClassStatistics.lowest.image.path) `
        -Name "StatsLowestServant" `
        -AlternativeText "持有率最低：$($ClassStatistics.lowest.name)，$($ClassStatistics.lowest.holding_count)/$($ClassStatistics.master_count)；来源：$($ClassStatistics.lowest.image.source_url)"
    Release-ComObject $lowestNew

    $Slide.Tags.Add("StatisticsRole", "Summary")
    $Slide.Tags.Add("StatisticsClass", $className)
    Set-AutomaticAdvance $Slide 8.0
}

function Get-TaggedSlide($Presentation, [string]$Role, [string]$ClassName) {
    for ($i = 1; $i -le $Presentation.Slides.Count; $i++) {
        $slide = $Presentation.Slides.Item($i)
        if ($slide.Tags.Item("StatisticsRole") -eq $Role -and $slide.Tags.Item("StatisticsClass") -eq $ClassName) {
            return $slide
        }
        Release-ComObject $slide
    }
    return $null
}

$input = (Resolve-Path -LiteralPath $InputPath).Path
$statisticsFile = (Resolve-Path -LiteralPath $StatisticsPath).Path
$output = Get-FullPath $OutputPath
$preview = Get-FullPath $PreviewDirectory

if ($input -eq $output) { throw "输出文件不能覆盖输入文件本身。" }
if (Test-Path -LiteralPath $output) {
    if (-not $Force) { throw "输出文件已存在；如需覆盖请加 -Force：$output" }
    Remove-Item -LiteralPath $output -Force
}

$statistics = Get-Content -LiteralPath $statisticsFile -Raw -Encoding UTF8 | ConvertFrom-Json
$statisticsByClass = @{}
$servantToClass = @{}
foreach ($entry in $statistics.classes) {
    $statisticsByClass[[string]$entry.class] = $entry
    foreach ($servant in $entry.servants) {
        $name = [string]$servant.name
        if ($servantToClass.ContainsKey($name)) { throw "统计表中从者名重复：$name" }
        $servantToClass[$name] = [string]$entry.class
    }
}
foreach ($className in $classOrder) {
    if (-not $statisticsByClass.ContainsKey($className)) { throw "统计 JSON 缺少职介：$className" }
    foreach ($key in "highest", "lowest") {
        $asset = $statisticsByClass[$className].$key.image.path
        if (-not (Test-Path -LiteralPath $asset -PathType Leaf)) { throw "缺少满破图：$asset" }
    }
    $icon = $statisticsByClass[$className].icon.path
    if (-not (Test-Path -LiteralPath $icon -PathType Leaf)) { throw "缺少职介图标：$icon" }
}

$powerPoint = $null
$presentation = $null
$validation = $null
try {
    $powerPoint = New-Object -ComObject PowerPoint.Application
    $presentation = $powerPoint.Presentations.Open($input, $msoTrue, $msoFalse, $msoFalse)
    if ($presentation.Slides.Count -ne 198) {
        throw "输入 PPT 页数不是预期的 198 页：$($presentation.Slides.Count)"
    }

    $classSlideIds = @{}
    foreach ($className in $classOrder) { $classSlideIds[$className] = [Collections.Generic.List[int]]::new() }
    $servantSlides = 0
    for ($i = 1; $i -le $presentation.Slides.Count; $i++) {
        $slide = $presentation.Slides.Item($i)
        try {
            $servantName = Get-ServantName $slide
            if ($null -ne $servantName) {
                if (-not $servantToClass.ContainsKey($servantName)) {
                    throw "第 $i 页从者不在统计表中：$servantName"
                }
                $className = $servantToClass[$servantName]
                $classSlideIds[$className].Add([int]$slide.SlideID)
                $servantSlides++
            }
        }
        finally {
            Release-ComObject $slide
        }
    }
    if ($servantSlides -ne 176) { throw "识别到的从者页不是 176 页：$servantSlides" }
    foreach ($className in $classOrder) {
        if ($classSlideIds[$className].Count -ne [int]$statisticsByClass[$className].servant_count) {
            throw "$className 从者页数与统计表不一致。"
        }
    }

    $introTemplate = $presentation.Slides.Item(21)
    $summaryTemplate = $presentation.Slides.Item(42)
    $introTemplate.Tags.Add("StatisticsRole", "Intro")
    $introTemplate.Tags.Add("StatisticsClass", "Saber")
    $summaryTemplate.Tags.Add("StatisticsRole", "Summary")
    $summaryTemplate.Tags.Add("StatisticsClass", "Saber")

    # 从末尾职介向前插入，避免前面职介的页码受到后续插入影响。
    for ($orderIndex = $classOrder.Count - 1; $orderIndex -ge 1; $orderIndex--) {
        $className = $classOrder[$orderIndex]
        $first = $presentation.Slides.FindBySlideID($classSlideIds[$className][0])
        try { $firstIndex = [int]$first.SlideIndex } finally { Release-ComObject $first }

        $introRange = $introTemplate.Duplicate()
        $intro = $introRange.Item(1)
        Release-ComObject $introRange
        $intro.MoveTo($firstIndex)
        $intro.Tags.Add("StatisticsRole", "Intro")
        $intro.Tags.Add("StatisticsClass", $className)
        Release-ComObject $intro

        $lastId = $classSlideIds[$className][$classSlideIds[$className].Count - 1]
        $last = $presentation.Slides.FindBySlideID($lastId)
        try { $summaryIndex = [int]$last.SlideIndex + 1 } finally { Release-ComObject $last }

        $summaryRange = $summaryTemplate.Duplicate()
        $summary = $summaryRange.Item(1)
        Release-ComObject $summaryRange
        $summary.MoveTo($summaryIndex)
        $summary.Tags.Add("StatisticsRole", "Summary")
        $summary.Tags.Add("StatisticsClass", $className)
        Release-ComObject $summary
    }

    foreach ($className in $classOrder) {
        Write-Host "更新 $className 总起页与总结页……"
        $intro = Get-TaggedSlide $presentation "Intro" $className
        $summarySlide = Get-TaggedSlide $presentation "Summary" $className
        if ($null -eq $intro -or $null -eq $summarySlide) { throw "$className 缺少插入的统计页。" }
        try {
            Update-IntroSlide $intro $statisticsByClass[$className]
            Update-SummarySlide $summarySlide $statisticsByClass[$className]
        }
        finally {
            Release-ComObject $intro
            Release-ComObject $summarySlide
        }
    }

    $presentation.SlideShowSettings.AdvanceMode = $ppAdvanceOnTime
    Write-Host "正在保存：$output"
    $presentation.SaveAs($output, $ppSaveAsOpenXMLPresentation)
    $presentation.Close()
    Release-ComObject $presentation
    $presentation = $null

    Write-Host "正在复开验证 224 页结构、图片、音频与自动翻页……"
    $validation = $powerPoint.Presentations.Open($output, $msoTrue, $msoFalse, $msoFalse)
    if ($validation.Slides.Count -ne 224) { throw "成品页数不是 224：$($validation.Slides.Count)" }
    if ($validation.SlideShowSettings.AdvanceMode -ne $ppAdvanceOnTime) { throw "成品没有设置按时间自动放映。" }

    $introCount = 0
    $summaryCount = 0
    $voiceCount = 0
    $servantCount = 0
    foreach ($className in $classOrder) {
        $intro = Get-TaggedSlide $validation "Intro" $className
        $summarySlide = Get-TaggedSlide $validation "Summary" $className
        if ($null -eq $intro -or $null -eq $summarySlide) { throw "$className 标签页复开后缺失。" }
        try {
            if (-not $intro.SlideShowTransition.AdvanceOnTime) { throw "$className 总起页未自动翻页。" }
            if ([Math]::Abs([double]$intro.SlideShowTransition.AdvanceTime - 3.5) -gt 0.01) { throw "$className 总起页时间错误。" }
            if (-not $summarySlide.SlideShowTransition.AdvanceOnTime) { throw "$className 总结页未自动翻页。" }
            if ([Math]::Abs([double]$summarySlide.SlideShowTransition.AdvanceTime - 8.0) -gt 0.01) { throw "$className 总结页时间错误。" }
            [void](Find-TextShape $intro $className)
            [void](Find-TextShape $summarySlide "$className 资料统计一览")
            [void]$summarySlide.Shapes.Item("StatsHighestServant")
            [void]$summarySlide.Shapes.Item("StatsLowestServant")
            $introCount++
            $summaryCount++
        }
        finally {
            Release-ComObject $intro
            Release-ComObject $summarySlide
        }
    }

    for ($i = 1; $i -le $validation.Slides.Count; $i++) {
        $slide = $validation.Slides.Item($i)
        try {
            if ($null -ne (Get-ServantName $slide)) { $servantCount++ }
            for ($s = 1; $s -le $slide.Shapes.Count; $s++) {
                $shape = $slide.Shapes.Item($s)
                try {
                    if ($shape.Name -eq "ServantVoice") {
                        $voiceCount++
                        if ($shape.AnimationSettings.PlaySettings.PlayOnEntry -ne $msoTrue) {
                            throw "第 $i 页语音不再自动播放。"
                        }
                    }
                }
                finally { Release-ComObject $shape }
            }
        }
        finally { Release-ComObject $slide }
    }
    if ($servantCount -ne 176) { throw "成品从者页不是 176：$servantCount" }
    if ($voiceCount -ne 176) { throw "成品语音不是 176：$voiceCount" }

    [IO.Directory]::CreateDirectory($preview) | Out-Null
    foreach ($className in $classOrder) {
        $summarySlide = Get-TaggedSlide $validation "Summary" $className
        try {
            $summarySlide.Export((Join-Path $preview ("{0:D2}_{1}.png" -f ($classOrder.IndexOf($className) + 1), $className)), "PNG", 1920, 1080)
        }
        finally { Release-ComObject $summarySlide }
    }

    Write-Host ""
    Write-Host "已生成：$output"
    Write-Host "总页数：$($validation.Slides.Count)"
    Write-Host "职介总起页：$introCount；统计总结页：$summaryCount"
    Write-Host "从者页：$servantCount；自动播放语音：$voiceCount"
    Write-Host "统计页预览：$preview"
}
finally {
    if ($null -ne $validation) {
        try { $validation.Close() } catch {}
        Release-ComObject $validation
    }
    if ($null -ne $presentation) {
        try { $presentation.Close() } catch {}
        Release-ComObject $presentation
    }
    if ($null -ne $powerPoint) {
        try { $powerPoint.Quit() } catch {}
        Release-ComObject $powerPoint
    }
    [GC]::Collect()
    [GC]::WaitForPendingFinalizers()
}
