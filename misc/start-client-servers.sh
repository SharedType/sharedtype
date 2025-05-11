#!/bin/bash
# this script is used by JsonE2eTest to spin up client servers

if [ -e "$GO_HOME" ]; then
  export PATH=$GO_HOME/bin:$PATH
fi

PWD=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
echo "Current dir: $PWD"

cd "$PWD/../client-test/go" || exit 1
go run server.go &

# kill all child processes upon exit
trap 'pkill -P $$' EXIT

# wait for signal to exit
wait
