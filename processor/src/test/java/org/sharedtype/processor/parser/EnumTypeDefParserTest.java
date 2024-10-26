package org.sharedtype.processor.parser;

import org.junit.jupiter.api.Test;
import org.sharedtype.domain.EnumDef;
import org.sharedtype.processor.context.ContextMocks;

import javax.lang.model.element.ElementKind;

import static org.assertj.core.api.Assertions.assertThat;

final class EnumTypeDefParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final EnumTypeDefParser parser = new EnumTypeDefParser(ctxMocks.getContext());

    @Test
    void parseSimpleEnum() {
        var enumType = ctxMocks.typeElement("com.github.cuzfrog.EnumA")
            .withElementKind(ElementKind.ENUM);
        enumType.withEnclosedElements(
            ctxMocks.declaredTypeVariable("Value1", enumType.type()).withElementKind(ElementKind.ENUM_CONSTANT).element(),
            ctxMocks.declaredTypeVariable("Value2", enumType.type()).withElementKind(ElementKind.ENUM_CONSTANT).element()
        );

        var typeDef = (EnumDef)parser.parse(enumType.element());
        assert typeDef != null;
        assertThat(typeDef.qualifiedName()).isEqualTo("com.github.cuzfrog.EnumA");
        assertThat(typeDef.simpleName()).isEqualTo("EnumA");
        assertThat(typeDef.components()).satisfiesExactly(
            c1 -> assertThat(c1.value()).isEqualTo("Value1"),
            c2 -> assertThat(c2.value()).isEqualTo("Value2")
        );
    }
}
