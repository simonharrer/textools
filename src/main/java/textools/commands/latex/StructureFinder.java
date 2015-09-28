package textools.commands.latex;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StructureFinder {

    private static final String STRUCTURE_REGEX = "\\\\(?<type>section|subsection|subsubsection|chapter|part)\\{(?<structure>[^\\}]*)\\}";
    private static final String LABEL_REGEX = "\\\\label\\{(?<label>[^\\}]*)\\}";

    private static final String STRUCTURE_FINDER = STRUCTURE_REGEX + "(" + LABEL_REGEX + ")?";
    private static final Pattern STRUCTURE_FINDER_PATTERN = Pattern.compile(STRUCTURE_FINDER);


    public List<Structure> findStructures(String line) {
        List<Structure> result = new LinkedList<>();

        Matcher matcher = STRUCTURE_FINDER_PATTERN.matcher(line);
        while(matcher.find()) {
            result.add(new Structure(matcher.group("type"), matcher.group("structure"), label));
        }

        return result;
    }

}
