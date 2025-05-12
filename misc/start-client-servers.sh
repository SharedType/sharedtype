#!/bin/bash
# this script is used by JsonE2eTest to spin up client servers



BASE_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
echo "Base dir: $BASE_DIR"

cd "$BASE_DIR/client-test/typescript" || exit 1
. setenv
npm i && npm run start &

cd "$BASE_DIR/client-test/go" || exit 1
. setenv
go run server.go &

# kill all child processes upon exit
trap 'pkill -P $$' EXIT

# wait for signal to exit
wait
