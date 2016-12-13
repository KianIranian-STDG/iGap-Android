package com.iGap.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.adapter.AvatarsAdapter;
import com.iGap.adapter.items.AvatarItem;
import com.iGap.helper.HelperAvatar;
import com.iGap.helper.HelperSaveFile;
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.interfaces.OnUserAvatarDelete;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.OnComplete;
import com.iGap.module.SUID;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmAvatarFields;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.enums.RoomType;
import com.iGap.request.RequestUserAvatarDelete;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/26/2016.
 */

public class FragmentShowAvatars extends Fragment implements OnFileDownloadResponse {
    private static final String ARG_PEER_ID = "arg_peer_id";
    private static final String ARG_Type = "arg_type";

    private long mPeerId = -1;
    private int curerntItemPosition = 0;

    private LinearLayout mToolbar;
    private TextView mCount;
    private RecyclerView mRecyclerView;
    private AvatarsAdapter<AvatarItem> mAdapter;

    public static OnComplete onComplete;

    public static View appBarLayout;

    private int type;
    private final int SETTING = 0;
    private final int CONTACT = 1;
    private final int GROUP = 2;
    private long userId;


    From from = From.chat;

    public static final int mChatNumber = 1;
    public static final int mGroupNumber = 2;
    public static final int mChannelNumber = 3;
    public static final int mSettingNumber = 4;


    public enum From {
        chat(mChatNumber),
        group(mGroupNumber),
        channel(mChannelNumber),
        setting(mSettingNumber);

        public int value;

        From(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static FragmentShowAvatars newInstance(long peerId, From from) {
        Bundle args = new Bundle();
        args.putLong(ARG_PEER_ID, peerId);
        args.putSerializable(ARG_Type, from);


        FragmentShowAvatars fragment = new FragmentShowAvatars();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDetach() {
        if (appBarLayout != null)
            appBarLayout.setVisibility(View.VISIBLE);

        if (onComplete != null)
            onComplete.complete(true, "", "");

        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        if (appBarLayout != null)
            appBarLayout.setVisibility(View.GONE);

        super.onAttach(context);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init passed data through bundle
        mPeerId = getArguments().getLong(ARG_PEER_ID, -1);

        From result = (From) getArguments().getSerializable(ARG_Type);

        if (result != null)
            from = result;


        // init callbacks
        G.onFileDownloadResponse = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_avatars, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init fields
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mCount = (TextView) view.findViewById(R.id.count);
        mToolbar = (LinearLayout) view.findViewById(R.id.toolbar);
        mAdapter = new AvatarsAdapter<>();

        // ripple back
        ((RippleView) view.findViewById(R.id.back)).setOnRippleCompleteListener(
                new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .remove(FragmentShowAvatars.this)
                                .commit();
                        appBarLayout.setVisibility(View.VISIBLE);
                        if (onComplete != null)
                            onComplete.complete(true, "", "");
                    }
                });

        // ripple menu
        ((RippleView) view.findViewById(R.id.menu)).setOnRippleCompleteListener(
                new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {

                        switch (from) {
                            case setting:
                                showPopupMenu(R.array.pop_up_menu_show_avatar_setting);
                                break;
                            case group:
                                showPopupMenu(R.array.pop_up_menu_show_avatar);
                                break;
                            case channel:
                                showPopupMenu(R.array.pop_up_menu_show_avatar);
                                break;
                            case chat:
                                showPopupMenu(R.array.pop_up_menu_show_avatar_setting);
                                break;
                        }

                    }
                });


        if (from == From.chat) {
            fillListChatAvatar();
        } else if (from == From.group) {
            fillListGroupAvatar();
        }


        if (mAdapter.getAdapterItemCount() > 0) {

            mRecyclerView.setItemAnimator(null);
            // following lines make scrolling smoother
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemViewCacheSize(20);
            mRecyclerView.setDrawingCacheEnabled(true);
            LinearLayoutManager layoutManager =
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mAdapter);

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    curerntItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    mCount.setText(String.format(getString(R.string.d_of_d), curerntItemPosition + 1, mAdapter.getAdapterItemCount()));
                }
            });

            // make RecyclerView snappy
            SnapHelper helper = new LinearSnapHelper();
            helper.attachToRecyclerView(mRecyclerView);

            mCount.setText(String.format(getString(R.string.d_of_d), 1, mAdapter.getAdapterItemCount()));
        } else {
            // no avatar exist
        }

    }

    private void fillListChatAvatar() {


        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo user = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, mPeerId).findFirst();
        if (user != null) {
            // user exists in DB
            final RealmList<RealmAvatar> userAvatars = user.getAvatars();


            long identifier = SUID.id().get();
            for (RealmAvatar avatar : userAvatars) {

                mAdapter.add(new AvatarItem().setAvatar(avatar.getFile(), avatar.getId()).withIdentifier(identifier));
                identifier++;
            }
        }

        realm.close();
    }

    private void fillListGroupAvatar() {

        Realm realm = Realm.getDefaultInstance();
        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, mPeerId).findFirst();
        if (room != null) {
            // user exists in DB

            RealmResults<RealmAvatar> userAvatars = realm.where(RealmAvatar.class).equalTo(RealmAvatarFields.OWNER_ID, mPeerId).findAll();

            long identifier = SUID.id().get();
            for (RealmAvatar avatar : userAvatars) {
                mAdapter.add(new AvatarItem().setAvatar(avatar.getFile(), avatar.getId()).withIdentifier(identifier));
                identifier++;
            }
        }

        realm.close();
    }

    private boolean deleteFromGallery(int itemPos) {
        // TODO: 10/26/2016 [Alireza] implement
        return false;
    }

    private void showPopupMenu(int r) {
        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity()).items(r)
                        .contentColor(Color.BLACK)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which,
                                                    CharSequence text) {
                                if (which == 0) {
                                    saveToGallery();
                                } else if (which == 1) {
//
                                    deletePhoto();

                                }
                                // TODO: 10/26/2016 [Alireza] implement delete
                        /*else if (which == 2) {
                            int pos = mRecyclerView.getCurrentItem();
                            if (deleteFromGallery(pos)) {
                                if (mAdapter.getAdapterItemCount() == 0) {
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(FragmentShowAvatars.this).commit();
                                    ((ActivitySetting) getActivity()).setAvatar();
                                }
                            }
                        }*/
                            }
                        }).show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp200);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void deletePhoto() {


        G.onUserAvatarDelete = new OnUserAvatarDelete() {
            @Override
            public void onUserAvatarDelete(final long avatarId, final String token) {

                G.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.remove(curerntItemPosition);
                        mCount.setText(String.format(getString(R.string.d_of_d), curerntItemPosition, mAdapter.getAdapterItemCount()));
                        HelperAvatar.avatarDelete(mPeerId, avatarId, HelperAvatar.AvatarType.USER, null);
                    }
                });
            }

            @Override
            public void onUserAvatarDeleteError() {

            }
        };
//        RealmAvatar realmAvatar = HelperAvatar.getLastAvatar(userId);

        new RequestUserAvatarDelete().userAvatarDelete(mAdapter.getAdapterItems().get(curerntItemPosition).imageId);

    }

    private void showAllMedia() {
        Log.i(FragmentShowAvatars.class.getSimpleName(), "Show all media");
    }

    private void saveToGallery() {

        if (mAdapter.getItem(curerntItemPosition) != null) {
            String media = mAdapter.getItem(curerntItemPosition).avatar.getLocalFilePath();
            if (media != null) {
                File file = new File(media);
                if (file.exists()) {
                    HelperSaveFile.savePicToGallary(media);
                }
            }
        }
    }

    @Override
    public void onFileDownload(final String token, final long offset,
                               final ProtoFileDownload.FileDownload.Selector selector, final int progress) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selector != ProtoFileDownload.FileDownload.Selector.FILE) {
                    // requested thumbnail
                    mAdapter.downloadingAvatarThumbnail(token);
                } else {
                    // requested file
                    mAdapter.downloadingAvatarFile(token, progress, offset);
                }
            }
        });
    }

    @Override
    public void onAvatarDownload(String token, long offset,
                                 ProtoFileDownload.FileDownload.Selector selector, int progress, long userId,
                                 RoomType roomType) {
        // empty
    }

    @Override
    public void onError(int majorCode, int minorCode) {
        if (majorCode == 713 && minorCode == 1) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_1), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 2) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_2), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 3) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_3), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 4) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_4), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 5) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_713_5), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 714) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_714), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 715) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Snackbar snack =
                            Snackbar.make(getActivity().findViewById(android.R.id.content),
                                    getResources().getString(R.string.E_715), Snackbar.LENGTH_LONG);

                    snack.setAction(R.string.cancel, new View.OnClickListener() {
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

    @Override
    public void onBadDownload(String token) {
        // empty
    }
}
