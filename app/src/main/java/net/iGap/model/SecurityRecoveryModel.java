package net.iGap.model;

import net.iGap.module.enums.Security;

public class SecurityRecoveryModel {

    private Security security;
    private String questionOne;
    private String questionTwo;
    private String emailPattern;
    private boolean isEmail;
    private boolean isConfirmEmail;

    public SecurityRecoveryModel(Security security, String questionOne, String questionTwo, String emailPattern, boolean isEmail, boolean isConfirmEmail) {
        this.security = security;
        this.questionOne = questionOne;
        this.questionTwo = questionTwo;
        this.emailPattern = emailPattern;
        this.isEmail = isEmail;
        this.isConfirmEmail = isConfirmEmail;
    }

    public Security getSecurity() {
        return security;
    }

    public String getQuestionOne() {
        return questionOne;
    }

    public String getQuestionTwo() {
        return questionTwo;
    }

    public String getEmailPattern() {
        return emailPattern;
    }

    public boolean isEmail() {
        return isEmail;
    }

    public boolean isConfirmEmail() {
        return isConfirmEmail;
    }
}
