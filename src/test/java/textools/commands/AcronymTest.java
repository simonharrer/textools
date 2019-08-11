package textools.commands;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import textools.commands.acronym.Acronym;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcronymTest {

    private static final Acronym API = new Acronym("API", "Application Programming Interface");

    @Test
    void find() {
        assertEquals(Optional.of(API),
                Acronym.find("    \\acro{API}{Application Programming Interface}  "));
    }

    @Test
    void isInLine() {
        assertTrue(API.isAbbreviationInLine("This is an API."));
        assertTrue(API.isAbbreviationInLine("This is an API!"));
        assertTrue(API.isAbbreviationInLine("This is an Application Programming Interface (API)"));
        assertTrue(API.isAbbreviationInLine("In this API,"));
        assertTrue(API.isAbbreviationInLine("In this API~2.0"));
        assertTrue(API.isAbbreviationInLine("API is important"));
    }

}
