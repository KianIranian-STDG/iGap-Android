package net.iGap.story.viewPager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.gson.Gson;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.emoji.SuggestedStickerAdapter;
import net.iGap.fragments.emoji.add.FragmentSettingAddStickers;
import net.iGap.fragments.emoji.remove.StickerSettingFragment;
import net.iGap.fragments.emoji.struct.StructIGSticker;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.libs.emojiKeyboard.EmojiView;
import net.iGap.libs.emojiKeyboard.KeyboardView;
import net.iGap.libs.emojiKeyboard.NotifyFrameLayout;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AppUtils;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.CircleImageView;
import net.iGap.module.FontIconTextView;
import net.iGap.module.LastSeenTimeUtil;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.TimeUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.additionalData.AdditionalType;
import net.iGap.module.customView.EventEditText;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.Downloader;
import net.iGap.module.downloader.HttpRequest;
import net.iGap.module.downloader.Status;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.proto.ProtoGlobal;
import net.iGap.realm.RealmAdditional;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomMessage;
import net.iGap.realm.RealmStickerItem;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmUserInfo;
import net.iGap.repository.StickerRepository;
import net.iGap.story.ExpandableTextView;
import net.iGap.story.ViewUserDialogFragment;
import net.iGap.story.liststories.ImageLoadingView;
import net.iGap.story.storyviews.StoryCell;
import net.iGap.structs.AttachmentObject;
import net.iGap.structs.MessageObject;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static android.content.Context.MODE_PRIVATE;
import static net.iGap.adapter.items.chat.ViewMaker.setTextSize;

public class StoryDisplayFragment extends BaseFragment implements StoriesProgressView.StoriesListener, StoriesProgressView.StoryProgressListener, RecyclerListView.OnItemClickListener, ViewUserDialogFragment.ViewUserDialogState {
    private static final String EXTRA_POSITION = "EXTRA_POSITION";
    private static final String EXTRA_STORY_USER = "EXTRA_STORY_USER";
    private static final String EXTRA_IS_MY_STORY = "EXTRA_IS_MY_STORY";
    private static final String EXTRA_IS_FOR_REPLY = "EXTRA_IS_FOR_REPLY";
    private int position;
    private StoryUser storyUser;
    private List<Story> stories;
    private long pressTime = 0L;
    private long limit = 500L;
    private int counter = 0;
    private int downloadCounter = 0;
    private boolean keyboardViewVisible;
    private boolean onResumeCalled = false;
    private PageViewOperator pageViewOperator;
    private AtomicInteger OpenKeyboard = new AtomicInteger();
    private AtomicInteger closeKeyboard = new AtomicInteger();
    private boolean clickable;
    boolean isMyStory = false;
    private KeyboardView keyboardView;
    int rowSize = 0;
    int userRow = 0;
    private RecyclerView suggestedRecyclerView;
    private SuggestedStickerAdapter suggestedAdapter;
    private FrameLayout suggestedLayout;
    private StickerRepository stickerRepository;
    private CompositeDisposable compositeDisposable;
    private Disposable disposable;
    private String lastChar;
    private Runnable openKeyboardRunnable;
    private boolean keyboardVisible;
    private SharedPreferences emojiSharedPreferences;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean showKeyboardOnResume;


    private View topShadow;
    private View next;
    private View previous;
    private CircleImageView profileImage;
    private StoriesProgressView storiesProgressView;
    private ProgressBar storyVideoProgress;
    private ImageView storyDisplayImage;
    private PlayerView storyVideoPlayer;
    private LinearLayout topLinearRootView;
    private TextView nickName, storyTime;
    private FontIconTextView chatIconTv;
    private FontIconTextView verifyIconTv;
    private FrameLayout keyboardContainer;
    private ProgressBar storyProgress;
    private TextView seenCount;
    private TextView eyeIcon;
    private LinearLayout storySeenContainer;
    private TextView replyText;
    private TextView replyIcon;
    private LinearLayout storyReplyContainer;
    private ExpandableTextView captionRootView;
    private TextView sendIcon;
    private EventEditText replyEditText;
    private TextView emojiIcon;
    private FrameLayout sendReplyContainer;
    private TextView replyCaption;
    private TextView replyTo;
    private LinearLayout replyContentContainer;
    private ImageView replyThumb;
    private FrameLayout replyContainer, rootLayout, storyOverlay;
    private NotifyFrameLayout notifyFrameLayout;
    private int keyboardViewHeight;
    private boolean isFormChat;

    public static StoryDisplayFragment newInstance(int position, StoryUser storyModel, boolean isMyStory, boolean isForReply) {

        Bundle args = new Bundle();
        StoryDisplayFragment fragment = new StoryDisplayFragment(position, storyModel, isMyStory, isForReply);
        args.putInt(EXTRA_POSITION, position);
        args.putSerializable(EXTRA_STORY_USER, storyModel);
        args.putBoolean(EXTRA_IS_MY_STORY, isMyStory);
        args.putBoolean(EXTRA_IS_FOR_REPLY, isForReply);
        fragment.setArguments(args);

        return fragment;
    }


    public StoryDisplayFragment(int position, StoryUser storyModel, boolean isMyStory, boolean isForReply) {
        this.position = position;
        this.storyUser = storyModel;
        this.isFormChat = isForReply;
    }

    public void setPageViewOperator(PageViewOperator pageViewOperator) {
        this.pageViewOperator = pageViewOperator;
    }


    @SuppressLint("WrongConstant")
    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        rootLayout = (FrameLayout) fragmentView;

        notifyFrameLayout = new NotifyFrameLayout(context) {
            @Override
            public boolean dispatchKeyEventPreIme(KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (keyboardViewVisible) {
                        hideStoryReply();
                        return true;
                    }
                    return false;
                }
                return super.dispatchKeyEventPreIme(event);
            }
        };
        notifyFrameLayout.setListener(this::onScreenSizeChanged);
        rootLayout.addView(notifyFrameLayout, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        storyVideoPlayer = new PlayerView(context);
        storyVideoPlayer.setVisibility(View.GONE);
        storyVideoPlayer.setContentDescription("storyVideoPlayer");
        notifyFrameLayout.addView(storyVideoPlayer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));


        storyDisplayImage = new ImageView(context);
        storyDisplayImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        storyDisplayImage.setContentDescription("storyDisplayImage");
        notifyFrameLayout.addView(storyDisplayImage, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        previous = new LinearLayout(context);
        FrameLayout.LayoutParams previousLayoutParam = new FrameLayout.LayoutParams(width / 2, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.LEFT);
        previous.setContentDescription("previous");
        notifyFrameLayout.addView(previous, previousLayoutParam);

        next = new LinearLayout(context);
        FrameLayout.LayoutParams nextLayoutParam = new FrameLayout.LayoutParams(width / 2, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.RIGHT);
        next.setContentDescription("next");

        notifyFrameLayout.addView(next, nextLayoutParam);

        storyVideoProgress = new ProgressBar(context);
        storyVideoProgress.setContentDescription("storyVideoProgress");
        notifyFrameLayout.addView(storyVideoProgress, LayoutCreator.createFrame(40, 40, Gravity.RIGHT | Gravity.TOP, 0, 20, 20, 0));

        storyOverlay = new FrameLayout(context);
        storyOverlay.setContentDescription("storyOverlay");
        notifyFrameLayout.addView(storyOverlay, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        topShadow = new View(context);
        topShadow.setContentDescription("topShadow");
        topShadow.setBackground(context.getResources().getDrawable(R.drawable.story_top_shadow));
        storyOverlay.addView(topShadow, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 60, Gravity.TOP));

        storiesProgressView = new StoriesProgressView(context, null);
        storiesProgressView.setContentDescription("storiesProgressView");
        storiesProgressView.setPadding(8, 0, 8, 0);
        storyOverlay.addView(storiesProgressView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 0, 2, 0, 0));

        profileImage = new CircleImageView(context);
        profileImage.setContentDescription("profileImage");
        storyOverlay.addView(profileImage, LayoutCreator.createFrame(40, 40, Gravity.LEFT, 8, 10, 8, 8));

        LinearLayout profileContainer = new LinearLayout(context);
        profileContainer.setContentDescription("profileContainer");
        profileContainer.setOrientation(LinearLayout.VERTICAL);

        topLinearRootView = new LinearLayout(context);
        topLinearRootView.setGravity(Gravity.CENTER);
        topLinearRootView.setOrientation(LinearLayout.HORIZONTAL);
        profileContainer.addView(topLinearRootView, 0, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));

        nickName = new TextView(context);
        nickName.setContentDescription("nickName");
        nickName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        nickName.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        nickName.setTextColor(Color.WHITE);
        topLinearRootView.addView(nickName, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

        chatIconTv = new FontIconTextView(getContext());
        setTextSize(chatIconTv, R.dimen.dp14);
        chatIconTv.setVisibility(View.GONE);
        chatIconTv.setContentDescription("chatIconTv");
        chatIconTv.setText(R.string.icon_channel);
        chatIconTv.setTextColor(Color.WHITE);
        topLinearRootView.addView(chatIconTv, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 4, 0, 0, 0));

        verifyIconTv = new FontIconTextView(getContext());
        verifyIconTv.setTextColor(Theme.getColor(Theme.key_dark_theme_color));
        verifyIconTv.setText(R.string.icon_blue_badge);
        verifyIconTv.setVisibility(View.GONE);
        setTextSize(verifyIconTv, R.dimen.standardTextSize);
        topLinearRootView.addView(verifyIconTv, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 4, 0, 0, 0));



        storyTime = new TextView(context);
        storyTime.setContentDescription("storyTime");
        storyTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        storyTime.setTypeface(nickName.getTypeface(), Typeface.BOLD);
        storyTime.setTextColor(Color.WHITE);


        profileContainer.addView(storyTime, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));

        storyOverlay.addView(profileContainer, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.LEFT, 56, 8, 0, 0));

        captionRootView = new ExpandableTextView(context);
        captionRootView.setContentDescription("captionRootView");
        captionRootView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        captionRootView.setTextColor(Color.WHITE);
        captionRootView.setPadding(8, 8, 8, 8);
        rootLayout.addView(captionRootView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM, 0, 0, 0, 80));

        storySeenContainer = new LinearLayout(context);
        storySeenContainer.setContentDescription("storySeenContainer");
        storySeenContainer.setGravity(Gravity.CENTER_VERTICAL);
        storySeenContainer.setOrientation(LinearLayout.HORIZONTAL);

        eyeIcon = new TextView(context);
        eyeIcon.setContentDescription("eyeIcon");
        eyeIcon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        eyeIcon.setTextColor(Color.WHITE);
        eyeIcon.setGravity(Gravity.CENTER_VERTICAL);
        eyeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_view_seen));
        eyeIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);


        seenCount = new TextView(context);
        seenCount.setContentDescription("seenCount");
        seenCount.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        seenCount.setGravity(Gravity.CENTER_VERTICAL);
        seenCount.setTextColor(Color.WHITE);
        seenCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        storySeenContainer.addView(eyeIcon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.LEFT));
        storySeenContainer.addView(seenCount, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.RIGHT, 3, 0, 0, 0));


        storyProgress = new ProgressBar(context);
        storyProgress.setContentDescription("storyProgress");
        storyProgress.setVisibility(View.GONE);
        notifyFrameLayout.addView(storyProgress, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));

        GradientDrawable layer1 = new GradientDrawable();
        layer1.setShape(GradientDrawable.RECTANGLE);
        layer1.setCornerRadii(new float[]{LayoutCreator.dp(8), LayoutCreator.dp(8), 0, 0, 0, 0, LayoutCreator.dp(8), LayoutCreator.dp(8)});
        layer1.setColor(Theme.getColor(Theme.key_toolbar_background));

        GradientDrawable layer2 = new GradientDrawable();
        layer2.setShape(GradientDrawable.RECTANGLE);
        layer2.setColor(Theme.getColor(Theme.key_white));

        InsetDrawable insetDrawable = new InsetDrawable(layer2, LayoutCreator.dp(8), 0, 0, 0);
        LayerDrawable drawable = new LayerDrawable(new Drawable[]{layer1, insetDrawable});

        LinearLayout replyAndKeyboardContainer = new LinearLayout(context);
        replyAndKeyboardContainer.setOrientation(LinearLayout.VERTICAL);
        notifyFrameLayout.addView(replyAndKeyboardContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));

        replyContainer = new FrameLayout(context);
        replyContainer.setVisibility(View.GONE);
        replyContainer.setContentDescription("replyContainer");
        replyContainer.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.chat_item_sent_bg_light), context, Theme.getColor(Theme.key_window_background)));
        replyContainer.setPadding(15, 15, 15, 15);
        replyAndKeyboardContainer.addView(replyContainer, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 15, 0, 15, 10));

        FrameLayout mainContentContainer = new FrameLayout(context);
        replyContainer.addView(mainContentContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.LEFT));

        replyContentContainer = new LinearLayout(context);
        replyContentContainer.setContentDescription("replyContentContainer");
        replyContentContainer.setBackground(drawable);
        replyContentContainer.setBackground(Theme.tintDrawable(context.getResources().getDrawable(R.drawable.chat_item_sent_bg_light), context, Theme.getColor(Theme.key_window_background)));
        replyContentContainer.setOrientation(LinearLayout.VERTICAL);
        mainContentContainer.addView(replyContentContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 50, Gravity.LEFT, 10, 5, 45, 5));

        replyThumb = new ImageView(context);
        replyThumb.setContentDescription("replyThumb");
        mainContentContainer.addView(replyThumb, LayoutCreator.createFrame(25, LayoutCreator.MATCH_PARENT, Gravity.RIGHT | Gravity.TOP, 0, 0, 5, 0));

        replyTo = new TextView(context);
        replyTo.setContentDescription("replyTo");
        replyTo.setTypeface(ResourcesCompat.getFont(context, R.font.main_font_bold));
        replyTo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        replyTo.setEllipsize(TextUtils.TruncateAt.END);
        replyTo.setTextColor(Theme.getColor(Theme.key_dark_theme_color));
        replyContentContainer.addView(replyTo, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP, 8, 0, 0, 0));

        replyCaption = new TextView(context);
        replyCaption.setContentDescription("replyCaption");
        replyCaption.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        replyCaption.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        replyCaption.setEllipsize(TextUtils.TruncateAt.END);
        replyCaption.setSingleLine(true);
        replyCaption.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        replyContentContainer.addView(replyCaption, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM, 8, 0, 8, 0));

        sendReplyContainer = new FrameLayout(context);
        sendReplyContainer.setContentDescription("sendReplyContainer");
        replyContainer.addView(sendReplyContainer, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM, 0, 60, 0, 0));

        emojiIcon = new TextView(context);
        emojiIcon.setContentDescription("emojiIcon");
        emojiIcon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        emojiIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        emojiIcon.setText(R.string.icon_emoji_smile);
        emojiIcon.setGravity(Gravity.CENTER);
        emojiIcon.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        sendReplyContainer.addView(emojiIcon, LayoutCreator.createFrame(50, 50, Gravity.LEFT, 5, 0, 5, 0));

        replyEditText = new EventEditText(context);
        replyEditText.setBackground(null);
        replyEditText.setContentDescription("replyEditText");
        replyEditText.setListener(this::chatMotionEvent);
        replyEditText.setTextColor(Theme.getColor(Theme.key_default_text));
        replyEditText.setHintTextColor(Theme.getColor(Theme.key_subtitle_text));
        sendReplyContainer.addView(replyEditText, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.LEFT, 50, 0, 55, 0));

        sendIcon = new TextView(context);
        sendIcon.setContentDescription("sendIcon");
        sendIcon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        sendIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        sendIcon.setText(R.string.icon_send);
        sendIcon.setGravity(Gravity.CENTER);
        sendIcon.setTextColor(Theme.getColor(Theme.key_theme_color));
        sendReplyContainer.addView(sendIcon, LayoutCreator.createFrame(50, 50, Gravity.RIGHT, 5, 0, 5, 0));

        keyboardContainer = new FrameLayout(context);
        keyboardContainer.setContentDescription("keyboardContainer");
        replyAndKeyboardContainer.addView(keyboardContainer, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.BOTTOM));

        storyReplyContainer = new LinearLayout(context);
        storyReplyContainer.setContentDescription("storyReplyContainer");
        storyReplyContainer.setOrientation(LinearLayout.VERTICAL);
        storyReplyContainer.setGravity(Gravity.CENTER_HORIZONTAL);

        replyIcon = new TextView(context);
        replyIcon.setContentDescription("replyIcon");
        replyIcon.setTypeface(ResourcesCompat.getFont(context, R.font.font_icons));
        replyIcon.setTextColor(Color.WHITE);
        replyIcon.setText(R.string.icon_chevron_Down);
        replyIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        replyText = new TextView(context);
        replyText.setContentDescription("replyText");
        replyText.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        replyText.setTextColor(Color.WHITE);
        replyText.setText(R.string.replay);
        replyText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        storyReplyContainer.addView(replyIcon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
        storyReplyContainer.addView(replyText, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));


        notifyFrameLayout.addView(storySeenContainer, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0, 0, 20));
        notifyFrameLayout.addView(storyReplyContainer, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0, 0, 30));

        return rootLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        counter = 0;

        emojiSharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.EMOJI, MODE_PRIVATE);
        stories = storyUser.getStories();
        isMyStory = storyUser.getUserId() == AccountManager.getInstance().getCurrentUser().getId();
        if (isMyStory) {
            storyReplyContainer.setVisibility(View.INVISIBLE);
            storySeenContainer.setVisibility(View.VISIBLE);
        } else {
            if (stories.get(counter).isRoom()) {
                storyReplyContainer.setVisibility(View.INVISIBLE);
            } else {
                storyReplyContainer.setVisibility(View.VISIBLE);
            }
            storySeenContainer.setVisibility(View.INVISIBLE);
        }

        if (isFormChat) {
            storyReplyContainer.setVisibility(View.INVISIBLE);
            storySeenContainer.setVisibility(View.INVISIBLE);
        }

        setUpUi();
        storyReplyContainer.setOnClickListener(view1 -> {
            setupReply();
        });

        sendIcon.setOnClickListener(view1 -> {
            String replyText = Objects.requireNonNull(replyEditText.getText()).toString().trim();
            if (replyText.length() == 0) {
                replyEditText.setText("");
                Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
            } else if (!checkEmptyMessageWithSemiSpace(new String[]{replyText})) {
                return;
            }
            replyEditText.setText("");
            hideKeyboard();

            RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
                return realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", stories.get(counter).getUserId()).findFirst();
            });

            if (realmRoom == null) {
                IG_RPC.Chat_get_room chat_get_room = new IG_RPC.Chat_get_room();
                chat_get_room.peerId = stories.get(counter).getUserId();

                getRequestManager().sendRequest(chat_get_room, (response, error) -> {
                    if (response != null) {
                        IG_RPC.Res_chat_get_room res = (IG_RPC.Res_chat_get_room) response;
                        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).buildStoryReply(res.room.getTypeValue(), res.room.getId(), stories.get(counter), null, replyText);
                        hideReplyViews();
                    }
                });

            } else {
                ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).buildStoryReply(realmRoom.getType().getNumber(), realmRoom.getId(), stories.get(counter), null, replyText);
                hideReplyViews();
                Toast.makeText(context, getString(R.string.sending_reply), Toast.LENGTH_SHORT).show();
            }

        });

        storySeenContainer.setOnClickListener(v -> {
            if (isMyStory && stories.get(counter).getViewCount() > 0) {
                ViewUserDialogFragment blankFragment = new ViewUserDialogFragment(stories.get(counter).getViewCount(), stories.get(counter).getUserIdList());
                blankFragment.setViewUserDialogState(StoryDisplayFragment.this::onCancel);
                blankFragment.show(getParentFragmentManager(), blankFragment.getTag());
                pauseCurrentStory();
            }

        });

        emojiIcon.setOnClickListener(view1 -> {
            if (keyboardView == null)
                createKeyboardView();

            if (isPopupShowing() && keyboardView.getCurrentMode() != KeyboardView.MODE_KEYBOARD && keyboardView.getCurrentMode() != -1) {
                showPopup(KeyboardView.MODE_KEYBOARD);
                openKeyBoard();
            } else {
                showPopup(KeyboardView.MODE_EMOJI);
                AndroidUtils.hideKeyboard(replyEditText);
            }
        });
    }

    private void hideReplyViews() {
        AnimatorSet animationSet = new AnimatorSet();
        animationSet.play(
                ObjectAnimator.ofFloat(replyContainer, View.TRANSLATION_X, 0, replyContainer.getMeasuredWidth())
        );
        animationSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                replyContainer.setVisibility(View.GONE);
                replyContainer.setTranslationX(0);
                storyReplyContainer.setVisibility(View.VISIBLE);
                captionRootView.setVisibility(View.VISIBLE);
                showPopup(-1);
                G.handler.postDelayed(() -> updateStory(), 200);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animationSet.setInterpolator(new AnticipateInterpolator());
        animationSet.setDuration(300);
        animationSet.start();

    }

    private void onScreenSizeChanged(int height, boolean land) {

        if (height > LayoutCreator.dp(50) && keyboardVisible) {
            if (land) {
                keyboardHeightLand = height;
                if (emojiSharedPreferences != null)
                    emojiSharedPreferences.edit().putInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, keyboardHeightLand).apply();
            } else {
                keyboardHeight = height;
                if (emojiSharedPreferences != null)
                    emojiSharedPreferences.edit().putInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, keyboardHeight).apply();
            }
        }

        if (isPopupShowing()) {
            int newHeight = land ? keyboardHeightLand : keyboardViewHeight;

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) keyboardView.getLayoutParams();
            if (layoutParams.width != AndroidUtils.displaySize.x || layoutParams.height != newHeight) {
                layoutParams.width = AndroidUtils.displaySize.x;
                layoutParams.height = newHeight;
                keyboardView.setLayoutParams(layoutParams);
            }
        }

        keyboardVisible = height > 0;

        if (notifyFrameLayout != null) {
            notifyFrameLayout.requestLayout();
        }

//        if (suggestedLayout != null && suggestedLayout.getVisibility() == View.VISIBLE) {
//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) suggestedLayout.getLayoutParams();
//            if (keyboardViewVisible)
//                layoutParams.bottomMargin = keyboardHeight + LayoutCreator.dp(60);
//            else
//                layoutParams.bottomMargin = LayoutCreator.dp(60);
//        }
    }

    private void showPopup(int mode) {

        if (mode == KeyboardView.MODE_EMOJI) {
            changeEmojiButtonImageResource(R.string.icon_keyboard);
        } else {
            changeEmojiButtonImageResource(R.string.icon_emoji_smile);
        }

        if (mode != -1) {
            keyboardViewVisible = true;
            if (keyboardView != null && keyboardView.getVisibility() == View.GONE)
                keyboardView.setVisibility(View.VISIBLE);
        }

        if (mode == KeyboardView.MODE_EMOJI) {
            if (keyboardView == null) {
                createKeyboardView();
            }

            if (keyboardView.getParent() == null)
                keyboardContainer.addView(keyboardView);

            keyboardVisible = false;

            if (keyboardViewHeight <= 0) {
                keyboardViewHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, 0);
            }

            if (keyboardHeightLand <= 0) {
                keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, 0);
            }

            int currentHeight = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y ? keyboardHeightLand : keyboardViewHeight;
            keyboardView.setKeyboardHeight(keyboardHeightLand, keyboardViewHeight);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) keyboardView.getLayoutParams();
            layoutParams.height = currentHeight;
            keyboardView.setLayoutParams(layoutParams);

            keyboardView.setCurrentMode(KeyboardView.MODE_EMOJI, EmojiView.STICKER);

            keyboardView.setVisibility(View.VISIBLE);

        } else if (mode == KeyboardView.MODE_ATTACHMENT) {

        } else if (mode == KeyboardView.MODE_KEYBOARD) {

            if (keyboardView == null)
                createKeyboardView();

            if (keyboardView != null) {

                keyboardView.setCurrentMode(KeyboardView.MODE_KEYBOARD, -1);

                if (keyboardViewHeight <= 0) {
                    keyboardViewHeight = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT, 0);
                }

                if (keyboardHeightLand <= 0) {
                    keyboardHeightLand = emojiSharedPreferences.getInt(SHP_SETTING.KEY_KEYBOARD_HEIGHT_LAND, 0);
                }

                int currentHeight = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y ? keyboardHeightLand : keyboardViewHeight;
                keyboardView.setKeyboardHeight(keyboardHeightLand, keyboardViewHeight);

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) keyboardView.getLayoutParams();
                layoutParams.height = currentHeight;
                keyboardView.setLayoutParams(layoutParams);

                keyboardView.setVisibility(View.VISIBLE);

                keyboardVisible = true;
            }
        } else {
            if (keyboardView != null)
                keyboardView.setCurrentMode(mode, -1);

            showKeyboardOnResume = false;

            closeKeyboard(replyEditText);
            G.handler.post(this::hideKeyboardView);
        }
    }

    private void hideKeyboardView() {
        if (keyboardView == null)
            return;

//        if (suggestedLayout != null && suggestedLayout.getVisibility() == View.VISIBLE) {
//            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) suggestedLayout.getLayoutParams();
//            layoutParams.bottomMargin = LayoutCreator.dp(60);
//        }

        keyboardViewVisible = false;
        keyboardView.setVisibility(View.GONE);
    }

    private void changeEmojiButtonImageResource(@StringRes int drawableResourceId) {
        if (emojiIcon != null)
            emojiIcon.setText(drawableResourceId);
    }


    private Runnable createKeyboardRunnable(EditText editText) {
        openKeyboardRunnable = () -> {
            editText.requestFocus();
            AndroidUtils.showKeyboard(editText);
        };
        return openKeyboardRunnable;
    }

    private boolean isPopupShowing() {
        return keyboardViewVisible || keyboardView != null;
    }

    private void createKeyboardView() {
        if (getContext() != null) {
            keyboardView = new KeyboardView(getContext(), new KeyboardView.Listener() {
                @Override
                public void onViewCreated(int mode) {

                }

                @Override
                public void onStickerSettingClicked() {
                    if (getActivity() != null) {
                        showPopup(-1);
                        new HelperFragment(getActivity().getSupportFragmentManager(), new StickerSettingFragment()).setReplace(false).load();
                    }
                }

                @Override
                public void onBackSpace() {
                    if (replyEditText.length() == 0) {
                        return;
                    }
                    replyEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }

                @Override
                public void onSendStickerAsMessage(StructIGSticker structIGSticker) {
                    sendStickerAsMessage(structIGSticker);
                }

                @Override
                public void onAddStickerClicked() {
                    if (getActivity() != null) {
                        showPopup(-1);
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentSettingAddStickers.newInstance("ALL")).setReplace(false).load();
                    }
                }

                @Override
                public void onEmojiSelected(String unicode) {
                    int i = replyEditText.getSelectionEnd();

                    if (i < 0) i = 0;

                    try {
                        CharSequence sequence = EmojiManager.getInstance().replaceEmoji(unicode, replyEditText.getPaint().getFontMetricsInt(), LayoutCreator.dp(22), false);
                        if (replyEditText.getText() != null)
                            replyEditText.setText(replyEditText.getText().insert(i, sequence));
                        int j = i + sequence.length();
                        replyEditText.setSelection(j, j);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (EmojiManager.getInstance().isValidEmoji(unicode) && replyEditText.getText().toString().equals(unicode)) {
                        getStickerByEmoji(unicode);
                    } else if (replyEditText.getText() != null && !replyEditText.getText().toString().equals("")) {
                        if (suggestedLayout != null && suggestedLayout.getVisibility() == View.VISIBLE) {
                            suggestedLayout.setVisibility(View.GONE);
                            lastChar = null;
                        }
                    }
                }

            }, KeyboardView.MODE_KEYBOARD, false);

            keyboardView.setVisibility(View.GONE);

//            if (mustCheckPermission())
//                keyboardView.setStickerPermission(currentRoomAccess != null && currentRoomAccess.getRealmPostMessageRights() != null && currentRoomAccess.getRealmPostMessageRights().isCanSendSticker());

            keyboardContainer.addView(keyboardView);
        }
    }

    private boolean checkEmptyMessageWithSemiSpace(String[] messages) {
        boolean haveCharacterExceptSemiSpace = false;

        char[] message = messages[0].toCharArray();
        for (int i = 0; i < message.length; i++) {

            if (message[i] != 8204) {
                haveCharacterExceptSemiSpace = true;
            }
        }

        return haveCharacterExceptSemiSpace;
    }

    private void getStickerByEmoji(String unicode) {
        if (lastChar == null) {
            lastChar = unicode;

            if (suggestedLayout == null && getContext() != null) {
                stickerRepository = StickerRepository.getInstance();
                suggestedAdapter = new SuggestedStickerAdapter();
                compositeDisposable = new CompositeDisposable();

                suggestedLayout = new FrameLayout(getContext());

                suggestedRecyclerView = new RecyclerView(getContext());

                suggestedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                suggestedRecyclerView.setAdapter(suggestedAdapter);
                suggestedRecyclerView.setClipToPadding(false);
                suggestedRecyclerView.setPadding(LayoutCreator.dp(2), LayoutCreator.dp(3), LayoutCreator.dp(8), LayoutCreator.dp(2));
                suggestedAdapter.setListener(structIGSticker -> {
                    lastChar = null;
                    suggestedLayout.setVisibility(View.GONE);
                    suggestedAdapter.clearData();
                    suggestedRecyclerView.scrollToPosition(0);

                    if (disposable != null && !disposable.isDisposed())
                        disposable.dispose();

                    replyEditText.setText("");
                    sendStickerAsMessage(structIGSticker);
                });

                suggestedLayout.addView(suggestedRecyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER));
//                rootLayout.addView(suggestedLayout, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.BOTTOM, 6, 8, 6, keyboardViewVisible ? LayoutCreator.pxToDp(keyboardHeight) + 60 : 60));
            }

            suggestedRecyclerView.setBackground(Theme.tintDrawable(getResources().getDrawable(R.drawable.shape_suggested_sticker), getContext(), Theme.getColor(Theme.key_title_text)));

            disposable = stickerRepository
                    .getStickerByEmoji(lastChar)
                    .filter(structIGStickers -> structIGStickers.size() > 0 && lastChar != null)
                    .subscribe(structIGStickers -> {
                        suggestedAdapter.setIgStickers(structIGStickers);
                        suggestedLayout.setVisibility(View.VISIBLE);
                    });

            compositeDisposable.add(disposable);
        }
    }

    private void sendStickerAsMessage(StructIGSticker structIGSticker) {

        RealmRoom realmRoom = DbManager.getInstance().doRealmTask(realm -> {
            return realm.where(RealmRoom.class).equalTo("chatRoom.peer_id", stories.get(counter).getUserId()).findFirst();
        });

        String additional = new Gson().toJson(structIGSticker);
        long identity = AppUtils.makeRandomId();
        int[] imageSize = AndroidUtils.getImageDimens(structIGSticker.getPath());
        RealmRoomMessage roomMessage = new RealmRoomMessage();
        roomMessage.setMessageId(identity);
        roomMessage.setMessageType(ProtoGlobal.RoomMessageType.STORY_REPLY);
        if (realmRoom != null) {
            roomMessage.setRoomId(realmRoom.getId());
        }
        roomMessage.setMessage(structIGSticker.getName());
        roomMessage.setStatus(ProtoGlobal.RoomMessageStatus.SENDING.toString());
        roomMessage.setUserId(AccountManager.getInstance().getCurrentUser().getId());
        roomMessage.setCreateTime(TimeUtils.currentLocalTime());

        RealmAdditional realmAdditional = new RealmAdditional();
        realmAdditional.setId(AppUtils.makeRandomId());
        realmAdditional.setAdditionalType(structIGSticker.isGiftSticker() ? AdditionalType.GIFT_STICKER : AdditionalType.STICKER);
        realmAdditional.setAdditionalData(additional);

        roomMessage.setRealmAdditional(realmAdditional);

        RealmAttachment realmAttachment = new RealmAttachment();
        if (structIGSticker.getToken() != null && !structIGSticker.getToken().isEmpty()) {
            realmAttachment.setToken(structIGSticker.getToken());
        }
        realmAttachment.setId(identity);
        realmAttachment.setLocalFilePath(structIGSticker.getPath());

        realmAttachment.setWidth(imageSize[0]);
        realmAttachment.setHeight(imageSize[1]);
        realmAttachment.setSize(new File(structIGSticker.getPath()).length());
        realmAttachment.setName(new File(structIGSticker.getPath()).getName());
        realmAttachment.setDuration(0);

        roomMessage.setAttachment(realmAttachment);

        roomMessage.getAttachment().setToken(structIGSticker.getToken());
        roomMessage.setAuthorHash(RealmUserInfo.getCurrentUserAuthorHash());
        roomMessage.setShowMessage(true);
        roomMessage.setCreateTime(TimeUtils.currentLocalTime());

        new Thread(() -> DbManager.getInstance().doRealmTransaction(realm -> {
            realm.copyToRealmOrUpdate(roomMessage);
            RealmStickerItem stickerItem = realm.where(RealmStickerItem.class).equalTo("id", structIGSticker.getId()).findFirst();
            if (stickerItem != null && stickerItem.isValid()) {
                stickerItem.setRecent();
            }
        })).start();

        MessageObject messageObject = MessageObject.create(roomMessage);
        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).buildStoryReply(realmRoom.getType().getNumber(), realmRoom.getId(), stories.get(counter), messageObject, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (stories.size() == 1) {
            counter = 0;
        } else {
            counter = restorePosition();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        AndroidUtils.requestAdjustResize(getActivity(), getClass().getSimpleName());
        onResumeCalled = true;
        if (counter != 0) {
            counter = restorePosition();
            storiesProgressView.startStories(counter);
        } else {
            setUpUi();
            storiesProgressView.startStories();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        storiesProgressView.abandon();
        storiesProgressView.getCurrentProgressBar().setStarted(false);
        closeKeyboard(rootLayout);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (stories.size() <= counter + 1) {
            savePosition(0);
        } else {
            savePosition(counter);
        }
    }

    private void updateStory() {
        storyDisplayImage.setVisibility(View.VISIBLE);
        storyProgress.setVisibility(View.VISIBLE);
        storyVideoProgress.setVisibility(View.GONE);

        if (stories.get(counter).getTxt() == null || stories.get(counter).getTxt().trim().equals("") || stories.get(counter).getTxt().trim().isEmpty()) {
            captionRootView.setVisibility(View.GONE);
        } else if (stories.get(counter).getTxt() != null) {
            captionRootView.setVisibility(View.VISIBLE);
            if (stories.get(counter).getTxt().length() >= 100) {
                captionRootView.setTextSize(20);
            } else if (stories.get(counter).getTxt().length() >= 17) {
                captionRootView.setTextSize(28);
            } else {
                captionRootView.setTextSize(40);
            }
            captionRootView.setMovementMethod(ScrollingMovementMethod.getInstance());
            captionRootView.setText(stories.get(counter).getTxt());
        }

        storyTime.setText(LastSeenTimeUtil.computeTime(context, stories.get(counter).getUserId(), stories.get(counter).getStoryData() / 1000L, false, false));
        nickName.setText(stories.get(counter).getDisplayName());

        if (stories.get(counter).isRoom()) {
            chatIconTv.setVisibility(View.VISIBLE);
        } else {
            chatIconTv.setVisibility(View.GONE);
        }

        if (stories.get(counter).isVerified()) {
            verifyIconTv.setVisibility(View.VISIBLE);
        } else {
            verifyIconTv.setVisibility(View.GONE);
        }

        replyTo.setText(EmojiManager.getInstance().replaceEmoji(storyUser.getUserName(), replyTo.getPaint().getFontMetricsInt()) + " \u25CF " + context.getString(R.string.moments_string));
        replyCaption.setText(stories.get(counter).getTxt() != null ? stories.get(counter).getTxt() : "Photo");
        AttachmentObject attachmentObject = stories.get(counter).getAttachment();
        String path = attachmentObject.filePath != null ? attachmentObject.filePath : attachmentObject.thumbnailPath;


        if (stories.get(counter).getStoryId() != 0 && !getMessageDataStorage().isStorySeen(stories.get(counter).getStoryId())) {
            AbstractObject req = null;
            IG_RPC.Story_Add_View story_add_view = new IG_RPC.Story_Add_View();
            story_add_view.storyId = String.valueOf(stories.get(counter).getStoryId());
            req = story_add_view;
            getRequestManager().sendRequest(req, (response, error) -> {
                if (error == null) {
                    IG_RPC.Res_Story_Add_View res = (IG_RPC.Res_Story_Add_View) response;
                    getMessageDataStorage().storySetSeen(res.storyId);
                } else {
                    storyVideoProgress.setVisibility(View.GONE);
                }
            });
        }

        if (counter == (stories.size() - 1) && !getMessageDataStorage().isAllStorySeen(storyUser.getRoomId() == 0, storyUser.getRoomId() == 0 ? storyUser.getUserId() : storyUser.getRoomId())) {
            getMessageDataStorage().storySetSeenAll(storyUser.getRoomId() == 0 ? storyUser.getUserId() : storyUser.getRoomId(), true, storyUser.getRoomId() == 0);
            int storyUnSeenCount = getMessageDataStorage().getUnSeenStoryCount();
            if (storyUnSeenCount > 0) {
                G.onUnreadChange.onChange(storyUnSeenCount, true);
            } else {
                G.onUnreadChange.onChange(0, true);
            }
            EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_ALL_SEEN);
        }

        File file = new File(path != null ? path : "");
        storyDisplayImage.setImageBitmap(null);
        if (file.exists()) {
            loadImage(path);
        } else {
            path = attachmentObject.thumbnailPath != null ? attachmentObject.thumbnailPath : "";
            file = new File(path);
            if (file.exists()) {
                Story story = stories.get(counter);
                if (story.getAttachment().filePath != null && (new File(story.getAttachment().filePath).exists())) {
                    return;
                }
                Bitmap bitmap = AndroidUtils.blurImage(BitmapFactory.decodeFile(path));
                storyDisplayImage.setImageBitmap(bitmap);

            } else {
                ProtoFileDownload.FileDownload.Selector selector;
                if (attachmentObject.largeThumbnail != null) {
                    selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                    path = AndroidUtils.getFilePathWithCashId(attachmentObject.smallThumbnail.cacheId, attachmentObject.name, G.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), true);
                    if (new File(path).exists()) {
                        Story story = stories.get(counter);
                        if (story.getAttachment().filePath != null && (new File(story.getAttachment().filePath).exists())) {
                            return;
                        }
                        Bitmap bitmap = AndroidUtils.blurImage(BitmapFactory.decodeFile(path));
                        storyDisplayImage.setImageBitmap(bitmap);

                    } else {
                        DownloadObject downloadObject = DownloadObject.createForThumb(attachmentObject, ProtoGlobal.RoomMessageType.STORY.getNumber(), false);
                        if (downloadObject != null) {
                            downloadObject.downloadId = stories.get(counter).getStoryId();
                            getDownloader().download(downloadObject, selector, arg -> {
                                if (arg.status == Status.SUCCESS) {
                                    if (arg.data != null) {
                                        String filePath = arg.data.getFilePath();
                                        Story story = stories.get(counter);
                                        if (story.getStoryId() == arg.data.getDownloadObject().downloadId) {
                                            if (story.getAttachment().filePath != null && (new File(story.getAttachment().filePath).exists())) {
                                                return;
                                            }
                                            Bitmap bitmap = AndroidUtils.blurImage(BitmapFactory.decodeFile(filePath));
                                            G.runOnUiThread(() -> storyDisplayImage.setImageBitmap(bitmap));
                                        }
                                    }
                                }
                            });
                        }
                    }
                } else if (attachmentObject.smallThumbnail != null) {
                    selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                    path = AndroidUtils.getFilePathWithCashId(attachmentObject.smallThumbnail.cacheId, attachmentObject.name, G.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath(), true);
                    if (new File(path).exists()) {
                        Story story = stories.get(counter);
                        if (story.getAttachment().filePath != null && (new File(story.getAttachment().filePath).exists())) {
                            return;
                        }
                        Bitmap bitmap = AndroidUtils.blurImage(BitmapFactory.decodeFile(path));
                        storyDisplayImage.setImageBitmap(bitmap);
                    } else {
                        DownloadObject downloadObject = DownloadObject.createForThumb(attachmentObject, ProtoGlobal.RoomMessageType.STORY.getNumber(), false);
                        if (downloadObject != null) {
                            downloadObject.downloadId = stories.get(counter).getStoryId();
                            getDownloader().download(downloadObject, selector, arg -> {
                                if (arg.status == Status.SUCCESS) {
                                    if (arg.data != null) {
                                        String filePath = arg.data.getFilePath();
                                        Story story = stories.get(counter);
                                        if (story.getStoryId() == arg.data.getDownloadObject().downloadId) {
                                            if (story.getAttachment().filePath != null && (new File(story.getAttachment().filePath).exists())) {
                                                return;
                                            }
                                            Bitmap bitmap = AndroidUtils.blurImage(BitmapFactory.decodeFile(filePath));
                                            G.runOnUiThread(() -> storyDisplayImage.setImageBitmap(bitmap));
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }

            DownloadObject object = DownloadObject.createForStory(attachmentObject, stories.get(counter).getStoryId(), true);
            if (object != null) {
                ProtoFileDownload.FileDownload.Selector imageSelector = ProtoFileDownload.FileDownload.Selector.FILE;
                Downloader.getInstance(AccountManager.selectedAccount).download(object, imageSelector, HttpRequest.PRIORITY.PRIORITY_HIGH, arg -> {
                    if (arg.status == Status.SUCCESS && arg.data != null) {
                        String filepath = arg.data.getFilePath();
                        long downloadId = arg.data.getDownloadObject().downloadId;
                        Story story = stories.get(counter);
                        if (downloadId == story.getStoryId()) {
                            story.getAttachment().filePath = filepath;
                            G.runOnUiThread(() -> loadImage(filepath));
                        }
                    }
                });
            }
        }

    }


    private void loadImage(String path) {
        storyProgress.setVisibility(View.GONE);
        storyDisplayImage.setImageBitmap(BitmapFactory.decodeFile(path));
//        Glide.with(storyDisplayImage.getContext()).load(path).into(new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//
//            }
//        });
        avatarHandler.getAvatar(new ParamWithAvatarType(profileImage, stories.get(counter).isRoom() ? stories.get(counter).getRoomId() : stories.get(counter).getUserId()).avatarType(stories.get(counter).isRoom() ? AvatarHandler.AvatarType.ROOM : AvatarHandler.AvatarType.USER));
        Glide.with(replyThumb.getContext()).load(path).into(replyThumb);
        if (counter == 0 && downloadCounter == 0) {
            storiesProgressView.startStories(counter);
        } else {
            resumeCurrentStory();
        }
        if (isMyStory) {
            if (G.selectedLanguage.equals("en")) {
                seenCount.setText(stories.get(counter).getViewCount() + "");
            } else {
                seenCount.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(stories.get(counter).getViewCount())));
            }
        }
        downloadCounter++;

    }


    private void setUpUi() {

        OnSwipeTouchListener touchListener = new OnSwipeTouchListener(getActivity()) {

            @Override
            public void onSwipeTop() {
                if (!isMyStory && !isFormChat && !stories.get(counter).isRoom()) {
                    setupReply();
                }
            }

            @Override
            public void onSwipeBottom() {
                requireActivity().onBackPressed();
            }

            @Override
            public void onClick(View view) {
                if (keyboardViewVisible) {
                    hideStoryReply();
                    return;
                }
                if (!clickable) {
                    if (view == next) {
                        if (counter == stories.size() - 1) {
                            pageViewOperator.nextPageView(false);
                        } else {
                            storiesProgressView.skip();
                        }
                    }
                    if (view == previous) {
                        if (counter == 0) {
                            pageViewOperator.backPageView();
                        } else {
                            storiesProgressView.reverse();
                        }
                    }
                } else {
                    closeKeyboard(rootLayout);
                }
            }

            @Override
            public boolean onTouchView(View view, MotionEvent event) {
                super.onTouchView(view, event);
                if (!clickable) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            pressTime = System.currentTimeMillis();
                            pauseCurrentStory();
                            return false;
                        case MotionEvent.ACTION_UP:
                            showStoryOverlay();
                            if (keyboardViewVisible) {
                                return false;
                            }
                            updateStory();
                            return limit < System.currentTimeMillis() - pressTime;
                    }
                } else {
                    closeKeyboard(rootLayout);
                }


                return false;

            }

            @Override
            public void onLongClick() {
                if (!clickable)
                    hideStoryOverlay();
            }
        };

        previous.setOnTouchListener(touchListener);
        next.setOnTouchListener(touchListener);

        storiesProgressView.setStoriesCount(stories.size(), position = getArguments() != null ? getArguments().getInt(EXTRA_POSITION) : counter);
        storiesProgressView.setAllStoryDuration(StoryProgress.DEFAULT_PROGRESS_DURATION);
        storiesProgressView.setStoriesListener(this);
        storiesProgressView.setProgressListener(this);
    }

    private void hideStoryReply() {
        showPopup(-1);
        replyContainer.setVisibility(View.GONE);
        storyReplyContainer.setVisibility(View.VISIBLE);
        captionRootView.setVisibility(View.VISIBLE);
        updateStory();
    }

    public void showStoryOverlay() {
        if (storyOverlay == null || storyOverlay.getAlpha() != 0F)
            return;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(100);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(storyOverlay, View.ALPHA, 0, 1f),
                ObjectAnimator.ofFloat(storySeenContainer, View.ALPHA, 0, 1f),
                ObjectAnimator.ofFloat(captionRootView, View.ALPHA, 0, 1f),
                ObjectAnimator.ofFloat(storyReplyContainer, View.ALPHA, 0, 1f)
        );
        animatorSet.start();
    }

    public void hideStoryOverlay() {
        if (storyOverlay == null || storyOverlay.getAlpha() != 1F)
            return;

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(storyOverlay, View.ALPHA, 1f, 0),
                ObjectAnimator.ofFloat(storySeenContainer, View.ALPHA, 1f, 0),
                ObjectAnimator.ofFloat(captionRootView, View.ALPHA, 1f, 0),
                ObjectAnimator.ofFloat(storyReplyContainer, View.ALPHA, 1f, 0)
        );
        animatorSet.start();

    }

    private void setupReply() {
        chatMotionEvent(null);
        pauseCurrentStory();
        replyContentContainer.setVisibility(View.VISIBLE);
        replyContainer.setVisibility(View.VISIBLE);
        captionRootView.setVisibility(View.GONE);
        storyReplyContainer.setVisibility(View.GONE);
    }

    private void chatMotionEvent(MotionEvent event) {
        showPopup(KeyboardView.MODE_KEYBOARD);
        openKeyBoard();
        replyEditText.requestFocus();
    }

    private void getKeyboardState() {
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rec = new Rect();
            rootLayout.getWindowVisibleDisplayFrame(rec);
            int screenHeight = rootLayout.getRootView().getHeight();
            keyboardHeight = screenHeight - rec.bottom;

            if (keyboardHeight > screenHeight * 0.15) {
                replyContentContainer.setVisibility(View.VISIBLE);
                replyContainer.setVisibility(View.VISIBLE);
                storyReplyContainer.setVisibility(View.GONE);
                keyboardViewVisible = true;
                if (OpenKeyboard.incrementAndGet() == 1) {
                    keyboardStateChanged(keyboardViewVisible);
                }
                closeKeyboard = new AtomicInteger();
            } else {

                replyContentContainer.setVisibility(View.INVISIBLE);
//                replyContainer.setVisibility(View.INVISIBLE);
                storyReplyContainer.setVisibility(View.VISIBLE);
                keyboardViewVisible = false;
                if (closeKeyboard.incrementAndGet() == 1) {
                    keyboardStateChanged(keyboardViewVisible);
                }
                OpenKeyboard = new AtomicInteger();
            }
        });
    }

    private void keyboardStateChanged(boolean closeKeyboard) {
        if (!closeKeyboard) {
            closeKeyboard(rootLayout);
            updateStory();
            clickable = false;
        } else {
            pauseCurrentStory();
            clickable = true;
        }
    }

    public void savePosition(int pos) {
        getMessageDataStorage().storySetIndexOfSeen(storyUser.getUserId(), storyUser.getRoomId(), pos);
    }

    public int restorePosition() {
        return DbManager.getInstance().doRealmTask((DbManager.RealmTaskWithReturn<Integer>) realm -> {
            RealmStory realmStory = realm.where(RealmStory.class).equalTo(storyUser.getRoomId() != 0 ? "roomId" : "userId", storyUser.getRoomId() != 0 ? storyUser.getRoomId() : storyUser.getUserId()).findFirst();
            if (realmStory != null) {
                return realmStory.getIndexOfSeen();
            }
            return 0;
        });


    }

    public void pauseCurrentStory() {
        storiesProgressView.pause();
    }

    @Override
    public void onNext() {
        if (stories.size() <= counter + 1) {
            return;
        }
        ++counter;
        savePosition(counter);
    }

    @Override
    public void onPrev() {
        if (counter - 1 < 0)
            return;
        --counter;
        savePosition(counter);
    }

    @Override
    public void onComplete() {
        savePosition(counter);
        pageViewOperator.nextPageView(clickable);
    }

    public void resumeCurrentStory() {
        if (onResumeCalled) {
            showStoryOverlay();
            storiesProgressView.resume();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (keyboardViewVisible) {
            hideStoryReply();
            return false;
        }
        return true;
    }

    @Override
    public void progressStarted(int current) {
        pauseCurrentStory();
        storyProgress.setVisibility(View.VISIBLE);
        updateStory();
    }

    @Override
    public void onCancel() {
        resumeCurrentStory();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public interface PageViewOperator {
        void backPageView();

        void nextPageView(boolean clickable);
    }


    private class ListAdapter extends RecyclerListView.SelectionAdapter {


        public void addRow() {
            rowSize = 0;

            for (int i = 0; i < 5; i++) {
                userRow = rowSize++;
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView;
            cellView = new StoryCell(context);
            return new RecyclerListView.Holder(cellView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            StoryCell storyCell = (StoryCell) holder.itemView;
            storyCell.initView(context, true, StoryCell.CircleStatus.CIRCLE_IMAGE, ImageLoadingView.Status.LOADING, null, 0);
            storyCell.setStatus(StoryCell.CircleStatus.CIRCLE_IMAGE);
            storyCell.setText(getString(R.string.my_status), getString(R.string.tap_to_add_status_update));
            storyCell.setImage(R.drawable.avatar, avatarHandler);
            storyCell.addIconVisibility(true);
            storyCell.deleteIconVisibility(false);

        }

        @Override
        public int getItemCount() {
            return rowSize;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType != 2;
        }
    }
}
