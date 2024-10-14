package org.sharedtype.processor.support.dagger;

import javax.lang.model.element.ElementKind;

import dagger.MapKey;

@MapKey
public @interface ElementKindKey {
    ElementKind value();
}
