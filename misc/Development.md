# Development Guide

## Setup
Setup Java env vars (>= Java17 for development):
```bash
. setenv
```
Optionally mount tmpfs to save your disk by:
```bash
./mount-tmpfs.sh
```

## Debug
Debug annotation processor by run maven build:
```bash
./mvnd <your args goes here>
```
Then attach your debugger on it.

## Run Integration test
```bash
./mvnw clean install -DskipTests -q && ./mvnw test -pl it
```
