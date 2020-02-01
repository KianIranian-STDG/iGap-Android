package net.iGap.kuknos.viewmodel;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.kuknos.service.Repository.UserRepo;
import net.iGap.kuknos.service.mnemonic.Wallet;
import net.iGap.kuknos.service.mnemonic.WalletException;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.model.OperatorType;

import java.util.Arrays;
import java.util.List;

public class KuknosShowRecoveryKeyVM extends BaseAPIViewModel {

    public List<String> lengths = Arrays.asList("12", "24");
    public List<String> languages = Arrays.asList("EN", "FA");
    private UserRepo userRepo = new UserRepo();
    private MutableLiveData<ErrorM> error;
    private MutableLiveData<Boolean> nextPage;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> mnemonic = new ObservableField<>();
    private String selectedLanguage;
    private String selectedLength;


    public KuknosShowRecoveryKeyVM() {
        nextPage = new MutableLiveData<>();
        nextPage.setValue(false);
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        progressState.setValue(false);
    }

    public void initMnemonic() {
        if (selectedLanguage == null || selectedLength == null)
            return;
        if (selectedLength.equals("12")) {
            if (selectedLanguage.equals("FA")) {
                userRepo.generateFa12Mnemonic();
            } else if (selectedLanguage.equals("EN")) {
                userRepo.generateEn12Mnemonic();
            }
        } else if (selectedLength.equals("24")) {
            if (selectedLanguage.equals("FA")) {
                userRepo.generateFa24Mnemonic();
            } else if (selectedLanguage.equals("EN")) {
                userRepo.generateEn24Mnemonic();
            }
        }

        if (userRepo.getMnemonic().equals("-1")) {
            error.setValue(new ErrorM(true, "generate fatal error", "1", R.string.kuknos_RecoverySK_ErrorGenerateMn));
            return;
        }
        mnemonic.set(userRepo.getMnemonic());
    }

    public void onNext() {

        progressState.setValue(true);
        nextPage.setValue(true);
        progressState.setValue(false);
    }

    public void onItemSelectSpinnerLanguage(int position) {
        Log.d("bagi" ,"onItemSelectSpinnerLanguage" + position);
        if (selectedLanguage != null) {
            initMnemonic();
        } else {
            selectedLanguage = languages.get(position);
        }
    }

    public void onItemSelectSpinnerLength(int position) {
        Log.d("bagi" ,"onItemSelectSpinnerLength" + position);
        if (selectedLength != null) {
            initMnemonic();
        } else {
            selectedLength = lengths.get(position);
        }
    }

    //Setter and Getter

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

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }

    public ObservableField<String> getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(ObservableField<String> mnemonic) {
        this.mnemonic = mnemonic;
    }

}
