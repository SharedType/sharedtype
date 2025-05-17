import {
    type DependencyClassA, type DependencyClassB, type DependencyClassC, type EnumGalaxy, type EnumSize,
    EnumTShirt, type JavaRecord, type AnotherJavaClass, type RecursiveClass, type MapClass,
    type OptionalMethod,
    type ArrayClass,
    type JavaTimeClass,
    type JodaTimeClass,
    type MathClass,
    type EnumConstReference,
    type EnumEnumReference,
} from "./index.java17";

export const list1: EnumGalaxy[] = ["Andromeda", "MilkyWay", "Triangulum"];
export const record1: Record<EnumTShirt, number> = {
    S: 1,
    M: 2,
    L: 3,
};
export const size1: EnumSize = 1;
export const tshirtSize1: EnumTShirt = EnumTShirt.S;

export const dependencyClassC: DependencyClassC = {
};

export const dependencyClassB: DependencyClassB = {
    c: dependencyClassC
};

export const dependencyClassA: DependencyClassA = {
    a: 0,
    b: dependencyClassB,
    notIgnoredImplementedMethod: 0,
    value: 0,
};
dependencyClassC.a = dependencyClassA;

export const obj: Omit<JavaRecord<string>, "aVoid" | "genericMap"> = {
    boxedBoolean: false,
    boxedByte: 0,
    boxedChar: "",
    boxedDouble: 0,
    boxedFloat: 0,
    boxedInt: 0,
    boxedIntArray: [],
    boxedLong: 0,
    boxedShort: 0,
    containerStringList: [],
    containerStringListCollection: [],
    cyclicDependency: dependencyClassA,
    duplicateAccessor: "",
    enumGalaxy: "MilkyWay",
    enumSize: 3,
    genericList: [],
    genericListSet: [],
    genericSet: [],
    intArray: [],
    object: undefined,
    primitiveBoolean: false,
    primitiveByte: 0,
    primitiveChar: "",
    primitiveDouble: 0,
    primitiveFloat: 0,
    primitiveInt: 0,
    primitiveLong: 0,
    primitiveShort: 0,
    string: "",
    value: "",
};

export const anotherJavaClass: AnotherJavaClass = {
    value: 333,
};

export const recursiveClass: RecursiveClass = {
    directRef: {
        directRef: undefined,
        arrayRef: [],
    },
    arrayRef: [],
};
recursiveClass.directRef = {
    directRef: undefined,
    arrayRef: [],
};

export const mapClass: MapClass = {
    mapField: {},
    enumKeyMapField: {
        1: "1",
    },
    customMapField: {
        55: "abc",
    },
    nestedMapField: {
        m1: {
            v: 1
        }
    }
};

export const optionalMethods: OptionalMethod = {
    valueOptional: undefined,
    nestedValueOptional: null,
    setNestedValueOptional: null,
    mapNestedValueOptional: undefined
};
optionalMethods.mapNestedValueOptional = { 1: "foo" };
optionalMethods.setNestedValueOptional = ["foo"];
optionalMethods.nestedValueOptional = "bar";

export const arrayClass: ArrayClass = {
    arr: ["abc"],
};

export const javaTime: JavaTimeClass = {
    utilDate: new Date(),
    sqlDate: new Date(),
    localDate: new Date(),
    localTime: new Date(),
    localDateTime: new Date(),
    zonedDateTime: new Date(),
    offsetDateTime: new Date(),
    offsetTime: new Date(),
    instant: new Date(),
};

export const jodaTime: JodaTimeClass = {
    jodaDateTime: "2022-01-01T00:00:00.000+08:00",
    jodaLocalDate: "2022-01-01T00:00:00.000+08:00",
    jodaMonthDay: "2022-01-01T00:00:00.000+08:00",
    jodaLocalTime: "2022-01-01T00:00:00.000+08:00",
    jodaLocalDateTime: "2022-01-01T00:00:00.000+08:00",
    jodaOffsetDateTime: "2022-01-01T00:00:00.000+08:00",
};

export const mathClass: MathClass = {
    bigDecimal: "1.1",
    bigInteger: "8888888555555",
};

  
export const enumConstValue1: EnumConstReference = 156;
export const enumEnumValue1: EnumEnumReference = 3;
