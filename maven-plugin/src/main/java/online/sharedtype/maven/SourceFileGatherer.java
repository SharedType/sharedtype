package online.sharedtype.maven;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

final class SourceFileGatherer extends SimpleFileVisitor<Path> {
    private final List<File> files = new ArrayList<>();

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        files.add(file.toFile());
        return FileVisitResult.CONTINUE;
    }

    List<File> getFiles() {
        return files;
    }
}
