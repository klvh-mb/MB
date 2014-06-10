#!/bin/bash

APP_HOME=/apps/MB
VERSION=parent-social-1.0-SNAPSHOT

INSTALL_PATH=$APP_HOME/$VERSION
cd $INSTALL_PATH

read playpid < RUNNING_PID

echo "Killing previous play PID " $playpid
kill -9 $playpid

rm nohup.out RUNNING_PID
