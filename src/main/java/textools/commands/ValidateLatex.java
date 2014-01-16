package textools.commands;

import textools.Command;
import textools.FileSystemTasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Validates all .tex files within the current directory and its descendants.
 * <p/>
 * Rules adopted by chktex (http://baruch.ev-en.org/proj/chktex/)
 */
public class ValidateLatex implements Command {

    @Override
    public String getName() {
        return "validate-latex";
    }

    @Override
    public String getDescription() {
        return "validates .tex files";
    }

    @Override
    public void execute() {
        List<Path> texFiles = new FileSystemTasks().getFilesByExtension(".tex");
        for (Path texFile : texFiles) {
            try {
                validateTexFile(texFile);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void validateTexFile(Path texFile) {
        List<String> lines = readFile(texFile);
        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);

			// only validate if line is not commented out
			if(!line.startsWith("%")) {
				spaceInFrontOfReferencesInsteadOfTilde(texFile, lineNumber, line);
				spaceInFrontOfFootnote(texFile, lineNumber, line);
				spaceInFrontOfLabel(texFile, lineNumber, line);
			}
        }
    }

    private void spaceInFrontOfLabel(Path texFile, int lineNumber, String line) {
        if (line.contains(" \\label")) {
            System.out.format("[%s:%d] space in front of label%n",
                    texFile, lineNumber);
        }
    }

    private void spaceInFrontOfFootnote(Path texFile, int lineNumber, String line) {
        if (line.contains(" \\footnote")) {
            System.out.format("[%s:%d] space in front of footnote%n",
                    texFile, lineNumber);
        }
    }

    private void spaceInFrontOfReferencesInsteadOfTilde(Path texFile, int lineNumber, String line) {
        if (line.contains(" \\ref")) {
            System.out.format("[%s:%d] space in front of references instead of \"~\"%n",
                    texFile, lineNumber);
        }
    }

    private List<String> readFile(Path texFile) {
        try {
            return Files.readAllLines(texFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not read in tex file " + texFile, e);
        }
    }
}
