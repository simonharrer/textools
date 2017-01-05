package textools.commands;

import java.nio.file.Paths;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LinkTest {

    @Test
    public void validateUrl() throws Exception {
        ValidateLinks.Link link = new ValidateLinks.Link("http://esbperformance.org/", 3, Paths.get("asf"));
        assertEquals(200, link.getStatusCode());
    }

}
