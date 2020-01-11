package net.iGap.fragments.chatMoneyTransfer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.iGap.R;

public class ChooseMoneyTransferActionFragment extends Fragment {

    private boolean isWalletActive;
    private boolean isWalletRegister;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isWalletActive = getArguments().getBoolean("isWalletActive", false);
            isWalletRegister = getArguments().getBoolean("isWalletRegister", false);
        }
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

        if (isWalletActive && isWalletRegister) {
            view.findViewById(R.id.transferMoney).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.transferMoney).setVisibility(View.GONE);
        }

        view.findViewById(R.id.transferMoney).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadTransferMoneyPage();
            }
        });

        view.findViewById(R.id.stickerGift).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).loadGiftSticker();
            }
        });
    }
}
