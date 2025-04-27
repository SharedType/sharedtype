package online.sharedtype.processor.writer.converter;

import lombok.RequiredArgsConstructor;
import online.sharedtype.processor.context.Context;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.TypeDef;
import online.sharedtype.processor.support.utils.Tuple;
import online.sharedtype.processor.writer.render.Template;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

final class RustEnumConverter extends AbstractRustConverter {
    public RustEnumConverter(Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldAccept(TypeDef typeDef) {
        return typeDef instanceof EnumDef && !((EnumDef) typeDef).components().isEmpty();
    }

    @Override
    public Tuple<Template, Object> convert(TypeDef typeDef) {
        EnumDef enumDef = (EnumDef) typeDef;

        EnumExpr value = new EnumExpr(
            enumDef.simpleName(),
            extractEnumValues(enumDef.components()),
            macroTraits(enumDef)
        );
        return Tuple.of(Template.TEMPLATE_RUST_ENUM, value);
    }

    private static List<EnumerationExpr> extractEnumValues(List<EnumValueInfo> components) {
        List<EnumerationExpr> exprs = new ArrayList<>(components.size());
        for (EnumValueInfo component : components) {
            exprs.add(new EnumerationExpr(component.name()));
        }
        return exprs;
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumExpr {
        final String name;
        final List<EnumerationExpr> enumerations;
        final Set<String> macroTraits;

        String macroTraitsExpr() {
            return buildMacroTraitsExpr(macroTraits);
        }
    }

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static final class EnumerationExpr {
        final String name;
    }
}
