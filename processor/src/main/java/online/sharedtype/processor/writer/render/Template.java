package online.sharedtype.processor.writer.render;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import online.sharedtype.SharedType;

/**
 * Represents a template used by {@link TemplateRenderer}.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode
@Getter
public final class Template {
    public static final Template TEMPLATE_TYPESCRIPT_HEADER = new Template(SharedType.TargetType.TYPESCRIPT, "header");
    public static final Template TEMPLATE_TYPESCRIPT_INTERFACE = new Template(SharedType.TargetType.TYPESCRIPT, "interface");
    public static final Template TEMPLATE_TYPESCRIPT_UNION_TYPE_ENUM = new Template(SharedType.TargetType.TYPESCRIPT, "union-type-enum");
    public static final Template TEMPLATE_TYPESCRIPT_ENUM = new Template(SharedType.TargetType.TYPESCRIPT, "enum");
    public static final Template TEMPLATE_RUST_HEADER = new Template(SharedType.TargetType.RUST, "header");
    public static final Template TEMPLATE_RUST_STRUCT = new Template(SharedType.TargetType.RUST, "struct");
    public static final Template TEMPLATE_RUST_ENUM = new Template(SharedType.TargetType.RUST, "enum");
    public static final Template TEMPLATE_GO_HEADER = new Template(SharedType.TargetType.GO, "header");
    public static final Template TEMPLATE_GO_STRUCT = new Template(SharedType.TargetType.GO, "struct");
    public static final Template TEMPLATE_GO_CONST_ENUM = new Template(SharedType.TargetType.GO, "const-enum");
    public static final Template TEMPLATE_GO_STRUCT_ENUM = new Template(SharedType.TargetType.GO, "struct-enum");

    private final SharedType.TargetType targetType;
    private final String resourcePath;

    Template(SharedType.TargetType targetType, String resourceName) {
        this.targetType = targetType;
        this.resourcePath = String.format("templates/%s/%s.mustache", targetType.name().toLowerCase(), resourceName);
    }

    public static Template forConstant(SharedType.TargetType targetType, boolean constantNamespaced) {
        return new Template(targetType, constantNamespaced ? "constant" : "constant-inline");
    }

    @Override
    public String toString() {
        return resourcePath;
    }
}
