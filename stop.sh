#!/bin/bash

APP_HOME=/apps/MB
INSTALL_PATH=$APP_HOME/parent-social-1.0-SNAPSHOT

cd $INSTALL_PATH
read playpid < RUNNING_PID

echo "Killing previous play PID " $playpid
kill -9 $playpid

rm nohup.out RUNNING_PID
