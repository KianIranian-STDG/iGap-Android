package net.iGap.fragments.mplTranaction;

import android.arch.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.interfaces.OnMplTransactionInfo;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestMplTransactionInfo;
import net.iGap.viewmodel.BaseViewModel;

public class MplTransactionInfoViewModel extends BaseViewModel implements OnMplTransactionInfo {

    private MutableLiveData<ProtoGlobal.MplTransaction> transactionInfoLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> errorTransActionInfoLiveData = new MutableLiveData<>();

    @Override
    public void onCreateViewModel() {
        super.onCreateViewModel();
        if (G.onMplTransactionInfo == null)
            G.onMplTransactionInfo = this;
    }

    public void getTransactionInfo(String token) {
        getProgressLiveData().postValue(true);
        new RequestMplTransactionInfo().mplTransactionInfo(token);
    }

    @Override
    public void onMplTransAction(ProtoGlobal.MplTransaction transaction, int status) {
        if (status == 0) {
            transactionInfoLiveData.postValue(transaction);
        } else
            errorTransActionInfoLiveData.postValue(true);
        getProgressLiveData().postValue(false);
    }

    @Override
    public void onError() {
        getProgressLiveData().postValue(false);
    }

    @Override
    public MutableLiveData<Boolean> getProgressLiveData() {
        return super.getProgressLiveData();
    }

    public MutableLiveData<ProtoGlobal.MplTransaction> getTransactionInfoLiveData() {
        return transactionInfoLiveData;
    }

    public MutableLiveData<Boolean> getErrorTransActionInfoLiveData() {
        return errorTransActionInfoLiveData;
    }
}
