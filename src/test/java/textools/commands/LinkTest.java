package textools.commands;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import textools.commands.latex.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkTest {

    @Test
    void validateUrl() throws Exception {
        Link link = new Link("http://esbperformance.org/", 3, Paths.get("asf"));
        assertEquals(200, link.getStatusCode());
    }

}
