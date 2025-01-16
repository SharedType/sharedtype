package online.sharedtype.processor.domain;

public enum DependingKind {
    SUPER_TYPE,
    /** Including fields and methods. */
    COMPONENTS,
    SELF,
    ENUM_VALUE
}
