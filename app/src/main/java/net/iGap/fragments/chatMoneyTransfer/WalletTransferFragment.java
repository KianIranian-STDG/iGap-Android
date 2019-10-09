package net.iGap.fragments.chatMoneyTransfer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.iGap.G;
import net.iGap.R;
import net.iGap.Theme;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;
import net.iGap.eventbus.socketMessages;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoWalletPaymentInit;
import net.iGap.request.RequestWalletPaymentInit;

import org.paygear.WalletActivity;
import org.paygear.model.Card;
import org.paygear.model.Payment;
import org.paygear.model.PaymentAuth;
import org.paygear.model.PaymentResult;
import org.paygear.web.Web;

import java.util.ArrayList;
import java.util.Locale;

import ir.radsense.raadcore.model.Auth;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static net.iGap.G.context;
import static net.iGap.G.fragmentActivity;
import static net.iGap.G.needGetSignalingConfiguration;

public class WalletTransferFragment extends BaseFragment implements EventListener {
    private static final String TAG = "aabolfazlWallet";

    private final String[] mPrice = {""};
    private View rootView;
    private EditText amountEt;
    private EditText descriptionEt;
    private Button confirmBtn;
    private Card selectedCard = null;
    private Long userId;
    private ProgressBar progressBar;
    private FragmentManager fragmentManager;
    private Button cancelBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_send_money_detail, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        amountEt = rootView.findViewById(R.id.et_moneyAction_amount);
        descriptionEt = rootView.findViewById(R.id.et_moneyAction_description);

        confirmBtn.setOnClickListener(v -> {
            if (mPrice[0] != null && !mPrice[0].isEmpty()) {
                confirmBtn.setEnabled(false);
                showProgress();
                new RequestWalletPaymentInit().walletPaymentInit(ProtoGlobal.Language.FA_IR,
                        Auth.getCurrentAuth().accessToken, userId, Long.parseLong(mPrice[0]),
                        descriptionEt.getText().toString());
                showProgress();
            }
        });

        EventManager.getInstance().addEventListener(EventManager.ON_INIT_PAY, this);


        amountEt.addTextChangedListener(new TextWatcher() {
            boolean isSettingText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPrice[0] = s.toString().replaceAll(",", "");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isSettingText) return;
                isSettingText = true;
                String s = null;
                try {
                    s = String.format(Locale.US, "%,d", Long.parseLong(mPrice[0]));
                } catch (NumberFormatException e) {
                    Log.d(TAG, "afterTextChanged: " + e);
                }

                amountEt.setText(s);
                amountEt.setSelection(amountEt.length());
                isSettingText = false;

            }
        });
    }

    @Override
    public void receivedMessage(int id, Object... message) {
        G.handler.post(() -> confirmBtn.setEnabled(true));

        switch (id) {
            case EventManager.ON_INIT_PAY:
                if (message == null) {
                    G.handler.post(() -> {
                        dismissProgress();
                        if (isAdded())
                            try {
                                HelperError.showSnackMessage(getResources().getString(R.string.PayGear_unavailable), false);
                            } catch (Exception e) {
                                Log.e(TAG, "receivedMessage: ", e);
                            }
                    });
                    return;
                }

                final ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder initPayResponse
                        = (ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder) message[0];

                if (initPayResponse != null) {
                    new android.os.Handler(getContext().getMainLooper()).post(() ->
                            Web.getInstance().getWebService()
                                    .getCards(null, false, true)
                                    .enqueue(new Callback<ArrayList<Card>>() {
                                        @Override
                                        public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                                            dismissProgress();
                                            if (getActivity() == null || getActivity().isFinishing())
                                                return;
                                            if (response.body() != null) {
                                                selectedCard = null;

                                                for (Card card : response.body()) {
                                                    if (card.isRaadCard()) {
                                                        selectedCard = card;
                                                        break;
                                                    }
                                                }

                                                if (selectedCard != null) {
                                                    if (selectedCard.cashOutBalance >= Long.parseLong(mPrice[0])) {
                                                        if (!selectedCard.isProtected) {
                                                            setWalletPassword();
                                                        } else {
                                                            showPasswordFragment(initPayResponse);
                                                        }
                                                    } else {
                                                        showWalletActivity(initPayResponse);
                                                    }

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                                            if (confirmBtn != null)
                                                confirmBtn.setEnabled(true);
                                            dismissProgress();
                                            HelperError.showSnackMessage(getResources().getString(R.string.PayGear_unavailable), false);
                                        }
                                    }));
                } else {
                    dismissProgress();
                    HelperError.showSnackMessage(getResources().getString(R.string.PayGear_unavailable), false);
                }
                break;
            case EventManager.ON_PAYMENT_RESULT_RECIEVED:

                dismissProgress();
                int response = (int) message[0];
                switch (response) {
                    case socketMessages.PaymentResultRecievedSuccess:
                        new android.os.Handler(getContext().getMainLooper()).post(() -> {
                            fragmentActivity.onBackPressed();
                            HelperError.showSnackMessage(getResources().getString(R.string.result_4), false);
                        });

                        break;

                    case socketMessages.PaymentResultRecievedFailed:
                        new android.os.Handler(getContext().getMainLooper()).post(() -> {
                            fragmentActivity.onBackPressed();
                            HelperError.showSnackMessage(getResources().getString(R.string.not_success_2), false);
                        });
                        break;

                    case socketMessages.PaymentResultNotRecieved:
                        new android.os.Handler(getContext().getMainLooper()).post(() -> {
                            fragmentActivity.onBackPressed();
                            HelperError.showSnackMessage(getResources().getString(R.string.result_3), false);
                        });
                        break;
                }
                break;

            case EventManager.ON_INIT_PAY_ERROR:
                dismissProgress();
                break;
        }
    }

    private void showPasswordFragment(ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder initPayResponse) {
        PaymentAuth paymentAuth = new PaymentAuth();
        paymentAuth.publicKey = initPayResponse.getPublicKey();
        paymentAuth.token = initPayResponse.getToken();

        WalletPasswordFragment passwordFragment = new WalletPasswordFragment();
        passwordFragment.setConfirmBtn(confirmBtn);
        passwordFragment.setPaymentAuth(paymentAuth);
        passwordFragment.setProgressBar(progressBar);
        passwordFragment.setCancelBtn(cancelBtn);
        passwordFragment.setSelectedCard(selectedCard);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.fl_moneyAction_Container, passwordFragment, "passwordFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showWalletActivity(ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder initPayResponse) {
        dismissProgress();
        Payment payment = new Payment();
        PaymentAuth paymentAuth = new PaymentAuth();
        paymentAuth.token = initPayResponse.getToken();
        paymentAuth.publicKey = initPayResponse.getPublicKey();
        payment.account = null;
        payment.paymentAuth = paymentAuth;
        payment.isCredit = false;
        payment.orderId = null;
        payment.price = Long.parseLong(mPrice[0]);
        Intent intent = new Intent(context, WalletActivity.class);
        intent.putExtra("Language", "fa");
        intent.putExtra("Mobile", "0" + G.userId);
        intent.putExtra("IsP2P", true);
        intent.putExtra("Payment", payment);
        intent.putExtra("PrimaryColor",new Theme().getPrimaryColor(getContext()));
        intent.putExtra("DarkPrimaryColor", new Theme().getPrimaryColor(getContext()));
        intent.putExtra("AccentColor", new Theme().getPrimaryColor(getContext()));
        intent.putExtra(WalletActivity.PROGRESSBAR, new Theme().getAccentColor(getContext()));
        intent.putExtra(WalletActivity.LINE_BORDER, G.lineBorder);
        intent.putExtra(WalletActivity.BACKGROUND,new Theme().getRootColor(getContext()));
        intent.putExtra(WalletActivity.BACKGROUND_2,new Theme().getRootColor(getContext()));
        intent.putExtra(WalletActivity.TEXT_TITLE, new Theme().getTitleTextColor(getContext()));
        intent.putExtra(WalletActivity.TEXT_SUB_TITLE, G.textSubTheme);
        startActivityForResult(intent, 66);
        cancelBtn.performClick();
    }

    private void setWalletPassword() {
        WalletConfirmPasswordFragment confirmPasswordFragment = new WalletConfirmPasswordFragment();
        confirmPasswordFragment.setConfirmBtn(confirmBtn);
        confirmPasswordFragment.setProgressBar(progressBar);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.fl_moneyAction_Container, confirmPasswordFragment, "confirmPasswordFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void dismissProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        confirmBtn.setEnabled(true);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        confirmBtn.setEnabled(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 66) {
            if (resultCode == RESULT_OK) {
                PaymentResult paymentResult = (PaymentResult) data.getSerializableExtra("result");
                if (paymentResult != null) {
                    HelperError.showSnackMessage(getResources().getString(R.string.trace_number)
                            + String.valueOf(paymentResult.traceNumber)
                            + getResources().getString(R.string.amount_2)
                            + paymentResult.amount, false);
                    EventManager.getInstance().postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, socketMessages.PaymentResultRecievedSuccess);
                } else {
                    HelperError.showSnackMessage(getResources().getString(R.string.not_success), false);
                    EventManager.getInstance().postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, socketMessages.PaymentResultRecievedFailed);
                }
            } else {
                HelperError.showSnackMessage(getResources().getString(R.string.payment_canceled), false);
                EventManager.getInstance().postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, socketMessages.PaymentResultNotRecieved);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setConfirmBtn(Button confirmBtn) {
        this.confirmBtn = confirmBtn;
    }

    public void setCancelBtn(Button cancelBtn) {
        this.cancelBtn = cancelBtn;
    }
}
