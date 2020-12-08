#!/usr/bash
YYInstallDir=${YYLinuxInstallDir}
YYAssetZip=${YYLinuxAssetZip} 

#kill currently running runner
pkill GMStudio_runner

#unzip the assets
#run the runner 
cd "$YYInstallDir"
rm -rf assets/
echo unzip $YYAssetZip
unzip -o "$YYAssetZip"

echo run!

# "$YYInstallDir"/runner -game "$YYInstallDir"/game.ios #xdeath
# tell the remote machine to use its own display; open terminal for debug output
#OR xterm -e <command>  - but looks ugly and gnome should always be available on ubuntu ( i think... )
#DISPLAY=:0 LD_LIBRARY_PATH=./lib:$LD_LIBRARY_PATH gnome-terminal -x ./GMStudio_runner 

#xterm has a hold option - only really want to keep it up in case of error though...
#DISPLAY=:0 LD_LIBRARY_PATH=./lib:$LD_LIBRARY_PATH xterm -hold -e ./GMStudio_runner 

#or this to keep terminal for a limited duration-
DISPLAY=:0 LD_LIBRARY_PATH=./lib:$LD_LIBRARY_PATH gnome-terminal -x bash -c "./GMStudio_runner; sleep 10"
#DISPLAY=:0 LD_LIBRARY_PATH=./lib:$LD_LIBRARY_PATH gnome-terminal -x bash -c "./GMStudio_runner; cat"