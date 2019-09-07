package net.iGap.viewmodel;

import net.iGap.module.structs.StructCountry;

import java.util.ArrayList;

public class EditUserProfileViewModel extends BaseViewModel {
    private ArrayList<StructCountry> structCountryArrayList = new ArrayList<>();



    public ArrayList<StructCountry> getStructCountryArrayList() {
        return structCountryArrayList;
    }

    public void setCountry(StructCountry country) {

    }

    public void uploadAvatar(String path) {

    }
}
