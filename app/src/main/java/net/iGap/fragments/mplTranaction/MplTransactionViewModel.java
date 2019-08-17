package net.iGap.fragments.mplTranaction;

import android.arch.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.interfaces.OnMplTransaction;
import net.iGap.proto.ProtoGlobal;
import net.iGap.request.RequestMplTransactionList;
import net.iGap.viewmodel.BaseViewModel;

import java.util.List;

public class MplTransactionViewModel extends BaseViewModel implements OnMplTransaction {
    private MutableLiveData<List<ProtoGlobal.MplTransaction>> mplTransactionLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressMutableLiveData = new MutableLiveData<>();

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


    public void getAllMplTransactionList() {
        progressMutableLiveData.postValue(true);
        new RequestMplTransactionList().mplTransactionList(ProtoGlobal.MplTransaction.Type.NONE, 50, 0);
    }

    public void getBillMplTransactionList() {
        progressMutableLiveData.postValue(true);
        new RequestMplTransactionList().mplTransactionList(ProtoGlobal.MplTransaction.Type.BILL, 50, 0);
    }

    public void getTopUpMplTransactionList() {
        progressMutableLiveData.postValue(true);
        new RequestMplTransactionList().mplTransactionList(ProtoGlobal.MplTransaction.Type.TOPUP, 50, 0);
    }

    public void getSaleMplTransactionList() {
        progressMutableLiveData.postValue(true);
        new RequestMplTransactionList().mplTransactionList(ProtoGlobal.MplTransaction.Type.SALES, 50, 0);
    }

    public void getCardToCardMplTransactionList() {
        progressMutableLiveData.postValue(true);
        new RequestMplTransactionList().mplTransactionList(ProtoGlobal.MplTransaction.Type.CARD_TO_CARD, 50, 0);
    }

    public MutableLiveData<List<ProtoGlobal.MplTransaction>> getMplTransactionLiveData() {
        return mplTransactionLiveData;
    }

    public MutableLiveData<Boolean> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }
}
