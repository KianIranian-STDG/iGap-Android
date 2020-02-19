package net.iGap.viewmodel.mobileBank;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.MobileBankRepository;
import net.iGap.model.mobileBank.BankShebaModel;
import net.iGap.model.mobileBank.BaseMobileBankResponse;

import java.util.ArrayList;
import java.util.List;

public class MobileBankBottomSheetViewModel extends BaseMobileBankViewModel {

    private MutableLiveData<List<String>> shebaListener = new MutableLiveData<>();
    private ObservableInt showRetry = new ObservableInt(View.GONE);

    public void getShebaNumber(String cardNumber) {
        if (cardNumber == null) {
            setLoaderState(false, true);
            return;
        }
        setLoaderState(true, false);
        MobileBankRepository.getInstance().getShebaNumber(cardNumber, this, new ResponseCallback<BaseMobileBankResponse<List<String>>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<List<String>> data) {
                setLoaderState(false, false);
                shebaListener.postValue(data.getData());
            }

            @Override
            public void onError(String error) {
                setLoaderState(false, true);
                showRequestErrorMessage.setValue(error);
            }

            @Override
            public void onFailed() {
                setLoaderState(false, true);
            }
        });
    }

    public void getShebaNumberByDeposit(String deposit) {
        if (deposit == null) {
            setLoaderState(false, true);
            return;
        }
        setLoaderState(true, false);
        MobileBankRepository.getInstance().getShebaNumberByDeposit(deposit, this, new ResponseCallback<BaseMobileBankResponse<BankShebaModel>>() {
            @Override
            public void onSuccess(BaseMobileBankResponse<BankShebaModel> data) {
                setLoaderState(false, false);
                List<String> shebaList = new ArrayList<>();
                shebaList.add(data.getData().getSheba());
                shebaListener.postValue(shebaList);
            }

            @Override
            public void onError(String error) {
                showRequestErrorMessage.setValue(error);
                setLoaderState(false, true);
            }

            @Override
            public void onFailed() {
                setLoaderState(false, true);
            }
        });
    }

    private void setLoaderState(boolean state, boolean isEffectRetry) {
        if (state) {
            showRetry.set(View.GONE);
            showLoading.set(View.VISIBLE);
        } else {
            showRetry.set(View.GONE);
            showLoading.set(isEffectRetry ? View.VISIBLE : View.GONE);
        }
    }

    public MutableLiveData<List<String>> getShebaListener() {
        return shebaListener;
    }

    public ObservableInt getShowRetry() {
        return showRetry;
    }
}
