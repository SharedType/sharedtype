[![CI](https://github.com/cuzfrog/sharedtype/actions/workflows/ci.yaml/badge.svg)](https://github.com/cuzfrog/sharedtype/actions/workflows/ci.yaml)
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
Rust (Planed):
```rust
pub struct User {
    name: String,
    age: i32,
    email: String,
}
```
And more.

## Features
* Java8 support. No hassles.
* Generics support.
* (Planed) Constant support.
* Client source dependency is only `@SharedType`. Nothing gets into bytecode/runtime.
* SharedType AP jars <100KB, only 2 small dependencies: jsr305 annotations and [mustache](https://github.com/spullara/mustache.java). Download less.
* Parsing takes milliseconds with `-proc:only`. Implemented with performance in head.
* Intuitive defaults, put `@SharedType` and there you go.
* Global + class level options. Fine tune your configs.

## Similar Projects
* [bsorrentino/java2typescript](https://github.com/bsorrentino/java2typescript)
* [vojtechhabarta/typescript-generator](https://github.com/vojtechhabarta/typescript-generator)

## Documentation
* [User Guide](doc/Usage.md)
* [Developer Guide](doc/Development.md)

## Authors
Cause Chung (cuzfrog@gmail.com)

## License
![CC BY 4.0](./misc/by.svg)
CC BY 4.0
