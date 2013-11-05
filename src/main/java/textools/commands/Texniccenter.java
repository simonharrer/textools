package textools.commands;

import textools.Command;
import textools.FileSystemTasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static textools.ConsoleTasks.askString;
import static textools.Constants.NOT_FOUND;

public class Texniccenter implements Command {

    @Override
    public String getName() {
        return "texniccenter";
    }

    @Override
    public String getDescription() {
        return "generates the texniccenter project files";
    }

    @Override
    public void execute() {
        FileSystemTasks tasks = new FileSystemTasks();

        String texFile = askString("enter name of main tex file", "main");

        tasks.createFile(texFile + ".tcp", getProjectFile(texFile));
    }

    public String getProjectFile(String name) {
        return "[FormatInfo]\n" +
                "Type=TeXnicCenterProjectInformation\n" +
                "Version=4\n" +
                "\n" +
                "[ProjectInfo]\n" +
                "MainFile=" + name + ".tex\n" +
                "UseBibTeX=1\n" +
                "UseMakeIndex=0\n" +
                "ActiveProfile=LaTeX => PDF\n" +
                "ProjectLanguage=en\n" +
                "ProjectDialect=US";
    }

    public static String getMainTexFile(Path workingDirectory) {
        try {
            Path projectFile = getProjectFile(workingDirectory);

            List<String> lines = Files.readAllLines(projectFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith("MainFile")) {
                    return line.split("=")[1].trim();
                }
            }
            return NOT_FOUND;
        } catch (IOException e) {
            return NOT_FOUND;
        }
    }

    private static Path getProjectFile(Path workingDirectory) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(workingDirectory, "*.tcp")) {
            if (stream.iterator().hasNext()) {
                return stream.iterator().next();
            }
        }

        return null;
    }
}
