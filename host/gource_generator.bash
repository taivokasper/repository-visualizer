#!/bin/bash
set -e # stop on first error

RESULTS_DIR=$1
FILENAME=$2
TITLE_TEXT=$3

rm -f ${RESULTS_DIR}/${FILENAME}*

screen -dmS recording xvfb-run -a -s "-screen 0 1280x720x24" gource -1280x720 -r 30 --title "$TITLE_TEXT" --user-image-dir /avatars/ --highlight-all-users -s 0.5 -o ${RESULTS_DIR}/${FILENAME}.ppm

# This hack is needed because gource process doesn't stop
sleep 5

lsof | grep ${RESULTS_DIR}/${FILENAME}.ppm
greprc=$?

while [[ "$greprc" != "0" ]] ;
do
	sleep 10
	echo "File is open by a process. Wait for the processing to finish"
done

screen -S recording -X quit

xvfb-run -a -s "-screen 0 1280x720x24" ffmpeg -y -r 30 -f image2pipe -loglevel info -vcodec ppm -i ${RESULTS_DIR}/${FILENAME}.ppm -vcodec libx264 -preset medium -pix_fmt yuv420p -crf 1 -threads 0 -bf 0 ${RESULTS_DIR}/${FILENAME}
rm -f ${RESULTS_DIR}/${FILENAME}.ppm
