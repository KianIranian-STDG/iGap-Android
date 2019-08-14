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
import net.iGap.api.errorhandler.ResponseCallback;
import net.iGap.helper.HelperCalander;

import org.jetbrains.annotations.NotNull;

public class PaymentViewModel extends ViewModel {

    private ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    private ObservableInt showRetryView = new ObservableInt(View.GONE);
    private ObservableInt showMainView = new ObservableInt(View.INVISIBLE);
    private ObservableInt showPaymentErrorMessage = new ObservableInt(View.GONE);
    private ObservableInt background = new ObservableInt();
    private ObservableInt paymentStateIcon = new ObservableInt(R.string.icon_card_to_card);
    private ObservableInt paymentStatusTextColor = new ObservableInt(R.color.black);
    private ObservableInt showButtons = new ObservableInt(View.GONE);
    private ObservableField<String> paymentType = new ObservableField<>();
    private ObservableField<String> title = new ObservableField<>();
    private ObservableField<String> description = new ObservableField<>();
    /*private ObservableField<String> paymentOrderId = new ObservableField<>();*/
    private ObservableField<String> paymentStatus = new ObservableField<>();
    private ObservableField<String> price = new ObservableField<>();
    private ObservableDouble paymentRRN = new ObservableDouble();
    private MutableLiveData<String> showErrorMessage = new MutableLiveData<>();
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
        background.set(G.isDarkTheme ? R.drawable.bottom_sheet_background : R.drawable.bottom_sheet_light_background);
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

    public ObservableInt getBackground() {
        return background;
    }

    public ObservableInt getPaymentStateIcon() {
        return paymentStateIcon;
    }

    public ObservableInt getPaymentStatusTextColor() {
        return paymentStatusTextColor;
    }

    public ObservableInt getShowButtons() {
        return showButtons;
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

    public MutableLiveData<String> getShowErrorMessage() {
        return showErrorMessage;
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
        repository.checkOrder(token, new ResponseCallback<CheckOrderResponse>() {
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
        repository.checkOrderStatus(orderId, new ResponseCallback<CheckOrderStatusResponse>() {
            @Override
            public void onSuccess(CheckOrderStatusResponse data) {
                showPaymentErrorMessage.set(View.VISIBLE);
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
        showErrorMessage.setValue(error.getMessage());
        showRetryView.set(View.VISIBLE);
    }

    private void onFailedHandler(boolean isNeedUpdateGooglePlay) {
        showLoadingView.set(View.GONE);
        showRetryView.set(View.VISIBLE);
        needUpdateGooglePlay.setValue(isNeedUpdateGooglePlay);
    }
}
