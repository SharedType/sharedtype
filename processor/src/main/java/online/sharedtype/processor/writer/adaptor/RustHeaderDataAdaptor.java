package online.sharedtype.processor.writer.adaptor;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.context.Props;

import java.util.LinkedHashSet;
import java.util.Set;

final class RustHeaderDataAdaptor extends AbstractDataAdaptor {
    RustHeaderDataAdaptor(Context ctx) {
        super(ctx);
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

    @Override
    String customCodeSnippet() {
        return readCustomCodeSnippet(ctx.getProps().getRust().getCustomCodePath());
    }
}
