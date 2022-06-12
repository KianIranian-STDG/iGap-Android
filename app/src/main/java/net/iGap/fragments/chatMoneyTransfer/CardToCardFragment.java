package net.iGap.fragments.chatMoneyTransfer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import net.iGap.R;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.theme.Theme;

import java.util.Locale;

public class CardToCardFragment extends Fragment {

    private AvatarHandler avatarHandler;
    private String userName;
    private long peerId;

    private final String[] mPrice = {""};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString("userName", "");
            peerId = getArguments().getLong("peerId", -1);
            avatarHandler = new AvatarHandler();
        } else {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_to_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarHandler.getAvatar(new ParamWithAvatarType(view.findViewById(R.id.userAvatar), peerId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());

        AppCompatTextView userNameTextView = view.findViewById(R.id.tv_cardToCard_transferTo);
        userNameTextView.setTextColor(Theme.getColor(Theme.key_default_text));
        AppCompatTextView bankNameTv = view.findViewById(R.id.tv_chat_card_bankName);
        bankNameTv.setTextColor(Theme.getColor(Theme.key_default_text));
        userNameTextView.setText(String.format(getString(R.string.money_request), userName));
        AppCompatTextView amountText = view.findViewById(R.id.tv_chat_card_amountText);
        amountText.setTextColor(Theme.getColor(Theme.key_default_text));
        AppCompatTextView cardNumberTv = view.findViewById(R.id.et_chat_card_cardNumberTv);
        cardNumberTv.setTextColor(Theme.getColor(Theme.key_default_text));
        AppCompatTextView card_desc = view.findViewById(R.id.tv_chat_card_desc);
        card_desc.setTextColor(Theme.getColor(Theme.key_default_text));
        AppCompatEditText cardNumberEtCard = view.findViewById(R.id.et_chat_card_cardNumber);
        cardNumberEtCard.setTextColor(Theme.getColor(Theme.key_default_text));
        cardNumberEtCard.setHintTextColor(Theme.getColor(Theme.key_theme_color));
        AppCompatEditText amountEtCard = view.findViewById(R.id.et_chat_card_cardamount);
        amountEtCard.setTextColor(Theme.getColor(Theme.key_default_text));
        amountEtCard.setHintTextColor(Theme.getColor(Theme.key_default_text));
        AppCompatEditText descEtCard = view.findViewById(R.id.et_chat_card_desc);
        descEtCard.setTextColor(Theme.getColor(Theme.key_default_text));
        descEtCard.setHintTextColor(Theme.getColor(Theme.key_default_text));
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

                try {
                    s1 = String.format(Locale.US, "%,d", Long.parseLong(mPrice[0]));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                amountEtCard.setText(s1);
                amountEtCard.setSelection(amountEtCard.length());

                isSettingText = false;

            }
        });

        cardNumberEtCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 7) {
                    bankNameTv.setText(bankName(cardNumberEtCard.getEditableText().toString().replace("-", "")));
                } else {
                    bankNameTv.setText("");
                }
            }
        });

        view.findViewById(R.id.btn_cardToCard_confirm).setOnClickListener(v -> {
            if (cardNumberEtCard.getEditableText().toString().trim().length() == 19) {
                if (amountEtCard.getEditableText().toString().trim().length() >= 6) {
                    if (descEtCard.getEditableText().toString().trim().length() > 0) {
                        if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                            ((ParentChatMoneyTransferFragment) getParentFragment()).finishedCardToCard(cardNumberEtCard.getEditableText().toString(), amountEtCard.getEditableText().toString(), descEtCard.getEditableText().toString());
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.cardtocard_descriotion_error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (amountEtCard.getEditableText().toString().trim().length() == 0) {
                        Toast.makeText(getContext(), R.string.cardtocard_amount_empty_error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.cardtocard_morethan_error, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (cardNumberEtCard.getEditableText().toString().trim().length() <= 19) {
                    Toast.makeText(getContext(), R.string.cardtocard_cardnumber_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.cardtocard_cartnumber_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.btn_cardToCard_cancel).setOnClickListener(v -> {
            if (getParentFragment() instanceof ParentChatMoneyTransferFragment) {
                ((ParentChatMoneyTransferFragment) getParentFragment()).dismissDialog();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        avatarHandler.registerChangeFromOtherAvatarHandler();
    }

    @Override
    public void onStop() {
        super.onStop();
        avatarHandler.unregisterChangeFromOtherAvatarHandler();
    }

    private int bankName(String cardNumber) {
        if (cardNumber.startsWith("603799"))
            return R.string.bank_melli;
        else if (cardNumber.startsWith("589210"))
            return R.string.bank_sepah;
        else if (cardNumber.startsWith("627648"))
            return R.string.bank_tosee_saderat;
        else if (cardNumber.startsWith("627961"))
            return R.string.bank_sanato_madan;
        else if (cardNumber.startsWith("603770"))
            return R.string.bank_keshavarzi;
        else if (cardNumber.startsWith("628023"))
            return R.string.bank_maskan;
        else if (cardNumber.startsWith("627760"))
            return R.string.bank_post_bank;
        else if (cardNumber.startsWith("502908"))
            return R.string.bank_tosee_taavon;
        else if (cardNumber.startsWith("627412"))
            return R.string.bank_eghtesad_novin;
        else if (cardNumber.startsWith("622106"))
            return R.string.bank_parsian;
        else if (cardNumber.startsWith("502229"))
            return R.string.bank_pasargad;
        else if (cardNumber.startsWith("627488"))
            return R.string.bank_karafarin;
        else if (cardNumber.startsWith("621986"))
            return R.string.bank_saman;
        else if (cardNumber.startsWith("639346"))
            return R.string.bank_sina;
        else if (cardNumber.startsWith("639607"))
            return R.string.bank_sarmayeh;
        else if (cardNumber.startsWith("502806"))
            return R.string.bank_shahr;
        else if (cardNumber.startsWith("502938"))
            return R.string.bank_dey;
        else if (cardNumber.startsWith("603769"))
            return R.string.bank_saderat;
        else if (cardNumber.startsWith("610433"))
            return R.string.bank_mellat;
        else if (cardNumber.startsWith("627353"))
            return R.string.bank_tejarat;
        else if (cardNumber.startsWith("585983"))
            return R.string.bank_tejarat;
        else if (cardNumber.startsWith("627381"))
            return R.string.bank_ansar;
        else if (cardNumber.startsWith("639370"))
            return R.string.bank_mehr_eghtesad;
        else
            return R.string.empty_error_message;
    }


}
