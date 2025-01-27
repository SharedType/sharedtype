package online.sharedtype.processor.writer.adaptor;

import online.sharedtype.processor.context.Context;

public interface RenderDataAdaptor {
    static RenderDataAdaptor header(Context ctx) {
        return new RustHeaderDataAdaptor(ctx);
    }
}
