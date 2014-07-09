#!/bin/bash

APP_HOME=/apps/MB
INSTALL_PATH=$APP_HOME/parent-social-1.0-SNAPSHOT
LOG_DIR=$INSTALL_PATH/logs

SERVICE_NAME=application
JMX_PORT=14001


#save existing logs
echo "Archiving old logs"
cd $LOG_DIR

if [ ! -d "archive" ]; then
    mkdir archive
fi

if [ -e  $SERVICE_NAME.log ]; then
    nowdate=`date +%y_%m_%d_%H_%M`
    tar cfz archive/${SERVICE_NAME}_$nowdate.tar.gz  $SERVICE_NAME.log* jvm.log
    rm $SERVICE_NAME.log*
fi


#Start up Server
echo "Starting play"
cd $INSTALL_PATH

nohup ./start -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -verbose:gc -Xloggc:$LOG_DIR/jvm.log -XX:+HeapDumpOnOutOfMemoryError -XX:+DisableExplicitGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xmx768m -XX:MaxPermSize=128m -Dhttp.port=80 -Dconfig.file=/opt/conf/mb_prod.conf &
