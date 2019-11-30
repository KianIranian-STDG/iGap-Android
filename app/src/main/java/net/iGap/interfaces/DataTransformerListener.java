package net.iGap.interfaces;

public interface DataTransformerListener<T> {
    void transform(int id , T data);
}
