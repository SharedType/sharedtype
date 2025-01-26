package online.sharedtype.processor.writer.adaptor;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.Props;
import online.sharedtype.processor.context.RenderFlags;

import java.util.LinkedHashSet;
import java.util.Set;

@RequiredArgsConstructor
final class RustHeaderDataAdaptor implements RenderDataAdaptor {
    private final Context ctx;

    RenderFlags renderFlags() {
        return ctx.getRenderFlags();
    }

    String allowExpr() {
        Set<String> allows = new LinkedHashSet<>();
        Props.Rust rust = ctx.getProps().getRust();
        if (rust.isAllowDeadcode()) {
            allows.add("dead_code");
        }

        if (!rust.isConvertToSnakeCase()) {
            allows.add("non_snake_case");
        }

        return allows.isEmpty() ? null : String.format("#![allow(%s)]", String.join(", ", allows));
    }
}
