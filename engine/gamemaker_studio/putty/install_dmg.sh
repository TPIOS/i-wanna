#!/usr/bash

set +x

YYInstallDir=${YYMacInstallDir}
YYDMGName=${YYMacDMGName}
YYDmgMD5=${YYMacDMGMD5} 
YYAppStore=${YYMacForAppStore}

# Determine if we need to copy the runner over to this Mac???
YYCopyDMG=1
if [ -d "$YYInstallDir/YoYo Runner.app" ]; then
	if [ -d "$YYInstallDir/.yoyo" ]; then
		#echo "$YYInstallDir/.yoyo" exists
		if [ -f "$YYInstallDir/.yoyo/dmg.info" ]; then
			#echo "$YYInstallDir/.yoyo/dmg.info" exists
			YYStoredMD5=`cat $YYInstallDir/.yoyo/dmg.info`
			# If the MD5's match then everything is good
			if [ "$YYDmgMD5" == "$YYStoredMD5" ]; then
				YYCopyDMG=0
			fi
		fi
	fi
fi

if [ -d "$YYInstallDir/YoYo Runner.app" ]; then
	if [ ! -f "$YYInstallDir/YoYo Runner.app/Contents/Frameworks/libYoYoGamepad.dylib" ]; then
		if [ $YYAppStore == "0" ]; then
			YYCopyDMG=1
		fi
	fi
	
	if [ ! -f "$YYInstallDir/YoYo Runner.app/Contents/Frameworks/libYoYoIAP.dylib" ]; then
		if [ $YYAppStore == "1" ]; then
			YYCopyDMG=1
		fi
	fi
fi

if [ "$YYCopyDMG" == "1" ]; then
	# create the install directory if it does not exist
	[ -d "$YYInstallDir" ] || mkdir -p $YYInstallDir
	# if the DMG exists
	if [ -f "$YYInstallDir/$YYDMGName" ]; then
		
		# Remove any app that may be present
		rm -rf "$YYInstallDir/YoYo Runner.app"

		# mount the DMG and copy the application over then update
		hdiutil mount -quiet $YYInstallDir/$YYDMGName
		cp -R "/Volumes/YoYo Runner/YoYo Runner.app" $YYInstallDir
		hdiutil unmount -quiet  "/Volumes/YoYo Runner/"

		if [ $YYAppStore == "1" ]; then
			rm -f "$YYInstallDir/YoYo Runner.app/Contents/Frameworks/libYoYoGamepad.dylib"						
		else
			rm -f "$YYInstallDir/YoYo Runner.app/Contents/Frameworks/libYoYoIAP.dylib"
		fi

		# record the MD5 
		[ -d "$YYInstallDir/.yoyo" ] || mkdir -p $YYInstallDir/.yoyo
		md5 -q $YYInstallDir/$YYDMGName >$YYInstallDir/.yoyo/dmg.info
		rm -rf $YYInstallDir/$YYDMGName

		# and done
		echo Done

	else
		echo CopyDMG
	fi
else
	if [ $YYAppStore == "1" ]; then
		rm -f "$YYInstallDir/YoYo Runner.app/Contents/Frameworks/libYoYoGamepad.dylib"		
	else
		rm -f "$YYInstallDir/YoYo Runner.app/Contents/Frameworks/libYoYoIAP.dylib"		
	fi

	echo Done
fi




