package net.iGap.observers.interfaces;

@FunctionalInterface
public interface ObserverView {
    default void subscribe() {
    }

    void unsubscribe();
}
