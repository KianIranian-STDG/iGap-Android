package net.iGap.fragments.chatMoneyTransfer;

import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.vicmikhailau.maskededittext.MaskedEditText;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.CircleImageView;
import net.iGap.module.EmojiEditTextE;

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
    private Button cancelBtnCard;
    private EditText amountEtCard;
    private EmojiEditTextE descEtCard;
    private MaskedEditText cardNumberEtCard;
    private CardToCardCallBack cardToCardCallBack;

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

    public void setCardToCardCallBack(CardToCardCallBack cardTocartCallBack) {
        this.cardToCardCallBack = cardTocartCallBack;
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
        CircleImageView userAvatarIvCard = rootView.findViewById(R.id.iv_cardToCard_userAvatar);
        TextView userNameTvCard = rootView.findViewById(R.id.tv_cardToCard_userName);
        cancelBtnCard = rootView.findViewById(R.id.btn_cardToCard_cancel);
        Button confirmBtnCard = rootView.findViewById(R.id.btn_cardToCard_confirm);
        TextView bankNameTv = view.findViewById(R.id.tv_chat_card_bankName);
        amountEtCard = view.findViewById(R.id.et_chat_card_cardamount);
        cardNumberEtCard = view.findViewById(R.id.et_chat_card_cardNumber);
        descEtCard = view.findViewById(R.id.et_chat_card_desc);
        descEtCard.setImeOptions(EditorInfo.IME_ACTION_DONE);
        walletTransferIv.setText(R.string.financial_send_money_icon);
        cardToCardIv.setText(R.string.wallet_icon);

        userAvatarIvCard.setImageDrawable(drawable);
        userNameTvCard.setText(userName);

        walletTransferIv.setTypeface(ResourcesCompat.getFont(walletTransferIv.getContext() , R.font.font_icon));
        cardToCardIv.setTypeface(ResourcesCompat.getFont(cardToCardIv.getContext() , R.font.font_icon));

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


        confirmBtnCard.setOnClickListener(v -> {

            if (cardNumberEtCard.getText().toString().trim().length() == 19) {
                if (amountEtCard.getText().toString().trim().length() >= 6) {
                    if (descEtCard.getText().toString().trim().length() > 0) {
                        cardToCardCallBack.onClick(cardNumberEtCard.getText().toString(), amountEtCard.getText().toString(), descEtCard.getText().toString());
//                       cardToCardCallBack.onClick("6221-0612-1741-0739","10,000","سلام من ابوالفضلم بهممممممم پول بزن :)");
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.cardtocard_descriotion_error), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (amountEtCard.getText().toString().trim().length() == 0) {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.cardtocard_amount_empty_error), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), getContext().getResources().getString(R.string.cardtocard_morethan_error), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (cardNumberEtCard.getText().toString().trim().length() <= 19) {
                    Toast.makeText(getContext(), R.string.cardtocard_cardnumber_error, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.cardtocard_cartnumber_error, Toast.LENGTH_SHORT).show();
                }

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

                try {
                    s1 = String.format(Locale.US, "%,d", Long.parseLong(mPrice[0]));
                } catch (NumberFormatException e) {
                    Log.i(TAG, "afterTextChanged: " + e.getMessage());
                }

                amountEtCard.setText(s1);
                amountEtCard.setSelection(amountEtCard.length());

                isSettingText = false;

            }
        });
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
            transferRootViewCard.setAnimation(fadeIn);
        });
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
        return R.style.CardToCardBottomSheetDialog;
    }

    private String bankName(String cardNumber) {
        if (cardNumber.startsWith("603799"))
            return getString(R.string.bank_melli);
        else if (cardNumber.startsWith("589210"))
            return getString(R.string.bank_sepah);
        else if (cardNumber.startsWith("627648"))
            return getString(R.string.bank_tosee_saderat);
        else if (cardNumber.startsWith("627961"))
            return getString(R.string.bank_sanato_madan);
        else if (cardNumber.startsWith("603770"))
            return getString(R.string.bank_keshavarzi);
        else if (cardNumber.startsWith("628023"))
            return getString(R.string.bank_maskan);
        else if (cardNumber.startsWith("627760"))
            return getString(R.string.bank_post_bank);
        else if (cardNumber.startsWith("502908"))
            return getString(R.string.bank_tosee_taavon);
        else if (cardNumber.startsWith("627412"))
            return getString(R.string.bank_eghtesad_novin);
        else if (cardNumber.startsWith("622106"))
            return getString(R.string.bank_parsian);
        else if (cardNumber.startsWith("502229"))
            return getString(R.string.bank_pasargad);
        else if (cardNumber.startsWith("627488"))
            return getString(R.string.bank_karafarin);
        else if (cardNumber.startsWith("621986"))
            return getString(R.string.bank_saman);
        else if (cardNumber.startsWith("639346"))
            return getString(R.string.bank_sina);
        else if (cardNumber.startsWith("639607"))
            return getString(R.string.bank_sarmayeh);
        else if (cardNumber.startsWith("502806"))
            return getString(R.string.bank_shahr);
        else if (cardNumber.startsWith("502938"))
            return getString(R.string.bank_dey);
        else if (cardNumber.startsWith("603769"))
            return getString(R.string.bank_saderat);
        else if (cardNumber.startsWith("610433"))
            return getString(R.string.bank_mellat);
        else if (cardNumber.startsWith("627353"))
            return getString(R.string.bank_tejarat);
        else if (cardNumber.startsWith("585983"))
            return getString(R.string.bank_tejarat);
        else if (cardNumber.startsWith("627381"))
            return getString(R.string.bank_ansar);
        else if (cardNumber.startsWith("639370"))
            return getString(R.string.bank_mehr_eghtesad);
        else
            return "";
    }

    public interface MoneyTransferAction {
        void cardToCardClicked();
    }

    public interface CardToCardCallBack {
        void onClick(String cardNum, String amountNum, String descriptionTv);
    }
}
