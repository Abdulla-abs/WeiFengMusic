package abbas.fun.myutil;

import java.util.HashMap;
import java.util.Optional;

public class Witch<O, S> {

    private final O origin;
    private S def;
    private final HashMap<O, S> map = new HashMap<>(2);

    private Witch(O origin) {
        this.origin = origin;
    }


    public static <O, S> Witch<O, S> of(O origin) {
        return new Witch<>(origin);
    }

    public Witch<O, S> with(O witch, S sendBack) {
        map.put(witch, sendBack);
        return this;
    }

    public Witch<O, S> withDefault(S sendBack) {
        def = sendBack;
        return this;
    }

    public S witchOne() {
        S s = map.get(origin);
        if (s == null) {
            return def;
        }
        return s;
    }

    public Optional<S> optionalWitch() {
        return Optional.ofNullable(map.get(origin));
    }
}
