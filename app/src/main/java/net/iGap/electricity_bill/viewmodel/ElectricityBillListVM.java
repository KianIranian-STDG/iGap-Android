package net.iGap.electricity_bill.viewmodel;

import android.view.View;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.electricity_bill.repository.model.Bill;
import net.iGap.helper.HelperNumerical;

import java.util.ArrayList;
import java.util.List;

public class ElectricityBillListVM extends BaseAPIViewModel {

    private MutableLiveData<List<Bill>>  mData;
    private ObservableField<Integer> progressVisibility;

    public ElectricityBillListVM() {

        mData = new MutableLiveData<>();
        progressVisibility = new ObservableField<>(View.GONE);

    }

    public void getData() {
        List<Bill> tempData = new ArrayList<>();
        tempData.add(new Bill("Home", "1234567890", "123456", "2000 $", "2019/8/10", false));
        tempData.add(new Bill("Home", "1234567890", "123456", "2000 $", "2019/8/10", true));
        tempData.add(new Bill("Home", "1234567890", "123456", "2000 $", "2019/8/10", true));
        tempData.add(new Bill("Home", "1234567890", "123456", "2000 $", "2019/8/10", false));
        mData.setValue(tempData);
    }

    public void deleteItem(int position) {
        List<Bill> tempData = mData.getValue();
        tempData.remove(position);
        mData.setValue(tempData);
    }

    public MutableLiveData<List<Bill>> getmData() {
        return mData;
    }

    public void setmData(MutableLiveData<List<Bill>> mData) {
        this.mData = mData;
    }

    public ObservableField<Integer> getProgressVisibility() {
        return progressVisibility;
    }

    public void setProgressVisibility(ObservableField<Integer> progressVisibility) {
        this.progressVisibility = progressVisibility;
    }
}
