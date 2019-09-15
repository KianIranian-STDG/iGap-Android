package net.iGap.kuknos.viewmodel;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosTradeHistoryM;

import java.util.ArrayList;
import java.util.List;

public class KuknosTradeActiveVM extends ViewModel {

    private MutableLiveData<List<KuknosTradeHistoryM>> listMutableLiveData;
    private MutableLiveData<ErrorM> errorM;
    private MutableLiveData<Boolean> progressState;

    public KuknosTradeActiveVM() {
        if (listMutableLiveData == null) {
            listMutableLiveData = new MutableLiveData<>();
        }
        if (errorM == null) {
            errorM = new MutableLiveData<>();
        }
        if (progressState == null) {
            progressState = new MutableLiveData<>();
            progressState.setValue(true);
        }
    }

    private void initModel() {
        KuknosTradeHistoryM temp = new KuknosTradeHistoryM("20.010", "10.020", "1.010", "2019/05/01");
        List<KuknosTradeHistoryM> listTemp = new ArrayList<>();
        listTemp.add(temp);
        listTemp.add(temp);
        listTemp.add(temp);
        listTemp.add(temp);
        listTemp.add(temp);
        listTemp.add(temp);
        listMutableLiveData.setValue(listTemp);
    }

    public void getDataFromServer() {
        progressState.setValue(true);
        // TODO Hard code in here baby
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // hard code
                initModel();
                progressState.setValue(false);
            }
        }, 1000);
    }

    public MutableLiveData<ErrorM> getErrorM() {
        return errorM;
    }

    public void setErrorM(MutableLiveData<ErrorM> errorM) {
        this.errorM = errorM;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<List<KuknosTradeHistoryM>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public void setListMutableLiveData(MutableLiveData<List<KuknosTradeHistoryM>> listMutableLiveData) {
        this.listMutableLiveData = listMutableLiveData;
    }
}
