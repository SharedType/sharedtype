package online.sharedtype.processor.writer.adaptor;

import online.sharedtype.processor.context.Context;

final class GoHeaderDataAdaptor extends AbstractDataAdaptor {
    public GoHeaderDataAdaptor(Context ctx) {
        super(ctx);
    }
    @Override
    String customCodeSnippet() {
        return readCustomCodeSnippet(ctx.getProps().getGo().getCustomCodePath());
    }
}
