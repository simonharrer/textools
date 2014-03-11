package textools.commands;

import org.jbibtex.*;
import textools.Command;
import textools.FileSystemTasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sh on 11.03.14.
 */
public class MinifyBibtex implements Command {
    @Override
    public String getName() {
        return "minify-bibtex";
    }

    @Override
    public String getDescription() {
        return "abbreviates authors and removes optional keys in bibtex entries";
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

            if ("TECHREPORT".equals(entry.getType().toString())) {

                Set<String> techrepKeys = new HashSet<>();
                techrepKeys.add("author");
                techrepKeys.add("title");
                techrepKeys.add("institution");
                techrepKeys.add("year");

                minifyEntry(entry, techrepKeys);
            } else if ("INPROCEEDINGS".equals(entry.getType().toString())) {

                Set<String> inproceedingKeys = new HashSet<>();
                inproceedingKeys.add("author");
                inproceedingKeys.add("title");
                inproceedingKeys.add("booktitle");
                inproceedingKeys.add("year");

                minifyEntry(entry, inproceedingKeys);
            }

            String author = entry.getField(new Key("author")).toUserString();
            String abbreviatedAuthor = abbreviateAuthor(author);

            if (!author.equals(abbreviatedAuthor)) {
                //entry.removeField(new Key("author"));
                System.out.println("\tMinifying " + entry.getKey() + ": abbreviating author to " + abbreviatedAuthor);
                entry.addField(new Key("author"), new StringValue(abbreviatedAuthor, StringValue.Style.BRACED));
            }
        }
    }

    private void minifyEntry(BibTeXEntry entry, Set<String> requiredKeys) {
        Set<Key> keysToRemove = new HashSet<>();

        for (Key key : entry.getFields().keySet()) {
            if (!requiredKeys.contains(key.getValue())) {
                keysToRemove.add(key);
            }
        }

        for (Key key : keysToRemove) {
            System.out.println("\tMinifying " + entry.getKey() + ": removing [" + key + "]");
            entry.removeField(key);
        }
    }

    public String abbreviateAuthor(String author) {
        // single author
        String authorSeparator = " and ";

        if (!author.contains(authorSeparator)) {
            return author;
        }

        String[] authors = author.split(authorSeparator);

        // trim authors (remove or let it in? is some magic...)
        for (int i = 0; i < authors.length; i++) {
            authors[i] = authors[i].trim();
        }

        // already abbreviated
        if ("others".equals(authors[authors.length - 1]) && authors.length == 2) {
            return author;
        }

        // abbreviate
        return authors[0] + authorSeparator + "others";
    }
}
