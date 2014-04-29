package textools.commands;

import org.jbibtex.*;
import textools.Command;
import textools.FileSystemTasks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class MinifyBibtexAuthors implements Command {

    @Override
    public String getName() {
        return "minify-bibtex-authors";
    }

    @Override
    public String getDescription() {
        return "replace additional authors with et al. in bibtex entries";
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

            String author = entry.getField(new Key("author")).toUserString();
            String abbreviatedAuthor = abbreviateAuthor(author);

            if (!author.equals(abbreviatedAuthor)) {
                //entry.removeField(new Key("author"));
                System.out.println("\tMinifying " + entry.getKey() + ": abbreviating author to " + abbreviatedAuthor);
                entry.addField(new Key("author"), new StringValue(abbreviatedAuthor, StringValue.Style.BRACED));
            }
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
