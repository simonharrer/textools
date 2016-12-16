package textools.commands;

import textools.Command;

import static textools.Main.COMMANDS;

public class Help implements Command {

    private static final int COMMAND_SHORT_LENGTH = 30;

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "prints usage information";
    }

    @Override
    public void execute() {
        System.out.format("textools [command]%n%n");
        for (Command command : COMMANDS) {
            StringBuilder firstPart = new StringBuilder();
            firstPart.append(' ');
            firstPart.append(command.getName());
            while (firstPart.length() < COMMAND_SHORT_LENGTH) {
                firstPart.append(' ');
            }
            System.out.format("%s%s%n", firstPart, command.getDescription());
        }
    }
}
