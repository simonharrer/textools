package textools.commands.latex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface Latex {

    void with(String line, int lineNumber, Path file);

    public static void with(List<Path> files, Latex withLatexLine) {
        for (Path path : files) {
            try {
                with(path, withLatexLine);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void with(Path file, Latex withLatexLine) {
        List<String> lines = readFile(file);
        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);
            if (line.startsWith("%")) {
                continue;
            }

            withLatexLine.with(line, lineNumber, file);
        }
    }

    static List<String> readFile(Path texFile) {
        try {
            return Files.readAllLines(texFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not read " + texFile + ": " + e.getMessage(), e);
        }
    }
}
