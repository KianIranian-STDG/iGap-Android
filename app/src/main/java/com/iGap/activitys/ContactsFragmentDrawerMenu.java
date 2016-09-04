package com.iGap.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.StickyHeaderAdapter;
import com.iGap.adapter.items.ContactItem;
import com.iGap.interface_package.OnChatGetRoom;
import com.iGap.libs.flowingdrawer.MenuFragment;
import com.iGap.module.ListOfContact;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmContacts;
import com.iGap.realm.RealmRoom;
import com.iGap.request.RequestChatGetRoom;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;


public class ContactsFragmentDrawerMenu extends MenuFragment {

    private FastAdapter fastAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_drawer_layout, container, false);

        return setupReveal(view, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //create our FastAdapter
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        final ItemAdapter itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContactItem>() {
            @Override
            public boolean filter(ContactItem item, CharSequence constraint) {
                return !item.mContact.text.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactItem>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, ContactItem item, int position) {
                chatGetRoom(item.mContact.peerId);
                return false;
            }
        });

        EditText searchView = (EditText) view.findViewById(R.id.edit_search);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemAdapter.filter(editable);
            }
        });

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        //this adds the Sticky Headers within our list
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        rv.addItemDecoration(decoration);

        List<IItem> items = new ArrayList<>();
        ArrayList<StructContactInfo> contacts = ListOfContact.Retrive("");
        for (StructContactInfo contact : contacts) {
            items.add(new ContactItem().setContact(contact).withIdentifier(100 + contacts.indexOf(contact)));
        }
        itemAdapter.add(items);

        //so the headers are aware of changes
        stickyHeaderAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                decoration.invalidateHeaders();
            }
        });


        //restore selections (this has to be done after the items were added
        fastAdapter.withSavedInstanceState(savedInstanceState);
    }

    private void chatGetRoom(final long peerId) {

        final RealmRoom realmRoom = G.realm.where(RealmRoom.class).equalTo("chat_room.peer_id", peerId).findFirst();

        if (realmRoom != null) {
            Log.i("XXX", "Room Exist");
            Intent intent = new Intent(G.context, ActivityChat.class);
            intent.putExtra("ChatID", realmRoom.getId());
            intent.putExtra("ChatType", realmRoom.getType().toString());
            intent.putExtra("NewChatRoom", false);
            //intent.putExtra("IsMute", ); //TODO [Saeed Mozaffari] [2016-09-03 11:12 AM] - set IsMute in RealmRoom
            intent.putExtra("LastSeen", G.realm.where(RealmContacts.class).equalTo("id", peerId).findFirst().getLast_seen());
            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            G.context.startActivity(intent);

        } else {
            Log.i("XXX", "Create new Room");
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final long roomId) {
                    G.currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(G.context, ActivityChat.class);
                            intent.putExtra("NewChatRoom", true);
                            intent.putExtra("ChatType", ProtoGlobal.Room.Type.CHAT.toString());
                            intent.putExtra("LastSeen", G.realm.where(RealmContacts.class).equalTo("id", peerId).findFirst().getLast_seen());
                            intent.putExtra("RoomId", roomId);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            G.context.startActivity(intent);
                        }
                    });
                }
            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundel
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
