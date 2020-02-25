package net.iGap.observers.interfaces;

public interface TwoStepVerificationRecoverPasswordByAnswersCallback {
    void recoveryByQuestion(String tokenR);

    void errorRecoveryByQuestion(int major, int minor);
}
