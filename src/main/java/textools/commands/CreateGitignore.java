package textools.commands;

import textools.Command;
import textools.tasks.FileSystemTasks;

public class CreateGitignore implements Command {

    @Override
    public String getName() {
        return "create-gitignore";
    }

    @Override
    public String getDescription() {
        return "creates a latex project specific .gitignore file";
    }

    @Override
    public void execute() {
        // cannot reference .gitignore files within a jar
        new FileSystemTasks().copyFile("tex.gitignore", ".gitignore");
    }
}
