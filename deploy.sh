!/bin/bash

BUILD_NAME=parent-social-1.0-SNAPSHOT
IMG_TMP_ZIP=image_default.gz

APP_HOME=/apps/MB
INSTALL_PATH=$APP_HOME/$BUILD_NAME
PRERELEASE_PATH=/home/ftp

# Backup previous version
if [ -f $APP_HOME/$BUILD_NAME.zip ];
then
   echo "Backing up previous zip version"
   mv $APP_HOME/$BUILD_NAME.zip $APP_HOME/$BUILD_NAME.zip.bak
else
   echo "No previous zip version to backup"
fi

if [ -f $PRERELEASE_PATH/$IMG_TMP_ZIP ];
then
   echo "Copying default images"
   cp $PRERELEASE_PATH/$IMG_TMP_ZIP $APP_HOME/storage
   cd $APP_HOME/storage
   tar -xzf $IMG_TMP_ZIP
   rm $IMG_TMP_ZIP
fi

echo "Deploying new version from prerelease"
cp $PRERELEASE_PATH/$BUILD_NAME.zip $APP_HOME

cd $APP_HOME
rm -rf $BUILD_NAME
unzip $BUILD_NAME.zip

echo "Fixing up permissions"
cd $INSTALL_PATH
chmod +x start
