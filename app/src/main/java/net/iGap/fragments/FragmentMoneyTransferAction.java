package net.iGap.fragments;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.eventbus.EventListener;
import net.iGap.module.CircleImageView;

import java.util.Locale;

public class FragmentMoneyTransferAction extends BottomSheetDialogFragment implements EventListener {
    private static final String TAG = "aabolfazl";
    private final String[] mPrice = {""};

    private View rootView;
    private View cardToCard;
    private View sendMoney;
    private View moneyActionRootView;
    private ValueAnimator anim;
    private View transferRootView;
    private CircleImageView userAvatarIv;
    private TextView userNameTv;
    private TextView creditTv;
    private EditText amountEt;
    private EditText descriptionEt;
    private Button payBtn;
    private Button cancelBtn;
    private Long userId;
    private String userName;
    private Drawable drawable;
    private MoneyTransferAction moneyTransferAction;

    public static FragmentMoneyTransferAction getInstance(long userId, Drawable userPicture, String userName) {
        FragmentMoneyTransferAction transferAction = new FragmentMoneyTransferAction();

        transferAction.userId = userId;
        transferAction.userName = userName;
        transferAction.drawable = userPicture;

        return transferAction;
    }


    public void setMoneyTransferAction(MoneyTransferAction moneyTransferAction) {
        this.moneyTransferAction = moneyTransferAction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_money_transfer_action, container);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        moneyActionRootView = rootView.findViewById(R.id.ll_moneyAction_root);
        sendMoney = rootView.findViewById(R.id.ll_moneyAction_sendMoney);
        cardToCard = rootView.findViewById(R.id.ll_moneyAction_cardToCard);
        transferRootView = rootView.findViewById(R.id.cl_moneyAction_transferRoot);
        userAvatarIv = rootView.findViewById(R.id.iv_moneyAction_userAvatar);
        userNameTv = rootView.findViewById(R.id.tv_moneyAction_userName);
        creditTv = rootView.findViewById(R.id.tv_moneyAction_credit);
        amountEt = rootView.findViewById(R.id.et_moneyAction_amount);
        descriptionEt = rootView.findViewById(R.id.et_moneyAction_description);
        payBtn = rootView.findViewById(R.id.btn_moneyAction_pay);
        cancelBtn = rootView.findViewById(R.id.btn_moneyAction_cancel);
    }

    @Override
    public void onStart() {
        super.onStart();
        int windowHeight = getScreenHeight();

        anim = ValueAnimator.ofInt(600, windowHeight);
        anim.setDuration(350);

        transferActionInit();
    }

    private void transferActionInit() {
        cardToCard.setOnClickListener(v -> {
            moneyTransferAction.cardToCardClicked();
            dismiss();
        });

        sendMoney.setOnClickListener(v -> {
            moneyActionRootView.setVisibility(View.GONE);
            anim.start();
            sendMoneyClicked();
        });

        TextView cardToCardTv = rootView.findViewById(R.id.tv_moneyAction_cardToCard);
        TextView sendMoneyTv = rootView.findViewById(R.id.tv_moneyAction_money);

        if (G.isDarkTheme) {
            cardToCardTv.setTextColor(getResources().getColor(R.color.white));
            sendMoneyTv.setTextColor(getResources().getColor(R.color.white));
        } else {
            cardToCardTv.setTextColor(getResources().getColor(R.color.black));
            sendMoneyTv.setTextColor(getResources().getColor(R.color.black));
        }

        anim.addUpdateListener(animation -> {
            int animProgress = (Integer) animation.getAnimatedValue();
            rootView.getLayoutParams().height = (int) (animProgress / 1.8);
            rootView.requestLayout();

            Log.i(TAG, "transferActionInit: " + animProgress);
        });

    }

    private void sendMoneyClicked() {
        transferRootView.setVisibility(View.VISIBLE);

        cancelBtn.setOnClickListener(v -> dismiss());

        userAvatarIv.setImageDrawable(drawable);
        userNameTv.setText(userName);
        if (G.selectedCard != null) {
            creditTv.setText(getString(R.string.wallet_Your_credit) + " " + String.format(getString(R.string.wallet_Reial), G.cardamount));
        } else {
            creditTv.setVisibility(View.GONE);
        }


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

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public int getTheme() {
        if (G.isDarkTheme) {
            return R.style.BaseBottomSheetDialog;
        } else {
            return R.style.BaseBottomSheetDialogLight;
        }
    }

    @Override
    public void receivedMessage(int id, Object... message) {

    }

    public interface MoneyTransferAction {
        void cardToCardClicked();

        void sendMoneyClicked();
    }
}
