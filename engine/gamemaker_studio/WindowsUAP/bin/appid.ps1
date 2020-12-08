param([string]$AppName = $null, [switch]$InstallLocation = $false)

$app = Get-AppxPackage | Where-Object {$_.Name -like $AppName }

if($app)
{
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
}