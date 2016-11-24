package com.iGap.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
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
import com.iGap.activities.ActivityShearedMedia;
import com.iGap.adapter.ImageMessagesAdapter;
import com.iGap.adapter.items.ImageMessageItem;
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.module.SUID;
import com.iGap.module.TimeUtils;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.enums.RoomType;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.iGap.R.id.recyclerView;
import static com.iGap.module.MusicPlayer.roomId;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/26/2016.
 */

public class FragmentShowImageMessages extends Fragment implements OnFileDownloadResponse {
    private static final String ARG_ROOM_ID = "arg_room_id";
    private static final String ARG_SELECTED_TOKEN = "arg_selected_token";
    private long mRoomId = -1;
    private String mSelectedToken;

    private LinearLayout mToolbar;
    private TextView mCount;
    private TextView mFileName;
    private TextView mMessageTime;
    private RecyclerView mRecyclerView;
    private ImageMessagesAdapter<ImageMessageItem> mAdapter;

    public static View appBarLayout;

    public static FragmentShowImageMessages newInstance(long roomId, String selectedToken) {
        Bundle args = new Bundle();
        args.putLong(ARG_ROOM_ID, roomId);
        args.putString(ARG_SELECTED_TOKEN, selectedToken);

        FragmentShowImageMessages fragment = new FragmentShowImageMessages();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDetach() {
        if (appBarLayout != null)
            appBarLayout.setVisibility(View.VISIBLE);

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
        mRoomId = getArguments().getLong(ARG_ROOM_ID, -1);
        mSelectedToken = getArguments().getString(ARG_SELECTED_TOKEN);

        // init callbacks
        G.onFileDownloadResponse = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_image_messages, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init fields
        mRecyclerView = (RecyclerView) view.findViewById(recyclerView);
        mCount = (TextView) view.findViewById(R.id.count);
        mFileName = (TextView) view.findViewById(R.id.fileName);
        mMessageTime = (TextView) view.findViewById(R.id.messageTime);
        mToolbar = (LinearLayout) view.findViewById(R.id.toolbar);
        mAdapter = new ImageMessagesAdapter<>();

        // ripple back
        ((RippleView) view.findViewById(R.id.back)).setOnRippleCompleteListener(
                new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        getActivity().onBackPressed();
                    }
                });

        // ripple menu
        ((RippleView) view.findViewById(R.id.menu)).setOnRippleCompleteListener(
                new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        showPopupMenu();
                    }
                });

        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmRoomMessage> roomMessages =
                realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.ROOM_ID, mRoomId).findAll();
        if (!roomMessages.isEmpty()) {
            // there is at least on history in DB
            long identifier = SUID.id().get();
            for (RealmRoomMessage roomMessage : roomMessages) {
                ProtoGlobal.RoomMessageType messageType =
                        roomMessage.getMessageType();
                if (messageType == ProtoGlobal.RoomMessageType.IMAGE
                        || messageType == ProtoGlobal.RoomMessageType.IMAGE_TEXT) {
                    mAdapter.add(new ImageMessageItem().setMessage(roomMessage)
                            .withIdentifier(identifier));
                    identifier++;
                }
            }

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

                    int currentlyViewedPos =
                            ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    ImageMessageItem item = mAdapter.getAdapterItem(currentlyViewedPos);
                    mCount.setText(String.format(getString(R.string.d_of_d), currentlyViewedPos + 1,
                            mAdapter.getAdapterItemCount()));
                    mFileName.setText(item.message.getAttachment().getName());
                    mMessageTime.setText(TimeUtils.getChatSettingsTimeAgo(getContext(),
                            new Date(item.message.getUpdateTime())));
                }
            });

            // make RecyclerView snappy
            SnapHelper helper = new LinearSnapHelper();
            helper.attachToRecyclerView(mRecyclerView);

            ImageMessageItem selectedItem = findByToken(mSelectedToken);

            mFileName.setText(selectedItem.message.getAttachment().getName());
            mMessageTime.setText(TimeUtils.getChatSettingsTimeAgo(getContext(),
                    new Date(selectedItem.message.getUpdateTime())));

            preSelect();
        } else {
            // there's no message for this room in DB
        }
        realm.close();
    }

    private void preSelect() {
        for (ImageMessageItem item : mAdapter.getAdapterItems()) {

            try {
                if (item.message.getAttachment().getToken().equalsIgnoreCase(mSelectedToken)) {
                    int position = mAdapter.getPosition(item);
                    mRecyclerView.scrollToPosition(position);

                    mCount.setText(String.format(getString(R.string.d_of_d), position + 1, mAdapter.getAdapterItemCount()));

                    break;
                }
            } catch (NullPointerException e) {

            }

        }
    }

    private boolean deleteFromGallery(int itemPos) {
        // TODO: 10/26/2016 [Alireza] implement
        return false;
    }

    private ImageMessageItem findByToken(String token) {
        for (ImageMessageItem item : mAdapter.getAdapterItems()) {
            try {
                if (item.message.getAttachment().getToken().equalsIgnoreCase(token)) {
                    return item;
                }
            } catch (NullPointerException e) {

            }

        }
        return null;
    }

    private void showPopupMenu() {
        MaterialDialog dialog =
                new MaterialDialog.Builder(getActivity())
                        .items(R.array.pop_up_menu_show_image)
                        .contentColor(Color.BLACK)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which,
                                                    CharSequence text) {
                                if (which == 0) {
                                    Intent intent = new Intent(getActivity(), ActivityShearedMedia.class);
                                    intent.putExtra("RoomID", roomId);
                                    startActivity(intent);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                } else if (which == 1) {
//                                    HelperSaveFile.savePicToGallary();
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
                        })
                        .show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) getResources().getDimension(R.dimen.dp200);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void showAllMedia() {

    }

    private void saveToGallery() {

    }

    @Override
    public void onFileDownload(final String token, final long offset,
                               final ProtoFileDownload.FileDownload.Selector selector, final int progress) {
        if (isVisible()) {
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
}
