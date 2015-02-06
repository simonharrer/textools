package textools.commands;

import org.junit.Assert;
import org.junit.Test;

public class MinifyBibtexAuthorsTests {

    @Test
    public void testMinifyAuthorNames() {
        Assert.assertEquals("Simon Harrer", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer"));
        Assert.assertEquals("Simon Harrer and others", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer and others"));
        Assert.assertEquals("Simon Harrer and Jörg Lenhard", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer and Jörg Lenhard"));
        Assert.assertEquals("Simon Harrer and others", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer and Jörg Lenhard and Guido Wirtz"));
        Assert.assertEquals("Simon Harrer and others", new MinifyBibtexAuthors().abbreviateAuthor("Simon Harrer and Jörg Lenhard and Guido Wirtz and others"));
    }

}
