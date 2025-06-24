package online.sharedtype.maven;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

final class SourceFileVisitor extends SimpleFileVisitor<Path> {
    private static final String FILE_EXTENSION = ".java";
    private final List<File> files = new ArrayList<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        if (file.toString().endsWith(FILE_EXTENSION)) {
            files.add(file.toFile());
        }
        return FileVisitResult.CONTINUE;
    }

    List<File> getFiles() {
        return files;
    }
}
