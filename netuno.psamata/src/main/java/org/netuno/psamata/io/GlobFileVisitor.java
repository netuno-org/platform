package org.netuno.psamata.io;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class GlobFileVisitor extends SimpleFileVisitor<Path> {
    private final PathMatcher pathMatcher;

    private Path path;

    public GlobFileVisitor(String glob) {
        this.pathMatcher = FileSystems.getDefault().getPathMatcher(glob);
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        if (pathMatcher.matches(path)) {
            this.path = path;
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    public Path getFoundPath() {
        return path;
    }

    public static Path find(String location, String glob) throws IOException {
        return find(Paths.get(location), glob);
    }

    public static Path find(Path location, String glob) throws IOException {
        GlobFileVisitor globFileVisitor = new GlobFileVisitor(glob);
        Files.walkFileTree(location, globFileVisitor);
        return globFileVisitor.getFoundPath();
    }
}
