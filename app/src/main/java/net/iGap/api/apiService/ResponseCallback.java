package net.iGap.api.apiService;

import net.iGap.api.errorhandler.ErrorModel;

public interface ResponseCallback<T> {

    /**
     * @since 2.0.6
     * @param data
     */
    void onSuccess(T data);
    /**
     * @deprecated As of 10/06/2019, replaced by {@link #onSuccess(Object)}
     * @param t
     */
    default void onResponse(T t) {

    }

    /**
     * @param error
     * @since 2.0.6
     */
    default void onError(String error) {

    }

    /**
     * @deprecated As of 10/06/2019, replaced by {@link #onError(String)}
     * @param error
     */
    void onError(ErrorModel error);

    /**
     * @apiNote will be called if we need handshake for lower APIs.
     * @since 2.0.6
     */
    default void onHandShake() {

    }

    /**
     * @deprecated As of 10/06/2019, replaced by {@link #onError(String)}
     * @param handShakeError
     */
    default void onFailed(boolean handShakeError) {

    }

    /**
     * @deprecated As of 10/06/2019, moved to BaseViewModel.class
     * @param visibility
     */
    default void setProgressIndicator(boolean visibility) {

    }
}
