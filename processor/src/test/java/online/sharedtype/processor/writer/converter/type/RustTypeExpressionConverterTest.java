package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.RenderFlags;
import online.sharedtype.processor.domain.component.EnumValueInfo;
import online.sharedtype.processor.domain.def.EnumDef;
import online.sharedtype.processor.domain.type.ArrayTypeInfo;
import online.sharedtype.processor.domain.def.ClassDef;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.type.DateTimeInfo;
import online.sharedtype.processor.domain.value.ValueHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class RustTypeExpressionConverterTest {
    private final ContextMocks contextMocks = new ContextMocks();
    private final RustTypeExpressionConverter converter = new RustTypeExpressionConverter(contextMocks.getContext());

    private final Config config = mock(Config.class);
    private final ClassDef contextTypeDef = ClassDef.builder().simpleName("Abc").build();

    @Test
    void convertArrayType() {
        String expr = converter.toTypeExpr(new ArrayTypeInfo(Constants.INT_TYPE_INFO), contextTypeDef);
        assertThat(expr).isEqualTo("Vec<i32>");
    }

    @Test
    void convertObjectType() {
        assertThat(converter.toTypeExpr(Constants.OBJECT_TYPE_INFO, contextTypeDef)).isEqualTo("Box<dyn Any>");
    }

    @Test
    void flagToRenderObjectType() {
        RenderFlags renderFlags = contextMocks.getRenderFlags();

        converter.beforeVisitTypeInfo(Constants.INT_TYPE_INFO);
        verify(renderFlags, never()).setUseRustAny(anyBoolean());

        converter.beforeVisitTypeInfo(Constants.OBJECT_TYPE_INFO);
        verify(renderFlags).setUseRustAny(true);

        verify(renderFlags, never()).setUseRustMap(anyBoolean());
        converter.beforeVisitTypeInfo(ConcreteTypeInfo.builder().kind(ConcreteTypeInfo.Kind.MAP).build());
        verify(renderFlags).setUseRustMap(true);
    }

    @Test
    void addSmartPointerBoxToCyclicReferencedType() {
        var classDef = ClassDef.builder().cyclicReferenced(true).build();
        var expr = converter.toTypeExpression(ConcreteTypeInfo.builder().simpleName("Abc").typeDef(classDef).build(), "Abc");
        assertThat(expr).isEqualTo("Box<Abc>");
    }

    @Test
    void typeMappings() {
        ConcreteTypeInfo typeInfo = ConcreteTypeInfo.builder().qualifiedName("a.b.A1").build();
        assertThat(converter.toTypeExpression(typeInfo, "DefaultName")).isEqualTo("DefaultName");
        typeInfo.addMappedName(SharedType.TargetType.RUST, "AAA");
        assertThat(converter.toTypeExpression(typeInfo, "DefaultName")).isEqualTo("AAA");

        when(config.getRustTargetDatetimeTypeLiteral()).thenReturn("DefaultDateLiteral");
        DateTimeInfo dateTimeInfo = new DateTimeInfo("a.b.A2");
        assertThat(converter.dateTimeTypeExpr(dateTimeInfo, config)).isEqualTo("DefaultDateLiteral");
        dateTimeInfo.addMappedName(SharedType.TargetType.RUST, "BBB");
        assertThat(converter.dateTimeTypeExpr(dateTimeInfo, config)).isEqualTo("BBB");
    }

    @Test
    void mapEnumValueType() {
        ConcreteTypeInfo enumType = ConcreteTypeInfo.builder().simpleName("EnumA").build();
        EnumDef enumDef = EnumDef.builder().simpleName("EnumA")
            .enumValueInfos(List.of(
                EnumValueInfo.builder().value(ValueHolder.ofEnum("Value1", Constants.INT_TYPE_INFO, 1)).build()
            ))
            .build();
        assertThat(converter.mapEnumValueType(enumType, enumDef)).isEqualTo(Constants.INT_TYPE_INFO);
    }
}
