package textools.commands;

import textools.Command;
import textools.FileSystemTasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates all .tex files within the current directory and its descendants.
 * <p/>
 * Rules adopted by chktex (http://baruch.ev-en.org/proj/chktex/)
 */
public class ValidateLatex implements Command {

    @Override
    public String getName() {
        return "validate-latex";
    }

    @Override
    public String getDescription() {
        return "validates .tex files";
    }

    @Override
    public void execute() {
        List<Path> texFiles = new FileSystemTasks().getFilesByExtension(".tex");
        for (Path texFile : texFiles) {
            try {
                validateTexFile(texFile);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static Map<String, String> getRules() {
        Map<String, String> rules = new HashMap<>();
        rules.put("^\\\\footnote", "line starts with footnote");
        rules.put(" \\\\label", "space in front of label");
        rules.put(" \\\\footnote", "space in front of footnote");
        rules.put(" \\\\ref", "space in front of references instead of \"~\"");
        rules.put("\\.~?\\\\cite", "use cite before the dot");

        rules.put("\\b(from|in|and|with|see|In|From)[~ ]+\\\\cite", "instead of 'in [x]' use 'Harrer et al. [x]'");
        rules.put("(table|figure|section|listing)~\\\\ref", "capitalize Table, Figure, Listing or Section");
        rules.put("[0-9]%", "% sign after number is normally invalid");

        rules.put("e\\.g\\.[^,]", "e.g. should be followed by a comma: 'e.g.,'");
        rules.put("i\\.e\\.[^,]", "i.e. should be followed by a comma: 'i.e.,'");

        rules.put("cf\\.[^\\\\]", "use 'cf.\\ ' when using cf.");

        rules.put("(all|the|of) [0-9][^0-9]","write the numbers out, e.g., one out of three");

        rules.put("et\\. al\\.", "no dot after et, use it like that: 'et al.'");

        rules.put("\\bnon[- ]", "join non with word, e.g., nonfunctional instead of non-functional or non functional");

        rules.put("\\bteh\\b", "use 'the' instead");

        return rules;
    }

    private static Map<Pattern, String> getCompiledRules() {
        Map<Pattern, String> rules = new HashMap<>();
        for(Map.Entry<String,String> entry : getRules().entrySet()) {
            rules.put(Pattern.compile(entry.getKey()), entry.getValue());
        }
        return rules;
    }

    public static final Map<Pattern, String> COMPILED_RULES = getCompiledRules();

    private void validateTexFile(Path texFile) {
        List<String> lines = readFile(texFile);
        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);

            // only validate if line is not commented out
            if (!line.startsWith("%")) {
                for(Map.Entry<Pattern, String> entry : COMPILED_RULES.entrySet()) {
                    applyPattern(texFile, lineNumber, line, entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void applyPattern(Path texFile, int lineNumber, String line, Pattern pattern, String message) {
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            System.out.format("%s#%d,%d %s%n", texFile, lineNumber, matcher.start(), message);
        }
    }

    private List<String> readFile(Path texFile) {
        try {
            return Files.readAllLines(texFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not read in tex file " + texFile, e);
        }
    }
}
