foreach ($arg in $args)
{
    Get-AppxPackage | Where-Object {$_.Name -like $arg } | Remove-AppxPackage
}