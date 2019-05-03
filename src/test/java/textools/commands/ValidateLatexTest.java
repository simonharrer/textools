package textools.commands;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidateLatexTest {

    @Test
    void testRules() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/errors.tex"), StandardCharsets.UTF_8);

        Map<Pattern, String> compiledRules = ValidateLatex.COMPILED_RULES;

        Set<String> matchedRules = new HashSet<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            List<String> violatedRules = new ArrayList<>();
            for (Map.Entry<Pattern, String> entry : compiledRules.entrySet()) {

                Matcher matcher = entry.getKey().matcher(line);
                while (matcher.find()) {
                    violatedRules.add(entry.getValue());
                    matchedRules.add(entry.getValue());
                }

            }

            final int lineNumber = i + 1;
            assertEquals(1, violatedRules.size(), "line #" + lineNumber + " violates rules " + violatedRules);
        }

        Set<String> untestedRules = new HashSet<>(compiledRules.values());
        untestedRules.removeAll(matchedRules);

        assertEquals(new HashSet<String>(), untestedRules);
    }
}
