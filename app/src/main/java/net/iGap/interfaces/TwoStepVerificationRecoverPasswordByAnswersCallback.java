package net.iGap.interfaces;

public interface TwoStepVerificationRecoverPasswordByAnswersCallback {
    void recoveryByQuestion(String tokenR);

    void errorRecoveryByQuestion(int major,int minor);
}
