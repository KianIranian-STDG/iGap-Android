package net.iGap.observers.interfaces;

public interface CheckPasswordCallback {
    void checkPassword();

    void errorCheckPassword(int major, int minor, int getWait);
}
