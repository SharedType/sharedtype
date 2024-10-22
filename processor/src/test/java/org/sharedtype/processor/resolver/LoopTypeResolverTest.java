package org.sharedtype.processor.resolver;

import org.junit.jupiter.api.Test;
import org.sharedtype.processor.context.ContextMocks;
import org.sharedtype.processor.domain.ArrayTypeInfo;
import org.sharedtype.processor.domain.ClassDef;
import org.sharedtype.processor.domain.ConcreteTypeInfo;
import org.sharedtype.processor.domain.FieldComponentInfo;
import org.sharedtype.processor.parser.TypeDefParser;

import javax.lang.model.element.TypeElement;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class LoopTypeResolverTest {
    private final ContextMocks ctxMocks = new ContextMocks();
    private final TypeDefParser typeDefParser = mock(TypeDefParser.class);
    private final LoopTypeResolver resolver = new LoopTypeResolver(ctxMocks.getContext(), typeDefParser);

    @Test
    void resolveFullClass() {
        var typeDef = ClassDef.builder()
            .qualifiedName("com.github.cuzfrog.Abc")
            .name("Abc")
            .components(List.of(
                FieldComponentInfo.builder().name("a").type(
                    ConcreteTypeInfo.builder()
                        .qualifiedName("com.github.cuzfrog.Map")
                        .simpleName("Map")
                        .typeArgs(List.of(
                            ConcreteTypeInfo.builder()
                                .qualifiedName("com.github.cuzfrog.Key")
                                .simpleName("Key")
                                .resolved(false)
                                .build(),
                            new ArrayTypeInfo(ConcreteTypeInfo.builder()
                                .qualifiedName("com.github.cuzfrog.Abc")
                                .simpleName("Abc")
                                .resolved(false)
                                .build())
                        ))
                        .build()
                ).build()
            ))
            .build();
        when(typeDefParser.parse(mockElementByName("com.github.cuzfrog.Abc"))).thenReturn(
            ClassDef.builder().build()
        );

        var defs = resolver.resolve(List.of(typeDef));
    }

    private TypeElement mockElementByName(String qualifiedName) {
        var typeElement = ctxMocks.typeElement(qualifiedName).element();
        when(ctxMocks.getElements().getTypeElement(qualifiedName)).thenReturn(typeElement);
        return typeElement;
    }
}
