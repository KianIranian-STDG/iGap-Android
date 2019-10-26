package net.iGap.api.apiService;

import net.iGap.api.errorhandler.ErrorModel;

public interface ResponseCallback<T> {

    /**
     * @param data
     * @since 2.0.6
     */
    void onSuccess(T data);

    /**
     * @param t
     * @deprecated As of 10/06/2019, replaced by {@link #onSuccess(Object)}
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
     * @param error
     * @deprecated As of 10/06/2019, replaced by {@link #onError(String)}
     */
    void onError(ErrorModel error);

    /**
     * @param handShakeError
     * @deprecated As of 10/06/2019, replaced by {@link #onError(String)}
     */
    default void onFailed(boolean handShakeError) {

    }

    /**
     * @param visibility
     * @deprecated As of 10/06/2019, moved to BaseViewModel.class
     */
    default void setProgressIndicator(boolean visibility) {

    }

    /**
     * @apiNote will be called if we need handshake for lower APIs.
     * @since 2.0.6
     */
    default void onHandShake() {

    }
}
