package net.iGap.story.viewPager;

import android.content.pm.ActivityInfo;
import android.graphics.Rect;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.AndroidUtils;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.customView.EventEditText;
import net.iGap.module.downloader.DownloadObject;
import net.iGap.module.downloader.Downloader;
import net.iGap.module.downloader.HttpRequest;
import net.iGap.module.downloader.Status;
import net.iGap.network.AbstractObject;
import net.iGap.network.IG_RPC;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.realm.RealmAttachment;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;
import net.iGap.story.ExpandableTextView;
import net.iGap.structs.AttachmentObject;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class StoryDisplayFragment extends BaseFragment implements StoriesProgressView.StoriesListener {
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
    private LinearLayout llReplay, llAttach, llSend;
    private EventEditText edtChat;
    private FrameLayout tvSend;
    private ConstraintLayout constraintLayout;
    private ExpandableTextView expandableTextView;
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
        llSend = rootView.findViewById(R.id.ll_chatRoom_send);
        edtChat = rootView.findViewById(R.id.et_chatRoom_writeMessage);
        tumNailImage = rootView.findViewById(R.id.story_image);
        tvSend = rootView.findViewById(R.id.chatRoom_send_container);
        constraintLayout = rootView.findViewById(R.id.root_display);
        expandableTextView = rootView.findViewById(R.id.caption_text_sub_view);
        progressBar = rootView.findViewById(R.id.storyDisplayVideoProgress);
        storyOverlay = rootView.findViewById(R.id.storyOverlay);
        loadingProgressbar = rootView.findViewById(R.id.display_story_progress_bar);
        storyViewsRootView = rootView.findViewById(R.id.story_display_seen_views_root_view);
        storyViewsCount = rootView.findViewById(R.id.story_display_views_count);


        if (getArguments() != null) {
            position = getArguments().getInt(EXTRA_POSITION);
            storyUser = (StoryUser) getArguments().getSerializable(EXTRA_STORY_USER);
            stories = storyUser.getStories();
        }
        isMyStory = storyUser.getUserId() == AccountManager.getInstance().getCurrentUser().getId();
        if (isMyStory) {
            replayFrame.setVisibility(View.INVISIBLE);
            storyViewsRootView.setVisibility(View.VISIBLE);
        } else {
            replayFrame.setVisibility(View.VISIBLE);
            storyViewsRootView.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        counter = 0;
        setUpUi();
        loadingProgressbar.setVisibility(View.VISIBLE);
        replayFrame.setOnClickListener(view1 -> {
            setupReplay();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        counter = restorePosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AndroidUtils.requestAdjustResize(getActivity(), getClass().getSimpleName());
        updateStory();
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
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        storiesProgressView.abandon();
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
        progressBar.setVisibility(View.GONE);

        if (stories.get(counter).getTxt() != null) {
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
        RealmAttachment ra = stories.get(counter).getAttachment();
        String path = ra.getLocalFilePath() != null ? ra.getLocalFilePath() : "";

        File file = new File(path);
        if (file.exists()) {
            loadImage(path);
        } else {
            path = ra.getLocalThumbnailPath() != null ? ra.getLocalThumbnailPath() : "";
            file = new File(path);
            if (file.exists()) {
                loadImage(path);
            } else {
                // if thumpnail not exist download it
                ProtoFileDownload.FileDownload.Selector selector = null;
                long fileSize = 0;

                if (ra.getSmallThumbnail() != null) {
                    selector = ProtoFileDownload.FileDownload.Selector.SMALL_THUMBNAIL;
                    fileSize = ra.getSmallThumbnail().getSize();
                } else if (ra.getLargeThumbnail() != null) {
                    selector = ProtoFileDownload.FileDownload.Selector.LARGE_THUMBNAIL;
                    fileSize = ra.getLargeThumbnail().getSize();
                }

                if (selector != null && fileSize > 0) {

                    DownloadObject object = DownloadObject.createForStoryAvatar(AttachmentObject.create(ra), true);
                    if (object != null) {
                        ProtoFileDownload.FileDownload.Selector imageSelector = ProtoFileDownload.FileDownload.Selector.FILE;
                        Downloader.getInstance(AccountManager.selectedAccount).download(object, imageSelector, HttpRequest.PRIORITY.PRIORITY_HIGH, arg -> {
                            if (arg.status == Status.SUCCESS && arg.data != null) {
                                String filepath = arg.data.getFilePath();
                                String downloadedFileToken = arg.data.getToken();

                                if (!(new File(filepath).exists())) {
                                    HelperLog.getInstance().setErrorLog(new Exception("File Dont Exist After Download !!" + filepath));
                                }

                                G.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadImage(filepath);
                                    }
                                });


                            }

                        });
                    }
                }
            }
        }

    }


    private void loadImage(String path) {
        Log.e("faslkfhsfhsakjd", "loadImage: " + downloadCounter);

        loadingProgressbar.setVisibility(View.GONE);
        Glide.with(storyDisplayImage.getContext()).load(path).into(storyDisplayImage);
        avatarHandler.getAvatar(new ParamWithAvatarType(userImage, stories.get(counter).getUserId()).avatarType(AvatarHandler.AvatarType.USER));
        Glide.with(tumNailImage.getContext()).load(path).into(tumNailImage);

        if (counter == 0 && downloadCounter == 0) {
            storiesProgressView.startStories(counter);
        } else {
            resumeCurrentStory();
        }
        if (isMyStory) {
            storyViewsCount.setText(stories.get(counter).getViewCount() + "");
        }

        AbstractObject req = null;
        IG_RPC.Story_Add_View story_add_view = new IG_RPC.Story_Add_View();
        story_add_view.storyId = String.valueOf(stories.get(counter).getStoryId());
        req = story_add_view;
        getRequestManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                IG_RPC.Res_Story_Add_View res = (IG_RPC.Res_Story_Add_View) response;
                DbManager.getInstance().doRealmTransaction(realm -> {
                    realm.where(RealmStoryProto.class).equalTo("storyId", Long.valueOf(res.storyId)).findFirst().setSeen(true);
                });

            } else {
                progressBar.setVisibility(View.GONE);
            }
        });


        downloadCounter++;

        if (downloadCounter == stories.size()) {
            DbManager.getInstance().doRealmTransaction(realm -> {
                RealmStory.putOrUpdate(realm, stories.get(counter).getUserId(), true);
            });
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
                            resumeCurrentStory();
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
            resumeCurrentStory();
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
                    llSend.setVisibility(View.GONE);
                } else {
                    tvSend.setVisibility(View.GONE);
                    llSend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }

    public void savePosition(int pos) {
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmStory realmStory = realm.where(RealmStory.class).equalTo("id", storyUser.getUserId()).findFirst();
            if (realmStory != null) {
                realmStory.setIndexOfSeen(pos);
            }
        });
    }

    public int restorePosition() {
        return DbManager.getInstance().doRealmTask((DbManager.RealmTaskWithReturn<Integer>) realm -> {
            RealmStory realmStory = realm.where(RealmStory.class).equalTo("id", storyUser.getUserId()).findFirst();
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
        Log.e("faslkfhsfhsakjd", "onNext: ");
        pauseCurrentStory();
        loadingProgressbar.setVisibility(View.VISIBLE);
        updateStory();
    }

    @Override
    public void onPrev() {
        if (counter - 1 < 0)
            return;
        --counter;
        savePosition(counter);
        pauseCurrentStory();
        updateStory();
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

    public interface PageViewOperator {
        void backPageView();

        void nextPageView(boolean clickable);
    }

}
