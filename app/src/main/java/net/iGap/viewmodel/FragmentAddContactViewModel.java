package net.iGap.viewmodel;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;

import net.iGap.module.CountryListComparator;
import net.iGap.module.structs.StructCountry;
import net.iGap.observers.interfaces.OnInfoCountryResponse;
import net.iGap.request.RequestInfoCountry;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class FragmentAddContactViewModel extends BaseViewModel {

    public ArrayList<StructCountry> structCountryList = new ArrayList<>();
    private ObservableInt showProgress = new ObservableInt(View.GONE);
    private MutableLiveData<String> countryCode = new MutableLiveData<>("+98");
    private MutableLiveData<String> phoneNumberMask = new MutableLiveData<>("###-###-####");
    private MutableLiveData<Boolean> hasError = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> codeCountryClick = new MutableLiveData<>(false);

    public FragmentAddContactViewModel(StringBuilder stringBuilder) {
        String list = stringBuilder.toString();
        String[] listArray = list.split("\\r?\\n");
        for (String s : listArray) {
            StructCountry structCountry = new StructCountry();

            String[] listItem = s.split(";");
            structCountry.setCountryCode(listItem[0]);
            structCountry.setAbbreviation(listItem[1]);
            structCountry.setName(listItem[2]);

            if (listItem.length > 3) {
                structCountry.setPhonePattern(listItem[3]);
            } else {
                structCountry.setPhonePattern(" ");
            }

            structCountryList.add(structCountry);
        }

        Collections.sort(structCountryList, new CountryListComparator());
    }

    public void setCountry(@NotNull StructCountry country) {
        showProgress.set(View.VISIBLE);
        new RequestInfoCountry().infoCountry(country.getAbbreviation(), new OnInfoCountryResponse() {
            @Override
            public void onInfoCountryResponse(final int callingCode, final String name, final String pattern, final String regexR) {
                G.runOnUiThread(() -> {
                    showProgress.set(View.GONE);
                    hasError.setValue(false);
                    countryCode.setValue("+" + callingCode);
                    if (pattern.equals("")) {
                        phoneNumberMask.setValue("##################");
                    } else {
                        phoneNumberMask.setValue(pattern.replace("X", "#").replace(" ", "-"));
                    }
                });

            }

            @Override
            public void onError(int majorCode, int minorCode) {
                G.handler.post(() -> {
                    showProgress.set(View.GONE);
                    hasError.setValue(true);
                });
            }
        });
    }

    public void onCountryCodeClick() {
        codeCountryClick.setValue(true);
    }

    public ObservableInt getShowProgress() {
        return showProgress;
    }

    public MutableLiveData<String> getCountryCode() {
        return countryCode;
    }

    public MutableLiveData<String> getPhoneNumberMask() {
        return phoneNumberMask;
    }

    public MutableLiveData<Boolean> getHasError() {
        return hasError;
    }

    public MutableLiveData<Boolean> getCodeCountryClick() {
        return codeCountryClick;
    }
}
