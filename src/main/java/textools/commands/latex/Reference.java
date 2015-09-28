package textools.commands.latex;

import java.util.Objects;

public class Reference {

    private final String type;
    private final String target;

    public Reference(String type, String target) {
        this.type = Objects.requireNonNull(type);
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public String toString() {
        return String.format("\\%s{%s}", type, target);
    }
}
