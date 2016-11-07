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
import com.iGap.interfaces.OnGroupAddMember;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.Contacts;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmMember;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
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

    private long roomId;
    private int countAddMemberResponse = 0;
    private int countAddMemberRequest = 0;

    private int sizeTextEditText = 0;
    private List<StructContactInfo> contacts;

    public static ContactGroupFragment newInstance() {
        return new ContactGroupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_group, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            roomId = bundle.getLong("RoomId");
        }

        txtStatus = (TextView) view.findViewById(R.id.fcg_txt_status);
        txtNumberOfMember = (TextView) view.findViewById(R.id.fcg_txt_number_of_member);
        edtSearch = (EditText) view.findViewById(R.id.fcg_edt_search);

        MaterialDesignTextView btnBack =
                (MaterialDesignTextView) view.findViewById(R.id.fcg_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.fcg_ripple_back);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        MaterialDesignTextView btnDone =
                (MaterialDesignTextView) view.findViewById(R.id.fcg_btn_done);
        RippleView rippleDone = (RippleView) view.findViewById(R.id.fcg_ripple_done);
        rippleDone.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                G.onGroupAddMember = new OnGroupAddMember() {
                    @Override
                    public void onGroupAddMember() { //TODO [Saeed Mozaffari] [2016-10-15 10:34 AM] - bayad id ra begirim ke daghighan motevajeh shavim ke chand nafar add shodeand
                        countAddMemberResponse++;
                        if (countAddMemberResponse >= countAddMemberRequest) {

                            Realm realm = Realm.getDefaultInstance();

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmRoom realmRoom = realm.where(RealmRoom.class)
                                            .equalTo(RealmRoomFields.ID, roomId)
                                            .findFirst();
                                    realmRoom.getGroupRoom()
                                            .setParticipantsCountLabel(countAddMemberResponse + "");
                                }
                            });

                            realm.close();

                            Intent intent = new Intent(G.context, ActivityChat.class);
                            intent.putExtra("RoomId", roomId);
                            startActivity(intent);
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }

                    @Override
                    public void onError(int majorCode, int minorCode) {
                        if (majorCode == 302 && minorCode == 1) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final Snackbar snack =
                                            Snackbar.make(view.findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_302_1),
                                                    Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
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
                                            Snackbar.make(view.findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_302_2),
                                                    Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
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
                                            Snackbar.make(view.findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_302_3),
                                                    Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
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
                                            Snackbar.make(view.findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_302_4),
                                                    Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
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
                                            Snackbar.make(view.findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_303),
                                                    Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
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
                                            Snackbar.make(view.findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_304),
                                                    Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
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
                                            Snackbar.make(view.findViewById(android.R.id.content),
                                                    getResources().getString(R.string.E_305),
                                                    Snackbar.LENGTH_LONG);

                                    snack.setAction("CANCEL", new View.OnClickListener() {
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

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmList<RealmMember> members = new RealmList<>();
                        for (long peerId : getSelectedList()) {

                            //add member to realm
                            RealmMember realmMember = new RealmMember();
                            int autoIncrement = 0;
                            if (realm.where(RealmMember.class).max("id") != null) {
                                autoIncrement =
                                        realm.where(RealmMember.class).max("id").intValue() + 1;
                            }
                            realmMember.setId(autoIncrement);
                            realmMember.setPeerId(peerId);
                            realmMember.setRole(ProtoGlobal.GroupRoom.Role.MEMBER.toString());
                            realmMember = realm.copyToRealm(realmMember);

                            members.add(realmMember);

                            //request for add member
                            new RequestGroupAddMember().groupAddMember(roomId, peerId, 0,
                                    ProtoGlobal.GroupRoom.Role.MEMBER);
                        }

                        RealmRoom realmRoom = realm.where(RealmRoom.class)
                                .equalTo(RealmRoomFields.ID, roomId)
                                .findFirst();

                        for (RealmMember member : realmRoom.getGroupRoom().getMembers()) {
                            members.add(member);
                        }

                        realmRoom.getGroupRoom().setMembers(members);
                    }
                });
                realm.close();
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
                    String s = edtSearch.getText()
                            .toString()
                            .substring(sizeTextEditText, charSequence.length());
                    itemAdapter.filter(s);
                } else {
                    itemAdapter.filter("");
                }

                edtSearch.setSelection(edtSearch.getText().length());
                //  fastAdapter.notifyDataSetChanged();

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

    private void refreshView() {

        int selectedNumber = 0;
        textString = "";

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected) {
                selectedNumber++;
                textString += contacts.get(i).displayName + ",";
            }
        }

        txtNumberOfMember.setText(
                selectedNumber + " / " + contacts.size() + getString(R.string.member));
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
