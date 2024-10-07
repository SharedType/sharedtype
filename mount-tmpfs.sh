#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

function tmpfsify() {
  mkdir -p "$DIR/$1"
  sudo mount -t tmpfs -o size="$2" -o noatime tmpfs "$DIR/$1"
}

tmpfsify "target" 64M
tmpfsify "annotation/target" 128M
tmpfsify "processor/target" 256M
echo "tmpfs mounted"
