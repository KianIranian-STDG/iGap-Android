package net.iGap.story.viewPager;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.AndroidUtils;
import net.iGap.module.ChatSendMessageUtil;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
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
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;
import net.iGap.story.ExpandableTextView;
import net.iGap.story.ViewUserDialogFragment;
import net.iGap.story.liststories.ImageLoadingView;
import net.iGap.story.storyviews.StoryCell;
import net.iGap.structs.AttachmentObject;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;

public class StoryDisplayFragment extends BaseFragment implements StoriesProgressView.StoriesListener, StoriesProgressView.StoryProgressListener, RecyclerListView.OnItemClickListener, ViewUserDialogFragment.ViewUserDialogState {
    private static final String EXTRA_POSITION = "EXTRA_POSITION";
    private static final String EXTRA_STORY_USER = "EXTRA_STORY_USER";
    private static final String EXTRA_IS_MY_STORY = "EXTRA_IS_MY_STORY";
    private int position;
    private StoryUser storyUser;
    private List<Story> stories;
    private long pressTime = 0L;
    private long limit = 500L;
    private int counter = 0;
    private int downloadCounter = 0;
    private boolean keyboardViewVisible;
    private boolean onResumeCalled = false;
    private StoriesProgressView storiesProgressView;
    private PageViewOperator pageViewOperator;
    private TextView userName, storyTime, nameFrom, descriptionFrom;
    private AppCompatImageView storyDisplayImage, userImage, tumNailImage;
    private View previous, next;
    private ConstraintLayout replayFrame;
    private LinearLayout llReplay, llAttach;
    private EventEditText edtChat;
    private FrameLayout tvSend;
    private ConstraintLayout constraintLayout;
    private ExpandableTextView expandableTextView;
    private LinearLayout captionRootView;
    private ProgressBar progressBar;
    private AtomicInteger OpenKeyboard = new AtomicInteger();
    private AtomicInteger closeKeyboard = new AtomicInteger();
    private View rootView;
    private LinearLayout storyViewsRootView;
    private TextView storyViewsCount;
    private boolean clickable;
    private ConstraintLayout storyOverlay;
    private ProgressBar loadingProgressbar;
    boolean isMyStory = false;
    private LinearLayout mBottomSheetLayout;
    private CoordinatorLayout mBottomSheetLayoutRootView;
    private BottomSheetBehavior sheetBehavior;
    private RecyclerView userViewsRecycler;
    int rowSize = 0;
    int userRow = 0;
    private Realm realm = Realm.getInstance(AccountManager.getInstance().getCurrentUser().getRealmConfiguration());

    public static StoryDisplayFragment newInstance(int position, StoryUser storyModel, boolean isMyStory) {

        Bundle args = new Bundle();
        StoryDisplayFragment fragment = new StoryDisplayFragment();
        args.putInt(EXTRA_POSITION, position);
        args.putSerializable(EXTRA_STORY_USER, storyModel);
        args.putBoolean(EXTRA_IS_MY_STORY, isMyStory);
        fragment.setArguments(args);

        return fragment;
    }

    public void setPageViewOperator(PageViewOperator pageViewOperator) {
        this.pageViewOperator = pageViewOperator;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        rootView = inflater.inflate(R.layout.fragment_story_display, container, false);

        storiesProgressView = rootView.findViewById(R.id.storiesProgressView);
        storyDisplayImage = rootView.findViewById(R.id.storyDisplayImage);
        userName = rootView.findViewById(R.id.storyDisplayNick);
        userImage = rootView.findViewById(R.id.storyDisplayProfilePicture);
        storyTime = rootView.findViewById(R.id.storyDisplayTime);
        previous = rootView.findViewById(R.id.previous);
        next = rootView.findViewById(R.id.next);
        replayFrame = rootView.findViewById(R.id.frame_story_replay);
        llReplay = rootView.findViewById(R.id.ll_replay_story);
        nameFrom = rootView.findViewById(R.id.nameFrom);
        descriptionFrom = rootView.findViewById(R.id.descriptionFrom);
        llAttach = rootView.findViewById(R.id.layout_attach_story);
        edtChat = rootView.findViewById(R.id.et_chatRoom_writeMessage);
        tumNailImage = rootView.findViewById(R.id.story_image);
        tvSend = rootView.findViewById(R.id.chatRoom_send_container);
        constraintLayout = rootView.findViewById(R.id.root_display);
        expandableTextView = rootView.findViewById(R.id.caption_text_sub_view);
        captionRootView = rootView.findViewById(R.id.caption_text_root_view);
        progressBar = rootView.findViewById(R.id.storyDisplayVideoProgress);
        storyOverlay = rootView.findViewById(R.id.storyOverlay);
        loadingProgressbar = rootView.findViewById(R.id.display_story_progress_bar);
        storyViewsRootView = rootView.findViewById(R.id.story_display_seen_views_root_view);
        storyViewsCount = rootView.findViewById(R.id.story_display_views_count);
        mBottomSheetLayoutRootView = rootView.findViewById(R.id.display_bottom_sheet_story);
        mBottomSheetLayout = rootView.findViewById(R.id.story_bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        userViewsRecycler = rootView.findViewById(R.id.story_bottom_sheet_recycler);


        if (getArguments() != null) {
            position = getArguments().getInt(EXTRA_POSITION);
            storyUser = (StoryUser) getArguments().getSerializable(EXTRA_STORY_USER);
            stories = storyUser.getStories();
        }
        isMyStory = storyUser.getUserId() == AccountManager.getInstance().getCurrentUser().getId();
        if (isMyStory) {
            replayFrame.setVisibility(View.INVISIBLE);
            storyViewsRootView.setVisibility(View.VISIBLE);
            if (stories.get(counter).getViewCount() > 0) {
                mBottomSheetLayoutRootView.setVisibility(View.VISIBLE);
            }

        } else {
            replayFrame.setVisibility(View.VISIBLE);
            storyViewsRootView.setVisibility(View.INVISIBLE);
            mBottomSheetLayoutRootView.setVisibility(View.GONE);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        counter = 0;
        setUpUi();
        replayFrame.setOnClickListener(view1 -> {
            setupReplay();
        });

        tvSend.setOnClickListener(view1 -> {
            String replyText = Objects.requireNonNull(edtChat.getText()).toString().trim();
            if (replyText.length() == 0) {
                edtChat.setText("");
                Toast.makeText(context, R.string.please_write_your_message, Toast.LENGTH_LONG).show();
            } else if (!checkEmptyMessageWithSemiSpace(new String[]{replyText})) {
                return;
            }
            edtChat.setText("");
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
                        ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).buildStoryReply(res.room.getTypeValue(), res.room.getId(), stories.get(counter), edtChat.getText().toString());
                    }
                });

            } else {
                ChatSendMessageUtil.getInstance(AccountManager.selectedAccount).buildStoryReply(realmRoom.getType().getNumber(), realmRoom.getId(), stories.get(counter), replyText);
                Toast.makeText(context, getString(R.string.reply_sent), Toast.LENGTH_SHORT).show();
            }

        });

        storyViewsRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMyStory && stories.get(counter).getViewCount() > 0) {
                    ViewUserDialogFragment blankFragment = new ViewUserDialogFragment(stories.get(counter).getViewCount(), stories.get(counter).getUserIdList());
                    blankFragment.setViewUserDialogState(StoryDisplayFragment.this::onCancel);
                    blankFragment.show(getParentFragmentManager(), blankFragment.getTag());
                    pauseCurrentStory();
                }

            }
        });
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
        AndroidUtils.requestAdjustResize(getActivity(), getClass().getSimpleName());
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
        closeKeyboard(rootView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AndroidUtils.removeAdjustResize(getActivity(), getClass().getSimpleName());
        if (stories.size() <= counter + 1) {
            savePosition(0);
        } else {
            savePosition(counter);
        }
    }

    private void updateStory() {
        storyDisplayImage.setVisibility(View.VISIBLE);
        loadingProgressbar.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        if (stories.get(counter).getTxt() == null || stories.get(counter).getTxt().trim().equals("") || stories.get(counter).getTxt().trim().isEmpty()) {
            captionRootView.setVisibility(View.GONE);
            expandableTextView.setVisibility(View.GONE);
        } else if (stories.get(counter).getTxt() != null) {
            captionRootView.setVisibility(View.VISIBLE);
            expandableTextView.setVisibility(View.VISIBLE);
            if (stories.get(counter).getTxt().length() >= 100) {
                expandableTextView.setTextSize(20);
            } else if (stories.get(counter).getTxt().length() >= 17) {
                expandableTextView.setTextSize(28);
            } else {
                expandableTextView.setTextSize(40);
            }
            expandableTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
            expandableTextView.setText(stories.get(counter).getTxt());
        }

        storyTime.setText(HelperCalander.getTimeForMainRoom(stories.get(counter).getStoryData()));
        userName.setText(storyUser.getUserName());
        nameFrom.setText(storyUser.getUserName());
        descriptionFrom.setText(stories.get(counter).getTxt() != null ? stories.get(counter).getTxt() : "Photo");
        AttachmentObject ra = stories.get(counter).getAttachment();
        String path = ra.filePath != null ? ra.filePath : ra.thumbnailPath;

        File file = new File(path != null ? path : "");
        if (file.exists()) {
            loadImage(path);
        } else {
            path = ra.thumbnailPath != null ? ra.thumbnailPath : "";
            file = new File(path);
            if (file.exists()) {
                loadImage(path);
            } else {
                // if thumpnail not exist download it
                ProtoFileDownload.FileDownload.Selector selector = null;
                long fileSize = 0;

                if (ra.largeThumbnail != null) {
                    selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                    fileSize = ra.largeThumbnail.size;
                    path = AndroidUtils.getFilePathWithCashId(ra.smallThumbnail.cacheId, ra.name, G.DIR_IMAGES, true);
                    if (new File(path).exists()) {
                        String finalPath = path;
                        if (!(new File(stories.get(counter).getAttachment().filePath).exists())) {
                            G.runOnUiThread(() -> Glide.with(storyDisplayImage.getContext()).load(finalPath).transform(new BlurTransformation(getContext())).dontAnimate().into(storyDisplayImage));
                        }
                    } else {
                        DownloadObject downloadObject = DownloadObject.createForThumb(ra, ProtoGlobal.RoomMessageType.STORY.getNumber(), false);
                        if (downloadObject != null) {
                            getDownloader().download(downloadObject, selector, arg -> {
                                switch (arg.status) {
                                    case SUCCESS:
                                        String filePath = arg.data.getFilePath();
                                        if (!(new File(stories.get(counter).getAttachment().filePath).exists())) {
                                            G.runOnUiThread(() -> Glide.with(storyDisplayImage.getContext()).load(filePath).transform(new BlurTransformation(getContext())).dontAnimate().into(storyDisplayImage));
                                        }
                                        break;
                                }
                            });
                        }
                    }
                } else if (ra.smallThumbnail != null) {
                    selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                    fileSize = ra.smallThumbnail.size;
                    path = AndroidUtils.getFilePathWithCashId(ra.smallThumbnail.cacheId, ra.name, G.DIR_IMAGES, true);
                    if (new File(path).exists()) {
                        String finalPath = path;
                        if (!(new File(stories.get(counter).getAttachment().filePath).exists())) {
                            G.runOnUiThread(() -> Glide.with(storyDisplayImage.getContext()).load(finalPath).transform(new BlurTransformation(getContext())).dontAnimate().into(storyDisplayImage));
                        }
                    } else {
                        DownloadObject downloadObject = DownloadObject.createForThumb(ra, ProtoGlobal.RoomMessageType.STORY.getNumber(), false);
                        if (downloadObject != null) {
                            getDownloader().download(downloadObject, selector, arg -> {
                                switch (arg.status) {
                                    case SUCCESS:
                                        String filePath = arg.data.getFilePath();
                                        if (!(new File(stories.get(counter).getAttachment().filePath).exists())) {
                                            G.runOnUiThread(() -> Glide.with(storyDisplayImage.getContext()).load(filePath).transform(new BlurTransformation(getContext())).dontAnimate().into(storyDisplayImage));
                                        }
                                        break;
                                }
                            });
                        }
                    }
                }

                if (selector != null && fileSize > 0) {

                    DownloadObject object = DownloadObject.createForStoryAvatar(ra, true);
                    if (object != null) {
                        ProtoFileDownload.FileDownload.Selector imageSelector = ProtoFileDownload.FileDownload.Selector.FILE;
                        Downloader.getInstance(AccountManager.selectedAccount).download(object, imageSelector, HttpRequest.PRIORITY.PRIORITY_HIGH, arg -> {
                            if (arg.status == Status.SUCCESS && arg.data != null) {
                                String filepath = arg.data.getFilePath();
                                String downloadedFileToken = arg.data.getToken();
                                if (!(new File(filepath).exists())) {
                                    HelperLog.getInstance().setErrorLog(new Exception("File Dont Exist After Download !!" + filepath));
                                }
                                if (filepath.equals(stories.get(counter).getAttachment().filePath)) {
                                    G.runOnUiThread(() -> loadImage(filepath));
                                }

                            }
                        });
                    }
                } else {
                    Glide.with(storyDisplayImage.getContext()).load("").into(storyDisplayImage);
                    avatarHandler.getAvatar(new ParamWithAvatarType(userImage, stories.get(counter).getUserId()).avatarType(AvatarHandler.AvatarType.USER));
                    Glide.with(tumNailImage.getContext()).load("").into(tumNailImage);
                }
            }
        }

    }


    private void loadImage(String path) {
        loadingProgressbar.setVisibility(View.GONE);
        Glide.with(storyDisplayImage.getContext()).load(path).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                storyDisplayImage.setImageDrawable(resource);
            }
        });
        avatarHandler.getAvatar(new ParamWithAvatarType(userImage, stories.get(counter).getUserId()).avatarType(AvatarHandler.AvatarType.USER));

        if (counter == 0 && downloadCounter == 0) {
            storiesProgressView.startStories(counter);
        } else {
            resumeCurrentStory();
        }
        if (isMyStory) {
            if (G.selectedLanguage.equals("en")) {
                storyViewsCount.setText(stories.get(counter).getViewCount() + "");
            } else {
                storyViewsCount.setText(HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(stories.get(counter).getViewCount())));
            }
        }

        if (stories.get(counter).getStoryId() != 0) {
            AbstractObject req = null;
            IG_RPC.Story_Add_View story_add_view = new IG_RPC.Story_Add_View();
            story_add_view.storyId = String.valueOf(stories.get(counter).getStoryId());
            req = story_add_view;
            getRequestManager().sendRequest(req, (response, error) -> {
                if (error == null) {
                    IG_RPC.Res_Story_Add_View res = (IG_RPC.Res_Story_Add_View) response;
                    realm.beginTransaction();
                    realm.where(RealmStoryProto.class).equalTo("storyId", Long.valueOf(res.storyId)).findFirst().setSeen(true);
                    realm.commitTransaction();


                } else {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }


        downloadCounter++;

        if (downloadCounter == stories.size()) {
            realm.beginTransaction();
            RealmStory.putOrUpdate(realm, stories.get(counter).getUserId(), true);
            realm.commitTransaction();

            EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.STORY_ALL_SEEN);
        }
    }


    private void setUpUi() {

        OnSwipeTouchListener touchListener = new OnSwipeTouchListener(getActivity()) {

            @Override
            public void onSwipeTop() {
                if (!isMyStory) {
                    setupReplay();
                }
            }

            @Override
            public void onSwipeBottom() {
                new HelperFragment(getActivity().getSupportFragmentManager(), StoryDisplayFragment.this).popBackStack();

            }

            @Override
            public void onClick(View view) {
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
                    closeKeyboard(rootView);
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
                            updateStory();
                            return limit < System.currentTimeMillis() - pressTime;
                    }
                } else {
                    closeKeyboard(rootView);
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

    public void showStoryOverlay() {
        if (storyOverlay == null || storyOverlay.getAlpha() != 0F)
            return;
        storyOverlay.animate()
                .setDuration(100)
                .alpha(1F)
                .start();
    }

    public void hideStoryOverlay() {
        if (storyOverlay == null || storyOverlay.getAlpha() != 1F)
            return;
        storyOverlay.animate()
                .setDuration(200)
                .alpha(0F)
                .start();

    }

    private void setupReplay() {
        openKeyBoard();
        edtChat.requestFocus();
        getKeyboardState();
        pauseCurrentStory();

    }

    private void getKeyboardState() {
        constraintLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rec = new Rect();
            constraintLayout.getWindowVisibleDisplayFrame(rec);
            int screenHeight = constraintLayout.getRootView().getHeight();
            int keypadHeight = screenHeight - rec.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                llReplay.setVisibility(View.VISIBLE);
                llAttach.setVisibility(View.VISIBLE);
                replayFrame.setVisibility(View.GONE);
                keyboardViewVisible = true;
                if (OpenKeyboard.incrementAndGet() == 1) {
                    keyboardStateChanged(keyboardViewVisible);
                }
                closeKeyboard = new AtomicInteger();
                setEdtChat();
            } else {

                llReplay.setVisibility(View.INVISIBLE);
                llAttach.setVisibility(View.INVISIBLE);
                replayFrame.setVisibility(View.VISIBLE);
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
            closeKeyboard(rootView);
            updateStory();
            clickable = false;
        } else {
            pauseCurrentStory();
            clickable = true;
        }
    }

    private void setEdtChat() {
        edtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtChat.getText() != null && edtChat.getText().length() > 0) {
                    tvSend.setVisibility(View.VISIBLE);
                } else {
                    tvSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }

    public void savePosition(int pos) {
        realm.beginTransaction();
        RealmStory realmStory = realm.where(RealmStory.class).equalTo("userId", storyUser.getUserId()).findFirst();
        if (realmStory != null) {
            realmStory.setIndexOfSeen(pos);
        }
        realm.commitTransaction();

    }

    public int restorePosition() {
        return DbManager.getInstance().doRealmTask((DbManager.RealmTaskWithReturn<Integer>) realm -> {
            RealmStory realmStory = realm.where(RealmStory.class).equalTo("userId", storyUser.getUserId()).findFirst();
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
    public void progressStarted(int current) {
        pauseCurrentStory();
        loadingProgressbar.setVisibility(View.VISIBLE);
        updateStory();
    }

    @Override
    public void onClick(View view, int position) {

    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void onCancel() {
        resumeCurrentStory();
    }

    public interface PageViewOperator {
        void backPageView();

        void nextPageView(boolean clickable);
    }


    private class ListAdapter extends RecyclerListView.ItemAdapter {


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
            return new RecyclerListView.ItemViewHolder(cellView, StoryDisplayFragment.this);
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
        public boolean isEnable(RecyclerView.ViewHolder holder, int viewType, int position) {
            return viewType != 2;
        }
    }
}
