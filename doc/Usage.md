# User Guide
Menu:
* [Setup Maven](#Maven)
* [A simple example](#A-simple-example)
* [Configurations](#Configurations)

## Setup

### Maven

Add sharedtype dependency, the annotation `@SharedType` is only used at compile time on source code:
```xml
<dependency>
    <groupId>org.sharedtype</groupId>
    <artifactId>sharedtype</artifactId>
    <version>${sharedtype.version}</version>
    <scope>provided</scope>
    <optional>true</optional>
</dependency>
```

Setup annotation processing:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.sharedtype</groupId>
                <artifactId>sharedtype-ap</artifactId>
                <version>${project.version}</version>
            </path>
        </annotationProcessorPaths>
        <showWarnings>true</showWarnings> <!-- Show annotation processing info log  -->
        <compilerArgs>
            <arg>${sharedtype.compilerArg}</arg> <!-- supplied as a property from cmd -->
        </compilerArgs>
    </configuration>
</plugin>
```

## Usage

### A simple example
Annotate on a class:
```java
@SharedType
record User(
    String name,
    int age,
    String email
) {
}
```

Execute annotation processing:
* maven: `./mvnw clean compile "-Dsharedtype.compilerArg=-proc:only"`

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
You can customize the path by config `maven-compiler-plugin`:
```xml
<compilerArgs>
    <arg>-Asharedtype.propsFile=${your.properties.path}</arg>
</compilerArgs>
```

See [Default Properties](../processor/src/main/resources/sharedtype-default.properties) for details.

#### Per annotation options
See Javadoc on [@SharedType](../annotation/src/main/java/org/sharedtype/annotation/SharedType.java) for details.

### Limitations
* Current design only retain `@SharedType` on source level. That means they are not visible if it is in a dependency jar during its dependent's compilation.
You have to execute the annotation processing on the same classpath with source code.
For multiple module builds, a workaround is to execute on every module.
