@echo off
set DESTDIR=%1
set RUNNERDIR=%2
set PACKAGENAME=%3
set _ZIP=%4
set _UNZIP=%5

rem remove any existing files, create package directory
pushd %DESTDIR%
IF EXIST %PACKAGENAME% rmdir /S /Q %PACKAGENAME%
mkdir %PACKAGENAME%

rem unzip assets into package directory
%_UNZIP% -q GameAssetsLinux.zip -d %PACKAGENAME%

rem delete yydebug file
del %DESTDIR%%PACKAGENAME%\assets\*.yydebug

rem copy runner & unzip it
xcopy /y %RUNNERDIR%runner.zip %DESTDIR%
%_UNZIP% -q runner.zip -d %PACKAGENAME%

rem zip up package
%_ZIP% -q -r %PACKAGENAME%.zip %PACKAGENAME%

popd