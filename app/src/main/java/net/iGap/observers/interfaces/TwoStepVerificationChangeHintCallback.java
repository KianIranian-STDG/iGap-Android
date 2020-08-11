package net.iGap.observers.interfaces;

public interface TwoStepVerificationChangeHintCallback {
    void changeHint();

    void errorChangeHint(int major, int minor);
}
