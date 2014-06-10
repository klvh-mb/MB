#!/bin/bash

APP_HOME=/apps/MB
VERSION=parent-social-1.0-SNAPSHOT

INSTALL_PATH=$APP_HOME/$VERSION
cd $INSTALL_PATH

echo "Starting play"
nohup ./start -Dhttp.port=80 -Dconfig.file=/opt/conf/mb_prod.conf &