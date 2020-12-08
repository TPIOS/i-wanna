param([string]$PackagePath = $null, [switch]$UpdatePackage = $false, [switch]$RegisterPackage = $false)

Write-Host "Path="$PackagePath

function PrintMessageAndExit($ErrorMessage, $ReturnCode)
{
    Write-Host $ErrorMessage
    exit $ReturnCode
}

function UpdatePackage
{
    $DeveloperPackagePath = Get-ChildItem (Join-Path $PackagePath "*.appx") | Where-Object { $_.Mode -NotMatch "d" }
    $DependencyPackagesDir = (Join-Path $PackagePath "Dependencies")
    $DependencyPackages = @()

    if (Test-Path $DependencyPackagesDir)
    {
        # Get architecture-neutral dependencies
        $DependencyPackages += Get-ChildItem (Join-Path $DependencyPackagesDir "*.appx") | Where-Object { $_.Mode -NotMatch "d" }

        # Get architecture-specific dependencies
        if (($Env:Processor_Architecture -eq "x86" -or $Env:Processor_Architecture -eq "amd64") -and (Test-Path (Join-Path $DependencyPackagesDir "x86")))
        {
            $DependencyPackages += Get-ChildItem (Join-Path $DependencyPackagesDir "x86\*.appx") | Where-Object { $_.Mode -NotMatch "d" }
        }
        if (($Env:Processor_Architecture -eq "amd64") -and (Test-Path (Join-Path $DependencyPackagesDir "x64")))
        {
            $DependencyPackages += Get-ChildItem (Join-Path $DependencyPackagesDir "x64\*.appx") | Where-Object { $_.Mode -NotMatch "d" }
        }
        if (($Env:Processor_Architecture -eq "arm") -and (Test-Path (Join-Path $DependencyPackagesDir "arm")))
        {
            $DependencyPackages += Get-ChildItem (Join-Path $DependencyPackagesDir "arm\*.appx") | Where-Object { $_.Mode -NotMatch "d" }
        }
    }

    Write-Host "Updating development package"
    if ($DependencyPackages.FullName.Count -gt 0)
    {
        Add-AppxPackage -Path $DeveloperPackagePath.FullName -Update -DependencyPath $DependencyPackages.FullName -ForceApplicationShutdown
    }
    else
    {
        Add-AppxPackage -Path $DeveloperPackagePath.FullName -Update -ForceApplicationShutdown
    }
}

function RegisterPackage
{
    $ManifestPath = Join-Path $PackagePath "AppxManifest.xml"

    Write-Host "Registering development package"
    Add-AppxPackage -Path $ManifestPath -Register -ForceApplicationShutdown
}

function InstallPackageWithDependencies
{
    $PackageSucceeded = $False
    try
    {
        if ($UpdatePackage)
        {
	    UpdatePackage
        }
        if ($RegisterPackage)
        {
            RegisterPackage
        }
        
        $PackageSucceeded = $?        
    }
    catch
    {
        # Dump details about the last error
        $Error[0]
    }

    if ($PackageSucceeded)
    {
        PrintMessageAndExit "Package installation succeeded" 0
    }
    else
    {    
        PrintMessageAndExit "Package installation failed..." -1
    }
}

InstallPackageWithDependencies