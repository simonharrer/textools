package textools.commands;

import textools.Command;
import textools.tasks.FileSystemTasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        List<String> globExpressions = new ArrayList<>();

        // TODO search for .gitignore in current directory. if it does not find it, use internal one

        List<String> lines = getGitIgnoreFileContents();

        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);

            // ignore uncommented lines
            if (!line.startsWith("#")) {
                globExpressions.add(line);
            }
        }

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

    private List<String> getGitIgnoreFileContents() {
        try {
            return new FileSystemTasks().readFile(".gitignore");
        } catch (Exception e) {
            return readFromResourceStream("/tex.gitignore");
        }
    }

    private List<String> readFromResourceStream(String source) {
        List<String> result = new ArrayList<>();
        try (InputStream in = getClass().getResourceAsStream(source)) {

            if (in == null) {
                throw new IllegalStateException("Cannot find resource " + source);
            }

            Scanner scanner = new Scanner(in);
            while(scanner.hasNextLine()) {
                result.add(scanner.nextLine());
            }

        } catch (IOException e) {
            throw new IllegalStateException("could not read from stream " + source, e);
        }
        return result;
    }

}
