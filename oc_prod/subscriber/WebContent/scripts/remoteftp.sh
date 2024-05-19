#!/bin/bash

ftp -v -n "sudheer" << cmd
user "sudheer" "sudheer"
prompt no
cd  /home/sudheer/Desktop/t1/
lcd /work/workspace_old/BounceData/bounce_dir/
mget *.*
ls  . /work/workspace_old/BounceData/filelist
quit
cmd

cd /work/workspace_old/BounceData/bounce_dir/
list=$(ls -l |  grep -v '^total'  | wc -l)
echo  "No of Files Downloaded $list "
cd /work/workspace_old/BounceData/
downlist=$(wc -l filelist | awk -F " " '{print /home/sudheer/Desktop/t1/}')
echo "No of Entries in file $downlist" 

if [ $downlist -eq $list ]; then
   flag=1    
else
   exit
fi

ftp -v -n "sudheer" << cmdd
user "sudheer" "sudheer"
prompt no
cd  /home/sudheer/Desktop/t1/
mdelete *.csv
echo files are deleted
cmdd
cd /work/workspace_old/BounceData/
rm filelist
