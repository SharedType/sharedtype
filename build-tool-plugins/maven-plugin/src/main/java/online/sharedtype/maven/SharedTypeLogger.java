package online.sharedtype.maven;

import org.apache.maven.plugin.logging.Log;

import java.io.Writer;

final class SharedTypeLogger extends Writer {
    private final Log log;
    private final StringBuffer buffer = new StringBuffer();

    SharedTypeLogger(Log log) {
        this.log = log;
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        buffer.append(cbuf, off, len);
    }

    @Override
    public void flush() {
        removeNewLine(buffer);
        String message = buffer.toString();
        if (!message.isEmpty()) {
            log.info(message);
        }
        buffer.setLength(0);
    }

    @Override
    public void close() {
        flush();
    }

    private static void removeNewLine(StringBuffer buffer) {
        if (buffer.length() <= 0) {
            return;
        }
        char c;
        while ((c = buffer.charAt(buffer.length() - 1)) == '\n' || (c == '\r')) {
            buffer.delete(buffer.length() - 1, buffer.length());
        }
    }
}
