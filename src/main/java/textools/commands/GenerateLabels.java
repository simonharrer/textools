package textools.commands;

import textools.Command;
import textools.commands.latex.RefFinder;
import textools.commands.latex.Reference;
import textools.commands.latex.Structure;
import textools.commands.latex.StructureFinder;
import textools.tasks.FileSystemTasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Generates labels and ensure that the labels are consistent with the latex elements they refer to.
 * <p/>
 * Assumptions:
 * - do not use macros that define labels and use refs
 * - label of a section is set directly behind its definition
 * <p/>
 * Results:
 * - labels will be kept in sync, even when they are changed
 * - labels are determined from the name of the section. if the section name changes, the label will be refactored
 */
public class GenerateLabels implements Command {

    @Override
    public String getName() {
        return "generate-labels";
    }

    @Override
    public String getDescription() {
        return "generates labels for each of the structural elements";
    }

    @Override
    public void execute() {
        List<Path> texFiles = new FileSystemTasks().getFilesByExtension(".tex");

        // (1) determine refs and structure + labels
        List<Structure> structures = new LinkedList<>();
        List<Reference> references = new LinkedList<>();

        StructureFinder structureFinder = new StructureFinder();
        RefFinder referencesFinder = new RefFinder();
        for (Path texFile : texFiles) {
            for (String line : readFile(texFile)) {
                structures.addAll(structureFinder.findStructures(line));
                references.addAll(referencesFinder.findRefs(line));
            }
        }

        // (2) determine new labels
        Map<String, String> oldLabels2NewLabels = new HashMap<>();

        // (3) apply new labels consistently
        for (Path texFile : texFiles) {

        }

    }

    private List<String> readFile(Path texFile) {
        try {
            return Files.readAllLines(texFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not read " + texFile + ": " + e.getMessage(), e);
        }
    }

}
