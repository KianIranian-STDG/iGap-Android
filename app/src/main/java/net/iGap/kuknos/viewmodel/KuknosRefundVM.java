package net.iGap.kuknos.viewmodel;

import android.util.Log;

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

public class KuknosRefundVM extends BaseAPIViewModel {
    private static final String TAG = "KuknosRefundVM";
    private PanelRepo panelRepo = new PanelRepo();
    private MutableLiveData<KuknosRefundModel> refundData;
    private MutableLiveData<KuknosAsset> assetData;
    private MutableLiveData<KuknosBalance> balanceData;
    private MutableLiveData<Boolean> refundProgress;
    private String hashString;
    private KuknosSendM sendModel;

    public KuknosRefundVM() {
        refundData = new MutableLiveData<>();
        assetData = new MutableLiveData<>();
        balanceData = new MutableLiveData<>();
        sendModel = new KuknosSendM();
        refundProgress = new MutableLiveData<>();
    }

    public void getRefundInfoFromServer() {
        panelRepo.getRefundData("PMN", this, new ResponseCallback<KuknosResponseModel<KuknosRefundModel>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosRefundModel> data) {

                refundData.setValue(data.getData());
                Log.e(TAG, "onSuccess: " + data.getData().getRefundType() );

            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: " + error );
            }

            @Override
            public void onFailed() {
                Log.e(TAG, "onFailed: " );
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

        Log.e(TAG, "requestForFetchHash: "
                + panelRepo.getUserRepo().getSeedKey() + " ---"
                + assetData.getValue().getAssets().get(0).getAssetCode() + " ===== "
                + assetData.getValue().getAssets().get(0).getAssetIssuer() + " ------"
                + refundData.getValue().getPublicKey());

        panelRepo.paymentUser(sendModel, this, new ResponseCallback<KuknosResponseModel<KuknosHash>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosHash> data) {

                if (data != null) {
                    hashString = data.getData().getHash();
                    Log.e(TAG, "Hash String: " + hashString );

                    panelRepo.getUserInfoResponse(KuknosRefundVM.this , new ResponseCallback<KuknosResponseModel<KuknosUserInfoResponse>>() {
                        @Override
                        public void onSuccess(KuknosResponseModel<KuknosUserInfoResponse> data) {

                            panelRepo.getVirtualRefund(data.getData().getPublicKey(), assetData.getValue().getAssets().get(0).getAssetCode(), Float.parseFloat(assetCount), amount, fee, hashString, KuknosRefundVM.this, new ResponseCallback<KuknosResponseModel<KuknosVirtualRefund>>() {
                                @Override
                                public void onSuccess(KuknosResponseModel<KuknosVirtualRefund> data) {
                                    refundProgress.setValue(false);
                                    Log.e(TAG, "onSuccess: " + data.getMessage());
                                }

                                @Override
                                public void onError(String error) {
                                    refundProgress.setValue(false);
                                }

                                @Override
                                public void onFailed() {
                                    refundProgress.setValue(false);
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
                Log.e(TAG, "Hash onError: " + error );
            }

            @Override
            public void onFailed() {
                Log.e(TAG, "Hash onFailed: " );
            }
        });
    }

    public void getPMNAssetData() {
        panelRepo.getSpecificAssets("PMN", this, new ResponseCallback<KuknosResponseModel<KuknosAsset>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosAsset> data) {
                assetData.setValue(data.getData());
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: " + error );
            }

            @Override
            public void onFailed() {
                Log.e(TAG, "onFailed: " );
            }
        });
    }

    public void getAccountAssets() {
        panelRepo.getAccountInfo(this, new ResponseCallback<KuknosResponseModel<KuknosBalance>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosBalance> data) {
                balanceData.setValue(data.getData());
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: " + error );
            }

            @Override
            public void onFailed() {
                Log.e(TAG, "onFailed: " );
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

    public void setRefundProgress(MutableLiveData<Boolean> refundProgress) {
        this.refundProgress = refundProgress;
    }
}
