package textools.commands.latex;

import org.junit.Test;

import static org.junit.Assert.*;

public class StructureTest {

    @Test
    public void testStructure() {
        Structure s = new Structure("subsubsection", "This is a Perfect Title", label);

        assertEquals("\\subsubsection{This is a Perfect Title}\\label{sssec:PerfectTitle}", s.toLatex());
        assertEquals("\\ref{sssec:PerfectTitle}", s.getRef());
    }

}