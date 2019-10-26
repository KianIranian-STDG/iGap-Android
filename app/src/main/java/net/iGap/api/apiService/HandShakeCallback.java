package net.iGap.api.apiService;

public interface HandShakeCallback {

    /**
     * @apiNote will be called if we need handshake for lower APIs.
     * @since 2.0.6
     */
    default void onHandShake() {

    }

}
