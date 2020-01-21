package net.iGap.mobileBank.viewmoedel;

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.MobileBankRepository;
import net.iGap.mobileBank.repository.model.BankDateModel;
import net.iGap.mobileBank.repository.model.BankHistoryModel;
import net.iGap.mobileBank.repository.model.BaseMobileBankResponse;
import net.iGap.mobileBank.repository.util.JalaliCalendar;
import net.iGap.module.CalendarShamsi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MobileBankCardHistoryViewModel extends BaseMobileBankViewModel {

    private ObservableField<String> balance = new ObservableField<>("...");
    private ObservableField<String> income = new ObservableField<>("...");
    private ObservableField<String> withdraw = new ObservableField<>("...");
    private MutableLiveData<List<BankDateModel>> calender;
    private MutableLiveData<List<BankHistoryModel>> bills;

    private ObservableInt progressVisibility = new ObservableInt(View.INVISIBLE);
    private MutableLiveData<ErrorModel> errorM;

    private String depositNumber;
    private boolean isCard;
    private int datePosition = 2;
    private static final String TAG = "CardHistoryViewModel";

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
        getAccountDataForMonth(0);
    }

    /*private void getCurrentTime() {
        getCurrentTime2();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long monthDiff = null;
        Long dayDiff = null;
        try {
            Date startM = sdf.parse("2020-01-01 00:00:00");
            Date endM = sdf.parse("2020-01-30 00:00:00");
            monthDiff = endM.getTime() - startM.getTime();

            Date startD = sdf.parse("2020-01-01 00:00:00");
            Date endD = sdf.parse("2020-01-01 23:59:59");
            dayDiff = endD.getTime() - startD.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // get now time and set to 00:00:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 1);// for 6 hour
        calendar.set(Calendar.MINUTE, 0);// for 0 min
        calendar.set(Calendar.SECOND, 0);// for 0 sec
        long timeNow = calendar.getTime().getTime();
        String temp = HelperCalander.getPersianCalander(timeNow);
        String[] timeParts = temp.split("/");
        // reset to start of the month
        timeNow = timeNow - (Integer.parseInt(timeParts[2]))*dayDiff;
        temp = HelperCalander.getPersianCalander(timeNow);
        timeParts = temp.split("/");
        // add this month to array
        List<BankDateModel> data = new ArrayList<>();
        CalendarShamsi monthName = new CalendarShamsi(new Date(timeNow));
        data.add(new BankDateModel(monthName.strMonth, timeParts[1], timeParts[0], new Date(timeNow), new Date(timeNow + monthDiff), true));
        // add future;
        for (int i = 2; i >= 1; i--) {
            Long newMonth = timeNow + monthDiff * i;
            String temp1 = HelperCalander.getPersianCalander(newMonth);
            String[] timeParts1 = temp1.split("/");
            monthName = new CalendarShamsi(new Date(newMonth));
            data.add(2 - i, new BankDateModel(monthName.strMonth, timeParts1[1], timeParts1[0], new Date(newMonth), new Date(newMonth + monthDiff), false));
        }
        // add past
        for (int i = 1; i <= 12; i++) {
            Long newMonth = timeNow - monthDiff * i;
            String temp1 = HelperCalander.getPersianCalander(newMonth);
            String[] timeParts1 = temp1.split("/");
            monthName = new CalendarShamsi(new Date(newMonth));
            data.add(new BankDateModel(monthName.strMonth, timeParts1[1], timeParts1[0], new Date(newMonth), new Date(newMonth + monthDiff), false));
        }
        calender.postValue(data);
        getAccountDataForMonth(null);
    }*/

    public void getAccountDataForMonth(int offset) {
        Log.d(TAG, "getAccountDataForMonth: " + calender.getValue().get(datePosition).getStartMonth());
        Log.d(TAG, "getAccountDataForMonth: " + calender.getValue().get(datePosition).getEndMonth());
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
                DecimalFormat df = new DecimalFormat(",###");
                Log.d(TAG, "onSuccess: " + data.getData().get(data.getData().size() - 1).getBalance());
                balance.set(compatibleUnicode(df.format(Double.parseDouble(data.getData().get(data.getData().size() - 1).getBalance()))));
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFailed() {

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
}
