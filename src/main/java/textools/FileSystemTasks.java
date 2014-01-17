package textools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileSystemTasks {

    private Path workingDirectory = Paths.get(".");

    public void deleteFile(Path path) {
        if (!Files.exists(path)) {
            // do nothing when file does not exist
            return;
        }

        System.out.println("\tdeleting file " + workingDirectory.resolve(path).getFileName());
        try {
            Files.delete(workingDirectory.resolve(path));
        } catch (IOException e) {
            throw new IllegalStateException("could not delete file " + workingDirectory.resolve(path).getFileName(), e);
        }
    }

    public void createEmptyDirectory(String directory) {
        System.out.println("\tcreating directory " + directory);
        try {
            Files.createDirectories(workingDirectory.resolve(directory));
        } catch (IOException e) {
            throw new IllegalStateException("could not create directory " + directory, e);
        }
    }

    public void createFile(String file, List<String> lines) {
        System.out.println("\tcreating file " + workingDirectory.resolve(file).getFileName() + " with " + lines.size() + " lines");
        try {
            Files.write(workingDirectory.resolve(file), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create file " + file, e);
        }
    }

    public void createFile(String file, String content) {
        createFile(file, Arrays.asList(content.split("\n")));
    }

    public void copyFile(String source, String target) {
        System.out.println("\tcopying file " + Paths.get(source).getFileName() + " to " + Paths.get(target).getFileName());

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(source)) {
            Files.copy(in, workingDirectory.resolve(target), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Could not copy file " + source + " to " + target, e);
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
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().endsWith(fileExtension)) {
                        result.add(file);
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
