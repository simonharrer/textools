package textools.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.BibTeXParser;
import org.jbibtex.Key;
import org.jbibtex.ParseException;
import textools.Command;
import textools.tasks.FileSystemTasks;

public class MinifyBibtexOptionals implements Command {

    @Override
    public String getName() {
        return "minify-bibtex-optionals";
    }

    @Override
    public String getDescription() {
        return "removes optional keys in bibtex entries";
    }

    @Override
    public void execute() {
        List<Path> bibtexFiles = new FileSystemTasks().getFilesByExtension(".bib");

        for (Path bibtexFile : bibtexFiles) {
            try {
                BibTeXDatabase database = new BibTeXParser().parse(Files.newBufferedReader(bibtexFile, StandardCharsets.UTF_8));
                minifyDatabase(database);
                BibTeXFormatter formatter = new BibTeXFormatter();
                formatter.setIndent("  ");
                formatter.format(database, Files.newBufferedWriter(bibtexFile, StandardCharsets.UTF_8));
            } catch (IOException | ParseException e) {
                System.out.println("\tError during minification of " + bibtexFile + ". Reason: " + e.getMessage());
            }
        }
    }

    public void minifyDatabase(BibTeXDatabase database) {
        for (BibTeXEntry entry : database.getEntries().values()) {

            if ("TECHREPORT".equalsIgnoreCase(entry.getType().toString())) {

                Set<String> keys = new HashSet<>();
                keys.add("author");
                keys.add("title");
                keys.add("institution");
                keys.add("year");

                minifyEntry(entry, keys);
            } else if ("INPROCEEDINGS".equalsIgnoreCase(entry.getType().toString())) {

                Set<String> keys = new HashSet<>();
                keys.add("author");
                keys.add("title");
                keys.add("booktitle");
                keys.add("year");

                minifyEntry(entry, keys);
            } else if ("ARTICLE".equalsIgnoreCase(entry.getType().toString())) {

                Set<String> keys = new HashSet<>();
                keys.add("author");
                keys.add("title");
                keys.add("journal");
                keys.add("year");

                minifyEntry(entry, keys);
            } else if ("BOOK".equalsIgnoreCase(entry.getType().toString())) {

                Set<String> keys = new HashSet<>();
                keys.add("author");
                keys.add("title");
                keys.add("editor");
                keys.add("publisher");
                keys.add("year");

                minifyEntry(entry, keys);
            } else if ("INCOLLECTION".equalsIgnoreCase(entry.getType().toString())) {

                Set<String> keys = new HashSet<>();
                keys.add("author");
                keys.add("title");
                keys.add("booktitle");
                keys.add("publisher");
                keys.add("year");

                minifyEntry(entry, keys);
            } else if ("MANUAL".equalsIgnoreCase(entry.getType().toString())) {

                Set<String> keys = new HashSet<>();
                keys.add("title");
                keys.add("author");
                keys.add("year");
                keys.add("note");

                minifyEntry(entry, keys);
            } else if ("PHDTHESIS".equalsIgnoreCase(entry.getType().toString())) {

                Set<String> keys = new HashSet<>();
                keys.add("title");
                keys.add("author");
                keys.add("year");
                keys.add("school");

                minifyEntry(entry, keys);
            } else if ("MISC".equalsIgnoreCase(entry.getType().toString())) {

                Set<String> keys = new HashSet<>();
                keys.add("title");
                keys.add("author");
                keys.add("howpublished");
                keys.add("note");

                minifyEntry(entry, keys);
            } else {
                System.out.println("\tNOT MINIFYING ENTRY TYPE " + entry.getType() + " as this type is unknown");
            }
        }
    }

    private void minifyEntry(BibTeXEntry entry, Set<String> requiredKeys) {
        Set<Key> keysToRemove = new HashSet<>();

        for (Key key : entry.getFields().keySet()) {
            if (!requiredKeys.contains(key.getValue().toLowerCase())) {
                keysToRemove.add(key);
            }
        }

        for (Key key : keysToRemove) {
            System.out.println("\tMinifying " + entry.getKey() + ": removing [" + key + "]");
            entry.removeField(key);
        }
    }

}
