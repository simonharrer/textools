package textools.commands;

import textools.Command;

public class Validate implements Command {

    @Override
    public String getName() {
        return "validate";
    }

    @Override
    public String getDescription() {
        return "executes validate-latex and validate-bibtex commands in sequence";
    }

    @Override
    public void execute() {
        new ValidateBibtex().execute();
        new ValidateLatex().execute();
    }
}
