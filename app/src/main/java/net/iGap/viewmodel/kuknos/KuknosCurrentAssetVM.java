package net.iGap.viewmodel.kuknos;

import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.model.kuknos.KuknosError;
import net.iGap.model.kuknos.Parsian.KuknosAsset;
import net.iGap.model.kuknos.Parsian.KuknosBalance;
import net.iGap.model.kuknos.Parsian.KuknosResponseModel;
import net.iGap.model.kuknos.Parsian.KuknosTransactionResult;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.kuknos.PanelRepo;
import net.iGap.repository.kuknos.TradeRepo;

public class KuknosCurrentAssetVM extends BaseAPIViewModel {

    private MutableLiveData<KuknosAsset> assetPageMutableLiveData;
    private MutableLiveData<KuknosBalance> accountPageMutableLiveData;
    private TradeRepo tradeRepo = new TradeRepo();
    private PanelRepo panelRepo = new PanelRepo();

    private MutableLiveData<KuknosError> error;
    private MutableLiveData<Boolean> progressState;
    private MutableLiveData<Integer> openAddList;
    // 0 : is current  assets - 1 : is all active assets
    private int mode;

    public KuknosCurrentAssetVM() {
        assetPageMutableLiveData = new MutableLiveData<>();
        accountPageMutableLiveData = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        openAddList = new MutableLiveData<>();
    }

    public void getData() {
        if (mode == 0)
            getAccountDataFromServer();
        else
            getAssetDataFromServer();
    }

    private void getAccountDataFromServer() {
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
                error.setValue(new KuknosError(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                error.setValue(new KuknosError(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }
        });
    }

    private void getAssetDataFromServer() {
        progressState.setValue(true);
        tradeRepo.getAssets(this, new ResponseCallback<KuknosResponseModel<KuknosAsset>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosAsset> data) {
                assetPageMutableLiveData.setValue(data.getData());
                /*advAssetPageMutableLiveData.setValue(data.getData());*/
                progressState.setValue(false);
            }

            @Override
            public void onError(String errorM) {
                error.setValue(new KuknosError(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                error.setValue(new KuknosError(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

        });
    }

    public void addAsset(int position) {
        KuknosAsset.Asset temp = assetPageMutableLiveData.getValue().getAssets().get(position);
        if (temp.getAssetIssuer() == null || temp.getAssetCode() == null || temp.getAssetIssuer().length() == 0 || temp.getAssetCode().length() == 0) {
            error.setValue(new KuknosError(true, "Fail to get data", "0", R.string.kuknos_AddAsset_nullEntry));
            return;
        }
        progressState.setValue(false);
        tradeRepo.changeTrustline(temp.getAssetCode(), temp.getAssetIssuer(), (temp.getTrustLimit() == 0) ? null : ("" + temp.getTrustLimit()), this, new ResponseCallback<KuknosResponseModel<KuknosTransactionResult>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosTransactionResult> data) {
                error.setValue(new KuknosError(false, "", data.getMessage(), 0));
                progressState.setValue(false);
            }

            @Override
            public void onError(String errorM) {
                error.setValue(new KuknosError(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

            @Override
            public void onFailed() {
                error.setValue(new KuknosError(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
                progressState.setValue(false);
            }

        });
    }

    public String getRegulationsAddress(int position) {
        return assetPageMutableLiveData.getValue().getAssets().get(position).getRegulations();
    }

    public void onSubmit() {
        openAddList.setValue(1);
    }


    public MutableLiveData<KuknosError> getError() {
        return error;
    }

    public void setError(MutableLiveData<KuknosError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public MutableLiveData<KuknosAsset> getAssetPageMutableLiveData() {
        return assetPageMutableLiveData;
    }

    public MutableLiveData<KuknosBalance> getAccountPageMutableLiveData() {
        return accountPageMutableLiveData;
    }
}
