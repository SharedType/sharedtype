module sharedtype.it {
    requires java.compiler;
    requires java.base;
    requires java.sql;
    requires online.sharedtype.annotation;
    requires com.fasterxml.jackson.annotation;
    requires org.joda.time;

    requires static lombok;
}
