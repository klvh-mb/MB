#!/bin/bash


FTP_HOME=/home/ftp


# remove all version zips except most recent four
cd $FTP_HOME

#master
ls -rt | grep zip | grep parent-social-master | head -n -4 | while read f; do
  rm -f "$f"
done

#admin
ls -rt | grep zip | grep parent-social-admin | head -n -4 | while read f; do
  rm -f "$f"
done

echo "Done purging old parent-social binary zips from $FTP_HOME"
