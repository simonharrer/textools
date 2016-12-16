package textools.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import textools.Command;
import textools.tasks.FileSystemTasks;

public class Cites implements Command {

    @Override
    public String getName() {
        return "cites";
    }

    @Override
    public String getDescription() {
        return "Print used cites";
    }

    @Override
    public void execute() {
        Map<String, Integer> citations = getCitations();

        // print result
        List<String> cites = new LinkedList<>();
        for (Map.Entry<String, Integer> citation : citations.entrySet()) {
            cites.add(citation.getKey() + " [" + citation.getValue() + "]");
        }
        cites.sort(Collator.getInstance());
        for (String cite : cites) {
            System.out.println(cite);
        }
        System.out.println("========");
        int total = 0;
        int count = 0;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (Integer integer : citations.values()) {
            total += integer;
            max = Math.max(max, integer);
            min = Math.min(min, integer);
            count++;
        }
        double avg = Math.round((double) total / count);

        System.out.println("Sum [" + total + "], Min [" + min + "], Max [" + max + "], Avg [" + avg + "]");
    }

    private Map<String, Integer> getCitations() {
        Map<String, Integer> citations = new HashMap<>();

        List<Path> texFiles = new FileSystemTasks().getFilesByExtension(".tex");
        for (Path texFile : texFiles) {
            try {
                for (Map.Entry<String, Integer> cite : determineCitations(texFile).entrySet()) {
                    if (citations.containsKey(cite.getKey())) {
                        citations.put(cite.getKey(), citations.get(cite.getKey()) + cite.getValue());
                    } else {
                        citations.put(cite.getKey(), cite.getValue());
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return citations;
    }

    private Map<String, Integer> determineCitations(Path texFile) {
        List<String> lines = readFile(texFile);

        Map<String, Integer> citations = new HashMap<>();

        for (int lineNumber = 1; lineNumber <= lines.size(); lineNumber++) {
            String line = lines.get(lineNumber - 1);

            // only validate if line is not commented out
            if (!line.startsWith("%")) {
                String regex = "\\\\cite\\{([^\\}]*)\\}";
                Matcher matcher = Pattern.compile(regex).matcher(line);
                while (matcher.find()) {
                    String match = matcher.group(1);
                    String[] matches = match.split(",");
                    for (String m : matches) {
                        if (citations.containsKey(m)) {
                            citations.put(m, citations.get(m) + 1);
                        } else {
                            citations.put(m, 1);
                        }
                    }
                }
            }
        }

        return citations;
    }

    private List<String> readFile(Path texFile) {
        try {
            return Files.readAllLines(texFile, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("could not read " + texFile + ": " + e.getMessage(), e);
        }
    }
}
