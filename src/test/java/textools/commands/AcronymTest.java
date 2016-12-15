package textools.commands;

import java.util.Optional;

import org.junit.Test;

import static org.junit.Assert.*;

public class AcronymTest {

    @Test
    public void find() throws Exception {
        assertEquals(Optional.of(new FindAcronyms.Acronym("API")),
                FindAcronyms.Acronym.find("    \\acro{API}{Application Programming Interface}  "));
    }

    @Test
    public void isInLine() throws Exception {
        assertTrue(new FindAcronyms.Acronym("API").isInLine("This is an API."));
        assertTrue(new FindAcronyms.Acronym("API").isInLine("This is an API!"));
        assertTrue(new FindAcronyms.Acronym("API").isInLine("This is an Application Programming Interface (API)"));
        assertTrue(new FindAcronyms.Acronym("API").isInLine("In this API,"));
        assertTrue(new FindAcronyms.Acronym("API").isInLine("API is important"));
    }

}
