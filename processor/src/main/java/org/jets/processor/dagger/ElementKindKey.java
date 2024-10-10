package org.jets.processor.dagger;

import dagger.MapKey;

import javax.lang.model.element.ElementKind;

@MapKey
public @interface ElementKindKey {
    ElementKind value();
}
