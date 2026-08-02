<#
.SYNOPSIS
    仅调整全统计 PPT 中职介总起页和总结页的自动翻页时间。

.DESCRIPTION
    总起页默认 1.5 秒，总结页默认 3 秒；关闭这两类页面的点击翻页。
    原文件不会被覆盖，输出为带“_自动切换”后缀的新文件。
#>

param(
    [string]$InputPath = ".\FGO报菜名_v2_全部从者_全语音_御主资料_全统计.pptx",
    [string]$OutputPath = ".\FGO报菜名_v2_全部从者_全语音_御主资料_全统计_自动切换.pptx",
    [double]$IntroSeconds = 1.5,
    [double]$SummarySeconds = 3.0,
    [switch]$Force
)

$ErrorActionPreference = "Stop"
$msoFalse = 0
$msoTrue = -1
$ppAdvanceOnTime = 2
$ppSaveAsOpenXMLPresentation = 24

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
    catch {}
    return ""
}

function Get-SlideText($Slide) {
    $parts = [Collections.Generic.List[string]]::new()
    for ($i = 1; $i -le $Slide.Shapes.Count; $i++) {
        $shape = $Slide.Shapes.Item($i)
        try {
            $text = Get-ShapeText $shape
            if (-not [string]::IsNullOrEmpty($text)) { $parts.Add($text) }
        }
        finally { Release-ComObject $shape }
    }
    return $parts -join ""
}

function Get-ServantName($Slide) {
    for ($i = 1; $i -le $Slide.Shapes.Count; $i++) {
        $shape = $Slide.Shapes.Item($i)
        try {
            $text = (Get-ShapeText $shape) -replace "[\r\n]", ""
            if ($text -match '^★(.+)★$') { return $Matches[1] }
        }
        finally { Release-ComObject $shape }
    }
    return $null
}

function Has-NamedShape($Slide, [string]$Name) {
    try {
        $shape = $Slide.Shapes.Item($Name)
        Release-ComObject $shape
        return $true
    }
    catch { return $false }
}

function Get-StatisticsRole($Slide) {
    $tagRole = [string]$Slide.Tags.Item("StatisticsRole")
    if ($tagRole -in @("Intro", "Summary")) { return $tagRole }

    $text = Get-SlideText $Slide
    if ($text.Contains("资料统计一览")) { return "Summary" }
    if ((Has-NamedShape $Slide "StatsClassIcon") -and $text -notmatch '^★') { return "Intro" }
    return $null
}

function Set-AutomaticAdvance($Slide, [double]$Seconds) {
    $transition = $Slide.SlideShowTransition
    try {
        $transition.AdvanceOnClick = $msoFalse
        $transition.AdvanceOnTime = $msoTrue
        $transition.AdvanceTime = $Seconds
    }
    finally { Release-ComObject $transition }
}

function Get-VoiceShape($Slide) {
    try { return $Slide.Shapes.Item("ServantVoice") }
    catch { return $null }
}

$input = (Resolve-Path -LiteralPath $InputPath).Path
$output = [IO.Path]::GetFullPath((Join-Path (Get-Location) $OutputPath))
if ($input -eq $output) { throw "输出路径不能与输入路径相同。" }
if (Test-Path -LiteralPath $output) {
    if (-not $Force) { throw "输出文件已存在；如需覆盖请加 -Force：$output" }
    Remove-Item -LiteralPath $output -Force
}

$powerPoint = $null
$presentation = $null
$validation = $null
try {
    $powerPoint = New-Object -ComObject PowerPoint.Application
    $presentation = $powerPoint.Presentations.Open($input, $msoTrue, $msoFalse, $msoFalse)
    if ($presentation.Slides.Count -ne 224) {
        throw "输入文件不是预期的 224 页：$($presentation.Slides.Count)"
    }

    $introCount = 0
    $summaryCount = 0
    $servantSettings = @{}
    $voiceCount = 0

    for ($i = 1; $i -le $presentation.Slides.Count; $i++) {
        $slide = $presentation.Slides.Item($i)
        try {
            $role = Get-StatisticsRole $slide
            if ($role -eq "Intro") {
                Set-AutomaticAdvance $slide $IntroSeconds
                $introCount++
            }
            elseif ($role -eq "Summary") {
                Set-AutomaticAdvance $slide $SummarySeconds
                $summaryCount++
            }

            $servantName = Get-ServantName $slide
            if ($null -ne $servantName) {
                $transition = $slide.SlideShowTransition
                try {
                    $servantSettings[$servantName] = [pscustomobject]@{
                        AdvanceTime    = [double]$transition.AdvanceTime
                        AdvanceOnTime  = [int]$transition.AdvanceOnTime
                        AdvanceOnClick = [int]$transition.AdvanceOnClick
                    }
                }
                finally { Release-ComObject $transition }

                $voice = Get-VoiceShape $slide
                if ($null -eq $voice) { throw "第 $i 页【$servantName】缺少 ServantVoice。" }
                try {
                    if ($voice.AnimationSettings.PlaySettings.PlayOnEntry -ne $msoTrue) {
                        throw "第 $i 页【$servantName】语音不是自动播放。"
                    }
                    $voiceCount++
                }
                finally { Release-ComObject $voice }
            }
        }
        finally { Release-ComObject $slide }
    }

    if ($introCount -ne 14) { throw "识别到的总起页不是 14 页：$introCount" }
    if ($summaryCount -ne 14) { throw "识别到的总结页不是 14 页：$summaryCount" }
    if ($servantSettings.Count -ne 176 -or $voiceCount -ne 176) {
        throw "从者页或语音数量异常：从者 $($servantSettings.Count)，语音 $voiceCount"
    }

    $presentation.SlideShowSettings.AdvanceMode = $ppAdvanceOnTime
    Write-Host "正在保存：$output"
    $presentation.SaveAs($output, $ppSaveAsOpenXMLPresentation)
    $presentation.Close()
    Release-ComObject $presentation
    $presentation = $null

    Write-Host "正在复开验证计时与语音……"
    $validation = $powerPoint.Presentations.Open($output, $msoTrue, $msoFalse, $msoFalse)
    if ($validation.Slides.Count -ne 224) { throw "成品页数发生变化。" }
    if ($validation.SlideShowSettings.AdvanceMode -ne $ppAdvanceOnTime) { throw "成品没有按时间放映。" }

    $validatedIntro = 0
    $validatedSummary = 0
    $validatedServants = 0
    $validatedVoices = 0
    for ($i = 1; $i -le $validation.Slides.Count; $i++) {
        $slide = $validation.Slides.Item($i)
        try {
            $role = Get-StatisticsRole $slide
            if ($role -eq "Intro") {
                if (-not $slide.SlideShowTransition.AdvanceOnTime -or $slide.SlideShowTransition.AdvanceOnClick) {
                    throw "第 $i 页总起页自动切换设置错误。"
                }
                if ([Math]::Abs([double]$slide.SlideShowTransition.AdvanceTime - $IntroSeconds) -gt 0.01) {
                    throw "第 $i 页总起页时间错误。"
                }
                $validatedIntro++
            }
            elseif ($role -eq "Summary") {
                if (-not $slide.SlideShowTransition.AdvanceOnTime -or $slide.SlideShowTransition.AdvanceOnClick) {
                    throw "第 $i 页总结页自动切换设置错误。"
                }
                if ([Math]::Abs([double]$slide.SlideShowTransition.AdvanceTime - $SummarySeconds) -gt 0.01) {
                    throw "第 $i 页总结页时间错误。"
                }
                $validatedSummary++
            }

            $servantName = Get-ServantName $slide
            if ($null -ne $servantName) {
                if (-not $servantSettings.ContainsKey($servantName)) { throw "成品出现未知从者：$servantName" }
                $before = $servantSettings[$servantName]
                if ([Math]::Abs([double]$slide.SlideShowTransition.AdvanceTime - $before.AdvanceTime) -gt 0.01) {
                    throw "【$servantName】从者页计时被改变。"
                }
                if ([int]$slide.SlideShowTransition.AdvanceOnTime -ne $before.AdvanceOnTime -or
                    [int]$slide.SlideShowTransition.AdvanceOnClick -ne $before.AdvanceOnClick) {
                    throw "【$servantName】从者页翻页模式被改变。"
                }
                $validatedServants++

                $voice = Get-VoiceShape $slide
                if ($null -eq $voice) { throw "成品【$servantName】缺少语音。" }
                try {
                    if ($voice.AnimationSettings.PlaySettings.PlayOnEntry -ne $msoTrue) {
                        throw "成品【$servantName】语音不是自动播放。"
                    }
                    $validatedVoices++
                }
                finally { Release-ComObject $voice }
            }
        }
        finally { Release-ComObject $slide }
    }

    if ($validatedIntro -ne 14 -or $validatedSummary -ne 14 -or
        $validatedServants -ne 176 -or $validatedVoices -ne 176) {
        throw "复开计数不一致：总起 $validatedIntro，总结 $validatedSummary，从者 $validatedServants，语音 $validatedVoices"
    }

    Write-Host ""
    Write-Host "已生成：$output"
    Write-Host "总起页：14 页 × $IntroSeconds 秒"
    Write-Host "总结页：14 页 × $SummarySeconds 秒"
    Write-Host "从者页计时未改变：176 页；自动播放语音：176 条"
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
