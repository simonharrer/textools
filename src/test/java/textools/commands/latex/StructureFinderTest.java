package textools.commands.latex;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class StructureFinderTest {

    @Test
    public void testFindStructures() throws Exception {
        assertEquals("[\\subsubsection{asdf}]", new StructureFinder().findStructures("\\subsubsection{asdf}").toString());
    }
}