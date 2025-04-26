package online.sharedtype.processor.parser;

import online.sharedtype.SharedType;
import online.sharedtype.processor.context.Config;
import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.context.TestUtils;
import online.sharedtype.processor.context.TypeElementMock;
import online.sharedtype.processor.domain.type.ConcreteTypeInfo;
import online.sharedtype.processor.domain.Constants;
import online.sharedtype.processor.domain.EnumDef;
import online.sharedtype.processor.domain.value.ValueHolder;
import online.sharedtype.processor.parser.type.TypeInfoParser;
import online.sharedtype.processor.parser.value.ValueResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class EnumTypeDefParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser typeInfoParser = mock(TypeInfoParser.class);
    private final ValueResolver valueResolver = mock(ValueResolver.class);
    private final EnumTypeDefParser parser = new EnumTypeDefParser(ctxMocks.getContext(), typeInfoParser, valueResolver);

    private final ArgumentCaptor<Config> configCaptor = ArgumentCaptor.forClass(Config.class);

    private final TypeElementMock enumType = ctxMocks.typeElement("com.github.cuzfrog.EnumA")
        .withElementKind(ElementKind.ENUM);

    @BeforeEach
    void beforeEach() {
        when(ctxMocks.getContext().isAnnotatedAsEnumValue(any())).then(
            invocation -> invocation.<Element>getArgument(0).getAnnotation(SharedType.EnumValue.class) != null);
    }

    @Test
    void skipIfNotEnum() {
        var clazz = ctxMocks.typeElement("com.github.cuzfrog.Abc").withElementKind(ElementKind.CLASS);
        assertThat(parser.parse(clazz.element())).isEmpty();
    }

    @Test
    void simpleEnum() {
        var anno = TestUtils.defaultSharedTypeAnnotation();
        var enumConstant1 = ctxMocks.declaredTypeVariable("Value1", enumType.type()).withElementKind(ElementKind.ENUM_CONSTANT).element();
        var enumConstant2 = ctxMocks.declaredTypeVariable("Value2", enumType.type()).withElementKind(ElementKind.ENUM_CONSTANT).element();
        enumType
            .withAnnotation(SharedType.class, () -> anno)
            .withEnclosedElements(enumConstant1, enumConstant2);

        ConcreteTypeInfo typeInfo = ConcreteTypeInfo.builder().qualifiedName("com.github.cuzfrog.EnumA").build();
        when(typeInfoParser.parse(enumType.type(), enumType.element())).thenReturn(typeInfo);
        var value1 = ValueHolder.ofEnum(enumConstant1.getSimpleName().toString(), Constants.STRING_TYPE_INFO, "Value1");
        when(valueResolver.resolve(enumConstant1, enumType.element())).thenReturn(value1);
        var value2 = ValueHolder.ofEnum(enumConstant2.getSimpleName().toString(), Constants.STRING_TYPE_INFO,"Value2");
        when(valueResolver.resolve(enumConstant2, enumType.element())).thenReturn(value2);

        EnumDef typeDef = (EnumDef) parser.parse(enumType.element()).getFirst();
        assertThat(typeDef.qualifiedName()).isEqualTo("com.github.cuzfrog.EnumA");
        assertThat(typeDef.simpleName()).isEqualTo("EnumA");
        assertThat(typeDef.components()).satisfiesExactly(
            c1 -> {
                assertThat(c1.value().getValue()).isEqualTo("Value1");
                assertThat(c1.value().getEnumConstantName()).isEqualTo("Value1");
                assertThat(c1.type()).isEqualTo(Constants.STRING_TYPE_INFO);
                assertThat(c1.name()).isEqualTo("Value1");
            },
            c2 -> {
                assertThat(c2.value().getValue()).isEqualTo("Value2");
                assertThat(c2.type()).isEqualTo(Constants.STRING_TYPE_INFO);
                assertThat(c2.name()).isEqualTo("Value2");
            }
        );
        assertThat(typeDef.typeInfoSet()).containsExactly(typeInfo);

        verify(ctxMocks.getTypeStore()).saveConfig(configCaptor.capture());
        var config = configCaptor.getValue();
        assertThat(config.getQualifiedName()).isEqualTo("com.github.cuzfrog.EnumA");
        assertThat(config.getAnno()).isSameAs(anno);
        assertThat(typeDef.isAnnotated()).isTrue();
    }
}
