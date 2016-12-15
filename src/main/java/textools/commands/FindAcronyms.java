package textools.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import textools.Command;
import textools.tasks.FileSystemTasks;

/**
 * Find unconverted acronyms
 */
public class FindAcronyms implements Command {

    private static final Pattern ACRO_DETECTION = Pattern.compile("\\\\acro\\{(?<acro>[a-zA-Z-]*)\\}");

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

        Set<String> acronyms = new HashSet<>();
        for (Path texFile : texFiles) {
            List<String> lines = readFile(texFile);
            for (String line : lines) {
                final Matcher matcher = ACRO_DETECTION.matcher(line);
                if (matcher.find()) {
                    acronyms.add(matcher.group("acro"));
                }
            }
        }

        Map<String, Pattern> patterns = new HashMap<>();
        for(String acro : acronyms) {
            patterns.put(acro, Pattern.compile("(\\s|\\A|\\()" + acro + "(\\s|\\.|!|\\z|\\)|,)"));
        }

        for (Path texFile : texFiles) {
            List<String> lines = readFile(texFile);
            for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
                String line = lines.get(lineNumber - 1);

                for (String acro : acronyms) {
                    final Matcher matcher = patterns.get(acro).matcher(line);
                    if (matcher.find()) {
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
