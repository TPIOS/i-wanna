#!/usr/bash

YYInstallDir=${YYLinuxInstallDir}
YYRunnerName=${YYLinuxRunnerName}
YYRunnerMD5=${YYLinuxRunnerMD5} 

# Determine if we need to copy the runner over ???

YYCopyDMG=1	
if [ -f "$YYInstallDir/GMStudio_runner" ]; then
	echo "$YYInstallDir/GMStudio_runner" exists
	if [ -d "$YYInstallDir/.yoyo" ]; then
		echo "$YYInstallDir/.yoyo" exists
		if [ -f "$YYInstallDir/.yoyo/runner.info" ]; then
			echo "$YYInstallDir/.yoyo/runner.info" exists
			YYStoredMD5=`cat $YYInstallDir/.yoyo/runner.info`
			echo StoredMD5 = $YYStoredMD5
			echo RunnerMD5 = $YYRunnerMD5
			# If the MD5's match then everything is good
			if [ "$YYRunnerMD5" == "$YYStoredMD5" ]; then
				echo MD5 matches - no copy
				YYCopyDMG=0
			else
				echo MD5 does not match - copy
			fi
		fi
	fi
fi

if [ "$YYCopyDMG" == "1" ]; then
	# create the install directory if it does not exist
	[ -d "$YYInstallDir" ] || mkdir -p $YYInstallDir
	#[ -d "$YYInstallDir/lib" ] || mkdir -p $YYInstallDir/lib
	
	# if the .zip exists
	if [ -f "$YYInstallDir/$YYRunnerName" ]; then
		# Remove any app that may be present- ?
		
		echo "$YYInstallDir/$YYRunnerName" exists
		
		#record MD5 of .zip (before decompression)
		echo record MD5
		[ -d "$YYInstallDir/.yoyo" ] || mkdir -p $YYInstallDir/.yoyo
		#md5sum outputs filename after hash - strip it off
		YYmd5=($(md5sum $YYInstallDir/$YYRunnerName))
		echo $YYmd5
		echo $YYmd5 > $YYInstallDir/.yoyo/runner.info
		#md5sum $YYInstallDir/$YYRunnerName >$YYInstallDir/.yoyo/runner.info
		YYStoredMD5=`cat $YYInstallDir/.yoyo/runner.info`
		echo created md5 store = $YYStoredMD5
		
		#unzip the runner
		#default is to unzip files in-place (.gz removed)
		#gunzip -c file.gz > file #to keep .gz
		#what about zipped directories/multiple files though? need to use tar -> tar.gz
		#or regular zip also supported on linux - but is it installed by default?
		
		#gunzip -f "$YYInstallDir/$YYRunnerName"
		unzip -o "$YYInstallDir/$YYRunnerName" -d "$YYInstallDir"
		
		#need to set executable permission for some reason
		chmod +x "$YYInstallDir"/runner
		
		#remove the runner zip now we have extracted and stored the md5
		rm "$YYInstallDir/$YYRunnerName"
		
		#give it a more unique name so we can kill it safely
		mv "$YYInstallDir"/runner "$YYInstallDir"/GMStudio_runner
		
		# and done
		echo Done
		
	else
		echo "$YYInstallDir/$YYRunnerName" does not exist
		echo CopyRunner
	fi
else
	#up to date runner already present
	echo Done
fi