package net.iGap.observers.interfaces;

public interface TwoStepVerificationRecoverPasswordByToken {
    void recoveryByEmail(String tokenR);

    void errorRecoveryByEmail(int major, int minor);
}
