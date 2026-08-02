param(
    [string]$PresentationPath = ".\FGO报菜名_v2_全部从者_全语音_御主资料.pptx",
    [string]$OutputDirectory = ".\_statistics_preview",
    [int[]]$SlideNumbers = @(21, 42)
)

$ErrorActionPreference = "Stop"
$source = (Resolve-Path -LiteralPath $PresentationPath).Path
$output = [IO.Path]::GetFullPath((Join-Path (Get-Location) $OutputDirectory))
[IO.Directory]::CreateDirectory($output) | Out-Null

$powerPoint = $null
$presentation = $null
try {
    $powerPoint = New-Object -ComObject PowerPoint.Application
    $presentation = $powerPoint.Presentations.Open($source, $true, $true, $false)
    foreach ($number in $SlideNumbers) {
        $slide = $presentation.Slides.Item($number)
        try {
            $slide.Export((Join-Path $output "slide_$number.png"), "PNG", 1920, 1080)
        }
        finally {
            [void][Runtime.InteropServices.Marshal]::ReleaseComObject($slide)
        }
    }
    "已导出模板预览：$output"
}
finally {
    if ($null -ne $presentation) {
        $presentation.Close()
        [void][Runtime.InteropServices.Marshal]::ReleaseComObject($presentation)
    }
    if ($null -ne $powerPoint) {
        $powerPoint.Quit()
        [void][Runtime.InteropServices.Marshal]::ReleaseComObject($powerPoint)
    }
    [GC]::Collect()
    [GC]::WaitForPendingFinalizers()
}
