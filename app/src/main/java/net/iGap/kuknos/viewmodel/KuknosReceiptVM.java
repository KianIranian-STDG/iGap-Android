package net.iGap.kuknos.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.Model.Parsian.KuknosUserRefundResponse;
import net.iGap.kuknos.Model.Parsian.Owners;
import net.iGap.kuknos.Repository.PanelRepo;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.realm.RealmKuknos;

public class KuknosReceiptVM extends BaseAPIViewModel {


    private MutableLiveData<KuknosUserRefundResponse> refundInfo = new MutableLiveData<>();
    private MutableLiveData<String> responseMessage = new MutableLiveData<>();
    private PanelRepo panelRepo = new PanelRepo();

    public KuknosReceiptVM() {

    }

    public void getUserRefundDetail(int refundNo){

        panelRepo.getUserRefundDetail(refundNo,this, new ResponseCallback<KuknosResponseModel<KuknosUserRefundResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosUserRefundResponse> data) {
                Log.e("vckijdshf", "onSuccess: " );
                refundInfo.setValue(data.getData());
                responseMessage.setValue("true");
            }

            @Override
            public void onError(String errorM) {
                Log.e("vckijdshf", "onError: " +errorM);
                refundInfo.setValue(null);
                responseMessage.setValue(errorM);
            }

            @Override
            public void onFailed() {
                Log.e("vckijdshf", "onFailed: " );
                refundInfo.setValue(null);
                responseMessage.setValue("onFailed");
            }
        });
    }

    public MutableLiveData<KuknosUserRefundResponse> getRefundInfo() {
        return refundInfo;
    }

    public void setRefundInfo(MutableLiveData<KuknosUserRefundResponse> refundInfo) {
        this.refundInfo = refundInfo;
    }

    public MutableLiveData<String> getResponseMessage() {
        return responseMessage;
    }

    public PanelRepo getPanelRepo() {
        return panelRepo;
    }

    public void setPanelRepo(PanelRepo panelRepo) {
        this.panelRepo = panelRepo;
    }

    public void setResponseMessage(MutableLiveData<String> responseMessage) {
        this.responseMessage = responseMessage;
    }
}
