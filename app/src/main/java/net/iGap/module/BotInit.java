package net.iGap.module;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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

    public void updateCommandList(boolean showCommandList, String message) {

        fillList(message);

        if (showCommandList) {
            //  makeTxtList(rootView);
        } else {
            makeButtonList(rootView);
        }
    }


    private void init(View rootView) {

        MaterialDesignTextView btnShowBot = (MaterialDesignTextView) rootView.findViewById(R.id.chl_btn_show_bot_action);
        btnShowBot.setVisibility(View.VISIBLE);

        layoutBot = rootView.findViewById(R.id.layout_bot);

        btnShowBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutBot.setVisibility(layoutBot.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

    }

    private void fillList(String message) {
        botActionList = new ArrayList<>();

        String spiltList[] = message.split("\n");

        for (String line : spiltList) {
            if (line.startsWith("/")) {
                String lineSplit[] = line.split("-");
                if (lineSplit.length == 2) {
                    StructRowBotAction _row = new StructRowBotAction();
                    _row.action = lineSplit[0];
                    _row.action = lineSplit[1];
                    botActionList.add(_row);
                }
            }
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
                StructRowBotAction sb1 = botActionList.get(i);
                if (sb1.name.length() > 0) {
                    addButton(layout, sb1.name, sb1.action);
                }
            }

            layoutBot.addView(layout);
        }

    }

    private void addButton(LinearLayout layout, String name, String action) {

        ViewGroup.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);

        Button btn = new Button(G.context);
        btn.setLayoutParams(param);
        btn.setText(name);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ddd", action);
                layoutBot.setVisibility(View.GONE);
            }
        });
        layout.addView(btn);
    }

    class StructRowBotAction {
        String action = "";
        String name = "";
    }


//    private void makeTxtList(View rootView) {
//
//        LinearLayout layoutBot = rootView.findViewById(R.id.bal_layout_bot_layout);
//        layoutBot.removeAllViews();
//
//        for (StructRowBotAction sb : botActionList) {
//
//            if (sb.name1.length() > 0) {
//                addTxt(layoutBot, sb.name1, sb.icon1, sb.action1);
//            }
//
//            if (sb.name2.length() > 0) {
//                addTxt(layoutBot, sb.name2, sb.icon2, sb.action2);
//            }
//
//            if (sb.name3.length() > 0) {
//                addTxt(layoutBot, sb.name3, sb.icon3, sb.action3);
//            }
//
//        }
//
//    }
//
//    private void addTxt(LinearLayout layout, String name, String icon, String action) {
//
//        ViewGroup.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
//
//        TextView txt = new TextView(G.context);
//        txt.setLayoutParams(param);
//        txt.setPadding(15, 6, 15, 6);
//        txt.setText(action);
//        txt.setTextColor(Color.BLACK);
//        txt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                ((EmojiEditTextE) rootView.findViewById(R.id.chl_edt_chat)).setText(action);
//
//                layoutBot.setVisibility(View.GONE);
//            }
//        });
//        layout.addView(txt);
//
//    }


    public void close() {
        layoutBot.setVisibility(View.GONE);
    }

}
