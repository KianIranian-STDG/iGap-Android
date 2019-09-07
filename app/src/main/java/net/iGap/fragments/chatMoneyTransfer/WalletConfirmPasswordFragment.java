package net.iGap.fragments.chatMoneyTransfer;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.libs.bottomNavigation.Util.Utils;

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

public class WalletConfirmPasswordFragment extends BaseFragment {
    private View rootView;
    private ProgressBar progressBar;
    private Button confirmBtn;
    private EditText passwordEt;
    private EditText confirmPasswordEt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_send_money_confirm_password, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        passwordEt = rootView.findViewById(R.id.et_confirm_enterPassword);
        confirmPasswordEt = rootView.findViewById(R.id.et_confirm_confirmPassword);

        TextView descriptionTv = rootView.findViewById(R.id.tv_confirm_description);
        TextView confirmDescriptionTv = rootView.findViewById(R.id.tv_confirm_confirmDescription);

        Utils.darkModeHandler(rootView);
        Utils.darkModeHandler(confirmDescriptionTv);
        Utils.darkModeHandler(descriptionTv);
        dismissProgress();
    }

    @Override
    public void onStart() {
        super.onStart();

        confirmBtn.setOnClickListener(v -> {
            startSavePin(passwordEt.getText().toString(), confirmPasswordEt.getText().toString());
        });

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
            public void onFailure(Call<Void> call, Throwable t) {
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

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setConfirmBtn(Button confirmBtn) {
        this.confirmBtn = confirmBtn;
    }
}
