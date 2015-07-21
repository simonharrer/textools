package textools.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileSystemTasks {

    private final Path workingDirectory = Paths.get(".");

    public List<String> readFile(String file) {
        try {
            Path path = workingDirectory.resolve(file);
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not read " + file + ": " + e.getMessage(), e);
        }
    }

    public void deleteFile(Path path) {
        if (!Files.exists(path)) {
            // do nothing when file does not exist
            return;
        }

        System.out.println("\tdeleting " + workingDirectory.resolve(path).getFileName());
        try {
            Files.delete(workingDirectory.resolve(path));
        } catch (IOException e) {
            throw new IllegalStateException("could not delete " + workingDirectory.resolve(path).getFileName(), e);
        }
    }

    public void createEmptyDirectory(String directory) {
        System.out.println("\tcreating " + directory);
        try {
            Files.createDirectories(workingDirectory.resolve(directory));
        } catch (IOException e) {
            throw new IllegalStateException("could not create " + directory, e);
        }
    }

    public void createFile(String file, List<String> lines) {
        System.out.println("\tcreating " + workingDirectory.resolve(file).getFileName() + " with " + lines.size() + " lines");
        try {
            Files.write(workingDirectory.resolve(file), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create " + file, e);
        }
    }

    public void createFile(String file, String content) {
        createFile(file, Arrays.asList(content.split("\n")));
    }

    public void copyResourceToFile(String source, String target) {
        Path targetPath = workingDirectory.resolve(target);
        System.out.println("\tcopying " + source + " to " + workingDirectory.relativize(targetPath));

        try (InputStream in = getClass().getResourceAsStream("/" + source)) {

            if (in == null) {
                throw new IllegalStateException("Cannot find resource " + source);
            }

            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);

            if (!Files.exists(targetPath)) {
                throw new IllegalStateException("Target " + targetPath + " must exist after copy");
            }

        } catch (IOException e) {
            throw new IllegalStateException("Could not copy " + source + " to " + target, e);
        }
    }

    public String getWorkingDirectory() {
        return this.workingDirectory.toAbsolutePath().getParent().getFileName().toString();
    }

    public List<Path> getFilesByExtension(final String fileExtension) {
        final List<Path> result = new LinkedList<>();

        try {
            Files.walkFileTree(workingDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                    if (file.getFileName().toString().endsWith(fileExtension)) {
                        result.add(workingDirectory.relativize(file));
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("problems during finding " + fileExtension + " files", e);
        }

        return result;
    }

}
