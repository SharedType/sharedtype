#!/bin/bash
set -e

if [ -z "$MAVEN_GPG_PASSPHRASE" ];then
  echo "No MAVEN_GPG_PASSPHRASE provided, exit 1"
  exit 1
fi

# https://stackoverflow.com/a/64390598/5172925
### Increments the part of the string
## $1: version itself
## $2: number of part: 0 – major, 1 – minor, 2 – patch
increment_version() {
  local delimiter='.'
  IFS=$delimiter read -r -a array <<< "$1"
  array[$2]=$((array[$2]+1))
  if [ $2 -lt 2 ]; then array[2]=0; fi
  if [ $2 -lt 1 ]; then array[1]=0; fi
  printf '%s' "$(local IFS=$delimiter ; echo "${array[*]}")-SNAPSHOT"
}

snapshotVersion=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
version="$(printf '%s' "$snapshotVersion" | sed -e "s/-SNAPSHOT//g")"

echo "Release version=$version"
./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion="$version"
./mvnw deploy -DskipTests -Prelease -DskipPublishing=true
./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion="$(increment_version "$version" 1)-SNAPSHOT"
