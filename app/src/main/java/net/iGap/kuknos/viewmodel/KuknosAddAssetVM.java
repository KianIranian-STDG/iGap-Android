package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.Repository.TradeRepo;
import net.iGap.kuknos.service.model.ErrorM;

import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.AssetResponse;
import org.stellar.sdk.responses.Page;
import org.stellar.sdk.responses.SubmitTransactionResponse;

public class KuknosAddAssetVM extends ViewModel {

    private MutableLiveData<Page<AssetResponse>> assetPageMutableLiveData;
    private MutableLiveData<Page<AssetResponse>> advAssetPageMutableLiveData;
    private MutableLiveData<AccountResponse> accountPageMutableLiveData;
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
        panelRepo.getAccountInfo(new ApiResponse<AccountResponse>() {
            @Override
            public void onResponse(AccountResponse accountResponse) {
                accountPageMutableLiveData.setValue(accountResponse);
            }

            @Override
            public void onFailed(String errorM) {
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });
    }

    public void getAssetDataFromServer() {
        tradeRepo.getAssets(new ApiResponse<Page<AssetResponse>>() {
            @Override
            public void onResponse(Page<AssetResponse> assetResponsePage) {
                assetPageMutableLiveData.setValue(assetResponsePage);
                // TODO: 8/18/2019 change this part to get data from server
                advAssetPageMutableLiveData.setValue(assetResponsePage);
            }

            @Override
            public void onFailed(String errorM) {
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressStateAdv.setValue(visibility);
            }
        });
    }

    public void addAsset(int position) {
        AssetResponse temp = assetPageMutableLiveData.getValue().getRecords().get(position);
        Log.d("amini", "addAsset: " + temp.getAssetCode() + " " + temp.getAssetIssuer());
        tradeRepo.changeTrustline(temp.getAssetCode(), temp.getAssetIssuer(), new ApiResponse<SubmitTransactionResponse>() {
            @Override
            public void onResponse(SubmitTransactionResponse submitTransactionResponse) {
                getAccountDataFromServer();
            }

            @Override
            public void onFailed(String errorM) {
                error.setValue(new ErrorM(true, "Fail to get data", "0", R.string.kuknos_send_errorServer));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });
    }

    public void onSubmit() {
        openAddList.setValue(1);
    }

    public MutableLiveData<Page<AssetResponse>> getAssetPageMutableLiveData() {
        return assetPageMutableLiveData;
    }

    public void setAssetPageMutableLiveData(MutableLiveData<Page<AssetResponse>> assetPageMutableLiveData) {
        this.assetPageMutableLiveData = assetPageMutableLiveData;
    }

    public MutableLiveData<Page<AssetResponse>> getAdvAssetPageMutableLiveData() {
        return advAssetPageMutableLiveData;
    }

    public void setAdvAssetPageMutableLiveData(MutableLiveData<Page<AssetResponse>> advAssetPageMutableLiveData) {
        this.advAssetPageMutableLiveData = advAssetPageMutableLiveData;
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

    public void setOpenAddList(MutableLiveData<Integer> openAddList) {
        this.openAddList = openAddList;
    }

    public MutableLiveData<AccountResponse> getAccountPageMutableLiveData() {
        return accountPageMutableLiveData;
    }

    public void setAccountPageMutableLiveData(MutableLiveData<AccountResponse> accountPageMutableLiveData) {
        this.accountPageMutableLiveData = accountPageMutableLiveData;
    }

    public MutableLiveData<Boolean> getProgressStateAdv() {
        return progressStateAdv;
    }

    public void setProgressStateAdv(MutableLiveData<Boolean> progressStateAdv) {
        this.progressStateAdv = progressStateAdv;
    }
}
