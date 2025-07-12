package online.sharedtype.exec.common;

import java.io.Writer;

/**
 * @author Cause Chung
 */
final class SimpleLoggerWriter extends Writer {
    private final Logger log;
    private final StringBuffer buffer = new StringBuffer();

    SimpleLoggerWriter(Logger log) {
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
