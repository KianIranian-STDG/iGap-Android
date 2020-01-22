package net.iGap.fragments.emoji.apiModels;

public class Issue {
    private String national_code;
    private String tel_num;
    private int count;

    public Issue(String national_code, String tel_num) {
        this.national_code = national_code;
        this.tel_num = tel_num;
        this.count = 1;
    }

    public String getNational_code() {
        return national_code;
    }

    public void setNational_code(String national_code) {
        this.national_code = national_code;
    }

    public String getTel_num() {
        return tel_num;
    }

    public void setTel_num(String tel_num) {
        this.tel_num = tel_num;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
