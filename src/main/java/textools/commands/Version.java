package textools.commands;

import java.util.ResourceBundle;

import textools.Command;

/**
 * Prints the current version of textools on the console.
 */
public class Version implements Command {

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
        System.out.println("Version: " + ResourceBundle.getBundle("textools").getString("version"));
        System.out.println("Build Date: " + ResourceBundle.getBundle("textools").getString("build.date"));
    }

}
