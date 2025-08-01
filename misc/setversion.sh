#!/bin/bash

./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion="$1" --ntp
sed -i -E "s/^version=.*\$/version=$1/g" build-tool-plugins/gradle-plugin/gradle.properties
sed -i -E "s/^projectVersion=.*\$/projectVersion=$1/g" build-tool-plugins/gradle-plugin/it/gradle.properties
