!/bin/bash

APP_HOME=/apps/MB
INSTALL_PATH=$APP_HOME/parent-social-1.0-SNAPSHOT

cd $INSTALL_PATH

echo "Starting play"
nohup ./start -Xmx768m -XX:MaxPermSize=128m -Dhttp.port=80 -Dconfig.file=/opt/conf/mb_prod.conf &