package net.iGap.fragments.mplTranaction;

import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.observers.interfaces.OnMplTransactionInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestMplTransactionInfo;
import net.iGap.viewmodel.BaseViewModel;

public class MplTransactionInfoViewModel extends BaseViewModel implements OnMplTransactionInfo {

    private MutableLiveData<ProtoGlobal.MplTransaction> transactionInfoLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> errorTransActionInfoLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressLiveData = new MutableLiveData<>();

    @Override
    public void onCreateViewModel() {
        super.onCreateViewModel();
        G.onMplTransactionInfo = this;
    }

    public void getTransactionInfo(String token) {
        progressLiveData.postValue(true);
        new RequestMplTransactionInfo().mplTransactionInfo(token);
    }

    @Override
    public void onMplTransAction(ProtoGlobal.MplTransaction transaction, int status) {
        if (status == 0) {
            transactionInfoLiveData.postValue(transaction);
        } else
            errorTransActionInfoLiveData.postValue(true);
        progressLiveData.postValue(false);
    }

    @Override
    public void onError() {
        progressLiveData.postValue(false);
    }

    public MutableLiveData<Boolean> getProgressLiveData() {
        return progressLiveData;
    }

    public MutableLiveData<ProtoGlobal.MplTransaction> getTransactionInfoLiveData() {
        return transactionInfoLiveData;
    }

    public MutableLiveData<Boolean> getErrorTransActionInfoLiveData() {
        return errorTransActionInfoLiveData;
    }
}
