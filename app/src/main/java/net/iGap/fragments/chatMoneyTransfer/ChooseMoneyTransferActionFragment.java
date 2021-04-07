package net.iGap.fragments.chatMoneyTransfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.iGap.R;
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

        view.findViewById(R.id.cardToCard).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadRequestCartToCardPage();
            }
        });

        view.findViewById(R.id.transferMoney).setVisibility((isWalletActive && isWalletRegister) ? View.VISIBLE : View.GONE);

        view.findViewById(R.id.transferMoney).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadTransferMoneyPage();
            }
        });

        view.findViewById(R.id.stickerGift).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
           //     ((ParentChatMoneyTransferFragment) getParentFragment()).loadGiftSticker();
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
