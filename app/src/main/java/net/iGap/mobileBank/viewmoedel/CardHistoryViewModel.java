package net.iGap.mobileBank.viewmoedel;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.model.BankCardModel;
import net.iGap.mobileBank.repository.model.BankDateModel;
import net.iGap.module.CalendarShamsi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CardHistoryViewModel extends BaseAPIViewModel {

    private ObservableField<String> balance = new ObservableField<>();
    private ObservableField<String> income = new ObservableField<>();
    private ObservableField<String> withdraw = new ObservableField<>();
    private MutableLiveData<List<BankDateModel>> calender;
    private MutableLiveData<BankCardModel> bills;

    private ObservableInt progressVisibility = new ObservableInt(View.VISIBLE);
    private MutableLiveData<ErrorModel> errorM;

    private static final String TAG = "CardHistoryViewModel";

    public CardHistoryViewModel() {
        calender = new MutableLiveData<>();
        bills = new MutableLiveData<>();
        errorM = new MutableLiveData<>();
    }

    public void init() {
        getCurrentTime();
    }

    private void getCurrentTime() {
        String time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Long monthDiff;
        try {
            Date startM = sdf.parse("2020-01-01 00:00:00");
            Date endM = sdf.parse("2020-02-01 00:00:00");
            monthDiff = endM.getTime() - startM.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Long timeNow = Calendar.getInstance().getTimeInMillis();
        String temp = HelperCalander.getPersianCalander(timeNow/DateUtils.SECOND_IN_MILLIS);
        Log.d(TAG, "getCurrentTime: " + temp);
    }

    private void getAccountDataForMonth(String monthAndYear) {
        // set bills
    }

    public ObservableField<String> getBalance() {
        return balance;
    }

    public ObservableField<String> getIncome() {
        return income;
    }

    public ObservableField<String> getWithdraw() {
        return withdraw;
    }

    public MutableLiveData<BankCardModel> getBills() {
        return bills;
    }

    public ObservableInt getProgressVisibility() {
        return progressVisibility;
    }

    public MutableLiveData<ErrorModel> getErrorM() {
        return errorM;
    }
}
