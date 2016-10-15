package com.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityChat;
import com.iGap.adapter.items.SearchItem;
import com.iGap.adapter.items.SearchItemHeader;
import com.iGap.interface_package.OnChatGetRoom;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestChatGetRoom;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by android3 on 10/10/2016.
 */
public class SearchFragment extends Fragment {

    private FastAdapter fastAdapter;
    private EditText edtSearch;
    private ArrayList<StructSearch> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private boolean isFillList = false;


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponent(view);
        initRecycleView();
    }


    private void initComponent(View view) {

        edtSearch = (EditText) view.findViewById(R.id.sfl_edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() == 1 && !isFillList)
                    fillList();

                itemAdapter.filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);

        Button btnBack = (Button) view.findViewById(R.id.sfl_btn_back);
        btnBack.setTypeface(G.fontawesome);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.sfl_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();
            }
        });

        Button btnClose = (Button) view.findViewById(R.id.sfl_btn_close);
        btnClose.setTypeface(G.fontawesome);
        RippleView rippleDown = (RippleView) view.findViewById(R.id.sfl_ripple_done);
        rippleDown.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                edtSearch.setText("");
            }
        });


        recyclerView = (RecyclerView) view.findViewById(R.id.sfl_recycleview);


    }


    private void initRecycleView() {

        fastAdapter = new FastAdapter();
        itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<IItem>() {
            @Override
            public boolean filter(IItem currentItem, CharSequence constraint) {
                if (currentItem instanceof SearchItemHeader) {
                    return false;
                } else {
                    SearchItem si = (SearchItem) currentItem;

                    if (si.item.type == SearchType.message) {
                        return !si.item.comment.toLowerCase().contains(String.valueOf(constraint).toLowerCase());
                    } else {
                        return !si.item.name.toLowerCase().contains(String.valueOf(constraint).toLowerCase());
                    }
                }
            }
        });

        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<IItem>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, IItem currentItem, int position) {

                if (currentItem instanceof SearchItemHeader) {

                } else {
                    SearchItem si = (SearchItem) currentItem;
                    goToRoom(si.item.id, si.item.type, si.item.messageId);

                    InputMethodManager imm = (InputMethodManager) G.context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                }

                Log.e("ddd", position + "");

                return false;
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter.wrap(fastAdapter));

    }


    private void fillList() {

        list.clear();
        addHeader("Chats");
        fillRoomList();
        addHeader("Contacts");
        fillContacts();
        addHeader("Messages");
        fillMessages();

        List<IItem> items = new ArrayList<>();

        for (StructSearch item : list) {
            if (item.type == SearchType.header) {
                items.add(new SearchItemHeader().setText(item.name).withIdentifier(100 + list.indexOf(item)));
            } else {
                items.add(new SearchItem().setContact(item).withIdentifier(100 + list.indexOf(item)));
            }
        }

        itemAdapter.add(items);

        isFillList = true;

    }

    private void fillRoomList() {

        Realm realm = Realm.getDefaultInstance();

        for (RealmRoom realmRoom : realm.where(RealmRoom.class).findAll()) {

            StructSearch item = new StructSearch();

//            item.avatar=realmRoom.getAvatar().getFile().getLocalFilePath();
//            if(item.avatar==null) {
//                item.avatar = realmRoom.getAvatar().getFile().getLocalThumbnailPath();
//            }

            item.roomType = realmRoom.getType();
            item.name = realmRoom.getTitle();
            item.time = realmRoom.getLastMessageTime();
            item.id = realmRoom.getId();
            item.type = SearchType.room;


            list.add(item);

        }
        realm.close();

    }

    private void fillContacts() {

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<RealmContacts> results = realm.where(RealmContacts.class).findAll();
        if (results != null) {

            for (RealmContacts contact : results) {
                Long phone = contact.getPhone();
                String str = phone.toString().replaceAll(" ", "");
                if (str.length() > 10) {
                    str = str.substring(str.length() - 10, str.length());
                }


                StructSearch item = new StructSearch();

                item.name = contact.getDisplay_name();
                item.time = contact.getLast_seen();
                item.comment = str;
                item.id = contact.getId();
                item.type = SearchType.contact;

                list.add(item);

            }


        }

        realm.close();

    }

    private void addHeader(String header) {
        StructSearch item = new StructSearch();
        item.name = header;
        item.type = SearchType.header;
        list.add(item);
    }

    private void fillMessages() {

        Realm realm = Realm.getDefaultInstance();

        for (RealmChatHistory realmChatHistory : realm.where(RealmChatHistory.class).findAll()) {
            RealmRoomMessage roomMessage = realmChatHistory.getRoomMessage();
            if (roomMessage != null) {
                StructSearch item = new StructSearch();

                item.time = roomMessage.getUpdateTime();
                item.comment = roomMessage.getMessage();
                item.id = realmChatHistory.getRoomId();
                item.type = SearchType.message;
                item.messageId = roomMessage.getMessageId();

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("id", realmChatHistory.getRoomId()).findFirst();

                if (realmRoom != null) { // room exist
                    item.name = realmRoom.getTitle();
                    item.roomType = realmRoom.getType();
                }

                list.add(item);
            }
        }

    }

    private void goToRoom(final long id, SearchType type, long messageId) {

        final Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = null;

        if (type == SearchType.room || type == SearchType.message) {
            realmRoom = realm.where(RealmRoom.class).equalTo("id", id).findFirst();
        } else if (type == SearchType.contact) {
            realmRoom = realm.where(RealmRoom.class).equalTo("chat_room.peer_id", id).findFirst();
        }

        if (realmRoom != null) {
            Intent intent = new Intent(G.context, ActivityChat.class);

            if (type == SearchType.message)
                intent.putExtra("MessageId", messageId);

            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            G.context.startActivity(intent);
            getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();

        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final long roomId) {
                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Realm realm = Realm.getDefaultInstance();
                            Intent intent = new Intent(G.context, ActivityChat.class);
                            intent.putExtra("peerId", id);
                            intent.putExtra("RoomId", roomId);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            realm.close();
                            G.context.startActivity(intent);
                            getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();
                        }
                    });
                }
            };

            new RequestChatGetRoom().chatGetRoom(id);
        }
        realm.close();
    }

    //*********************************************************************************************

    public class StructSearch {
        public String avatar = "";
        public RoomType roomType;
        public String name = "";
        public long time = 0;
        public String comment = "";
        public long id = 0;
        public SearchType type = SearchType.header;
        public long messageId = 0;
    }

    private enum SearchType {
        header,
        room,
        contact,
        message;
    }


}
