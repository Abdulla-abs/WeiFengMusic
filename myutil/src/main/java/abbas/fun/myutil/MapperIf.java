package abbas.fun.myutil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class MapperIf<T, S> {

    private T origin;
    private S transform;
    private final ConcurrentHashMap<S, Consumer<T, S>> actions = new ConcurrentHashMap<>();
    private Consumer<T, S> orElseFunc;

    private MapperIf() {

    }

    protected MapperIf(T data, S transform) {
        this.origin = data;
        this.transform = transform;
    }

    public MapperIf<T, S> mapper(Function<T, S> mapper) {
        return new MapperIf<>(origin, mapper.apply(origin));
    }

    public MapperIf<T, S> is(S target, Consumer<T, S> action) {
        if (target == null || action == null)
            throw new NullPointerException("target key and action can't be null");
        actions.put(target, action);
        return this;
    }

    public MapperIf<T, S> orElse(Consumer<T, S> elseAction) {
        if (elseAction == null) throw new NullPointerException("orElse Action can't be null");
        orElseFunc = elseAction;
        return this;
    }

    public If<S> toIf() {
        return If.of(transform);
    }

    public void end() {
        Consumer<T, S> action = actions.get(transform);
        if (action != null) {
            action.accept(origin, transform);
        } else {
            if (orElseFunc != null) {
                orElseFunc.accept(origin, transform);
            }
        }
    }
}
