#!/bin/bash

function usage()
{
   SCRIPT=`basename $0`
   echo "Usage"
   echo $SCRIPT "<UpgradeVersion>"
   exit 1
}


if [ -n "$1" ]; then
   UPGRADE_VERSION=$1
else
   usage
fi


IMG_TMP_ZIP=image_default.gz

APP_HOME=/apps/MB
INSTALL_PATH=$APP_HOME/current
PRERELEASE_PATH=/home/ftp

if [ -f $PRERELEASE_PATH/$IMG_TMP_ZIP ];
then
   echo "Copying default images"
   cp $PRERELEASE_PATH/$IMG_TMP_ZIP $APP_HOME/storage
   cd $APP_HOME/storage
   tar -xzf $IMG_TMP_ZIP
   rm $IMG_TMP_ZIP
fi

echo "Deploying new version $UPGRADE_VERSION from prerelease"
cp $PRERELEASE_PATH/$UPGRADE_VERSION.zip $APP_HOME

cd $APP_HOME
rm current
find . -maxdepth 1 -name "parent-social*" | grep -v zip | xargs -i rm -rf '{}' \;
unzip $UPGRADE_VERSION.zip

echo "Upgrade current soft link"
ln -s $UPGRADE_VERSION current

echo "Fixing up permissions"
cd $INSTALL_PATH
chmod +x start

echo "Deploy Completed!"
