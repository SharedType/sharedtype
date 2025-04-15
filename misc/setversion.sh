#!/bin/bash

./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion="$1" --ntp
