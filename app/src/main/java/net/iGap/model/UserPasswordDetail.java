package net.iGap.model;

public class UserPasswordDetail {
    private String questionOne;
    private String questionTwo;
    private String hint;
    private boolean hasConfirmedRecoveryEmail;
    private String unconfirmedEmailPattern;

    public UserPasswordDetail(String questionOne, String questionTwo, String hint, boolean hasConfirmedRecoveryEmail, String unconfirmedEmailPattern) {
        this.questionOne = questionOne;
        this.questionTwo = questionTwo;
        this.hint = hint;
        this.hasConfirmedRecoveryEmail = hasConfirmedRecoveryEmail;
        this.unconfirmedEmailPattern = unconfirmedEmailPattern;
    }

    public String getQuestionOne() {
        return questionOne;
    }

    public String getQuestionTwo() {
        return questionTwo;
    }

    public String getHint() {
        return hint;
    }

    public boolean isHasConfirmedRecoveryEmail() {
        return hasConfirmedRecoveryEmail;
    }

    public String getUnconfirmedEmailPattern() {
        return unconfirmedEmailPattern;
    }
}
