# Powershell -executionpolicy unrestricted ./Register-DevPackage.ps1 {Path to manifest} {Path to .appx}
param([string]$PackagePath = $null, [string]$AppxPath = $null)

function PrintMessageAndExit($ErrorMessage, $ReturnCode)
{
    Write-Host $ErrorMessage
    exit $ReturnCode
}

function InstallDependencies
{
	$ScriptDir = Split-Path -Parent $AppxPath		
	$DependencyPackagesDir = Join-Path $ScriptDir "Dependencies"	
	
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
	
	$Packages = $DependencyPackages.FullName -split " "
	foreach ($Package in $Packages)
	{ 
		if ($Package)
		{
			Write-Host "Installing dependency="$Package
			Add-AppxPackage -Path $Package
		}
	}	
}

# Add an appx package using the -Register flag to install from a folder of unpackaged files
function RegisterPackage
{
    $ManifestPath = Join-Path $PackagePath "AppxManifest.xml"    
    
    $RegistrationSucceeded = $False
    try 
    {
		InstallDependencies

		Write-Host "Registering development package"	
		Add-AppxPackage -Register $ManifestPath

		$RegistrationSucceeded = $?
    }
    catch
    {
        $Error[0]
    }

    if ($RegistrationSucceeded)
    {
        PrintMessageAndExit "Appx Registration Succeeded" 0
    }
    else
    {
        PrintMessageAndExit "Appx Registration Succeeded" -1
    }    
}

RegisterPackage