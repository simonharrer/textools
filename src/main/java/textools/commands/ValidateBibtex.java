package textools.commands;

import org.jbibtex.*;
import textools.Command;
import textools.tasks.FileSystemTasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ValidateBibtex implements Command {

    @Override
    public String getName() {
        return "validate-bibtex";
    }

    @Override
    public String getDescription() {
        return "validates all .bib files for the existence of certain fields";
    }

    @Override
    public void execute() {
        List<Path> bibtexFiles = new FileSystemTasks().getFilesByExtension(".bib");

        for (Path bibtexFile : bibtexFiles) {
            try {
                BibTeXDatabase database = parseBibtexFile(bibtexFile);
                validate(database, bibtexFile);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void validate(BibTeXDatabase database, Path bibtexFile) {
        for (BibTeXEntry entry : database.getEntries().values()) {
            if ("ARTICLE".equals(entry.getType().toString())) {
                validateArticle(bibtexFile, entry);
            } else if ("TECHREPORT".equals(entry.getType().toString())) {
                validateTechreport(bibtexFile, entry);
            } else if ("MANUAL".equals(entry.getType().toString())) {
                validateManual(bibtexFile, entry);
            } else if ("INPROCEEDINGS".equals(entry.getType().toString())) {
                validateInproceedings(bibtexFile, entry);
            }
        }
    }

    private void validateInproceedings(Path bibtexFile, BibTeXEntry entry) {
        // TODO
        String[] keys = {"author", "title", "booktitle", "year", "pages"};
        for (String key : keys) {
            ensureKeyExistence(bibtexFile, entry, new Key(key));
        }
    }

    private void validateManual(Path bibtexFile, BibTeXEntry entry) {
        // TODO
        String[] keys = {"author", "title", "year", "month"};
        for (String key : keys) {
            ensureKeyExistence(bibtexFile, entry, new Key(key));
        }
    }

    private void validateTechreport(Path bibtexFile, BibTeXEntry entry) {
        String[] keys = {"author", "title", "year", "month", "institution", "number"};
        for (String key : keys) {
            ensureKeyExistence(bibtexFile, entry, new Key(key));
        }
    }

    private void validateArticle(Path bibtexFile, BibTeXEntry entry) {
        String[] keys = {"author", "title", "year", "month", "volume", "number", "pages"};
        for (String key : keys) {
            ensureKeyExistence(bibtexFile, entry, new Key(key));
        }
    }

    private void ensureKeyExistence(Path bibtexFile, BibTeXEntry entry, Key key) {
        if (!entry.getFields().containsKey(key) || entry.getFields().get(key).toString().trim().isEmpty()) {
            System.out.format("%s\t%s\t%s\t%s is missing%n", bibtexFile, entry.getKey(), entry.getType(), key);
        }
    }

    private BibTeXDatabase parseBibtexFile(Path bibtexFile) {
        try (BufferedReader reader = Files.newBufferedReader(bibtexFile, StandardCharsets.UTF_8)) {
            return new BibTeXParser().parse(reader);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + bibtexFile, e);
        } catch (ParseException e) {
            throw new IllegalStateException("could not parse bibtex file " + bibtexFile + "\n" + e.getMessage(), e);
        } catch (TokenMgrError e) {
            throw new IllegalStateException("bib tex file " + bibtexFile + " is not well formed. Reason: " + e.getMessage());
        }
    }
}
