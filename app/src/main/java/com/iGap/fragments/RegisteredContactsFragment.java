package com.iGap.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activities.ActivityChat;
import com.iGap.adapter.StickyHeaderAdapter;
import com.iGap.adapter.items.ContactItem;
import com.iGap.helper.HelperPermision;
import com.iGap.interfaces.OnChatGetRoom;
import com.iGap.interfaces.OnGetPermission;
import com.iGap.interfaces.OnUserContactGetList;
import com.iGap.interfaces.OnUserContactImport;
import com.iGap.interfaces.OnUserInfoResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.Contacts;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.OnComplete;
import com.iGap.module.SHP_SETTING;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.request.RequestChatGetRoom;
import com.iGap.request.RequestUserContactsGetList;
import com.iGap.request.RequestUserInfo;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import io.realm.Realm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.iGap.G.context;

public class RegisteredContactsFragment extends Fragment {
    private FastAdapter fastAdapter;
    private SearchView searchView;
    private TextView menu_txt_titleToolbar;
    private ViewGroup vgAddContact, vgRoot;
    private List<StructContactInfo> contacts;
    private RecyclerView rv;
    private SharedPreferences sharedPreferences;
    private boolean isImportContactList = false;
    private ProgressBar prgWaiting;
    private ItemAdapter itemAdapter;
    private List<IItem> items;
    public static OnComplete onImportComplete;

    public static RegisteredContactsFragment newInstance() {
        return new RegisteredContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    private void importContactList() {

        G.onContactImport = new OnUserContactImport() {
            @Override
            public void onContactImport() {

                new RequestUserContactsGetList().userContactGetList();
                G.isImportContactToServer = true;
            }
        };

        Contacts.getListOfContact(true);

    }

    @Override
    public void onViewCreated(View view, final @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        isImportContactList = sharedPreferences.getBoolean(SHP_SETTING.KEY_GET_CONTACT_IN_FRAGMENT, false);
        if (!isImportContactList) {
            try {
                HelperPermision.getContactPermision(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() throws IOException {
                        importContactList();
                    }

                    @Override
                    public void deny() {

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(SHP_SETTING.KEY_GET_CONTACT_IN_FRAGMENT, true);
            editor.apply();
        }

        //set interface for get callback here
        prgWaiting = (ProgressBar) view.findViewById(R.id.prgWaiting_addContact);
        prgWaiting.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.toolbar_background), android.graphics.PorterDuff.Mode.MULTIPLY);
        prgWaiting.setVisibility(View.GONE);
        vgAddContact = (ViewGroup) view.findViewById(R.id.menu_layout_addContact);
        vgRoot = (ViewGroup) view.findViewById(R.id.menu_parent_layout);


        Bundle bundle = this.getArguments();
        String title = null;
        if (bundle != null) {
            title = bundle.getString("TITLE");
        }
        //create our FastAdapter
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContactItem>() {
            @Override
            public boolean filter(ContactItem item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactItem>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, ContactItem item, int position) {

                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                prgWaiting.setVisibility(View.VISIBLE);
                chatGetRoom(item.mContact.peerId);
                return false;
            }
        });
        menu_txt_titleToolbar = (TextView) view.findViewById(R.id.menu_txt_titleToolbar);
        menu_txt_titleToolbar.setText(title);

        searchView = (android.support.v7.widget.SearchView) view.findViewById(R.id.menu_edtSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemAdapter.filter(newText);
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && menu_txt_titleToolbar.getVisibility() == View.VISIBLE) {
                    menu_txt_titleToolbar.setVisibility(View.GONE);
                }
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                menu_txt_titleToolbar.setVisibility(View.VISIBLE);
                return false;
            }
        });

        final EditText searchBox = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchBox.setTextColor(getResources().getColor(R.color.white));


        vgAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentAddContact fragment = FragmentAddContact.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "add_contact");
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).add(R.id.fragmentContainer, fragment).commit();

            }
        });

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) view.findViewById(R.id.menu_txtBack);
        RippleView rippleMenu = (RippleView) view.findViewById(R.id.menu_ripple_txtBack);
        rippleMenu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                // close and remove fragment from stack
                if (!searchView.isIconified()) {
                    searchView.onActionViewCollapsed();
                }
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        rv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        //this adds the Sticky Headers within our list
        final StickyRecyclerHeadersDecoration decoration = new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        rv.addItemDecoration(decoration);


        items = new ArrayList<>();
        contacts = Contacts.retrieve(null);
        G.onUserContactGetList = new OnUserContactGetList() {
            @Override
            public void onContactGetList() {
                //                if (contacts.size() == 0) {
                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contacts.clear();
                        itemAdapter.clear();
                        items.clear();

                        contacts = Contacts.retrieve(null);
                        if (contacts != null && fastAdapter != null && itemAdapter != null) {
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
                            fastAdapter.notifyDataSetChanged();
                        }


                    }
                });
            }
            //            }
        };
        if (contacts.size() == 0) {
            /**
             * if contacts size is zero send request for get contacts list
             * for insuring that contacts not exist really or not
             */
            new RequestUserContactsGetList().userContactGetList();
        } else {
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

        onImportComplete = new OnComplete() {
            @Override
            public void complete(boolean result, String messageOne, String MessageTow) {
                new RequestUserContactsGetList().userContactGetList();
            }
        };


    }

    private void chatGetRoom(final long peerId) {

        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.CHAT_ROOM.PEER_ID, peerId).findFirst();

        if (realmRoom != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            prgWaiting.setVisibility(View.GONE);
            Intent intent = new Intent(context, ActivityChat.class);
            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            getActivity().getSupportFragmentManager().beginTransaction().remove(RegisteredContactsFragment.this).commit();
            //            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final long roomId) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            prgWaiting.setVisibility(View.GONE);
                        }
                    });
                    getUserInfo(peerId, roomId);
                }

                @Override
                public void onChatGetRoomCompletely(ProtoGlobal.Room room) {

                }

                @Override
                public void onChatGetRoomTimeOut() {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            prgWaiting.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onChatGetRoomError(int majorCode, int minorCode) {
                    G.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            prgWaiting.setVisibility(View.GONE);
                        }
                    });
                }

            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
        realm.close();
    }

    private void getUserInfo(final long peerId, final long roomId) {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, String identity) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (user.getId() == peerId) {
                            Realm realm = Realm.getDefaultInstance();

                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, user.getId()).findFirst();
                                    if (realmRegisteredInfo == null) {
                                        realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class);
                                        realmRegisteredInfo.setId(user.getId());
                                        realmRegisteredInfo.setDoNotshowSpamBar(false);
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
                                @Override
                                public void onSuccess() {
                                    try {
                                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        prgWaiting.setVisibility(View.GONE);
                                        Intent intent = new Intent(context, ActivityChat.class);
                                        intent.putExtra("peerId", peerId);
                                        intent.putExtra("RoomId", roomId);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            realm.close();
                        }

                    }
                });

            }

            @Override
            public void onUserInfoTimeOut() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        prgWaiting.setVisibility(View.GONE);

                    }
                });

            }

            @Override
            public void onUserInfoError(int majorCode, int minorCode) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        prgWaiting.setVisibility(View.GONE);
                    }
                });

            }
        };

        new RequestUserInfo().userInfo(peerId);
    }

    public void updateChatAvatar(long userId) {
        int position = getPosition(contacts, userId);
        if (position != -1) {
            fastAdapter.notifyAdapterItemChanged(position);
        }
    }

    private int getPosition(List<StructContactInfo> structContactInfos, long userId) {

        for (int i = 0; i < structContactInfos.size(); i++) {
            if (structContactInfos.get(i).peerId == userId) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        if (fastAdapter != null) {
            outState = fastAdapter.saveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }



}
