package com.iGap.helper;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.activities.ActivityWebView;
import com.iGap.interfaces.OnAvatarGet;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.interfaces.OnClientCheckInviteLink;
import com.iGap.interfaces.OnClientJoinByInviteLink;
import com.iGap.interfaces.OnClientResolveUsername;
import com.iGap.module.AndroidUtils;
import com.iGap.module.CircleImageView;
import com.iGap.module.SHP_SETTING;
import com.iGap.proto.ProtoClientResolveUsername;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.request.RequestChatGetRoom;
import com.iGap.request.RequestClientCheckInviteLink;
import com.iGap.request.RequestClientJoinByInviteLink;
import com.iGap.request.RequestClientResolveUsername;
import com.nostra13.universalimageloader.core.ImageLoader;
import io.realm.Realm;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.iGap.G.context;

/**
 * Created by android3 on 11/26/2016.
 */

public class HelperUrl {

    ////**********************************************************************************************************
    //
    //
    //        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
    //        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
    //
    ////**********************************************************************************************************
    //
    //    // NOTES:   1) \w includes 0-9, a-z, A-Z, _
    ////          2) The leading '-' is the '-' character. It must go first in character class expression
    //    private static final String VALID_CHARS = "-\\w+&@#/%=~()|";
    //    private static final String VALID_NON_TERMINAL = "?!:,.;";
    //
    //    // Notes on the expression:
    ////  1) Any number of leading '(' (left parenthesis) accepted.  Will be dealt with.
    ////  2) s? ==> the s is optional so either [http, https] accepted as scheme
    ////  3) All valid chars accepted and then one or more
    ////  4) Case insensitive so that the scheme can be hTtPs (for example) if desired
    //    private static final Pattern URI_FINDER_PATTERN = Pattern.compile("\\(*https?://["+ VALID_CHARS + VALID_NON_TERMINAL + "]*[" +VALID_CHARS + "]",
    //            Pattern.CASE_INSENSITIVE );
    ////**********************************************************************************************************

    public static int LinkColor = Color.GRAY;
    public static String igapSite1 = "igap.im/";
    public static String igapSite2 = "igap.net/";
    public static MaterialDialog dialogWaiting;

    public static SpannableStringBuilder setUrlLink(String text, boolean withClickable, boolean withHash, String messageID, boolean withAtSign) {

        if (text == null) return null;

        if (text.trim().length() < 1) return null;

        SpannableStringBuilder strBuilder = new SpannableStringBuilder(text);

        if (withAtSign) strBuilder = analaysAtSign(strBuilder);

        if (withHash) strBuilder = analaysHash(strBuilder, messageID);

        Pattern urlPattern = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

        Matcher matcher = urlPattern.matcher(text);
        while (matcher.find()) {
            int matchStart = matcher.start(1);
            int matchEnd = matcher.end();

            if (strBuilder.toString().substring(matchStart, matchEnd).toLowerCase().contains(igapSite1) || strBuilder.toString().substring(matchStart, matchEnd).toLowerCase().contains(igapSite2)) {
                //  insertIgapLink(strBuilder, matchStart, matchEnd);
            } else {
                insertLinkSpan(strBuilder, matchStart, matchEnd, withClickable);
            }
        }

        String[] list = text.toLowerCase().split(" ");

        int count = 0;

        for (int i = 0; i < list.length; i++) {

            String str = list[i];

            if (str.contains(igapSite1) || str.contains(igapSite2)) {
                insertIgapLink(strBuilder, count, count + str.length());
            }
            count += str.length() + 1;
        }




        return strBuilder;
    }

    private static void insertLinkSpan(final SpannableStringBuilder strBuilder, final int start, final int end, final boolean withclickable) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                if (withclickable) {

                    boolean openLocalWebPage;
                    SharedPreferences sharedPreferences = G.context.getSharedPreferences(SHP_SETTING.FILE_NAME, G.context.MODE_PRIVATE);

                    int checkedInappBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 0);

                    if (checkedInappBrowser == 1) {
                        openLocalWebPage = true;
                    } else {
                        openLocalWebPage = false;
                    }

                    String url = strBuilder.toString().substring(start, end);

                    if (!url.startsWith("https://") && !url.startsWith("http://")) {
                        url = "http://" + url;
                    }

                    if (openLocalWebPage) {

                        Intent intent = new Intent(G.context, ActivityWebView.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("PATH", url);
                        G.context.startActivity(intent);
                        try {
                            G.context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e("ddd", "can not open url");
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(url));

                        try {
                            G.context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Log.e("ddd", "can not open url");
                        }
                    }
                }
            }

            @Override public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void insertIgapLink(final SpannableStringBuilder strBuilder, final int start, final int end) {

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {

                String url = strBuilder.toString().substring(start, end);

                int index = url.lastIndexOf("/");
                if (index >= 0 && index < url.length() - 1) {
                    String token = url.substring(index + 1);

                    if (url.toLowerCase().contains("join")) {
                        checkAndJoinToRoom(token);
                    } else {
                        checkUsernameAndGoToRoom(token);
                    }



                    Log.e("ddd", "token = " + token);
                }
            }

            @Override public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        strBuilder.setSpan(clickable, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //*********************************************************************************************************

    private static SpannableStringBuilder analaysHash(SpannableStringBuilder builder, String messageID) {

        if (builder == null) return builder;

        String text = builder.toString();

        if (text.length() < 1) return builder;

        String s = "";
        String tmp = "";
        Boolean isHash = false;
        int start = 0;
        String enter = System.getProperty("line.separator");

        for (int i = 0; i < text.length(); i++) {

            s = text.substring(i, i + 1);
            if (s.equals("#")) {
                isHash = true;
                tmp = "";
                start = i;
                continue;
            }

            if (isHash) {
                if (s.equals("!") || s.equals("@") || s.equals("$") || s.equals("%") || s.equals("^") || s.equals("&") ||
                    s.equals("(") || s.equals(")") || s.equals("-") || s.equals("+") || s.equals("=") || s.equals("!") ||
                    s.equals("`") || s.equals("{") || s.equals("}") || s.equals("[") || s.equals("]") || s.equals(";") ||
                    s.equals(":") || s.equals("'") || s.equals("?") || s.equals("<") || s.equals(">") || s.equals(",") || s.equals(" ") ||
                    s.equals("\\") || s.equals("|") || s.equals("//") || s.codePointAt(0) == 8192 || s.equals(enter) || s.equals("")) {

                    insertHashLink(tmp, builder, start, messageID);

                    tmp = "";
                    isHash = false;
                } else {
                    tmp += s;
                }
            }
        }

        if (isHash) {
            if (!tmp.equals("")) insertHashLink(tmp, builder, start, messageID);
        }

        return builder;
    }

    private static void insertHashLink(final String text, SpannableStringBuilder builder, int start, final String messageID) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override public void onClick(View widget) {
                if (ActivityChat.hashListener != null) {
                    ActivityChat.hashListener.complete(true, text, messageID);
                }
            }

            @Override public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        builder.setSpan(clickableSpan, start, start + text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //*********************************************************************************************************

    private static SpannableStringBuilder analaysAtSign(SpannableStringBuilder builder) {

        if (builder == null) return builder;

        String text = builder.toString();

        if (text.length() < 1) return builder;

        String s = "";
        String tmp = "";
        Boolean isAtSign = false;
        int start = 0;
        String enter = System.getProperty("line.separator");

        for (int i = 0; i < text.length(); i++) {

            s = text.substring(i, i + 1);
            if (s.equals("@")) {
                isAtSign = true;
                tmp = "";
                start = i;
                continue;
            }

            if (isAtSign) {
                if (s.equals("!") || s.equals("#") || s.equals("$") || s.equals("%") || s.equals("^") || s.equals("&") ||
                    s.equals("(") || s.equals(")") || s.equals("-") || s.equals("+") || s.equals("=") || s.equals("!") ||
                    s.equals("`") || s.equals("{") || s.equals("}") || s.equals("[") || s.equals("]") || s.equals(";") ||
                    s.equals(":") || s.equals("'") || s.equals("?") || s.equals("<") || s.equals(">") || s.equals(",") || s.equals(" ") ||
                    s.equals("\\") || s.equals("|") || s.equals("//") || s.codePointAt(0) == 8192 || s.equals(enter) || s.equals("")) {

                    insertAtSignLink(tmp, builder, start);

                    tmp = "";
                    isAtSign = false;
                } else {
                    tmp += s;
                }
            }
        }

        if (isAtSign) {
            if (!tmp.equals("")) insertAtSignLink(tmp, builder, start);
        }

        return builder;
    }

    private static void insertAtSignLink(final String text, SpannableStringBuilder builder, int start) {

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override public void onClick(View widget) {

                checkUsernameAndGoToRoom(text);
            }

            @Override public void updateDrawState(TextPaint ds) {
                ds.linkColor = LinkColor;
                super.updateDrawState(ds);
            }
        };

        builder.setSpan(clickableSpan, start, start + text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    //*********************************************************************************************************

    public static boolean hasInMessageLink(String message) {
        boolean result = false;

        message = message.toLowerCase();

        if (message.contains("#") || message.contains("@")) return true;

        if (message.contains(igapSite1) || message.contains(igapSite2)) return true;

        Pattern urlPattern = Pattern.compile("(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)" + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*" + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

        Matcher matcher = urlPattern.matcher(message);
        while (matcher.find()) {
            result = true;
        }

        return result;
    }


    //**************************************    invite by link *******************************************************************

    private static void checkAndJoinToRoom(final String token) {

        if (token == null || token.length() < 0) return;

        showIndeterminateProgressDialog();

        G.onClientCheckInviteLink = new OnClientCheckInviteLink() {
            @Override public void onClientCheckInviteLinkResponse(ProtoGlobal.Room room) {
                dialogWaiting.dismiss();
                openDialogJoin(room, token);
            }

            @Override public void onError(int majorCode, int minorCode) {
                dialogWaiting.dismiss();
            }
        };

        new RequestClientCheckInviteLink().clientCheckInviteLink(token);
    }

    private static void openDialogJoin(final ProtoGlobal.Room room, final String token) {

        if (room == null) return;

        final Realm realm = Realm.getDefaultInstance();

        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

        if (realmRoom == null) {

            realm.executeTransaction(new Realm.Transaction() {
                @Override public void execute(Realm realm) {
                    RealmRoom realmRoom1 = RealmRoom.putOrUpdate(room);
                    realmRoom1.setDeleted(true);
                }
            });
        }

        realm.close();

        String title = G.context.getString(R.string.do_you_want_to_join_to_this);
        String memberNumber = "";
        final CircleImageView[] imageView = new CircleImageView[1];

        switch (room.getType()) {
            case CHANNEL:
                title += G.context.getString(R.string.channel);
                memberNumber = room.getChannelRoomExtra().getParticipantsCount() + " " + G.context.getString(R.string.member);
                break;
            case GROUP:
                title += G.context.getString(R.string.group);
                memberNumber = room.getGroupRoomExtra().getParticipantsCount() + " " + G.context.getString(R.string.member);
                break;
        }

        final String finalMemberNumber = memberNumber;
        final String finalTitle = title;

        G.currentActivity.runOnUiThread(new Runnable() {
            @Override public void run() {

                final MaterialDialog dialog = new MaterialDialog.Builder(G.currentActivity).title(finalTitle)
                    .customView(R.layout.dialog_alert_join, true).positiveText(R.string.join)
                    .cancelable(false)
                    .negativeText(android.R.string.cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            joinToRoom(token, room);
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Realm realm = Realm.getDefaultInstance();
                            final RealmRoom reamRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

                            if (reamRoom != null) {
                                if (reamRoom.isDeleted()) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override public void execute(Realm realm) {
                                            reamRoom.deleteFromRealm();
                                        }
                                    });
                                }
                            }

                            realm.close();
                        }
                    })
                    .build();

                imageView[0] = (CircleImageView) dialog.findViewById(R.id.daj_img_room_picture);

                TextView txtRoomName = (TextView) dialog.findViewById(R.id.daj_txt_room_name);
                txtRoomName.setText(room.getTitle());

                TextView txtMemeberNumber = (TextView) dialog.findViewById(R.id.daj_txt_member_count);
                txtMemeberNumber.setText(finalMemberNumber);

                dialog.show();

                HelperAvatar.getAvatar(room.getId(), HelperAvatar.AvatarType.ROOM, new OnAvatarGet() {
                    @Override public void onAvatarGet(final String avatarPath, long roomId) {
                        ImageLoader.getInstance().displayImage(AndroidUtils.suitablePath(avatarPath), imageView[0]);
                    }

                    @Override public void onShowInitials(String initials, String color) {
                        imageView[0].setImageBitmap(HelperImageBackColor.drawAlphabetOnPicture((int) imageView[0].getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                    }
                });
            }
        });
    }

    private static void joinToRoom(String token, final ProtoGlobal.Room room) {

        showIndeterminateProgressDialog();

        G.onClientJoinByInviteLink = new OnClientJoinByInviteLink() {
            @Override public void onClientJoinByInviteLinkResponse() {

                dialogWaiting.dismiss();

                Realm realm = Realm.getDefaultInstance();
                final RealmRoom reamRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

                if (reamRoom != null) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override public void execute(Realm realm) {
                            reamRoom.setDeleted(false);
                            if (reamRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                                reamRoom.setReadOnly(false);
                            }
                        }
                    });
                }

                realm.close();

                Intent intent = new Intent(G.currentActivity, ActivityChat.class);
                intent.putExtra("RoomId", room.getId());
                G.currentActivity.startActivity(intent);
            }

            @Override public void onError(int majorCode, int minorCode) {
                dialogWaiting.dismiss();
            }
        };

        new RequestClientJoinByInviteLink().clientJoinByInviteLink(token);
    }

    //************************************  go to room by userName   *********************************************************************

    private static void checkUsernameAndGoToRoom(final String userName) {

        if (userName == null || userName.length() < 1) return;

        showIndeterminateProgressDialog();

        // this methode check user name and if it is ok go to room
        G.onClientResolveUsername = new OnClientResolveUsername() {
            @Override public void onClientResolveUsername(ProtoClientResolveUsername.ClientResolveUsernameResponse.Type type, ProtoGlobal.RegisteredUser user, ProtoGlobal.Room room) {
                dialogWaiting.dismiss();
                openChat(userName, type, user, room);
            }

            @Override public void onError(int majorCode, int minorCode) {
                dialogWaiting.dismiss();
            }
        };

        new RequestClientResolveUsername().channelAddMessageReaction(userName);
    }

    private static void openChat(String username, ProtoClientResolveUsername.ClientResolveUsernameResponse.Type type, ProtoGlobal.RegisteredUser user, ProtoGlobal.Room room) {

        switch (type) {
            case USER:
                goToChat(user);
                break;
            case ROOM:
                goToRoom(username, room);
                break;
        }
    }

    private static void goToChat(final ProtoGlobal.RegisteredUser user) {

        Long id = user.getId();

        Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, id).findFirst();

        if (realmRoom != null) {
            Intent intent = new Intent(context, ActivityChat.class);
            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            G.currentActivity.startActivity(intent);

            realm.close();
        } else {

            showIndeterminateProgressDialog();

            G.onChatGetRoom = new OnChatGetRoom() {
                @Override public void onChatGetRoom(long roomId) {
                    dialogWaiting.dismiss();
                }

                @Override public void onChatGetRoomCompletely(ProtoGlobal.Room room) {
                    addchatToDatabaseAndGoToChat(user, room.getId());
                    dialogWaiting.dismiss();
                }

                @Override public void onChatGetRoomTimeOut() {
                    dialogWaiting.dismiss();
                }

                @Override public void onChatGetRoomError(int majorCode, int minorCode) {
                    dialogWaiting.dismiss();
                }
            };

            new RequestChatGetRoom().chatGetRoomWithIdentity(user.getId());
        }
    }

    public static void showIndeterminateProgressDialog() {

        G.currentActivity.runOnUiThread(new Runnable() {
            @Override public void run() {
                dialogWaiting = new MaterialDialog.Builder(G.currentActivity).title(R.string.please_wait)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .cancelable(false)
                    .progressIndeterminateStyle(false)
                    .show();
            }
        });
    }

    private static void addchatToDatabaseAndGoToChat(final ProtoGlobal.RegisteredUser user, final long roomid) {

        new Handler(G.currentActivity.getMainLooper()).post(new Runnable() {
            @Override public void run() {

                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {

                        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, user.getId()).findFirst();
                        if (realmRegisteredInfo == null) {
                            realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class);
                            realmRegisteredInfo.setId(user.getId());
                            realmRegisteredInfo.setShowSpamBar(true);
                        }
                        RealmAvatar.put(user.getId(), user.getAvatar(), true);
                        realmRegisteredInfo.setUsername(user.getUsername());
                        realmRegisteredInfo.setPhoneNumber(Long.toString(user.getPhone()));
                        realmRegisteredInfo.setFirstName(user.getFirstName());
                        realmRegisteredInfo.setLastName(user.getLastName());
                        realmRegisteredInfo.setDisplayName(user.getDisplayName());
                        realmRegisteredInfo.setInitials(user.getInitials());
                        realmRegisteredInfo.setColor(user.getColor());
                        realmRegisteredInfo.setStatus(user.getStatus().toString());
                        realmRegisteredInfo.setAvatarCount(user.getAvatarCount());
                        realmRegisteredInfo.setMutual(user.getMutual());
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {

                        Intent intent = new Intent(context, ActivityChat.class);
                        intent.putExtra("peerId", user.getId());
                        intent.putExtra("RoomId", roomid);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }

    private static void goToRoom(String username, final ProtoGlobal.Room room) {

        final Realm realm = Realm.getDefaultInstance();

        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, room.getId()).findFirst();

        if (realmRoom != null) {

            if (realmRoom.isDeleted()) {
                addRoomToDataBaseAndGoToRoom(username, room);
            } else {
                Intent intent = new Intent(G.currentActivity, ActivityChat.class);
                intent.putExtra("RoomId", room.getId());
                G.currentActivity.startActivity(intent);
            }

            realm.close();
        } else {

            addRoomToDataBaseAndGoToRoom(username, room);
        }
    }

    private static void addRoomToDataBaseAndGoToRoom(final String username, final ProtoGlobal.Room room) {

        new Handler(G.currentActivity.getMainLooper()).post(new Runnable() {
            @Override public void run() {

                final Realm realm = Realm.getDefaultInstance();

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override public void execute(Realm realm) {

                        RealmRoom realmRoom1 = RealmRoom.putOrUpdate(room);
                        realmRoom1.setDeleted(true);                            // if in chat activity join to room set deleted goes to false
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override public void onSuccess() {

                        Intent intent = new Intent(G.currentActivity, ActivityChat.class);
                        intent.putExtra("RoomId", room.getId());
                        intent.putExtra("GoingFromUserLink", true);
                        intent.putExtra("ISNotJoin", true);
                        intent.putExtra("UserName", username);

                        G.currentActivity.startActivity(intent);

                        realm.close();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override public void onError(Throwable error) {
                        realm.close();
                    }
                });
            }
        });
    }
}
