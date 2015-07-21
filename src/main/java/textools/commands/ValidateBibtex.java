package textools.commands;

import org.jbibtex.*;
import textools.Command;
import textools.tasks.FileSystemTasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static Map<String, List<String>> getRequiredFieldsDatabase() {
        Map<String, List<String>> result = new HashMap<>();

        result.put("article", Arrays.asList("author", "title", "year", "month", "volume", "number", "pages"));
        result.put("techreport", Arrays.asList("author", "title", "year", "month", "institution", "number"));
        result.put("manual", Arrays.asList("author", "title", "year", "month"));
        result.put("inproceedings", Arrays.asList("author", "title", "booktitle", "year", "pages"));

        result.put("book", Arrays.asList("author", "title", "publisher", "year"));
        result.put("phdthesis", Arrays.asList("author", "title", "school", "year"));
        result.put("misc", Arrays.asList("author", "title", "howpublished", "year"));

        result.put("incollection", Arrays.asList("author", "title", "booktitle", "year", "pages", "publisher"));

        return result;
    }

    private void validate(BibTeXDatabase database, Path bibtexFile) {
        for (BibTeXEntry entry : database.getEntries().values()) {
            String type = entry.getType().toString().toLowerCase();

            detectRequiredAndMissingFields(bibtexFile, entry, type);
            detectProceedingsWithPages(bibtexFile, entry, type);
            detectAbbreviations(bibtexFile, entry, type);
        }
    }

    private void detectRequiredAndMissingFields(Path bibtexFile, BibTeXEntry entry, String type) {
        List<String> requiredFields = getRequiredFieldsDatabase().get(type);
        if (requiredFields == null) {
            printError(bibtexFile, entry, "no required fields available for this type");
            return;
        }

        for (String key : requiredFields) {
            ensureKeyExistence(bibtexFile, entry, new Key(key));
        }
    }

    private void detectProceedingsWithPages(Path bibtexFile, BibTeXEntry entry, String type) {
        if ("proceedings".equals(type) && entry.getField(BibTeXEntry.KEY_PAGES) != null) {
            printError(bibtexFile, entry, "proceedings with pages, maybe should be inproceedings?");
        }
    }

    private void detectAbbreviations(Path bibtexFile, BibTeXEntry entry, String type) {
        if ("article".equals(type) && entry.getField(BibTeXEntry.KEY_JOURNAL) != null && entry.getField(BibTeXEntry.KEY_JOURNAL).toString().contains(".")) {
            printError(bibtexFile, entry, "journal is abbreviated");
        }
    }

    private void ensureKeyExistence(Path bibtexFile, BibTeXEntry entry, Key key) {
        if (!entry.getFields().containsKey(key) || entry.getFields().get(key).toString().trim().isEmpty()) {
            String message = key + " is missing";
            printError(bibtexFile, entry, message);
        }
    }

    private void printError(Path bibtexFile, BibTeXEntry entry, String message) {
        System.out.format("%s\t%s\t%s\t%s%n", bibtexFile, entry.getKey(), entry.getType().toString().toUpperCase(), message);
    }

    private BibTeXDatabase parseBibtexFile(Path bibtexFile) {
        try (BufferedReader reader = Files.newBufferedReader(bibtexFile, StandardCharsets.UTF_8)) {
            return new BibTeXParser().parse(reader);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + bibtexFile, e);
        } catch (ParseException e) {
            throw new IllegalStateException("could not parse bibtex file " + bibtexFile + "\n" + e.getMessage(), e);
        } catch (TokenMgrException e) {
            throw new IllegalStateException("bib tex file " + bibtexFile + " is not well formed. Reason: " + e.getMessage());
        }
    }
}
