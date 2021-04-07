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
import net.iGap.fragments.payment.FragmentPaymentInternet;
import net.iGap.fragments.payment.PaymentChargeFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperWallet;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.proto.ProtoWalletPaymentInit;

import org.paygear.WalletActivity;
import org.paygear.model.Card;
import org.paygear.model.Payment;
import org.paygear.model.PaymentAuth;

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

    public void loadTransferMoneyPage() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.transferMoneyContainer);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putLong("peerId", peerId);
        if (!(fragment instanceof TransferMoneyFragment)) {
            fragment = new TransferMoneyFragment();
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

    public void showPasswordFragment(ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder initPayResponse, Card selectedCard) {
        PaymentAuth paymentAuth = new PaymentAuth();
        paymentAuth.publicKey = initPayResponse.getPublicKey();
        paymentAuth.token = initPayResponse.getToken();

        WalletPasswordFragment passwordFragment = new WalletPasswordFragment();
        passwordFragment.setPaymentAuth(paymentAuth);
        passwordFragment.setSelectedCard(selectedCard);

        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putLong("peerId", peerId);
        passwordFragment.setArguments(bundle);

        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.transferMoneyContainer, passwordFragment, "passwordFragment")
                .addToBackStack(passwordFragment.getClass().getName())
                .commit();
    }

    public void showWalletActivity(ProtoWalletPaymentInit.WalletPaymentInitResponse.Builder initPayResponse, long price) {
        Payment payment = new Payment();
        PaymentAuth paymentAuth = new PaymentAuth();
        paymentAuth.token = initPayResponse.getToken();
        paymentAuth.publicKey = initPayResponse.getPublicKey();
        payment.account = null;
        payment.paymentAuth = paymentAuth;
        payment.isCredit = false;
        payment.orderId = null;
        payment.price = price;
        startActivityForResult(new HelperWallet().goToWallet(payment, getContext(), new Intent(getActivity(), WalletActivity.class), "0" + AccountManager.getInstance().getCurrentUser().getId(), false), 66);
        dismiss();
    }

    public void setWalletPassword() {
        WalletConfirmPasswordFragment confirmPasswordFragment = new WalletConfirmPasswordFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putLong("peerId", peerId);
        confirmPasswordFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.transferMoneyContainer, confirmPasswordFragment, "confirmPasswordFragment")
                .addToBackStack(confirmPasswordFragment.getClass().getName())
                .commit();
    }

    public void loadChargePayment() {
        Fragment fragment = PaymentChargeFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putLong("peerId", peerId);
        fragment.setArguments(bundle);
        new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        dismiss();
    }

    public void loadInternetPayment() {
        Fragment fragment = FragmentPaymentInternet.newInstance();
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
        void cardToCardClicked(String cardNum, String amountNum, String descriptionTv);
    }
}
