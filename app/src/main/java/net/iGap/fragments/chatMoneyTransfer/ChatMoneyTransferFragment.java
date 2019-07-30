package net.iGap.fragments.chatMoneyTransfer;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vicmikhailau.maskededittext.MaskedEditText;

import net.iGap.G;
import net.iGap.R;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.module.CircleImageView;

import java.util.Locale;

public class ChatMoneyTransferFragment extends BottomSheetDialogFragment {
    private static final String TAG = "aabolfazl";
    private final String[] mPrice = {""};
    private View rootView;
    private View cardToCard;
    private View sendMoney;
    private View moneyActionRootView;
    private View transferRootView;
    private CircleImageView userAvatarIv;
    private TextView userNameTv;
    private TextView creditTv;
    private Drawable drawable;
    private Button cancelBtn;
    private View transferRootViewCard;
    private CircleImageView userAvatarIvCard;
    private TextView userNameTvCard;
    private TextView creditTvCard;
    private Button cancelBtnCard;
    private Button confirmBtnCard;
    private TextView amountTvCard;
    private TextView cardNumberTvCard;
    private EditText amountEtCard;
    private MaskedEditText cardNumberEtCard;

    private long userId;
    private MoneyTransferAction moneyTransferAction;
    private String userName;


    public static ChatMoneyTransferFragment getInstance(long userId, Drawable userPicture, String userName) {
        ChatMoneyTransferFragment transferAction = new ChatMoneyTransferFragment();
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
        /** wallet**/
        WalletTransferFragment sendMoneyFragment = new WalletTransferFragment();
        moneyActionRootView = rootView.findViewById(R.id.ll_moneyAction_root);
        sendMoney = rootView.findViewById(R.id.ll_moneyAction_sendMoney);
        cardToCard = rootView.findViewById(R.id.ll_moneyAction_cardToCard);
        transferRootView = rootView.findViewById(R.id.cl_moneyAction_transferRoot);
        userAvatarIv = rootView.findViewById(R.id.iv_moneyAction_userAvatar);
        userNameTv = rootView.findViewById(R.id.tv_moneyAction_userName);
        creditTv = rootView.findViewById(R.id.tv_moneyAction_credit);
        cancelBtn = rootView.findViewById(R.id.btn_moneyAction_cancel);
        TextView transferToTv = rootView.findViewById(R.id.tv_moneyAction_transferTo);
        Button confirmBtn = rootView.findViewById(R.id.btn_moneyAction_confirm);
        ProgressBar progressBar = rootView.findViewById(R.id.pb_moneyAction);
        FrameLayout layoutContainer = rootView.findViewById(R.id.fl_moneyAction_Container);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        TextView cardToCardIv = rootView.findViewById(R.id.iv_moneyAction_cardToCard);

        /** cardToCard **/
        TextView walletTransferIv = rootView.findViewById(R.id.iv_moneyAction_wallet);
        transferRootViewCard = rootView.findViewById(R.id.cl_cardToCard_transferRoot);
        userAvatarIvCard = rootView.findViewById(R.id.iv_cardToCard_userAvatar);
        userNameTvCard = rootView.findViewById(R.id.tv_cardToCard_userName);
        creditTvCard = rootView.findViewById(R.id.tv_cardToCard_credit);
        cancelBtnCard = rootView.findViewById(R.id.btn_cardToCard_cancel);
        confirmBtnCard = rootView.findViewById(R.id.btn_cardToCard_confirm);
        TextView transferToTvCard = rootView.findViewById(R.id.tv_cardToCard_transferTo);
        amountTvCard = view.findViewById(R.id.tv_chat_card_amountText);
        cardNumberTvCard = view.findViewById(R.id.et_chat_card_cardNumberTv);
        amountEtCard = view.findViewById(R.id.et_chat_card_cardamount);
        cardNumberEtCard = view.findViewById(R.id.et_chat_card_cardNumber);

        walletTransferIv.setText("0");
        cardToCardIv.setText("4");

        walletTransferIv.setTypeface(G.typeface_FonticonNew);
        cardToCardIv.setTypeface(G.typeface_FonticonNew);

        Utils.darkModeHandler(layoutContainer);
        Utils.darkModeHandler(creditTv);
        Utils.darkModeHandler(userNameTv);
        Utils.darkModeHandler(transferToTv);
        Utils.darkModeHandler(userNameTv);
        Utils.darkModeHandler(cardToCardIv);
        Utils.darkModeHandler(walletTransferIv);

        creditTv.setTextColor(Utils.darkModeHandler(getContext()));


        /**
         * because add fragment in other fragment can not use getFragmentManager
         * */

        sendMoneyFragment.setConfirmBtn(confirmBtn);
        sendMoneyFragment.setCancelBtn(cancelBtn);
        sendMoneyFragment.setUserId(userId);
        sendMoneyFragment.setProgressBar(progressBar);
        sendMoneyFragment.setFragmentManager(getChildFragmentManager());
        fragmentTransaction.add(layoutContainer.getId(), sendMoneyFragment);
        fragmentTransaction.commit();


    }

    @Override
    public void onStart() {
        super.onStart();

        userAvatarIv.setImageDrawable(drawable);
        userNameTv.setText(userName);

        if (G.selectedCard != null) {
            creditTv.setText(getString(R.string.wallet_Your_credit) + " " + String.format(getString(R.string.wallet_Reial), G.cardamount));
        } else {
            creditTv.setVisibility(View.GONE);
        }

        transferActionInit();


        amountEtCard.addTextChangedListener(new TextWatcher() {
            boolean isSettingText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPrice[0] = s.toString().replaceAll(",", "");

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isSettingText) return;
                isSettingText = true;
                String s1 = null;
                Log.d(TAG, "afterTextChanged: " + s);
                try {
                    s1 = String.format(Locale.US, "%,d", Long.parseLong(mPrice[0]));
                } catch (NumberFormatException e) {

                }

                amountEtCard.setText(s1);
                amountEtCard.setSelection(amountEtCard.length());

                isSettingText = false;

            }
        });


//        cardNumberEtCard.addTextChangedListener(new TextWatcher() {
//            private static final char space = '-';
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length() > 0 && (s.length() % 5) == 0) {
//                    final char c = s.charAt(s.length() - 1);
//                    if (space == c) {
//                        s.delete(s.length() - 1, s.length());
//                    }
//                }
//                // Insert char where needed.
//                if (s.length() > 0 && (s.length() % 5) == 0) {
//                    char c = s.charAt(s.length() - 1);
//                    // Only if its a digit where there should be a space we insert a space
//                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
//                        s.insert(s.length() - 1, String.valueOf(space));
//                    }
//                }
//            }
//
//        });
    }

    private void transferActionInit() {
        cardToCard.setOnClickListener(v -> {
            moneyTransferAction.cardToCardClicked();
            dismiss();
        });

        cancelBtn.setOnClickListener(v -> dismiss());
        cancelBtnCard.setOnClickListener(v -> dismiss());

        sendMoney.setOnClickListener(v -> {
            moneyActionRootView.setVisibility(View.GONE);
            transferRootView.setVisibility(View.VISIBLE);

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(300);
            transferRootView.setAnimation(fadeIn);

        });
        cardToCard.setOnClickListener(v -> {
            moneyActionRootView.setVisibility(View.GONE);
            transferRootViewCard.setVisibility(View.VISIBLE);
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(300);
            transferRootView.setAnimation(fadeIn);
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
    }

    private void sendMoneyClicked() {
        ValueAnimator anim = ValueAnimator.ofInt(500, transferRootView.getMeasuredHeight());
        anim.setDuration(500);

        anim.addUpdateListener(animation -> {
            int animProgress = (Integer) animation.getAnimatedValue();
            rootView.getLayoutParams().height = animProgress;
            rootView.requestLayout();
        });

        anim.start();
    }

    @Override
    public int getTheme() {
        if (G.isDarkTheme) {
            return R.style.BaseBottomSheetDialog;
        } else {
            return R.style.BaseBottomSheetDialogLight;
        }
    }

    public interface MoneyTransferAction {
        void cardToCardClicked();
    }
}
