package textools.commands;

import textools.Command;
import textools.FileSystemTasks;

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
        new FileSystemTasks().copyFile("tex.gitignore");
    }
}
