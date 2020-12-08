#!/bin/bash

SCRIPT_DIR=$(readlink -f $(dirname ${BASH_SOURCE[0]}))
cd $SCRIPT_DIR
YYAppName=${YYPackageName}
NEW=$SCRIPT_DIR/$YYAppName

#set permissions
chmod +x $YYAppName/${YYExeName}
chmod +x $YYAppName/$YYAppName.desktop

#set the path in the .desktop file
OLD="YYAppExePath"
echo path to exe: $NEW
sed "s%$OLD%$NEW%g" $YYAppName/$YYAppName.desktop > ~/Desktop/$YYAppName.desktop
chmod +x ~/Desktop/$YYAppName.desktop

#move to desktop
#mv -f $YYAppName/$YYAppName.desktop ~/Desktop

echo Done






