package net.iGap.interfaces;

public interface TwoStepVerificationChangeRecoveryQuestionCallback {
    void changeRecoveryQuestion();

    void errorChangeRecoveryQuestion(int major, int minor);
}
