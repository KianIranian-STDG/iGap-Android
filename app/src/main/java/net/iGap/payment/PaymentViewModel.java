package net.iGap.payment;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableInt;
import android.view.View;

public class PaymentViewModel extends ViewModel {

    private ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableInt showMainView = new ObservableInt(View.GONE);
    private CheckOrderResponse orderDetail;
    private MutableLiveData<String> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private MutableLiveData<String> goToWebPage = new MutableLiveData<>();

    private String token;
    private PaymentRepository repository;

    public PaymentViewModel(String token) {
        repository = PaymentRepository.getInstance();
        this.token = token;
        if (token != null) {
            checkOrderToken();
        } else {
            goBack.setValue(true);
        }
    }

    public ObservableInt getShowLoadingView() {
        return showLoadingView;
    }

    public ObservableInt getShowRetryView() {
        return showRetryView;
    }

    public ObservableInt getShowMainView() {
        return showMainView;
    }

    public CheckOrderResponse getOrderDetail() {
        return orderDetail;
    }

    public MutableLiveData<String> getShowErrorMessage() {
        return showErrorMessage;
    }

    public MutableLiveData<Boolean> getGoBack() {
        return goBack;
    }

    public MutableLiveData<String> getGoToWebPage() {
        return goToWebPage;
    }

    public void onRetryClick() {
        checkOrderToken();
    }

    public void onCancelClick() {
        goBack.setValue(true);
    }

    public void onAcceptClick() {
        goToWebPage.setValue(orderDetail.getRedirectUrl());
    }

    private void checkOrderToken() {
        showMainView.set(View.GONE);
        showRetryView.set(View.GONE);
        showLoadingView.set(View.VISIBLE);
        repository.checkOrder(token, new PaymentRepository.PaymentCallback() {
            @Override
            public void onSuccess(CheckOrderResponse data) {
                showLoadingView.set(View.GONE);
                showMainView.set(View.VISIBLE);
                orderDetail = data;
            }

            @Override
            public void onError(ErrorModel error) {
                showLoadingView.set(View.GONE);
                showErrorMessage.setValue(error.getMessage());
                showRetryView.set(View.VISIBLE);
            }

            @Override
            public void onFail() {
                showLoadingView.set(View.GONE);
                showRetryView.set(View.VISIBLE);
            }
        });
    }

}
