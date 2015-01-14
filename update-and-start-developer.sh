#!/bin/sh
set -e

fig -f fig-build-developer.yml rm --force && \
    fig -f fig-build-developer.yml up

fig rm --force && \
    fig up