package net.iGap.fragments.mplTranaction;

import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.observers.interfaces.OnMplTransaction;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestMplTransactionList;
import net.iGap.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class MplTransactionViewModel extends BaseViewModel implements OnMplTransaction {
    public static final int PAGINATION_LIMIT = 20;
    private List<ProtoGlobal.MplTransaction> tmp = new ArrayList<>();
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
        if (tmp != null) {
            tmp.addAll(mplTransactions);
        } else {
            tmp.addAll(mplTransactions);
        }
        mplTransactionLiveData.postValue(tmp);
        progressMutableLiveData.postValue(false);
    }

    @Override
    public void onError() {
        progressMutableLiveData.postValue(false);
    }


    public void getFirstPageMplTransactionList(ProtoGlobal.MplTransaction.Type type) {
        progressMutableLiveData.postValue(true);
        this.type = type;
        tmp.clear();
        new RequestMplTransactionList().mplTransactionList(type, 0, PAGINATION_LIMIT);
    }

    public ProtoGlobal.MplTransaction.Type getType() {
        return type;
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
