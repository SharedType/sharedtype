import {
    type DependencyClassA, type DependencyClassB, type DependencyClassC, type EnumGalaxy, type EnumSize,
    type EnumTShirt, type JavaRecord, type AnotherJavaClass, type RecursiveClass, type MapClass,
    type OptionalMethod,
} from "./index.java17";

export const list1: EnumGalaxy[] = ["Andromeda", "MilkyWay", "Triangulum"];
export const record1: Record<EnumTShirt, number> = {
    S: 1,
    M: 2,
    L: 3,
}
export const size1: EnumSize = 1;

export const dependencyClassC: DependencyClassC = {
};

export const dependencyClassB: DependencyClassB = {
    c: dependencyClassC
}

export const dependencyClassA: DependencyClassA = {
    a: 0,
    b: dependencyClassB,
    notIgnoredImplementedMethod: 0,
    value: 0,
};
dependencyClassC.a = dependencyClassA

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
}

export const recursiveClass: RecursiveClass = {
    directRef: {
        directRef: undefined,
        arrayRef: [],
    },
    arrayRef: [],
}
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
}

export const optionalMethods: OptionalMethod = {};
optionalMethods.mapNestedValueOptional = { 1: "foo" };
optionalMethods.setNestedValueOptional = ["foo"];
optionalMethods.nestedValueOptional = "bar";
