param([string]$AppName = $null, [switch]$InstallLocation = $false)

$app = Get-AppxPackage | Where-Object {$_.Name -like $AppName }
foreach ($id in (Get-AppxPackageManifest $app).package.applications.application.id)
{
    if ($InstallLocation)
    {
        $app.InstallLocation
    }
    else
    {
        $app.packagefamilyname + "!" + $id
    }
}