package net.iGap.viewmodel;

import android.content.SharedPreferences;
import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.G;
import net.iGap.module.SHP_SETTING;

/**
 * Created by amir on 09/12/2017.
 */

public class FragmentDataViewModel extends ViewModel {

    private final int MILADI = 0;
    private final int SHAMSI = 1;
    private final int GHAMARY = 2;
    private SharedPreferences sharedPreferences;

    private ObservableInt isMiladi = new ObservableInt(View.GONE);
    private ObservableInt isShamsi = new ObservableInt(View.GONE);
    private ObservableInt isGhamari = new ObservableInt(View.GONE);
    private MutableLiveData<Boolean> dateChanged = new MutableLiveData<>();

    public FragmentDataViewModel(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        int typeData = sharedPreferences.getInt(SHP_SETTING.KEY_DATA, 1);

        isMiladi.set(typeData == MILADI ? View.VISIBLE : View.GONE);
        isShamsi.set(typeData == SHAMSI ? View.VISIBLE : View.GONE);
        isGhamari.set(typeData == GHAMARY ? View.VISIBLE : View.GONE);
    }

    public ObservableInt getIsMiladi() {
        return isMiladi;
    }

    public ObservableInt getIsShamsi() {
        return isShamsi;
    }

    public ObservableInt getIsGhamari() {
        return isGhamari;
    }

    public MutableLiveData<Boolean> getDateChanged() {
        return dateChanged;
    }

    public void onMiladiClick() {
        if (G.onDateChanged != null) {
            G.onDateChanged.onChange();
        }
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_DATA, MILADI).apply();
        dateChanged.setValue(true);
    }

    public void onShamsiClick() {
        if (G.onDateChanged != null) {
            G.onDateChanged.onChange();
        }
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_DATA, SHAMSI).apply();
        dateChanged.setValue(true);
    }

    public void onGhamariClick() {
        if (G.onDateChanged != null) {
            G.onDateChanged.onChange();
        }
        sharedPreferences.edit().putInt(SHP_SETTING.KEY_DATA, GHAMARY).apply();
        dateChanged.setValue(true);
    }
}
