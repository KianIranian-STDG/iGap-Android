package com.iGap.fragments;

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
import com.iGap.interfaces.OnFileDownloadResponse;
import com.iGap.libs.rippleeffect.RippleView;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.RealmAvatar;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.enums.RoomType;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/26/2016.
 */

public class FragmentShowAvatars extends Fragment implements OnFileDownloadResponse {
    private static final String ARG_PEER_ID = "arg_peer_id";
    private long mPeerId = -1;

    private LinearLayout mToolbar;
    private TextView mCount;
    private RecyclerView mRecyclerView;
    private AvatarsAdapter<AvatarItem> mAdapter;

    public static FragmentShowAvatars newInstance(long peerId) {
        Bundle args = new Bundle();
        args.putLong(ARG_PEER_ID, peerId);

        FragmentShowAvatars fragment = new FragmentShowAvatars();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init passed data through bundle
        mPeerId = getArguments().getLong(ARG_PEER_ID, -1);

        // init callbacks
        G.onFileDownloadResponse = this;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_avatars, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // init fields
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mCount = (TextView) view.findViewById(R.id.count);
        mToolbar = (LinearLayout) view.findViewById(R.id.toolbar);
        mAdapter = new AvatarsAdapter<>();

        // ripple back
        ((RippleView) view.findViewById(R.id.back)).setOnRippleCompleteListener(
            new RippleView.OnRippleCompleteListener() {
                @Override public void onComplete(RippleView rippleView) {
                    getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .remove(FragmentShowAvatars.this)
                        .commit();
                }
            });

        // ripple menu
        ((RippleView) view.findViewById(R.id.menu)).setOnRippleCompleteListener(
            new RippleView.OnRippleCompleteListener() {
                @Override public void onComplete(RippleView rippleView) {
                    showPopupMenu();
                }
            });

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo user = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, mPeerId)
            .findFirst();
        if (user != null) {
            // user exists in DB
            final RealmList<RealmAvatar> userAvatars = user.getAvatar();

            long identifier = System.nanoTime();
            for (RealmAvatar avatar : userAvatars) {
                mAdapter.add(
                    new AvatarItem().setAvatar(avatar.getFile()).withIdentifier(identifier));
                identifier++;
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
                    mCount.setText(String.format(getString(R.string.d_of_d),
                        ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition()
                            + 1, mAdapter.getAdapterItemCount()));
                }
            });

            // make RecyclerView snappy
            SnapHelper helper = new LinearSnapHelper();
            helper.attachToRecyclerView(mRecyclerView);

            mCount.setText(
                String.format(getString(R.string.d_of_d), 1, mAdapter.getAdapterItemCount()));
        } else {
            // user doesn't exist in DB
        }
        realm.close();
    }

    private boolean deleteFromGallery(int itemPos) {
        // TODO: 10/26/2016 [Alireza] implement
        return false;
    }

    private void showPopupMenu() {
        MaterialDialog dialog =
            new MaterialDialog.Builder(getActivity()).items(R.array.pop_up_menu_show_image)
                .contentColor(Color.BLACK)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override public void onSelection(MaterialDialog dialog, View view, int which,
                        CharSequence text) {
                        if (which == 0) {
                            showAllMedia();
                        } else if (which == 1) {
                            saveToGallery();
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

    private void showAllMedia() {
        Log.i(FragmentShowAvatars.class.getSimpleName(), "Show all media");
    }

    private void saveToGallery() {
        Log.i(FragmentShowAvatars.class.getSimpleName(), "Save to gallery");
    }

    @Override public void onFileDownload(final String token, final int offset,
        final ProtoFileDownload.FileDownload.Selector selector, final int progress) {
        getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
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

    @Override public void onAvatarDownload(String token, int offset,
        ProtoFileDownload.FileDownload.Selector selector, int progress, long userId,
        RoomType roomType) {
        // empty
    }

    @Override public void onError(int majorCode, int minorCode) {
        if (majorCode == 713 && minorCode == 1) {
            getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack =
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getResources().getString(R.string.E_713_1), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 2) {
            getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack =
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getResources().getString(R.string.E_713_2), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 3) {
            getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack =
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getResources().getString(R.string.E_713_3), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 4) {
            getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack =
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getResources().getString(R.string.E_713_4), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 713 && minorCode == 5) {
            getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack =
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getResources().getString(R.string.E_713_5), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 714) {
            getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack =
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getResources().getString(R.string.E_714), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        } else if (majorCode == 715) {
            getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    final Snackbar snack =
                        Snackbar.make(getActivity().findViewById(android.R.id.content),
                            getResources().getString(R.string.E_715), Snackbar.LENGTH_LONG);

                    snack.setAction("CANCEL", new View.OnClickListener() {
                        @Override public void onClick(View view) {
                            snack.dismiss();
                        }
                    });
                    snack.show();
                }
            });
        }
    }
}
