package com.iGap.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.StickyHeaderAdapter;
import com.iGap.adapter.items.ContactItemGroupProfile;
import com.iGap.interfaces.OnGroupKickAdmin;
import com.iGap.interfaces.OnGroupKickModerator;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.MaterialDesignTextView;
import com.iGap.module.StructContactInfo;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestGroupKickAdmin;
import com.iGap.request.RequestGroupKickModerator;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.HeaderAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import static com.iGap.module.MusicPlayer.roomId;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentListAdmin extends Fragment {


    private static List<StructContactInfo> contacts;

    private FastAdapter fastAdapter;
    private TextView txtStatus;
    private TextView txtNumberOfMember;
    private EditText edtSearch;
    private String textString = "";
    private String type;
    private long roomid;
    private ItemAdapter itemAdapter;

    public static FragmentListAdmin newInstance(List<StructContactInfo> list) {

        contacts = list;

        for (int i = 0; i < contacts.size(); i++) {
            contacts.get(i).isSelected = false;
        }

        return new FragmentListAdmin();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            type = bundle.getString("TYPE");
            roomid = bundle.getLong("ID");
        }

        txtStatus = (TextView) view.findViewById(R.id.fcg_txt_status);
        txtNumberOfMember = (TextView) view.findViewById(R.id.fcg_txt_number_of_member);
        edtSearch = (EditText) view.findViewById(R.id.fcg_edt_search);
        edtSearch = (EditText) view.findViewById(R.id.fcg_edt_search);
        edtSearch.setVisibility(View.GONE);
        MaterialDesignTextView btnBack = (MaterialDesignTextView) view.findViewById(R.id.fcg_btn_back);
        RippleView rippleBack = (RippleView) view.findViewById(R.id.fcg_ripple_back);

        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        MaterialDesignTextView txtDone = (MaterialDesignTextView) view.findViewById(R.id.fcg_btn_done);
        txtDone.setVisibility(View.GONE);


        //create our FastAdapter
        fastAdapter = new FastAdapter();
        fastAdapter.withSelectable(true);

        //create our adapters
        final StickyHeaderAdapter stickyHeaderAdapter = new StickyHeaderAdapter();
        final HeaderAdapter headerAdapter = new HeaderAdapter();
        itemAdapter = new ItemAdapter();

        fastAdapter.withOnClickListener(new FastAdapter.OnClickListener() {
            @Override
            public boolean onClick(View v, IAdapter adapter, final IItem item, final int position) {

                final ContactItemGroupProfile contactItemGroupProfile = (ContactItemGroupProfile) item;

                if (type.equals("ADMIN")) {
                    new MaterialDialog.Builder(getActivity())
                            .content(R.string.text_kick_admin)
                            .positiveText(R.string.B_ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    G.onGroupKickAdmin = new OnGroupKickAdmin() {
                                        @Override
                                        public void onGroupKickAdmin(long roomId, long memberId) {
                                            updateRole(memberId, ProtoGlobal.GroupRoom.Role.ADMIN, position);
                                            G.updateListAfterKick.updateList(memberId, ProtoGlobal.GroupRoom.Role.MEMBER);
                                        }

                                        @Override
                                        public void onError(int majorCode, final int minorCode) {
                                            if (majorCode == 327) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (minorCode == 1) {

                                                            final Snackbar snack =
                                                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                            getResources().getString(R.string.E_327_A),
                                                                            Snackbar.LENGTH_LONG);

                                                            snack.setAction(getResources().getString(R.string.cancel), new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    snack.dismiss();
                                                                }
                                                            });
                                                            snack.show();
                                                        } else {

                                                            final Snackbar snack =
                                                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                            getResources().getString(R.string.E_327_B),
                                                                            Snackbar.LENGTH_LONG);

                                                            snack.setAction(getResources().getString(R.string.cancel), new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    snack.dismiss();
                                                                }
                                                            });
                                                            snack.show();
                                                        }
                                                    }
                                                });
                                            } else if (majorCode == 328) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Snackbar snack =
                                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                        getResources().getString(R.string.E_328),
                                                                        Snackbar.LENGTH_LONG);

                                                        snack.setAction(getResources().getString(R.string.cancel), new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                snack.dismiss();
                                                            }
                                                        });
                                                        snack.show();
                                                    }
                                                });
                                            } else if (majorCode == 329) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Snackbar snack =
                                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                        getResources().getString(R.string.E_329),
                                                                        Snackbar.LENGTH_LONG);

                                                        snack.setAction(getResources().getString(R.string.cancel), new View.OnClickListener() {
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

                                    new RequestGroupKickAdmin().groupKickAdmin(roomid, contactItemGroupProfile.mContact.peerId);
                                }
                            })
                            .negativeText(R.string.B_cancel)
                            .show();
                } else {

                    new MaterialDialog.Builder(getActivity())
                            .content(R.string.text_kick_moderator)
                            .positiveText(R.string.B_ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                    G.onGroupKickModerator = new OnGroupKickModerator() {
                                        @Override
                                        public void onGroupKickModerator(long roomId, long memberId) {

                                            updateRole(memberId, ProtoGlobal.GroupRoom.Role.MODERATOR, position);
                                            G.updateListAfterKick.updateList(memberId, ProtoGlobal.GroupRoom.Role.MEMBER);

                                        }

                                        @Override
                                        public void onError(int majorCode, final int minorCode) {
                                            if (majorCode == 324) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        if (minorCode == 1) {
                                                            final Snackbar snack =
                                                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                            getResources().getString(R.string.E_324_1),
                                                                            Snackbar.LENGTH_LONG);

                                                            snack.setAction(getResources().getString(R.string.cancel), new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    snack.dismiss();
                                                                }
                                                            });
                                                            snack.show();
                                                        } else {
                                                            final Snackbar snack =
                                                                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                            getResources().getString(R.string.E_324_2),
                                                                            Snackbar.LENGTH_LONG);

                                                            snack.setAction(getResources().getString(R.string.cancel), new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    snack.dismiss();
                                                                }
                                                            });
                                                            snack.show();
                                                        }
                                                    }
                                                });
                                            } else if (majorCode == 325) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Snackbar snack =
                                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                        getResources().getString(R.string.E_325),
                                                                        Snackbar.LENGTH_LONG);

                                                        snack.setAction(getResources().getString(R.string.cancel), new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                snack.dismiss();
                                                            }
                                                        });
                                                        snack.show();
                                                    }
                                                });
                                            } else if (majorCode == 326) {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        final Snackbar snack =
                                                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                                                        getResources().getString(R.string.E_326),
                                                                        Snackbar.LENGTH_LONG);

                                                        snack.setAction(getResources().getString(R.string.cancel), new View.OnClickListener() {
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


                                    new RequestGroupKickModerator().groupKickModerator(roomId, contactItemGroupProfile.mContact.peerId);
                                }
                            })
                            .negativeText(R.string.B_cancel)
                            .show();
                }
                return false;
            }
        });

        //configure our fastAdapter
        //as we provide id's for the items we want the hasStableIds enabled to speed up things
        fastAdapter.setHasStableIds(true);

        //get our recyclerView and do basic setup
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.fcg_recycler_view_add_item_to_group);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(stickyHeaderAdapter.wrap(itemAdapter.wrap(headerAdapter.wrap(fastAdapter))));

        //this adds the Sticky Headers within our list
        final StickyRecyclerHeadersDecoration decoration =
                new StickyRecyclerHeadersDecoration(stickyHeaderAdapter);
        rv.addItemDecoration(decoration);

        ContactItemGroupProfile.mainRole = ProtoGlobal.GroupRoom.Role.OWNER.toString();

        List<IItem> items = new ArrayList<>();
        for (int i = 0; i < contacts.size(); i++) {


            if (type.equals("ADMIN")) {
                if (contacts.get(i).role.equals(ProtoGlobal.GroupRoom.Role.ADMIN.toString())) {

                    IItem item = (new ContactItemGroupProfile().setContact(contacts.get(i))).withIdentifier(100 + contacts.indexOf(contacts.get(i)));

                    items.add(item);

                    Log.i("GGGGGGG", "getSelectedList2: ");
                }
            } else {
                if (contacts.get(i).role.equals(ProtoGlobal.GroupRoom.Role.MODERATOR.toString())) {

                    IItem item = (new ContactItemGroupProfile().setContact(contacts.get(i))).withIdentifier(100 + contacts.indexOf(contacts.get(i)));
                    items.add(item);

                    Log.i("GGGGGGG", "getSelectedList2: ");
                }
            }


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

        refreshView();
    }


    private void refreshView() {

        int selectedNumber = 0;
        textString = "";

        int size = contacts.size();

        for (int i = 0; i < size; i++) {
            if (contacts.get(i).isSelected) {
                selectedNumber++;
                textString += contacts.get(i).displayName + ",";
            }
        }

        if (type.equals("ADMIN")) {

            txtNumberOfMember.setText(getResources().getString(R.string.list_admin));
        } else {
            txtNumberOfMember.setText(getResources().getString(R.string.list_modereator));
        }

//        edtSearch.setText(textString);
    }

    private ArrayList<StructContactInfo> getSelectedList() {

        ArrayList<StructContactInfo> list = new ArrayList<>();

        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).isSelected) {
                list.add(contacts.get(i));
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

    private void updateRole(final long memberId, final ProtoGlobal.GroupRoom.Role role, final int po) {

        getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            List<ContactItemGroupProfile> items = itemAdapter.getAdapterItems();
                                            for (int i = 0; i < items.size(); i++) {
                                                if (items.get(i).mContact.peerId == memberId) {
                                                    itemAdapter.remove(po);
                                                    if (items.size() == 0) {
                                                        getActivity().getSupportFragmentManager().popBackStack();
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
        );
    }
}
