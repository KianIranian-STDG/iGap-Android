package net.iGap.viewmodel;

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.helper.HelperCalander;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.payment.CheckOrderResponse;
import net.iGap.model.payment.CheckOrderStatusResponse;
import net.iGap.model.payment.Payment;
import net.iGap.model.payment.PaymentFeature;
import net.iGap.model.payment.PaymentResult;
import net.iGap.module.SingleLiveEvent;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.PaymentRepository;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PaymentViewModel extends BaseAPIViewModel {

    private final ObservableInt showLoadingView = new ObservableInt(View.VISIBLE);
    private final ObservableInt showRetryView = new ObservableInt(View.GONE);
    private final ObservableInt showMainView = new ObservableInt(View.INVISIBLE);
    private final ObservableInt showDiscountCoupon = new ObservableInt(View.INVISIBLE);
    private final ObservableInt showPaymentErrorMessage = new ObservableInt(View.GONE);
    private final ObservableInt paymentStateIcon = new ObservableInt(R.string.icon_card_to_card);
    private final ObservableInt paymentStatusTextColor = new ObservableInt(Theme.getColor(Theme.key_black));
    private final ObservableInt showButtons = new ObservableInt(View.GONE);
    private final ObservableInt showPaymentStatus = new ObservableInt(View.GONE);
    private final ObservableInt showSaveReceipt = new ObservableInt(View.GONE);
    private final ObservableInt closeButtonColor = new ObservableInt(R.color.accent);
    private final ObservableField<String> paymentType = new ObservableField<>();
    private final ObservableField<String> title = new ObservableField<>();
    private final ObservableField<String> description = new ObservableField<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final SingleLiveEvent<Integer> price = new SingleLiveEvent<>();
    private final ObservableDouble paymentRRN = new ObservableDouble();
    private final MutableLiveData<PaymentResult> goBack = new MutableLiveData<>();
    private final MutableLiveData<String> goToWebPage = new MutableLiveData<>();
    private final MutableLiveData<List<PaymentFeature>> discountOption = new MutableLiveData<>(null);
    private final ObservableInt discountVisibility = new ObservableInt(View.GONE);
    private final ObservableInt discountReceiptVisibility = new ObservableInt(View.GONE);
    private final ObservableField<String> discountReceiptAmount = new ObservableField<>("");
    private final ObservableInt taxReceiptVisibility = new ObservableInt(View.GONE);
    private final ObservableField<String> taxReceiptAmount = new ObservableField<>("");
    private final ObservableBoolean discountCodeEnable = new ObservableBoolean(true);
    private final ObservableBoolean saveDiscountCodeEnable = new ObservableBoolean(true);
    private final ObservableInt discountErrorVisibility = new ObservableInt(View.GONE);
    private final ObservableField<String> discountErrorText = new ObservableField<>();
    private final ObservableInt saveDiscountCodeColor = new ObservableInt(R.color.dayGreenTheme);

    private int discountPlanPosition = -1;
    private int originalPrice;

    private final String token;
    private String orderId;
    private CheckOrderResponse orderDetail;
    private final PaymentRepository repository;
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

    public ObservableBoolean getDiscountCodeEnable() {
        return discountCodeEnable;
    }

    public ObservableBoolean getSaveDiscountCodeEnable() {
        return saveDiscountCodeEnable;
    }

    public ObservableInt getDiscountErrorVisibility() {
        return discountErrorVisibility;
    }

    public ObservableField<String> getDiscountErrorText() {
        return discountErrorText;
    }

    public ObservableInt getSaveDiscountCodeColor() {
        return saveDiscountCodeColor;
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

    public ObservableInt getShowDiscountCoupon() {
        return showDiscountCoupon;
    }

    public ObservableInt getShowPaymentErrorMessage() {
        return showPaymentErrorMessage;
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

    public ObservableInt getShowPaymentStatus() {
        return showPaymentStatus;
    }

    public ObservableInt getShowSaveReceipt() {
        return showSaveReceipt;
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

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

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
            showDiscountCoupon.set(View.GONE);
            showButtons.set(View.INVISIBLE);
            showPaymentErrorMessage.set(View.VISIBLE);
            showPaymentStatus.set(View.VISIBLE);
            showSaveReceipt.set(View.GONE);
            paymentStateIcon.set(R.string.icon_close);
            paymentStatusTextColor.set(Theme.getColor(Theme.key_red));
            errorMessage.setValue(payment.getMessage());
            discountVisibility.set(View.GONE);
        }
    }

    public void checkOrderStatus(String orderId) {
        this.orderId = orderId;
        checkOrderStatus();
    }

    private void checkOrderToken() {
        showMainView.set(View.INVISIBLE);
        showDiscountCoupon.set(View.INVISIBLE);
        showButtons.set(View.INVISIBLE);
        showRetryView.set(View.GONE);
        showLoadingView.set(View.VISIBLE);
        repository.checkOrder(token, this, new ResponseCallback<CheckOrderResponse>() {
            @Override
            public void onSuccess(CheckOrderResponse data) {
                showLoadingView.set(View.GONE);
                showMainView.set(View.VISIBLE);
                showDiscountCoupon.set(View.VISIBLE);
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

    public void checkOrderTokenForDiscount(String strCoupon) {
        if (strCoupon == null || strCoupon.isEmpty()) {
            discountErrorVisibility.set(View.VISIBLE);
            discountErrorText.set(G.context.getString(R.string.enter_your_discount_code));
        } else {
            HashMap<String, String> coupon = new HashMap<>();
            coupon.put("coupon", strCoupon);
            repository.checkOrderForDiscount(token, coupon, this, new ResponseCallback<CheckOrderResponse>() {
                @Override
                public void onSuccess(CheckOrderResponse data) {
                    discountCodeEnable.set(false);
                    saveDiscountCodeEnable.set(false);
                    saveDiscountCodeColor.set(Theme.getColor(Theme.key_gray));
                    discountErrorVisibility.set(View.GONE);
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
                    discountCodeEnable.set(true);
                    saveDiscountCodeEnable.set(true);
                    saveDiscountCodeColor.set(R.color.dayGreenTheme);
                    discountErrorVisibility.set(View.VISIBLE);
                    discountErrorText.set(error);
                }

                @Override
                public void onFailed() {
                    onFailedHandler();
                }
            });
        }
    }

    private void checkOrderStatus() {
        showMainView.set(View.INVISIBLE);
        showDiscountCoupon.set(View.INVISIBLE);
        showLoadingView.set(View.VISIBLE);
        showButtons.set(View.INVISIBLE);
        showRetryView.set(View.GONE);
        discountVisibility.set(View.GONE);
        repository.checkOrderStatus(orderId, this, new ResponseCallback<CheckOrderStatusResponse>() {
            @Override
            public void onSuccess(CheckOrderStatusResponse data) {
                showPaymentErrorMessage.set(View.VISIBLE);
                showSaveReceipt.set(View.VISIBLE);
                showPaymentStatus.set(View.VISIBLE);
                showRetryView.set(View.GONE);
                showLoadingView.set(View.GONE);
                showMainView.set(View.VISIBLE);
                showDiscountCoupon.set(View.VISIBLE);
                description.set(data.getPaymentInfo().getProduct().getDescription());
                price.setValue(data.getPaymentInfo().getPrice());
                title.set(data.getPaymentInfo().getProduct().getTitle());
                errorMessage.setValue(data.getMessage());
                if (data.isPaymentSuccess()) {
                    paymentStatusTextColor.set(R.color.dayGreenTheme);
                    paymentStateIcon.set(R.string.icon_sent);
                    showDiscountCoupon.set(View.GONE);
                } else if (data.isPaymentUnknown()) {
                    paymentStatusTextColor.set(R.color.dayOrangeTheme);
                    paymentStateIcon.set(R.string.icon_error);
                } else {
                    paymentStatusTextColor.set(Theme.getColor(Theme.key_red));
                    paymentStateIcon.set(R.string.icon_close);
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
        paymentStateIcon.set(R.string.icon_error);
        showPaymentStatus.set(View.VISIBLE);
        showSaveReceipt.set(View.GONE);
        errorMessage.setValue(error);
        paymentStatusTextColor.set(Theme.getColor(Theme.key_theme_color));
    }

    private void onFailedHandler() {
        showLoadingView.set(View.GONE);
        paymentStateIcon.set(R.string.icon_error);
        showPaymentStatus.set(View.VISIBLE);
        showSaveReceipt.set(View.GONE);
        errorMessage.setValue("errorPayment");
        paymentStatusTextColor.set(Theme.getColor(Theme.key_title_text));
        closeButtonColor.set(Theme.getColor(Theme.key_red));
        showRetryView.set(View.VISIBLE);
    }

    public MutableLiveData<List<PaymentFeature>> getDiscountOption() {
        return discountOption;
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
