!/bin/bash


FTP_HOME=/home/ftp


# remove all version zips except most recent four
cd $FTP_HOME
ls -lrt | grep zip | grep parent-social | head -n -4 | while read f; do
  rm -f "$f"
done

echo "Done purging old parent-social binary zips from $FTP_HOME"
