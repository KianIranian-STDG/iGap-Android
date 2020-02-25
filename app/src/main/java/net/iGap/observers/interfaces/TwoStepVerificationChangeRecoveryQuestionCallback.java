package net.iGap.observers.interfaces;

public interface TwoStepVerificationChangeRecoveryQuestionCallback {
    void changeRecoveryQuestion();

    void errorChangeRecoveryQuestion(int major, int minor);
}
