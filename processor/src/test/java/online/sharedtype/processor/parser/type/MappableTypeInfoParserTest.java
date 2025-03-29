package online.sharedtype.processor.parser.type;

import online.sharedtype.processor.context.ContextMocks;
import online.sharedtype.processor.domain.DateTimeInfo;
import online.sharedtype.processor.domain.TargetCodeType;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;

import javax.lang.model.type.TypeMirror;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SetSystemProperty(key = "sharedtype.typescript.type-mappings", value = "a.b.MyDateTime:MyString")
@SetSystemProperty(key = "sharedtype.rust.type-mappings", value = "a.b.MyDateTime:MyStringR")
final class MappableTypeInfoParserTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeInfoParser delegate = mock(TypeInfoParser.class);
    private final MappableTypeInfoParser parser = new MappableTypeInfoParser(ctxMocks.getContext(), delegate);

    @Test
    void addTypeMappings() {
        TypeContext typeContext = TypeContext.builder().build();
        TypeMirror typeMirror1 = ctxMocks.typeElement("a.b.MyDateTime").type();

        DateTimeInfo dateTimeInfo = new DateTimeInfo("a.b.MyDateTime");
        when(delegate.parse(typeMirror1, typeContext)).thenReturn(dateTimeInfo);

        var resTypeInfo = parser.parse(typeMirror1, typeContext);
        assertThat(resTypeInfo).isSameAs(dateTimeInfo);
        assertThat(dateTimeInfo.mappedNameOrDefault(TargetCodeType.TYPESCRIPT, "DefaultName")).isEqualTo("MyString");
        assertThat(dateTimeInfo.mappedNameOrDefault(TargetCodeType.RUST, "DefaultName")).isEqualTo("MyStringR");
    }
}
