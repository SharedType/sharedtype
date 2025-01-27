package online.sharedtype.processor.domain;

public enum DependingKind {
    SUPER_TYPE,
    /** Including fields and methods. */
    COMPONENTS,
    /** E.g. recursive references */
    SELF,
    ENUM_VALUE,
}
