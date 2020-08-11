package net.iGap.observers.interfaces;

public interface TwoStepVerificationVerifyPassword {
    void verifyPassword(String tokenR);

    void errorVerifyPassword(int major, int minor, int wait);
}
