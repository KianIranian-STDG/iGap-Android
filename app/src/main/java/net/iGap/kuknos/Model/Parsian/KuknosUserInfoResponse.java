package net.iGap.kuknos.Model.Parsian;

import com.google.gson.annotations.SerializedName;

public class KuknosUserInfoResponse {
    @SerializedName("national_code")
    private String nationalCode;
    @SerializedName("federation_name")
    private String federationName;
    @SerializedName("phone_number")
    private String status;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("mail")
    private String mail;
    @SerializedName("public_key")
    private String publicKey;
    @SerializedName("domain")
    private String domain;
    @SerializedName("iban")
    private String iban;

    public KuknosUserInfoResponse(String nationalCode, String federationName, String status, String firstName, String lastName, String mail, String publicKey, String domain, String iban) {
        this.nationalCode = nationalCode;
        this.federationName = federationName;
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.publicKey = publicKey;
        this.domain = domain;
        this.iban = iban;
    }

    public KuknosUserInfoResponse() {

    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getFederationName() {
        return federationName;
    }

    public void setFederationName(String federationName) {
        this.federationName = federationName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
}
