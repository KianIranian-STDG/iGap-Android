package net.iGap.module;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.module.additionalData.ButtonEntity;
import net.iGap.module.structs.StructWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.view.Gravity.CENTER;
import static android.widget.LinearLayout.HORIZONTAL;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class MakeButtons {
    private static LinearLayout childLayout;
    private static HashMap<Integer, JSONArray> buttonList;
    private static Gson gson;


    public static HashMap<Integer, JSONArray> parseData(String json) {

        // childLayout = createLayout();
        // parsedList = parsJson(mMessage.additionalData.additionalData);

        gson = new GsonBuilder().create();

        ArrayList<ButtonEntity> jsonList = new ArrayList<>();

        buttonList = new HashMap<>();

        try {
            //    JSONObject jObject = new JSONObject(mJson);

            JSONArray jsonElements = new JSONArray(json);
            //   JSONArray jsonElements = new JSONArray(mJson);


            //   rows = jsonElements.length();
            for (int i = 0; i < jsonElements.length(); i++) {
                // jsonElements.get(0);
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

    public static LinearLayout createLayout() {
        LinearLayout linearLayout_179 = new LinearLayout(G.context);
        linearLayout_179.setOrientation(HORIZONTAL);
        LinearLayout.LayoutParams layout_937 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout_937.topMargin = 4;
        linearLayout_179.setLayoutParams(layout_937);
        return linearLayout_179;
    }

    public static LinearLayout addButtons(ButtonEntity entity, View.OnClickListener clickListener, int culmn, float wightSum, int btnId, LinearLayout mainLayout, Integer additionalType) {
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
        CardView card = new CardView(G.context);

        // Set the CardView layoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp2), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp2));
        //      card.setPadding(i_Dp(R.dimen.dp15), 0, i_Dp(R.dimen.dp15), 0);

        params.weight = weight;
        card.setLayoutParams(params);

        // Set CardView corner radius
        card.setRadius(16);

        card.setCardElevation(2);

        // Set cardView content padding
        //card.setContentPadding(15, 15, 15, 15);

        // Set a background color for CardView

    /*    if (additionalType == AdditionalType.UNDER_KEYBOARD_BUTTON) {
            card.setCardBackgroundColor(Color.parseColor("#20000000"));
        }*/





        LinearLayout linearLayout_529 = new LinearLayout(G.context);
        LinearLayout.LayoutParams layout_941 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, i_Dp(R.dimen.dp36));

        layout_941.gravity = Gravity.CENTER;
        layout_941.setMargins(0, i_Dp(R.dimen.dp4), 0, i_Dp(R.dimen.dp4));
        linearLayout_529.setLayoutParams(layout_941);
        linearLayout_529.setWeightSum(weightSum);

        ImageView img1 = new ImageView(G.context);

        /*img1.setId(1);
        img1.setTag("abc");*/
        if (!entity.getImageUrl().equals("")) {
            Picasso.get()
                    .load(entity.getImageUrl())
                    .resize(i_Dp(R.dimen.dp32), i_Dp(R.dimen.dp32))
                    .into(img1);


            LinearLayout.LayoutParams layout_738 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

            layout_738.weight = imageWeight;
            layout_738.setMargins(0, i_Dp(R.dimen.dp2), i_Dp(R.dimen.dp10), i_Dp(R.dimen.dp2));
            layout_738.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;





            img1.setLayoutParams(layout_738);
            linearLayout_529.addView(img1);
        }

        if (entity.getLable().trim() != null) {
            TextView btn1 = new TextView(G.context);

            // btn1.setId(R.id.btn1);
            btn1.setEllipsize(TextUtils.TruncateAt.END);
            btn1.setGravity(CENTER);
            btn1.setMaxLines(1);
            btn1.setTypeface(G.typeface_IRANSansMobile);
            btn1.setText(entity.getLable());

            btn1.setTextSize(16);
            if (Build.VERSION.SDK_INT < 21) {
                card.setCardBackgroundColor(Color.parseColor("#cfd8dc"));
                btn1.setTextColor(Color.parseColor("#000000"));
            }

            LinearLayout.LayoutParams layout_844 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);

            layout_844.setMargins(0, 0, i_Dp(R.dimen.dp2), 0);
            layout_844.weight = textWeight;
            //     btn1.setBackgroundColor(Color.parseColor("#FF0000"));
            btn1.setLayoutParams(layout_844);

            linearLayout_529.addView(btn1);
        }
        card.addView(linearLayout_529);

        ArrayList<String> actions = new ArrayList<>();
        actions.add(entity.getValue());
        actions.add(entity.getLable());
        actions.add(entity.getJsonObject());
        card.setTag(actions);


        card.setId(entity.getActionType());
/** add this section to make inside effect
            /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && additionalType != AdditionalType.UNDER_KEYBOARD_BUTTON) {
                  StateListAnimator stateListAnimator = AnimatorInflater
                  .loadStateListAnimator(G.context, R.animator.lift_on_touch);
                  card.setStateListAnimator(stateListAnimator);
 }*/

        //    card.setLongClickable(false);

/*        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                card.setCardBackgroundColor(Color.parseColor("#ffffff"));
                return true;
            }
        });*/
        card.setFocusableInTouchMode(false);
        card.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                                            card.setCardBackgroundColor(Color.parseColor("#ffffff"));
                                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                            //    event.addBatch(0,0,0,0,0,0);
                                            //            card.setCardBackgroundColor(Color.parseColor("#ffffff"));
                                        } else if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

                                            card.setCardBackgroundColor(Color.parseColor("#20000000"));

                                        } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                                            /* Reset Color */
                                            card.setCardBackgroundColor(Color.parseColor("#ffffff"));
                                            //  card.setOnClickListener(clickListener);

                                        }
                                        return false;
                                    }
                                }
        );
        card.setOnClickListener(clickListener);
        mainLayout.addView(card);
        return mainLayout;


    }

    public static Drawable getSelectedItemDrawable() {
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray ta = G.context.obtainStyledAttributes(attrs);
        Drawable selectedItemDrawable = ta.getDrawable(0);
        ta.recycle();
        return selectedItemDrawable;
    }

}
