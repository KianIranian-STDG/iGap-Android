package net.iGap.fragments.chatMoneyTransfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.realm.RealmUserInfo;

public class ChooseMoneyTransferActionFragment extends Fragment {

    private boolean isWalletActive = false;
    private boolean isWalletRegister = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo userInfo = RealmUserInfo.getRealmUserInfo(realm);
            if (userInfo != null) {
                isWalletActive = userInfo.isWalletActive();
                isWalletRegister = userInfo.isWalletRegister();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_money_transfer_action_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout mainContainer = view.findViewById(R.id.mainContainer);
        mainContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        AppCompatTextView buyDigitalGiftCard = view.findViewById(R.id.buyDigitalGiftCard);
        buyDigitalGiftCard.setTextColor(Theme.getColor(Theme.key_icon));
        AppCompatTextView requestCardToCard = view.findViewById(R.id.requestCardToCard);
        requestCardToCard.setTextColor(Theme.getColor(Theme.key_icon));
        AppCompatTextView walletMoneyTransfer = view.findViewById(R.id.walletMoneyTransfer);
        walletMoneyTransfer.setTextColor(Theme.getColor(Theme.key_icon));
        AppCompatTextView buyingTheCharge = view.findViewById(R.id.buyingTheCharge);
        buyingTheCharge.setTextColor(Theme.getColor(Theme.key_icon));
        AppCompatTextView buyInternetPackage = view.findViewById(R.id.buyInternetPackage);
        buyInternetPackage.setTextColor(Theme.getColor(Theme.key_icon));
        AppCompatTextView cardToCardIcon = view.findViewById(R.id.cardToCardIcon);
        cardToCardIcon.setTextColor(Theme.getColor(Theme.key_icon));
        AppCompatTextView giftStickerIcon = view.findViewById(R.id.giftStickerIcon);
        giftStickerIcon.setTextColor(Theme.getColor(Theme.key_icon));
        AppCompatTextView chargePaymentIcon = view.findViewById(R.id.chargePaymentIcon);
        chargePaymentIcon.setTextColor(Theme.getColor(Theme.key_icon));
        AppCompatTextView internetPaymentIcon = view.findViewById(R.id.internetPaymentIcon);
        internetPaymentIcon.setTextColor(Theme.getColor(Theme.key_icon));


        view.findViewById(R.id.cardToCard).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadRequestCartToCardPage();
            }
        });

        //view.findViewById(R.id.transferMoney).setVisibility((isWalletActive && isWalletRegister) ? View.VISIBLE : View.GONE);
//
//        view.findViewById(R.id.transferMoney).setOnClickListener(v -> {
//            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
//                ((ParentChatMoneyTransferFragment) getParentFragment()).loadTransferMoneyPage();
//            }
//        });

        view.findViewById(R.id.stickerGift).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadGiftSticker();
            }
        });

        view.findViewById(R.id.chargePayment).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadChargePayment();
            }
        });

        view.findViewById(R.id.internetPayment).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadInternetPayment();
            }
        });
    }
}
