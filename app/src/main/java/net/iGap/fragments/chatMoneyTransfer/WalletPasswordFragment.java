package net.iGap.fragments.chatMoneyTransfer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.webservice.APIService;
import net.iGap.webservice.ApiUtils;
import net.iGap.webservice.Post;

import org.paygear.RaadApp;
import org.paygear.fragment.PaymentResultDialog;
import org.paygear.model.Card;
import org.paygear.model.PaymentAuth;
import org.paygear.model.PaymentResult;
import org.paygear.web.Web;

import java.util.HashMap;
import java.util.Map;

import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.web.PostRequest;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.paygear.utils.RSAUtils.getRSA;

public class WalletPasswordFragment extends BaseFragment {

    private ProgressBar progressBar;
    private Button confirmBtn;
    private Button cancelBtn;
    private EditText passwordEt;
    private Card selectedCard = null;
    private PaymentAuth paymentAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_money_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        passwordEt = view.findViewById(R.id.et_enterPassword);
        confirmBtn.setText(R.string.pay);
    }

    @Override
    public void onStart() {
        super.onStart();

        confirmBtn.setOnClickListener(v -> {
            startPay(paymentAuth, passwordEt.getText().toString());
        });

    }

    private void startPay(final PaymentAuth paymentAuth, String pin) {

        String cardDataRSA = getCardDataRSA(paymentAuth, selectedCard, pin, null);
        Map<String, String> finalInfoMap = new HashMap<>();
        finalInfoMap.put("token", paymentAuth.token);
        finalInfoMap.put("card_info", cardDataRSA);

        showProgress();

        Web.getInstance().getWebService().pay(PostRequest.getRequestBody(finalInfoMap)).enqueue(new Callback<PaymentResult>() {
            @Override
            public void onResponse(Call<PaymentResult> call, final Response<PaymentResult> response) {

                Boolean success = Web.checkResponse(WalletPasswordFragment.this, call, response);
                if (success == null)
                    return;

                dismissProgress();

                if (response.errorBody() == null && response.body() != null) {
                    PaymentResult paymentResult = response.body();
                    final PaymentResultDialog dialog = PaymentResultDialog.newInstance(paymentResult);
                    dialog.setListener(v -> {
                        RaadApp.cards = null;
                        dialog.dismiss();
                        sendPost(response.body().callbackUrl, paymentAuth.token);
                        G.cardamount -= response.body().amount;
                    }, G.appBarColor);
                    dialog.show(getActivity().getSupportFragmentManager(), "PaymentSuccessDialog");
                    cancelBtn.performClick();
                }
            }

            @Override
            public void onFailure(Call<PaymentResult> call, Throwable t) {
                dismissProgress();
                HelperError.showSnackMessage(getResources().getString(R.string.wallet_error_server), false);
            }
        });
    }


    public void sendPost(String url, String token) {
        Map<String, String> finalInfoMap = new HashMap<>();
        finalInfoMap.put("token", token);
        APIService mAPIService = ApiUtils.getAPIService();
        mAPIService.sendToken(url, getRequestBody(finalInfoMap)).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
            }
        });
    }

    private RequestBody getRequestBody(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return RequestBody.create(MediaType.parse("application/json"), json);
    }

    private String getCardDataRSA(PaymentAuth paymentAuth, Card mCard, String pin2, String cvv2) {
        Map<String, Object> map = new HashMap();
        map.put("t", System.currentTimeMillis());
        map.put("c", mCard.token);
        map.put("bc", mCard.bankCode);
        map.put("type", mCard.type);
        if (!TextUtils.isEmpty(cvv2)) {
            map.put("cv", cvv2);
        }

        if (pin2 != null) {
            map.put("p2", pin2);
        }

        Gson gson = new Gson();
        String cardInfoJson = gson.toJson(map);
        String publicKey;
        if (paymentAuth != null) {
            publicKey = paymentAuth.publicKey;
        } else {
            publicKey = Auth.getCurrentAuth().getPublicKey();
        }

        return getRSA(publicKey, cardInfoJson);
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

    public void setSelectedCard(Card selectedCard) {
        this.selectedCard = selectedCard;
    }

    public void setPaymentAuth(PaymentAuth paymentAuth) {
        this.paymentAuth = paymentAuth;
    }

    public void setConfirmBtn(Button confirmBtn) {
        this.confirmBtn = confirmBtn;
    }

    public void setCancelBtn(Button cancelBtn) {
        this.cancelBtn = cancelBtn;
    }
}
