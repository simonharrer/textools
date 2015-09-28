package textools.commands.latex;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Structure {

    private final String type;
    private final String content;
    private final Optional<String> label;

    public Structure(String type, String content) {
        this(type, content, Optional.empty());
    }

    public Structure(String type, String content, String label) {
        this(type, content, Optional.of(label));
    }

    private Structure(String type, String content, Optional<String> label) {
        this.type = Objects.requireNonNull(type);
        this.content = Objects.requireNonNull(content);
        this.label = Objects.requireNonNull(label);
    }

    public String getShortType() {
        return type.replaceAll("section", "sec")
                .replaceAll("paragraph", "para")
                .replaceAll("chapter", "cha")
                .replaceAll("image", "img")
                .replaceAll("table", "tab")
                .replaceAll("sub", "s");
    }

    public Reference getRef() {
        return new Reference("ref", getLabel());
    }

    public String getLabel() {
        return String.format("%s:%s", getShortType(), getAbbreviatedContent());
    }

    public String toLatex() {
        return String.format("\\%s{%s}\\label{%s}", type, content, getLabel());
    }

    public String getAbbreviatedContent() {
        String[] elements = content.split("\\s");
        List<String> words = new LinkedList<>();
        for(String word : elements) {
            words.add(word);
        }

        return words.stream()
                .filter(word -> !word.equalsIgnoreCase("the"))
                .filter(word -> !word.equalsIgnoreCase("a"))
                .filter(word -> !word.equalsIgnoreCase("is"))
                .filter(word -> !word.equalsIgnoreCase("on"))
                .filter(word -> !word.equalsIgnoreCase("this"))
                .filter(word -> !word.equalsIgnoreCase("these"))
                .filter(word -> !word.equalsIgnoreCase("those"))
                .collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return String.format("\\%s{%s}", type, content);
    }
}
