package textools.commands.acronym;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Acronym {

    private static final Pattern DEFINITION_DETECTION = Pattern.compile("\\\\acro\\{(?<acro>[a-zA-Z-]*)\\}\\{(?<acroLong>[^\\}]*)\\}");

    private final String name;
    private final Pattern usageDetectionPattern;
    private final String acroLong;

    public Acronym(String name, String acroLong) {
        this.name = Objects.requireNonNull(name);
        this.usageDetectionPattern = Pattern.compile("(\\s|\\A|\\()" + name + "(\\s|\\.|!|\\z|\\)|,|~)");
        this.acroLong = Objects.requireNonNull(acroLong);
    }

    public String getName() {
        return name;
    }

    public static Optional<Acronym> find(String line) {
        final Matcher matcher = DEFINITION_DETECTION.matcher(line);
        if (matcher.find()) {
            return Optional.of(new Acronym(matcher.group("acro"), matcher.group("acroLong")));
        } else {
            return Optional.empty();
        }
    }

    public boolean isAbbreviationInLine(String line) {
        return usageDetectionPattern.matcher(line).find();
    }

    public boolean isLongInLine(String line) {
        return line.toLowerCase(Locale.ROOT).contains(acroLong.toLowerCase(Locale.ROOT));
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

    public String getLongName() {
        return acroLong;
    }
}
