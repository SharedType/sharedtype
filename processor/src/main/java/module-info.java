module sharedtype.processor {
    requires sharedtype.annotation;
    requires java.base;
    requires java.compiler;
    requires jdk.compiler;
    requires static lombok;
    requires static com.google.auto.service;

    requires jsr305;
    requires com.github.mustachejava;

    provides javax.annotation.processing.Processor with online.sharedtype.processor.AnnotationProcessorImpl;
}
