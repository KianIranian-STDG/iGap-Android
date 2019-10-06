package net.iGap.api.apiService;

/**
 * @deprecated
 * @param <T>
 */
public interface ApiResponse<T> {
    void onResponse(T t);

    void onFailed(String error);

    void setProgressIndicator(boolean visibility);
}
