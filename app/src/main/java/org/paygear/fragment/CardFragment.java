package org.paygear.fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;

import org.paygear.WalletActivity;
import org.paygear.model.Card;
import org.paygear.model.Payment;
import org.paygear.model.PaymentResult;
import org.paygear.utils.BankUtils;
import org.paygear.utils.RSAUtils;
import org.paygear.utils.Utils;
import org.paygear.web.Web;
import org.paygear.widget.BankCardView;

import java.util.HashMap;
import java.util.Map;

import ir.radsense.raadcore.app.NavigationBarActivity;
import ir.radsense.raadcore.utils.RaadCommonUtils;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.web.PostRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CardFragment extends Fragment {

    private BankCardView cardView;
    private SwitchCompat defaultCardSwitch;
    private TextView button;
    private TextView balance;
    private ProgressBar progressBar;
    private ProgressBar defaultCardProgress;

    private EditText pinText;
    private EditText cvv2Text;

    private Card mCard;
    private Payment mPayment;

    private boolean isUpdating;
    private Button availableMerchants;

    public CardFragment() {
    }

    public static CardFragment newInstance(Card card) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putSerializable("Card", card);
        fragment.setArguments(args);
        return fragment;
    }

    public static CardFragment newInstance(Payment payment, Card card) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putSerializable("Payment", payment);
        args.putSerializable("Card", card);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPayment = (Payment) getArguments().getSerializable("Payment");
            mCard = (Card) getArguments().getSerializable("Card");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.payment))
                .setRightIcons(R.string.icon_QR_code)
                .setLeftIcon(R.string.icon_back)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null)
                            getActivity().onBackPressed();
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        ((NavigationBarActivity) getActivity()).pushFullFragment(
                                new ScannerFragment(), "ScannerFragment");
                    }
                });

        ViewGroup layoutToolbar = view.findViewById(R.id.fc_layout_toolbar);
        layoutToolbar.addView(toolbar.getView());

        if (mPayment != null) {
            toolbar.setDefaultTitle(getString(R.string.payment));
        } else {
            if (mCard.bankCode != 69)
                toolbar.setDefaultTitle(BankUtils.getBank(getContext(), mCard.bankCode).getName(getActivity()));
            else
                toolbar.setDefaultTitle(mCard.cardNumber);
        }

        cardView = view.findViewById(R.id.card_view);
        ViewGroup rootCarView = view.findViewById(R.id.rootCardView);
        rootCarView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));
        TextView defaultCardTitle = view.findViewById(R.id.default_card_title);
        defaultCardSwitch = view.findViewById(R.id.default_card_switch);
        button = view.findViewById(R.id.button);
        Drawable mDrawableSkip = ContextCompat.getDrawable(getContext(), R.drawable.button_green_selector_24dp);
        if (mDrawableSkip != null) {
            if (WalletActivity.isDarkTheme) {
                mDrawableSkip.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.backgroundTheme_2), PorterDuff.Mode.SRC_IN));
            } else {
                mDrawableSkip.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(mDrawableSkip);
            }
        }
        balance = view.findViewById(R.id.balance);
        progressBar = view.findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(WalletActivity.progressColorWhite), PorterDuff.Mode.SRC_IN);
        defaultCardProgress = view.findViewById(R.id.default_card_progress);
        defaultCardProgress.getIndeterminateDrawable().setColorFilter(Color.parseColor(WalletActivity.progressColorWhite), PorterDuff.Mode.SRC_IN);
        availableMerchants = view.findViewById(R.id.available_merchants);

        TextView paymentPriceText = view.findViewById(R.id.payment_price);

        TextView pinTitle = view.findViewById(R.id.pin_title);
        TextView cvv2Title = view.findViewById(R.id.cvv2_title);
        pinText = view.findViewById(R.id.pin);
        cvv2Text = view.findViewById(R.id.cvv2);

        if (WalletActivity.isDarkTheme) {
            cvv2Text.setHintTextColor(Color.parseColor(WalletActivity.textSubTheme));
            cvv2Text.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            pinText.setHintTextColor(Color.parseColor(WalletActivity.textSubTheme));
            pinText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            defaultCardTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            pinTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            cvv2Title.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            paymentPriceText.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        }

        Typefaces.setTypeface(getContext(), Typefaces.IRAN_MEDIUM, defaultCardTitle, button,
                pinTitle, cvv2Title, paymentPriceText, balance);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_MEDIUM, pinText, cvv2Text, availableMerchants);
        int cardHeight = BankCardView.getDefaultCardHeight(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, cardHeight);
        int dp16 = RaadCommonUtils.getPx(16, getContext());
        params.setMargins(dp16, dp16, dp16, dp16);
        cardView.setLayoutParams(params);
        if (mCard.bankCode == 69 && !mCard.isRaadCard()) {
            balance.setVisibility(View.VISIBLE);
            balance.setText(getString(R.string.balance_title) + " " + RaadCommonUtils.formatPrice(mCard.balance, true));
            button.setVisibility(View.GONE);
        } else {
            balance.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPayment != null) {
                    startPayProcess();
                } else {
                    showDeleteConfirm();
                }
            }
        });


        if (mPayment != null) {
            paymentPriceText.setVisibility(View.VISIBLE);
            availableMerchants.setVisibility(View.GONE);
            paymentPriceText.setText(getString(R.string.pay_with_price_x)
                    .replace("*", RaadCommonUtils.formatPrice(mPayment.getPaymentPrice(), true)));
            view.findViewById(R.id.pin_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.switch_layout).setVisibility(View.GONE);

//            if (mPayment.getPaymentPrice() <= Payment.MAX_PRICE_CVV2&&mPayment.getPaymentPrice()>=Payment.MIN_PRICE_CVV2) {
//            cvv2Title.setVisibility(View.GONE);
//            cvv2Text.setVisibility(View.GONE);
//            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(mDrawableSkip);
            }
            button.setText(R.string.pay);
            button.setBackgroundResource(R.drawable.button_green_selector_24dp);

        } else {
            button.setText(R.string.delete_card);
            button.setBackgroundColor(Color.parseColor(WalletActivity.primaryColor));
            button.setTextColor(Color.WHITE);
            //ViewCompat.setBackground(button, RaadCommonUtils.getSelector(getContext(),
            //        R.color.remove_card_button_normal, R.color.remove_card_button_selected, 0, 24, 0));
            defaultCardSwitch.setChecked(mCard.isDefault);
            defaultCardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (!isUpdating)
                        updateCard();
                }
            });
            if (mCard.clubId != null) {
                availableMerchants.setVisibility(View.GONE);
                availableMerchants.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        ((NavigationBarActivity) getActivity()).pushFullFragment(
//                                AvailableMerchantsFragment.newInstance(mCard.clubId),
//                                "AvailableMerchantsFragment");
                    }
                });
            } else {
                availableMerchants.setVisibility(View.GONE);
            }
        }

        cardView.setCard(mCard, true);
        if (mCard.clubId!=null)
            view.findViewById(R.id.switch_layout).setVisibility(View.GONE);
        else
            view.findViewById(R.id.switch_layout).setVisibility(View.VISIBLE);
        return view;
    }

    private void updateCard() {
        if (isUpdating)
            return;
        isUpdating = true;
        defaultCardProgress.setVisibility(View.VISIBLE);
        defaultCardSwitch.setEnabled(false);

        Map<String, Object> map = new HashMap<>();
        map.put("default", defaultCardSwitch.isChecked());

        Web.getInstance().getWebService().updateCard(mCard.token, PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(CardFragment.this, call, response);
                if (success == null)
                    return;

                defaultCardProgress.setVisibility(View.GONE);
                defaultCardSwitch.setEnabled(true);

                if (response.isSuccessful()) {
                    ((NavigationBarActivity) getActivity()).broadcastMessage(
                            CardFragment.this, null, CardsFragment.class);
                    //getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    defaultCardSwitch.setChecked(!defaultCardSwitch.isChecked());
                }
                isUpdating = false;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(CardFragment.this, call, t)) {
                    defaultCardProgress.setVisibility(View.GONE);
                    defaultCardSwitch.setChecked(!defaultCardSwitch.isChecked());
                    defaultCardSwitch.setEnabled(true);
                    isUpdating = false;
                }
            }
        });
    }

    private void showDeleteConfirm() {

        new MaterialDialog.Builder(getActivity())
                .title(R.string.delete_card)
                .content(R.string.delete_card_confirm)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        removeCard();
                    }
                })
                .show();
    }

    private void removeCard() {
        setLoading(true);
        Web.getInstance().getWebService().deleteCard(mCard.token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(CardFragment.this, call, response);
                if (success == null)
                    return;

                setLoading(false);

                if (success) {
                    ((NavigationBarActivity) getActivity()).broadcastMessage(
                            CardFragment.this, null, CardsFragment.class);
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(CardFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void startPayProcess() {
        String pin2 = pinText.getText().toString();
        if (pin2.length() == 0) {
            Toast.makeText(getActivity(), R.string.enter_your_second_pin, Toast.LENGTH_SHORT).show();
            return;
        }

        String cvv2 = null;
        if (cvv2Text.getVisibility() == View.VISIBLE) {
            cvv2 = cvv2Text.getText().toString();
            if (cvv2.length() == 0) {
                Toast.makeText(getActivity(), R.string.enter_cvv2, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Utils.hideKeyboard(getContext(), pinText);
        startPay(RSAUtils.getCardDataRSA(mPayment, mCard, pin2, cvv2));
    }


    private void startPay(String encryptedCardData) {
        setLoading(true);

        Map<String, String> finalInfoMap = new HashMap<>();
        finalInfoMap.put("token", mPayment.paymentAuth.token);
        finalInfoMap.put("card_info", encryptedCardData);

        Web.getInstance().getWebService().pay(PostRequest.getRequestBody(finalInfoMap)).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, Response<PaymentResult> response) {
                Boolean success = Web.checkResponse(CardFragment.this, call, response);
                if (success == null)
                    return;

                setLoading(false);
                if (success) {
                    PaymentResult paymentResult = response.body();

                    PaymentResultDialog dialog = PaymentResultDialog.newInstance(paymentResult);
                    dialog.setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (getActivity() instanceof NavigationBarActivity) {
                                ((NavigationBarActivity) getActivity()).broadcastMessage(
                                        CardFragment.this, null, CardsFragment.class);
                            }

                            String frag = "PaymentEntryFragment";
                            getActivity().getSupportFragmentManager().popBackStack(null,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        }
                    }, WalletActivity.primaryColor );
                    dialog.show(getActivity().getSupportFragmentManager(), "PaymentSuccessDialog");
                } else {
                    /*if (mCard == null && getActivity() instanceof NavigationBarActivity) {
                        ((NavigationBarActivity) getActivity()).broadcastMessageToPreviousFragment(CardFragment.this, null, CardsFragment.class);
                    }*/
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                if (Web.checkFailureResponse(CardFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void setLoading(boolean loading) {
        pinText.setEnabled(!loading);
        cvv2Text.setEnabled(!loading);
        button.setEnabled(!loading);
        button.setText(loading ? "" : getString(mPayment != null ? R.string.pay : R.string.delete_card));
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

}
