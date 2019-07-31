package net.iGap.payment;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableDouble;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.errorhandler.ErrorModel;

public class PaymentViewModel extends ViewModel {

    private ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableInt showMainView = new ObservableInt(View.GONE);
    private ObservableInt background = new ObservableInt();
    private ObservableField<String> title = new ObservableField<>();
    private ObservableField<String> description = new ObservableField<>();
    private ObservableDouble price = new ObservableDouble();
    private MutableLiveData<String> showErrorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> goBack = new MutableLiveData<>();
    private MutableLiveData<String> goToWebPage = new MutableLiveData<>();

    private String token;
    private String orderId;
    private CheckOrderResponse orderDetail;
    private PaymentRepository repository;

    public PaymentViewModel(String token) {
        repository = PaymentRepository.getInstance();
        background.set(G.isDarkTheme ? R.drawable.bottom_sheet_background : R.drawable.bottom_sheet_light_background);
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

    public ObservableInt getBackground() {
        return background;
    }

    public ObservableField<String> getTitle() {
        return title;
    }

    public ObservableField<String> getDescription() {
        return description;
    }

    public ObservableDouble getPrice() {
        return price;
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

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearRepository();
    }

    public void onRetryClick() {
        if (orderId == null) {
            checkOrderToken();
        } else {
            checkOrderStatus();
        }
    }

    public void onCancelClick() {
        goBack.setValue(true);
    }

    public void onAcceptClick() {
        goToWebPage.setValue(orderDetail.getRedirectUrl());
    }

    public void checkOrderStatus(String orderId) {
        this.orderId = orderId;
        checkOrderStatus();
    }

    private void checkOrderToken() {
        showMainView.set(View.GONE);
        showRetryView.set(View.GONE);
        showLoadingView.set(View.VISIBLE);
        repository.checkOrder(token, new PaymentRepository.PaymentCallback<CheckOrderResponse>() {
            @Override
            public void onSuccess(CheckOrderResponse data) {
                showLoadingView.set(View.GONE);
                showMainView.set(View.VISIBLE);
                description.set(data.getInfo().getProduct().getDescription());
                price.set(data.getInfo().getPrice());
                title.set(data.getInfo().getProduct().getTitle());
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

    private void checkOrderStatus() {
        showMainView.set(View.GONE);
        showLoadingView.set(View.VISIBLE);
        showRetryView.set(View.GONE);
        repository.checkOrderStatus(orderId, new PaymentRepository.PaymentCallback<Object>() {
            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onError(ErrorModel error) {

            }

            @Override
            public void onFail() {

            }
        });
    }
}
