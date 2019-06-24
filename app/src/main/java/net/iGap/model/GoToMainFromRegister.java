package net.iGap.model;

public class GoToMainFromRegister {
    private boolean showDialogDisableTwoStepVerification;
    private long userId;

    public GoToMainFromRegister(boolean showDialogDisableTwoStepVerification, long userId) {
        this.showDialogDisableTwoStepVerification = showDialogDisableTwoStepVerification;
        this.userId = userId;
    }

    public boolean isShowDialogDisableTwoStepVerification() {
        return showDialogDisableTwoStepVerification;
    }

    public long getUserId() {
        return userId;
    }
}
