package net.iGap.observers.interfaces;

public interface RecoveryEmailCallback {

    void confirmEmail();

    void errorEmail(int major, int minor);
}
