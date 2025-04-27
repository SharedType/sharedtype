package online.sharedtype.processor.parser.value;

interface ValueResolverBackend {
    Object recursivelyResolve(ValueResolveContext parsingContext);
}
