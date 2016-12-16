package textools.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import textools.Command;
import textools.tasks.FileSystemTasks;

/**
 * Find acronyms defined in the acronym package but that are not yet included.
 */
public class FindAcronyms implements Command {

    public static class Acronym {

        private static final Pattern DEFINITION_DETECTION = Pattern.compile("\\\\acro\\{(?<acro>[a-zA-Z-]*)\\}");

        private final String name;
        private final Pattern usageDetectionPattern;

        public Acronym(String name) {
            this.name = name;
            this.usageDetectionPattern = Pattern.compile("(\\s|\\A|\\()" + name + "(\\s|\\.|!|\\z|\\)|,)");
        }

        public static Optional<Acronym> find(String line) {
            final Matcher matcher = DEFINITION_DETECTION.matcher(line);
            if (matcher.find()) {
                return Optional.of(new Acronym(matcher.group("acro")));
            } else {
                return Optional.empty();
            }
        }

        public boolean isInLine(String line) {
            return usageDetectionPattern.matcher(line).find();
        }

        @Override public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Acronym acronym = (Acronym) o;
            return Objects.equals(name, acronym.name);
        }

        @Override public int hashCode() {
            return Objects.hash(name);
        }

        @Override public String toString() {
            final StringBuilder sb = new StringBuilder("Acronym{");
            sb.append("name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String getName() {
        return "find-acronyms";
    }

    @Override
    public String getDescription() {
        return "find acronyms";
    }

    @Override
    public void execute() {
        List<Path> texFiles = new FileSystemTasks().getFilesByExtension(".tex");

        Set<Acronym> acronyms = new HashSet<>();
        for (Path texFile : texFiles) {
            List<String> lines = readFile(texFile);
            for (String line : lines) {
                Acronym.find(line).ifPresent(acronyms::add);
            }
        }

        for (Path texFile : texFiles) {
            List<String> lines = readFile(texFile);
            for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
                String line = lines.get(lineNumber - 1);

                for (Acronym acro : acronyms) {
                    if (acro.isInLine(line)) {
                        System.out.format("%s:%d:%s - %s%n", texFile.toString(), lineNumber, acro, line);
                    }
                }
            }
        }
    }

    private List<String> readFile(Path texFile) {
        try {
            return Files.readAllLines(texFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not read " + texFile + ": " + e.getMessage(), e);
        }
    }
}
