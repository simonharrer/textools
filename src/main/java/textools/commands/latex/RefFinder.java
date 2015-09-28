package textools.commands.latex;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RefFinder {

    private static final String REF_REGEX = "\\\\(?<reftype>cref|ref)\\{(?<target>[^\\}]*)\\}";
    public static final Pattern REF_REGEX_PATTERN = Pattern.compile(REF_REGEX);

    public List<Reference> findRefs(String line) {
        List<Reference> result = new LinkedList<>();

        Matcher matcher = REF_REGEX_PATTERN.matcher(line);
        while(matcher.find()) {
            result.add(new Reference(matcher.group("reftype"), matcher.group("target")));
        }

        return result;
    }

}
