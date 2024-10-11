package org.jets.processor.parser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;

import lombok.RequiredArgsConstructor;
import org.jets.processor.context.GlobalContext;
import org.jets.processor.domain.ClassInfo;
import org.jets.processor.domain.DefInfo;
import org.jets.processor.domain.FieldInfo;
import org.jets.processor.parser.mapper.TypeMapper;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class RecordParser implements TypeElementParser {
    private final TypeMapper typeMapper;
    private final GlobalContext ctx;

    @Override
    public List<DefInfo> parse(TypeElement typeElement) {
        ctx.checkArgument(typeElement.getKind() == ElementKind.RECORD,
                "Unsupported element kind: " + typeElement.getKind());

        var config = new AnnoConfig(typeElement);
        ctx.saveType(config.getQualifiedName(), config.getName());

        var builder = ClassInfo.builder().name(config.getName());
        var recordComponents = typeElement.getRecordComponents();
        var fields = new ArrayList<FieldInfo>(recordComponents.size());
        for (RecordComponentElement recordComponent : recordComponents) {
            if (config.isComponentExcluded(recordComponent)) {
                continue;
            }
            var types = typeMapper.map(recordComponent.asType());
            var fieldInfo = FieldInfo.builder()
                    .name(recordComponent.getSimpleName().toString())
                    .modifiers(recordComponent.getModifiers())
                    .optional(recordComponent.getAnnotation(ctx.getProps().getOptionalAnno()) != null)
                    .javaQualifiedTypename(types.javaQualifiedTypename())
                    .typename(types.typename())
                    .build();
            fields.add(fieldInfo);
        }
        builder.fields(fields);

        if (config.isIncludeGetters()) {
            // TODO
        }

        return List.of(builder.build());
    }
}
