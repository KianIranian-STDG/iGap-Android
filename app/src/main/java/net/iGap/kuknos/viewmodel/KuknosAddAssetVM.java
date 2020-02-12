package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.Repository.TradeRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.Parsian.KuknosAsset;
import net.iGap.kuknos.service.model.Parsian.KuknosBalance;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.service.model.Parsian.KuknosTransactionResult;

public class KuknosAddAssetVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosAsset> assetPageMutableLiveData;
    private MutableLiveData<KuknosAsset> advAssetPageMutableLiveData;
    private MutableLiveData<KuknosBalance> accountPageMutableLiveData;
    private TradeRepo tradeRepo = new TradeRepo();
    private PanelRepo panelRepo = new PanelRepo();

    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<Boolean> progressStateAdv;
    private MutableLiveData<Integer> openAddList;

    public KuknosAddAssetVM() {
        assetPageMutableLiveData = new MutableLiveData<>();
        accountPageMutableLiveData = new MutableLiveData<>();
        advAssetPageMutableLiveData = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressStateAdv = new MutableLiveData<>();
        openAddList = new MutableLiveData<>();
    }

    public void getAccountDataFromServer() {
        progressState.setValue(true);
        panelRepo.getAccountInfo(this, new ResponseCallback<KuknosResponseModel<KuknosBalance>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosBalance> data) {
                //todo fix it here
                accountPageMutableLiveData.setValue(data.getData());
                progressState.setValue(false);
            }

            @Override
            public void onError(String errorM) {
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }
        });
    }

    public void getAssetDataFromServer() {
        progressStateAdv.setValue(true);
        tradeRepo.getAssets(this, new ResponseCallback<KuknosResponseModel<KuknosAsset>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosAsset> data) {
                assetPageMutableLiveData.setValue(data.getData());
                // TODO: 8/18/2019 change this part to get data from server
                advAssetPageMutableLiveData.setValue(data.getData());
                progressStateAdv.setValue(false);
            }

            @Override
            public void onError(String errorM) {
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressStateAdv.setValue(false);
            }

            @Override
            public void onFailed() {
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressStateAdv.setValue(false);
            }

        });
    }

    public void addAsset(int position) {
        KuknosAsset.Asset temp = assetPageMutableLiveData.getValue().getAssets().get(position);
        progressState.setValue(false);
        tradeRepo.changeTrustline(temp.getAssetCode(), temp.getAssetIssuer(), this, new ResponseCallback<KuknosResponseModel<KuknosTransactionResult>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosTransactionResult> data) {
                getAccountDataFromServer();
                progressState.setValue(false);
            }

            @Override
            public void onError(String errorM) {
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

        });
    }

    public void onSubmit() {
        openAddList.setValue(1);
    }

    public MutableLiveData<KuknosAsset> getAssetPageMutableLiveData() {
        return assetPageMutableLiveData;
    }

    public MutableLiveData<KuknosAsset> getAdvAssetPageMutableLiveData() {
        return advAssetPageMutableLiveData;
    }

    public MutableLiveData<ErrorM> getError() {
        return error;
    }

    public void setError(MutableLiveData<ErrorM> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<Integer> getOpenAddList() {
        return openAddList;
    }

    public MutableLiveData<KuknosBalance> getAccountPageMutableLiveData() {
        return accountPageMutableLiveData;
    }

    public MutableLiveData<Boolean> getProgressStateAdv() {
        return progressStateAdv;
    }

}
