package net.iGap.observers.interfaces;

public interface ResponseCallback<T> {

    /**
     * @param data
     * @since 2.0.6
     */
    void onSuccess(T data);

    /**
     * @param error
     * @since 2.0.6
     */
    void onError(String error);

    /**
     * @since 2.1.0
     */
    void onFailed();
}
