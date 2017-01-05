package textools;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import textools.commands.Cites;
import textools.commands.Clean;
import textools.commands.CreateGitignore;
import textools.commands.Help;
import textools.commands.MinifyBibtexAuthors;
import textools.commands.MinifyBibtexOptionals;
import textools.commands.Pdf;
import textools.commands.PdfClean;
import textools.commands.Texlipse;
import textools.commands.Texniccenter;
import textools.commands.Validate;
import textools.commands.ValidateAcronym;
import textools.commands.ValidateBibtex;
import textools.commands.ValidateLabels;
import textools.commands.ValidateLatex;
import textools.commands.ValidateLinks;
import textools.commands.Version;

public class Main {

    private static final Command DEFAULT = new Help();

    public static final List<Command> COMMANDS = Stream.of(
            new CreateGitignore(),
            new Clean(),
            new Cites(),
            new Texlipse(),
            new Texniccenter(),
            new Validate(),
            new ValidateBibtex(),
            new ValidateLatex(),
            new ValidateAcronym(),
            new ValidateLabels(),
            new MinifyBibtexOptionals(),
            new MinifyBibtexAuthors(),
            new Pdf(),
            new PdfClean(),
            new ValidateLinks(),
            new Version(),
            DEFAULT
    ).sorted(Comparator.comparing(Command::getName)).collect(Collectors.toList());

    public static void main(String... args) throws IOException {
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
