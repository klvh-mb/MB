root@li664-78:# cd /home/MB
root@li664-78:/home/MB# ./pullandsynchtest
root@li664-78:/home/MB# play test (Only in case you need to populate test data)
root@li664-78:/home/MB# cd ../parent-social-1.0-SNAPSHOT/

root@li664-78:/home/parent-social-1.0-SNAPSHOT#  rm nohup.out RUNNING_PID

root@li664-78:/home/parent-social-1.0-SNAPSHOT# ps -ef
root     22764 22086  2 20:22 pts/0    00:00:28 java -Dhttp.port=80 -cp 
kill -9 22764 


root@li664-78:/home/parent-social-1.0-SNAPSHOT# nohup  ./start -Dhttp.port=80 &




