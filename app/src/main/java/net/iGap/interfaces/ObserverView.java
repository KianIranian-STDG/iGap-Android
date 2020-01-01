package net.iGap.interfaces;

@FunctionalInterface
public interface ObserverView {
    default void subscribe() {
    }

    void unsubscribe();
}
