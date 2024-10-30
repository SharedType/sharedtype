[![CI](https://github.com/cuzfrog/sharedtype/actions/workflows/ci.yaml/badge.svg)](https://github.com/cuzfrog/sharedtype/actions/workflows/ci.yaml)
# SharedType - Sharing Java Types made easy

* Only client source dependency is `@SharedType`.
* SharedType annotation processor jar is <100KB, only 2 small dependencies: jsr305 annotations and mustache.
* Parsing takes milliseconds. See [Performance](doc/Performance.md).
* Put `@SharedType` and there you go.
* Global + class level options.
* (Not Implemented) Compile time resolvable values. E.g. constant literals.
* Generics and complex type structures.
* (Only TS Implemented) Multiple target schemas, extendable.

## Alternatives
* [bsorrentino/java2typescript](https://github.com/bsorrentino/java2typescript)
* [vojtechhabarta/typescript-generator](https://github.com/vojtechhabarta/typescript-generator)

## Documentation
[User Guide](doc/Usage.md)
[Development Guide](doc/Development.md)

## Authors
Cause Chung (cuzfrog@gmail.com)

## License
![CC BY 4.0](./misc/by.svg)
CC BY 4.0
