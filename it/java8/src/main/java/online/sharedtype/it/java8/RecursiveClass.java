package online.sharedtype.it.java8;

import online.sharedtype.SharedType;

@SharedType(rustMacroTraits = {"PartialEq", "serde::Serialize", "serde::Deserialize"})
final class RecursiveClass {
    RecursiveClass directRef;
    RecursiveClass[] arrayRef;
}
