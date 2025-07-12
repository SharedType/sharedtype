package online.sharedtype.maven;

import online.sharedtype.exec.common.Logger;
import org.apache.maven.plugin.logging.Log;

final class MavenLoggerAdaptor implements Logger {
    private final Log log;
    MavenLoggerAdaptor(Log log) {
        this.log = log;
    }

    @Override
    public void info(String message) {
        log.info(message);
    }
    @Override
    public void warn(String message) {
        log.warn(message);
    }
    @Override
    public void error(String message) {
        log.error(message);
    }
}
