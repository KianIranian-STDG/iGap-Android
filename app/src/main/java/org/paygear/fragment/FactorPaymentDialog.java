package org.paygear.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.databinding.FragmentDialogFactorPaymentBinding;

import org.paygear.RaadApp;
import org.paygear.model.AvailableClubs_Result;
import org.paygear.model.Card;
import org.paygear.model.Order;
import org.paygear.model.Payment;
import org.paygear.model.PaymentAuth;
import org.paygear.model.QRResponse;
import org.paygear.model.Transport;
import org.paygear.web.Web;
import org.paygear.widget.BankCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.radsense.raadcore.app.AlertDialog;
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

public class FactorPaymentDialog extends BottomSheetDialogFragment {
    Context context;
    View rootView;
    FragmentDialogFactorPaymentBinding factorPaymentBinding;
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
    private JellyToggleButton mCreditSwitch;
    private QRResponse qrResponse;
    private long amountToPay;
    private long discount = 0;
    private String mInvoiceNumber;


    public static FactorPaymentDialog newInstance(Account account, Order order, Transport transport, Product product, String orderId, boolean isServices, boolean isCashOutToWallet, String orderToken, String pubKey, String xAccessToken, long amount, QRResponse qrResponse, boolean isCredit, String invoiceNumber) {
        FactorPaymentDialog fragment = new FactorPaymentDialog();
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
        args.putString("InvoiceNumber", invoiceNumber);
        args.putLong("Amount", amount);
        args.putSerializable("QrResponse", qrResponse);
        args.putBoolean("IsCredit", isCredit);
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
            mInvoiceNumber=getArguments().getString("InvoiceNumber");
        }
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.fragment_dialog_factor_payment, container, false);
        context = inflater.getContext();
        factorPaymentBinding = FragmentDialogFactorPaymentBinding.bind(rootView);
        getDiscount();
        return rootView;
    }

    private void Initialize() {
        if (mTransport != null || mProduct != null) {
            factorPaymentBinding.name.setVisibility(View.VISIBLE);
            if (mTransport != null)
                factorPaymentBinding.name.setText(mTransport.name);
            if (mProduct != null)
                factorPaymentBinding.name.setText(mProduct.name);
        } else {
            factorPaymentBinding.name.setVisibility(View.GONE);
        }
        mCreditSwitch = rootView.findViewById(R.id.credit_switch);
        mCreditSwitch.setLeftText(getString(R.string.bank_card));
        mCreditSwitch.setRightText(getString(R.string.wallet_card));
        mCreditSwitch.setRightTextSize(RaadCommonUtils.getPx(10, getContext()));
        mCreditSwitch.setLeftTextSize(RaadCommonUtils.getPx(10, getContext()));
        mCreditSwitch.setBackgroundColor(Color.parseColor("#e3f2fd"));
        mCreditSwitch.setThumbColor(Color.parseColor("#2196f3"));
        mCreditSwitch.setRightTextColor(Color.parseColor("#9e9e9e"));
        mCreditSwitch.setLeftTextColor(Color.WHITE);
        mCreditSwitch.setChecked(isCredit);
        mCreditSwitch.setTextMarginBottom(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setTextMarginTop(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setTextMarginLeft(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setTextMarginRight(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setTextMarginCenter(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setBezierScaleRatioValue(1);

        factorPaymentBinding.title.setText(R.string.pay_to);
        if (mOrder == null) {
            Picasso.get().load(RaadCommonUtils.getImageUrl(mAccount.profilePicture))
                    .fit()
                    .into(factorPaymentBinding.image);
            factorPaymentBinding.subtitle.setText(mAccount.getName());
            factorPaymentBinding.totalAmount.setText(RaadCommonUtils.formatPrice(mAmount, false));
            factorPaymentBinding.discountAmount.setText(RaadCommonUtils.formatPrice(discount, false));
            factorPaymentBinding.amountToPay.setText(RaadCommonUtils.formatPrice(amountToPay, false));

        } else {
            Picasso.get().load(RaadCommonUtils.getImageUrl(mOrder.receiver.profilePicture))
                    .fit()
                    .into(factorPaymentBinding.image);
            factorPaymentBinding.subtitle.setText(mOrder.receiver.getName());
            factorPaymentBinding.totalAmount.setText(RaadCommonUtils.formatPrice(mOrder.amount, false));
            factorPaymentBinding.discountAmount.setText(RaadCommonUtils.formatPrice(mOrder.discountPrice, false));
            factorPaymentBinding.amountToPay.setText(RaadCommonUtils.formatPrice(mOrder.paidAmount, false));

        }
        if (isCredit){
            mCreditSwitch.setLeftTextColor(Color.parseColor("#9e9e9e"));
            mCreditSwitch.setRightTextColor(Color.WHITE);
            factorPaymentBinding.cardsBox.setVisibility(View.VISIBLE);
            factorPaymentBinding.button.setVisibility(View.GONE);
            showCards();
        }else {
            mCreditSwitch.setRightTextColor(Color.parseColor("#9e9e9e"));
            mCreditSwitch.setLeftTextColor(Color.WHITE);
            factorPaymentBinding.cardsBox.setVisibility(View.GONE);
            factorPaymentBinding.button.setVisibility(View.VISIBLE);
        }
        mCreditSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCreditSwitch.setLeftTextColor(Color.parseColor("#9e9e9e"));
                    mCreditSwitch.setRightTextColor(Color.WHITE);
                    factorPaymentBinding.cardsBox.setVisibility(View.VISIBLE);
                    factorPaymentBinding.button.setVisibility(View.GONE);
                    showCards();
                } else {
                    mCreditSwitch.setRightTextColor(Color.parseColor("#9e9e9e"));
                    mCreditSwitch.setLeftTextColor(Color.WHITE);
                    factorPaymentBinding.cardsBox.setVisibility(View.GONE);
                    factorPaymentBinding.button.setVisibility(View.VISIBLE);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity()!=null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            factorPaymentBinding.progress.setStatus(1);

                        }
                    });
                else {
                    try{
                        factorPaymentBinding.progress.setStatus(1);
                    }catch (Exception e){

                    }


                }

            }
        }, 1000);
        factorPaymentBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPay();
            }
        });

    }

    private void initPay() {
        setLoading(true);
        RequestBody requestBody;
        if (mOrder != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("token", mOrder.id);
            map.put("credit", mCreditSwitch.isChecked() && mOrder.amount <= RaadApp.paygearCard.balance);
            map.put("transaction_type", 4);
            requestBody = PostRequest.getRequestBody(map);
        } else {
            Map<String, Object> podMap = new HashMap<>();
            podMap.put("to", mAccount.id);
            podMap.put("amount", mAmount);
            podMap.put("credit", false);
            if (discount!=0){
                podMap.put("discount_price",discount);
            }
            podMap.put("transaction_type", 4);
            if (mTransport != null) {
                podMap.put("transport_id", mTransport.id);
            }
            if (qrResponse != null) {
                podMap.put("qr_code", qrResponse.sequenceNumber);
            }
            if (mInvoiceNumber!=null){
                podMap.put("hyperme_invoice_number",mInvoiceNumber);
            }
            requestBody = PostRequest.getRequestBody(podMap);
        }

        Web.getInstance().getWebService().initPayment(requestBody).enqueue(new Callback<PaymentAuth>() {
            @Override
            public void onResponse(Call<PaymentAuth> call, Response<PaymentAuth> response) {
                Boolean success = Web.checkResponse(FactorPaymentDialog.this, call, response);
                if (success == null)
                    return;
                setLoading(false);

                if (success) {
                    Payment newPayment = new Payment();

                    newPayment.paymentAuth = response.body();


                    if (newPayment.paymentAuth.IPGUrl != null && !newPayment.paymentAuth.IPGUrl.replaceAll(" ", "").equals("")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(newPayment.paymentAuth.IPGUrl));
                        startActivity(intent);
                    } else {
                        getActivity().onBackPressed();
                        ((NavigationBarActivity) getActivity()).replaceFragment(
                                CardsFragment.newInstance(newPayment), "CardsFragment", true);
                        dismiss();
                    }

                } else {
                    setLoading(false);
                }
            }

            @Override
            public void onFailure(Call<PaymentAuth> call, Throwable t) {
                if (Web.checkFailureResponse(FactorPaymentDialog.this, call, t)) {
                    setLoading(false);
                }
            }
        });

    }

    private void loadAvailableClubs() {
        Web.getInstance().getWebService().getAvailableClubs(mAccount.id, Auth.getCurrentAuth().getId()).enqueue(new Callback<List<AvailableClubs_Result>>() {
            @Override
            public void onResponse(Call<List<AvailableClubs_Result>> call, Response<List<AvailableClubs_Result>> response) {
                Boolean success = Web.checkResponse(FactorPaymentDialog.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mClubs = response.body();
                } else {
                }
                Initialize();
                showCards();
            }

            @Override
            public void onFailure(Call<List<AvailableClubs_Result>> call, Throwable t) {
                if (Web.checkFailureResponse(FactorPaymentDialog.this, call, t)) {
                    Initialize();
                }
            }
        });
    }

    private void showCards() {
        factorPaymentBinding.cards.removeAllViews();
        ArrayList<Card> cards = new ArrayList<>();
        if (mClubs == null) {
            if (RaadApp.selectedMerchant == null)
                cards.add(RaadApp.paygearCard);
            else if (merchantCard != null)
                cards.add(merchantCard);
        } else {
            cards.add(RaadApp.paygearCard);
            if (mAccount != null)
                if (mAccount.type == 0 && mClubs != null && mClubs.size() > 0 && RaadApp.cards != null) {
                    for (AvailableClubs_Result item : mClubs) {
                        for (Card clubCard : RaadApp.cards) {
                            if (item.getID().equals(clubCard.clubId)&&clubCard.balance>0) {
                                cards.add(clubCard);
                            }
                        }

                    }
                }

        }
        for (Card item : cards) {
            addCard(cards, cards.indexOf(item));

        }

    }

    private void addCard(final ArrayList<Card> mCards, final int position) {
        final Card card = mCards.get(position);
        Context context = getContext();
        int dp8 = RaadCommonUtils.getPx(8, context);
        int dp16 = RaadCommonUtils.getPx(16, context);

        int cardHeight = RaadCommonUtils.getPx(200, getContext());

        BankCardView cardView = new BankCardView(context);
        cardView.setId(position + 1);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, cardHeight);
        if (position > 0) {
            int collapsedDp = RaadCommonUtils.getPx(60, context);
            params.setMargins(dp16, -(cardHeight + dp16 - collapsedDp), dp16, dp16);
        } else {
            params.setMargins(dp16, 0, dp16, dp16);
        }
        cardView.setLayoutParams(params);
        cardView.setPreventCornerOverlap(false);
        cardView.setCardElevation(RaadCommonUtils.getPx(6 + (position * 6), context));
        factorPaymentBinding.cards.addView(cardView);
        cardView.setCard(card, position == mCards.size() - 1);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean clubHasMaximium=false;
                Boolean clubIsPercentage=false;
                Long avaialableAmount=0l;
                for (AvailableClubs_Result item:mClubs) {
                    if (item.getID().equals(card.clubId)){
                        if (item.getMerchant_max()!=null&&item.getMerchant_max()!=0){
                            clubHasMaximium=true;
                            clubIsPercentage=item.getMerchant_is_percentage();
                            avaialableAmount=item.getMerchant_max();
                        }else if(item.getMax()!=null&&item.getMax()!=0) {
                            clubHasMaximium=true;
                            clubIsPercentage=item.getIs_percentage();
                            avaialableAmount=item.getMax();
                        }else {
                            clubHasMaximium=false;
                            clubIsPercentage=false;
                            avaialableAmount=0l;
                        }
                    }
                    
                }
                if (RaadApp.paygearCard.isProtected) {
                    FactorPaymentDialog.this.dismiss();
                    if (getActivity() instanceof NavigationBarActivity)
                        CreditPaymentDialog.newInstance(mAccount, mOrder, mTransport, mProduct, mOrderId, isServices, isCashOutToWallet, orderToken, pubKey, xAccessToken, amountToPay, qrResponse, mCreditSwitch.isChecked(), card,discount,mInvoiceNumber,clubHasMaximium,clubIsPercentage,avaialableAmount).show(
                                getActivity().getSupportFragmentManager(), "CreditPaymentDialog");

                } else {
                    showSetPinConfirm(null);
                }
            }
        });
    }

    private void showSetPinConfirm(final Card merchantCard) {
        new AlertDialog()
                .setTitle(getString(R.string.paygear_card_pin))
                .setMessage(getString(R.string.credit_card_set_pin_confirm))
                .setPositiveAction(getString(R.string.yes))
                .setNegativeAction(getString(R.string.no))
                .setOnActionListener(new AlertDialog.OnAlertActionListener() {
                    @Override
                    public boolean onAction(int i, Object o) {
                        if (i == 1) {
                            dismiss();
                            ((NavigationBarActivity) getActivity()).pushFullFragment(
                                    merchantCard == null ? new SetCardPinFragment() : SetCardPinFragment.newInstance(merchantCard), "SetCardPinFragment");
                        }
                        return true;
                    }
                })
                .show(getActivity().getSupportFragmentManager());
    }

    private void getDiscount() {
//        Web.getInstance().getWebService().getDiscount().enqueue(new Callback<Discount_Result>() {
//            @Override
//            public void onResponse(Call<Discount_Result> call, Response<Discount_Result> response) {
//                Boolean success=Web.checkResponse(FactorPaymentDialog.this,call,response);
//                if(success==null)
//                    return;
//                if (success){
//                    discount=response.body().getDiscount();
//                }else {
//                    loadAvailableClubs();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Discount_Result> call, Throwable t) {
//                if (Web.checkFailureResponse(FactorPaymentDialog.this,call,t)){
//                    loadAvailableClubs();
//                }
//            }
//        });
        amountToPay=mAmount;
        if (mAccount.discountPercent != null &&mAccount.discountPercent!=0) {
            discount = (mAmount * mAccount.discountPercent) / 100;
            amountToPay = mAmount - discount;
        }
        else if (mAccount.discountValue!=null&&mAccount.discountValue!=0){
            discount=mAccount.discountValue;
            amountToPay=mAmount-discount;
        }
        loadAvailableClubs();
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
        factorPaymentBinding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        factorPaymentBinding.button.setEnabled(!loading);
        factorPaymentBinding.button.setText(loading ? "" : getString(R.string.pay));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((NavigationBarActivity) getContext()).broadcastMessage(
                    FactorPaymentDialog.this, null, ScannerFragment.class);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            Bundle bundle=new Bundle();
            bundle.putBoolean("Visible",false);
            ((NavigationBarActivity) getContext()).broadcastMessage(
                    FactorPaymentDialog.this, bundle, ScannerFragment.class);
        }
    }

}
