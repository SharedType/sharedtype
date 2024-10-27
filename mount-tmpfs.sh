#!/bin/bash
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
MAVEN_REPO_DIR="$(./mvnw help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)/org/sharedtype"

function mountTmpfs() {
  mkdir -p "$1"
  sudo mount -t tmpfs -o size="$2" -o noatime tmpfs "$1"
  echo "tmpfs mounted at $1 of size $2"
}

mountTmpfs "$DIR/annotation/target" 128M
mountTmpfs "$DIR/processor/target" 256M
mountTmpfs "$DIR/it/target" 256M
mountTmpfs "$MAVEN_REPO_DIR" 64M
