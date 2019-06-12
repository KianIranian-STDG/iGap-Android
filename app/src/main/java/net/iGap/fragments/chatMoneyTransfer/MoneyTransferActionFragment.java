package net.iGap.fragments.chatMoneyTransfer;

import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.CircleImageView;

public class MoneyTransferActionFragment extends BottomSheetDialogFragment {
    private static final String TAG = "aabolfazl";

    private View rootView;
    private View cardToCard;
    private View sendMoney;
    private View moneyActionRootView;
    private View transferRootView;
    private CircleImageView userAvatarIv;
    private TextView userNameTv;
    private TextView creditTv;
    private Drawable drawable;
    private ProgressBar progressBar;
    private Button confirmBtn;
    private Button cancelBtn;

    private long userId;
    private ValueAnimator anim;
    private MoneyTransferAction moneyTransferAction;
    private String userName;
    private int windowHeight;


    public static MoneyTransferActionFragment getInstance(long userId, Drawable userPicture, String userName) {
        MoneyTransferActionFragment transferAction = new MoneyTransferActionFragment();
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
        progressBar = rootView.findViewById(R.id.pb_moneyAction);
        confirmBtn = rootView.findViewById(R.id.btn_moneyAction_confirm);
        cancelBtn = rootView.findViewById(R.id.btn_moneyAction_cancel);
    }

    @Override
    public void onStart() {
        super.onStart();
        windowHeight = getScreenHeight()/2;

        userAvatarIv.setImageDrawable(drawable);
        userNameTv.setText(userName);

        if (G.selectedCard != null) {
            creditTv.setText(getString(R.string.wallet_Your_credit) + " " + String.format(getString(R.string.wallet_Reial), G.cardamount));
        } else {
            creditTv.setVisibility(View.GONE);
        }

        transferActionInit();
    }

    private void transferActionInit() {
        cardToCard.setOnClickListener(v -> {
            moneyTransferAction.cardToCardClicked();
            dismiss();
        });

        sendMoney.setOnClickListener(v -> {
            moneyActionRootView.setVisibility(View.GONE);
            transferRootView.setVisibility(View.VISIBLE);
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
    }

    private void sendMoneyClicked() {

//        anim = ValueAnimator.ofInt(600, windowHeight);
//        anim.setDuration(500);
//
//        anim.addUpdateListener(animation -> {
//            int animProgress = (Integer) animation.getAnimatedValue();
//            rootView.getLayoutParams().height = animProgress;
//            rootView.requestLayout();
//
//            Log.i(TAG, "transferActionInit: " + animProgress);
//        });
//
//        anim.start();

        WalletTransferFragment sendMoneyFragment = new WalletTransferFragment();
        sendMoneyFragment.setCancelBtn(cancelBtn);
        sendMoneyFragment.setConfirmBtn(confirmBtn);
        sendMoneyFragment.setUserId(userId);
        sendMoneyFragment.setProgressBar(progressBar);
        sendMoneyFragment.setFragmentManager(getChildFragmentManager());

        /**
         * because add fragment in other fragment can not use getFragmentManager
         * */

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl_moneyAction_Container, sendMoneyFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


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

    public interface MoneyTransferAction {
        void cardToCardClicked();
    }
}
