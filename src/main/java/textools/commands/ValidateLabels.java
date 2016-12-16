package textools.commands;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import textools.Command;
import textools.commands.latex.Latex;
import textools.tasks.FileSystemTasks;

/**
 * Find acronyms defined in the acronym package but that are not yet included.
 */
public class ValidateLabels implements Command {

    private static final Pattern LABEL = Pattern.compile("\\\\label\\{(?<label>[^\\}]*)\\}");
    private static final Pattern REF = Pattern.compile("\\\\(ref|cref|nameref)\\{(?<ref>[^\\}]*)\\}");

    @Override
    public String getName() {
        return "validate-labels";
    }

    @Override
    public String getDescription() {
        return "detects unused labels";
    }

    @Override
    public void execute() {
        List<Path> texFiles = new FileSystemTasks().getFilesByExtension(".tex");

        Set<String> labels = new HashSet<>();

        Latex.with(texFiles, (line, lineNumber, file) -> {
            final Matcher matcher = LABEL.matcher(line);
            while (matcher.find()) {
                labels.add(matcher.group("label"));
            }
        });

        Set<String> refs = new HashSet<>();

        Latex.with(texFiles, (line, lineNumber, file) -> {
            final Matcher matcher = REF.matcher(line);
            while (matcher.find()) {
                refs.add(matcher.group("ref"));
            }
        });

        Set<String> unusedLabels = new HashSet<>(labels);
        unusedLabels.removeAll(refs);

        unusedLabels.stream().sorted().forEach(label -> System.out.format("unused label %s%n", label));
    }

}
