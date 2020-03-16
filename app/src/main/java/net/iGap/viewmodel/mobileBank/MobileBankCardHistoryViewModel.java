package net.iGap.viewmodel.mobileBank;

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperCalander;
import net.iGap.repository.MobileBankRepository;
import net.iGap.model.mobileBank.BankDateModel;
import net.iGap.model.mobileBank.BankHistoryModel;
import net.iGap.model.mobileBank.BaseMobileBankResponse;
import net.iGap.module.mobileBank.JalaliCalendar;
import net.iGap.module.CalendarShamsi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MobileBankCardHistoryViewModel extends BaseMobileBankMainAndHistoryViewModel {

    private ObservableField<String> balance = new ObservableField<>("...");
    private ObservableField<String> income = new ObservableField<>("...");
    private ObservableField<String> withdraw = new ObservableField<>("...");
    private MutableLiveData<List<BankDateModel>> calender;
    private MutableLiveData<List<BankHistoryModel>> bills;

    private ObservableInt progressVisibility = new ObservableInt(View.INVISIBLE);
    private ObservableInt noItemVisibility = new ObservableInt(View.GONE);
    private MutableLiveData<ErrorModel> errorM;

    private String depositNumber;
    private boolean isCard;
    private int datePosition = 2;

    public MobileBankCardHistoryViewModel() {
        calender = new MutableLiveData<>();
        bills = new MutableLiveData<>();
        errorM = new MutableLiveData<>();
    }

    public void init() {
        getCurrentTime();
    }

    private void getCurrentTime() {
        // go to first of the month
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        JalaliCalendar.YearMonthDate tempYMD = JalaliCalendar.gregorianToJalali(new JalaliCalendar.YearMonthDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)));
        // find the Jalali date
        JalaliCalendar jalaliCalendar = new JalaliCalendar(tempYMD.getYear(), tempYMD.getMonth(), 1);
        // add this month to array
        List<BankDateModel> data = new ArrayList<>();
        CalendarShamsi monthName = new CalendarShamsi(jalaliCalendar.getTime());
        data.add(new BankDateModel(monthName.strMonth, jalaliCalendar, true, true));
        // add future;
        for (int i = 2; i >= 1; i--) {
            JalaliCalendar temp = new JalaliCalendar(tempYMD.getYear(), tempYMD.getMonth(), 1);
            temp.add(Calendar.MONTH, i);
            monthName = new CalendarShamsi(temp.getTime());
            data.add(2 - i, new BankDateModel(monthName.strMonth, temp, false, false));
        }
        // add past
        for (int i = 1; i <= 12; i++) {
            JalaliCalendar temp = new JalaliCalendar(tempYMD.getYear(), tempYMD.getMonth(), 1);
            temp.add(Calendar.MONTH, -i);
            monthName = new CalendarShamsi(temp.getTime());
            data.add(new BankDateModel(monthName.strMonth, temp, false, true));
        }
        calender.setValue(data);
    }

    public void getAccountDataForMonth(int offset) {
        noItemVisibility.set(View.GONE);
        if (offset == 0) {
            bills.setValue(null);
        }
        // set bills
        MobileBankRepository.getInstance().getAccountHistory(depositNumber, offset,
                calender.getValue().get(datePosition).getStartMonth(),
                calender.getValue().get(datePosition).getEndMonth(),
                this, new ResponseCallback<BaseMobileBankResponse<List<BankHistoryModel>>>() {
                    @Override
                    public void onSuccess(BaseMobileBankResponse<List<BankHistoryModel>> data) {
                        bills.setValue(data.getData());
                        if (offset == 0) {
                            if (data.getData() != null && data.getData().size() > 0) {
                                DecimalFormat df = new DecimalFormat(",###");
                                balance.set(compatibleUnicode(df.format(Double.parseDouble(data.getData().get(0).getBalance()))));
                            } else {
                                noItemVisibility.set(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (offset == 0) {
                            noItemVisibility.set(View.VISIBLE);
                        } else {
                            showRequestErrorMessage.setValue(error);
                        }
                    }

                    @Override
                    public void onFailed() {
                        if (offset == 0) {
                            noItemVisibility.set(View.VISIBLE);
                        } else {
                            showRequestErrorMessage.setValue("something went wrong.");
                        }
                    }
                });
    }

    private String compatibleUnicode(String entry) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
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

    public MutableLiveData<List<BankHistoryModel>> getBills() {
        return bills;
    }

    public ObservableInt getProgressVisibility() {
        return progressVisibility;
    }

    public MutableLiveData<ErrorModel> getErrorM() {
        return errorM;
    }

    public MutableLiveData<List<BankDateModel>> getCalender() {
        return calender;
    }

    public int getDatePosition() {
        return datePosition;
    }

    public void setDatePosition(int datePosition) {
        this.datePosition = datePosition;
    }

    public void setDepositNumber(String depositNumber) {
        this.depositNumber = depositNumber;
    }

    public void setCard(boolean card) {
        isCard = card;
    }

    public ObservableInt getNoItemVisibility() {
        return noItemVisibility;
    }
}
