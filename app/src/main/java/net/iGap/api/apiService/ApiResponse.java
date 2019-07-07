package net.iGap.api.apiService;

public interface ApiResponse<T> {
    void onResponse(T t);
    void onFailed(String error);
    void onStart();
    void onFinish();
}
