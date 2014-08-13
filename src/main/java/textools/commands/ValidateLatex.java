package textools.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import textools.Command;
import textools.tasks.FileSystemTasks;

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
        rules.put("^\\\\footnote(\\{|\\[)", "line starts with footnote");
        rules.put(" \\\\label", "space in front of label");
        rules.put(" \\\\footnote", "space in front of footnote");
        rules.put("[^~]\\\\ref", "use '~\\ref' to prevent bad line breaks");
        rules.put("(?<!et al)\\.~?\\\\cite", "use cite before the dot"); // use negative lookbehind in regex
        rules.put("[^~]\\\\cite", "use '~\\cite' to prevent bad line breaks");

        rules.put("\\b(from|in|and|with|see|In|From|With|And|See)~\\\\cite", "instead of 'in [x]' use 'Harrer et al. [x]'");
        rules.put("(table|figure|section|listing|chapter|theorem|corollary|definition)~\\\\ref",
				"capitalize Table, Figure, Listing, Section, Chapter, Theorem, Corollary, Definition; use abbreviations: Table, Fig., Sect., Chap., Theorem, Corollary, Definition when used with numbers, e.g. Fig.3, Table 1, Theorem 2");
        rules.put("[0-9]%", "% sign after number is normally invalid");

        rules.put("e\\.g\\.[^,]", "use 'e.g.,' instead of 'e.g.'");
        rules.put("i\\.e\\.[^,]", "use 'i.e.,' instead of 'i.e.'");

        rules.put("cf\\.[^\\\\]", "use 'cf.\\ ' instead of 'cf. '");

        rules.put("(All|The|Of|all|the|of) [0-9][^0-9]", "write the numbers out, e.g., one out of three");

        rules.put("et\\. al\\.", "use 'et al.' instead of 'et. al.'");

        rules.put("\\b[Nn]on[- ]", "join non with word, e.g., nonfunctional instead of non-functional or non functional");

        rules.put("\\b[Tt]eh\\b", "use 'the' instead of 'teh'");

        rules.put("[ ],", "no space before a comma");
        rules.put(",,", "no double comma");

        rules.put("(In|in) order to", "instead of 'in order to' use 'to'");
        
        rules.put("behaviour", "Use the AE when possible: 'behavior'");

        return rules;
    }

    private static Map<Pattern, String> getCompiledRules() {
        Map<Pattern, String> rules = new HashMap<>();
        for (Map.Entry<String, String> entry : getRules().entrySet()) {
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
                for (Map.Entry<Pattern, String> entry : COMPILED_RULES.entrySet()) {
                    applyPattern(texFile, lineNumber, line, entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void applyPattern(Path texFile, int lineNumber, String line, Pattern pattern, String message) {
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            System.out.format("%s#%4d,%-4d %s%n", texFile, lineNumber, matcher.start(), message);
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
