package online.sharedtype.processor.writer.adaptor;

import online.sharedtype.processor.context.Context;

final class TypescriptHeaderDataAdaptor extends AbstractDataAdaptor {
    TypescriptHeaderDataAdaptor(Context ctx) {
        super(ctx);
    }

    @Override
    String customCodeSnippet() {
        return readCustomCodeSnippet(ctx.getProps().getTypescript().getCustomCodePath());
    }
}
