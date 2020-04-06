package net.iGap.observers.interfaces;

public interface TwoStepVerificationGetPasswordDetail {

    void getDetailPassword(String questionOne, String questionTwo, String hint, boolean hasConfirmedRecoveryEmail, String unconfirmedEmailPattern);

    void errorGetPasswordDetail(int majorCode, int minorCode);
}
