package net.iGap.module;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityPopUpNotification;
import net.iGap.adapter.items.chat.ViewMaker;
import net.iGap.helper.HelperUrl;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.module.additionalData.ButtonActionType;
import net.iGap.module.additionalData.ButtonEntity;
import net.iGap.observers.interfaces.Ipromote;
import net.iGap.observers.interfaces.OnChatGetRoom;
import net.iGap.proto.ProtoClientGetPromote;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestClientGetPromote;
import net.iGap.request.RequestClientGetRoom;
import net.iGap.request.RequestClientPinRoom;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

import static net.iGap.G.context;
import static net.iGap.G.isLocationFromBot;
import static net.iGap.adapter.items.chat.ViewMaker.i_Dp;

public class BotInit implements MakeButtons.OnClickListener {

    ProtoGlobal.RoomMessage newMessage;
    private ArrayList<StructRowBotAction> botActionList;
    private View layoutBot;
    private View rootView;
    private Gson gson;
    private LinearLayout mainLayout;
    private LinearLayout childLayout;
    private HashMap<Integer, JSONArray> buttonList;
    private RealmRoomMessage roomMessage;
    private String additionalData;
    private int additionalType;
    private MaterialDesignTextView btnShowBot;
    private long roomId;

    private static String prefrenceName = "DrIgap";
    // private boolean state;


    public BotInit(View rootView, boolean showCommandList) {
        this.rootView = rootView;
        init(rootView);

    }

    public static void setCheckDrIgap(boolean check) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BotInit.prefrenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("check", check);
        editor.apply();
    }

    public static boolean getCheckDrIgap() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BotInit.prefrenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("check", false);
    }

    public static void checkDrIgap() {
        if (!BotInit.getCheckDrIgap())
            return;

        new RequestClientGetPromote().getPromote();
        G.ipromote = new Ipromote() {
            @Override
            public void onGetPromoteResponse(ProtoClientGetPromote.ClientGetPromoteResponse.Builder builder) {
                setCheckDrIgap(false);
                ArrayList<Long> promoteIds = new ArrayList<>();

                for (int i = 0; i < builder.getPromoteList().size(); i++)
                    promoteIds.add(builder.getPromoteList().get(i).getId());
                DbManager.getInstance().doRealmTask(realm -> {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<RealmRoom> roomList = realm.where(RealmRoom.class).equalTo("isFromPromote", true).findAll();
                            for (RealmRoom room : roomList) {
                                if (!promoteIds.contains(room.getPromoteId())) {
                                    room.setFromPromote(false);
                                    new RequestClientPinRoom().pinRoom(room.getId(), false);
                                }
                            }
                        }
                    });

                    for (int i = builder.getPromoteList().size() - 1; i >= 0; i--) {

                        ProtoClientGetPromote.ClientGetPromoteResponse.Promote.Type TYPE = builder.getPromoteList().get(i).getType();
                        RealmRoom realmRoom;

                        if (TYPE == ProtoClientGetPromote.ClientGetPromoteResponse.Promote.Type.USER) {
                            realmRoom = realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", builder.getPromoteList().get(i).getId()).equalTo("isFromPromote", true).findFirst();
                        } else {
                            realmRoom = realm.where(RealmRoom.class).equalTo("id", builder.getPromoteList().get(i).getId()).equalTo("isFromPromote", true).findFirst();
                        }

                        if (realmRoom == null) {
                            if (TYPE == ProtoClientGetPromote.ClientGetPromoteResponse.Promote.Type.USER) {
                                //                   RealmRoom.setPromote(builder.getPromoteList().get(i).getId(), TYPE);
                                G.onChatGetRoom = new OnChatGetRoom() {
                                    @Override
                                    public void onChatGetRoom(final ProtoGlobal.Room room) {
                                        G.onChatGetRoom = null;
                                        DbManager.getInstance().doRealmTransaction(realm1 -> {
                                            RealmRoom realmRoom1 = RealmRoom.putOrUpdate(room, realm1);
                                            realmRoom1.setFromPromote(true);
                                            realmRoom1.setPromoteId(realmRoom1.getChatRoom().getPeerId());
                                            //  RealmRoom.setPromote(2297310L, ProtoClientGetPromote.ClientGetPromoteResponse.Promote.Type.USER);
                                        });

                                        new RequestClientPinRoom().pinRoom(room.getId(), true);

                                        ActivityPopUpNotification.sendMessage("/start", room.getId(), ProtoGlobal.Room.Type.CHAT);

                                    }


                                    @Override
                                    public void onChatGetRoomTimeOut() {

                                    }

                                    @Override
                                    public void onChatGetRoomError(int majorCode, int minorCode) {

                                    }
                                };
                                new RequestChatGetRoom().chatGetRoom(builder.getPromoteList().get(i).getId());
                            } else {
                                new RequestClientGetRoom().clientGetRoom(builder.getPromoteList().get(i).getId(), RequestClientGetRoom.CreateRoomMode.getPromote);
                            }
                        } else {
                            new RequestClientPinRoom().pinRoom(realmRoom.getId(), true);
                        }
                    }
                });
            }

        };
    }

    public void updateCommandList(boolean showCommandList, String message, Activity activity, boolean backToMenu, RealmRoomMessage roomMessage, long roomId) {
        if (roomMessage != null) {
            this.roomMessage = roomMessage;

        }
        if (roomId != 0)
            this.roomId = roomId;


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fillList(message, backToMenu);

                if (botActionList.size() == 0) {
                    return;
                }
                if (message.equalsIgnoreCase("clear")) {
                    botActionList.clear();
                    StructRowBotAction _row = new StructRowBotAction();
                    _row.action = "/start";
                    _row.name = G.context.getString(R.string.start);
                    botActionList.add(_row);
                }

                if (showCommandList) {
                    makeTxtList(rootView);
                } else if (roomMessage != null && roomMessage.getRealmAdditional() != null) {
                    try {
                        makeButtonList(rootView, roomMessage.getRealmAdditional().getAdditionalData(), roomMessage.getRealmAdditional().getAdditionalType());
                        if (btnShowBot != null)
                            btnShowBot.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                    }

                } else
                    makeButtonList(rootView);

                setLayoutBot(false, false);
            }
        });

    }

    public void updateCommandList(boolean showCommandList, String message, Activity activity, boolean backToMenu, ProtoGlobal.RoomMessage newMessage, long roomId, boolean state) {

        if (newMessage != null) {
            this.newMessage = newMessage;

        }
        if (roomId != 0)
            this.roomId = roomId;


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fillList(message, backToMenu);

                if (botActionList.size() == 0) {
                    return;
                }
                if (message.equalsIgnoreCase("clear")) {
                    if (!state) {
                        botActionList.clear();
                        StructRowBotAction _row = new StructRowBotAction();
                        _row.action = "/start";
                        _row.name = G.context.getString(R.string.start);
                        botActionList.add(_row);
                    } else {
                        botActionList.clear();
                        StructRowBotAction _row = new StructRowBotAction();
                       /* _row.action = "/start";
                        _row.name = G.context.getString(R.string.start);*/
                        botActionList.add(_row);
                        try {
                            if (btnShowBot != null)
                                btnShowBot.setVisibility(View.GONE);
                        } catch (Exception e) {
                        }
                    }

                } else {
                    try {
                        if (btnShowBot != null)
                            btnShowBot.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                    }

                }

                if (showCommandList) {
                    makeTxtList(rootView);
                } else if (newMessage != null && newMessage.getAdditionalData() != null && roomId != 0) {
                    try {
                        makeButtonList(rootView, newMessage.getAdditionalData(), newMessage.getAdditionalType());
                    } catch (Exception e) {
                    }

                } else
                    makeButtonList(rootView);

                setLayoutBot(false, false);
            }
        });

    }

    private void init(View rootView) {

//        btnShowBot = rootView.findViewById(R.id.chl_btn_show_bot_action);
        btnShowBot = rootView.findViewById(R.id.tv_chatRoom_botAction);
        btnShowBot.setVisibility(View.GONE);

        layoutBot = rootView.findViewById(R.id.layout_bot);

        btnShowBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutBot(layoutBot.getVisibility() == View.VISIBLE, true);
            }
        });

    }

    private void setLayoutBot(boolean gone, boolean changeKeyboard) {

     /*   if (botActionList.size() == 0) {
            return;
        }*/

//        btnShowBot = rootView.findViewById(R.id.chl_btn_show_bot_action);
        btnShowBot = rootView.findViewById(R.id.tv_chatRoom_botAction);


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

        String[] spiltList = message.split("\n");

        for (String line : spiltList) {
            if (line.startsWith("/")) {
                String[] lineSplit = line.split("-");
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

    private void makeButtonList(View rootView, String additionalData, int type) {
        if (type == AdditionalType.UNDER_KEYBOARD_BUTTON) {
            try {
                InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.findViewById(R.id.chl_edt_chat).getWindowToken(), 0);
            } catch (IllegalStateException e) {
                e.getStackTrace();
            }

        }

        LinearLayout layoutBot = rootView.findViewById(R.id.bal_layout_bot_layout);
        layoutBot.removeAllViews();

        LinearLayout layout = null;
        buttonList = new HashMap<>();
        buttonList = MakeButtons.parseData(additionalData);

        childLayout = MakeButtons.createLayout(rootView.getContext());
        gson = new GsonBuilder().create();
        for (int i = 0; i < buttonList.size(); i++) {
            for (int j = 0; j < buttonList.get(i).length(); j++) {
                try {
                    ButtonEntity btnEntery = new ButtonEntity();
                    btnEntery = gson.fromJson(buttonList.get(i).get(j).toString(), new TypeToken<ButtonEntity>() {
                    }.getType());
                    btnEntery.setJsonObject(buttonList.get(i).get(j).toString());
                    childLayout = MakeButtons.addButtons(Theme.getInstance(), btnEntery, buttonList.get(i).length(), 1f, childLayout, this);
                    //   childLayout = MakeButtons.addButtons(buttonList.get(i).get(j).toString(), this, buttonList.get(i).length(), .75f, btnEntery.getLable(), btnEntery.getLable(), btnEntery.getImageUrl(), i, btnEntery.getValue(), childLayout, btnEntery.getActionType(), type);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            layoutBot.setPadding(i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4), i_Dp(R.dimen.dp4));
            layoutBot.addView(childLayout);
            childLayout = MakeButtons.createLayout(rootView.getContext());

        }

    }

    private void makeButtonList(View rootView) {

        LinearLayout layoutBot = rootView.findViewById(R.id.bal_layout_bot_layout);
        layoutBot.removeAllViews();

        RelativeLayout layout = null;


        for (int i = 0; i < botActionList.size(); i += 2) {

            layout = new RelativeLayout(G.context);
            layout.setGravity(RelativeLayout.CENTER_IN_PARENT);

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

    private void addButton(RelativeLayout layout, String name, String action) {

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        AppCompatButton btn = new AppCompatButton(G.context);
        btn.setLayoutParams(param);
        btn.setPadding(ViewMaker.dpToPixel(14), ViewMaker.dpToPixel(14), ViewMaker.dpToPixel(14), ViewMaker.dpToPixel(14));
        btn.setTextColor(ContextCompat.getColor(G.context, R.color.start_color));
        btn.setBackgroundColor(ContextCompat.getColor(G.context, R.color.background_setting_light));
        btn.setText(name);
        btn.setAllCaps(false);
        btn.setGravity(Gravity.CENTER);
        btn.setTypeface(ResourcesCompat.getFont(btn.getContext(), R.font.main_font));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.onBotClick != null) {
                    G.onBotClick.onBotCommandText(action, 0);
                }
                setLayoutBot(true, false);
            }
        });
        layout.addView(btn);

        /*childLayout = MakeButtons.createLayout();
        layout.addView(MakeButtons.addButtons(null, this, 1, .75f, "start", "start", "", 0, "/start", childLayout, 0, 1));*/
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

        TextView txt = new AppCompatTextView(G.context);
        txt.setLayoutParams(param);
        txt.setPadding(15, 6, 15, 6);
        txt.setText(action);
        txt.setTypeface(ResourcesCompat.getFont(txt.getContext(), R.font.main_font));
        txt.setTextColor(Color.BLACK);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (G.onBotClick != null) {
                    G.onBotClick.onBotCommandText(action, 0);
                }
                setLayoutBot(true, false);
            }
        });
        layout.addView(txt);

    }


    public void close() {
        setLayoutBot(true, false);
    }

    @Override
    public void onClick(View v, ButtonEntity buttonEntity) {
        DbManager.getInstance().doRealmTask(realm -> {
            try {
                if (v.getId() == ButtonActionType.USERNAME_LINK) {
                    //TODO: fixed this and do not use G.currentActivity
                    HelperUrl.checkUsernameAndGoToRoomWithMessageId(G.currentActivity, ((ArrayList<String>) v.getTag()).get(0).substring(1), HelperUrl.ChatEntry.chat, 0);
                } else if (v.getId() == ButtonActionType.BOT_ACTION) {
                    try {
                        Long identity = System.currentTimeMillis();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoomMessage realmRoomMessage = RealmRoomMessage.makeAdditionalData(roomId, identity, ((ArrayList<String>) v.getTag()).get(1), ((ArrayList<String>) v.getTag()).get(2), 3, realm, ProtoGlobal.RoomMessageType.TEXT);
                                ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(ProtoGlobal.Room.Type.CHAT, roomId, realmRoomMessage);
                                if (G.onBotClick != null) {
                                    G.onBotClick.onBotCommandText(realmRoomMessage, ButtonActionType.BOT_ACTION);
                                }
                            }
                        });
                    } catch (Exception e) {
                    }
                } else if (v.getId() == ButtonActionType.JOIN_LINK) {
                    HelperUrl.checkAndJoinToRoom(G.currentActivity, ((ArrayList<String>) v.getTag()).get(0).substring(14));
                } else if (v.getId() == ButtonActionType.WEB_LINK) {
                    HelperUrl.openBrowser(((ArrayList<String>) v.getTag()).get(0));
                } else if (v.getId() == ButtonActionType.WEBVIEW_LINK) {
                    G.onBotClick.onBotCommandText(((ArrayList<String>) v.getTag()).get(0), ButtonActionType.WEBVIEW_LINK);
                } else if (v.getId() == ButtonActionType.REQUEST_PHONE) {
                    try {
                        new MaterialDialog.Builder(G.currentActivity).title(R.string.access_phone_number).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Long identity = System.currentTimeMillis();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmUserInfo realmUserInfo = RealmUserInfo.getRealmUserInfo(realm);
                                        RealmRoomMessage realmRoomMessage = RealmRoomMessage.makeAdditionalData(roomId, identity, realmUserInfo.getUserInfo().getPhoneNumber(), realmUserInfo.getUserInfo().getPhoneNumber(), 0, realm, ProtoGlobal.RoomMessageType.TEXT);
                                        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).build(ProtoGlobal.Room.Type.CHAT, roomId, realmRoomMessage);
                                        if (G.onBotClick != null) {
                                            G.onBotClick.onBotCommandText(realmRoomMessage, ButtonActionType.BOT_ACTION);
                                        }
                                    }
                                });
                            }
                        }).show();


                    } catch (Exception e) {
                    }

                } else if (v.getId() == ButtonActionType.REQUEST_LOCATION) {
                    try {
                        new MaterialDialog.Builder(G.currentActivity).title(R.string.access_location).positiveText(R.string.ok).negativeText(R.string.cancel).onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Boolean response = false;
                                if (G.locationListener != null) {
                                    isLocationFromBot = true;
                                    G.locationListener.requestLocation();
                                }

          /*              G.locationListenerResponse = new LocationListenerResponse() {
                            @Override
                            public void setLocationResponse(Double latitude, Double longitude) {
                                Long identity = System.currentTimeMillis();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmRoomMessage realmRoomMessage = RealmRoomMessage.makeAdditionalData(roomId, identity, latitude + "," + longitude, ((ArrayList<String>) v.getTag()).get(2).toString(), 3, realm, ProtoGlobal.RoomMessageType.TEXT);
                                        G.chatSendMessageUtil.build(ProtoGlobal.Room.Type.CHAT, roomId, realmRoomMessage).sendMessage(identity + "");
                                        if (G.onBotClick != null) {
                                            G.onBotClick.onBotCommandText(realmRoomMessage, ButtonActionType.BOT_ACTION);
                                        }
                                    }
                                });
                            }
                        };*/


                            }
                        }).show();


                    } catch (Exception e) {
                    }


                }

            } catch (Exception e) {
                Toast.makeText(G.context, "دستور با خطا مواجه شد", Toast.LENGTH_LONG).show();
            }
        });

    }

    class StructRowBotAction {
        String action = "";
        String name = "";
    }

}
