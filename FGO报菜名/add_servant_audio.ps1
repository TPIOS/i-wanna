<#
.SYNOPSIS
    为从者 PPT 的每一页嵌入一条最接近 6 秒的语音。

.DESCRIPTION
    - 使用“从者语音选择.json”中的测量结果；
    - 音频嵌入 PPT，不依赖外部文件路径；
    - 进入页面时自动播放，放映时隐藏媒体图标；
    - 禁用鼠标提前翻页，页面自动停留 max(6 秒, 音频时长 + 2.5 秒)；
    - 动态时间向上取整到 0.1 秒，避免截断音频尾音；
    - 设置整个演示文稿使用各页排练计时。

.EXAMPLE
    powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\add_servant_audio.ps1
#>

[CmdletBinding()]
param(
    [Parameter()]
    [string]$InputPpt = ".\FGO报菜名_v2_全部从者.pptx",

    [Parameter()]
    [string]$OutputPpt = ".\FGO报菜名_v2_全部从者_全语音.pptx",

    [Parameter()]
    [string]$ImageRoot = ".\提取结果_vFinal2",

    [Parameter()]
    [string]$VoiceRoot = ".\从者语音",

    [Parameter()]
    [string]$SelectionManifest = ".\从者语音选择.json",

    [Parameter()]
    [string]$SelectorScript = ".\select_servant_voice.py",

    [Parameter()]
    [string]$PythonPath = "C:\Users\Senyue\.conda\envs\fgo\python.exe",

    [Parameter()]
    [string]$FfprobePath = "C:\Users\Senyue\.conda\envs\fgo\Library\bin\ffprobe.exe",

    [Parameter()]
    [switch]$RefreshSelection,

    [Parameter()]
    [ValidateRange(0, 10000)]
    [int]$PageLimit = 0,

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
    return [IO.Path]::Combine((Resolve-Path -LiteralPath $parent).Path, $leaf)
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
    catch { }
    return ""
}

function Get-SlideServantName {
    param(
        [Parameter(Mandatory)]
        $Slide
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
        throw "第 $($Slide.SlideIndex) 页找不到从者标题。"
    }
    return (Get-ShapeText -Shape $candidate).Trim().Trim([char]0x2605)
}

function Remove-ExistingVoiceShape {
    param(
        [Parameter(Mandatory)]
        $Slide
    )

    for ($i = $Slide.Shapes.Count; $i -ge 1; $i--) {
        $shape = $Slide.Shapes.Item($i)
        if ($shape.Name -eq "ServantVoice") {
            $shape.Delete()
        }
    }
}

$resolvedInput = Get-AbsolutePath -Path $InputPpt
$resolvedOutput = Get-AbsolutePath -Path $OutputPpt -AllowMissing
$resolvedImageRoot = Get-AbsolutePath -Path $ImageRoot
$resolvedVoiceRoot = Get-AbsolutePath -Path $VoiceRoot
$resolvedManifest = Get-AbsolutePath -Path $SelectionManifest -AllowMissing
$resolvedSelector = Get-AbsolutePath -Path $SelectorScript
$resolvedPython = Get-AbsolutePath -Path $PythonPath
$resolvedFfprobe = Get-AbsolutePath -Path $FfprobePath

if ($resolvedInput -eq $resolvedOutput) {
    throw "输出文件不能覆盖输入 PPT。"
}

if ($RefreshSelection -or -not (Test-Path -LiteralPath $resolvedManifest)) {
    Write-Host "正在测量全部语音并选择最接近 6 秒的文件……"
    & $resolvedPython $resolvedSelector `
        --voice-root $resolvedVoiceRoot `
        --image-root $resolvedImageRoot `
        --ffprobe $resolvedFfprobe `
        --output $resolvedManifest `
        --target-seconds 6
    if ($LASTEXITCODE -ne 0) {
        throw "语音时长测量失败，退出码：$LASTEXITCODE"
    }
}

$manifest = Get-Content -LiteralPath $resolvedManifest -Raw -Encoding UTF8 | ConvertFrom-Json
if ($manifest.total_servants -ne 176) {
    throw "语音选择清单应有 176 名从者，实际为 $($manifest.total_servants)。"
}

$selectionByName = @{}
foreach ($selection in $manifest.selections) {
    $name = [string]$selection.servant
    if ($selectionByName.ContainsKey($name)) {
        throw "语音选择清单中从者名重复：$name"
    }
    if (-not (Test-Path -LiteralPath $selection.selected_file -PathType Leaf)) {
        throw "选择的语音文件不存在：$($selection.selected_file)"
    }
    $selectionByName[$name] = $selection
}

$classByServant = @{}
foreach ($classDir in Get-ChildItem -LiteralPath $resolvedImageRoot -Directory) {
    foreach ($servantDir in Get-ChildItem -LiteralPath $classDir.FullName -Directory) {
        if ($classByServant.ContainsKey($servantDir.Name)) {
            throw "从者名跨职介重复：$($servantDir.Name)"
        }
        $classByServant[$servantDir.Name] = $classDir.Name
    }
}

if (Test-Path -LiteralPath $resolvedOutput) {
    if (-not $Force) {
        throw "输出文件已存在：$resolvedOutput。若要覆盖，请添加 -Force。"
    }
    Remove-Item -LiteralPath $resolvedOutput -Force
}

$powerPoint = $null
$presentation = $null
$processed = [Collections.Generic.List[object]]::new()
try {
    $powerPoint = New-Object -ComObject PowerPoint.Application
    # ReadOnly = msoTrue, Untitled = msoFalse, WithWindow = msoFalse
    $presentation = $powerPoint.Presentations.Open($resolvedInput, -1, 0, 0)

    $pageCount = if ($PageLimit -gt 0) {
        [Math]::Min($PageLimit, $presentation.Slides.Count)
    }
    else {
        $presentation.Slides.Count
    }
    if ($PageLimit -eq 0 -and $pageCount -ne $manifest.total_servants) {
        throw "输入 PPT 应有 $($manifest.total_servants) 张从者页，实际为 $pageCount。"
    }

    # ppSlideShowUseSlideTimings = 2。启用动画才能触发 PlayOnEntry。
    $presentation.SlideShowSettings.AdvanceMode = 2
    $presentation.SlideShowSettings.ShowWithAnimation = -1
    $presentation.SlideShowSettings.ShowMediaControls = 0

    Write-Host "准备为 $pageCount 页嵌入语音……"
    for ($i = 1; $i -le $pageCount; $i++) {
        $slide = $presentation.Slides.Item($i)
        try {
            $servantName = Get-SlideServantName -Slide $slide
            if (-not $classByServant.ContainsKey($servantName)) {
                throw "第 $i 页标题无法映射到从者目录：$servantName"
            }
            if (-not $selectionByName.ContainsKey($servantName)) {
                throw "第 $i 页没有语音选择记录：$servantName"
            }

            $className = $classByServant[$servantName]
            $selection = $selectionByName[$servantName]
            if ([string]$selection.class -ne $className) {
                throw "第 $i 页职介不一致：$className / $($selection.class)"
            }

            Remove-ExistingVoiceShape -Slide $slide
            $audioShape = $slide.Shapes.AddMediaObject2(
                [string]$selection.selected_file,
                0,   # LinkToFile = msoFalse
                -1,  # SaveWithDocument = msoTrue
                0,
                0,
                10,
                10
            )
            try {
                $audioShape.Name = "ServantVoice"
                $audioShape.AlternativeText = (
                    "{0} / {1} / {2:N3}s / slide {3:N1}s" -f `
                        $className,
                        $selection.selected_filename,
                        [double]$selection.duration_seconds,
                        [double]$selection.slide_seconds
                )
                # 即使媒体在播放也将图标放到画布外，放映画面中完全不可见。
                $audioShape.Left = -100
                $audioShape.Top = -100

                $animation = $audioShape.AnimationSettings
                $playSettings = $animation.PlaySettings
                try {
                    $animation.Animate = -1
                    $playSettings.PlayOnEntry = -1
                    $playSettings.PauseAnimation = 0
                    $playSettings.HideWhileNotPlaying = -1
                    $playSettings.LoopUntilStopped = 0
                    # ppAdvanceOnTime = 2；延迟 0 秒，避免 PowerPoint 将媒体
                    # 保留为需要单击才触发的动画效果。
                    $animation.AdvanceMode = 2
                    $animation.AdvanceTime = 0
                }
                finally {
                    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($playSettings)
                    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($animation)
                }
            }
            finally {
                [void][Runtime.InteropServices.Marshal]::ReleaseComObject($audioShape)
            }

            $transition = $slide.SlideShowTransition
            $transition.AdvanceOnClick = 0
            $transition.AdvanceOnTime = -1
            $transition.AdvanceTime = [single]$selection.slide_seconds

            $processed.Add([pscustomobject]@{
                Page       = $i
                Class      = $className
                Servant    = $servantName
                File       = [string]$selection.selected_filename
                Duration   = [double]$selection.duration_seconds
                SlideTime  = [double]$selection.slide_seconds
            })
            Write-Host (
                "[{0:D3}/{1:D3}] {2} / {3} / {4} / {5:N3}s -> {6:N1}s" -f `
                    $i,
                    $pageCount,
                    $className,
                    $servantName,
                    $selection.selected_filename,
                    [double]$selection.duration_seconds,
                    [double]$selection.slide_seconds
            )
        }
        finally {
            [void][Runtime.InteropServices.Marshal]::ReleaseComObject($slide)
        }
    }

    Write-Host "正在保存带语音 PPT：$resolvedOutput"
    # ppSaveAsOpenXMLPresentation = 24
    $presentation.SaveAs($resolvedOutput, 24)

    # 重新打开成品，验证嵌入对象和放映属性确实被 PowerPoint 持久化。
    $presentation.Close()
    [void][Runtime.InteropServices.Marshal]::ReleaseComObject($presentation)
    $presentation = $null
    $presentation = $powerPoint.Presentations.Open($resolvedOutput, -1, 0, 0)
    if ($presentation.SlideShowSettings.AdvanceMode -ne 2) {
        throw "成品未保存【使用幻灯片计时】放映模式。"
    }

    Write-Host "正在验证成品中的自动播放和翻页计时……"
    for ($i = 1; $i -le $processed.Count; $i++) {
        $slide = $presentation.Slides.Item($i)
        $voiceShape = $null
        try {
            try {
                $voiceShape = $slide.Shapes.Item("ServantVoice")
            }
            catch {
                throw "成品第 $i 页缺少 ServantVoice 媒体对象。"
            }

            $animation = $voiceShape.AnimationSettings
            $playSettings = $animation.PlaySettings
            try {
                if ($playSettings.PlayOnEntry -ne -1) {
                    throw "成品第 $i 页未保存自动播放设置。"
                }
                if ($playSettings.HideWhileNotPlaying -ne -1) {
                    throw "成品第 $i 页未保存隐藏媒体对象设置。"
                }
                if ($animation.AdvanceMode -ne 2) {
                    throw "成品第 $i 页媒体动画仍不是自动计时触发。"
                }
                if ([Math]::Abs([double]$animation.AdvanceTime) -gt 0.01) {
                    throw "成品第 $i 页媒体自动播放延迟不为 0 秒。"
                }
            }
            finally {
                [void][Runtime.InteropServices.Marshal]::ReleaseComObject($playSettings)
                [void][Runtime.InteropServices.Marshal]::ReleaseComObject($animation)
            }

            $transition = $slide.SlideShowTransition
            $expectedTime = [double]$processed[$i - 1].SlideTime
            if ($transition.AdvanceOnTime -ne -1) {
                throw "成品第 $i 页未保存自动翻页设置。"
            }
            if ([Math]::Abs([double]$transition.AdvanceTime - $expectedTime) -gt 0.05) {
                throw (
                    "成品第 $i 页翻页时间不正确：{0:N3}s，预期 {1:N3}s。" -f `
                        [double]$transition.AdvanceTime,
                        $expectedTime
                )
            }
        }
        finally {
            if ($null -ne $voiceShape) {
                [void][Runtime.InteropServices.Marshal]::ReleaseComObject($voiceShape)
            }
            [void][Runtime.InteropServices.Marshal]::ReleaseComObject($slide)
        }

        if ($i % 25 -eq 0 -or $i -eq $processed.Count) {
            Write-Host "已验证：$i / $($processed.Count)"
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

$minimumPages = @($processed | Where-Object { [Math]::Abs($_.SlideTime - 6.0) -lt 0.01 })
$dynamicPages = @($processed | Where-Object { $_.SlideTime -gt 6.0 })
$totalSeconds = ($processed | Measure-Object SlideTime -Sum).Sum
$totalMinutes = [Math]::Floor($totalSeconds / 60)
$remainingSeconds = $totalSeconds - ($totalMinutes * 60)
Write-Host "`n已生成：$resolvedOutput"
Write-Host "已嵌入语音：$($processed.Count) 页"
Write-Host "最低 6 秒页面：$($minimumPages.Count)"
Write-Host "按语音长度动态延长页面：$($dynamicPages.Count)"
Write-Host ("总放映时间：{0} 分 {1:N1} 秒" -f $totalMinutes, $remainingSeconds)
