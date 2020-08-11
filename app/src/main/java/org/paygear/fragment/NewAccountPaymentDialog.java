package org.paygear.fragment;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nightonke.jellytogglebutton.JellyToggleButton;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;

import org.paygear.RaadApp;
import org.paygear.model.Card;
import org.paygear.model.Order;
import org.paygear.model.QRResponse;
import org.paygear.model.Transport;
import org.paygear.web.Web;

import java.util.Locale;

import ir.radsense.raadcore.app.AlertDialog;
import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.model.Account;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.model.Product;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.widget.ProgressLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAccountPaymentDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private ImageView mImageView;
    private TextView mTitleText;
    private TextView mSubtitleText;
    private TextView mNameText;
    private TextView mCountText;
    private EditText mPriceText;
    private LinearLayout mSwitchLayout;
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

    private boolean isCashOutToWallet = false;
    private Card merchantCard;
    private String mInvoiceNumber;

    public static NewAccountPaymentDialog newInstance(String accountId, String productId) {
        NewAccountPaymentDialog fragment = new NewAccountPaymentDialog();
        Bundle args = new Bundle();
        args.putString("AccountId", accountId);
        args.putString("ProductId", productId);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewAccountPaymentDialog newInstance(boolean isServices, String orderToken, String pubKey, String xAccessToken) {
        NewAccountPaymentDialog fragment = new NewAccountPaymentDialog();
        Bundle args = new Bundle();
        args.putBoolean("IsServices", isServices);
        args.putString("OrderToken", orderToken);
        args.putString("PubKey", pubKey);
        args.putString("XAccessToken", xAccessToken);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewAccountPaymentDialog newInstance(QRResponse qrResponse) {
        NewAccountPaymentDialog fragment = new NewAccountPaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable("QRResponse", qrResponse);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewAccountPaymentDialog newInstance(QRResponse qrResponse, long amount) {
        NewAccountPaymentDialog fragment = new NewAccountPaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable("QRResponse", qrResponse);
        args.putLong("Amount", amount);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewAccountPaymentDialog newInstance(QRResponse qrResponse, long amount,String invoiceNumber) {
        NewAccountPaymentDialog fragment = new NewAccountPaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable("QRResponse", qrResponse);
        args.putLong("Amount", amount);
        args.putString("InvoiceNumber",invoiceNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewAccountPaymentDialog newInstance(QRResponse qrResponse, long amount, boolean isCashoutToWallet, Card merchantPayGearCard) {
        NewAccountPaymentDialog fragment = new NewAccountPaymentDialog();
        Bundle args = new Bundle();
        args.putSerializable("QRResponse", qrResponse);
        args.putLong("Amount", amount);
        args.putBoolean("IsCashOutToWallet", isCashoutToWallet);
        args.putSerializable("MerchantCard", merchantPayGearCard);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewAccountPaymentDialog newInstance(String orderId) {
        NewAccountPaymentDialog fragment = new NewAccountPaymentDialog();
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
            mInvoiceNumber = getArguments().getString("InvoiceNumber");
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
        View view = inflater.inflate(R.layout.fragment_dialog_new_account_payment, container, false);


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

        button = view.findViewById(R.id.button);
        progressBar = view.findViewById(R.id.progress_bar);

        mSwitchLayout = view.findViewById(R.id.switch_layout);
        mCreditSwitch = view.findViewById(R.id.credit_switch);
        mCreditSwitch.setLeftText(getString(R.string.bank_card));
        mCreditSwitch.setRightText(getString(R.string.wallet_card));
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
        mCreditSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCreditSwitch.setLeftTextColor(Color.parseColor("#9e9e9e"));
                    mCreditSwitch.setRightTextColor(Color.WHITE);
                    button.setVisibility(View.VISIBLE);
                } else {
                    mCreditSwitch.setRightTextColor(Color.parseColor("#9e9e9e"));
                    mCreditSwitch.setLeftTextColor(Color.WHITE);
                    button.setVisibility(View.VISIBLE);
                }
            }
        });
        mCreditSwitch.setChecked(true, true);

        progress = view.findViewById(R.id.progress);
        progress.setOnRetryButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAccount();
            }
        });

        Typefaces.setTypeface(getContext(), Typefaces.IRAN_YEKAN_BOLD,
                mTitleText, mSubtitleText, mNameText, mCountText, unit, mPriceText, button);

        button.setOnClickListener(this);
        minus.setOnClickListener(this);
        plus.setOnClickListener(this);

        int[] attrs = new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = getActivity().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        typedArray.recycle();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        }

        return view;
    }

    private void loadSingleOrderId(final boolean isServices) {
        Web.getInstance().getWebService().getSingleOrder(Auth.getCurrentAuth().getId(), !isServices ? mOrderId : orderToken).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                Boolean success = Web.checkResponse(NewAccountPaymentDialog.this, call, response);
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
                if (Web.checkFailureResponse(NewAccountPaymentDialog.this, call, t)) {
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


    private void init() {
        getDialog().setCanceledOnTouchOutside(false);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button: {
                if (TextUtils.isEmpty(mPriceText.getText())) {
                    Toast.makeText(getContext(), getString(R.string.enter_info_completely), Toast.LENGTH_SHORT).show();
                    break;
                } else {
                    NewAccountPaymentDialog.this.dismiss();
                    if (getActivity() instanceof NavigationBarActivity)
                        FactorPaymentDialog.newInstance(mAccount, mOrder, mTransport, mProduct, mOrderId, isServices, isCashOutToWallet, orderToken, pubKey, xAccessToken,  Long.parseLong(mPriceText.getText().toString().replaceAll(",", "")),qrResponse,mCreditSwitch.isChecked(),mInvoiceNumber).show(
                                getActivity().getSupportFragmentManager(), "FactorPaymentDialog");

                    break;
                }
            }
            case R.id.minus:
                if (mCount > 1) {
                    mCount--;
                    updateValues(false);
                }
                break;
            case R.id.plus:
                mCount++;
                updateValues(false);
                break;
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
                    mSubtitleText.setText(mAccount.getName());
                    mNameText.setVisibility(View.GONE);
                } else {
                    mSubtitleText.setText(mName);
                    mNameText.setText(mAccount.getName());
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
                mNameText.setText(mAccount.getName());
                mCountText.setText(String.valueOf(mCount) + " " + getString(R.string.person));
                mPriceText.setText(RaadCommonUtils.formatPrice(mPrice * mCount, false));
                long price = Long.parseLong(mPriceText.getText().toString().replaceAll(",", ""));
            }

            if (mOrder == null) {
                Picasso.with(G.context)
                        .load(RaadCommonUtils.getImageUrl(mAccount.profilePicture))
                        .fit()
                        .into(mImageView);
            } else {
                Picasso.with(G.context)
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
            }
            if (mOrder == null) {
                Picasso.with(G.context)
                        .load(RaadCommonUtils.getImageUrl(mAccount.profilePicture))
                        .fit()
                        .into(mImageView);
            } else {
                Picasso.with(G.context)
                        .load(RaadCommonUtils.getImageUrl(mOrder.receiver.profilePicture))
                        .fit()
                        .into(mImageView);
            }

            progress.setStatus(1);
        }
        if (mInvoiceNumber!=null){
            mPriceLayout.setClickable(false);
            mPriceText.setClickable(false);
            mPriceText.setLongClickable(false);
            mPriceText.setEnabled(false);
        }


    }

    private void loadAccount() {
        Web.getInstance().getWebService().getAccountInfo(mAccountId, 1).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Boolean success = Web.checkResponse(NewAccountPaymentDialog.this, call, response);
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
                        updateValues(false);
                    }
                } else {
                    progress.setStatus(-1, getString(R.string.error));
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                if (Web.checkFailureResponse(NewAccountPaymentDialog.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });
    }


    private void loadTransport() {
        Web.getInstance().getWebService().getTransport(mProductId).enqueue(new Callback<Transport>() {
            @Override
            public void onResponse(Call<Transport> call, Response<Transport> response) {
                Boolean success = Web.checkResponse(NewAccountPaymentDialog.this, call, response);
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
                if (Web.checkFailureResponse(NewAccountPaymentDialog.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });
    }

    private void loadProduct() {
        Web.getInstance().getWebService().getProduct(mAccountId, mProductId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                Boolean success = Web.checkResponse(NewAccountPaymentDialog.this, call, response);
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
                if (Web.checkFailureResponse(NewAccountPaymentDialog.this, call, t)) {
                    progress.setStatus(-1, getString(R.string.network_error));
                }
            }
        });
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
    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            ((NavigationBarActivity) getContext()).broadcastMessage(
                    NewAccountPaymentDialog.this, null, ScannerFragment.class);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            Bundle bundle=new Bundle();
            bundle.putBoolean("Visible",false);
            ((NavigationBarActivity) getContext()).broadcastMessage(
                    NewAccountPaymentDialog.this, bundle, ScannerFragment.class);
        }
    }




}
