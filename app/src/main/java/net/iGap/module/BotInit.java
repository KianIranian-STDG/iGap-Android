package net.iGap.module;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;

import java.util.ArrayList;

public class BotInit {

    private ArrayList<StructRowBotAction> botActionList;
    private View layoutBot;
    private View rootView;

    public BotInit(View rootView, boolean showCommandList) {
        this.rootView = rootView;
        init(rootView);

    }

    public void updateCommandList(boolean showCommandList, String message, Activity activity, boolean backToMenu) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fillList(message, backToMenu);

                if (botActionList.size() == 0) {
                    return;
                }

                if (showCommandList) {
                    makeTxtList(rootView);
                } else {
                    makeButtonList(rootView);
                }

                setLayoutBot(false, false);
            }
        });

    }


    private void init(View rootView) {

        MaterialDesignTextView btnShowBot = (MaterialDesignTextView) rootView.findViewById(R.id.chl_btn_show_bot_action);
        btnShowBot.setVisibility(View.VISIBLE);

        layoutBot = rootView.findViewById(R.id.layout_bot);

        btnShowBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutBot(layoutBot.getVisibility() == View.VISIBLE, true);
            }
        });

    }

    private void setLayoutBot(boolean gone, boolean changeKeyboard) {

        if (botActionList.size() == 0) {
            return;
        }

        MaterialDesignTextView btnShowBot = (MaterialDesignTextView) rootView.findViewById(R.id.chl_btn_show_bot_action);

        if (gone) {
            layoutBot.setVisibility(View.GONE);
            btnShowBot.setText(R.string.md_bot);

            if (changeKeyboard) {
                try {
                    InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(rootView.findViewById(R.id.chl_edt_chat), InputMethodManager.SHOW_IMPLICIT);
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }

        } else {
            layoutBot.setVisibility(View.VISIBLE);
            btnShowBot.setText(R.string.md_black_keyboard_with_white_keys);

            if (changeKeyboard) {
                try {
                    InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.findViewById(R.id.chl_edt_chat).getWindowToken(), 0);
                } catch (IllegalStateException e) {
                    e.getStackTrace();
                }
            }

        }

    }

    private void fillList(String message, boolean backToMenu) {
        botActionList = new ArrayList<>();

        if (message.equals("")) {
            return;
        }

        String spiltList[] = message.split("\n");

        for (String line : spiltList) {
            if (line.startsWith("/")) {
                String lineSplit[] = line.split("-");
                if (lineSplit.length == 2) {
                    StructRowBotAction _row = new StructRowBotAction();
                    _row.action = lineSplit[0];
                    _row.name = lineSplit[1];
                    botActionList.add(_row);
                }
            }
        }

        if (botActionList.size() == 0 || backToMenu) {
            StructRowBotAction _row = new StructRowBotAction();
            _row.action = "/back";
            _row.name = G.context.getString(R.string.back_to_menu);
            botActionList.add(_row);
        }

    }

    private void makeButtonList(View rootView) {

        LinearLayout layoutBot = rootView.findViewById(R.id.bal_layout_bot_layout);
        layoutBot.removeAllViews();

        LinearLayout layout = null;


        for (int i = 0; i < botActionList.size(); i += 2) {

            layout = new LinearLayout(G.context);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            StructRowBotAction sb0 = botActionList.get(i);
            if (sb0.name.length() > 0) {
                addButton(layout, sb0.name, sb0.action);
            }

            if (i + 1 < botActionList.size()) {
                StructRowBotAction sb1 = botActionList.get(i + 1);
                if (sb1.name.length() > 0) {
                    addButton(layout, sb1.name, sb1.action);
                }
            }

            layoutBot.addView(layout);
        }

    }

    private void addButton(LinearLayout layout, String name, String action) {

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        param.setMargins(2, 2, 2, 2);

        Button btn = new Button(G.context);
        btn.setLayoutParams(param);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(ContextCompat.getColor(G.context, R.color.backgroundColorCall2));
        btn.setText(name);
        btn.setTypeface(G.typeface_IRANSansMobile);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.onBotClick != null) {
                    G.onBotClick.onBotCommandText(action);
                }
                setLayoutBot(true, false);
            }
        });
        layout.addView(btn);
    }

    class StructRowBotAction {
        String action = "";
        String name = "";
    }

    private void makeTxtList(View rootView) {

        LinearLayout layoutBot = rootView.findViewById(R.id.bal_layout_bot_layout);
        layoutBot.removeAllViews();

        for (int i = 0; i < botActionList.size(); i++) {

            StructRowBotAction sb0 = botActionList.get(i);
            if (sb0.name.length() > 0) {
                addTxt(layoutBot, sb0.name, sb0.action);
            }

            layoutBot.addView(layoutBot);
        }

    }

    private void addTxt(LinearLayout layout, String name, String action) {

        ViewGroup.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);

        TextView txt = new TextView(G.context);
        txt.setLayoutParams(param);
        txt.setPadding(15, 6, 15, 6);
        txt.setText(action);
        txt.setTypeface(G.typeface_IRANSansMobile);
        txt.setTextColor(Color.BLACK);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.onBotClick != null) {
                    G.onBotClick.onBotCommandText(action);
                }
                setLayoutBot(true, false);
            }
        });
        layout.addView(txt);

    }


    public void close() {
        setLayoutBot(true, false);
    }

}
