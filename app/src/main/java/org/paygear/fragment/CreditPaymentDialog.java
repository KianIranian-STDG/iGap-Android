package org.paygear.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.databinding.FragmentDialogCreditPaymentBinding;

import org.paygear.RaadApp;
import org.paygear.model.AvailableClubs_Result;
import org.paygear.model.Card;
import org.paygear.model.Order;
import org.paygear.model.Payment;
import org.paygear.model.PaymentAuth;
import org.paygear.model.PaymentResult;
import org.paygear.model.QRResponse;
import org.paygear.model.Transport;
import org.paygear.utils.RSAUtils;
import org.paygear.web.Web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.model.Product;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.web.PostRequest;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditPaymentDialog extends BottomSheetDialogFragment {
    Context context;
    View rootView;
    FragmentDialogCreditPaymentBinding creditPaymentBinding;
    private String mOrderId;
    private Order mOrder;
    private Account mAccount;
    private Product mProduct;
    private Transport mTransport;
    private boolean isServices = false;
    private boolean isCredit = false;
    private String orderToken;
    private String pubKey;
    private String xAccessToken;
    private boolean isCashOutToWallet = false;
    private long mAmount;

    private Card merchantCard;
    private List<AvailableClubs_Result> mClubs;
    private QRResponse qrResponse;
    private Card selectedCard;
    boolean haveEnoughCredit = true;
    long discount = 0;
    private String mInvoiceNumber;
    private Boolean mHasMaximum;
    private Boolean mIsPercentage;
    private Long mAvailableClubAmount;
    long clubAvailableAmount= 0L;


    public static CreditPaymentDialog newInstance(Account account, Order order, Transport transport, Product product, String orderId, boolean isServices, boolean isCashOutToWallet, String orderToken, String pubKey, String xAccessToken, long amount, QRResponse qrResponse, boolean isCredit, Card selectedCard, long discount, String invoiceNumber
            , Boolean clubHasMaximium, Boolean clubIsPercentage, Long avaialableAmount) {
        CreditPaymentDialog fragment = new CreditPaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable("Account", account);
        args.putSerializable("Order", order);
        args.putSerializable("Transport", transport);
        args.putSerializable("Product", product);
        args.putString("OrderId", orderId);
        args.putBoolean("IsServices", isServices);
        args.putBoolean("IsCashOutToWallet", isCashOutToWallet);
        args.putString("OrderToken", orderToken);
        args.putString("PubKey", pubKey);
        args.putString("XAccessToken", xAccessToken);
        args.putLong("Amount", amount);
        args.putSerializable("QrResponse", qrResponse);
        args.putBoolean("IsCredit", isCredit);
        args.putSerializable("SelectedCard", selectedCard);
        args.putLong("Discount", discount);
        args.putString("InvoiceNumber", invoiceNumber);
        args.putBoolean("HasMaximum", clubHasMaximium);
        args.putBoolean("IsPercentage", clubIsPercentage);
        args.putLong("AvailableClubAmount", avaialableAmount);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccount = (Account) getArguments().getSerializable("Account");
            mOrder = (Order) getArguments().getSerializable("Order");
            mTransport = (Transport) getArguments().getSerializable("Transport");
            mProduct = (Product) getArguments().getSerializable("Product");
            mOrderId = getArguments().getString("OrderId");
            isServices = getArguments().getBoolean("IsServices", false);
            isCashOutToWallet = getArguments().getBoolean("IsCashOutToWallet", false);
            orderToken = getArguments().getString("OrderToken");
            pubKey = getArguments().getString("PubKey");
            xAccessToken = getArguments().getString("XAccessToken");
            mAmount = getArguments().getLong("Amount", 0);
            isCredit = getArguments().getBoolean("IsCredit", false);
            qrResponse = (QRResponse) getArguments().getSerializable("QrResponse");
            selectedCard = (Card) getArguments().getSerializable("SelectedCard");
            discount = getArguments().getLong("Discount");
            mInvoiceNumber = getArguments().getString("InvoiceNumber");

            mHasMaximum = getArguments().getBoolean("HasMaximum", false);
            mIsPercentage = getArguments().getBoolean("IsPercentage", false);
            mAvailableClubAmount= getArguments().getLong("AvailableClubAmount");


        }
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.fragment_dialog_credit_payment, container, false);
        context = inflater.getContext();
        creditPaymentBinding = FragmentDialogCreditPaymentBinding.bind(rootView);
        Initialize();
        return rootView;
    }

    private void Initialize() {
        if (selectedCard == null)
            return;
        creditPaymentBinding.amountToPay.setText(RaadCommonUtils.formatPrice(mAmount, false));

        if (selectedCard.isRaadCard()) {
            creditPaymentBinding.clubCardBalance.setVisibility(View.GONE);
            creditPaymentBinding.clubCardBalanceTitle.setVisibility(View.GONE);
            creditPaymentBinding.paygearCardBalance.setText(RaadCommonUtils.formatPrice(selectedCard.balance, false));
            if (mAmount > selectedCard.balance) {
                creditPaymentBinding.paygearCardBalance.setTextColor(Color.RED);
                creditPaymentBinding.paygearCardBalanceTitle.setTextColor(Color.RED);
                creditPaymentBinding.message.setTextColor(Color.RED);
                haveEnoughCredit = false;
                creditPaymentBinding.chargeLayout.setVisibility(View.VISIBLE);
                creditPaymentBinding.price.setText(RaadCommonUtils.formatPrice(mAmount - selectedCard.balance, false));
                creditPaymentBinding.pinLayout.setVisibility(View.GONE);
                creditPaymentBinding.message.setText(R.string.balance_low);
                creditPaymentBinding.button.setText(R.string.charge_wallet);

            } else {
                creditPaymentBinding.paygearCardBalance.setTextColor(Color.parseColor("#2196f3"));
                creditPaymentBinding.paygearCardBalanceTitle.setTextColor(Color.parseColor("#2196f3"));
                haveEnoughCredit = true;
                creditPaymentBinding.chargeLayout.setVisibility(View.GONE);
                creditPaymentBinding.pinLayout.setVisibility(View.VISIBLE);
                creditPaymentBinding.message.setText(R.string.enter_your_password);
                creditPaymentBinding.message.setTextColor(Color.BLACK);
                creditPaymentBinding.button.setText(R.string.pay);

            }
        } else {

            creditPaymentBinding.clubCardBalance.setVisibility(View.VISIBLE);
            creditPaymentBinding.clubCardBalanceTitle.setVisibility(View.VISIBLE);
            if (mAmount <= selectedCard.balance) {
                creditPaymentBinding.paygearCardBalanceTitle.setVisibility(View.GONE);
                creditPaymentBinding.paygearCardBalance.setVisibility(View.GONE);
                creditPaymentBinding.amountToPayFactor.setVisibility(View.GONE);
                creditPaymentBinding.amountToPayFactorTitle.setVisibility(View.GONE);
            } else {
                creditPaymentBinding.paygearCardBalanceTitle.setVisibility(View.VISIBLE);
                creditPaymentBinding.paygearCardBalance.setVisibility(View.VISIBLE);
                creditPaymentBinding.amountToPayFactor.setVisibility(View.VISIBLE);
                creditPaymentBinding.amountToPayFactorTitle.setVisibility(View.VISIBLE);

            }

            if (mHasMaximum){
                if (mIsPercentage){
                    clubAvailableAmount=Math.min(selectedCard.balance,((mAmount*mAvailableClubAmount)/100));
                }else {
                    clubAvailableAmount=Math.min(selectedCard.balance,mAvailableClubAmount);
                }
            }else {
                clubAvailableAmount=selectedCard.balance;
            }
            creditPaymentBinding.paygearCardBalance.setText(RaadCommonUtils.formatPrice(RaadApp.paygearCard.balance, false));
            creditPaymentBinding.clubCardBalance.setText(RaadCommonUtils.formatPrice(clubAvailableAmount, false));
            creditPaymentBinding.amountToPayFactor.setText(RaadCommonUtils.formatPrice(mAmount - clubAvailableAmount, false));
            if (mAmount > selectedCard.balance + RaadApp.paygearCard.balance) {
                creditPaymentBinding.paygearCardBalance.setTextColor(Color.RED);
                creditPaymentBinding.paygearCardBalanceTitle.setTextColor(Color.RED);
                creditPaymentBinding.message.setTextColor(Color.RED);
                haveEnoughCredit = false;
                creditPaymentBinding.chargeLayout.setVisibility(View.VISIBLE);
                creditPaymentBinding.price.setText(RaadCommonUtils.formatPrice(mAmount - clubAvailableAmount - RaadApp.paygearCard.balance, false));
                creditPaymentBinding.pinLayout.setVisibility(View.GONE);
                creditPaymentBinding.message.setText(R.string.balance_low);
                creditPaymentBinding.button.setText(R.string.charge_wallet);

            } else {
                creditPaymentBinding.paygearCardBalance.setTextColor(Color.parseColor("#2196f3"));
                creditPaymentBinding.paygearCardBalanceTitle.setTextColor(Color.parseColor("#2196f3"));
                haveEnoughCredit = true;
                creditPaymentBinding.chargeLayout.setVisibility(View.GONE);
                creditPaymentBinding.pinLayout.setVisibility(View.VISIBLE);
                creditPaymentBinding.message.setText(R.string.enter_your_password);
                creditPaymentBinding.message.setTextColor(Color.BLACK);
                creditPaymentBinding.button.setText(R.string.pay);
            }

        }


        if (mTransport != null || mProduct != null) {
            creditPaymentBinding.name.setVisibility(View.VISIBLE);
            if (mTransport != null)
                creditPaymentBinding.name.setText(mTransport.name);
            if (mProduct != null)
                creditPaymentBinding.name.setText(mProduct.name);
        } else {
            creditPaymentBinding.name.setVisibility(View.GONE);
        }

        creditPaymentBinding.title.setText(R.string.pay_to);
        if (mOrder == null) {
            Picasso.with(G.context)
                    .load(RaadCommonUtils.getImageUrl(mAccount.profilePicture))
                    .fit()
                    .into(creditPaymentBinding.image);
            creditPaymentBinding.subtitle.setText(mAccount.getName());

        } else {
            Picasso.with(G.context)
                    .load(RaadCommonUtils.getImageUrl(mOrder.receiver.profilePicture))
                    .fit()
                    .into(creditPaymentBinding.image);
            creditPaymentBinding.subtitle.setText(mOrder.receiver.getName());


        }

        creditPaymentBinding.progress.setStatus(1);

        creditPaymentBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveEnoughCredit) {

                    if (mOrder == null) {
                        Payment payment = new Payment();
                        Account account = new Account();
                        account.id = mAccount.id;
                        payment.account = account;
                        payment.price = mAmount + discount;
                        payment.isCredit = true;
                        if (!TextUtils.isEmpty(creditPaymentBinding.pin.getText().toString().trim())) {
                            initPay(payment, creditPaymentBinding.pin.getText().toString(), (!selectedCard.isRaadCard() && clubAvailableAmount < mAmount && clubAvailableAmount != 0));
                        } else {
                            Toast.makeText(context, getString(R.string.enter_your_password), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        initPay(null, creditPaymentBinding.pin.getText().toString(), (!selectedCard.isRaadCard() && clubAvailableAmount < mAmount && clubAvailableAmount != 0));
                    }
                } else {
                    if (TextUtils.isEmpty(creditPaymentBinding.price.getText())) {
                        Toast.makeText(getContext(), R.string.enter_cash_in_price, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        Payment payment = new Payment();
                        Account account = new Account();
                        account.id = Auth.getCurrentAuth().getId();
                        payment.account = account;
                        payment.price = Long.parseLong(creditPaymentBinding.price.getText().toString().replaceAll(",", ""));
                        payment.orderType = Order.ORDER_TYPE_CHARGE_CREDIT;
                        payment.isCredit = false;
                        chargeWallet(payment);

                    } catch (Exception e) {

                    }

//                    Toast.makeText(context, "لطفا به صفحه شارژ کیف پول بروید!!!(اتوماتیک خواهد شد)", Toast.LENGTH_SHORT).show();

                }
            }
        });
        creditPaymentBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CreditPaymentDialog.this.dismiss();
                } catch (Exception e) {

                }
            }
        });
        creditPaymentBinding.progress.setStatus(1);

    }

    private void chargeWallet(final Payment payment) {
        setLoading(true);
        Web.getInstance().getWebService().initPayment(payment.getRequestBody()).enqueue(new Callback<PaymentAuth>() {
            @Override
            public void onResponse(Call<PaymentAuth> call, Response<PaymentAuth> response) {
                Boolean success = Web.checkResponse(CreditPaymentDialog.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    payment.paymentAuth = response.body();
                    if (payment.paymentAuth.IPGUrl != null && !payment.paymentAuth.IPGUrl.replaceAll(" ", "").equals("")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(payment.paymentAuth.IPGUrl));
                        startActivity(intent);
                    } else if (getActivity() instanceof NavigationBarActivity) {
                        ((NavigationBarActivity) getActivity()).replaceFullFragment(
                                CardsFragment.newInstance(payment), "CardsFragment", true);
                    }
                    try {
                        ((FactorPaymentDialog) getActivity().getSupportFragmentManager().findFragmentByTag("FactorPaymentDialog")).dismiss();
                    } catch (Exception e) {

                    }
                } else {
                    setLoading(false);
                }
            }

            @Override
            public void onFailure(Call<PaymentAuth> call, Throwable t) {
                if (Web.checkFailureResponse(CreditPaymentDialog.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void initPay(final Payment payment, final String payGearPin, final boolean isSplitPayment) {
        setLoading(true);
        RequestBody requestBody;

        if (payment == null && mOrder != null) {
            Map<String, String> podMap = null;

            Map<String, Object> map = new HashMap<>();
            map.put("token", mOrder.id);
            map.put("credit", true);
            map.put("transaction_type", 4);

            requestBody = PostRequest.getRequestBody(map);
        } else {
            Map<String, Object> podMap = new HashMap<>();
            podMap.put("to", payment.account.id);
            podMap.put("amount", payment.getPaymentPrice());

            if (payment.orderType > -1) {
                //map.put("pre_order", true);
                podMap.put("order_type", payment.orderType);
            }
            if (discount != 0) {
                podMap.put("transaction_type", 1);
                podMap.put("discount_price", discount);
            } else {
                podMap.put("transaction_type", 4);
            }

            podMap.put("credit", payment.isCredit);

            if (mInvoiceNumber != null) {
                podMap.put("hyperme_invoice_number", mInvoiceNumber);
            }

            if (mTransport != null) {
                podMap.put("transport_id", mTransport.id);
            }
            if (qrResponse != null) {
                podMap.put("qr_code", qrResponse.sequenceNumber);
            }

            requestBody = PostRequest.getRequestBody(podMap);
        }

        Web.getInstance().getWebService().initPayment(requestBody).enqueue(new Callback<PaymentAuth>() {
            @Override
            public void onResponse(Call<PaymentAuth> call, Response<PaymentAuth> response) {
                Boolean success = Web.checkResponse(CreditPaymentDialog.this, call, response);
                if (success == null)
                    return;
                setLoading(false);

                if (success) {
                    Payment newPayment = new Payment();
                    if (payment != null) {
                        payment.paymentAuth = response.body();
                        newPayment = payment;
                    } else {
                        newPayment.paymentAuth = response.body();
                    }

                    if (!isSplitPayment) {
                        String cardDataRSA;
                        if (!selectedCard.isRaadCard() && clubAvailableAmount == 0)
                            cardDataRSA = RSAUtils.getCardDataRSA(newPayment, RaadApp.paygearCard, payGearPin, null);
                        else
                            cardDataRSA = RSAUtils.getCardDataRSA(newPayment, selectedCard, payGearPin, null);

                        startPay(cardDataRSA, newPayment.paymentAuth.token, null);
                    } else {
                        String cardDataRSA = RSAUtils.getSplitPaymentCardDataRSA(newPayment, RaadApp.paygearCard, payGearPin, null, mAmount - clubAvailableAmount);
                        String cardDataRSA2 = RSAUtils.getSplitPaymentCardDataRSA(newPayment, selectedCard, payGearPin, null, clubAvailableAmount);
                        startPay(cardDataRSA, newPayment.paymentAuth.token, cardDataRSA2);
                    }
                } else {
                    setLoading(false);
                }
            }

            @Override
            public void onFailure(Call<PaymentAuth> call, Throwable t) {
                if (Web.checkFailureResponse(CreditPaymentDialog.this, call, t)) {
                    setLoading(false);
                }
            }
        });

    }

    private void startPay(String encryptedCardData, String token, String encryptedCardData2) {
        setLoading(true);
        Map<String, String> finalInfoMap = new HashMap<>();
        finalInfoMap.put("token", token);
        finalInfoMap.put("card_info", encryptedCardData);
        if (encryptedCardData2 != null)
            finalInfoMap.put("card_info2", encryptedCardData2);
        Web.getInstance().getWebService().pay(PostRequest.getRequestBody(finalInfoMap)).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                Boolean success = Web.checkResponse(CreditPaymentDialog.this, call, response);
                if (success == null)
                    return;

                setLoading(false);
                if (success) {
                    PaymentResult paymentResult = response.body();

                    PaymentResultDialog dialog = PaymentResultDialog.newInstance(paymentResult);
                    dialog.setListener(v -> {
                        RaadApp.cards = null;
                        if (getActivity() instanceof NavigationBarActivity) {
                            ((NavigationBarActivity) getActivity()).broadcastMessage(
                                    CreditPaymentDialog.this, null, CardsFragment.class);
                            try {
                                ((FactorPaymentDialog) getActivity().getSupportFragmentManager().findFragmentByTag("FactorPaymentDialog")).dismiss();
                            } catch (Exception e) {

                            }
                        }
                        dismiss();
                    }, String.valueOf(new Theme().getPrimaryColor(getContext())));

                    dialog.show(getActivity().getSupportFragmentManager(), "PaymentSuccessDialog");


                } else {

                }

            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (Web.checkFailureResponse(CreditPaymentDialog.this, call, t)) {
                    setLoading(false);

                }
            }
        });

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        getDialog().setCanceledOnTouchOutside(false);
    }

    private void setLoading(boolean loading) {
        creditPaymentBinding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        creditPaymentBinding.button.setEnabled(!loading);
        creditPaymentBinding.button.setText(loading ? "" : getString(R.string.pay));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((NavigationBarActivity) getContext()).broadcastMessage(
                    CreditPaymentDialog.this, null, ScannerFragment.class);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("Visible", false);
            ((NavigationBarActivity) getContext()).broadcastMessage(
                    CreditPaymentDialog.this, bundle, ScannerFragment.class);
        }
    }
}
