package net.iGap.viewmodel.kuknos;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.model.kuknos.KuknosError;
import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.kuknos.UserRepo;

import java.util.Arrays;
import java.util.List;

public class KuknosShowRecoveryKeyVM extends ViewModel {

    private List<String> lengths = Arrays.asList("24", "12");
    private List<String> languages = Arrays.asList("FA", "EN");
    private UserRepo userRepo = new UserRepo();
    private MutableLiveData<KuknosError> error;
    private SingleLiveEvent<Boolean> nextPage;
    private MutableLiveData<Boolean> progressState;
    private ObservableField<String> mnemonic = new ObservableField<>();
    private String selectedLanguage = "FA";
    private String selectedLength = "24";


    public KuknosShowRecoveryKeyVM() {
        nextPage = new SingleLiveEvent<>();
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

        if (userRepo.getMnemonic() == null || userRepo.getMnemonic().equals("-1")) {
            error.setValue(new KuknosError(true, "generate fatal error", "1", R.string.kuknos_RecoverySK_ErrorGenerateMn));
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
        selectedLanguage = languages.get(position);
        initMnemonic();
    }

    public void onItemSelectSpinnerLength(int position) {
        selectedLength = lengths.get(position);
        initMnemonic();
    }

    //Setter and Getter

    public MutableLiveData<KuknosError> getError() {
        return error;
    }

    public void setError(MutableLiveData<KuknosError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getNextPage() {
        return nextPage;
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
