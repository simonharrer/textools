package textools.commands;

import textools.Command;
import textools.FileSystemTasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        List<String> globExpressions = new ArrayList<String>();

        List<String> lines = readFile("tex.gitignore");

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

    private List<String> readFile(String file) {
        List<String> lines = new ArrayList<String>();

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(file)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                // no empty lines
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
            return lines;
        } catch (IOException e) {
            throw new IllegalStateException("could not read in file " + file, e);
        }
    }
}
