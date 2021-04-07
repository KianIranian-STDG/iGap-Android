package net.iGap.fragments.chatMoneyTransfer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.eventbus.SocketMessages;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoWalletPaymentInit;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestWalletPaymentInit;

import org.paygear.model.Card;
import org.paygear.model.PaymentResult;
import org.paygear.web.Web;

import java.util.ArrayList;
import java.util.Locale;

import ir.radsense.raadcore.model.Auth;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static net.iGap.G.fragmentActivity;

public class TransferMoneyFragment extends Fragment implements EventManager.EventDelegate {

    private long peerId;
    private String userName;

    private final String[] mPrice = {""};

    private AvatarHandler avatarHandler;

    private ProgressBar progressBar;
    private MaterialButton confirmBtn;

    private Card selectedCard = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            peerId = getArguments().getLong("peerId", -1);
            userName = getArguments().getString("userName", "");
            avatarHandler = new AvatarHandler();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer_money, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        confirmBtn = view.findViewById(R.id.btn_moneyAction_confirm);
        Button cancelBtn = view.findViewById(R.id.btn_moneyAction_cancel);
        progressBar = view.findViewById(R.id.pb_moneyAction);

        AppCompatTextView creditTv = view.findViewById(R.id.tv_moneyAction_credit);
        AppCompatTextView userNameTextView = view.findViewById(R.id.tv_moneyAction_transferTo);
        userNameTextView.setText(String.format(getString(R.string.transfer_to_dialog), userName));

        avatarHandler.getAvatar(new ParamWithAvatarType(view.findViewById(R.id.iv_moneyAction_userAvatar), peerId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());


        if (G.selectedCard != null) {
            creditTv.setText(getString(R.string.wallet_Your_credit) + " " + String.format(getString(R.string.wallet_Reial), RealmUserInfo.queryWalletAmount()));
        } else {
            creditTv.setVisibility(View.GONE);
        }

        AppCompatEditText amountEt = view.findViewById(R.id.et_moneyAction_amount);
        AppCompatEditText descriptionEt = view.findViewById(R.id.et_moneyAction_description);

        confirmBtn.setOnClickListener(v -> {
            if (mPrice[0] != null && !mPrice[0].isEmpty()) {
                confirmBtn.setEnabled(false);
                showProgress();
                new RequestWalletPaymentInit().walletPaymentInit(ProtoGlobal.Language.FA_IR,
                        Auth.getCurrentAuth().accessToken, peerId, Long.parseLong(mPrice[0]),
                        descriptionEt.getEditableText().toString());
                showProgress();
            }
        });

        cancelBtn.setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).dismissDialog();
            }
        });

        EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.ON_INIT_PAY, this);


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
                    e.printStackTrace();
                }

                amountEt.setText(s);
                amountEt.setSelection(amountEt.length());
                isSettingText = false;

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        avatarHandler.registerChangeFromOtherAvatarHandler();
    }

    @Override
    public void onStop() {
        super.onStop();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();
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
                    G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, SocketMessages.PaymentResultReceivedSuccess));
                } else {
                    HelperError.showSnackMessage(getResources().getString(R.string.not_success), false);
                    G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, SocketMessages.PaymentResultReceivedFailed));
                }
            } else {
                HelperError.showSnackMessage(getResources().getString(R.string.payment_canceled), false);
                G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_PAYMENT_RESULT_RECIEVED, SocketMessages.PaymentResultNotReceived));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
    public void receivedEvent(int id, int account, Object... args) {
        G.handler.post(() -> {
            confirmBtn.setEnabled(true);
            if (id == EventManager.ON_INIT_PAY) {
                if (args[0].equals("")) {
                    dismissProgress();
                    if (isAdded())
                        try {
                            HelperError.showSnackMessage(getResources().getString(R.string.PayGear_unavailable), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    return;
                }

                final ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder initPayResponse
                        = (ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder) args[0];

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
                                        if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                                            if (selectedCard.cashOutBalance >= Long.parseLong(mPrice[0])) {
                                                if (!selectedCard.isProtected) {
                                                    ((ParentChatMoneyTransferFragment) getParentFragment()).setWalletPassword();
                                                } else {
                                                    ((ParentChatMoneyTransferFragment) getParentFragment()).showPasswordFragment(initPayResponse, selectedCard);
                                                }
                                            } else {
                                                dismissProgress();
                                                ((ParentChatMoneyTransferFragment) getParentFragment()).showWalletActivity(initPayResponse, Long.parseLong(mPrice[0]));
                                            }
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
                        });
            } else if (id == EventManager.ON_PAYMENT_RESULT_RECIEVED) {
                dismissProgress();
                int response = (int) args[0];
                switch (response) {
                    case SocketMessages.PaymentResultReceivedSuccess:
                        fragmentActivity.onBackPressed();
                        HelperError.showSnackMessage(getResources().getString(R.string.result_4), false);
                        break;

                    case SocketMessages.PaymentResultReceivedFailed:
                        fragmentActivity.onBackPressed();
                        HelperError.showSnackMessage(getResources().getString(R.string.not_success_2), false);
                        break;

                    case SocketMessages.PaymentResultNotReceived:
                        fragmentActivity.onBackPressed();
                        HelperError.showSnackMessage(getResources().getString(R.string.result_3), false);
                        break;
                }
            } else if (id == EventManager.ON_INIT_PAY_ERROR) {
                dismissProgress();
            }
        });
    }
}
