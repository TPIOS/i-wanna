param([switch]$caption, [switch]$major, [switch]$minor, [switch]$revision)

if ($major)
{
    $edition = [System.Environment]::OSVersion.Version
    $edition.Major
}
if ($minor)
{
    $edition = [System.Environment]::OSVersion.Version
    $edition.Minor
}
if ($revision)
{
    $edition = [System.Environment]::OSVersion.Version
    $edition.Revision
}

if ($caption)
{
    $win = (Get-WmiObject -class Win32_OperatingSystem).Caption
    $win
}


