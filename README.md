<img src="./misc/logo-color.svg" width="150" alt="sharedtype-logo"/>

[![Gitter](https://badges.gitter.im/sharedtype/sharedtype.svg)](https://app.gitter.im/#/room/#sharedtype:gitter.im)
[![CI](https://github.com/cuzfrog/sharedtype/actions/workflows/ci.yaml/badge.svg)](https://github.com/cuzfrog/sharedtype/actions/workflows/ci.yaml)
[![Maven Central](https://img.shields.io/maven-central/v/online.sharedtype/sharedtype?style=social)](https://central.sonatype.com/search?q=g:online.sharedtype++a:sharedtype&smo=true)

# SharedType - Lightweight Java Type Sharing
From Java:
```java
@SharedType
record User(String name, int age, String email) {}
```
To Typescript:
```typescript
export interface User {
    name: string;
    age: number;
    email: string;
}
```

## Features
* Java8+ compatible.
* Generics support.
* Compile-time constant support.
* Fast. (Execution takes milliseconds with `-proc:only`.)
* Simple global + type level configurations.
* Multiple target language options: Typescript, Go, Rust.

## Documentation
* [User Guide](doc/Usage.md)
* [Developer Guide](doc/Development.md)

## Similar Projects
* [bsorrentino/java2typescript](https://github.com/bsorrentino/java2typescript)
* [vojtechhabarta/typescript-generator](https://github.com/vojtechhabarta/typescript-generator)
* [aws/smithy](https://github.com/smithy-lang/smithy)

## Authors
Cause Chung (cuzfrog@gmail.com), Jeremy Zhou (hb.zhou.jeremy@gmail.com)

## License
![CC BY 4.0](./misc/by.svg)
CC BY 4.0
