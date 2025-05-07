package online.sharedtype.processor.writer.adaptor;

import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.render.Template;

public interface RenderDataAdaptorFactory {
    Tuple<Template, RenderDataAdaptor> header(Context ctx);

    static Tuple<Template, RenderDataAdaptor> typescript(Context ctx) {
        return Tuple.of(Template.TEMPLATE_TYPESCRIPT_HEADER, new TypescriptHeaderDataAdaptor(ctx));
    }

    static Tuple<Template, RenderDataAdaptor> rust(Context ctx) {
        return Tuple.of(Template.TEMPLATE_RUST_HEADER, new RustHeaderDataAdaptor(ctx));
    }

    static Tuple<Template, RenderDataAdaptor> go(Context ctx) {
        return Tuple.of(Template.TEMPLATE_GO_HEADER, new GoHeaderDataAdaptor(ctx));
    }
}
