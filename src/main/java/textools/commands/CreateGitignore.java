package textools.commands;

import textools.Command;
import textools.FileSystemTasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        String[] globExpressions = {
                "*~", "*.aux", "*.bbl", "*.blg", "*.lof", "*.log", "*.lot", "*.out",
                "*.pgf", "*.dvi", "*.synctex*", "*.tdo", "*.toc", "*.tps", "*.lol", "*.bak", "*.pdf"
        };

        List<String> content = new ArrayList<>();
        Collections.addAll(content, globExpressions);
        content.add("tmp");

        new FileSystemTasks().createFile(".gitignore", content);
    }
}
