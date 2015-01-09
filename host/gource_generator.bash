#!/bin/bash
set -e # stop on first error

RESULTS_DIR=$1
FILENAME=$2
TITLE_TEXT=$3

rm -f ${RESULTS_DIR}/${FILENAME}*

screen -dmS recording xvfb-run -a -s "-screen 0 1280x720x24" gource -1280x720 -r 30 --title "$TITLE_TEXT" --user-image-dir /avatars/ --highlight-all-users -s 0.5 -o ${RESULTS_DIR}/${FILENAME}.ppm

# This hack is needed because gource process doesn't stop
sleep 5
filesize=$(stat -c '%s' ${RESULTS_DIR}/${FILENAME}.ppm)
sleep 30
while [[ "$filesize" -eq "0" || $filesize -lt $(stat -c '%s' ${RESULTS_DIR}/${FILENAME}.ppm) ]] ;
do
	sleep 20
	filesize=$(stat -c '%s' ${RESULTS_DIR}/${FILENAME}.ppm)
    echo 'Polling the size. Current size is' $filesize
done
echo 'Force stopping recording because file size is not growing'
screen -S recording -X quit

xvfb-run -a -s "-screen 0 1280x720x24" ffmpeg -y -r 30 -f image2pipe -loglevel info -vcodec ppm -i ${RESULTS_DIR}/${FILENAME}.ppm -vcodec libx264 -preset medium -pix_fmt yuv420p -crf 1 -threads 0 -bf 0 ${RESULTS_DIR}/${FILENAME}
rm -f ${RESULTS_DIR}/${FILENAME}.ppm
