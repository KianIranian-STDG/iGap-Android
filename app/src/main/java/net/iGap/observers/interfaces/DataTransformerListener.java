package net.iGap.observers.interfaces;

public interface DataTransformerListener<T> {
    void transform(int id , T data);
}
