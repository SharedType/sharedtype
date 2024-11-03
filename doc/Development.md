# Development Guide

## Setup
**Linux is assumed**. If you use Windows, you can use WSL with a remotely connected IDE. Windows 11 supports GUI app inside WSL.

Setup Java env vars (>= Java17 for development), configure `JAVA17_HOME` to point to your Java installation:
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
Before run test in IDE/individual module, run `./mvnw clean install -DskipTests` to build dependency classes.

#### E.g. run integration test locally:
```bash
./mvnw clean install -DskipTests -q && ./mvnw test -pl it/java17 -pl it/java8
```

#### Run local verification with all java tests
```bash
./mvnw verify
```

#### Verify JDK8 compatibility locally
Setup `JAVA8_HOME` to point to your Java8 installation.
```bash
. setenv 8
./mvnw verify -pl it/java8
```

#### Run client tests locally
Client tests are run in target languages in respective dir inside `./client-test`. They do basic type checking.
* Typescript. `. setenv && npm i && npm run test`


## Coding Style Guide
1. since annotation processing is one shot execution, JIT is not likely to optimize the code. So prefer plain loop than long calling stacks like Stream chains.
2. no adding dependencies without strong justification.
