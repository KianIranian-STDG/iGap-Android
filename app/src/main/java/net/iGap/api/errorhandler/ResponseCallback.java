package net.iGap.api.errorhandler;

public interface ResponseCallback<T> {
    void onSuccess(T data);

    void onError(ErrorModel error);

    void onFailed(boolean handShakeError);
}
