package online.sharedtype.processor.writer.adaptor;

import online.sharedtype.processor.context.Context;

final class GoHeaderDataAdaptor extends AbstractDataAdaptor {
    public GoHeaderDataAdaptor(Context ctx) {
        super(ctx);
    }

    @SuppressWarnings("unused")
    String packageName() {
        return ctx.getProps().getGo().getOutputFilePackageName();
    }

    @Override
    String customCodeSnippet() {
        return readCustomCodeSnippet(ctx.getProps().getGo().getCustomCodePath());
    }
}
