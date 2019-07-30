package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;

import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.KuknosWHistoryM;
import net.iGap.kuknos.service.model.KuknosWalletBalanceInfoM;
import net.iGap.kuknos.service.model.KuknosWalletsAccountM;

import java.util.ArrayList;
import java.util.List;

public class KuknosWHistoryVM extends ViewModel {

    private MutableLiveData<List<KuknosWHistoryM>> listMutableLiveData;
    private MutableLiveData<ErrorM> errorM;
    private MutableLiveData<Boolean> progressState;

    public KuknosWHistoryVM() {
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
        KuknosWHistoryM temp = new KuknosWHistoryM("2019/05/12","10.0256","Buy House");
        List<KuknosWHistoryM> listTemp = new ArrayList<>();
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

    public MutableLiveData<List<KuknosWHistoryM>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public void setListMutableLiveData(MutableLiveData<List<KuknosWHistoryM>> listMutableLiveData) {
        this.listMutableLiveData = listMutableLiveData;
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
}
