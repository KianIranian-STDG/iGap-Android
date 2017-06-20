package net.iGap.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;
import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityCall;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperAvatar;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperError;
import net.iGap.interfaces.ISignalingGetCallLog;
import net.iGap.interfaces.OnAvatarGet;
import net.iGap.libs.rippleeffect.RippleView;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.DialogAnimation;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.TimeUtils;
import net.iGap.proto.ProtoSignalingGetLog;
import net.iGap.realm.RealmCallConfig;
import net.iGap.realm.RealmCallLog;
import net.iGap.realm.RealmCallLogFields;
import net.iGap.request.RequestSignalingClearLog;
import net.iGap.request.RequestSignalingGetConfiguration;
import net.iGap.request.RequestSignalingGetLog;

public class FragmentCall extends Fragment {

    public static final String strGonTitle = "strGonTitle";
    boolean goneTitle = false;

    private int mOffset = 0;
    private int mLimit = 50;
    private RecyclerView.OnScrollListener onScrollListener;
    boolean isSendRequestForLoading = false;
    boolean isThereAnyMoreItemToLoad = true;
    private AppCompatImageView imgCallEmpty;
    private FragmentActivity mActivity;
    private TextView empty_call;
    ProgressBar progressBar;
    private int attampOnError = 0;
    boolean canclick = false;
    int move = 0;
    public FloatingActionButton fabContactList;


    private RealmRecyclerView mRecyclerView;

    public static FragmentCall newInstance(boolean goneTitle) {

        FragmentCall fragmentCall = new FragmentCall();

        Bundle bundle = new Bundle();
        bundle.putBoolean(strGonTitle, goneTitle);
        fragmentCall.setArguments(bundle);

        return fragmentCall;
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_call, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goneTitle = getArguments().getBoolean(strGonTitle);

        view.findViewById(R.id.fc_layot_title).setBackgroundColor(Color.parseColor(G.appBarColor));  //set title bar color


        imgCallEmpty = (AppCompatImageView) view.findViewById(R.id.img_icCall);
        empty_call = (TextView) view.findViewById(R.id.textEmptyCal);
        progressBar = (ProgressBar) view.findViewById(R.id.fc_progress_bar_waiting);
        AppUtils.setProgresColler(progressBar);

        RippleView rippleBack = (RippleView) view.findViewById(R.id.fc_call_ripple_txtBack);
        rippleBack.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override public void onComplete(RippleView rippleView) {
                mActivity.getSupportFragmentManager().popBackStack();
            }
        });

        MaterialDesignTextView txtMenu = (MaterialDesignTextView) view.findViewById(R.id.fc_btn_menu);

        txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openDialogMenu();
            }
        });


        mRecyclerView = (RealmRecyclerView) view.findViewById(R.id.fc_recycler_view_call);
        mRecyclerView.setItemViewCacheSize(500);
        mRecyclerView.setDrawingCacheEnabled(true);

        Realm realm = Realm.getDefaultInstance();

        RealmResults<RealmCallLog> results = realm.where(RealmCallLog.class).findAllSorted(RealmCallLogFields.TIME, Sort.DESCENDING);

        if (results.size() > 0) {
            imgCallEmpty.setVisibility(View.GONE);
            empty_call.setVisibility(View.GONE);

        } else {
            imgCallEmpty.setVisibility(View.VISIBLE);
            empty_call.setVisibility(View.VISIBLE);
        }

        CallAdapter callAdapter = new CallAdapter(mActivity, results);
        mRecyclerView.setAdapter(callAdapter);

        onScrollListener = new RecyclerView.OnScrollListener() {

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isThereAnyMoreItemToLoad) {
                    if (!isSendRequestForLoading) {

                        int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                        if (lastVisiblePosition + 10 >= mOffset) {
                            getLogListWithOfset();
                        }
                    }
                }
            }
        };

        mRecyclerView.getRecycleView().addOnScrollListener(onScrollListener);

        G.iSignalingGetCallLog = new ISignalingGetCallLog() {
            @Override public void onGetList(final int size) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                if (size == -1) {

                    if (attampOnError < 2) {
                        isSendRequestForLoading = false;
                        attampOnError++;
                    } else {
                        isThereAnyMoreItemToLoad = false;
                        mRecyclerView.getRecycleView().removeOnScrollListener(onScrollListener);
                    }
                } else if (size == 0) {
                    isThereAnyMoreItemToLoad = false;
                    mRecyclerView.getRecycleView().removeOnScrollListener(onScrollListener);
                } else {
                    isSendRequestForLoading = false;
                    mOffset += size;
                }
            }
        };

        realm.close();

        fabContactList = (FloatingActionButton) view.findViewById(R.id.fc_fab_contact_list);
        fabContactList.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(G.appBarColor)));

        fabContactList.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

                final Fragment fragment = RegisteredContactsFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "call");
                bundle.putBoolean("ACTION", true);
                fragment.setArguments(bundle);

                try {
                    mActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_left)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainer, fragment)
                        .commit();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            }
        });

        getLogListWithOfset();

        if (goneTitle) {

            fabContactList.setVisibility(View.GONE);

            view.findViewById(R.id.fc_layot_title).setVisibility(View.GONE);

            mRecyclerView.getRecycleView().addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (((ActivityMain) getActivity()).arcMenu.isMenuOpened()) {
                        ((ActivityMain) getActivity()).arcMenu.toggleMenu();
                    }

                    if (dy > 0) {
                        // Scroll Down
                        if (((ActivityMain) getActivity()).arcMenu.fabMenu.isShown()) {
                            ((ActivityMain) getActivity()).arcMenu.fabMenu.hide();
                        }
                    } else if (dy < 0) {
                        // Scroll Up
                        if (!((ActivityMain) getActivity()).arcMenu.fabMenu.isShown()) {
                            ((ActivityMain) getActivity()).arcMenu.fabMenu.show();
                        }
                    }
                }
            });

        }
    }



    private void getLogListWithOfset() {

        if (G.isSecure && G.userLogin) {
            isSendRequestForLoading = true;
            new RequestSignalingGetLog().signalingGetLog(mOffset, mLimit);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override public void run() {
                    getLogListWithOfset();
                }
            }, 1000);
        }

    }


    public void openDialogMenu() {
        final MaterialDialog dialog = new MaterialDialog.Builder(mActivity).customView(R.layout.chat_popup_dialog_custom, true).build();
        View view = dialog.getCustomView();

        DialogAnimation.animationUp(dialog);
        dialog.show();

        final TextView txtClear = (TextView) view.findViewById(R.id.dialog_text_item1_notification);
        txtClear.setText(getResources().getString(R.string.clean_log));

        TextView iconClear = (TextView) view.findViewById(R.id.dialog_icon_item1_notification);
        iconClear.setText(getResources().getString(R.string.md_rubbish_delete_file));

        ViewGroup root1 = (ViewGroup) view.findViewById(R.id.dialog_root_item1_notification);
        root1.setVisibility(View.VISIBLE);

        root1.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                dialog.dismiss();

                if (G.userLogin) {
                    new MaterialDialog.Builder(mActivity).title(R.string.clean_log).content(R.string.are_you_sure_clear_call_logs).
                        positiveText(R.string.B_ok).onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Realm realm = Realm.getDefaultInstance();
                            try {
                                RealmCallLog realmCallLog = realm.where(RealmCallLog.class).findAllSorted(RealmCallLogFields.TIME, Sort.DESCENDING).first();
                                new RequestSignalingClearLog().signalingClearLog(realmCallLog.getId());
                                imgCallEmpty.setVisibility(View.VISIBLE);
                                empty_call.setVisibility(View.VISIBLE);
                            } catch (Exception e) {

                            } finally {
                                realm.close();
                            }
                        }
                    }).negativeText(R.string.B_cancel).show();
                } else {
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server));
                }
            }
        });
    }

    //*************************************************************************************************************

    public static void call(long userID, boolean isIncomingCall) {

        if (G.userLogin) {

            if (!G.isInCall) {
                Realm realm = Realm.getDefaultInstance();
                RealmCallConfig realmCallConfig = realm.where(RealmCallConfig.class).findFirst();

                if (realmCallConfig == null) {
                    new RequestSignalingGetConfiguration().signalingGetConfiguration();
                    HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server));
                } else {

                    if (G.currentActivity != null) {

                        Intent intent = new Intent(G.currentActivity, ActivityCall.class);
                        intent.putExtra(ActivityCall.USER_ID_STR, userID);
                        intent.putExtra(ActivityCall.INCOMING_CALL_STR, isIncomingCall);
                        ActivityCall.isGoingfromApp = true;
                        G.currentActivity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(G.context, ActivityCall.class);
                        intent.putExtra(ActivityCall.USER_ID_STR, userID);
                        intent.putExtra(ActivityCall.INCOMING_CALL_STR, isIncomingCall);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        ActivityCall.isGoingfromApp = true;
                        G.context.startActivity(intent);
                    }


                }

                realm.close();
            }


        } else {

            HelperError.showSnackMessage(G.context.getString(R.string.there_is_no_connection_to_server));
        }
    }

    //*************************************************************************************************************

    enum CallMode {
        call_made, call_received, call_missed, call_missed_outgoing
    }

    //***************************************** adapater call ***************************************************

    public class CallAdapter extends RealmBasedRecyclerViewAdapter<RealmCallLog, CallAdapter.ViewHolder> {

        public CallAdapter(Context context, RealmResults<RealmCallLog> realmResults) {
            super(context, realmResults, true, false, false, "");
        }

        public class ViewHolder extends RealmViewHolder {

            protected CircleImageView image;
            protected TextView name;
            protected MaterialDesignTextView icon;
            //  protected MaterialDesignTextView call_type_icon;
            protected TextView timeAndInfo;
            // protected RippleView rippleCall;
            protected TextView timeDureation;

            public ViewHolder(View view) {
                super(view);

                imgCallEmpty.setVisibility(View.GONE);
                empty_call.setVisibility(View.GONE);

                timeDureation = (TextView) itemView.findViewById(R.id.fcsl_txt_dureation_time);
                // call_type_icon = (MaterialDesignTextView) itemView.findViewById(R.id.fcsl_call_type_icon);
                image = (CircleImageView) itemView.findViewById(R.id.fcsl_imv_picture);
                name = (TextView) itemView.findViewById(R.id.fcsl_txt_name);
                icon = (MaterialDesignTextView) itemView.findViewById(R.id.fcsl_txt_icon);
                timeAndInfo = (TextView) itemView.findViewById(R.id.fcsl_txt_time_info);
                // rippleCall = (RippleView) itemView.findViewById(R.id.fcsl_ripple_call);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {

                        // HelperPublicMethod.goToChatRoom(realmResults.get(getPosition()).getLogProto().getPeer().getId(), null, null);

                        if (canclick) {
                            long userId = realmResults.get(getPosition()).getLogProto().getPeer().getId();

                            if (userId != 134 && G.userId != userId) {
                                call(userId, false);
                            }
                        }
                    }
                });

                itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            move = (int) event.getX();
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            int i = Math.abs((int) (move - event.getX()));

                            if (i < 2) {
                                canclick = true;
                            } else {
                                canclick = false;
                            }
                        }

                        return false;
                    }
                });
            }
        }

        @Override public CallAdapter.ViewHolder onCreateRealmViewHolder(ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_call_sub_layout, null);
            ViewHolder callHolder = new ViewHolder(view);

            return callHolder;
        }

        @Override public void onBindRealmViewHolder(final CallAdapter.ViewHolder viewHolder, int i) {

            ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog item = realmResults.get(i).getLogProto();

            // set icon and icon color
            switch (item.getStatus()) {
                case OUTGOING:
                    viewHolder.icon.setText(R.string.md_call_made);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    viewHolder.timeDureation.setTextColor(G.context.getResources().getColor(R.color.green));
                    break;
                case MISSED:
                    viewHolder.icon.setText(R.string.md_call_missed);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.red));
                    viewHolder.timeDureation.setTextColor(G.context.getResources().getColor(R.color.red));
                    viewHolder.timeDureation.setText(R.string.miss);
                    break;
                case CANCELED:

                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.green));
                    viewHolder.timeDureation.setTextColor(G.context.getResources().getColor(R.color.green));
                    viewHolder.timeDureation.setText(R.string.not_answer);
                    break;
                case INCOMING:
                    viewHolder.icon.setText(R.string.md_call_received);
                    viewHolder.icon.setTextColor(G.context.getResources().getColor(R.color.colorPrimary));
                    viewHolder.timeDureation.setTextColor(G.context.getResources().getColor(R.color.colorPrimary));
                    break;
            }

            //switch (item.getType()) {
            //
            //    case SCREEN_SHARING:
            //        viewHolder.call_type_icon.setText(R.string.md_stay_current_portrait);
            //        break;
            //    case VIDEO_CALLING:
            //        viewHolder.call_type_icon.setText(R.string.md_video_cam);
            //        break;
            //    case VOICE_CALLING:
            //        viewHolder.call_type_icon.setText(R.string.md_phone);
            //        break;
            //}

            if (HelperCalander.isLanguagePersian) {
                viewHolder.timeAndInfo.setText(TimeUtils.toLocal(item.getOfferTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME + " " + HelperCalander.checkHijriAndReturnTime(item.getOfferTime())));
            } else {
                viewHolder.timeAndInfo.setText(HelperCalander.checkHijriAndReturnTime(item.getOfferTime()) + " " + TimeUtils.toLocal(item.getOfferTime() * DateUtils.SECOND_IN_MILLIS, G.CHAT_MESSAGE_TIME));
            }

            if (item.getDuration() > 0) {
                viewHolder.timeDureation.setText(DateUtils.formatElapsedTime(item.getDuration()));
            }

            if (HelperCalander.isLanguagePersian) {
                viewHolder.timeAndInfo.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeAndInfo.getText().toString()));
                viewHolder.timeDureation.setText(HelperCalander.convertToUnicodeFarsiNumber(viewHolder.timeDureation.getText().toString()));
            }

            viewHolder.name.setText(item.getPeer().getDisplayName());

            HelperAvatar.getAvatar(item.getPeer().getId(), HelperAvatar.AvatarType.USER, new OnAvatarGet() {
                @Override public void onAvatarGet(final String avatarPath, long ownerId) {
                    G.imageLoader.displayImage(AndroidUtils.suitablePath(avatarPath), viewHolder.image);
                }

                @Override public void onShowInitials(final String initials, final String color) {
                    viewHolder.image.setImageBitmap(
                        net.iGap.helper.HelperImageBackColor.drawAlphabetOnPicture((int) viewHolder.image.getContext().getResources().getDimension(R.dimen.dp60), initials, color));
                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }
}
