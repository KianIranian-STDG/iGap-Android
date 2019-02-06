package org.paygear.wallet.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.squareup.picasso.Picasso;


import org.paygear.wallet.R;
import org.paygear.wallet.RaadApp;
import org.paygear.wallet.model.AvailableClubs_Result;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.model.Order;
import org.paygear.wallet.model.Payment;
import org.paygear.wallet.model.PaymentAuth;
import org.paygear.wallet.model.PaymentResult;
import org.paygear.wallet.model.QRResponse;
import org.paygear.wallet.model.Transport;
import org.paygear.wallet.utils.RSAUtils;
import org.paygear.wallet.web.Web;
import org.paygear.wallet.widget.BankCardView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ir.radsense.raadcore.app.AlertDialog;
import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.model.Product;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.web.PostRequest;
import ir.radsense.raadcore.widget.ProgressLayout;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountPaymentDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private ImageView mImageView;
    private TextView mTitleText;
    private TextView mSubtitleText;
    private TextView mNameText;
    private TextView mCountText;
    private EditText mPriceText;
    private LinearLayout mSwitchLayout;
    private LinearLayout mCardsBox;
    private TextView mCardsTitle;
    private JellyToggleButton mCreditSwitch;
    private FrameLayout mCountLayout;
    private LinearLayout mPriceLayout;
    private ProgressLayout progress;
    private ProgressBar progressBar;
    private TextView button;


    private int mCount = 1;
    private String mAccountId;
    private String mProductId;
    private int qrType;

    private String mOrderId;
    private Order mOrder;

    private Account mAccount;
    private Product mProduct;
    private Transport mTransport;

    private String mName;
    private long mPrice;
    private QRResponse qrResponse;

    private boolean isServices = false;
    private String orderToken;
    private String pubKey;
    private String xAccessToken;

    public OnServicesPaymentResult onServicesPaymentResult;
    private boolean isCashOutToWallet = false;
    private Card merchantCard;
    private List<AvailableClubs_Result> mClubs;
    private LinearLayout cardsLayout;

    public static AccountPaymentDialog newInstance(String accountId, String productId) {
        AccountPaymentDialog fragment = new AccountPaymentDialog();
        Bundle args = new Bundle();
        args.putString("AccountId", accountId);
        args.putString("ProductId", productId);
        fragment.setArguments(args);
        return fragment;
    }

    public static AccountPaymentDialog newInstance(boolean isServices, String orderToken, String pubKey, String xAccessToken) {
        AccountPaymentDialog fragment = new AccountPaymentDialog();
        Bundle args = new Bundle();
        args.putBoolean("IsServices", isServices);
        args.putString("OrderToken", orderToken);
        args.putString("PubKey", pubKey);
        args.putString("XAccessToken", xAccessToken);
        fragment.setArguments(args);
        return fragment;
    }

    public static AccountPaymentDialog newInstance(QRResponse qrResponse) {
        AccountPaymentDialog fragment = new AccountPaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable("QRResponse", qrResponse);
        fragment.setArguments(args);
        return fragment;
    }

    public static AccountPaymentDialog newInstance(QRResponse qrResponse, long amount) {
        AccountPaymentDialog fragment = new AccountPaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable("QRResponse", qrResponse);
        args.putLong("Amount", amount);
        fragment.setArguments(args);
        return fragment;
    }

    public static AccountPaymentDialog newInstance(QRResponse qrResponse, long amount, boolean isCashoutToWallet, Card merchantPayGearCard) {
        AccountPaymentDialog fragment = new AccountPaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable("QRResponse", qrResponse);
        args.putLong("Amount", amount);
        args.putBoolean("IsCashOutToWallet", isCashoutToWallet);
        args.putSerializable("MerchantCard", merchantPayGearCard);
        fragment.setArguments(args);
        return fragment;
    }

    public static AccountPaymentDialog newInstance(String orderId) {
        AccountPaymentDialog fragment = new AccountPaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable("OrderId", orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccountId = getArguments().getString("AccountId");
            mProductId = getArguments().getString("ProductId");
            mPrice = getArguments().getLong("Amount");
            mOrderId = getArguments().getString("OrderId");

            orderToken = getArguments().getString("OrderToken");
            if (orderToken != null)
                mOrderId = orderToken;
            pubKey = getArguments().getString("PubKey");
            xAccessToken = getArguments().getString("XAccessToken");
            isServices = getArguments().getBoolean("IsServices", false);

            qrResponse = (QRResponse) getArguments().getSerializable("QRResponse");
            if (qrResponse != null) {
                mAccountId = qrResponse.accountId;
                mProductId = TextUtils.isEmpty(qrResponse.value) ? null : qrResponse.value;
                qrType = qrResponse.type;
            }
            isCashOutToWallet = getArguments().getBoolean("IsCashOutToWallet", false);
            merchantCard = (Card) getArguments().getSerializable("MerchantCard");
        }
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_dialog_account_payment, container, false);


        mTitleText = view.findViewById(R.id.title);
        mSubtitleText = view.findViewById(R.id.subtitle);
        mImageView = view.findViewById(R.id.image);
        mNameText = view.findViewById(R.id.name);

        mCountLayout = view.findViewById(R.id.count_layout);
        ImageView minus = view.findViewById(R.id.minus);
        ImageView plus = view.findViewById(R.id.plus);
        mCountText = view.findViewById(R.id.count);

        mPriceLayout = view.findViewById(R.id.price_layout);
        TextView unit = view.findViewById(R.id.unit);
        mPriceText = view.findViewById(R.id.price);

        mCardsTitle = view.findViewById(R.id.select_card_title);
        mCardsBox = view.findViewById(R.id.cards_box);
        cardsLayout = view.findViewById(R.id.cards);

        button = view.findViewById(R.id.button);
        progressBar = view.findViewById(R.id.progress_bar);

        mSwitchLayout = view.findViewById(R.id.switch_layout);
        mCreditSwitch = view.findViewById(R.id.credit_switch);
        mCreditSwitch.setLeftText("کارت بانکی");
        mCreditSwitch.setRightText("کیف پول");
        mCreditSwitch.setRightTextSize(RaadCommonUtils.getPx(10, getContext()));
        mCreditSwitch.setLeftTextSize(RaadCommonUtils.getPx(10, getContext()));
        mCreditSwitch.setBackgroundColor(Color.parseColor("#e3f2fd"));
        mCreditSwitch.setThumbColor(Color.parseColor("#2196f3"));
        mCreditSwitch.setRightTextColor(Color.parseColor("#9e9e9e"));
        mCreditSwitch.setLeftTextColor(Color.WHITE);
        mCreditSwitch.setTextMarginBottom(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setTextMarginTop(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setTextMarginLeft(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setTextMarginRight(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setTextMarginCenter(RaadCommonUtils.getPx(6, getContext()));
        mCreditSwitch.setBezierScaleRatioValue(1);
        ;

        progress = view.findViewById(R.id.progress);
        progress.setOnRetryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAccount();
            }
        });

        Typefaces.setTypeface(getContext(), Typefaces.IRAN_MEDIUM,
                mTitleText, mSubtitleText, mNameText, mCountText, unit, mPriceText, button, mCardsTitle);

        button.setOnClickListener(this);
        minus.setOnClickListener(this);
        plus.setOnClickListener(this);

        int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            minus.setClickable(true);
            plus.setClickable(true);
            minus.setFocusable(true);
            plus.setFocusable(true);
            minus.setBackgroundResource(backgroundResource);
            plus.setBackgroundResource(backgroundResource);

        } else {
            plus.setBackgroundResource(R.color.zxing_transparent);
            minus.setBackgroundResource(R.color.zxing_transparent);
        }
        mPriceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPriceText.setEnabled(true);
                mPriceText.setClickable(true);
                mPriceText.setLongClickable(true);
                if (mTransport != null) {
                    mTransport.type = Transport.TYPE_GARDESHI;
                }
                updateValues(false);
            }
        });


        ViewCompat.setBackground(mCountText, RaadCommonUtils.getRectShape(getContext(),
                R.color.tip_text_back, 28, 1));
        if (mOrderId == null)
            loadAccount();
        else {
            loadSingleOrderId(isServices);
        }

        if (RaadApp.selectedMerchant == null) {
            if (!RaadApp.paygearCard.isProtected)
                showSetPinConfirm(null, null);
        } else {
            mCreditSwitch.setVisibility(View.GONE);
//            mCreditSwitch.setChecked(true);

//            if (merchantCard!=null&&!merchantCard.isProtected);
//                showSetPinConfirm(merchantCard);
        }
        mCreditSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCreditSwitch.setLeftTextColor(Color.parseColor("#9e9e9e"));
                    mCreditSwitch.setRightTextColor(Color.WHITE);
                    mCardsBox.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);
                    showCards();
                } else {
                    mCreditSwitch.setRightTextColor(Color.parseColor("#9e9e9e"));
                    mCreditSwitch.setLeftTextColor(Color.WHITE);
                    mCardsBox.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);
                }
            }
        });
        mCreditSwitch.setChecked(true, true);
        return view;
    }

    private void loadSingleOrderId(final boolean isServices) {
        Web.getInstance().getWebService().getSingleOrder(Auth.getCurrentAuth().getId(), !isServices ? mOrderId : orderToken).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                Boolean success = Web.checkResponse(AccountPaymentDialog.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mOrder = response.body();
                    updateValues(isServices);
                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                if (Web.checkFailureResponse(AccountPaymentDialog.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("AccountPaymentDialog", "onDestroy: ");
        if (getActivity() != null) {
            ((NavigationBarActivity) getContext()).broadcastMessage(
                    AccountPaymentDialog.this, null, ScannerFragment.class);
        }
    }

    private void init() {
        getDialog().setCanceledOnTouchOutside(false);
//
        //getDialog().setCancelable(false);

//        Window window = getDialog().getWindow();
//        window.setBackgroundDrawable(RaadCommonUtils.getRectShape(getContext(), android.R.color.white, 8, 0));
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(window.getAttributes());
////        lp.width = RaadCommonUtils.getPx(280, getActivity());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        window.setAttributes(lp);

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button) {
            if (mOrder == null) {
                if (TextUtils.isEmpty(mPriceText.getText()))
                    return;

                Payment payment = new Payment();
                Account account = new Account();
                account.id = mAccount.id;
                payment.account = account;
                payment.price = Long.parseLong(mPriceText.getText().toString().replaceAll(",", ""));
                payment.isCredit = mCreditSwitch.isChecked();
                if (mCreditSwitch.isChecked()) {
                    if (merchantCard == null) {
                        if (RaadApp.paygearCard.isProtected)
                            showPinConfirm(payment, null);
                        else
                            showSetPinConfirm(null, null);
                    } else {
                        if (merchantCard.isProtected)
                            showPinConfirm(null, null);
                        else
                            showSetPinConfirm(merchantCard, null);
                    }
                } else {
                    initPay(payment, null, null);
                }

            } else {
                if (mCreditSwitch.isChecked()) {
                    if (merchantCard == null) {
                        if (RaadApp.paygearCard.isProtected)
                            showPinConfirm(null, null);
                        else
                            showSetPinConfirm(null, null);
                    } else {
                        if (merchantCard.isProtected)
                            showPinConfirm(null, null);
                        else
                            showSetPinConfirm(merchantCard, null);
                    }
                } else {
                    initPay(null, null, null);
                }
            }

            if (mCount > 1) {
                mCount--;
                updateValues(false);
            }

        } else if (id == R.id.minus) {
            if (mCount > 1) {
                mCount--;
                updateValues(false);
            }

        } else if (id == R.id.plus) {
            mCount++;
            updateValues(false);

        }
    }


    private void updateValues(boolean isServices) {
        if (!isServices) {
            if (mOrder != null) {
                mPriceText.setClickable(false);
                mPriceText.setLongClickable(false);
                mPriceText.setEnabled(false);
                mTitleText.setText(R.string.pay_to);
                mSubtitleText.setText(mOrder.receiver.name);
                mNameText.setVisibility(View.GONE);
                mCountLayout.setVisibility(View.GONE);
                String s = null;
                try {
                    s = String.format(Locale.US, "%,d", mOrder.amount);
                } catch (NumberFormatException e) {
                }
                mPriceText.setText(s);
            } else if (mProductId == null ||
                    (mTransport != null && (mTransport.type == Transport.TYPE_GARDESHI || mTransport.type == Transport.TYPE_AGENCY))) {

                if (mProductId == null) {
                    mTitleText.setText(R.string.pay_to);
                    mSubtitleText.setText(mAccount.name);
                    mNameText.setVisibility(View.GONE);
                } else {
                    mSubtitleText.setText(mName);
                    mNameText.setText(mAccount.name);
                }

                mCountLayout.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, RaadCommonUtils.getPx(48, getContext()));
                int dp16 = RaadCommonUtils.getPx(16, getContext());
                params.setMargins(dp16, dp16, dp16, 0);
                mPriceLayout.setLayoutParams(params);
                if (mPrice != 0) {
                    mPriceText.setText(RaadCommonUtils.formatPrice(mPrice, false));
                }

                mPriceText.addTextChangedListener(new TextWatcher() {
                    boolean isSettingText;
                    long price;

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > 0) {
                            price = Long.parseLong(charSequence.toString().replaceAll(",", ""));
                            mPrice = Long.parseLong(charSequence.toString().replaceAll(",", ""));
                        } else
                            price = 0;
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (isSettingText) return;
                        isSettingText = true;
                        String s = null;
                        try {
                            s = String.format(Locale.US, "%,d", price);
                        } catch (NumberFormatException e) {
                        }
                        mPriceText.setText(s);
                        mPriceText.setSelection(mPriceText.length());
                        isSettingText = false;
                    }
                });
            } else {
                mPriceText.setClickable(false);
                mPriceText.setLongClickable(false);
                mPriceText.setEnabled(false);
                mSubtitleText.setText(mName);
                mNameText.setText(mAccount.name);
                //mCountText.setText(getString(R.string.x_people).replace("*", String.valueOf(mCount)));
                mCountText.setText(String.valueOf(mCount) + " " + getString(R.string.person));
                mPriceText.setText(RaadCommonUtils.formatPrice(mPrice * mCount, false));
                long price = Long.parseLong(mPriceText.getText().toString().replaceAll(",", ""));
//                mCreditSwitch.setChecked(price <= RaadApp.paygearCard.balance);
//                mCreditSwitch.setEnabled(price <= RaadApp.paygearCard.balance);
            }

            if (mOrder == null) {
                Picasso.get()
                        .load(RaadCommonUtils.getImageUrl(mAccount.profilePicture))
                        .fit()
                        .into(mImageView);
            } else {
                Picasso.get()
                        .load(RaadCommonUtils.getImageUrl(mOrder.receiver.profilePicture))
                        .fit()
                        .into(mImageView);
            }

            progress.setStatus(1);

        } else {
            if (mOrder != null) {
                mPriceLayout.setClickable(false);
                mPriceText.setClickable(false);
                mPriceText.setLongClickable(false);
                mPriceText.setEnabled(false);
                mTitleText.setText(R.string.pay_to);
                mSubtitleText.setText(mOrder.receiver.name);
                mNameText.setVisibility(View.GONE);
                mCountLayout.setVisibility(View.GONE);
                String s = null;
                try {
                    s = String.format(Locale.US, "%,d", mOrder.amount);
                } catch (NumberFormatException e) {
                }
                mPriceText.setText(s);
//                mCreditSwitch.setEnabled(false);
//                mCreditSwitch.setChecked(mOrder.amount <= RaadApp.paygearCard.balance);
//                mCreditSwitch.setEnabled(mOrder.amount <= RaadApp.paygearCard.balance);
            }
            if (mOrder == null) {
                Picasso.get()
                        .load(RaadCommonUtils.getImageUrl(mAccount.profilePicture))
                        .fit()
                        .into(mImageView);
            } else {
                Picasso.get()
                        .load(RaadCommonUtils.getImageUrl(mOrder.receiver.profilePicture))
                        .fit()
                        .into(mImageView);
            }

            progress.setStatus(1);
        }

    }

    private void showCards() {
        cardsLayout.removeAllViews();
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
                            if (item.getID().equals(clubCard.clubId)) {
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

    private void addCard(ArrayList<Card> mCards, int position) {
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
            //int dp160 = RaadCommonUtils.getPx(160, context);
            int collapsedDp = RaadCommonUtils.getPx(60, context);
            params.setMargins(dp16, -(cardHeight + dp16 - collapsedDp), dp16, dp16);
        } else {
            params.setMargins(dp16, 0, dp16, dp16);
        }
        cardView.setLayoutParams(params);
        cardView.setPreventCornerOverlap(false);
        cardView.setCardElevation(RaadCommonUtils.getPx(6 + (position * 6), context));
        cardsLayout.addView(cardView);
//        viewItems.add(cardView);
        cardView.setCard(card, position == mCards.size() - 1);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isServices) {
                    Payment payment = new Payment();
                    Account account = new Account();
                    if (mAccount != null)
                        account.id = mAccount.id;
                    payment.account = account;
                    try {
                        payment.price = Long.parseLong(mPriceText.getText().toString().replaceAll(",", ""));
                        payment.isCredit = mCreditSwitch.isChecked();
                        if (mPrice <= card.balance) {
                            if (RaadApp.paygearCard.isProtected)
                                showPinConfirm(payment, card);
                            else
                                showSetPinConfirm(null, null);
                        } else {
                            Toast.makeText(getContext(), R.string.club_balance_not_enough, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

                    }
                } else {
                    try {
                        mOrder.amount = Long.parseLong(mPriceText.getText().toString().replaceAll(",", ""));
                        if (mOrder.amount <= card.balance) {
                            if (RaadApp.paygearCard.isProtected)
                                showPinConfirm(null, card);
                            else
                                showSetPinConfirm(null, null);
                        } else {
                            Toast.makeText(getContext(), R.string.club_balance_not_enough, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {

                    }
                }

            }
        });
    }

//    private void showClubsCard(List<Card> availableClubCards) {
//        if (availableClubCards != null && availableClubCards.size() > 0) {
//            mClubBox.setVisibility(View.VISIBLE);
//            mClubRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//            AvailableClubsAdapter availableClubsAdapter = new AvailableClubsAdapter(getActivity(), availableClubCards);
//            availableClubsAdapter.setItemClickListener(new AvailableClubsAdapter.ItemClickListener() {
//                @Override
//                public void itemClickListener(Card clubCard, int position) {
//
//                }
//            });
//            if (mCreditSwitch.isChecked()) {
//                mClubBox.setVisibility(View.VISIBLE);
//                mClubRecycler.setAdapter(availableClubsAdapter);
//
//                availableClubsAdapter.notifyDataSetChanged();
//            } else {
//                mClubBox.setVisibility(View.GONE);
//            }
//        } else {
//            mClubBox.setVisibility(View.GONE);
//        }
//    }


    private void loadAccount() {
        Web.getInstance().getWebService().getAccountInfo(mAccountId, 1).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Boolean success = Web.checkResponse(AccountPaymentDialog.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mAccount = response.body();
                    if (mProductId != null) {
                        if (qrType == QRResponse.QR_TYPE_TAXI)
                            loadTransport();
                        else
                            loadProduct();
                    } else {
                        if (mAccount.type == 0) {
                            loadAvailableClubs();
                        } else {
                            updateValues(false);
                        }
                    }
                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                if (Web.checkFailureResponse(AccountPaymentDialog.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });
    }

    private void loadAvailableClubs() {
        Web.getInstance().getWebService().getAvailableClubs(mAccountId, Auth.getCurrentAuth().getId()).enqueue(new Callback<List<AvailableClubs_Result>>() {
            @Override
            public void onResponse(Call<List<AvailableClubs_Result>> call, Response<List<AvailableClubs_Result>> response) {
                Boolean success = Web.checkResponse(AccountPaymentDialog.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mClubs = response.body();
                } else {

                }
                updateValues(false);
                showCards();
            }

            @Override
            public void onFailure(Call<List<AvailableClubs_Result>> call, Throwable t) {
                if (Web.checkFailureResponse(AccountPaymentDialog.this, call, t)) {
                    updateValues(false);
                }
            }
        });
    }

    private void loadTransport() {
        Web.getInstance().getWebService().getTransport(mProductId).enqueue(new Callback<Transport>() {
            @Override
            public void onResponse(Call<Transport> call, Response<Transport> response) {
                Boolean success = Web.checkResponse(AccountPaymentDialog.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mTransport = response.body();
                    mName = mTransport.name;
                    mPrice = mTransport.value;
                    updateValues(false);
                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Transport> call, Throwable t) {
                if (Web.checkFailureResponse(AccountPaymentDialog.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });
    }

    private void loadProduct() {
        Web.getInstance().getWebService().getProduct(mAccountId, mProductId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                Boolean success = Web.checkResponse(AccountPaymentDialog.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    mProduct = response.body();
                    mName = mProduct.name;
                    mPrice = mProduct.price;
                    updateValues(false);
                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                if (Web.checkFailureResponse(AccountPaymentDialog.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });
    }

    private void initPay(final Payment payment, final String payGearPin, final Card clubCard) {
        setLoading(true);
        RequestBody requestBody;
        if (merchantCard == null & RaadApp.selectedMerchant == null) {
            if (payment == null && mOrder != null) {
                Map<String, String> podMap = null;

                Map<String, Object> map = new HashMap<>();
                map.put("token", mOrder.id);
                map.put("credit", mCreditSwitch.isChecked() && mOrder.amount <= RaadApp.paygearCard.balance);
                map.put("transaction_type", 4);

                requestBody = PostRequest.getRequestBody(map);
            } else {


                Map<String, Object> podMap = new HashMap<>();
                podMap.put("to", payment.account.id);
                podMap.put("amount", payment.getPaymentPrice());

                /**
                 * # peyman
                 * this url must be use to redirect to our app done
                 *
                 *  podMap.put("callback_url","our i gap url);
                 */
                //         podMap.put("callback_url", "");

                if (payment.orderType > -1) {
                    //map.put("pre_order", true);
                    podMap.put("order_type", payment.orderType);
                }

                podMap.put("credit", payment.isCredit);

                podMap.put("transaction_type", 4);
                if (mTransport != null) {
                    podMap.put("transport_id", mTransport.id);
                }
                if (qrResponse != null) {
                    podMap.put("qr_code", qrResponse.sequenceNumber);
                }

                requestBody = PostRequest.getRequestBody(podMap);
            }
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("to", Auth.getCurrentAuth().getId());
            map.put("from", RaadApp.selectedMerchant.get_id());
            map.put("amount", mPrice);
            map.put("credit", true);
            requestBody = PostRequest.getRequestBody(map);
        }
        Web.getInstance().getWebService().initPayment(requestBody).enqueue(new Callback<PaymentAuth>() {
            @Override
            public void onResponse(Call<PaymentAuth> call, Response<PaymentAuth> response) {
                Boolean success = Web.checkResponse(AccountPaymentDialog.this, call, response);
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

                    if (mCreditSwitch.isChecked()) {
                        if (merchantCard == null) {
                            String cardDataRSA = RSAUtils.getCardDataRSA(newPayment, clubCard == null ? RaadApp.paygearCard : clubCard, payGearPin, null);
                            startPay(cardDataRSA, newPayment.paymentAuth.token);
                        } else {
                            String cardDataRSA = RSAUtils.getCardDataRSA(newPayment, merchantCard, payGearPin, null);
                            startPay(cardDataRSA, newPayment.paymentAuth.token);
                        }
                    } else {
                        if (newPayment.paymentAuth.IPGUrl != null && !newPayment.paymentAuth.IPGUrl.replaceAll(" ", "").equals("")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(newPayment.paymentAuth.IPGUrl));
                            startActivity(intent);
                        } else {
                            ((NavigationBarActivity) getActivity()).replaceFragment(
                                    CardsFragment.newInstance(newPayment), "CardsFragment", true);
                            dismiss();
                        }
                    }
                } else {
                    setLoading(false);
                }
            }

            @Override
            public void onFailure(Call<PaymentAuth> call, Throwable t) {
                if (Web.checkFailureResponse(AccountPaymentDialog.this, call, t)) {
                    setLoading(false);
                }
            }
        });

    }

    private void startPay(String encryptedCardData, String token) {
        setLoading(true);

        Map<String, String> finalInfoMap = new HashMap<>();
        finalInfoMap.put("token", token);
        finalInfoMap.put("card_info", encryptedCardData);

        Web.getInstance().getWebService().pay(PostRequest.getRequestBody(finalInfoMap)).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                Boolean success = Web.checkResponse(AccountPaymentDialog.this, call, response);
                if (success == null)
                    return;

                setLoading(false);
                if (success) {
                    PaymentResult paymentResult = response.body();
                    if (isServices) {

                        if (onServicesPaymentResult != null)
                            onServicesPaymentResult.onServicesPaymentResult(paymentResult, true);
                        dismiss();
                    } else {
                        PaymentResultDialog dialog = PaymentResultDialog.newInstance(paymentResult);
                        dialog.setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RaadApp.cards = null;
                                if (getActivity() instanceof NavigationBarActivity) {
                                    ((NavigationBarActivity) getActivity()).broadcastMessage(
                                            AccountPaymentDialog.this, null, CardsFragment.class);
                                }
                                if (isCashOutToWallet)
                                    getActivity().getSupportFragmentManager().popBackStack();

                                //String frag = "PaymentEntryFragment";
                                //getActivity().getSupportFragmentManager().popBackStack(null,
                                //        FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                dismiss();
                            }
                        }, "");
                        dialog.show(getActivity().getSupportFragmentManager(), "PaymentSuccessDialog");
                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (Web.checkFailureResponse(AccountPaymentDialog.this, call, t)) {
                    setLoading(false);

                }
            }
        });

    }

    private void showPinConfirm(final Payment payment, final Card clubCard) {
        new AlertDialog()
                .setMode(AlertDialog.MODE_INPUT)
                .setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                .setTitle(getString(R.string.paygear_card_pin) + (merchantCard == null ? "" : RaadApp.selectedMerchant.getName()))
                .setPositiveAction(getString(R.string.ok))
                .setNegativeAction(getString(R.string.cancel))
                .setOnActionListener(new AlertDialog.OnAlertActionListener() {
                    @Override
                    public boolean onAction(int i, Object o) {
                        if (i == 1) {
                            String pin = (String) o;
                            if (pin != null)
                                if (!TextUtils.isEmpty(pin.trim())) {
                                    initPay(payment, pin, clubCard);
                                }
                        }
                        return true;
                    }
                }).show(getActivity().getSupportFragmentManager());
    }


    private void showSetPinConfirm(final Card merchantCard, final Card clubCard) {
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


    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        button.setEnabled(!loading);
        button.setText(loading ? "" : getString(R.string.ok));
    }


    public interface OnServicesPaymentResult {
        void onServicesPaymentResult(PaymentResult paymentResult, boolean isSuccess);
    }

    public void setPaymentResultListener(OnServicesPaymentResult onServicesPaymentResult) {
        this.onServicesPaymentResult = onServicesPaymentResult;
    }

}
