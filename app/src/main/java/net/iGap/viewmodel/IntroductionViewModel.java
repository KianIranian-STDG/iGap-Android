package net.iGap.viewmodel;

import androidx.core.text.HtmlCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.WebSocketClient;
import net.iGap.model.repository.RegisterRepository;
import net.iGap.module.SingleLiveEvent;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.RequestManager;

public class IntroductionViewModel extends ViewModel {

    private MutableLiveData<Integer> showErrorMessage = new MutableLiveData<>();
    private SingleLiveEvent<Boolean> goToRegistrationPage = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> goToChangeLanguagePage = new SingleLiveEvent<>();
    private String agreementDescription;
    public SingleLiveEvent<String> showTermsAndConditionDialog = new SingleLiveEvent<>();
    private RegisterRepository repository;

    public MutableLiveData<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    public SingleLiveEvent<Boolean> getGoToRegistrationPage() {
        return goToRegistrationPage;
    }

    public SingleLiveEvent<Boolean> getGoToChangeLanguagePage() {
        return goToChangeLanguagePage;
    }

    public IntroductionViewModel() {
        repository = RegisterRepository.getInstance();
    }

    public void onLanguageChangeClick() {
        if (WebSocketClient.getInstance().isConnect()) {
            goToChangeLanguagePage.setValue(false);
        } else {
            showErrorMessage.setValue(R.string.waiting_for_connection);
        }
    }

    public void onStartClick() {
        if (WebSocketClient.getInstance().isConnect()) {
            goToRegistrationPage.setValue(true);
        } else {
            showErrorMessage.setValue(R.string.waiting_for_connection);
        }
    }

    public void onTermsAndConditionClick() {
        if (agreementDescription == null || agreementDescription.isEmpty()) {
            getTermsAndConditionData();
        } else {
            showTermsAndConditionDialog.setValue(agreementDescription);
        }
    }

    private void getTermsAndConditionData() {
//        isShowLoading.set(View.VISIBLE);
//        showRetryView.set(View.GONE);
        if (RequestManager.getInstance(AccountManager.selectedAccount).isSecure()) {
//            isShowLoading.set(View.VISIBLE);
//            showRetryView.set(View.GONE);
            repository.getTermsOfServiceBody(new RegisterRepository.RepositoryCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    if (data != null) {
                        agreementDescription = HtmlCompat.fromHtml(data, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
                    }
//                    isShowLoading.set(View.INVISIBLE);
                    showTermsAndConditionDialog.postValue(agreementDescription);
//                    viewVisibility.set(View.VISIBLE);
                }

                @Override
                public void onError() {
//                    isShowLoading.set(View.INVISIBLE);
                }
            });
        } else {
//            isShowLoading.set(View.GONE);
//            showRetryView.set(View.VISIBLE);
//            viewVisibility.set(View.INVISIBLE);
        }
    }

}
