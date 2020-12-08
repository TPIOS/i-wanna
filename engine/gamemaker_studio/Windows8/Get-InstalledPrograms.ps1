param([string]$ProgramName = $null)

$Programs = Get-ItemProperty HKLM:\Software\Wow6432Node\Microsoft\Windows\CurrentVersion\Uninstall\* | Where-Object { $_.DisplayName -like $ProgramName }
Foreach ($name in $Programs)
{
    Write-Host $name.DisplayVersion
}



