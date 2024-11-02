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

## Style check
```bash
./mvnw editorconfig:check
```

## Debug
Debug annotation processor by run maven build:
```bash
./mvnd <your args goes here>
```
Then attach your debugger on it.

## Run test
If you encounter compilation problems with your IDE, delegate compilation to maven.
Before run test, run `./mvnw clean install -DskipTests` to build dependency classes.

#### E.g. run integration test:
```bash
./mvnw clean install -DskipTests -q && ./mvnw test -pl it
```

## Coding Style Guide
1. since annotation processing is one shot execution, JIT is not likely to optimize the code. So prefer plain loop than long calling stacks like Stream chains.
2. no dependencies without strong justification.
