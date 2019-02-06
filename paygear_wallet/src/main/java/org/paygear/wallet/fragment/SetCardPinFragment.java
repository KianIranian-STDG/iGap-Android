package org.paygear.wallet.fragment;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.paygear.wallet.R;
import org.paygear.wallet.RaadApp;
import org.paygear.wallet.WalletActivity;
import org.paygear.wallet.model.Card;
import org.paygear.wallet.utils.Utils;
import org.paygear.wallet.web.Web;


import java.util.HashMap;
import java.util.Map;

import ir.radsense.raadcore.app.RaadToolBar;
import ir.radsense.raadcore.model.Auth;
import ir.radsense.raadcore.utils.Typefaces;
import ir.radsense.raadcore.web.PostRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SetCardPinFragment extends Fragment {

    private RaadToolBar appBar;
    private EditText currentPass;
    private EditText newPass;
    private EditText confirmPass;
    private TextView button;
    private ProgressBar progressBar;
    private Card mCard;
    private boolean isRecovery = false;
    private static SharedPreferences sharedPreferences;
    private boolean isResetPassword;

    public SetCardPinFragment() {
    }

    public static SetCardPinFragment newInstance(boolean isRecovery) {
        SetCardPinFragment fragment = new SetCardPinFragment();
        Bundle args = new Bundle();
        args.putBoolean("isRecovery", isRecovery);
        fragment.setArguments(args);
        return fragment;
    }
    public static SetCardPinFragment newInstance(boolean isRecovery,Card card) {
        SetCardPinFragment fragment = new SetCardPinFragment();
        Bundle args = new Bundle();
        args.putBoolean("isRecovery", isRecovery);
        args.putSerializable("Card",card);
        fragment.setArguments(args);
        return fragment;
    }

    public static SetCardPinFragment newInstance(Card card) {
        SetCardPinFragment fragment = new SetCardPinFragment();
        Bundle args = new Bundle();
        args.putSerializable("Card", card);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCard = (Card) getArguments().getSerializable("Card");
            isRecovery = getArguments().getBoolean("isRecovery", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = getActivity().getSharedPreferences(WalletActivity.SH_SETTING, getActivity().MODE_PRIVATE);
        isResetPassword = sharedPreferences.getBoolean(WalletActivity.RESET_PASSWORD, false);
        Log.i("CCCCCCCCCC", "0 isResetPassword: " + isResetPassword);

        View view = inflater.inflate(R.layout.fragment_set_card_pin, container, false);
        ViewGroup rootView = view.findViewById(R.id.rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme_2));
        }
        appBar = view.findViewById(R.id.app_bar);
        appBar.setTitle(getString(R.string.paygear_card_pin));
        appBar.setToolBarBackgroundRes(R.drawable.app_bar_back_shape, true);
        appBar.getBack().getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
        if (RaadApp.selectedMerchant != null) {
            if (RaadApp.selectedMerchant.getName() != null && !RaadApp.selectedMerchant.getName().equals("")) {
                appBar.setTitle(getString(R.string.paygear_card_pin) + " " + RaadApp.selectedMerchant.getName());
            } else {
                appBar.setTitle(getString(R.string.paygear_card_pin) + " " + RaadApp.selectedMerchant.getUsername());
            }
        } else {
            appBar.setTitle(getString(R.string.paygear_card_pin));
        }

        appBar.showBack();
        ViewGroup root_current = view.findViewById(R.id.root_current);
        root_current.setBackgroundColor(Color.parseColor(WalletActivity.backgroundTheme));


        TextView currentPassTitle = view.findViewById(R.id.current_pass_title);
        TextView newPassTitle = view.findViewById(R.id.new_pass_title);
        TextView confirmPassTitle = view.findViewById(R.id.confirm_pass_title);

        currentPass = view.findViewById(R.id.current_pass);
        newPass = view.findViewById(R.id.new_pass);
        confirmPass = view.findViewById(R.id.confirm_pass);

        button = view.findViewById(R.id.button);
        Drawable mDrawableSkip = ContextCompat.getDrawable(getContext(), R.drawable.button_green_selector_24dp);
        if (mDrawableSkip != null) {
            mDrawableSkip.setColorFilter(new PorterDuffColorFilter(Color.parseColor(WalletActivity.primaryColor), PorterDuff.Mode.SRC_IN));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                button.setBackground(mDrawableSkip);
            }
        }

        if (WalletActivity.isDarkTheme) {
            currentPass.setHintTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            currentPass.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            newPass.setHintTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            newPass.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            confirmPass.setHintTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            confirmPass.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));

            currentPassTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            newPassTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
            confirmPassTitle.setTextColor(Color.parseColor(WalletActivity.textTitleTheme));
        }
        progressBar = view.findViewById(R.id.progress);

        Typefaces.setTypeface(getContext(), Typefaces.IRAN_MEDIUM, currentPassTitle, newPassTitle, confirmPassTitle, button);
        Typefaces.setTypeface(getContext(), Typefaces.IRAN_LIGHT, currentPass, newPass, confirmPass);


        confirmPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!isRecovery)
                        startSavePin();
                    else {
                        startSetNewPassword();
                    }
                }
                return false;
            }
        });

        currentPassTitle.setVisibility((mCard == null ? RaadApp.paygearCard.isProtected : mCard.isProtected) ? View.VISIBLE : View.GONE);
        currentPass.setVisibility((mCard == null ? RaadApp.paygearCard.isProtected : mCard.isProtected) ? View.VISIBLE : View.GONE);


        if (isRecovery) {
            currentPassTitle.setText(R.string.four_digit_code);
            currentPass.setHint(R.string.enter_four_digit_code);
        } else {
            currentPassTitle.setText(R.string.current_pass);
            currentPass.setHint(R.string.current_pass);

        }
        if (isRecovery) {
            getOTP();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecovery)
                    startSavePin();
                else {
                    startSetNewPassword();
                }
            }
        });


        return view;
    }

    private void startSetNewPassword() {
        String[] data = new String[]{currentPass.getText().toString(),
                newPass.getText().toString(),
                confirmPass.getText().toString()};
        if (((mCard == null ? RaadApp.paygearCard.isProtected : mCard.isProtected) && TextUtils.isEmpty(data[0])) || TextUtils.isEmpty(data[1]) || TextUtils.isEmpty(data[2])) {
            Toast.makeText(getContext(), R.string.enter_info_completely, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!data[1].equals(data[2])) {
            Toast.makeText(getContext(), R.string.passwords_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        Map<String, String> map = new HashMap<>();
        map.put("otp",data[0]);
        map.put("old_password", "");
        map.put("new_password", data[1]);

        Web.getInstance().getWebService().setNewPassword(mCard == null ? RaadApp.paygearCard.token : mCard.token, mCard == null ? RaadApp.me.id : RaadApp.selectedMerchant.get_id(), PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(SetCardPinFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    if (currentPass.getVisibility() == View.VISIBLE)
                        Toast.makeText(getContext(), R.string.card_pin_changed, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), R.string.card_pin_saved, Toast.LENGTH_SHORT).show();
                    if (mCard == null)
                        RaadApp.paygearCard.isProtected = true;
                    else {
                        CashOutRequestFragment.mCard.isProtected = true;
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                }

                setLoading(false);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(SetCardPinFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });

    }

    private void getOTP() {
        setLoading(true);
        Web.getInstance().getWebService().getRecoverPasswordOTP((mCard == null ? RaadApp.paygearCard.token : mCard.token), mCard == null ? RaadApp.me.id : RaadApp.selectedMerchant.get_id()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(SetCardPinFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {

                }

                setLoading(false);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(SetCardPinFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void startSavePin() {
        String[] data = new String[]{currentPass.getText().toString(),
                newPass.getText().toString(),
                confirmPass.getText().toString()};
        if (((mCard == null ? RaadApp.paygearCard.isProtected : mCard.isProtected) && TextUtils.isEmpty(data[0])) || TextUtils.isEmpty(data[1]) || TextUtils.isEmpty(data[2])) {
            Toast.makeText(getContext(), R.string.enter_info_completely, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!data[1].equals(data[2])) {
            Toast.makeText(getContext(), R.string.passwords_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        Map<String, String> map = new HashMap<>();
        if ((mCard == null ? RaadApp.paygearCard.isProtected : mCard.isProtected))
            map.put("old_password", data[0]);
        map.put("new_password", data[1]);

        Web.getInstance().getWebService().setCreditCardPin(mCard == null ? RaadApp.paygearCard.token : mCard.token, mCard == null ? Auth.getCurrentAuth().getId() : (RaadApp.selectedMerchant!=null?RaadApp.selectedMerchant.get_id(): Auth.getCurrentAuth().getId()), PostRequest.getRequestBody(map)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Boolean success = Web.checkResponse(SetCardPinFragment.this, call, response);
                if (success == null)
                    return;

                if (success) {
                    if (currentPass.getVisibility() == View.VISIBLE)
                        Toast.makeText(getContext(), R.string.card_pin_changed, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), R.string.card_pin_saved, Toast.LENGTH_SHORT).show();
                    if (mCard == null)
                        RaadApp.paygearCard.isProtected = true;
                    else {
                        CashOutRequestFragment.mCard.isProtected = true;
                    }
                    getActivity().getSupportFragmentManager().popBackStack();
                }

                setLoading(false);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (Web.checkFailureResponse(SetCardPinFragment.this, call, t)) {
                    setLoading(false);
                }
            }
        });
    }

    private void setLoading(boolean loading) {
        currentPass.setEnabled(!loading);
        newPass.setEnabled(!loading);
        confirmPass.setEnabled(!loading);
        button.setEnabled(!loading);
        button.setText(loading ? "" : getString(R.string.ok));
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);

        if (loading)
            Utils.hideKeyboard(getContext(), button);
    }

}
