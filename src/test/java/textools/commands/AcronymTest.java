package textools.commands;

import java.util.Optional;

import org.junit.Test;
import textools.commands.acronym.Acronym;

import static org.junit.Assert.*;

public class AcronymTest {

    public static final Acronym API = new Acronym("API", "Application Programming Interface");

    @Test
    public void find() throws Exception {
        assertEquals(Optional.of(API),
                Acronym.find("    \\acro{API}{Application Programming Interface}  "));
    }

    @Test
    public void isInLine() throws Exception {
        assertTrue(API.isAbbreviationInLine("This is an API."));
        assertTrue(API.isAbbreviationInLine("This is an API!"));
        assertTrue(API.isAbbreviationInLine("This is an Application Programming Interface (API)"));
        assertTrue(API.isAbbreviationInLine("In this API,"));
        assertTrue(API.isAbbreviationInLine("In this API~2.0"));
        assertTrue(API.isAbbreviationInLine("API is important"));
    }

}
