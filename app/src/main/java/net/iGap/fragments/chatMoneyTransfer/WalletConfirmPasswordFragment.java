package net.iGap.fragments.chatMoneyTransfer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.HelperError;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.realm.RealmUserInfo;

import org.jetbrains.annotations.NotNull;
import org.paygear.RaadApp;
import org.paygear.web.Web;

import java.util.HashMap;
import java.util.Map;

import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.PostRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static net.iGap.G.fragmentActivity;

public class WalletConfirmPasswordFragment extends Fragment {

    private ProgressBar progressBar;
    private Button confirmBtn;
    private EditText passwordEt;
    private EditText confirmPasswordEt;

    private AvatarHandler avatarHandler;
    private String userName;
    private long peerId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString("userName", "");
            peerId = getArguments().getLong("peerId", -1);
            avatarHandler = new AvatarHandler();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_money_confirm_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        passwordEt = view.findViewById(R.id.et_confirm_enterPassword);
        confirmPasswordEt = view.findViewById(R.id.et_confirm_confirmPassword);
        progressBar = view.findViewById(R.id.pb_moneyAction);
        confirmBtn = view.findViewById(R.id.btn_moneyAction_confirm);

        AppCompatTextView creditTv = view.findViewById(R.id.tv_moneyAction_credit);
        AppCompatTextView userNameTextView = view.findViewById(R.id.tv_moneyAction_transferTo);
        userNameTextView.setText(String.format(getString(R.string.transfer_to_dialog), userName));

        avatarHandler.getAvatar(new ParamWithAvatarType(view.findViewById(R.id.iv_moneyAction_userAvatar), peerId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());

        if (G.selectedCard != null) {
            creditTv.setText(getString(R.string.wallet_Your_credit) + " " + String.format(getString(R.string.wallet_Reial), RealmUserInfo.queryWalletAmount()));
        } else {
            creditTv.setVisibility(View.GONE);
        }

        view.findViewById(R.id.btn_moneyAction_cancel).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).dismissDialog();
            }
        });
        confirmBtn.setOnClickListener(v -> startSavePin(passwordEt.getEditableText().toString(), confirmPasswordEt.getEditableText().toString()));
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
        showProgress();
        Web.getInstance().getWebService().setCreditCardPin(RaadApp.paygearCard.token, Auth.getCurrentAuth().getId(), PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                dismissProgress();
                Boolean success = Web.checkResponse(WalletConfirmPasswordFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.card_pin_saved), false);
                    RaadApp.paygearCard.isProtected = true;
                    fragmentActivity.onBackPressed();
                }
            }

            @Override
            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {
                dismissProgress();
                if (Web.checkFailureResponse(WalletConfirmPasswordFragment.this, call, t)) {
                }
            }
        });
    }


    private void dismissProgress() {
        progressBar.setVisibility(View.INVISIBLE);
        confirmBtn.setEnabled(true);
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        confirmBtn.setEnabled(false);
    }
}
