package net.iGap.fragments.mplTranaction;

import android.arch.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.interfaces.OnMplTransaction;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestMplTransactionList;
import net.iGap.viewmodel.BaseViewModel;

import java.util.List;

public class MplTransactionViewModel extends BaseViewModel implements OnMplTransaction {
    public static final int PAGINATION_LIMIT = 5;
    private MutableLiveData<List<ProtoGlobal.MplTransaction>> mplTransactionLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();
    private ProtoGlobal.MplTransaction.Type type;

    @Override
    public void onCreateViewModel() {
        super.onCreateViewModel();
        G.onMplTransaction = this;
    }

    @Override
    public void onMplTransAction(List<ProtoGlobal.MplTransaction> mplTransactions) {
        mplTransactionLiveData.postValue(mplTransactions);
        progressMutableLiveData.postValue(false);
    }

    @Override
    public void onError() {
        progressMutableLiveData.postValue(false);
    }


    public void getFirstPageMplTransactionList(ProtoGlobal.MplTransaction.Type type) {
        progressMutableLiveData.postValue(true);
        this.type = type;
        new RequestMplTransactionList().mplTransactionList(type, 0, PAGINATION_LIMIT);
    }

    public void getMorePageOffset(int start, int end) {
        progressMutableLiveData.postValue(true);
        new RequestMplTransactionList().mplTransactionList(type, start, end);
    }

    public MutableLiveData<List<ProtoGlobal.MplTransaction>> getMplTransactionLiveData() {
        return mplTransactionLiveData;
    }

    public MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

}
