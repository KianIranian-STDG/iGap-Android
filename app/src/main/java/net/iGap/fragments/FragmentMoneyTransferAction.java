package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;

public class FragmentMoneyTransferAction extends BottomSheetDialogFragment {
    private View rootView;

    private MoneyTransferAction moneyTransferAction;

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
    public void onStart() {
        super.onStart();

        View cardToCard = rootView.findViewById(R.id.ll_moneyAction_cardToCard);
        cardToCard.setOnClickListener(v -> {
            moneyTransferAction.cardToCardClicked();
            dismiss();
        });

        View sendMoney = rootView.findViewById(R.id.ll_moneyAction_sendMoney);
        sendMoney.setOnClickListener(v -> {
            moneyTransferAction.sendMoneyClicked();
            dismiss();
        });

        TextView call = rootView.findViewById(R.id.tv_moneyAction_cardToCard);
        TextView message = rootView.findViewById(R.id.tv_moneyAction_money);

        if (G.isDarkTheme) {
            call.setTextColor(getResources().getColor(R.color.white));
            message.setTextColor(getResources().getColor(R.color.white));
        } else {
            call.setTextColor(getResources().getColor(R.color.black));
            message.setTextColor(getResources().getColor(R.color.black));
        }
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

        void sendMoneyClicked();
    }
}
