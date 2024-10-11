package org.jets.processor.parser.mapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.jets.processor.GlobalContext;
import org.jets.processor.domain.TypeSymbol;

@Singleton
final class TypescriptTypeMapper implements TypeMapper {
  private final GlobalContext ctx;
  private final Elements elemUtils;
  private final Types typeUtils;

  @Inject
  TypescriptTypeMapper(GlobalContext ctx) {
    this.ctx = ctx;
    this.elemUtils = ctx.getProcessingEnv().getElementUtils();
    this.typeUtils = ctx.getProcessingEnv().getTypeUtils();
  }

  @Override
  public TypeSymbol map(TypeMirror typeMirror) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'map'");
  }
  
}
