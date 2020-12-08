# .SYNOPSIS
# Add-AppxDevPackage.ps1 is a PowerShell script designed to install app
# We've carved that up to just leave the certificate installation part

param([string]$CertificatePath = $null, [string]$PfxPath = $null, [string]$OpenSSL)

$ErrorActionPreference = "Stop"
Import-LocalizedData -BindingVariable UiStrings

$ErrorCodes = Data {
    ConvertFrom-StringData @'
    Success = 0
    NoScriptPath = 1
    NoPackageFound = 2
    ManyPackagesFound = 3
    NoCertificateFound = 4
    ManyCertificatesFound = 5
    BadCertificate = 6
    PackageUnsigned = 7
    CertificateMismatch = 8
    ForceElevate = 9
    LaunchAdminFailed = 10
    GetDeveloperLicenseFailed = 11
    InstallCertificateFailed = 12
    AddPackageFailed = 13
    ForceDeveloperLicense = 14
    CertUtilInstallFailed = 17
    CertIsCA = 18
    BannedEKU = 19
    NoBasicConstraints = 20
    NoCodeSigningEku = 21
    InstallCertificateCancelled = 22
    BannedKeyUsage = 23
'@
}

function PrintMessageAndExit($ErrorMessage, $ReturnCode)
{
    Write-Host $ErrorMessage
    if (!$Force)
    {
        Start-Sleep 5
    }
    exit $ReturnCode
}

#
# Warns the user about installing certificates, and presents a Yes/No prompt
# to confirm the action.  The default is set to No.
#
function ConfirmCertificateInstall
{
    Write-Warning $UiStrings.WarningInstallCert
    Write-Host $UiStrings.WarningPromptContinue

    while ($True)
    {
        $Answer = Read-Host $UiStrings.PromptText

        if ($Answer -eq $UiStrings.PromptYesCharacter -or $Answer -eq $UiStrings.PromptYesString)
        {
            return $True
        }
        if (!$Answer -or $Answer -eq $UiStrings.PromptNoCharacter -or $Answer -eq $UiStrings.PromptNoString)
        {
            return $False
        }
    }
}

#
# Validates whether a file is a valid certificate using CertUtil.
# This needs to be done before calling Get-PfxCertificate on the file, otherwise
# the user will get a cryptic "Password: " prompt for invalid certs.
#
function ValidateCertificateFormat($FilePath)
{
    # certutil -verify prints a lot of text that we don't need, so it's redirected to $null here
    certutil.exe -verify $FilePath > $null
    if ($LastExitCode -lt 0)
    {
        PrintMessageAndExit ($UiStrings.ErrorBadCertificate -f $FilePath, $LastExitCode) $ErrorCodes.BadCertificate
    }
}

#
# Verify that the developer certificate meets the following restrictions:
#   - The certificate must contain a Basic Constraints extension, and its
#     Certificate Authority (CA) property must be false.
#   - The certificate's Key Usage extension must be either absent, or set to
#     only DigitalSignature.
#   - The certificate must contain an Extended Key Usage (EKU) extension with
#     Code Signing usage.
#   - The certificate must NOT contain any other EKU except Code Signing and
#     Lifetime Signing.
#
# These restrictions are enforced to decrease security risks that arise from
# trusting digital certificates.
#
function CheckCertificateRestrictions
{
    Set-Variable -Name BasicConstraintsExtensionOid -Value "2.5.29.19" -Option Constant
    Set-Variable -Name KeyUsageExtensionOid -Value "2.5.29.15" -Option Constant
    Set-Variable -Name EkuExtensionOid -Value "2.5.29.37" -Option Constant
    Set-Variable -Name CodeSigningEkuOid -Value "1.3.6.1.5.5.7.3.3" -Option Constant
    Set-Variable -Name LifetimeSigningEkuOid -Value "1.3.6.1.4.1.311.10.3.13" -Option Constant

    $CertificateExtensions = (Get-PfxCertificate $CertificatePath).Extensions
    $HasBasicConstraints = $false
    $HasCodeSigningEku = $false

    foreach ($Extension in $CertificateExtensions)
    {
        # Certificate must contain the Basic Constraints extension
        if ($Extension.oid.value -eq $BasicConstraintsExtensionOid)
        {
            # CA property must be false
            if ($Extension.CertificateAuthority)
            {
                PrintMessageAndExit $UiStrings.ErrorCertIsCA $ErrorCodes.CertIsCA
            }
            $HasBasicConstraints = $true
        }

        # If key usage is present, it must be set to digital signature
        elseif ($Extension.oid.value -eq $KeyUsageExtensionOid)
        {
            if ($Extension.KeyUsages -ne "DigitalSignature")
            {
                PrintMessageAndExit ($UiStrings.ErrorBannedKeyUsage -f $Extension.KeyUsages) $ErrorCodes.BannedKeyUsage
            }
        }

        elseif ($Extension.oid.value -eq $EkuExtensionOid)
        {
            # Certificate must contain the Code Signing EKU
            $EKUs = $Extension.EnhancedKeyUsages.Value
            if ($EKUs -contains $CodeSigningEkuOid)
            {
                $HasCodeSigningEKU = $True
            }

            # EKUs other than code signing and lifetime signing are not allowed
            foreach ($EKU in $EKUs)
            {
                if ($EKU -ne $CodeSigningEkuOid -and $EKU -ne $LifetimeSigningEkuOid)
                {
                    PrintMessageAndExit ($UiStrings.ErrorBannedEKU -f $EKU) $ErrorCodes.BannedEKU
                }
            }
        }
    }

    if (!$HasBasicConstraints)
    {
        PrintMessageAndExit $UiStrings.ErrorNoBasicConstraints $ErrorCodes.NoBasicConstraints
    }
    if (!$HasCodeSigningEKU)
    {
        PrintMessageAndExit $UiStrings.ErrorNoCodeSigningEku $ErrorCodes.NoCodeSigningEku
    }
}

#
# Performs operations that require administrative privileges:
#   - Install the developer certificate (if -Force is not specified, also prompts the user to confirm)
#
function DoElevatedOperations
{
    if ($CertificatePath)
    {
        Write-Host $UiStrings.InstallingCertificate

        # Make sure certificate format is valid and usage constraints are followed
        ValidateCertificateFormat $CertificatePath
        CheckCertificateRestrictions

        # If -Force is not specified, warn the user and get consent
        if (ConfirmCertificateInstall)
        {
            # Add cert to store
            certutil.exe -addstore TrustedPeople $CertificatePath
            if ($LastExitCode -lt 0)
            {
                PrintMessageAndExit ($UiStrings.ErrorCertUtilInstallFailed -f $LastExitCode) $ErrorCodes.CertUtilInstallFailed
            }
        }
        else
        {
            PrintMessageAndExit $UiStrings.ErrorInstallCertificateCancelled $ErrorCodes.InstallCertificateCancelled
        }
    }
    Start-Sleep 5
}

#
# Launches an elevated process running the current script to perform tasks
# that require administrative privileges.  This function waits until the
# elevated process terminates, and checks whether those tasks were successful.
#
function LaunchElevated
{
    # Set up command line arguments to the elevated process
    $RelaunchArgs = '-ExecutionPolicy Unrestricted -file "' + $ScriptPath + '"'    
    $RelaunchArgs += ' -CertificatePath "' + $DeveloperCertificatePath.FullName + '"'

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
        Start-Sleep -Seconds 2
    }
}

#
# Main script logic when the user launches the script without parameters.
#
function DoStandardOperations
{
    $OpenSSLExe = $OpenSSL + '\openssl.exe'
    $OpenSSLConfig = $OpenSSL + '\openssl.cnf'

    [Environment]::SetEnvironmentVariable("OPENSSL_CONF", $OpenSSLConfig, "Process")

    # Create the certificate file from the given pfx with a non-password password
    &$OpenSSLExe pkcs12 -passin pass: -in $PfxPath -out gmtempcert.crt -nokeys -clcerts
    &$OpenSSLExe x509 -inform pem -in gmtempcert.crt -outform der -out gmtempcert.cer
    Remove-Item gmtempcert.crt

    # List all .cer files in the current directory
    $DeveloperCertificatePath = Get-ChildItem *.cer | Where-Object { $_.Mode -NotMatch "d" }

    # There must be exactly 1 certificate
    if ($DeveloperCertificatePath.Count -lt 1)
    {
        PrintMessageAndExit $UiStrings.ErrorNoCertificateFound $ErrorCodes.NoCertificateFound
    }
    elseif ($DeveloperCertificatePath.Count -gt 1)
    {
        PrintMessageAndExit $UiStrings.ErrorManyCertificatesFound $ErrorCodes.ManyCertificatesFound
    }

    # The .cer file must have the format of a valid certificate
    ValidateCertificateFormat $DeveloperCertificatePath

    # Now install the certificate
    $IsAlreadyElevated = ([Security.Principal.WindowsIdentity]::GetCurrent().Groups.Value -contains "S-1-5-32-544")
    if ($IsAlreadyElevated)
    {
        if ($Force -and $NeedInstallCertificate)
        {
            Write-Warning $UiStrings.WarningInstallCert
        }
    }
    else
    {
        if ($Force)
        {
            PrintMessageAndExit $UiStrings.ErrorForceElevate $ErrorCodes.ForceElevate
        }
        else
        {
            Write-Host $UiStrings.ElevateActionsContinue
            Start-Sleep 5
        }
    }
    LaunchElevated
}

#
# Main script entry point
#
if ($CertificatePath)
{
    DoElevatedOperations
}
else
{
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
        Remove-Item gmtempcert.cer
    }
    catch
    {
        Start-Sleep 5
    }
}