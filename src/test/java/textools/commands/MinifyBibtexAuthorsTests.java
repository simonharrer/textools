package textools.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MinifyBibtexAuthorsTests {

    @Test
    void testMinifyAuthorNames() {
        assertEquals("Simon Harrer", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer"));
        assertEquals("Simon Harrer and others", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer and others"));
        assertEquals("Simon Harrer and Jörg Lenhard", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer and Jörg Lenhard"));
        assertEquals("Simon Harrer and others", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer and Jörg Lenhard and Guido Wirtz"));
        assertEquals("Simon Harrer and others", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer and Jörg Lenhard and Guido Wirtz and others"));
    }

}
