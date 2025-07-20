package online.sharedtype.gradle;

import org.slf4j.Logger;

final class GradleLoggerAdaptor implements online.sharedtype.exec.common.Logger {
    private final Logger delegate;
    GradleLoggerAdaptor(Logger delegate) {
        this.delegate = delegate;
    }
    @Override
    public void info(String s) {
        delegate.info(s);
    }
    @Override
    public void warn(String s) {
        delegate.warn(s);
    }
    @Override
    public void error(String s) {
        delegate.error(s);
    }
}
