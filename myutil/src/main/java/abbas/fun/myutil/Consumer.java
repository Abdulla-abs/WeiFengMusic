package abbas.fun.myutil;

@FunctionalInterface
public interface Consumer<T,S> {

    void accept(T t, S s);

}
