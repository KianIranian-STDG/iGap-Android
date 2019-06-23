package net.iGap.interfaces;

public interface CheckPasswordCallback {
    void checkPassword();

    void errorCheckPassword(int major, int minor, int getWait);
}
