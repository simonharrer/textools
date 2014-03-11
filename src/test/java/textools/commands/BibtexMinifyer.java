package textools.commands;

import org.jbibtex.*;
import org.junit.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class BibtexMinifyer {

    @Test
    public void testMinifyAuthorNames() {
        assertEquals("Simon Harrer", abbreviateAuthor("Simon Harrer"));
        assertEquals("Simon Harrer and others", abbreviateAuthor("Simon Harrer and others"));
        assertEquals("Simon Harrer and others", abbreviateAuthor("Simon Harrer and Jörg Lenhard"));
        assertEquals("Simon Harrer and others", abbreviateAuthor("Simon Harrer and Jörg Lenhard and Guido Wirtz"));
    }

    private String abbreviateAuthor(String author) {
        // single author
        String authorSeparator = " and ";

        if(!author.contains(authorSeparator)) {
            return author;
        }

        String[] authors = author.split(authorSeparator);

        // trim authors (remove or let it in? is some magic...)
        for(int i = 0; i < authors.length; i++){
            authors[i] = authors[i].trim();
        }

        // already abbreviated
        if("others".equals(authors[authors.length - 1]) && authors.length == 2) {
            return author;
        }

        // abbreviate
        return authors[0] + authorSeparator + "others";
    }

    @Test
    public void testMinifyInproceedings() throws IOException, ParseException {
        String bibtexEntry = "@INPROCEEDINGS{Harrer2012BPELConformancein,\n" +
                "  author = {Simon Harrer and Jörg Lenhard and Guido Wirtz},\n" +
                "  title = {{BPEL Conformance in Open Source Engines}},\n" +
                "  booktitle = {Proceedings of the 5th {IEEE} International Conference on Service-Oriented\n" +
                "    Computing and Applications {(SOCA'12)}, Taipei, Taiwan},\n" +
                "  year = {2012},\n" +
                "  pages = {1--8},\n" +
                "  month = {December 17-19},\n" +
                "  organization = {IEEE},\n" +
                "  file = {:Harrer2012BPELconformancein.pdf:PDF}\n" +
                "}";


        String minifiedEntry= "@INPROCEEDINGS{Harrer2012BPELConformancein,\n" +
                "  author = {Simon Harrer and others},\n" +
                "  title = {{BPEL Conformance in Open Source Engines}},\n" +
                "  booktitle = {Proceedings of the 5th {IEEE} International Conference on Service-Oriented\n" +
                "    Computing and Applications {(SOCA'12)}, Taipei, Taiwan},\n" +
                "  year = {2012}\n" +
                "}";

        assertEquals(minifiedEntry, minifyBibtexEntry(bibtexEntry));
    }

    @Test
    public void testMinifyTechrep() throws IOException, ParseException {
        String bibtexEntry = "@TECHREPORT{Harrer2012BetsyBPELEngine,\n" +
                "  author = {Harrer, Simon and Lenhard, Jörg},\n" +
                "  title = {{Betsy--A BPEL Engine Test System}},\n" +
                "  institution = {Otto-Friedrich Universität Bamberg},\n" +
                "  year = {2012},\n" +
                "  number = {90},\n" +
                "  month = {July},\n" +
                "  file = {:Harrer2012BetsyBPELEngine.pdf:PDF},\n" +
                "  journal = {Otto-Friedrich Universität Bamberg, Bamberger Beiträge zur WIAI},\n" +
                "  owner = {sh},\n" +
                "  timestamp = {2013.04.26},\n" +
                "  volume = {90}\n" +
                "}";

        String minifiedEntry = "@TECHREPORT{Harrer2012BetsyBPELEngine,\n" +
                "  author = {Harrer, Simon and others},\n" +
                "  title = {{Betsy--A BPEL Engine Test System}},\n" +
                "  institution = {Otto-Friedrich Universität Bamberg},\n" +
                "  year = {2012}\n" +
                "}";

        assertEquals(minifiedEntry, minifyBibtexEntry(bibtexEntry));
    }

    private String minifyBibtexEntry(String bibtexEntry) throws IOException, ParseException {

        BibTeXDatabase database = new BibTeXParser().parse(new StringReader(bibtexEntry));

        for(BibTeXEntry entry : database.getEntries().values()) {

            if ("TECHREPORT".equals(entry.getType().toString())) {

                Set<String> techrepKeys = new HashSet<>();
                techrepKeys.add("author");
                techrepKeys.add("title");
                techrepKeys.add("institution");
                techrepKeys.add("year");

                minify(entry, techrepKeys);
            } else if ("INPROCEEDINGS".equals(entry.getType().toString())) {

                Set<String> inproceedingKeys = new HashSet<>();
                inproceedingKeys.add("author");
                inproceedingKeys.add("title");
                inproceedingKeys.add("booktitle");
                inproceedingKeys.add("year");

                minify(entry, inproceedingKeys);
            }

            String author = entry.getField(new Key("author")).toUserString();
            String abbreviatedAuthor = abbreviateAuthor(author);

            if(!author.equals(abbreviatedAuthor)) {
                //entry.removeField(new Key("author"));
                entry.addField(new Key("author"), new StringValue(abbreviatedAuthor, StringValue.Style.BRACED));
            }
        }

        StringWriter writer = new StringWriter();
        BibTeXFormatter bibTeXFormatter = new BibTeXFormatter();
        bibTeXFormatter.setIndent("  ");
        bibTeXFormatter.format(database, writer);

        return writer.toString();
    }

    private void minify(BibTeXEntry entry, Set<String> requiredKeys) {
        Set<Key> keysToRemove = new HashSet<>();

        for(Key key : entry.getFields().keySet()) {
            if(!requiredKeys.contains(key.getValue())) {
                keysToRemove.add(key);
            }
        }

        for(Key key : keysToRemove) {
            entry.removeField(key);
        }
    }

}
