package textools.commands;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import textools.Command;
import textools.commands.latex.Latex;
import textools.commands.latex.Link;
import textools.tasks.FileSystemTasks;

/**
 * Find acronyms defined in the acronym package but that are not yet included.
 */
public class PrintLinksSorted implements Command {

    @Override
    public String getName() {
        return "print-links";
    }

    @Override
    public String getDescription() {
        return "prints all used urls";
    }

    @Override
    public void execute() {
        List<Path> texFiles = new FileSystemTasks().getFilesByExtension(".tex");

        Set<Link> links = new HashSet<>();
        Latex.with(texFiles, (line, lineNumber, file) -> links.addAll(Link.find(line, lineNumber, file)));

        links.stream().map(l -> l.url).sorted().forEach(System.out::println);
    }

}
