package org.jets.processor.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class TypeCache {
    private final Set<String> qualifiedNames = new HashSet<>();
    private final Map<String, String> names = new HashMap<>();

    public void add(String qualifiedName, String name) {
        qualifiedNames.add(qualifiedName);
        names.put(qualifiedName, name);
    }

    public String getName(String qualifiedName) {
        return names.get(qualifiedName);
    }

    public boolean contains(String qualifiedName) {
        return qualifiedNames.contains(qualifiedName);
    }
}
