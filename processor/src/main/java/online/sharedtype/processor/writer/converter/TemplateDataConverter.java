package online.sharedtype.processor.writer.converter;

import online.sharedtype.processor.domain.TypeDef;
import online.sharedtype.processor.writer.render.Template;
import online.sharedtype.processor.support.utils.Tuple;

import javax.annotation.Nullable;

public interface TemplateDataConverter {

    /**
     *
     * @return null if the typeDef is not supported by this particular converter
     */
    @Nullable
    Tuple<Template, Object> convert(TypeDef typeDef);
}
