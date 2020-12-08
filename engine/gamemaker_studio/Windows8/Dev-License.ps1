param([switch]$GetDeveloperLicence = $false, [switch]$CheckDeveloperLicence = $false)

$ErrorActionPreference = "Stop"
Import-LocalizedData -BindingVariable UiStrings

function CheckIfNeedDeveloperLicence
{
    $Result = $true
    try
    {
        $Result = (Get-WindowsDeveloperLicense | Where-Object { $_.IsValid }).Count -eq 0
    }
    catch {}

    return $Result
}

function DoElevatedOperations
{
    Write-Host $UiStrings.GettingDeveloperLicense
    try
    {
        Show-WindowsDeveloperLicenseRegistration
    }
    catch
    {
	    # Dump details about the last error
        $Error[0]
        PrintMessageAndExit $UiStrings.ErrorGetDeveloperLicenseFailed $ErrorCodes.GetDeveloperLicenseFailed
    }
	Pause
}

function DoStandardOperations
{
	$NeedDeveloperLicence = CheckIfNeedDeveloperLicence
	if ($NeedDeveloperLicence)	
	{
		Write-Host $UiStrings.ElevateActionsContinue
		Start-Sleep 5
		LaunchElevated
    }
	else
	{
		Write-Host Developer license already installed
		Start-Sleep 5
	}
}

# Launches an elevated process running the current script to perform tasks
# that require administrative privileges.  This function waits until the
# elevated process terminates, and checks whether those tasks were successful.
function LaunchElevated
{
    # Set up command line arguments to the elevated process
    $RelaunchArgs = '-ExecutionPolicy Unrestricted -file "' + $ScriptPath + '" -GetDeveloperLicence'

    # Launch the process and wait for it to finish
    try
    {
        $AdminProcess = Start-Process "$PsHome\PowerShell.exe" -Verb RunAs -ArgumentList $RelaunchArgs -PassThru
    }
    catch
    {
        $Error[0] # Dump details about the last error
        PrintMessageAndExit $UiStrings.ErrorLaunchAdminFailed $ErrorCodes.LaunchAdminFailed
    }

    while (!($AdminProcess.HasExited))
    {
        Start-Sleep 2
    }

    # Check if all elevated operations were successful
    if (CheckIfNeedDeveloperLicence)
    {
        PrintMessageAndExit $UiStrings.ErrorGetDeveloperLicenseFailed $ErrorCodes.GetDeveloperLicenseFailed
    }
    else
    {
        Write-Host $UiStrings.AcquireLicenseSuccessful
    }
}

#
# Main script entry point
#
if ($GetDeveloperLicence)
{
	# If a developer licence has been requested	
    DoElevatedOperations
}
elseif ($CheckDeveloperLicence)
{
	# If we're just checking the status of the developer licence
	$NeedDeveloperLicence = CheckIfNeedDeveloperLicence
	$NeedDeveloperLicence
}
else
{
	# If we're just running the script as normal it means we're running
	# the first part of requesting a developer licence.
    # Get the path to this script.  This is needed if the script needs to be
    # relaunched as admin.  This must be done first in the script because
    # the MyInvocation variable gets overwritten by function calls.
    $ScriptPath = $null
    try
    {
        $ScriptPath = (Get-Variable MyInvocation).Value.MyCommand.Path
    }
    catch {}

    if (!$ScriptPath)
    {
        PrintMessageAndExit $UiStrings.ErrorNoScriptPath $ErrorCodes.NoScriptPath
    }

    try
    {
        DoStandardOperations
    }
    catch
    {
        Start-Sleep 5
    }
}