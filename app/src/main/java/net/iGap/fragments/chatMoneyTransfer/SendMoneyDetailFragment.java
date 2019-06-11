package net.iGap.fragments.chatMoneyTransfer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.eventbus.EventListener;
import net.iGap.eventbus.EventManager;
import net.iGap.eventbus.socketMessages;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoWalletPaymentInit;
import net.iGap.request.RequestWalletPaymentInit;

import org.paygear.RaadApp;
import org.paygear.WalletActivity;
import org.paygear.model.Card;
import org.paygear.model.Payment;
import org.paygear.model.PaymentAuth;
import org.paygear.model.PaymentResult;
import org.paygear.web.Web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.PostRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static net.iGap.G.context;
import static net.iGap.G.fragmentActivity;

public class SendMoneyDetailFragment extends BaseFragment implements EventListener {
    private final String[] mPrice = {""};
    private View rootView;
    private EditText amountEt;
    private EditText descriptionEt;
    private Button payBtn;
    private String TAG = "aabolfazl";
    private Card selectedCard = null;
    private Long userId;
    private ProgressBar progressBar;
    private FragmentManager fragmentManager;


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
        payBtn = rootView.findViewById(R.id.btn_moneyAction_pay);
        Button cancelBtn = rootView.findViewById(R.id.btn_moneyAction_cancel);


        payBtn.setOnClickListener(v -> {
            if (mPrice[0] != null && !mPrice[0].isEmpty()) {
                payBtn.setEnabled(false);
                showProgress();
                new RequestWalletPaymentInit().walletPaymentInit(ProtoGlobal.Language.FA_IR,
                        Auth.getCurrentAuth().accessToken, userId, Long.parseLong(mPrice[0]),
                        descriptionEt.getText().toString());
            }
        });

        EventManager.getInstance().addEventListener(EventManager.ON_INIT_PAY, this);


        if (getActivity() != null)
            cancelBtn.setOnClickListener(v -> getActivity().onBackPressed());

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
        G.handler.post(() -> payBtn.setEnabled(true));

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
                final ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder
                        initPayResponse = (ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder) message[0];

                if (initPayResponse != null) {
                    new android.os.Handler(getContext().getMainLooper()).post(() ->
                            Web.getInstance().getWebService()
                                    .getCards(null, false, true)
                                    .enqueue(new Callback<ArrayList<Card>>() {
                                        @Override
                                        public void onResponse(Call<ArrayList<Card>> call, Response<ArrayList<Card>> response) {
                                            dismissProgress();
                                            if (!HelperFragment.isFragmentVisible("PaymentFragment"))
                                                return;
                                            if (response.body() != null) {
                                                selectedCard = null;
                                                if (response.body().size() > 0)
                                                    selectedCard = response.body().get(0);
                                                if (selectedCard != null) {
                                                    if (selectedCard.cashOutBalance >= Long.parseLong(mPrice[0])) {
                                                        if (!selectedCard.isProtected) {
                                                            dismissProgress();
                                                            setNewPassword();
                                                        } else {
                                                            dismissProgress();
                                                            PaymentAuth paymentAuth = new PaymentAuth();
                                                            paymentAuth.publicKey = initPayResponse.getPublicKey();
                                                            paymentAuth.token = initPayResponse.getToken();

                                                            SendMoneyPasswordFragment passwordFragment = new SendMoneyPasswordFragment();
                                                            passwordFragment.setPaymentAuth(paymentAuth);
                                                            passwordFragment.setProgressBar(progressBar);
                                                            passwordFragment.setSelectedCard(selectedCard);

                                                            /**
                                                             * because add fragment in other fragment can not use getFragmentManager
                                                             * */

                                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left);

                                                            fragmentTransaction.replace(R.id.fl_moneyAction_Container, passwordFragment);
                                                            fragmentTransaction.addToBackStack(null);
                                                            fragmentTransaction.commit();

                                                        }
                                                    } else {
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
                                                        intent.putExtra("PrimaryColor", G.appBarColor);
                                                        intent.putExtra("DarkPrimaryColor", G.appBarColor);
                                                        intent.putExtra("AccentColor", G.appBarColor);
                                                        intent.putExtra(WalletActivity.PROGRESSBAR, G.progressColor);
                                                        intent.putExtra(WalletActivity.LINE_BORDER, G.lineBorder);
                                                        intent.putExtra(WalletActivity.BACKGROUND, G.backgroundTheme);
                                                        intent.putExtra(WalletActivity.BACKGROUND_2, G.backgroundTheme_2);
                                                        intent.putExtra(WalletActivity.TEXT_TITLE, G.textTitleTheme);
                                                        intent.putExtra(WalletActivity.TEXT_SUB_TITLE, G.textSubTheme);
                                                        startActivityForResult(intent, 66);
                                                        G.currentActivity.onBackPressed();
                                                    }

                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ArrayList<Card>> call, Throwable t) {
                                            if (payBtn != null)
                                                payBtn.setEnabled(true);
                                            dismissProgress();
                                            HelperError.showSnackMessage(getResources().getString(R.string.PayGear_unavailable), false);
                                        }
                                    }));
                } else {
                    dismissProgress();
                    HelperError.showSnackMessage(getResources().getString(R.string.PayGear_unavailable), false);
                }
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

            case EventManager.ON_INIT_PAY_ERROR:
                dismissProgress();
                break;
        }
    }

    private void dismissProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        payBtn.setEnabled(true);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        payBtn.setEnabled(false);
    }

    public void setNewPassword() {
        final LinearLayout layoutNickname = new LinearLayout(G.fragmentActivity);
        layoutNickname.setOrientation(LinearLayout.VERTICAL);

        final View viewNewPassword = new View(G.fragmentActivity);
        viewNewPassword.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

        TextInputLayout inputNewPassWord = new TextInputLayout(G.fragmentActivity, null, R.attr.hintColorSettingTheme);
        final AppCompatEditText newPassWord = new AppCompatEditText(G.fragmentActivity);
        newPassWord.setHint(G.fragmentActivity.getResources().getString(R.string.please_enter_your_password));
        newPassWord.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        newPassWord.setTypeface(G.typeface_IRANSansMobile);
        newPassWord.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        newPassWord.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        newPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newPassWord.setPadding(0, 8, 0, 8);
        newPassWord.setMaxLines(1);
        inputNewPassWord.addView(newPassWord);
        inputNewPassWord.addView(viewNewPassword, viewParams);

        if (G.isDarkTheme) {
            newPassWord.setTextColor(G.context.getResources().getColor(R.color.white));
        }

        final View viewConfirmPassWord = new View(G.fragmentActivity);
        viewConfirmPassWord.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newPassWord.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }

        TextInputLayout inputConfirmPassWord = new TextInputLayout(G.fragmentActivity, null, R.attr.hintColorSettingTheme);
        final AppCompatEditText confirmPassWord = new AppCompatEditText(G.fragmentActivity);
        confirmPassWord.setHint(G.fragmentActivity.getResources().getString(R.string.please_re_enter_your_password));
        confirmPassWord.setImeOptions(EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        confirmPassWord.setTypeface(G.typeface_IRANSansMobile);
        confirmPassWord.setTextSize(TypedValue.COMPLEX_UNIT_PX, G.context.getResources().getDimension(R.dimen.dp14));
        confirmPassWord.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        confirmPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmPassWord.setPadding(0, 8, 0, 8);
        confirmPassWord.setMaxLines(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            confirmPassWord.setBackground(G.context.getResources().getDrawable(android.R.color.transparent));
        }
        if (G.isDarkTheme) {
            confirmPassWord.setTextColor(G.context.getResources().getColor(R.color.white));
        }
        inputConfirmPassWord.addView(confirmPassWord);
        inputConfirmPassWord.addView(viewConfirmPassWord, viewParams);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 15);
        LinearLayout.LayoutParams lastNameLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lastNameLayoutParams.setMargins(0, 15, 0, 10);

        layoutNickname.addView(inputNewPassWord, layoutParams);
        layoutNickname.addView(inputConfirmPassWord, lastNameLayoutParams);

        final MaterialDialog dialog =
                new MaterialDialog.Builder(G.fragmentActivity)
                        .title(G.fragmentActivity.getResources().getString(R.string.please_set_password))
                        .inputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                        .positiveText(G.fragmentActivity.getResources().getString(R.string.B_ok)).customView(layoutNickname, true)
                        .widgetColor(Color.parseColor(G.appBarColor)).negativeText(G.fragmentActivity.getResources().getString(R.string.B_cancel)).build();

        final View positive = dialog.getActionButton(DialogAction.POSITIVE);

        newPassWord.setOnFocusChangeListener((view, b) -> {
            if (b) {
                viewNewPassword.setBackgroundColor(Color.parseColor(G.appBarColor));
            } else {
                viewNewPassword.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
            }
        });

        confirmPassWord.setOnFocusChangeListener((view, b) -> {
            if (b) {
                viewConfirmPassWord.setBackgroundColor(Color.parseColor(G.appBarColor));
            } else {
                viewConfirmPassWord.setBackgroundColor(G.context.getResources().getColor(R.color.line_edit_text));
            }
        });

        positive.setOnClickListener(view -> {

            startSavePin(newPassWord.getText().toString(), confirmPassWord.getText().toString());
            dialog.dismiss();

        });

        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case 66:
                if (resultCode == RESULT_OK) {
                    PaymentResult paymentResult = (PaymentResult) data.getSerializableExtra("result");
                    if (paymentResult != null) {
                        HelperError.showSnackMessage(getResources().getString(R.string.trace_number) + String.valueOf(paymentResult.traceNumber) + getResources().getString(R.string.amount_2) + String.valueOf(paymentResult.amount), false);
                        EventManager.getInstance().postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, socketMessages.PaymentResultRecievedSuccess);
                    } else {
                        HelperError.showSnackMessage(getResources().getString(R.string.not_success), false);
                        EventManager.getInstance().postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, socketMessages.PaymentResultRecievedFailed);

                    }
                } else {

                    HelperError.showSnackMessage(getResources().getString(R.string.payment_canceled), false);
                    EventManager.getInstance().postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, socketMessages.PaymentResultNotRecieved);
                }
                break;
        }

    }

    private void startSavePin(String newPassword, String confirmPassword) {
        String[] data = new String[]{newPassword,
                confirmPassword,};
        if ((RaadApp.paygearCard.isProtected && TextUtils.isEmpty(data[0])) || TextUtils.isEmpty(data[1])) {
            HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.please_enter_your_password), true);
            return;
        }

        if (!data[0].equals(data[1])) {
            HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.Password_dose_not_match), true);
            return;
        }

        Map<String, String> map = new HashMap<>();
        if (RaadApp.paygearCard.isProtected)
            map.put("old_password", data[0]);
        map.put("new_password", data[1]);

        Web.getInstance().getWebService().setCreditCardPin(RaadApp.paygearCard.token, Auth.getCurrentAuth().getId(), PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(SendMoneyDetailFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.card_pin_saved), false);
                    RaadApp.paygearCard.isProtected = true;
                    fragmentActivity.onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(SendMoneyDetailFragment.this, call, t)) {
                }
            }
        });
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
}
