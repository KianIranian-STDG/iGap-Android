package net.iGap.observers.interfaces;

public interface GeneralResponseCallBack<T> {
    void onSuccess(T data);

    void onError(int major, int minor);
}
