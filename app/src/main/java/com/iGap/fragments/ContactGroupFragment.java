package com.iGap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.adapter.StickyHeaderAdapter;
import com.iGap.adapter.items.ContactItemGroup;
import com.iGap.interfaces.OnChannelAddMember;
import com.iGap.interfaces.OnGroupAddMember;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.Contacts;
import com.iGap.module.SUID;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmChannelRoom;
import com.iGap.realm.RealmGroupRoom;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.request.RequestChannelAddMember;
import com.iGap.request.RequestGroupAddMember;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class ContactGroupFragment extends Fragment {
    private FastAdapter fastAdapter;
    private TextView txtStatus;
    private TextView txtNumberOfMember;
    private EditText edtSearch;
    private String textString = "";
    private String participantsLimit = "5000";

    private long roomId;
    private int countAddMemberResponse = 0;
    private int countAddMemberRequest = 0;
    private static ProtoGlobal.Room.Type type;
    private String typeCreate;

    private int sizeTextEditText = 0;
    private List<StructContactInfo> contacts;

    public static ContactGroupFragment newInstance() {
        return new ContactGroupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_group, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            roomId = bundle.getLong("RoomId");
            typeCreate = bundle.getString("TYPE");
            if (bundle.getString("LIMIT") != null) participantsLimit = bundle.getString("LIMIT");
        }

        txtStatus = (TextView) view.findViewById(R.id.fcg_txt_status);
        txtNumberOfMember = (TextView) view.findViewById(R.id.fcg_txt_number_of_member);
        txtNumberOfMember.setText("0" + "/" + participantsLimit + " " + getString(R.string.member));

        if (typeCreate.equals("CHANNEL")) {
            txtNumberOfMember.setText("Add Members");
        }

        edtSearch = (EditText) view.findViewById(R.id.fcg_edt_search);

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fcg_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(ContactGroupFragment.this)
                        .commit();
            }
        });

        RippleView rippleDone = (RippleView) view.findViewById(R.id.fcg_ripple_done);
        rippleDone.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                if (typeCreate.equals("CHANNEL")) { // addMemberChannel
                    G.onChannelAddMember = new OnChannelAddMember() {
                        @Override
                        public void onChannelAddMember(Long RoomId, Long UserId, ProtoGlobal.ChannelRoom.Role role) {

                            countAddMemberResponse++;
                            if (countAddMemberResponse == countAddMemberRequest) {

                                addOwnerToDatabase(RoomId, ProtoGlobal.Room.Type.CHANNEL);

                                Realm realm = Realm.getDefaultInstance();
                                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, RoomId).findFirst();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realmRoom.getChannelRoom().setParticipantsCountLabel(realmRoom.getChannelRoom().getMembers().size() + "");
                                    }
                                });
                                realm.close();

                                Intent intent = new Intent(G.context, ActivityChat.class);
                                intent.putExtra("RoomId", RoomId);
                                startActivity(intent);
                                getActivity().getSupportFragmentManager().beginTransaction().remove(ContactGroupFragment.this).commit();
                            }
                        }

                        @Override
                        public void onError(int majorCode, int minorCode) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }

                        @Override
                        public void onTimeOut() {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    };

                    if (getSelectedList().size() > 0) {
                        for (long peerId : getSelectedList()) {
                            new RequestChannelAddMember().channelAddMember(roomId, peerId, 0);
                        }
                    } else {
                        Intent intent = new Intent(G.context, ActivityChat.class);
                        intent.putExtra("RoomId", ContactGroupFragment.this.roomId);
                        startActivity(intent);
                        getActivity().getSupportFragmentManager().beginTransaction().remove(ContactGroupFragment.this).commit();
                    }

                }

                if (typeCreate.equals("GROUP")) { //  addMemberGroup
                    G.onGroupAddMember = new OnGroupAddMember() {
                        @Override
                        public void onGroupAddMember(Long roomId, Long UserId) {
                            countAddMemberResponse++;
                            if (countAddMemberResponse == countAddMemberRequest) {

                                addOwnerToDatabase(ContactGroupFragment.this.roomId, ProtoGlobal.Room.Type.GROUP);

                                Realm realm = Realm.getDefaultInstance();
                                final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, ContactGroupFragment.this.roomId).findFirst();

                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realmRoom.getGroupRoom().setParticipantsCountLabel(realmRoom.getGroupRoom().getMembers().size() + "");
                                        realmRoom.getGroupRoom().setParticipants_count_limit_label(participantsLimit);
                                    }
                                });
                                realm.close();

                                Intent intent = new Intent(G.context, ActivityChat.class);
                                intent.putExtra("RoomId", ContactGroupFragment.this.roomId);
                                startActivity(intent);
                                getActivity().getSupportFragmentManager().beginTransaction().remove(ContactGroupFragment.this).commit();
                            }
                        }

                        @Override
                        public void onError(int majorCode, int minorCode) {

                            G.onGroupAddMember.onGroupAddMember(-1l, -1l);

                            if (majorCode == 302 && minorCode == 1) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snack =
                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                        getResources().getString(R.string.E_302_1),
                                                        Snackbar.LENGTH_LONG);

                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snack.dismiss();
                                            }
                                        });
                                        snack.show();
                                    }
                                });
                            } else if (majorCode == 302 && minorCode == 2) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snack =
                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                        getResources().getString(R.string.E_302_2),
                                                        Snackbar.LENGTH_LONG);

                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snack.dismiss();
                                            }
                                        });
                                        snack.show();
                                    }
                                });
                            } else if (majorCode == 302 && minorCode == 3) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snack =
                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                        getResources().getString(R.string.E_302_3),
                                                        Snackbar.LENGTH_LONG);

                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snack.dismiss();
                                            }
                                        });
                                        snack.show();
                                    }
                                });
                            } else if (majorCode == 302 && minorCode == 4) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snack =
                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                        getResources().getString(R.string.E_302_4),
                                                        Snackbar.LENGTH_LONG);

                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snack.dismiss();
                                            }
                                        });
                                        snack.show();
                                    }
                                });
                            } else if (majorCode == 303) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snack =
                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                        getResources().getString(R.string.E_303),
                                                        Snackbar.LENGTH_LONG);

                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snack.dismiss();
                                            }
                                        });
                                        snack.show();
                                    }
                                });
                            } else if (majorCode == 304) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snack =
                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                        getResources().getString(R.string.E_304),
                                                        Snackbar.LENGTH_LONG);

                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snack.dismiss();
                                            }
                                        });
                                        snack.show();
                                    }
                                });
                            } else if (majorCode == 305) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Snackbar snack =
                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                        getResources().getString(R.string.E_305),
                                                        Snackbar.LENGTH_LONG);

                                        snack.setAction(getString(R.string.cancel), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                snack.dismiss();
                                            }
                                        });
                                        snack.show();
                                    }
                                });
                            }
                        }
                    };
                    if (getSelectedList().size() > 0) {
                        for (long peerId : getSelectedList()) {
                            new RequestGroupAddMember().groupAddMember(roomId, peerId, 0);
                        }
                    } else {
                        Intent intent = new Intent(G.context, ActivityChat.class);
                        intent.putExtra("RoomId", ContactGroupFragment.this.roomId);
                        startActivity(intent);
                        getActivity().getSupportFragmentManager().beginTransaction().remove(ContactGroupFragment.this).commit();
                    }
                }
            }
        });

        //create our FastAdapter
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        final ItemAdapter itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContactItemGroup>() {
            @Override
            public boolean filter(ContactItemGroup item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase()
                        .startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactItemGroup>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, ContactItemGroup item, int position) {

                item.mContact.isSelected = !item.mContact.isSelected;
                fastAdapter.notifyItemChanged(position);

                refreshView();

                return false;
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > sizeTextEditText) {
                    String s = edtSearch.getText().toString().substring(sizeTextEditText, charSequence.length());
                    itemAdapter.filter(s);
                } else {
                    itemAdapter.filter("");
                }

                edtSearch.setSelection(edtSearch.getText().length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyCode == KeyEvent.KEYCODE_DEL)) {
                    if (edtSearch.getText().length() <= sizeTextEditText) {
                        return true;
                    }
                }

                return false;
            }
        });

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        RecyclerView rv =
                (RecyclerView) view.findViewById(R.id.fcg_recycler_view_add_item_to_group);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        //this adds the Sticky Headers within our list
        final StickyRecyclerHeadersDecoration decoration =
                new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        rv.addItemDecoration(decoration);

        List<IItem> items = new ArrayList<>();
        contacts = Contacts.retrieve(null);

        for (StructContactInfo contact : contacts) {
            items.add(new ContactItemGroup().setContact(contact)
                    .withIdentifier(100 + contacts.indexOf(contact)));
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

    private void addOwnerToDatabase(Long roomId, ProtoGlobal.Room.Type type) {

        Realm realm = Realm.getDefaultInstance();
        final Long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomId).findFirst();

        if (realmRoom != null) {

            if (type == ProtoGlobal.Room.Type.CHANNEL) {

                RealmChannelRoom realmChannelRoom = realmRoom.getChannelRoom();
                if (realmChannelRoom != null) {
                    final RealmList<RealmMember> members = realmChannelRoom.getMembers();

                    final RealmMember realmMember = new RealmMember();
                    realmMember.setId(SUID.id().get());
                    realmMember.setPeerId(userId);
                    realmMember.setRole(ProtoGlobal.ChannelRoom.Role.OWNER.toString());

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            members.add(realmMember);
                        }
                    });
                }

            } else if (type == ProtoGlobal.Room.Type.GROUP) {
                RealmGroupRoom realmGroupRoom = realmRoom.getGroupRoom();
                if (realmGroupRoom != null) {
                    final RealmList<RealmMember> members = realmGroupRoom.getMembers();

                    final RealmMember realmMember = new RealmMember();
                    realmMember.setId(SUID.id().get());
                    realmMember.setPeerId(userId);
                    realmMember.setRole(ProtoGlobal.GroupRoom.Role.OWNER.toString());

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            members.add(realmMember);
                        }
                    });
                }
            }
        }
    }

    private void refreshView() {

        textString = "";

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected) {
                textString += contacts.get(i).displayName + ",";
            }
        }

        if (typeCreate.equals("CHANNEL")) {
            txtNumberOfMember.setVisibility(View.GONE);
        }
        sizeTextEditText = textString.length();
        edtSearch.setText(textString);
    }

    private ArrayList<Long> getSelectedList() {

        ArrayList<Long> list = new ArrayList<>();

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected) {
                countAddMemberRequest++;
                list.add(contacts.get(i).peerId);
            }
        }

        return list;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
