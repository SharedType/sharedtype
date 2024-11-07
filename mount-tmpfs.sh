#!/bin/bash
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
MAVEN_REPO_CACHE_PATH="$DIR/.run/maven-repository-path.cache"
if [ -f "$MAVEN_REPO_CACHE_PATH" ]; then
  MAVEN_REPO_DIR=$(cat "$MAVEN_REPO_CACHE_PATH")
else
  MAVEN_REPO_DIR="$(./mvnw help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)/online/sharedtype"
  printf '%s' "$MAVEN_REPO_DIR" > "$MAVEN_REPO_CACHE_PATH"
fi

function mountTmpfs() {
  mkdir -p "$1"
  sudo mount -t tmpfs -o size="$2" -o noatime tmpfs "$1"
  echo "tmpfs mounted at $1 of size $2"
}

mountTmpfs "$DIR/annotation/target" 32M
mountTmpfs "$DIR/processor/target" 64M
mountTmpfs "$DIR/it/java17/target" 64M
mountTmpfs "$DIR/it/java8/target" 64M
mountTmpfs "$MAVEN_REPO_DIR" 64M
