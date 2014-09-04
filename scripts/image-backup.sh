!/bin/bash


STORAGE_PATH=/apps/MB-Image/
DATA_BACKUP_PATH=/root/backup_image


nowdate=`date +%Y%m%d_%H%M`


# tar up images
cd $STORAGE_PATH
tar -czf $DATA_BACKUP_PATH/mage_backup_$nowdate.tgz storage/

echo "Done backing up images to $DATA_BACKUP_PATH/mage_backup_$nowdate.tgz"
