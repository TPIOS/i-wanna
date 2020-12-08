#!/usr/bash

#set -x	#echo on

YYAppInstallDir=${YYAppInstallDir}
YYAppZipName=${YYAppZipName}
YYPackageName=${YYPackageName}
YYInstallApp=${YYInstallApp}
YYInstallScript=${YYPackageName}_install.sh

#extract the install script so we can change permissions
# must make sure zip file has .zip extension
cd "$YYAppInstallDir"
unzip -o "$YYAppZipName" $YYInstallScript
sed -i 's/\r//' $YYInstallScript	#remove dos carriage returns...
chmod +x $YYInstallScript
#pop it back in with executable permission
zip -g "$YYAppZipName" $YYInstallScript

if [ $YYInstallApp == "1" ]; then
	[ -d $YYPackageName ] && rm -r $YYPackageName	#remove existing directory if present
	unzip -o "$YYAppZipName" 
	#run install script
	./$YYInstallScript
else
	# just remove the extracted file
	rm $YYInstallScript
fi
