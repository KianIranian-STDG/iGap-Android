package net.iGap.fragments.emoji.apiModels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ids {
    @SerializedName("ids")
    private List<String> ids = new ArrayList<>();

    public Ids(String... id) {
        ids.addAll(Arrays.asList(id));
    }

    public void setIds(List<String> ids) {
        this.ids.addAll(ids);
    }
}
