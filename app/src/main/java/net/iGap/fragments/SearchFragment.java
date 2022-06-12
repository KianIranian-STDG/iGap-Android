/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.SearchItem;
import net.iGap.adapter.items.SearchItemHeader;
import net.iGap.controllers.MessageController;
import net.iGap.helper.FileLog;
import net.iGap.helper.GoToChatActivity;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperUrl;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.accountManager.DbManager;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.observers.interfaces.IClientSearchUserName;
import net.iGap.proto.ProtoClientSearch;
import net.iGap.proto.ProtoClientSearchUsername;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAvatar;
import net.iGap.realm.RealmContacts;
import net.iGap.realm.RealmMember;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestChatGetRoom;
import net.iGap.request.RequestClientSearchUsername;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Case;
import io.realm.RealmResults;

import static net.iGap.proto.ProtoGlobal.Room.Type.CHAT;

public class SearchFragment extends BaseFragment {

    private FastAdapter fastAdapter;
    private ArrayList<StructSearch> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ImageView imvNothingFound;
    private TextView txtSearchHint;
    private long index = 500;
    private String preventRepeatSearch = "";
    private ContentLoadingProgressBar loadingProgressBar;
    private static final String SEARCH_TXT = "searchText";
    private static final String SEARCH_AUTO = "isSearchAuto";
    private String searchTxt;
    private long roomId;
    private RelativeLayout mainContainer;

    public static SearchFragment newInstance() {
        Bundle bundle = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SearchFragment newInstance(String searchText, boolean searchAuto) {
        Bundle bundle = new Bundle();
        SearchFragment fragment = new SearchFragment();
        bundle.putString(SEARCH_TXT, searchText);
        bundle.putBoolean(SEARCH_AUTO, searchAuto);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SearchFragment newInstance(long roomId, String searchText, boolean searchAuto) {
        Bundle bundle = new Bundle();
        SearchFragment fragment = new SearchFragment();
        bundle.putString(SEARCH_TXT, searchText);
        bundle.putBoolean(SEARCH_AUTO, searchAuto);
        bundle.putLong("roomId", roomId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().getBoolean(SEARCH_AUTO, false)) {
            searchTxt = getArguments().getString(SEARCH_TXT);
            roomId = getArguments().getLong("roomId");
        }
    }

    @Override
    public View createView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.search_fragment_layout, null, true);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        openKeyBoard();
        initComponent(view);
        initRecycleView();
        if (searchTxt != null) {
            startOrReStartSearchTimer();
        }


    }

    private void initComponent(View view) {

        index = 500;


        loadingProgressBar = view.findViewById(R.id.sfl_progress_loading);
        imvNothingFound = view.findViewById(R.id.sfl_imv_nothing_found);
        mainContainer = view.findViewById(R.id.mainContainer);
        imvNothingFound.setImageResource(R.drawable.find1);
        txtSearchHint = view.findViewById(R.id.txt_search_hint);

        mainContainer.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        txtSearchHint.setTextColor(Theme.getColor(Theme.key_default_text));
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imvNothingFound.setVisibility(View.VISIBLE);
                txtSearchHint.setVisibility(View.VISIBLE);
            }
        }, 150);


        recyclerView = view.findViewById(R.id.sfl_recycleview);
    }

    private void initRecycleView() {

        itemAdapter = new ItemAdapter();
        fastAdapter = FastAdapter.with(itemAdapter);

        fastAdapter.withOnClickListener((OnClickListener<IItem>) (v, adapter, currentItem, position) -> {

            if (currentItem instanceof SearchItemHeader) {

            } else {
                SearchItem si = (SearchItem) currentItem;
                goToRoom(si.item.id, si.item.type, si.item.messageId, si.item.userName);

                hideKeyboard();
            }

            return false;
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fastAdapter);
    }

    public void onTextChanged(String text) {
        if (text != null) {
            searchTxt = text;
        }
        if (text.trim().length() < 1) {
            cancelSearchTimer();
            fillList("");
            preventRepeatSearch = "";
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imvNothingFound.setVisibility(View.VISIBLE);
                    txtSearchHint.setText(R.string.at_least_5_characters_are_required_for_a_global_search);
                    txtSearchHint.setVisibility(View.VISIBLE);
                }
            }, 150);
        }
        startOrReStartSearchTimer();
    }

    public void onOpenSearchKeyboard() {

    }

    private void fillList(String text) {

        itemAdapter.clear();
        if (fastAdapter == null) {
            initRecycleView();
        }
        fastAdapter.clearTypeInstance();

        int strSize = text.length();

//        if (text.startsWith("#")) {
//            fillListItemHashtag(text);
//            return;
//        }

        if (strSize >= 5) {
            if (getRequestManager().isUserLogin()) {
                if ((!text.equals(preventRepeatSearch))) {
                    itemAdapter.clear();
/*                    if (text.startsWith("@")) {

                        search(text.substring(1));
                        new RequestClientSearchUsername().clientSearchUsername(text.substring(1));
                    } else {
                        search(text);
                      new RequestClientSearchUsername().clientSearchUsername(text);
                    }*/

                    search(text);


                    loadingProgressBar.setVisibility(View.VISIBLE);
                    preventRepeatSearch = text;
                }
            } else {
                HelperError.showSnackMessage(getString(R.string.there_is_no_connection_to_server), false);
            }
        } else if (strSize >= 1 && strSize < 5) {
            fillListItemGlobal(text);
        } else {
            preventRepeatSearch = "";
        }

/*
        G.onClientSearchUserName = new IClientSearchUserName() {
            @Override
            public void OnGetList(final ProtoClientSearchUsername.ClientSearchUsernameResponse.Builder builderList) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        G.refreshRealmUi();
                        loadingProgressBar.setVisibility(View.GONE);
                        fillAfterResponse();
                    }
                });
            }

            @Override
            public void OnErrore() {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (getContext() == null) {
                            return;
                        }

                        loadingProgressBar.setVisibility(View.GONE);
                        fillAfterResponse();
                        HelperError.showSnackMessage(getString(R.string.connection_error), false);
                    }
                });
            }
        };
*/

    }

    List<ProtoClientSearch.ClientSearchResponse.Info> infoMain = new ArrayList<>();
    List<ProtoClientSearch.ClientSearchResponse.Info> infoGlobal = new ArrayList<>();

    private void search(String text) {
        IG_RPC.Client_search clientSearch = new IG_RPC.Client_search();
        clientSearch.query = text;
        getRequestManager().sendRequest(clientSearch, (response, error) -> {
            if (error == null && response != null) {
                IG_RPC.Res_client_search res = (IG_RPC.Res_client_search) response;

                G.runOnUiThread(() -> loadingProgressBar.setVisibility(View.GONE));


                if (res.infoList != null && res.infoList.size() > 0) {
                    G.runOnUiThread(() -> loadingProgressBar.setVisibility(View.GONE));
                    G.runOnUiThread(() -> txtSearchHint.setVisibility(View.GONE));
                    G.runOnUiThread(() -> imvNothingFound.setVisibility(View.GONE));
                    infoMain.clear();
                    infoGlobal.clear();
                    for (ProtoClientSearch.ClientSearchResponse.Info info : res.infoList) {
                        switch (info.getCategory()) {
                            case MINE:

                                infoMain.add(info);

                                break;
                            case GLOBAL:

                                infoGlobal.add(info);
                                break;
                        }
                    }
                    fillAfterResponse();
                } else {
                    G.runOnUiThread(() -> txtSearchHint.setVisibility(View.VISIBLE));
                    G.runOnUiThread(() -> txtSearchHint.setText(R.string.there_is_no_any_result));
                    G.runOnUiThread(() -> imvNothingFound.setVisibility(View.VISIBLE));
                }


            } else {
                IG_RPC.Error err = (IG_RPC.Error) error;

                FileLog.e("client Search error  ", "major: " + err.major + "  minor: " + err.minor);
            }
        });
    }

    private void fillAfterResponse() {
        fileWithMainListGlobal(searchTxt);
    }

    private void fileWithMainList() {
        list.clear();
        listProcessing(infoMain, getString(R.string.search_main));
        updateAdapter();
    }


    private void fileWithMainListGlobal(String text) {

        list.clear();
        listProcessing(infoMain, getString(R.string.search_main));
        listProcessing(infoGlobal, getString(R.string.search_global));

//        fillChat(text);
        updateAdapter();
    }

    private void listProcessing(List<ProtoClientSearch.ClientSearchResponse.Info> infoList, String header) {
        if (infoList.size() > 0) addHeader(header);
        for (ProtoClientSearch.ClientSearchResponse.Info info : infoList) {

            switch (info.getType()) {
                case ROOM:

                    if (info.getRoom().hasChannelRoomExtra()) {
                        StructSearch item = new StructSearch();
                        DbManager.getInstance().doRealmTransaction(realm -> {
                            RealmRoom room2 = RealmRoom.putOrUpdate(info.getRoom(), realm);
                            item.avatar = room2.getAvatar();
                            item.idDetectAvatar = room2.getId();
                            item.time = room2.getUpdatedTime();

                            room2.setDeleted(true);
                        });


                        item.roomType = info.getRoom().getType();
                        item.name = info.getRoom().getTitle();
                        item.isVerified = info.getRoom().getChannelRoomExtra().getVerified();
                        item.participantsCount = info.getRoom().getChannelRoomExtra().getParticipantsCount();
                        item.id = info.getRoom().getId();

                        item.userName = info.getRoom().getChannelRoomExtra().getPublicExtra().getUsername();
                        item.type = SearchType.room;
                        item.initials = info.getRoom().getInitials();
                        item.color = info.getRoom().getColor();
                        list.add(item);


                    } else if (info.getRoom().hasGroupRoomExtra()) {
                        StructSearch item = new StructSearch();

                        DbManager.getInstance().doRealmTransaction(realm -> {
                            RealmRoom room2 = RealmRoom.putOrUpdate(info.getRoom(), realm);
                            item.avatar = room2.getAvatar();
                            item.idDetectAvatar = room2.getId();
                            item.time = room2.getUpdatedTime();

                            room2.setDeleted(true);
                        });

                        item.roomType = info.getRoom().getType();
                        item.name = info.getRoom().getTitle();

                        item.id = info.getRoom().getId();

                        item.userName = info.getRoom().getGroupRoomExtra().getPublicExtra().getUsername();
                        item.participantsCount = info.getRoom().getGroupRoomExtra().getParticipantsCount();

                        item.idDetectAvatar = info.getRoom().getId();

                        item.type = SearchType.room;
                        item.initials = info.getRoom().getInitials();
                        item.color = info.getRoom().getColor();


                        list.add(item);


                    }
                    /*else if (info.getRoom().hasChatRoomExtra()){
                        StructSearch item = new StructSearch();

                        item.name = contact.getDisplayName();
                        item.isVerified = contact.isVerified();
                        item.time = info.getLastSeen();
                        item.userName = contact.getUsername();
                        item.comment = "";
                        item.id = info.getRoom().getId();
                        item.idDetectAvatar = info.getRoom().getId();
                        item.type = SearchType.contact;
                        item.initials = info.getRoom().getInitials();
                        item.color = info.getRoom().getColor();
                        item.avatar = contact.getLastAvatar(realm);
                        list.add(item);
                    }*/

                    break;
                case USER:


                    StructSearch item = new StructSearch();
                    DbManager.getInstance().doRealmTransaction(realm -> {
                        RealmRegisteredInfo realmRoom = RealmRegisteredInfo.putOrUpdate(realm, info.getUser());
                        item.avatar = realmRoom.getLastAvatar(realm);

                        item.idDetectAvatar = realmRoom.getId();
                        item.time = realmRoom.getLastSeen();
                    });


                    item.name = info.getUser().getDisplayName();
                    item.isVerified = info.getUser().getVerified();
                    item.time = info.getUser().getLastSeen();
                    item.userName = info.getUser().getUsername();
                    item.comment = "";
                    item.id = info.getUser().getId();
                    item.type = SearchType.contact;
                    item.initials = info.getUser().getInitials();
                    item.color = info.getUser().getColor();

                    list.add(item);
                    break;
            }

        }
    }


    private void fillListItemHashtag(String text) {

        list.clear();
        fillHashtag(text);
        updateAdapter();

    }

    /* private void fillListItemAtsign(String text) {

         list.clear();
         fillBot(text);
         fillChat(text);
         fillRoomListGroup(text);
         fillRoomListChannel(text);
         updateAdapter();

     }

 */
    private void fillListItemGlobal(String text) {

        list.clear();

        fillBot(text);

        fillChat(text);

        fillRoomListGroup(text);

        fillRoomListChannel(text);

        fillMessages(text);

        fillHashtag(text);

        updateAdapter();

    }

    private void fillHashtag(String tt) {
        DbManager.getInstance().doRealmTask(realm -> {
            String text = tt;
            if (!text.startsWith("#")) {
                text = "#" + text;
            }
            RealmResults<RealmRoomMessage> results;
            if (roomId != 0) {
                results = realm.where(RealmRoomMessage.class).equalTo("roomId", roomId).equalTo("hasMessageLink", true).contains("message", text, Case.INSENSITIVE).equalTo("edited", false).isNotEmpty("message").findAll();
            } else {
                results = realm.where(RealmRoomMessage.class).equalTo("hasMessageLink", true).contains("message", text, Case.INSENSITIVE).equalTo("edited", false).isNotEmpty("message").findAll();
            }

            if (results != null && results.size() > 0) {

                addHeader(G.fragmentActivity.getResources().getString(R.string.hashtag));
                for (RealmRoomMessage roomMessage : results) {


                    StructSearch item = new StructSearch();

                    item.time = roomMessage.getUpdateTime();
                    item.comment = roomMessage.getMessage();
                    item.id = roomMessage.getRoomId();
                    item.type = SearchType.message;
                    item.messageId = roomMessage.getMessageId();

                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomMessage.getRoomId()).findFirst();

                    if (realmRoom != null) { // room exist
                        item.name = realmRoom.getTitle();
                        if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom().isVerified() || realmRoom.getType() == CHAT && realmRoom.getChatRoom().isVerified()) {
                            item.isVerified = true;
                        }
                        item.initials = realmRoom.getInitials();
                        item.color = realmRoom.getColor();
                        item.roomType = realmRoom.getType();
                        item.avatar = realmRoom.getAvatar();
                        if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT && realmRoom.getChatRoom() != null) {
                            item.idDetectAvatar = realmRoom.getChatRoom().getPeerId();
                        } else {
                            item.idDetectAvatar = realmRoom.getId();
                        }
                        list.add(item);
                    }
                }
            }
        });
    }


    private void updateAdapter() {
        List<IItem> items = new ArrayList<>();
        for (StructSearch item : list) {
            if (item != null) {
                if (item.type == SearchType.header) {
                    items.add(new SearchItemHeader().setText(item.name).withIdentifier(index++));
                } else {
                    items.add(new SearchItem(avatarHandler).setContact(item).withIdentifier(index++));
                }
            }
        }

        if (items.size() > 0) {
            txtSearchHint.setVisibility(View.GONE);
            imvNothingFound.setVisibility(View.GONE);
        } else {
            txtSearchHint.setVisibility(View.VISIBLE);
            txtSearchHint.setText(R.string.there_is_no_any_result);
            imvNothingFound.setVisibility(View.VISIBLE);
        }


        G.runOnUiThread(() -> {
            itemAdapter.clear();
            itemAdapter.add(items);
            fastAdapter.notifyAdapterDataSetChanged();
        });

    }

    private void fillRoomListGroup(String text) {
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmResults<RealmRoom> results;

            if (searchTxt.startsWith("@")) {
                results = realm.where(RealmRoom.class).beginGroup().contains("channelRoom.username", text, Case.INSENSITIVE).or().contains("groupRoom.username", text, Case.INSENSITIVE).endGroup().equalTo("type", "GROUP", Case.INSENSITIVE).findAll();

            } else {
                results = realm.where(RealmRoom.class).beginGroup().contains("title", text, Case.INSENSITIVE).or().contains("channelRoom.username", text, Case.INSENSITIVE).or().contains("groupRoom.username", text, Case.INSENSITIVE).endGroup().equalTo("type", "GROUP", Case.INSENSITIVE).findAll();
            }

            if (results != null) {

                if (results.size() > 0 && getContext() != null)
                    addHeader(getContext().getString(R.string.Groups));


                for (RealmRoom realmRoom : results) {

                    StructSearch item = new StructSearch();

                    item.roomType = realmRoom.getType();
                    item.name = realmRoom.getTitle();
                    if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom().isVerified() || realmRoom.getType() == CHAT && realmRoom.getChatRoom().isVerified()) {
                        item.isVerified = true;
                    }
                    item.time = realmRoom.getUpdatedTime();
                    item.id = realmRoom.getId();
                    if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT && realmRoom.getChatRoom() != null) {
                        item.idDetectAvatar = realmRoom.getChatRoom().getPeerId();
                    } else {

                        if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP && realmRoom.getGroupRoom() != null) {
                            item.userName = realmRoom.getGroupRoom().getUsername();

                        } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom() != null) {
                            item.userName = realmRoom.getChannelRoom().getUsername();

                        }

                        item.idDetectAvatar = realmRoom.getId();
                    }
                    item.type = SearchType.room;
                    item.initials = realmRoom.getInitials();
                    item.color = realmRoom.getColor();
                    item.avatar = realmRoom.getAvatar();
                    item.participantsCount=realmRoom.groupRoom.participants_count;

                    list.add(item);
                }
            }
        });
    }

    private void fillRoomListChannel(String text) {
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmResults<RealmRoom> results;

            if (searchTxt.startsWith("@")) {
                results = realm.where(RealmRoom.class).beginGroup().contains("channelRoom.username", text, Case.INSENSITIVE).or().contains("groupRoom.username", text, Case.INSENSITIVE).endGroup().equalTo("type", "CHANNEL", Case.INSENSITIVE).findAll();

            } else {
                results = realm.where(RealmRoom.class).beginGroup().contains("title", text, Case.INSENSITIVE).or().contains("channelRoom.username", text, Case.INSENSITIVE).or().contains("groupRoom.username", text, Case.INSENSITIVE).endGroup().equalTo("type", "CHANNEL", Case.INSENSITIVE).findAll();
            }

            if (results != null && results.size() > 0) {

                addHeader(G.fragmentActivity.getResources().getString(R.string.channel));

                for (RealmRoom realmRoom : results) {

                    StructSearch item = new StructSearch();

                    item.roomType = realmRoom.getType();
                    item.name = realmRoom.getTitle();
                    if (realmRoom.getChannelRoom() != null && realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom().isVerified() || realmRoom.getType() == CHAT && realmRoom.getChatRoom().isVerified()) {
                        item.isVerified = true;
                    }
                    item.time = realmRoom.getUpdatedTime();
                    item.id = realmRoom.getId();
                    if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT && realmRoom.getChatRoom() != null) {
                        item.idDetectAvatar = realmRoom.getChatRoom().getPeerId();
                    } else {

                        if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP && realmRoom.getGroupRoom() != null) {
                            item.userName = realmRoom.getGroupRoom().getUsername();

                        } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom() != null) {
                            item.userName = realmRoom.getChannelRoom().getUsername();

                        }

                        item.idDetectAvatar = realmRoom.getId();
                    }
                    item.type = SearchType.room;
                    item.initials = realmRoom.getInitials();
                    item.color = realmRoom.getColor();
                    item.avatar = realmRoom.getAvatar();
                    item.participantsCount=realmRoom.channelRoom!=null? realmRoom.channelRoom.participants_count:0;

                    list.add(item);
                }
            }
        });
    }

    private void fillBot(String text) {
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmResults<RealmRegisteredInfo> results;

            if (searchTxt.startsWith("@")) {
                results = realm.where(RealmRegisteredInfo.class).contains("username", text, Case.INSENSITIVE).equalTo("isBot", true).findAll();

            } else {
                results = realm.where(RealmRegisteredInfo.class).beginGroup().contains("username", text, Case.INSENSITIVE).or().contains("displayName", text).endGroup().equalTo("isBot", true).findAll();
            }

            if (results != null && results.size() > 0) {

                addHeader(G.fragmentActivity.getResources().getString(R.string.bot));

                for (RealmRegisteredInfo contact : results) {


                    StructSearch item = new StructSearch();

                    item.name = contact.getDisplayName();
                    item.isVerified = contact.isVerified();
                    item.time = contact.getLastSeen();
                    item.userName = contact.getUsername();
                    item.comment = "";
                    item.id = contact.getId();
                    item.idDetectAvatar = contact.getId();
                    item.type = SearchType.contact;
                    item.initials = contact.getInitials();
                    item.color = contact.getColor();
                    item.avatar = contact.getLastAvatar(realm);
                    list.add(item);
                }
            }
        });
    }

    private void fillChat(String text) {
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmResults<RealmRegisteredInfo> results;

            if (searchTxt.startsWith("@")) {
                results = realm.where(RealmRegisteredInfo.class).contains("username", text, Case.INSENSITIVE).equalTo("isBot", false).findAll();

            } else {
                results = realm.where(RealmRegisteredInfo.class).equalTo("isBot", false).beginGroup().contains("username", text, Case.INSENSITIVE).or().contains("displayName", text, Case.INSENSITIVE).endGroup().findAll();
            }

            if (results != null && results.size() > 0) {

                addHeader(G.fragmentActivity.getResources().getString(R.string.member));

                for (RealmRegisteredInfo contact : results) {


                    StructSearch item = new StructSearch();

                    item.name = contact.getDisplayName();
                    item.isVerified = contact.isVerified();
                    item.time = contact.getLastSeen();
                    item.userName = contact.getUsername();
                    item.comment = "";
                    item.id = contact.getId();
                    item.idDetectAvatar = contact.getId();
                    item.type = SearchType.contact;
                    item.initials = contact.getInitials();
                    item.color = contact.getColor();
                    item.avatar = contact.getLastAvatar(realm);
                    list.add(item);
                }
            }
        });
    }

    private void fillContacts(String text) {
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmResults<RealmContacts> results;

            if (searchTxt.startsWith("@")) {
                results = realm.where(RealmContacts.class).contains("username", text, Case.INSENSITIVE).findAll();

            } else {
                results = realm.where(RealmContacts.class).beginGroup().contains("username", text, Case.INSENSITIVE).or().contains("display_name", text).endGroup().findAll();
            }

            if (results != null) {

                for (RealmContacts contact : results) {
                    Long phone = contact.getPhone();
                    String str = phone.toString().replaceAll(" ", "");
                    if (str.length() > 10) {
                        str = str.substring(str.length() - 10);
                    }

                    StructSearch item = new StructSearch();

                    item.name = contact.getDisplay_name();
                    item.time = contact.getLast_seen();
                    item.isVerified = contact.isVerified();
                    item.comment = str;
                    item.userName = contact.getUsername();
                    item.id = contact.getId();
                    item.idDetectAvatar = contact.getId();
                    item.type = SearchType.contact;
                    item.initials = contact.getInitials();
                    item.color = contact.getColor();
                    item.avatar = contact.getAvatar();
                    list.add(item);
                }
            }
        });
    }

    private void addHeader(String header) {
        StructSearch item = new StructSearch();
        item.name = header;
        item.type = SearchType.header;
        list.add(item);
    }

    private void fillMessages(String text) {
        DbManager.getInstance().doRealmTask(realm -> {
            final RealmResults<RealmRoomMessage> results = realm.where(RealmRoomMessage.class).contains("message", text, Case.INSENSITIVE).equalTo("deleted", false).isNotEmpty("message").findAll();
            if (results != null && results.size() > 0) {
                addHeader(G.fragmentActivity.getResources().getString(R.string.messages));
                for (RealmRoomMessage roomMessage : results) {

                    StructSearch item = new StructSearch();

                    item.time = roomMessage.getUpdateTime();
                    item.comment = roomMessage.getMessage();
                    item.id = roomMessage.getRoomId();
                    item.type = SearchType.message;
                    item.messageId = roomMessage.getMessageId();

                    RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", roomMessage.getRoomId()).findFirst();

                    if (realmRoom != null) { // room exist
                        item.name = realmRoom.getTitle();

                        item.initials = realmRoom.getInitials();
                        item.color = realmRoom.getColor();
                        item.roomType = realmRoom.getType();
                        item.avatar = realmRoom.getAvatar();
                        if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom().isVerified() || realmRoom.getType() == CHAT && realmRoom.getChatRoom().isVerified()) {
                            item.isVerified = true;
                        }
                        if (realmRoom.getType() == ProtoGlobal.Room.Type.CHAT && realmRoom.getChatRoom() != null) {
                            item.idDetectAvatar = realmRoom.getChatRoom().getPeerId();

                        } else {

                            if (realmRoom.getType() == ProtoGlobal.Room.Type.GROUP && realmRoom.getGroupRoom() != null) {
                                item.userName = realmRoom.getGroupRoom().getUsername();

                            } else if (realmRoom.getType() == ProtoGlobal.Room.Type.CHANNEL && realmRoom.getChannelRoom() != null) {
                                item.userName = realmRoom.getChannelRoom().getUsername();
                            }

                            item.idDetectAvatar = realmRoom.getId();
                        }
                        list.add(item);
                    }
                }
            }
        });
    }

    private void goToRoom(final long id, SearchType type, long messageId, String userName) {
        DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom;

            if (type == SearchType.message) {
                realmRoom = realm.where(RealmRoom.class).equalTo("id", id).findFirst();
                goToRoomWithRealm(realmRoom, type, id, messageId);
            } else if (type == SearchType.contact) {
                realmRoom = realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", id).findFirst();
                goToRoomWithRealm(realmRoom, type, id, messageId);
            } else if (type == SearchType.room) {
                if (userName != null && userName.length() > 1) {
                    HelperUrl.checkUsernameAndGoToRoom(getActivity(), userName, HelperUrl.ChatEntry.profile);
                    /*Log.wtf(this.getClass().getName(),"goTo chat");
                    popBackStackFragment();*/
                } else {
                    realmRoom = realm.where(RealmRoom.class).equalTo("id", id).findFirst();
                    goToRoomWithRealm(realmRoom, type, id, messageId);
                }

            }
        });
    }


    public void goToRoomWithRealm(RealmRoom realmRoom, SearchType type, long id, long messageId) {
        if (realmRoom != null) {
            G.refreshRealmUi();
            if (type == SearchType.message) {
                new GoToChatActivity(realmRoom.getId()).setMessageID(messageId).startActivity(getActivity());
            } else {
                new GoToChatActivity(realmRoom.getId()).startActivity(getActivity());
            }
        } else {
            new RequestChatGetRoom().chatGetRoom(id, new RequestChatGetRoom.OnChatRoomReady() {
                @Override
                public void onReady(ProtoGlobal.Room room) {
                    DbManager.getInstance().doRealmTransaction(realm -> {
                        RealmRoom room2 = RealmRoom.putOrUpdate(room, realm);
                        room2.setDeleted(true);
                    });
                    if (getActivity() != null && getActivity().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED))
                        new GoToChatActivity(room.getId()).setPeerID(id).setMessageID(messageId).startActivity(getActivity());

                }

                @Override
                public void onError(int major, int minor) {

                }
            });
        }


    }

    public void onSearchCollapsed() {
        if (getActivity() != null) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.detach(this).commit();
        }
    }


//*********************************************************************************************

    public enum SearchType {
        header, room, contact, message, CHANNEL, GROUP
    }

    public class StructSearch {
        public String name = "";
        public String comment = "";
        public String userName = "";
        public String initials;
        public boolean isVerified = false;
        public String color;
        public long time = 0;
        public long id = 0;
        public long participantsCount = 0;
        public long idDetectAvatar = 0; // fill roomId for rooms and userId for contacts
        public long messageId = 0;
        public RealmAvatar avatar;
        public ProtoGlobal.Room.Type roomType;
        public SearchType type = SearchType.header;
    }


    private Timer mTimerSearch;
    private TimerTask mTimerTaskSearch;

    private void startOrReStartSearchTimer() {

        cancelSearchTimer();

        mTimerTaskSearch = new TimerTask() {
            @Override
            public void run() {
                String text = searchTxt.trim();
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        fillList(text);
                    }
                });
            }
        };

        mTimerSearch = new Timer();
        mTimerSearch.schedule(mTimerTaskSearch, 500);
    }

    private void cancelSearchTimer() {

        if (mTimerSearch != null) {
            mTimerTaskSearch.cancel();
            mTimerSearch.cancel();
            mTimerTaskSearch = null;
            mTimerSearch = null;
        }

    }
}
