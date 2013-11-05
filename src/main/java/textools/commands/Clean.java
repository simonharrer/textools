package textools.commands;

import textools.Command;
import textools.FileSystemTasks;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Clean implements Command {

    @Override
    public String getName() {
        return "clean";
    }

    @Override
    public String getDescription() {
        return "Removes all generated files during a tex build";
    }

    @Override
    public void execute() {
        String[] globExpressions = {
                "*~", "*.aux", "*.bbl", "*.blg", "*.lof", "*.log", "*.lot", "*.out",
                "*.pgf", "*.dvi", "*.synctex*", "*.tdo", "*.toc", "*.tps", "*.lol", "*.bak", "*.pdf"
        };

        FileSystemTasks fileSystemTasks = new FileSystemTasks();

        for (String globExpression : globExpressions) {
            try {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("."), globExpression)) {
                    for (Path path : stream) {
                        fileSystemTasks.deleteFile(path);
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("error during deletion of files", e);
            }
        }
    }
}
