package textools;

import java.io.IOException;

import textools.commands.Cites;
import textools.commands.Clean;
import textools.commands.CreateGitignore;
import textools.commands.FindAcronyms;
import textools.commands.Help;
import textools.commands.MinifyBibtexAuthors;
import textools.commands.MinifyBibtexOptionals;
import textools.commands.Pdf;
import textools.commands.PdfClean;
import textools.commands.Texlipse;
import textools.commands.Texniccenter;
import textools.commands.Validate;
import textools.commands.ValidateBibtex;
import textools.commands.ValidateLatex;
import textools.commands.Version;

public class Main {

    private static final Command DEFAULT = new Help();

    public static final Command[] COMMANDS = {
            new CreateGitignore(),
            new Clean(),
            new Cites(),
            new FindAcronyms(),
            new Texlipse(),
            new Texniccenter(),
            new Validate(),
            new ValidateBibtex(),
            new ValidateLatex(),
            new MinifyBibtexOptionals(),
            new MinifyBibtexAuthors(),
            new Pdf(),
            new PdfClean(),
            new Version(),
            DEFAULT
    };

    public static void main(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            DEFAULT.execute();
            System.exit(0);
        }

        String commandName = args[0];
        Command command = findCommandByName(commandName);
        try {
            command.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static Command findCommandByName(String command) {
        for (Command task : COMMANDS) {
            if (command.equals(task.getName())) {
                return task;
            }
        }
        return DEFAULT;
    }

    private Main() {
    }

}
