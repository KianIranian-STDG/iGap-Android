package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.kuknos.Model.KuknosSendM;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;
import net.iGap.kuknos.Model.Parsian.KuknosHash;
import net.iGap.kuknos.Model.Parsian.KuknosRefundModel;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.kuknos.Model.Parsian.KuknosUserInfoResponse;
import net.iGap.kuknos.Model.Parsian.KuknosVirtualRefund;
import net.iGap.kuknos.Repository.PanelRepo;
import net.iGap.observers.interfaces.ResponseCallback;

import java.util.concurrent.atomic.AtomicInteger;

public class KuknosRefundVM extends BaseAPIViewModel {
    private static final String TAG = "KuknosRefundVM";
    private PanelRepo panelRepo = new PanelRepo();
    private MutableLiveData<KuknosRefundModel> refundData;
    private MutableLiveData<KuknosAsset> assetData;
    private MutableLiveData<KuknosBalance> balanceData;
    private MutableLiveData<Boolean> refundProgress;
    private MutableLiveData<Boolean> isRefundSuccess;
    private String hashString;
    private KuknosSendM sendModel;
    private MutableLiveData<Integer> requestsSuccess;
    private MutableLiveData<Boolean> requestsError;
    private AtomicInteger success = new AtomicInteger(3);
    private String assetCode;

    public KuknosRefundVM() {
        refundData = new MutableLiveData<>();
        assetData = new MutableLiveData<>();
        balanceData = new MutableLiveData<>();
        sendModel = new KuknosSendM();
        refundProgress = new MutableLiveData<>();
        requestsSuccess = new MutableLiveData<>();
        requestsError = new MutableLiveData<>();
        isRefundSuccess = new MutableLiveData<>();

    }

    public void getRefundInfoFromServer() {
        panelRepo.getRefundData("PMN", this, new ResponseCallback<KuknosResponseModel<KuknosRefundModel>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosRefundModel> data) {

                refundData.setValue(data.getData());
                requestsSuccess.setValue(success.decrementAndGet());
            }

            @Override
            public void onError(String error) {
                requestsError.setValue(true);
            }

            @Override
            public void onFailed() {
                requestsError.setValue(true);
            }
        });
    }

    public void requestForVirtualRefund(String assetCount, int amount, float fee) {
        refundProgress.setValue(true);

        sendModel.setAmount(assetCount);
        sendModel.setSrc(panelRepo.getUserRepo().getSeedKey());
        sendModel.setAssetCode(assetData.getValue().getAssets().get(0).getAssetCode());
        sendModel.setAssetInssuer(assetData.getValue().getAssets().get(0).getAssetIssuer());
        sendModel.setMemo("TRANSFER");
        sendModel.setDest(refundData.getValue().getPublicKey());


        panelRepo.paymentUser(sendModel, this, new ResponseCallback<KuknosResponseModel<KuknosHash>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosHash> data) {

                if (data != null) {
                    hashString = data.getData().getHash();


                    panelRepo.getUserInfoResponse(KuknosRefundVM.this, new ResponseCallback<KuknosResponseModel<KuknosUserInfoResponse>>() {
                        @Override
                        public void onSuccess(KuknosResponseModel<KuknosUserInfoResponse> data) {

                            panelRepo.getVirtualRefund(data.getData().getPublicKey(), assetData.getValue().getAssets().get(0).getAssetCode(), Float.parseFloat(assetCount), amount, fee, hashString, KuknosRefundVM.this, new ResponseCallback<KuknosResponseModel<KuknosVirtualRefund>>() {
                                @Override
                                public void onSuccess(KuknosResponseModel<KuknosVirtualRefund> data) {
                                    refundProgress.setValue(false);
                                    isRefundSuccess.setValue(true);
                                }

                                @Override
                                public void onError(String error) {
                                    refundProgress.setValue(false);
                                    isRefundSuccess.setValue(false);
                                }

                                @Override
                                public void onFailed() {
                                    refundProgress.setValue(false);
                                    isRefundSuccess.setValue(false);
                                }
                            });

                        }

                        @Override
                        public void onError(String error) {

                        }

                        @Override
                        public void onFailed() {

                        }
                    });

                }
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void getPMNAssetData() {
        panelRepo.getSpecificAssets("PMN", this, new ResponseCallback<KuknosResponseModel<KuknosAsset>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosAsset> data) {
                assetData.setValue(data.getData());
                requestsSuccess.setValue(success.decrementAndGet());
            }

            @Override
            public void onError(String error) {
                requestsError.setValue(true);
            }

            @Override
            public void onFailed() {
                requestsError.setValue(true);
            }
        });
    }

    public void getAccountAssets() {
        panelRepo.getAccountInfo(this, new ResponseCallback<KuknosResponseModel<KuknosBalance>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosBalance> data) {
                balanceData.setValue(data.getData());
                requestsSuccess.setValue(success.decrementAndGet());
            }

            @Override
            public void onError(String error) {
                requestsError.setValue(true);
            }

            @Override
            public void onFailed() {
                requestsError.setValue(true);
            }
        });
    }

    public MutableLiveData<KuknosAsset> getAssetData() {
        return assetData;
    }

    public MutableLiveData<KuknosBalance> getBalanceData() {
        return balanceData;
    }

    public MutableLiveData<KuknosRefundModel> getRefundData() {
        return refundData;
    }

    public MutableLiveData<Boolean> getRefundProgress() {
        return refundProgress;
    }

    public MutableLiveData<Integer> getRequestsSuccess() {
        return requestsSuccess;
    }

    public MutableLiveData<Boolean> getRequestsError() {
        return requestsError;
    }

    public MutableLiveData<Boolean> getIsRefundSuccess() {
        return isRefundSuccess;
    }
}
