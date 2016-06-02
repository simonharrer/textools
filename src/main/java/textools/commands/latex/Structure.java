package textools.commands.latex;

import java.util.*;
import java.util.stream.Collectors;

public class Structure {

    private static final Set<String> SMALLER_WORDS;

    static {
        Set<String> smallerWords = new HashSet<>();

        // Articles
        smallerWords.addAll(Arrays.asList("a", "an", "the"));
        // Prepositions
        smallerWords.addAll(Arrays.asList("above", "about", "across", "against", "along", "among", "around", "at", "before", "behind", "below", "beneath", "beside", "between", "beyond", "by", "down", "during", "except", "for", "from", "in", "inside", "into", "like", "near", "of", "off", "on", "onto", "since", "to", "toward", "through", "under", "until", "up", "upon", "with", "within", "without"));
        // Conjunctions
        smallerWords.addAll(Arrays.asList("and", "but", "for", "nor", "or", "so", "yet"));

        // unmodifiable for thread safety
        SMALLER_WORDS = Collections.unmodifiableSet(smallerWords);
    }

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
                .replaceAll("chapter", "chap")
                .replaceAll("image", "img")
                .replaceAll("table", "tab")
                ;
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
        String[] elements = content.replaceAll("([:;_,.?!]|--)", "").split("\\s");
        List<String> words = new LinkedList<>();
        for(String word : elements) {
            words.add(word);
        }

        return words.stream()
                .filter(word -> !SMALLER_WORDS.contains(word.toLowerCase()))
                .collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return String.format("\\%s{%s}", type, content);
    }
}
