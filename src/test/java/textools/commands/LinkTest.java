package textools.commands;

import java.nio.file.Paths;

import org.junit.Test;
import textools.commands.latex.Link;

import static org.junit.Assert.assertEquals;

public class LinkTest {

    @Test
    public void validateUrl() throws Exception {
        Link link = new Link("http://esbperformance.org/", 3, Paths.get("asf"));
        assertEquals(200, link.getStatusCode());
    }

}
