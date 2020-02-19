package net.iGap.model.electricity_bill;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompanyList {

    @SerializedName("data")
    private List<Company> companiesList;

    public List<Company> getCompaniesList() {
        return companiesList;
    }

    public void setCompaniesList(List<Company> companiesList) {
        this.companiesList = companiesList;
    }

    public class Company {
        @SerializedName("_id")
        private String ID;
        @SerializedName("code")
        private int code;
        @SerializedName("title")
        private String title;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

}
