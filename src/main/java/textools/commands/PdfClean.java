package textools.commands;

import textools.Command;

public class PdfClean implements Command {

    @Override
    public String getName() {
        return "pdfclean";
    }

    @Override
    public String getDescription() {
        return "executes pdf and clean commands in sequence";
    }

    @Override
    public void execute() {
        new Pdf().execute();
        new Clean().execute();
    }
}