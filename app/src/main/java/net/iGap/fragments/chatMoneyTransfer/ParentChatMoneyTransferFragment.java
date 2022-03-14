package net.iGap.fragments.chatMoneyTransfer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import net.iGap.R;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.fragments.payment.ChargeFragment;
import net.iGap.fragments.payment.InternetFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.module.dialog.BaseBottomSheet;


public class ParentChatMoneyTransferFragment extends BaseBottomSheet {

    private long roomId;
    private String userName;
    private long peerId;
    private boolean isWalletActive;
    private boolean isWalletRegister;
    private String phoneNumber;

    public Delegate delegate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString("userName", "");
            roomId = getArguments().getLong("roomId", -1);
            peerId = getArguments().getLong("peerId", -1);
            isWalletActive = getArguments().getBoolean("isWalletActive", false);
            isWalletRegister = getArguments().getBoolean("isWalletRegister", false);
            phoneNumber = getArguments().getString("phoneNumber", "");
        } else {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadStep();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parent_chat_money_transfer, container, false);
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    private void loadStep() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        if (!(fragment instanceof ChooseMoneyTransferActionFragment)) {
            fragment = new ChooseMoneyTransferActionFragment();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("isWalletActive", isWalletActive);
        bundle.putBoolean("isWalletRegister", isWalletRegister);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.transferMoneyContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void loadRequestCartToCardPage() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putLong("peerId", peerId);
        if (!(fragment instanceof CardToCardFragment)) {
            fragment = new CardToCardFragment();
            fragmentTransaction.addToBackStack(fragment.getClass().getName());
        }
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.transferMoneyContainer, fragment, fragment.getClass().getName()).commit();
    }

    public void finishedCardToCard(String cardNum, String amountNum, String descriptionTv) {
        dismiss();
        delegate.cardToCardClicked(cardNum, amountNum, descriptionTv);
//                       cardToCardCallBack.onClick("6221-0612-1741-0739","10,000","سلام من ابوالفضلم بهممممممم پول بزن :)");
    }

    public void loadChargePayment() {
        Fragment fragment = ChargeFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putLong("peerId", peerId);
        fragment.setArguments(bundle);
        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        dismiss();
    }

    public void loadInternetPayment() {
        Fragment fragment = InternetFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putLong("peerId", peerId);
        fragment.setArguments(bundle);
        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        dismiss();
    }

    public void dismissDialog() {
        dismiss();
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public interface Delegate {
        void onGiftStickerGetStartPayment(StructIGSticker structIGSticker, String paymentToke);

        void cardToCardClicked(String cardNum, String amountNum, String descriptionTv);
    }
}
