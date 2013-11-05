package textools.commands;

import textools.Command;

/**
 * Prints the current version of textools on the console.
 */
public class Version implements Command {

    public static final String VERSION = "0.0.1";

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "prints the current version";
    }

    @Override
    public void execute() {
        System.out.println(VERSION);
    }

}
