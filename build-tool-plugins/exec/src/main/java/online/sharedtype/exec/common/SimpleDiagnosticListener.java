package online.sharedtype.exec.common;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Cause Chung
 */
final class SimpleDiagnosticListener implements DiagnosticListener<JavaFileObject> {
    private final Logger log;
    private final Path projectBaseDir;
    SimpleDiagnosticListener(Logger log, Path projectBaseDir) {
        this.log = log;
        this.projectBaseDir = projectBaseDir;
    }

    @Override
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        StringBuilder sb = new StringBuilder();

        addSourceInfo(sb, diagnostic);
        sb.append(diagnostic.getMessage(null));
        String message = sb.toString();
        switch (diagnostic.getKind()) {
            case NOTE:
            case OTHER:
                log.info(message);
                break;
            case WARNING:
            case MANDATORY_WARNING:
                log.warn(message);
                break;
            case ERROR:
                log.error(message);
                break;
        }
    }

    void addSourceInfo(StringBuilder sb, Diagnostic<? extends JavaFileObject> diagnostic) {
        if (diagnostic.getSource() == null) {
            return;
        }
        JavaFileObject source = diagnostic.getSource();
        sb.append(projectBaseDir.relativize(Paths.get(source.getName())));
        sb.append(':');
        sb.append(diagnostic.getLineNumber());
        sb.append(':');
        sb.append(diagnostic.getColumnNumber());
        sb.append(" ");
    }
}
