#!/bin/bash
set -ex
dirOutput=IconSet.iconset
rm -rf $dirOutput
mkdir -p $dirOutput
sips -z 16 16     $1 --out $dirOutput/icon_16x16.png
sips -z 32 32     $1 --out $dirOutput/icon_16x16@2x.png
sips -z 32 32     $1 --out $dirOutput/icon_32x32.png
sips -z 64 64     $1 --out $dirOutput/icon_32x32@2x.png
sips -z 128 128   $1 --out $dirOutput/icon_128x128.png
sips -z 256 256   $1 --out $dirOutput/icon_128x128@2x.png
sips -z 256 256   $1 --out $dirOutput/icon_256x256.png
sips -z 512 512   $1 --out $dirOutput/icon_256x256@2x.png
sips -z 512 512   $1 --out $dirOutput/icon_512x512.png
sips -z 1024 1024 $1 --out $dirOutput/icon_512x512@2x.png
iconutil -c icns $dirOutput -o "$2"
rm -R $dirOutput