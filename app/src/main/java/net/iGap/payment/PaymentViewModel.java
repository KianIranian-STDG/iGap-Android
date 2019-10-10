package net.iGap.payment;

import android.view.View;

import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.helper.HelperCalander;

import org.jetbrains.annotations.NotNull;

public class PaymentViewModel extends BaseAPIViewModel {

    private ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableInt showMainView = new ObservableInt(View.INVISIBLE);
    private ObservableInt showPaymentErrorMessage = new ObservableInt(View.GONE);
    /*private ObservableInt background = new ObservableInt();*/
    private ObservableInt paymentStateIcon = new ObservableInt(R.string.icon_card_to_card);
    private ObservableInt paymentStatusTextColor = new ObservableInt(R.color.black);
    private ObservableInt showButtons = new ObservableInt(View.GONE);
    private ObservableInt showPaymentStatus = new ObservableInt(View.GONE);
    private ObservableInt closeButtonColor = new ObservableInt(R.color.accent);
    private ObservableField<String> paymentType = new ObservableField<>();
    private ObservableField<String> title = new ObservableField<>();
    private ObservableField<String> description = new ObservableField<>();
    /*private ObservableField<String> paymentOrderId = new ObservableField<>();*/
    private ObservableField<String> paymentStatus = new ObservableField<>();
    private ObservableField<String> price = new ObservableField<>();
    private ObservableDouble paymentRRN = new ObservableDouble();
    private MutableLiveData<PaymentResult> goBack = new MutableLiveData<>();
    private MutableLiveData<String> goToWebPage = new MutableLiveData<>();
    private MutableLiveData<Boolean> needUpdateGooglePlay = new MutableLiveData<>();

    private String token;
    private String orderId;
    private CheckOrderResponse orderDetail;
    private PaymentRepository repository;
    private PaymentResult paymentResult;

    public PaymentViewModel(String token, String type) {
        repository = PaymentRepository.getInstance();
        this.token = token;
        paymentType.set(type);
        paymentResult = new PaymentResult();
        if (token != null) {
            checkOrderToken();
        } else {
            goBack.setValue(paymentResult);
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

    public ObservableInt getShowPaymentErrorMessage() {
        return showPaymentErrorMessage;
    }

    /*public ObservableInt getBackground() {
        return background;
    }*/

    public ObservableInt getPaymentStateIcon() {
        return paymentStateIcon;
    }

    public ObservableInt getPaymentStatusTextColor() {
        return paymentStatusTextColor;
    }

    public ObservableInt getShowButtons() {
        return showButtons;
    }

    public ObservableInt getShowPaymentStatus() {
        return showPaymentStatus;
    }

    public ObservableInt getCloseButtonColor() {
        return closeButtonColor;
    }

    public ObservableField<String> getPaymentType() {
        return paymentType;
    }

    public ObservableField<String> getTitle() {
        return title;
    }

    public ObservableField<String> getDescription() {
        return description;
    }

    public ObservableDouble getPaymentRRN() {
        return paymentRRN;
    }

    public ObservableField<String> getPaymentStatus() {
        return paymentStatus;
    }

    /*public ObservableField<String> getPaymentOrderId() {
        return paymentOrderId;
    }*/

    public ObservableField<String> getPrice() {
        return price;
    }

    public MutableLiveData<PaymentResult> getGoBack() {
        return goBack;
    }

    public MutableLiveData<String> getGoToWebPage() {
        return goToWebPage;
    }

    public MutableLiveData<Boolean> getNeedUpdateGooglePlay() {
        return needUpdateGooglePlay;
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
        goBack.setValue(paymentResult);
    }

    public void onAcceptClick() {
        goToWebPage.setValue(orderDetail.getRedirectUrl());
    }

    public void setPaymentResult(Payment payment) {
        if (payment.getStatus().equals("SUCCESS")) {
            checkOrderStatus(payment.getOrderId());
        } else {
            showRetryView.set(View.GONE);
            showLoadingView.set(View.GONE);
            showMainView.set(View.VISIBLE);
            showButtons.set(View.INVISIBLE);
            showPaymentErrorMessage.set(View.VISIBLE);
            showPaymentStatus.set(View.VISIBLE);
            paymentStateIcon.set(R.string.close_icon);
            paymentStatusTextColor.set(R.color.red);
            paymentStatus.set(payment.getMessage());
            /*paymentOrderId.set(payment.getOrderId());*/
        }
    }

    public void checkOrderStatus(String orderId) {
        this.orderId = orderId;
        checkOrderStatus();
    }

    private void checkOrderToken() {
        showMainView.set(View.INVISIBLE);
        showButtons.set(View.INVISIBLE);
        showRetryView.set(View.GONE);
        showLoadingView.set(View.VISIBLE);
        repository.checkOrder(token, this, new ResponseCallback<CheckOrderResponse>() {
            @Override
            public void onSuccess(CheckOrderResponse data) {
                showLoadingView.set(View.GONE);
                showMainView.set(View.VISIBLE);
                showButtons.set(View.VISIBLE);
                description.set(data.getInfo().getProduct().getDescription());
                String tmp = String.valueOf(data.getInfo().getPrice());
                price.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(tmp) : tmp);
                title.set(data.getInfo().getProduct().getTitle());
                orderDetail = data;
            }

            @Override
            public void onError(ErrorModel error) {
                onErrorHandler(error);
            }

            @Override
            public void onFailed(boolean handShakeError) {
                onFailedHandler(handShakeError);
            }
        });
    }

    private void checkOrderStatus() {
        showMainView.set(View.INVISIBLE);
        showLoadingView.set(View.VISIBLE);
        showButtons.set(View.INVISIBLE);
        showRetryView.set(View.GONE);
        repository.checkOrderStatus(orderId, this, new ResponseCallback<CheckOrderStatusResponse>() {
            @Override
            public void onSuccess(CheckOrderStatusResponse data) {
                showPaymentErrorMessage.set(View.VISIBLE);
                showPaymentStatus.set(View.VISIBLE);
                showRetryView.set(View.GONE);
                showLoadingView.set(View.GONE);
                showMainView.set(View.VISIBLE);
                description.set(data.getPaymentInfo().getProduct().getDescription());
                String tmp = String.valueOf(data.getPaymentInfo().getPrice());
                price.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(tmp) : tmp);
                title.set(data.getPaymentInfo().getProduct().getTitle());
                /*paymentOrderId.set(data.getPaymentInfo().getId());*/
                paymentStatus.set(data.getMessage());
                if (data.isPaymentSuccess()) {
                    paymentStatusTextColor.set(R.color.green);
                    paymentStateIcon.set(R.string.check_icon);
                } else {
                    paymentStatusTextColor.set(R.color.red);
                    paymentStateIcon.set(R.string.close_icon);
                }
                closeButtonColor.set(R.color.accent);
                paymentRRN.set(data.getPaymentInfo().getRrn());
                paymentResult = new PaymentResult(data.getPaymentInfo().getOrderId(), data.isPaymentSuccess());
            }

            @Override
            public void onError(ErrorModel error) {
                onErrorHandler(error);
            }

            @Override
            public void onFailed(boolean handShakeError) {
                onFailedHandler(handShakeError);
            }
        });
    }

    public void onCloseClick() {
        goBack.setValue(paymentResult);
    }

    private void onErrorHandler(@NotNull ErrorModel error) {
        showLoadingView.set(View.GONE);
        showPaymentStatus.set(View.VISIBLE);
        paymentStatus.set(error.getMessage());
        paymentStateIcon.set(R.string.error_icon);
        paymentStatusTextColor.set(R.color.layout_background_top_connectivity);
    }

    private void onFailedHandler(boolean isNeedUpdateGooglePlay) {
        showLoadingView.set(View.GONE);
        paymentStateIcon.set(R.string.error_icon);
        paymentStatusTextColor.set(R.color.layout_background_top_connectivity);
        showPaymentStatus.set(View.VISIBLE);
        paymentStatus.set("error");
        closeButtonColor.set(R.color.red);
        showRetryView.set(View.VISIBLE);
        needUpdateGooglePlay.setValue(isNeedUpdateGooglePlay);
    }
}
