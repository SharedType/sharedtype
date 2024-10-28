#!/bin/bash

# Requires GitHub CLI installed and authenticated.

export USER="cuzfrog"
export REPO="sharedtype"

gh api repos/$USER/$REPO/actions/runs | jq -r '.workflow_runs[] | "\(.id)"' | xargs -n1 -I % gh api repos/$USER/$REPO/actions/runs/% -X DELETE
