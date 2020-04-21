package net.iGap.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.ActivityProfileChannelBinding;
import net.iGap.module.dialog.topsheet.TopSheetDialog;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperUrl;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.observers.interfaces.OnChannelAvatarDelete;
import net.iGap.module.AppUtils;
import net.iGap.module.CircleImageView;
import net.iGap.module.MEditText;
import net.iGap.request.RequestChannelKickAdmin;
import net.iGap.request.RequestChannelKickMember;
import net.iGap.request.RequestChannelKickModerator;
import net.iGap.request.RequestClientMuteRoom;
import net.iGap.viewmodel.FragmentChannelProfileViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;

import static android.content.Context.CLIPBOARD_SERVICE;


public class FragmentChannelProfile extends BaseFragment implements OnChannelAvatarDelete {

    private static final String ROOM_ID = "RoomId";
    private static final String IS_NOT_JOIN = "is_not_join";

    private final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.6f;
    private final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private FragmentChannelProfileViewModel viewModel;
    private ActivityProfileChannelBinding binding;
    private CircleImageView imvChannelAvatar;

    public static FragmentChannelProfile newInstance(long roomId, Boolean isNotJoin) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        args.putBoolean(IS_NOT_JOIN, isNotJoin);
        FragmentChannelProfile fragment = new FragmentChannelProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNeedResume = true;

        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                long roomId = -1;
                boolean v = false;
                if (getArguments() != null) {
                    roomId = getArguments().getLong(ROOM_ID);
                    v = getArguments().getBoolean(IS_NOT_JOIN);
                }
                return (T) new FragmentChannelProfileViewModel(FragmentChannelProfile.this, roomId, v);
            }
        }).get(FragmentChannelProfileViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_profile_channel, container, false);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imvChannelAvatar = binding.toolbarAvatar;
        imvChannelAvatar.setOnClickListener(v -> viewModel.onClickCircleImage());

        binding.toolbarBack.setOnClickListener(v -> popBackStackFragment());
        binding.toolbarMore.setOnClickListener(v -> showPopUp());
        binding.toolbarEdit.setOnClickListener(v -> {
            if (getActivity() != null && viewModel.checkIsEditableAndReturnState()) {
                new HelperFragment(getActivity().getSupportFragmentManager(), EditChannelFragment.newInstance(viewModel.roomId)).setReplace(false).load();
            }
        });

        viewModel.channelName.observe(getViewLifecycleOwner(), s -> {
            binding.toolbarTxtNameCollapsed.setText(EmojiManager.getInstance().replaceEmoji(s, binding.toolbarTxtNameCollapsed.getPaint().getFontMetricsInt()));
            binding.toolbarTxtNameExpanded.setText(EmojiManager.getInstance().replaceEmoji(s, binding.toolbarTxtNameExpanded.getPaint().getFontMetricsInt()));
        });

        viewModel.channelSecondsTitle.observe(getViewLifecycleOwner(), s -> binding.toolbarTxtStatusExpanded.setText(s));

        viewModel.menuPopupVisibility.observe(getViewLifecycleOwner(), integer -> {
            if (integer != null) {
                binding.toolbarMore.setVisibility(integer);
            }
        });

        viewModel.editButtonVisibility.observe(getViewLifecycleOwner(), visibility -> {
            if (visibility != null) {
                binding.toolbarEdit.setVisibility(visibility);
            }
        });

        viewModel.channelDescription.observe(getViewLifecycleOwner(), description -> {
            if (getActivity() != null && description != null) {
                binding.description.setText(HelperUrl.setUrlLink(getActivity(), description, true, false, null, true));
            }
        });

        viewModel.goBack.observe(getViewLifecycleOwner(), goBack -> {
            if (goBack != null && goBack) {
                popBackStackFragment();
            }
        });

        viewModel.muteNotifListener.observe(getViewLifecycleOwner(), isMute -> {
            new RequestClientMuteRoom().muteRoom(viewModel.roomId, isMute);
            binding.enableNotification.setChecked(isMute);
        });

        viewModel.goToRoomListPage.observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() instanceof ActivityMain && isGo != null && isGo) {
                ((ActivityMain) getActivity()).removeAllFragmentFromMain();
                /*new HelperFragment(getActivity().getSupportFragmentManager()).popBackStack(2);*/
            }
        });

        viewModel.goToShowMemberList.observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                new HelperFragment(
                        getActivity().getSupportFragmentManager(),
                        FragmentShowMember.newInstance2(this, data.getRoomId(), data.getRole(), data.getUserId(), data.getSelectedRole(), data.isNeedGetMemberList(), false)
                ).setReplace(false).load();
            }
        });

        viewModel.showDialogCopyLink.observe(getViewLifecycleOwner(), link -> {
            if (getActivity() != null && link != null) {

                LinearLayout layoutChannelLink = new LinearLayout(getActivity());
                layoutChannelLink.setOrientation(LinearLayout.VERTICAL);
                View viewRevoke = new View(getActivity());
                LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);

                TextInputLayout inputChannelLink = new TextInputLayout(getActivity());
                MEditText edtLink = new MEditText(getActivity());
                edtLink.setHint(R.string.channel_public_hint_revoke);
                edtLink.setTypeface(ResourcesCompat.getFont(edtLink.getContext(), R.font.main_font));
                edtLink.setText(link);
                edtLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.dp14));
                edtLink.setTextColor(new Theme().getTitleTextColor(getActivity()));
                edtLink.setHintTextColor(getResources().getColor(R.color.hint_edit_text));
                edtLink.setPadding(0, 8, 0, 8);
                edtLink.setEnabled(false);
                edtLink.setSingleLine(true);
                inputChannelLink.addView(edtLink);
                inputChannelLink.addView(viewRevoke, viewParams);

                TextView txtLink = new AppCompatTextView(getActivity());
                txtLink.setText(/*Config.IGAP_LINK_PREFIX +*/ link);
                txtLink.setTextColor(new Theme().getTitleTextColor(getActivity()));

                viewRevoke.setBackgroundColor(getResources().getColor(R.color.line_edit_text));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    edtLink.setBackground(getResources().getDrawable(android.R.color.transparent));
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutChannelLink.addView(inputChannelLink, layoutParams);
                layoutChannelLink.addView(txtLink, layoutParams);

                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).title(R.string.channel_link)
                        .positiveText(R.string.array_Copy)
                        .customView(layoutChannelLink, true)
                        .widgetColor(new Theme().getAccentColor(getContext()))
                        .negativeText(R.string.B_cancel)
                        .onPositive((dialog1, which) -> {
                            if (getActivity() != null) {
                                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("LINK_GROUP", /*Config.IGAP_LINK_PREFIX +*/ link);
                                clipboard.setPrimaryClip(clip);
                            }
                        })
                        .build();

                dialog.show();
            }
        });

        viewModel.goToSharedMediaPage.observe(getViewLifecycleOwner(), typeModel -> {
            if (getActivity() != null && typeModel != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentShearedMedia.newInstance(typeModel)).setReplace(false).load();
            }
        });

        viewModel.goToShowAvatarPage.observe(getViewLifecycleOwner(), roomId -> {
            if (getActivity() != null && roomId != null) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentShowAvatars.newInstance(roomId, FragmentShowAvatars.From.channel)).setReplace(false).load();
            }
        });

        viewModel.showDialogLeaveChannel.observe(getViewLifecycleOwner(), isShow -> showDialogLeaveChannel());

        viewModel.goToChatRoom.observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() != null && isGo != null && isGo) {
                ((ActivityMain) getActivity()).removeAllFragmentFromMain();
            }
        });

        viewModel.showErrorMessage.observe(getViewLifecycleOwner(), errorMessageResId -> {
            if (errorMessageResId != null) {
                HelperError.showSnackMessage(getString(errorMessageResId), false);
            }
        });

        BetterLinkMovementMethod
                .linkify(Linkify.ALL, binding.description)
                .setOnLinkClickListener((tv, url) -> {
                    return false;
                })
                .setOnLinkLongClickListener((tv, url) -> {
                    if (HelperUrl.isTextLink(url)) {
                        G.isLinkClicked = true;

                        HelperUrl.openLinkDialog(getActivity(), url);
                    }
                    return true;
                });

        //binding.description.setMovementMethod(LinkMovementMethod.getInstance());

        AppUtils.setProgresColler(binding.loading);

        FragmentShowAvatars.onComplete = (result, messageOne, MessageTow) -> {

            long mAvatarId = 0;
            if (messageOne != null && !messageOne.equals("")) {
                mAvatarId = Long.parseLong(messageOne);
            }
            avatarHandler.avatarDelete(new ParamWithAvatarType(imvChannelAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM), mAvatarId);
        };

        setAvatar();

        initialToolbar();
    }

    private void initialToolbar() {

        binding.toolbarAppbar.addOnOffsetChangedListener((appBarLayout, offset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(offset) / (float) maxScroll;

            handleAlphaOnTitle(percentage);
            handleToolbarTitleVisibility(percentage);
        });
        startAlphaAnimation(binding.toolbarTxtNameCollapsed, 0, View.INVISIBLE);

    }

    private void showDialogLeaveChannel() {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity()).title(R.string.channel_left).content(R.string.do_you_want_leave_this_channel).positiveText(R.string.yes).onPositive((dialog, which) -> {
                viewModel.leaveChannel();
            }).negativeText(R.string.no).show();
        }
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(binding.toolbarTxtNameCollapsed, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(binding.toolbarTxtNameCollapsed, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.toolbarLayoutExpTitles, 100, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(binding.toolbarLayoutExpTitles, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        if (visibility == View.VISIBLE) v.setVisibility(View.VISIBLE);

        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAvatar();
        viewModel.checkChannelIsEditable();
        viewModel.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        FragmentChat fragment = (FragmentChat) getFragmentManager().findFragmentByTag(FragmentChat.class.getName());
        if (fragment != null && fragment.isVisible()) {
            fragment.onResume();
        }
    }

    private void setAvatar() {
        avatarHandler.getAvatar(new ParamWithAvatarType(imvChannelAvatar, viewModel.roomId).avatarType(AvatarHandler.AvatarType.ROOM).showMain());
    }

    private void showPopUp() {
        if (getActivity() != null) {
            List<String> items = new ArrayList<>();
            items.add(getString(R.string.add_to_home_screen));
            new TopSheetDialog(getActivity()).setListData(items, -1, position -> {
                //ToDo: add code for add to home screen
            }).show();
        }
    }


    //********* kick user from roles
    public void kickMember(final Long peerId) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity())
                    .content(R.string.do_you_want_to_kick_this_member)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive((dialog, which) -> new RequestChannelKickMember().channelKickMember(viewModel.roomId, peerId))
                    .show();
        }
    }

    public void kickModerator(final Long peerId) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity()).
                    content(R.string.do_you_want_to_set_modereator_role_to_member)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive((dialog, which) -> new RequestChannelKickModerator().channelKickModerator(viewModel.roomId, peerId))
                    .show();
        }
    }

    public void kickAdmin(final Long peerId) {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity())
                    .content(R.string.do_you_want_to_set_admin_role_to_member)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .onPositive((dialog, which) -> new RequestChannelKickAdmin().channelKickAdmin(viewModel.roomId, peerId))
                    .show();
        }
    }

    @Override
    public void onChannelAvatarDelete(long roomId, long avatarId) {
        setAvatar();
    }

    @Override
    public void onError(int majorCode, int minorCode) {

    }

    @Override
    public void onTimeOut() {

    }
}