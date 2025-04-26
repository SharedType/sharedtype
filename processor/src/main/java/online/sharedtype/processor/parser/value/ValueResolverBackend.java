package online.sharedtype.processor.parser.value;

interface ValueResolverBackend {
    Object recursivelyResolve(ValueResolveContext parsingContext);

    static ValueResolverBackend create(ValueResolver valueResolver) {
        return new ValueResolverBackendImpl(valueResolver);
    }
}
