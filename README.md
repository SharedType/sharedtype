# SharedType - TypeScript type emitter for Java

A lightweight, fast, flexible solution to share Java types and constants in Typescript.

## Why?
SharedType uses Java annotation processing + templating. Here's a speed comparison:

## Alternatives

* [bsorrentino/java2typescript](https://github.com/bsorrentino/java2typescript)
* [vojtechhabarta/typescript-generator](https://github.com/vojtechhabarta/typescript-generator)

## Doc

## Development

Setup Java env vars (>= Java17 for development):
```bash
. setenv
```
Optionally mount tmpfs to save your disk by:
```bash
`./mount-tmpfs.sh`
```

Debug annotation processor by run maven build:
```bash
./mvnd <your args goes here>
```
Then attach your debugger on it.

## Authors
Cause Chung (cuzfrog@gmail.com)

## License
![CC BY 4.0](./misc/by.svg)
CC BY 4.0
