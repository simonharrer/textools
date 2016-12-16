package textools.commands.acronym;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Acronym {

    private static final Pattern DEFINITION_DETECTION = Pattern.compile("\\\\acro\\{(?<acro>[a-zA-Z-]*)\\}");

    private final String name;
    private final Pattern usageDetectionPattern;

    public Acronym(String name) {
        this.name = name;
        this.usageDetectionPattern = Pattern.compile("(\\s|\\A|\\()" + name + "(\\s|\\.|!|\\z|\\)|,|~)");
    }

    public String getName() {
        return name;
    }

    public static Optional<Acronym> find(String line) {
        final Matcher matcher = DEFINITION_DETECTION.matcher(line);
        if (matcher.find()) {
            return Optional.of(new Acronym(matcher.group("acro")));
        } else {
            return Optional.empty();
        }
    }

    public boolean isInLine(String line) {
        return usageDetectionPattern.matcher(line).find();
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Acronym acronym = (Acronym) o;
        return Objects.equals(name, acronym.name);
    }

    @Override public int hashCode() {
        return Objects.hash(name);
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("Acronym{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
