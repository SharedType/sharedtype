package online.sharedtype.processor.writer.render;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import online.sharedtype.processor.context.OutputTarget;

/**
 * Represents a template used by {@link TemplateRenderer}.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode
@Getter
public final class Template {
    public static final Template TEMPLATE_TYPESCRIPT_HEADER = new Template(OutputTarget.TYPESCRIPT, "header");
    public static final Template TEMPLATE_TYPESCRIPT_INTERFACE = new Template(OutputTarget.TYPESCRIPT, "interface");
    public static final Template TEMPLATE_TYPESCRIPT_UNION_TYPE_ENUM = new Template(OutputTarget.TYPESCRIPT, "union-type-enum");
    public static final Template TEMPLATE_TYPESCRIPT_ENUM = new Template(OutputTarget.TYPESCRIPT, "enum");
    public static final Template TEMPLATE_RUST_HEADER = new Template(OutputTarget.RUST, "header");
    public static final Template TEMPLATE_RUST_STRUCT = new Template(OutputTarget.RUST, "struct");
    public static final Template TEMPLATE_RUST_ENUM = new Template(OutputTarget.RUST, "enum");
    public static final Template TEMPLATE_GO_HEADER = new Template(OutputTarget.GO, "header");
    public static final Template TEMPLATE_GO_STRUCT = new Template(OutputTarget.GO, "struct");
    public static final Template TEMPLATE_GO_CONST_ENUM = new Template(OutputTarget.GO, "const-enum");

    private final OutputTarget outputTarget;
    private final String resourcePath;

    Template(OutputTarget outputTarget, String resourceName) {
        this.outputTarget = outputTarget;
        this.resourcePath = String.format("templates/%s/%s.mustache", outputTarget.name().toLowerCase(), resourceName);
    }

    public static Template forConstant(OutputTarget outputTarget, boolean constantNamespaced) {
        return new Template(outputTarget, constantNamespaced ? "constant" : "constant-inline");
    }

    @Override
    public String toString() {
        return resourcePath;
    }
}
