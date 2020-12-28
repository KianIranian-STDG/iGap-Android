
package net.iGap.model.igasht;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Language {

    @SerializedName("language_name")
    private String mLanguageName;

    public String getLanguageName() {
        return mLanguageName;
    }

    public void setLanguageName(String languageName) {
        mLanguageName = languageName;
    }

}
