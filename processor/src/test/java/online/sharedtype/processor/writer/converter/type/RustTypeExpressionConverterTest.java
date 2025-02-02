package online.sharedtype.processor.writer.converter.type;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.RenderFlags;
import online.sharedtype.processor.domain.ArrayTypeInfo;
import online.sharedtype.processor.domain.ClassDef;
import online.sharedtype.processor.domain.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class RustTypeExpressionConverterTest {
    private final ContextMocks contextMocks = new ContextMocks();
    private final RustTypeExpressionConverter converter = new RustTypeExpressionConverter(contextMocks.getContext());

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
    }

    @Test
    void addSmartPointerBoxToCyclicReferencedType() {
        var classDef = ClassDef.builder().cyclicReferenced(true).build();
        var expr = converter.toTypeExpression(ConcreteTypeInfo.builder().simpleName("Abc").typeDef(classDef).build(), "Abc");
        assertThat(expr).isEqualTo("Box<Abc>");
    }
}
