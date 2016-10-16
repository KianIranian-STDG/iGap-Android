package com.iGap.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityChat;
import com.iGap.adapter.StickyHeaderAdapter;
import com.iGap.adapter.items.ContactItem;
import com.iGap.interface_package.OnChatGetRoom;
import com.iGap.interface_package.OnUserInfoResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.Contacts;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRoom;
import com.iGap.request.RequestChatGetRoom;
import com.iGap.request.RequestUserInfo;
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

public class RegisteredContactsFragment extends Fragment {
    private FastAdapter fastAdapter;
    private SearchView searchView;
    private TextView menu_txt_titleToolbar;
    private ViewGroup vgAddContact;

    public static RegisteredContactsFragment newInstance() {
        return new RegisteredContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        final ItemAdapter itemAdapter = new ItemAdapter();
        itemAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContactItem>() {
            @Override
            public boolean filter(ContactItem item, CharSequence constraint) {
                return !item.mContact.displayName.toLowerCase().startsWith(String.valueOf(constraint).toLowerCase());
            }
        });
        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactItem>() {
            @Override
            public boolean onClick(View v, IAdapter adapter, ContactItem item, int position) {
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
                    Log.i("AASSAA", "0: ");
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
//        ViewGroup root = (ViewGroup) view.findViewById(R.id.menu_parent_layout);
//        InputMethodManager im = (InputMethodManager) G.context.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        final SoftKeyboard softKeyboard = new SoftKeyboard(root, im);
//        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
//            @Override
//            public void onSoftKeyboardHide() {
//                G.handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (searchView.getQuery().toString().length() > 0) {
//                            searchView.setIconified(false);
//                            menu_txt_titleToolbar.setVisibility(View.GONE);
//                            Log.i("AASSAA", "3: "+menu_txt_titleToolbar );
//                        } else {
//                            searchView.setIconified(true);
//                            menu_txt_titleToolbar.setVisibility(View.VISIBLE);
//                            Log.i("AASSAA", "4: "+menu_txt_titleToolbar );
//                        }
//                    }
//                });
//            }
//            @Override
//            public void onSoftKeyboardShow() {
//                G.handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        menu_txt_titleToolbar.setVisibility(View.GONE);
//                        Log.i("AASSAA", "5: "+menu_txt_titleToolbar );
//                    }
//                });
//            }
//        });

        final EditText searchBox = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchBox.setTextColor(getResources().getColor(R.color.white));

        vgAddContact = (ViewGroup) view.findViewById(R.id.menu_layout_addContact);
        vgAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentAddContact fragment = FragmentAddContact.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "add_contact");
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left).addToBackStack(null).replace(R.id.fragmentContainer, fragment).commit();
            }
        });

        TextView txtMenu = (TextView) view.findViewById(R.id.menu_txtBack);
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
        txtMenu.setTypeface(G.fontawesome);
//        txtMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // close and remove fragment from stack
//                if (!searchView.isIconified()) {
//                    searchView.onActionViewCollapsed();
//                }
//                getActivity().getSupportFragmentManager().popBackStack();
//            }
//        });
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
        List<StructContactInfo> contacts = Contacts.retrieve(null);
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

        final Realm realm = Realm.getDefaultInstance();
        final RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo("chat_room.peer_id", peerId).findFirst();

        if (realmRoom != null) {
            Intent intent = new Intent(G.context, ActivityChat.class);
            intent.putExtra("RoomId", realmRoom.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            G.context.startActivity(intent);
            getActivity().getSupportFragmentManager().popBackStack();

        } else {
            G.onChatGetRoom = new OnChatGetRoom() {
                @Override
                public void onChatGetRoom(final long roomId) {
                    getUserInfo(peerId, roomId);
                }
            };

            new RequestChatGetRoom().chatGetRoom(peerId);
        }
        realm.close();
    }

    private void getUserInfo(final long peerId, final long roomId) {

        G.onUserInfoResponse = new OnUserInfoResponse() {
            @Override
            public void onUserInfo(final ProtoGlobal.RegisteredUser user, ProtoResponse.Response response) {

                G.currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (user.getId() == peerId) {
                            Realm realm = Realm.getDefaultInstance();

                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo("id", user.getId()).findFirst();
                                    if (realmRegisteredInfo == null) {
                                        realmRegisteredInfo = realm.createObject(RealmRegisteredInfo.class);
                                        realmRegisteredInfo.setId(user.getId());
                                    }

                                    realmRegisteredInfo.setUsername(user.getUsername());
                                    realmRegisteredInfo.setPhone(user.getPhone());
                                    realmRegisteredInfo.setFirstName(user.getFirstName());
                                    realmRegisteredInfo.setLastName(user.getLastName());
                                    realmRegisteredInfo.setDisplayName(user.getDisplayName());
                                    realmRegisteredInfo.setInitials(user.getInitials());
                                    realmRegisteredInfo.setColor(user.getColor());
                                    realmRegisteredInfo.setStatus(user.getStatus().toString());
                                    realmRegisteredInfo.setAvatarCount(user.getAvatarCount());

                                    RealmList<RealmAvatar> avatars = new RealmList<>();
                                    avatars.add(RealmAvatar.convert(user, realm));
                                    realmRegisteredInfo.setAvatar(avatars);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    Intent intent = new Intent(G.context, ActivityChat.class);
                                    intent.putExtra("peerId", peerId);
                                    intent.putExtra("RoomId", roomId);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    G.context.startActivity(intent);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            });

                            realm.close();
                        }
                    }
                });
            }
        };

        new RequestUserInfo().userInfo(peerId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the adapter to the bundle
        outState = fastAdapter.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

}
