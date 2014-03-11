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
        assertEquals("Simon Harrer", new MinifyBibtex().abbreviateAuthor("Simon Harrer"));
        assertEquals("Simon Harrer and others", new MinifyBibtex().abbreviateAuthor("Simon Harrer and others"));
        assertEquals("Simon Harrer and others", new MinifyBibtex().abbreviateAuthor("Simon Harrer and Jörg Lenhard"));
        assertEquals("Simon Harrer and others", new MinifyBibtex().abbreviateAuthor("Simon Harrer and Jörg Lenhard and Guido Wirtz"));
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

        assertEquals(minifiedEntry, minifyDatabase(bibtexEntry));
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

        assertEquals(minifiedEntry, minifyDatabase(bibtexEntry));
    }

    public String minifyDatabase(String input) throws IOException, ParseException {
        BibTeXDatabase database = new BibTeXParser().parse(new StringReader(input));

        new MinifyBibtex().minifyDatabase(database);

        StringWriter writer = new StringWriter();
        BibTeXFormatter bibTeXFormatter = new BibTeXFormatter();
        bibTeXFormatter.setIndent("  ");
        bibTeXFormatter.format(database, writer);

        return writer.toString();
    }



}
