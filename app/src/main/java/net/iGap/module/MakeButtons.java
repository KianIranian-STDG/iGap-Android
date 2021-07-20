package net.iGap.module;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.additionalData.ButtonEntity;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGlobal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.Gravity.CENTER;
import static android.widget.LinearLayout.HORIZONTAL;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class MakeButtons {
    private static HashMap<Integer, JSONArray> buttonList;

    public static HashMap<Integer, JSONArray> parseData(String json) {
        buttonList = new HashMap<>();
        try {
            JSONArray jsonElements = new JSONArray(json);
            for (int i = 0; i < jsonElements.length(); i++) {
                buttonList.put(i, jsonElements.getJSONArray(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                buttonList.put(0, new JSONObject(json).getJSONArray(""));
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return buttonList;
    }

    public static LinearLayout createLayout(Context context) {
        LinearLayout linearLayout_179 = new LinearLayout(context);
        linearLayout_179.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_937 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_937.topMargin = 2;
        linearLayout_179.setLayoutParams(layout_937);
        return linearLayout_179;
    }

    @FunctionalInterface
    public interface OnClickListener {
        void onClick(View view, ButtonEntity buttonEntity);
    }

    public static LinearLayout addButtons(Theme theme, ButtonEntity entity, int culmn, float wightSum, LinearLayout mainLayout, OnClickListener onClickListener) {
        float weight = wightSum / culmn;
        float weightSum = 0;
        float textWeight = 0f;
        float imageWeight = 0f;
        if (culmn == 1) {
            if (!entity.getImageUrl().equals("")) {
                weightSum = 5f;
                textWeight = 4f;
                imageWeight = 1f;
            } else {
                weightSum = 1f;
                textWeight = 1f;
            }
        } else if (culmn == 2) {
            if (!entity.getImageUrl().equals("")) {
                weightSum = .5f;
                textWeight = .33f;
                imageWeight = .16f;
            } else {
                weightSum = .5f;
                textWeight = .5f;
            }
        } else if (culmn == 3) {
            if (!entity.getImageUrl().equals("")) {
                weightSum = 3f;
                textWeight = 1.8f;
                imageWeight = 1.2f;
            } else {
                weightSum = 3f;
                textWeight = 3f;
            }
        } else if (culmn == 4) {
            if (!entity.getImageUrl().equals("")) {
                weightSum = 4f;
                textWeight = 2.6f;
                imageWeight = 1.4f;
            } else {
                weightSum = 4f;
                textWeight = 4f;
            }
        }

        CardView cardView = new CardView(mainLayout.getContext());
        cardView.setLayoutParams(LayoutCreator.createLinear(0, LayoutCreator.WRAP_CONTENT, weight, CENTER, 2, 2, 2, 2));
        cardView.setRadius(2);
        cardView.setCardElevation(2);

        LinearLayout linearLayout_529 = new LinearLayout(mainLayout.getContext());
        LinearLayout.LayoutParams layout_941 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp32));

        layout_941.gravity = Gravity.CENTER;
        layout_941.setMargins(0, i_Dp(R.dimen.dp1), 0, i_Dp(R.dimen.dp1));
        linearLayout_529.setLayoutParams(layout_941);
        linearLayout_529.setWeightSum(weightSum);

        AppCompatImageView img1 = new AppCompatImageView(mainLayout.getContext());

        if (!entity.getImageUrl().equals("")) {
            Glide.with(G.context).load(entity.getImageUrl()).override(i_Dp(R.dimen.dp32), i_Dp(R.dimen.dp32)).into(img1);

            LinearLayout.LayoutParams layout_738 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

            layout_738.weight = imageWeight;
            layout_738.setMargins(0, i_Dp(R.dimen.dp2), i_Dp(R.dimen.dp10), i_Dp(R.dimen.dp2));
            layout_738.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;

            img1.setLayoutParams(layout_738);
            linearLayout_529.addView(img1);
        }


        TextView textView = new AppCompatTextView(mainLayout.getContext()) {
            EventManager.EventDelegate eventListener = (id, account, args) -> {
                if (id == EventManager.EMOJI_LOADED) {
                    G.runOnUiThread(this::invalidate);
                }
            };

            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                EventManager.getInstance(AccountManager.selectedAccount).addObserver(EventManager.EMOJI_LOADED, eventListener);
            }

            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                EventManager.getInstance(AccountManager.selectedAccount).removeObserver(EventManager.EMOJI_LOADED, eventListener);
            }
        };

        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity(CENTER);
        textView.setPadding(i_Dp(R.dimen.dp1), i_Dp(R.dimen.dp1), i_Dp(R.dimen.dp1), i_Dp(R.dimen.dp1));
        textView.setMaxLines(1);
        textView.setTypeface(ResourcesCompat.getFont(textView.getContext(), R.font.main_font_bold));

        if (entity.getActionType() == ProtoGlobal.DiscoveryField.ButtonActionType.CARD_TO_CARD.getNumber()) {
            textView.setText(R.string.cardToCardBtnText);
        } else {
            textView.setText(EmojiManager.getInstance().replaceEmoji(entity.getLable(), textView.getPaint().getFontMetricsInt(), -1, false));
        }

        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        cardView.setBackgroundResource(theme.getCardToCardButtonBackground(mainLayout.getContext()));
        textView.setTextColor(theme.getReceivedMessageColor(textView.getContext()));

        LinearLayout.LayoutParams layout_844 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

        layout_844.setMargins(0, 0, i_Dp(R.dimen.dp2), 0);
        layout_844.weight = textWeight;
        textView.setLayoutParams(layout_844);

        linearLayout_529.addView(textView);

        cardView.addView(linearLayout_529);

        ArrayList<String> actions = new ArrayList<>();
        if (entity.getActionType() == ProtoGlobal.DiscoveryField.ButtonActionType.CARD_TO_CARD.getNumber()) {
            actions.add(entity.getValue().toString());
        } else {
            actions.add(entity.getValue().toString());
        }
        actions.add(entity.getLable());
        actions.add(entity.getJsonObject());
        cardView.setTag(actions);

        cardView.setId(entity.getActionType());
        cardView.setFocusableInTouchMode(false);
        cardView.setOnClickListener(v -> onClickListener.onClick(v, entity));
        mainLayout.addView(cardView);

        return mainLayout;
    }

}
