package net.iGap.kuknos.service.model;

public class KuknosSignupM {

    private String username;
    private String name;
    private String family;
    private String email;
    private String pinCode;
    private String keyString;

    public KuknosSignupM() {
    }

    public KuknosSignupM(String username, String name, String family, String email, String pinCode, String keyString) {
        this.username = username;
        this.name = name;
        this.family = family;
        this.email = email;
        this.pinCode = pinCode;
        this.keyString = keyString;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getKeyString() {
        return keyString;
    }

    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }
}
