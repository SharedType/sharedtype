package online.sharedtype.processor.writer.render;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import online.sharedtype.processor.context.OutputTarget;

/**
 * Represents a template used by {@link TemplateRenderer}.
 *
 * @author Cause Chung
 */
@EqualsAndHashCode
@Getter(AccessLevel.PACKAGE)
public final class Template {
    public static final Template TEMPLATE_TYPESCRIPT_HEADER = new Template(OutputTarget.TYPESCRIPT, "header");
    public static final Template TEMPLATE_TYPESCRIPT_INTERFACE = new Template(OutputTarget.TYPESCRIPT, "interface");
    public static final Template TEMPLATE_TYPESCRIPT_ENUM_UNION = new Template(OutputTarget.TYPESCRIPT, "enum-union");
    public static final Template TEMPLATE_TYPESCRIPT_CONSTANT = new Template(OutputTarget.TYPESCRIPT, "constant");
    public static final Template TEMPLATE_TYPESCRIPT_CONSTANT_INLINE = new Template(OutputTarget.TYPESCRIPT, "constant-inline");
    public static final Template TEMPLATE_RUST_HEADER = new Template(OutputTarget.RUST, "header");
    public static final Template TEMPLATE_RUST_STRUCT = new Template(OutputTarget.RUST, "struct");
    public static final Template TEMPLATE_RUST_ENUM = new Template(OutputTarget.RUST, "enum");

    private final OutputTarget outputTarget;
    private final String resourcePath;

    Template(OutputTarget outputTarget, String resourceName) {
        this.outputTarget = outputTarget;
        this.resourcePath = String.format("templates/%s/%s.mustache", outputTarget.name().toLowerCase(), resourceName);
    }

    @Override
    public String toString() {
        return resourcePath;
    }
}
