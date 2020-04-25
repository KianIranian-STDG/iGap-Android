package net.iGap.viewmodel;

import android.view.View;

import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.helper.HelperCalander;
import net.iGap.model.payment.CheckOrderResponse;
import net.iGap.model.payment.CheckOrderStatusResponse;
import net.iGap.model.payment.Payment;
import net.iGap.model.payment.PaymentFeature;
import net.iGap.model.payment.PaymentResult;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.PaymentRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

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
    private SingleLiveEvent<Integer> price = new SingleLiveEvent<>();
    private ObservableDouble paymentRRN = new ObservableDouble();
    private MutableLiveData<PaymentResult> goBack = new MutableLiveData<>();
    private MutableLiveData<String> goToWebPage = new MutableLiveData<>();

    private MutableLiveData<List<PaymentFeature>> discountOption = new MutableLiveData<>(null);
    private ObservableInt discountVisibility = new ObservableInt(View.GONE);
    private ObservableInt discountReceiptVisibility = new ObservableInt(View.GONE);
    private ObservableField<String> discountReceiptAmount = new ObservableField<>("");
    private ObservableInt taxReceiptVisibility = new ObservableInt(View.GONE);
    private ObservableField<String> taxReceiptAmount = new ObservableField<>("");
    private int discountPlanPosition = -1;
    private int originalPrice;

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

    public SingleLiveEvent<Integer> getPrice() {
        return price;
    }

    public MutableLiveData<PaymentResult> getGoBack() {
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
        goBack.setValue(paymentResult);
    }

    public void onAcceptClick() {
        if (discountPlanPosition == -1)
            goToWebPage.setValue(orderDetail.getRedirectUrl());
        else
            goToWebPage.setValue(orderDetail.getRedirectUrl() + "?feature=" + discountOption.getValue().get(discountPlanPosition).getType());
    }

    public void setPaymentResult(Payment payment) {
        if (payment.getStatus().equals("SUCCESS") || payment.getStatus().equals("PAID")) {
            checkOrderStatus(payment.getOrderId());
            if (payment.getDiscount() != null && !payment.getDiscount().equals("null")) {
                discountReceiptVisibility.set(View.VISIBLE);
                discountReceiptAmount.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(payment.getDiscount()) : payment.getDiscount());
            }
            if (payment.getTax() != null && !payment.getTax().equals("null") && !payment.getTax().equals("0")) {
                taxReceiptVisibility.set(View.VISIBLE);
                taxReceiptAmount.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(payment.getTax()) : payment.getTax());
            }
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
            discountVisibility.set(View.GONE);
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
                originalPrice = data.getInfo().getPrice();
                price.setValue(originalPrice);
                title.set(data.getInfo().getProduct().getTitle());
                orderDetail = data;
                discountOption.setValue(data.getDiscountOption());
                if (data.getDiscountOption() != null && data.getDiscountOption().size() > 0) {
                    discountVisibility.set(View.VISIBLE);
                }
            }

            @Override
            public void onError(String error) {
                onErrorHandler(error);
            }

            @Override
            public void onFailed() {
                onFailedHandler();
            }
        });
    }

    private void checkOrderStatus() {
        showMainView.set(View.INVISIBLE);
        showLoadingView.set(View.VISIBLE);
        showButtons.set(View.INVISIBLE);
        showRetryView.set(View.GONE);
        discountVisibility.set(View.GONE);
        repository.checkOrderStatus(orderId, this, new ResponseCallback<CheckOrderStatusResponse>() {
            @Override
            public void onSuccess(CheckOrderStatusResponse data) {
                showPaymentErrorMessage.set(View.VISIBLE);
                showPaymentStatus.set(View.VISIBLE);
                showRetryView.set(View.GONE);
                showLoadingView.set(View.GONE);
                showMainView.set(View.VISIBLE);
                description.set(data.getPaymentInfo().getProduct().getDescription());
                price.setValue(data.getPaymentInfo().getPrice());
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
                paymentResult = new PaymentResult(data.getPaymentInfo().getOrderId(), String.format(Locale.US, "%.0f", data.getPaymentInfo().getRrn()), data.isPaymentSuccess());
            }

            @Override
            public void onError(String error) {
                onErrorHandler(error);
            }

            @Override
            public void onFailed() {
                onFailedHandler();
            }
        });
    }

    public void onCloseClick() {
        goBack.setValue(paymentResult);
    }

    private void onErrorHandler(@NotNull String error) {
        showLoadingView.set(View.GONE);
        showPaymentStatus.set(View.VISIBLE);
        paymentStatus.set(error);
        paymentStateIcon.set(R.string.error_icon);
        paymentStatusTextColor.set(R.color.layout_background_top_connectivity);
    }

    private void onFailedHandler() {
        showLoadingView.set(View.GONE);
        paymentStateIcon.set(R.string.error_icon);
        paymentStatusTextColor.set(R.color.layout_background_top_connectivity);
        showPaymentStatus.set(View.VISIBLE);
        paymentStatus.set("error");
        closeButtonColor.set(R.color.red);
        showRetryView.set(View.VISIBLE);
    }

    public MutableLiveData<List<PaymentFeature>> getDiscountOption() {
        return discountOption;
    }

    public int getDiscountPlanPosition() {
        return discountPlanPosition;
    }

    public void setDiscountPlanPosition(int discountPlanPosition) {
        this.discountPlanPosition = discountPlanPosition;
        if (discountPlanPosition != -1) {
            price.setValue(discountOption.getValue().get(discountPlanPosition).getPrice());
        } else {
            price.setValue(originalPrice);
        }
    }

    public ObservableInt getDiscountVisibility() {
        return discountVisibility;
    }

    public ObservableInt getDiscountReceiptVisibility() {
        return discountReceiptVisibility;
    }

    public ObservableField<String> getDiscountReceiptAmount() {
        return discountReceiptAmount;
    }

    public ObservableInt getTaxReceiptVisibility() {
        return taxReceiptVisibility;
    }

    public ObservableField<String> getTaxReceiptAmount() {
        return taxReceiptAmount;
    }
}
