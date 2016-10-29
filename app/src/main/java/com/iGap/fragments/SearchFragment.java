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
import android.widget.EditText;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.adapter.items.SearchItem;
import com.iGap.adapter.items.SearchItemHeader;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmChatHistory;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestChatGetRoom;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.List;

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

        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.sfl_btn_back);
        final RippleView rippleBack = (RippleView) view.findViewById(R.id.sfl_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rippleBack.getWindowToken(), 0);
                getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFragment.this).commit();

            }
        });

        MaterialDesignTextView btnClose = (MaterialDesignTextView) view.findViewById(R.id.sfl_btn_close);
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
        addHeader(getString(R.string.chats));
        fillRoomList();
        addHeader(getString(R.string.contacts));
        fillContacts();
        addHeader(getString(R.string.messages));
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

            item.roomType = realmRoom.getType();
            item.name = realmRoom.getTitle();
            item.time = realmRoom.getLastMessageTime();
            item.id = realmRoom.getId();
            item.type = SearchType.room;
            item.initials = realmRoom.getInitials();
            item.color = realmRoom.getColor();
            item.avatar = realmRoom.getAvatar();

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
                item.initials = contact.getInitials();
                item.color = contact.getColor();
                item.avatar = contact.getAvatar();

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

        //TODO [Saeed Mozaffari] [2016-10-18 10:19 AM] - now load avatar just from local . shayad avatar download nashode bashe. inja download nemikonim faghat agar vojud dashte bashe neshun midim

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

                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, realmChatHistory.getRoomId()).findFirst();

                if (realmRoom != null) { // room exist
                    item.name = realmRoom.getTitle();
                    item.initials = realmRoom.getInitials();
                    item.color = realmRoom.getColor();
                    item.roomType = realmRoom.getType();
                    item.avatar = realmRoom.getAvatar();
                }

                list.add(item);
            }
        }
    }

    private void goToRoom(final long id, SearchType type, long messageId) {

        final Realm realm = Realm.getDefaultInstance();
        RealmRoom realmRoom = null;

        if (type == SearchType.room || type == SearchType.message) {
            realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, id).findFirst();
        } else if (type == SearchType.contact) {
            realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, id).findFirst();
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

                @Override
                public void onChatGetRoomTimeOut() {

                }

                @Override
                public void onChatGetRoomError() {

                }
            };

            new RequestChatGetRoom().chatGetRoom(id);
        }
        realm.close();
    }

    //*********************************************************************************************

    public class StructSearch {
        public String name = "";
        public String comment = "";
        public String initials;
        public String color;
        public long time = 0;
        public long id = 0;
        public long messageId = 0;
        public RealmAvatar avatar;
        public RoomType roomType;
        public SearchType type = SearchType.header;
    }

    public enum SearchType {
        header,
        room,
        contact,
        message;
    }


}
