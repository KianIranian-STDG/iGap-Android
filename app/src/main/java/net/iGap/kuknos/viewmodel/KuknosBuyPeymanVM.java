package net.iGap.kuknos.viewmodel;

import androidx.core.text.HtmlCompat;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.Model.KuknosError;
import net.iGap.kuknos.Model.KuknosPaymentResponse;
import net.iGap.kuknos.Model.Parsian.KuknosAsset;
import net.iGap.kuknos.Model.Parsian.KuknosBalance;
import net.iGap.kuknos.Model.Parsian.KuknosBankPayment;
import net.iGap.kuknos.Model.Parsian.KuknosResponseModel;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.kuknos.Repository.PanelRepo;
import net.iGap.request.RequestInfoPage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class KuknosBuyPeymanVM extends BaseAPIViewModel {

    private ObservableField<String> amount = new ObservableField<>();
    private ObservableField<Boolean> amountEnable = new ObservableField<>(false);
    private ObservableField<String> sum = new ObservableField<>();
    private ObservableField<String> assetPrice = new ObservableField<>("قیمت هر توکن: ...");
    private MutableLiveData<KuknosError> error;
    private MutableLiveData<KuknosPaymentResponse> paymentData = new MutableLiveData<>(null);
    // 0 : nothing 1: connecting to server 2: connecting to bank
    private MutableLiveData<Integer> progressState;
    private MutableLiveData<Boolean> sumState;
    private MutableLiveData<String> goToPaymentPage;
    //go to bank
    private MutableLiveData<Boolean> nextPage;
    private SingleLiveEvent<Boolean> goToPin = new SingleLiveEvent<>();
    //    private int PMNprice = -1;
//    private double maxAmount = 1000000;
    Double sumTemp;
    private PanelRepo panelRepo = new PanelRepo();
    private MutableLiveData<String> TandCAgree;
    private boolean termsAndConditionIsChecked = false;
    private KuknosAsset.Asset requestedAsset = null;
    private KuknosBalance.Balance currentAssetInfo;

    public KuknosBuyPeymanVM() {
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(0);
        sumState = new MutableLiveData<>(false);
        nextPage = new MutableLiveData<>(false);
        goToPaymentPage = new MutableLiveData<>();
        TandCAgree = new MutableLiveData<>(null);
    }

    public void onSubmitBtn() {
        if (checkForm()) {
            return;
        }
        sendDataServer();
    }

    public boolean updateSum() {
        if (checkEntry()) {
            return false;
        }
        if (requestedAsset == null)
            return false;
        if (Double.parseDouble(amount.get()) > requestedAsset.getRemainAmount()
                || Double.parseDouble(amount.get()) > requestedAsset.getiGapTransferLimit()
                || Double.parseDouble(amount.get()) > requestedAsset.getSaleMax()) {
            error.setValue(new KuknosError(true, "", "1", R.string.kuknos_buyP_MaxAmount));
            return false;
        }
//        if (requestedAsset.getBuyRate() == -1)
//            return false;
        sumTemp = Double.parseDouble(amount.get()) * requestedAsset.getBuyRate();
        BigDecimal tmp = new BigDecimal(sumTemp).setScale(0, RoundingMode.UP);
        sumTemp = Double.parseDouble(tmp.toString());
        DecimalFormat df = new DecimalFormat(",###");
        sum.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(sumTemp)) : df.format(sumTemp));
        return true;
    }

    public void getAssetValue() {
        progressState.setValue(3);
        panelRepo.getSpecificAssets(getCurrentAssetType(), this, new ResponseCallback<KuknosResponseModel<KuknosAsset>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosAsset> data) {
                requestedAsset = data.getData().getAssets().get(0);
//                PMNprice = data.getData().getAssets().get(0).getBuyRate();
//                maxAmount = data.getData().getAssets().get(0).getRemainAmount();
                assetPrice.set("قیمت " + getCurrentAssetType() + " لحظه ای: " + CompatibleUnicode(decimalFormatter(Double.parseDouble("" + requestedAsset.getBuyRate()))) + " ریال");
                progressState.setValue(0);
                amountEnable.set(true);
//                getFees();
            }

            @Override
            public void onError(String errorM) {
                progressState.setValue(0);
                error.setValue(new KuknosError(true, "", "2", R.string.kuknos_buy_noPrice));
            }

            @Override
            public void onFailed() {
                progressState.setValue(0);
                error.setValue(new KuknosError(true, "", "2", R.string.kuknos_buy_noPrice));
            }
        });
    }

    private String decimalFormatter(Double entry) {
        DecimalFormat df = new DecimalFormat(",###");
        return df.format(entry);
    }

    private String CompatibleUnicode(String entry) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
    }

    public void sendDataServer() {
        progressState.setValue(1);
        panelRepo.buyAsset(getCurrentAssetType(), amount.get(), "" + (int) Math.round(sumTemp),
                "", this, new ResponseCallback<KuknosResponseModel<KuknosBankPayment>>() {
                    @Override
                    public void onSuccess(KuknosResponseModel<KuknosBankPayment> data) {
                        goToPaymentPage.setValue(data.getData().getToken());
                        progressState.setValue(0);
                        amountEnable.set(true);
                    }

                    @Override
                    public void onError(String errorM) {
                        progressState.setValue(0);
                        amountEnable.set(true);
                        error.setValue(new KuknosError(true, "wrong pin", errorM, R.string.kuknos_buyP_failS));
                    }

                    @Override
                    public void onFailed() {
                        progressState.setValue(0);
                        amountEnable.set(true);
                        error.setValue(new KuknosError(true, "wrong pin", "1", R.string.kuknos_buyP_failS));
                    }

                });
    }

    public void getPaymentData(String RRN) {
        progressState.setValue(3);
        panelRepo.getPaymentData(RRN, this, new ResponseCallback<KuknosResponseModel<KuknosPaymentResponse>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosPaymentResponse> data) {
                paymentData.setValue(data.getData());
                progressState.setValue(0);
            }

            @Override
            public void onError(String error) {
                progressState.setValue(0);
            }

            @Override
            public void onFailed() {
                progressState.setValue(0);
            }
        });
    }

    private boolean checkForm() {
        if (amount.get() == null) {
            // empty
            error.setValue(new KuknosError(true, "empty amount", "0", R.string.kuknos_buyP_emptyAmount));
            return true;
        }
        if (amount.get().isEmpty()) {
            // empty
            error.setValue(new KuknosError(true, "empty amount", "0", R.string.kuknos_buyP_emptyAmount));
            return true;
        }
        if (Double.parseDouble(amount.get()) == 0) {
            error.setValue(new KuknosError(true, "zero fail", "0", R.string.kuknos_buyP_zeroAmount));
            return true;
        }
        if (Double.parseDouble(amount.get()) < requestedAsset.getSaleMin()) {
            error.setValue(new KuknosError(true, "", "1", R.string.kuknos_buyP_MinAmount));
            return true;
        }
        //Terms and Condition
        if (!termsAndConditionIsChecked) {
            error.setValue(new KuknosError(true, "TermsAndConditionError", "1", R.string.kuknos_SignupInfo_errorTermAndCondition));
            return true;
        }
        return false;
    }

    private boolean checkEntry() {
        if (amount.get() == null) {
            // empty
            error.setValue(new KuknosError(true, "empty amount", "0", R.string.kuknos_buyP_emptyAmount));
            return true;
        }
        if (amount.get().isEmpty()) {
            // empty
            error.setValue(new KuknosError(true, "empty amount", "0", R.string.kuknos_buyP_emptyAmount));
            return true;
        }
        if (Double.parseDouble(amount.get()) == 0) {
            error.setValue(new KuknosError(true, "zero fail", "0", R.string.kuknos_buyP_zeroAmount));
            return true;
        }
        return false;
    }

    public void getTermsAndCond() {
        if (TandCAgree.getValue() != null && !TandCAgree.getValue().equals("error")) {
            TandCAgree.postValue(TandCAgree.getValue());
            return;
        }
        if (!G.isSecure) {
            TandCAgree.postValue("error");
            return;
        }
        new RequestInfoPage().infoPageAgreementDiscovery("KUKNUS_BUY_AGREEMENT", new RequestInfoPage.OnInfoPage() {
            @Override
            public void onInfo(String body) {
                if (body != null) {
                    TandCAgree.postValue(HtmlCompat.fromHtml(body, HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                } else
                    TandCAgree.postValue("error");
            }

            @Override
            public void onError(int major, int minor) {
                TandCAgree.postValue("error");
            }
        });
    }

    public String getRegulationsAddress() {
        if (requestedAsset == null)
            return "";
        return requestedAsset.getRegulations();
    }

    public void termsOnCheckChange(boolean isChecked) {
        termsAndConditionIsChecked = isChecked;
    }

    public MutableLiveData<KuknosError> getError() {
        return error;
    }

    public void setError(MutableLiveData<KuknosError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getNextPage() {
        return nextPage;
    }

    public void setNextPage(MutableLiveData<Boolean> nextPage) {
        this.nextPage = nextPage;
    }

    public ObservableField<String> getAmount() {
        return amount;
    }

    public void setAmount(ObservableField<String> amount) {
        this.amount = amount;
    }

    public ObservableField<String> getSum() {
        return sum;
    }

    public MutableLiveData<Boolean> getSumState() {
        return sumState;
    }

    public MutableLiveData<Integer> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Integer> progressState) {
        this.progressState = progressState;
    }

    public MutableLiveData<String> getGoToPaymentPage() {
        return goToPaymentPage;
    }

    public ObservableField<String> getAssetPrice() {
        return assetPrice;
    }

    public void setAssetPrice(ObservableField<String> assetPrice) {
        this.assetPrice = assetPrice;
    }

    public MutableLiveData<String> getTandCAgree() {
        return TandCAgree;
    }

    public void setTandCAgree(MutableLiveData<String> tandCAgree) {
        TandCAgree = tandCAgree;
    }

    public SingleLiveEvent<Boolean> getGoToPin() {
        return goToPin;
    }

    public ObservableField<Boolean> getAmountEnable() {
        return amountEnable;
    }

    public MutableLiveData<KuknosPaymentResponse> getPaymentData() {
        return paymentData;
    }

    public void setAmountEnable(boolean amountEnable) {
        this.amountEnable.set(amountEnable);
    }

    public KuknosBalance.Balance getCurrentAssetInfo() {
        return currentAssetInfo;
    }

    public void setCurrentAssetInfo(String currentAssetInfo) {
        Gson gson = new Gson();
        this.currentAssetInfo = gson.fromJson(currentAssetInfo, KuknosBalance.Balance.class);
    }

    public String getCurrentAssetType() {
        return this.currentAssetInfo.getAsset().getType().equals("native") ? "PMN" : this.currentAssetInfo.getAssetCode();
    }
}
