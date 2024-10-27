package org.sharedtype.processor.writer.render;

import lombok.AccessLevel;
import lombok.Getter;
import org.sharedtype.processor.context.Language;


@Getter(AccessLevel.PACKAGE)
public final class Template {
    public static final Template TEMPLATE_INTERFACE = new Template(Language.TYPESCRIPT, "interface");
    public static final Template TEMPLATE_ENUM_UNION = new Template(Language.TYPESCRIPT, "enum-union");

    private final Language language;
    private final String resourcePath;

    Template(Language language, String resourceName) {
        this.language = language;
        this.resourcePath = String.format("templates/%s/%s.mustache", language.name().toLowerCase(), resourceName);
    }

    @Override
    public String toString() {
        return resourcePath;
    }
}
