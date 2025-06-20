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
    <groupId>online.sharedtype</groupId>
    <artifactId>sharedtype</artifactId>
    <version>${sharedtype.version}</version>
    <scope>provided</scope>
    <optional>true</optional>
</dependency>
```

Add Maven properties:
```xml
<properties>
    <compilerArg /> <!-- Placeholder -->
    <sharedtype.version>0.12.1</sharedtype.version>
    <sharedtype.enabled>false</sharedtype.enabled> <!-- Disable by default so not to participate in every compilation -->
</properties>
```
Setup annotation processing:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>online.sharedtype</groupId>
                <artifactId>sharedtype-ap</artifactId>
                <version>${sharedtype.version}</version>
            </path>
        </annotationProcessorPaths>
        <showWarnings>true</showWarnings> <!-- Show annotation processing info log -->
        <compilerArgs>
            <!-- supplied as properties from cmd -->
            <arg>${compilerArg}</arg>
            <arg>-Asharedtype.enabled=${sharedtype.enabled}</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

## Usage

### A simple example
Annotate on a class:
```java
@SharedType
record User(String name, int age, String email) {}
```

Execute annotation processing:
* maven: `./mvnw compile -DcompilerArg=-proc:only -Dsharedtype.enabled=true`

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

Properties can also be passed in as system properties, which will override the properties files, e.g.
```bash
./mvnw clean compile -Dsharedtype.typescript.custom-code-path=it/custom-code.ts
```
or
```bash
MAVEN_OPTS="-Dsharedtype.typescript.custom-code-path=it/custom-code.ts" ./mvnw clean compile
```
or can use [properties-maven-plugin](https://www.mojohaus.org/properties-maven-plugin/usage.html#set-system-properties) to set system properties for the build.

See [Default Properties](../processor/src/main/resources/sharedtype-default.properties) for details.

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
