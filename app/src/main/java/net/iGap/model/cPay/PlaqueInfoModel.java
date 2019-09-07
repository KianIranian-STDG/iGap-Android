package net.iGap.model.cPay;

import com.google.gson.annotations.SerializedName;

public class PlaqueInfoModel {

    @SerializedName("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public class Data {

        @SerializedName("first_name")
        private String firstName;

        @SerializedName("last_name")
        private String lastName;

        @SerializedName("national_id")
        private String nationalId;

        @SerializedName("plate")
        private String plaque;

        @SerializedName("mobile")
        private String mobile;

        @SerializedName("PAN")
        private String pan;

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getNationalId() {
            return nationalId;
        }

        public String getPlaque() {
            return plaque;
        }

        public String getMobile() {
            return mobile;
        }

        public String getPan() {
            return pan;
        }
    }
}
