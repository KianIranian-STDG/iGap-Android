package net.iGap.fragments.chatMoneyTransfer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class ChatCardToCardFragment extends BaseFragment {
    private View rootView;
    private TextView amountTv;
    private TextView cardNumberTv;
    private EditText amountEt;
    private EditText cardNumberEt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_chat_cardtocard, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        amountTv = view.findViewById(R.id.tv_chat_card_amountText);
        cardNumberTv = view.findViewById(R.id.et_chat_card_cardNumberTv);
        amountEt = view.findViewById(R.id.et_chat_card_amount);
        cardNumberEt = view.findViewById(R.id.et_chat_card_cardNumber);

        amountTv.setTextColor(Utils.darkModeHandler(getContext()));
        cardNumberTv.setTextColor(Utils.darkModeHandler(getContext()));
        amountEt.setTextColor(Utils.darkModeHandler(getContext()));
        cardNumberEt.setTextColor(Utils.darkModeHandler(getContext()));

        Utils.darkModeHandler(rootView);

    }
}
