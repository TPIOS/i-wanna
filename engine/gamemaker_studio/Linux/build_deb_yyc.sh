#!/bin/bash

set -x

ZIP_FOLDER=~/${YYZipFolder}
YYAPPZIPNAME=${YYAppZipName}
PACKAGE_NAME=${YYPackageName}
VERSION_NUMBER=${YYVersionNumber}
MAINTAINER=${YYMaintainer}
HOMEPAGE=${YYHomePage}
DESCRIPTION_SHORT="${YYDescriptionShort}"
DESCRIPTION_LONG="${YYDescriptionLong}"
OUTPUT_FOLDER=~/${YYOutputFolder}

TMP_DIR=`mktemp -d`



mkdir -p $TMP_DIR

pushd $TMP_DIR

mkdir -p $PACKAGE_NAME/DEBIAN
mkdir -p $PACKAGE_NAME/opt/$PACKAGE_NAME/assets

# echo $PWD

echo "2.0" > debian-binary

# generate the control file
cat > $PACKAGE_NAME/DEBIAN/control <<EOL_THIS_LOT_INTO_THE_CONTROL_FILE
Package: $PACKAGE_NAME
Version: $VERSION_NUMBER
Section: games
Priority: optional
Architecture: i386
Maintainer: $MAINTAINER
Depends: libopenal1, libc6
Homepage: $HOMEPAGE
Description: $DESCRIPTION_SHORT
  $DESCRIPTION_LONG
EOL_THIS_LOT_INTO_THE_CONTROL_FILE

# copy the runner + assets into the right place
chmod 755 $PACKAGE_NAME/opt/$PACKAGE_NAME/$PACKAGE_NAME
unzip $ZIP_FOLDER/$YYAPPZIPNAME -d $PACKAGE_NAME/opt/$PACKAGE_NAME
chmod 644 $PACKAGE_NAME/opt/$PACKAGE_NAME/assets/splash.png
chmod 644 $PACKAGE_NAME/opt/$PACKAGE_NAME/assets/icon.png

# chmod the folders
find ./$PACKAGE_NAME -type d | xargs chmod 755

# run `dpkg-deb --build package-name`
fakeroot dpkg-deb --build $PACKAGE_NAME

cp ${PACKAGE_NAME}.deb $OUTPUT_FOLDER

popd

rm -rf $TMP_DIR
