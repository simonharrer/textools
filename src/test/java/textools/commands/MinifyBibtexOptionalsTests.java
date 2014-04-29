package textools.commands;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.BibTeXParser;
import org.jbibtex.ParseException;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class MinifyBibtexOptionalsTests {

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
                "  author = {Simon Harrer and Jörg Lenhard and Guido Wirtz},\n" +
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
                "  author = {Harrer, Simon and Lenhard, Jörg},\n" +
                "  title = {{Betsy--A BPEL Engine Test System}},\n" +
                "  institution = {Otto-Friedrich Universität Bamberg},\n" +
                "  year = {2012}\n" +
                "}";

        assertEquals(minifiedEntry, minifyDatabase(bibtexEntry));
    }

    public String minifyDatabase(String input) throws IOException, ParseException {
        BibTeXDatabase database = new BibTeXParser().parse(new StringReader(input));

        new MinifyBibtexOptionals().minifyDatabase(database);

        StringWriter writer = new StringWriter();
        BibTeXFormatter bibTeXFormatter = new BibTeXFormatter();
        bibTeXFormatter.setIndent("  ");
        bibTeXFormatter.format(database, writer);

        return writer.toString();
    }

}
