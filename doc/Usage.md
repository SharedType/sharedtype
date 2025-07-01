# User Guide
Menu:
* [Setup Maven](#Maven)
* [A simple example](#A-simple-example)
* [Configurations](#Configurations)

## Setup
### Maven

Add Maven properties:
```xml
<properties>
    <sharedtype.version>0.13.0</sharedtype.version>
</properties>
```

Add sharedtype-maven-plugin:
```xml
<plugin>
    <groupId>online.sharedtype</groupId>
    <artifactId>sharedtype-maven-plugin</artifactId>
    <version>${project.version}</version>
</plugin>
```

Add sharedtype dependency:
```xml
<dependency>
    <groupId>online.sharedtype</groupId>
    <artifactId>sharedtype</artifactId>
    <version>${sharedtype.version}</version>
    <scope>provided</scope>
    <optional>true</optional>
</dependency>
```

## Usage

### A simple example
Annotate on a class:
```java
@SharedType
record User(String name, int age, String email) {}
```

Execute:
* maven: `./mvnw stype:gen` (Why `stype`? Because it's easy to type while explicitly enough to remember.)

By default, below code will be generated:
```typescript
export interface User {
    name: string;
    age: number;
    email: string;
}
```

### Configurations

#### Global options
By default, the file `sharedtype.properties` on current cmd path will be picked up.
You can customize the path:
```xml
<plugin>
    <configuration>
        <propertyFile>${project.basedir}/sharedtype-my-custom.properties</propertyFile>
    </configuration>
</plugin>
```

You can also specify individual properties:
```xml
<plugin>
    <configuration>
        <properties>
            <sharedtype.typescript.custom-code-path>${project.basedir}/custom-code.ts</sharedtype.typescript.custom-code-path>
        </properties>
    </configuration>
</plugin>
```

See [Default Properties](../processor/src/main/resources/sharedtype-default.properties) for all property entries.

Execution goal `gen` can be bound to a Maven lifecycle phase.
Annotation processing can also be setup and configured via `maven-compiler-plugin`, see [example](../it/pom.xml).
The advantage of using `sharedtype-maven-plugin` is that you don't need to execute other annotation processors if there are multiple.
#### Per annotation options
See Javadoc on [@SharedType](../annotation/src/main/java/online/sharedtype/SharedType.java) for details.

### Limitations
* Current design only retain `@SharedType` on source level. That means they are not visible if it is in a dependency jar during its dependent's compilation.
You have to execute the annotation processing on the same classpath with source code.
For multiple module builds, a workaround is to execute on every module.
* Non-static inner classes are not supported. Instance class may refer to its enclosing class's generic type without the type declaration on its own,
which could break the generated code. Later version of SharedType may loosen this limitation.
* Although SharedType does not depend on any serialization format, it assumes the contract of JSON.
That means a client can use any format of serializations, but may not exceed JSON capacity.
