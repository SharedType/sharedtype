package org.jets.processor.parser.type;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.type.TypeMirror;

import lombok.RequiredArgsConstructor;
import org.jets.processor.JetsContext;
import org.jets.processor.domain.TypeSymbol;

@RequiredArgsConstructor(onConstructor_ = @Inject)
@Singleton
final class TypescriptTypeMapper implements TypeMapper {

  @Override
  public TypeSymbol map(TypeMirror typeMirror, JetsContext ctx) {

    
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'map'");
  }
  
}
