package abbas.fun.myutil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class If<T> {

    protected T data;
    private final ConcurrentHashMap<T, Consumer<T>> actions = new ConcurrentHashMap<>();
    private Consumer<T> orElseFunc;

    private If() {

    }

    protected If(T data) {
        this.data = data;
    }


    public static <T> If<T> of(T data) {
        return new If<>(data);
    }


    public <S> MapperIf<T, S> mapper(Function<T, S> mapper) {
        return new MapperIf<>(data, mapper.apply(data));
    }

    public If<T> is(T target, Consumer<T> action) {
        if (target == null || action == null)
            throw new NullPointerException("target key and action can't be null");
        actions.put(target, action);
        return this;
    }

    public If<T> orElse(Consumer<T> elseAction) {
        if (elseAction == null) throw new NullPointerException("orElse Action can't be null");
        orElseFunc = elseAction;
        return this;
    }

    public void end() {
        Consumer<T> action = actions.get(data);
        if (action != null) {
            action.accept(data);
        } else {
            if (orElseFunc != null){
                orElseFunc.accept(data);
            }
        }
    }


}
