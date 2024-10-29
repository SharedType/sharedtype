package org.sharedtype.processor.writer.render;

import lombok.AccessLevel;
import lombok.Getter;
import org.sharedtype.processor.context.OutputTarget;


@Getter(AccessLevel.PACKAGE)
public final class Template {
    public static final Template TEMPLATE_INTERFACE = new Template(OutputTarget.TYPESCRIPT, "interface");
    public static final Template TEMPLATE_ENUM_UNION = new Template(OutputTarget.TYPESCRIPT, "enum-union");

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
