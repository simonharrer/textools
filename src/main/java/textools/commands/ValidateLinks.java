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
public class ValidateLinks implements Command {

    @Override
    public String getName() {
        return "validate-links";
    }

    @Override
    public String getDescription() {
        return "detects malformed and unreachable urls";
    }

    @Override
    public void execute() {
        List<Path> texFiles = new FileSystemTasks().getFilesByExtension(".tex");

        Set<Link> links = new HashSet<>();
        Latex.with(texFiles, (line, lineNumber, file) -> links.addAll(Link.find(line, lineNumber, file)));

        final Map<String, List<Link>> urlToLink = links.stream().collect(Collectors.groupingBy(l -> l.url));
        urlToLink.forEach((url, ls) -> {
            if(ls.size() > 1) {
                System.out.format("URL %s is duplicated in %s%n", url, ls.stream().map(l -> l.file + "#" + l.lineNumber).collect(Collectors.joining(";")));
            }
        });

        links.stream()
                .parallel()
                .filter(link -> {
                    try {
                        link.validateUrl();
                        return true;
                    } catch (MalformedURLException e) {
                        System.out.format("%s#%4d URL %s is malformed %s%n", link.file, link.lineNumber, link.url, e.getMessage());
                        return false;
                    }
                })
                .forEach(link -> {
                    try {
                        final int statusCode = link.getStatusCode();
                        if (statusCode < 0) {
                            System.out.format("%s#%4d URL %s returned no response%n", link.file, link.lineNumber, link.url);
                        } else if (statusCode != 200) {
                            System.out.format("%s#%4d URL %s return http status code %d%n", link.file, link.lineNumber, link.url, statusCode);
                        }
                    } catch (IOException e) {
                        System.out.format("%s#%4d URL %s %s%n", link.file, link.lineNumber, link.url, e.getMessage());
                    }
                });
    }

}
