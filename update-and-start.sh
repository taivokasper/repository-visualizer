#!/bin/sh
set -e

git pull origin master

fig -f fig-build.yml stop && \
    fig -f fig-build.yml rm --force && \
    fig -f fig-build.yml up && \
    fig -f fig-build.yml rm --force

fig stop && \
    fig rm --force && \
    fig up -d
