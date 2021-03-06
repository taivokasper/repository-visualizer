#!/bin/bash

RESULTS_DIR=$1
FILENAME=$2
TITLE_TEXT=$3
SCREEN_NAME="recording_${FILENAME}"

rm -f ${RESULTS_DIR}/${FILENAME}*

screen -DmS "${SCREEN_NAME}" xvfb-run -a -s "-screen 0 1280x720x24" gource -1280x720 -r 30 --title "$TITLE_TEXT" --user-image-dir /avatars/ --highlight-all-users -s 0.5 -o ${RESULTS_DIR}/${FILENAME}.ppm &
PID=$!
SCREEN_FULLNAME="${PID}.${SCREEN_NAME}"

# This hack is needed because gource process doesn't stop by itself
sleep 5

lsof | grep ${RESULTS_DIR}/${FILENAME}.ppm
greprc=$?
while [[ "$greprc" -eq "0" ]] ;
do
	echo "File is open by another process. Waiting for the process to finish"
	sleep 10
	lsof | grep ${RESULTS_DIR}/${FILENAME}.ppm
	greprc=$?
done
echo "Quiting screen ${SCREEN_FULLNAME}"
screen -S ${SCREEN_FULLNAME} -X quit

echo "Recording finished. Starting to process ppm to mp4"
xvfb-run -a -s "-screen 0 1280x720x24" ffmpeg -y -r 30 -f image2pipe -loglevel info -vcodec ppm -i ${RESULTS_DIR}/${FILENAME}.ppm -vcodec libx264 -preset medium -pix_fmt yuv420p -crf 1 -threads 0 -bf 0 ${RESULTS_DIR}/${FILENAME}

rm -f ${RESULTS_DIR}/${FILENAME}.ppm
