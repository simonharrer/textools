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

        Set<String> definedlabels = new HashSet<>();

        Latex.with(texFiles, (line, lineNumber, file) -> {
            final Matcher matcher = LABEL.matcher(line);
            while (matcher.find()) {
                definedlabels.add(matcher.group("label"));
            }
        });

        Set<String> referencedLabels = new HashSet<>();

        Latex.with(texFiles, (line, lineNumber, file) -> {
            final Matcher matcher = REF.matcher(line);
            while (matcher.find()) {
                referencedLabels.add(matcher.group("ref"));
            }
        });

        Set<String> unusedDefinedLabels = new HashSet<>(definedlabels);
        unusedDefinedLabels.removeAll(referencedLabels);
        unusedDefinedLabels
                .stream()
                .sorted()
                .forEach(label -> System.out.format("unused defined label %s%n", label));

        Set<String> missingReferencedLabels = new HashSet<>(referencedLabels);
        missingReferencedLabels.removeAll(definedlabels);
        missingReferencedLabels
                .stream()
                .filter(l -> !l.startsWith("lst:")) // because labels of listings cannot be detected easily, and to prevent a lot of false negatives
                .sorted()
                .forEach(label -> System.out.format("referenced label %s is missing%n", label));
    }

}
