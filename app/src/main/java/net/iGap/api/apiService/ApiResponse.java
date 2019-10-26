package net.iGap.api.apiService;

/**
 * @param <T>
 * @deprecated
 */
public interface ApiResponse<T> {
    void onResponse(T t);

    void onFailed(String error);

    void setProgressIndicator(boolean visibility);
}
