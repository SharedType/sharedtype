import type {DependencyClassA, DependencyClassB, DependencyClassC, EnumGalaxy, EnumSize, EnumTShirt, JavaRecord} from "../src/index.js";

export const list1: EnumGalaxy[] = ["Andromeda", "MilkyWay", "Triangulum"];
export const record1: Record<EnumTShirt, number> = {
    S: 1,
    M: 2,
    L: 3,
}
export const size1: EnumSize = 1;

export const dependencyClassC: DependencyClassC = {} as DependencyClassC;

export const dependencyClassB: DependencyClassB = {
    c: dependencyClassC
}

export const dependencyClassA: DependencyClassA = {
    a: 0,
    b: dependencyClassB
};
dependencyClassC.a = dependencyClassA

export const obj: Omit<JavaRecord<string, number>, "aVoid" | "genericMap"> = {
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
};
