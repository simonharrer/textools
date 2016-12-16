package textools.commands;

import java.util.Optional;

import org.junit.Test;
import textools.commands.acronym.Acronym;

import static org.junit.Assert.*;

public class AcronymTest {

    @Test
    public void find() throws Exception {
        assertEquals(Optional.of(new Acronym("API")),
                Acronym.find("    \\acro{API}{Application Programming Interface}  "));
    }

    @Test
    public void isInLine() throws Exception {
        assertTrue(new Acronym("API").isInLine("This is an API."));
        assertTrue(new Acronym("API").isInLine("This is an API!"));
        assertTrue(new Acronym("API").isInLine("This is an Application Programming Interface (API)"));
        assertTrue(new Acronym("API").isInLine("In this API,"));
        assertTrue(new Acronym("API").isInLine("In this API~2.0"));
        assertTrue(new Acronym("API").isInLine("API is important"));
    }

}
