package textools.commands;

import textools.Command;
import textools.tasks.FileSystemTasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static textools.tasks.ConsoleTasks.askString;
import static textools.Constants.MAIN_LATEX_FILE;
import static textools.Constants.NOT_FOUND;

public class Texlipse implements Command {

    private static final String TEXLIPSE_FILE = ".texlipse";
    private static final String ECLIPSE_PROJECT_FILE = ".project";

    @Override
    public String getName() {
        return "texlipse";
    }

    @Override
    public String getDescription() {
        return "generates texlipse project files";
    }

    @Override
    public void execute() {
        FileSystemTasks tasks = new FileSystemTasks();

        String projectName = askString("enter project name", tasks.getWorkingDirectory());
        String texFile = askString("enter name of main tex file", MAIN_LATEX_FILE);

        tasks.createEmptyDirectory("tmp");
        tasks.createFile(ECLIPSE_PROJECT_FILE, getProjectFile(projectName));
        tasks.createFile(TEXLIPSE_FILE, getTexlipseFile(texFile));
    }


    private String getProjectFile(String name) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<projectDescription>\n" +
                "\t<name>" + name + "</name>\n" +
                "\t<comment></comment>\n" +
                "\t<projects>\n" +
                "\t</projects>\n" +
                "\t<buildSpec>\n" +
                "\t\t<buildCommand>\n" +
                "\t\t\t<name>net.sourceforge.texlipse.builder.TexlipseBuilder</name>\n" +
                "\t\t\t<arguments>\n" +
                "\t\t\t</arguments>\n" +
                "\t\t</buildCommand>\n" +
                "\t</buildSpec>\n" +
                "\t<natures>\n" +
                "\t\t<nature>net.sourceforge.texlipse.builder.TexlipseNature</nature>\n" +
                "\t</natures>\n" +
                "</projectDescription>";
    }

    private String getTexlipseFile(String name) {
        return "#TeXlipse project settings\n" +
                "builderNum=2\n" +
                "outputDir=\n" +
                "makeIndSty=\n" +
                "bibrefDir=\n" +
                "outputFormat=pdf\n" +
                "tempDir=tmp\n" +
                "mainTexFile=" + name + ".tex\n" +
                "outputFile=" + name + ".pdf\n" +
                "langSpell=en\n" +
                "markDer=true\n" +
                "srcDir=";
    }

    public static String getMainTexFile(Path workingDirectory) {

        Path texlipseFile = workingDirectory.resolve(TEXLIPSE_FILE);
        if (!Files.exists(texlipseFile)) {
            return NOT_FOUND;
        }

        try {
            List<String> lines = Files.readAllLines(texlipseFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith("mainTexFile")) {
                    return line.split("=")[1].trim();
                }
            }
            return NOT_FOUND;
        } catch (IOException e) {
            return NOT_FOUND;
        }
    }

}
