#!/bin/bash
#set -e

# To test locally, need to prepare GPG keys
# gpg --gen-key
# See also: https://maven.apache.org/plugins/maven-gpg-plugin/
# See also: https://stackoverflow.com/questions/3174537/how-to-transfer-pgp-private-key-to-another-computer

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
  printf '%s' "$(local IFS=$delimiter ; echo "${array[*]}")"
}

snapshotVersion=$(./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)
version="$(printf '%s' "$snapshotVersion" | sed -e "s/-SNAPSHOT//g")"

./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion="$version" --ntp -B
sed -i -E "s/<sharedtype\.version>[0-9]+\.[0-9\.\[0-9]+\.[0-9]+<\/sharedtype\.version>/<sharedtype.version>$version<\/sharedtype.version>/g" doc/Usage.md
./mvnw deploy -DskipTests -Prelease --ntp -B # to debug release can add -DskipPublishing=true to prevent actual upload
NEW_VERSION="$(increment_version "$version" 1)-SNAPSHOT"
./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion="$NEW_VERSION" --ntp -B
printf '%s' "$NEW_VERSION" > NEW_VERSION.cache

# gradle plugin
cd build-tool-plugins/gradle-plugin || exit 1
./gradlew publishPlugins -Pversion="$version" -Pgradle.publish.key="$GRADLE_PUBLISH_KEY" -Pgradle.publish.secret="$GRADLE_PUBLISH_SECRET" --no-daemon
sed -i -E "s/^version=.*\$/version=$NEW_VERSION/g" ./gradle.properties
sed -i -E "s/^projectVersion=.*\$/projectVersion=$NEW_VERSION/g" ./it/gradle.properties
cd ../..
