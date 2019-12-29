package net.iGap.kuknos.viewmodel;

import android.os.Handler;
import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.helper.HelperCalander;
import net.iGap.kuknos.service.Repository.PanelRepo;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.service.model.Parsian.KuknosAsset;
import net.iGap.kuknos.service.model.Parsian.KuknosResponseModel;

import java.text.DecimalFormat;

public class KuknosBuyPeymanVM extends BaseAPIViewModel {

    private ObservableField<String> amount = new ObservableField<>();
    private ObservableField<String> sum = new ObservableField<>();
    private MutableLiveData<ErrorM> error;
    // 0 : nothing 1: connecting to server 2: connecting to bank
    private MutableLiveData<Integer> progressState;
    private MutableLiveData<Boolean> sumState;
    //go to bank
    private MutableLiveData<Boolean> nextPage;
    private int PMNprice = -1;
    private int maxAmount = 1000000;
    private PanelRepo panelRepo = new PanelRepo();

    public KuknosBuyPeymanVM() {
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(0);
        sumState = new MutableLiveData<>();
        sumState.setValue(false);
        nextPage = new MutableLiveData<>();
        nextPage.setValue(false);
    }

    public void onSubmitBtn() {
        if (!checkEntry()) {
            return;
        }
        sendDataServer();
    }

    public boolean updateSum() {
        if (!checkEntry()) {
            return false;
        }
        if (Integer.parseInt(amount.get()) > maxAmount) {
            error.setValue(new ErrorM(true, "", "1", R.string.kuknos_buyP_MaxAmount));
            return false;
        }
        if (PMNprice == -1)
            return false;
        int sumTemp = Integer.parseInt(amount.get()) * PMNprice;
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
                progressState.setValue(0);
            }

            @Override
            public void onError(ErrorModel error) {
                progressState.setValue(0);
            }
        });
    }

    private void sendDataServer() {
        progressState.setValue(1);
        // TODO: send data to server
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //success
                connectingToBank();
                //error
                //error.setValue(new ErrorM(true, "wrong pin", "1", R.string.kuknos_buyP_failS));
            }
        }, 1000);
    }

    private void connectingToBank() {
        progressState.setValue(2);
        // TODO: send data to server
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressState.setValue(0);

                //success
                //nextPage.setValue(true);
                //error
                error.setValue(new ErrorM(true, "wrong pin", "1", R.string.kuknos_buyP_failB));
            }
        }, 1000);
    }

    private boolean checkEntry() {
        if (amount.get() == null) {
            // empty
            error.setValue(new ErrorM(true, "empty amount", "0", R.string.kuknos_buyP_emptyAmount));
            return false;
        }
        if (amount.get().isEmpty()) {
            // empty
            error.setValue(new ErrorM(true, "empty amount", "0", R.string.kuknos_buyP_emptyAmount));
            return false;
        }
        Log.d("amini", "checkEntry: " + amount.get());
        if (Integer.parseInt(amount.get()) == 0) {
            error.setValue(new ErrorM(true, "zero fail", "0", R.string.kuknos_buyP_zeroAmount));
            return false;
        }
        return true;
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

    public void setSum(ObservableField<String> sum) {
        this.sum = sum;
    }

    public MutableLiveData<Boolean> getSumState() {
        return sumState;
    }

    public void setSumState(MutableLiveData<Boolean> sumState) {
        this.sumState = sumState;
    }

    public MutableLiveData<Integer> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Integer> progressState) {
        this.progressState = progressState;
    }
}
