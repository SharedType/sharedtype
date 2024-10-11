package org.jets.processor.parser.mapper;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.jets.processor.context.GlobalContext;
import org.jets.processor.support.exception.JetsInternalError;

import java.util.HashMap;
import java.util.Map;

@Singleton
final class TypescriptTypeMapper implements TypeMapper {
  private static final Map<TypeKind, String> PRIMITIVES = Map.of(
     TypeKind.BOOLEAN, "boolean",
     TypeKind.BYTE, "number",
     TypeKind.CHAR, "string",
     TypeKind.DOUBLE, "number",
     TypeKind.FLOAT, "number",
     TypeKind.INT, "number",
     TypeKind.LONG, "number",
     TypeKind.SHORT, "number"
  );
  private static final Map<String, String> PREDEFINED_OBJECT_TYPES = Map.of(
      "java.lang.Boolean", "boolean",
      "java.lang.Byte", "number",
      "java.lang.Character", "string",
      "java.lang.Double", "number",
      "java.lang.Float", "number",
      "java.lang.Integer", "number",
      "java.lang.Long", "number",
      "java.lang.Short", "number",
      "java.lang.String", "string",
      "java.lang.Void", "never"
  );
  private static final String OBJECT_NAME = Object.class.getName();
  private final GlobalContext ctx;
  private final Elements elemUtils;
  private final Types typeUtils;
  private final Map<String, String> objectTypes;

  @Inject
  TypescriptTypeMapper(GlobalContext ctx) {
    this.ctx = ctx;
    this.elemUtils = ctx.getProcessingEnv().getElementUtils();
    this.typeUtils = ctx.getProcessingEnv().getTypeUtils();
    this.objectTypes = new HashMap<>(PREDEFINED_OBJECT_TYPES);
    objectTypes.put(OBJECT_NAME, ctx.getProps().getJavaObjectMapType());
  }

  @Override
  public Result map(TypeMirror typeMirror) {
    var qualifiedName = typeMirror.toString();
    var typeKind = typeMirror.getKind();
    if (typeKind.isPrimitive()) {
      return new Result(qualifiedName, PRIMITIVES.get(typeKind));
    } else if (typeKind == TypeKind.ARRAY) {
      throw new UnsupportedOperationException("Not implemented");
    } else if (typeKind == TypeKind.DECLARED) {
      var targetTypeName = objectTypes.get(qualifiedName);
      if (targetTypeName != null) {
        return new Result(qualifiedName, targetTypeName);
      } else {
        if (!ctx.hasType(qualifiedName)) {
          throw new JetsInternalError(String.format("Java type not found: '%s'", qualifiedName));
        }
        return new Result(qualifiedName, ctx.getTypename(qualifiedName));
      }
    } else {
      ctx.error("Unsupported type kind: " + typeKind);
      return new Result(null, null);
    }
  }
}
