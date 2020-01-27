package net.iGap.kuknos.viewmodel;

import android.util.Log;

import androidx.core.text.HtmlCompat;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.Parsian.IgapPayment;
import net.iGap.kuknos.service.model.Parsian.KuknosAsset;
import net.iGap.kuknos.service.model.Parsian.KuknosFeeModel;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;
import net.iGap.request.RequestInfoPage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class KuknosBuyPeymanVM extends BaseAPIViewModel {

    private ObservableField<String> amount = new ObservableField<>();
    private ObservableField<String> sum = new ObservableField<>();
    private ObservableField<String> assetPrice = new ObservableField<>("قیمت هر پیمان: ...");
    private ObservableField<String> transactionFee = new ObservableField<>("کارمزد خرید پیمان: ...");
    private MutableLiveData<ErrorM> error;
    // 0 : nothing 1: connecting to server 2: connecting to bank
    private MutableLiveData<Integer> progressState;
    private MutableLiveData<Boolean> sumState;
    private MutableLiveData<String> goToPaymentPage;
    //go to bank
    private MutableLiveData<Boolean> nextPage;
    private int PMNprice = -1;
    private int maxAmount = 1000000;
    Double sumTemp;
    private PanelRepo panelRepo = new PanelRepo();
    private MutableLiveData<String> TandCAgree;
    private KuknosFeeModel.Fee fees = null;

    public KuknosBuyPeymanVM() {
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(0);
        sumState = new MutableLiveData<>();
        sumState.setValue(false);
        nextPage = new MutableLiveData<>();
        nextPage.setValue(false);
        goToPaymentPage = new MutableLiveData<>();
        TandCAgree = new MutableLiveData<>();
    }

    public void onSubmitBtn() {
        if (checkEntry()) {
            return;
        }
        sendDataServer();
    }

    public boolean updateSum() {
        if (checkEntry()) {
            return false;
        }
        if (Integer.parseInt(amount.get()) > maxAmount) {
            error.setValue(new ErrorM(true, "", "1", R.string.kuknos_buyP_MaxAmount));
            return false;
        }
        if (PMNprice == -1 || fees == null)
            return false;
        sumTemp = Double.parseDouble(amount.get()) * PMNprice * (100 - fees.getDiscount()) / 100 + fees.getFee() / 10000000 * PMNprice;
        BigDecimal tmp = new BigDecimal(sumTemp).setScale(0, RoundingMode.UP);
        sumTemp = Double.parseDouble(tmp.toString());
        DecimalFormat df = new DecimalFormat(",###");
        sum.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(df.format(sumTemp)) : df.format(sumTemp));
        return true;
    }

    public void getAssetValue() {
        progressState.setValue(3);
        panelRepo.getSpecificAssets("PMN", this, new ResponseCallback<KuknosResponseModel<KuknosAsset>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosAsset> data) {
                PMNprice = data.getData().getAssets().get(0).getBuyRate();
                maxAmount = data.getData().getAssets().get(0).getRemainAmount();
                getFees();
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

    public void getFees() {
        panelRepo.getFee(this, new ResponseCallback<KuknosResponseModel<KuknosFeeModel>>() {
            @Override
            public void onSuccess(KuknosResponseModel<KuknosFeeModel> data) {
                for (KuknosFeeModel.Fee tmp : data.getData().getFees()) {
                    if (tmp.getMethod().equals("charge-wallet")) {
                        fees = tmp;
                        break;
                    }
                }
                assetPrice.set("قیمت هر پیمان: " + PMNprice * (100 - fees.getDiscount()) / 10 + " ریال");
                transactionFee.set("کارمزد خرید: " + fees.getFee() / 10000000 + " پیمان");
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

    private void sendDataServer() {
        progressState.setValue(1);
        panelRepo.buyAsset("PMN", amount.get(), "" + (int) Math.round(sumTemp),
                "", this, new ResponseCallback<KuknosResponseModel<IgapPayment>>() {
                    @Override
                    public void onSuccess(KuknosResponseModel<IgapPayment> data) {
                        goToPaymentPage.setValue(data.getData().getToken());
                        progressState.setValue(0);
                    }

                    @Override
                    public void onError(String errorM) {
                        progressState.setValue(0);
                        error.setValue(new ErrorM(true, "wrong pin", errorM, R.string.kuknos_buyP_failS));

                    }

                    @Override
                    public void onFailed() {
                        progressState.setValue(0);
                        error.setValue(new ErrorM(true, "wrong pin", "1", R.string.kuknos_buyP_failS));
                    }

                });
    }

    private boolean checkEntry() {
        if (amount.get() == null) {
            // empty
            error.setValue(new ErrorM(true, "empty amount", "0", R.string.kuknos_buyP_emptyAmount));
            return true;
        }
        if (amount.get().isEmpty()) {
            // empty
            error.setValue(new ErrorM(true, "empty amount", "0", R.string.kuknos_buyP_emptyAmount));
            return true;
        }
        Log.d("amini", "checkEntry: " + amount.get());
        if (Integer.parseInt(amount.get()) == 0) {
            error.setValue(new ErrorM(true, "zero fail", "0", R.string.kuknos_buyP_zeroAmount));
            return true;
        }
        return false;
    }

    public void getTermsAndCond() {
        if (!G.isSecure)
            return;
        new RequestInfoPage().infoPageAgreementDiscovery("TOS", new RequestInfoPage.OnInfoPage() {
            @Override
            public void onInfo(String body) {
                if (body != null) {
                    TandCAgree.setValue(HtmlCompat.fromHtml(body, HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
                }
                TandCAgree.setValue("error");
            }

            @Override
            public void onError(int major, int minor) {
                TandCAgree.setValue("error");
            }
        });
    }

    public MutableLiveData<ErrorM> getError() {
        return error;
    }

    public void setError(MutableLiveData<ErrorM> error) {
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

    public ObservableField<String> getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(ObservableField<String> transactionFee) {
        this.transactionFee = transactionFee;
    }
}
