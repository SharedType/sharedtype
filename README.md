[![CI](https://github.com/cuzfrog/sharedtype/actions/workflows/ci.yaml/badge.svg)](https://github.com/cuzfrog/sharedtype/actions/workflows/ci.yaml)
[![Maven Central](https://img.shields.io/maven-central/v/online.sharedtype/sharedtype?style=social)](https://central.sonatype.com/search?q=g:online.sharedtype++a:sharedtype&smo=true)

# SharedType - Sharing Java Types made easy
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
Go (Planed):
```golang
type User struct {
    Name string
    Age int
    Email string
}
```
Rust:
```rust
pub struct User {
    name: String,
    age: i32,
    email: String,
}
```

## Features
* Java8 support.
* Generics support.
* Constant support.
* Client source dependency is only `@SharedType` retained at source code level.
* SharedType AP jar <150KB, only 2 small dependencies: jsr305 annotations and [mustache](https://github.com/spullara/mustache.java).
* Parsing takes milliseconds with `-proc:only`.
* Intuitive defaults, put `@SharedType` and there you go. Global + class level options.

## Documentation
* [User Guide](doc/Usage.md)
* [Developer Guide](doc/Development.md)

## Similar Projects
* [bsorrentino/java2typescript](https://github.com/bsorrentino/java2typescript)
* [vojtechhabarta/typescript-generator](https://github.com/vojtechhabarta/typescript-generator)

## Authors
Cause Chung (cuzfrog@gmail.com), Jeremy Zhou (hb.zhou.jeremy@gmail.com)

## License
![CC BY 4.0](./misc/by.svg)
CC BY 4.0
