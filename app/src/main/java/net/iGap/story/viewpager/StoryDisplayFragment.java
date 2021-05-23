package net.iGap.story.viewpager;

import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.module.AndroidUtils;
import net.iGap.module.customView.EventEditText;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StoryDisplayFragment extends BaseFragment implements StoriesProgressView.StoriesListener {
    private static String EXTRA_POSITION = "EXTRA_POSITION";
    private static String EXTRA_STORY_USER = "EXTRA_STORY_USER";
    private int position;
    private StoryUser storyUser;
    private List<Story> stories;
    private long pressTime = 0L;
    private long limit = 500L;
    private boolean onResumeCalled = false;
    private boolean onVideoPrepared = false;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView storyDisplayVideo;
    private static PageViewOperator pageViewOperator;
    private StoriesProgressView storiesProgressView;
    private TextView storyDisplayNick;
    private TextView storyDisplayTime;
    private ConstraintLayout storyOverlay;
    public AppCompatImageView storyDisplayImage, storyDisplayProfilePicture;
    private ProgressBar storyDisplayVideoProgress;
    private View previous, next;
    private int counter = 0;
    TrackSelector trackSelector;
    private ConstraintLayout replayFrame;
    private LinearLayout llReplay, llAttach, llSend;
    private EventEditText edtChat;
    private ImageView storyImage;
    private FrameLayout tvSend;
    private ConstraintLayout constraintLayout;

    public static StoryDisplayFragment newInstance(int position, StoryUser story) {

        Bundle args = new Bundle();

        StoryDisplayFragment fragment = new StoryDisplayFragment(pageViewOperator);
        args.putInt(EXTRA_POSITION, position);
        args.putSerializable(EXTRA_STORY_USER, story);
        fragment.setArguments(args);
        return fragment;
    }

    public StoryDisplayFragment(PageViewOperator pageViewOperator) {
        this.pageViewOperator = pageViewOperator;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_story_display, container, false);
        assert getArguments() != null;
        position = getArguments().getInt(EXTRA_POSITION);
        storyUser = (StoryUser) getArguments().getSerializable(EXTRA_STORY_USER);
        stories = storyUser.getStories();
        storyDisplayVideo = view.findViewById(R.id.storyDisplayVideo);
        storiesProgressView = view.findViewById(R.id.storiesProgressView);
        storyDisplayImage = view.findViewById(R.id.storyDisplayImage);
        storyDisplayNick = view.findViewById(R.id.storyDisplayNick);
        storyDisplayProfilePicture = view.findViewById(R.id.storyDisplayProfilePicture);
        storyDisplayVideoProgress = view.findViewById(R.id.storyDisplayVideoProgress);
        storyDisplayTime = view.findViewById(R.id.storyDisplayTime);
        storyOverlay = view.findViewById(R.id.storyOverlay);
        previous = view.findViewById(R.id.previous);
        next = view.findViewById(R.id.next);
        replayFrame = view.findViewById(R.id.frame_story_replay);
        llReplay = view.findViewById(R.id.ll_replay_story);
        llAttach = view.findViewById(R.id.layout_attach_story);
        llSend = view.findViewById(R.id.ll_chatRoom_send);
        trackSelector = new DefaultTrackSelector();
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(view.getContext(), trackSelector);
        edtChat = view.findViewById(R.id.et_chatRoom_writeMessage);
        storyImage = view.findViewById(R.id.story_image);
        tvSend = view.findViewById(R.id.chatRoom_send_container);
        constraintLayout = view.findViewById(R.id.root_display);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        storyDisplayVideo.setUseController(false);
        replayFrame.setOnClickListener(view1 -> reply(true));
        tvSend.setOnClickListener(view12 -> {
            closeKeyboard(view);
            resumeCurrentStory();
            reply(false);
        });
        getKeyboardState();
        updateStory();
        setUpUi();
    }

    public void updateStory() {
        simpleExoPlayer.stop();
        if (stories.get(counter).isVideo()) {
            storyDisplayVideo.setVisibility(View.VISIBLE);
            storyDisplayImage.setVisibility(View.GONE);
            storyDisplayVideoProgress.setVisibility(View.VISIBLE);
            initializePlayer();
        } else {
            storyDisplayVideo.setVisibility(View.GONE);
            storyDisplayVideoProgress.setVisibility(View.GONE);
            storyDisplayImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(stories.get(counter).getUrl()).into(storyDisplayImage);

        }

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(stories.get(counter).getStoryData());
        storyDisplayTime.setText(DateFormat.format(" HH:mm", cal).toString());
    }

    public void setUpUi() {
        OnSwipeTouchListener touchListener = new OnSwipeTouchListener(getActivity()) {

            @Override
            public void onSwipeTop() {
                Toast.makeText(getActivity(), "onSwipeTop", Toast.LENGTH_LONG).show();
                new HelperFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), StoryDisplayFragment.this).popBackStack();
            }

            @Override
            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "onSwipeBottom", Toast.LENGTH_LONG).show();
                new HelperFragment(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), StoryDisplayFragment.this).popBackStack();

            }

            @Override
            public void onClick(View view) {

                if (view == next) {
                    if (counter == stories.size() - 1) {
                        pageViewOperator.nextPageView();
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
            }

            @Override
            public void onLongClick() {
                hideStoryOverlay();
            }


            @Override
            public boolean onTouchView(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pauseCurrentStory();
                        pressTime = System.currentTimeMillis();
                        return false;
                    case MotionEvent.ACTION_UP:
                        showStoryOverlay();
                        resumeCurrentStory();
                        return limit < System.currentTimeMillis() - pressTime;
                }
                return false;
            }
        };

        previous.setOnTouchListener(touchListener);
        next.setOnTouchListener(touchListener);

        storiesProgressView.setStoriesCountDebug(stories.size());
        storiesProgressView.setAllStoryDuration(4000L);
        storiesProgressView.setStoriesListener(this);

        Glide.with(this).load(storyUser.getProfilePicUrl()).circleCrop().into(storyDisplayProfilePicture);
        Glide.with(this).load(stories.get(counter).getUrl()).into(storyImage);
        storyDisplayNick.setText(storyUser.getUserName());

    }

    /**
     * replay layout clicked
     */
    private void reply(boolean visibility) {
        if (visibility) {
           openKeyBoard();
        } else {
          closeKeyboard(edtChat);
        }

    }

    /**
     * detect keyboard is show or no
     */
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
                setEdtChat();
            } else {
                llReplay.setVisibility(View.GONE);
                llAttach.setVisibility(View.GONE);
                replayFrame.setVisibility(View.VISIBLE);
            }
        });
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


    @Override
    public void onStart() {
        super.onStart();
        counter = restorePosition();
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidUtils.requestAdjustResize(getActivity(), getClass().getSimpleName());
        onResumeCalled = true;
        if (stories.get(counter).isVideo() && !onVideoPrepared) {
            if (simpleExoPlayer == null) {
                simpleExoPlayer.setPlayWhenReady(false);
                return;
            }
        }
        if (simpleExoPlayer == null)
            simpleExoPlayer.seekTo(5);
        if (counter == 0) {
            storiesProgressView.startStories();
        } else {
            assert getArguments() != null;
            counter = StoryViewFragment.progressStateArray.get(getArguments().getInt(EXTRA_POSITION));
            storiesProgressView.startStories(counter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        simpleExoPlayer.setPlayWhenReady(false);
        storiesProgressView.abandon();
    }

    public void initializePlayer() {
        if (simpleExoPlayer == null) {
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext());
        } else {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(requireContext());
        }
        mediaDataSourceFactory = new CacheDataSourceFactory(
                StoryApp.simpleCache,
                new DefaultHttpDataSourceFactory(
                        Util.getUserAgent(
                                getContext(),
                                Util.getUserAgent(requireContext(), getString(R.string.app_name))
                        )
                )
        );

        ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
                Uri.parse(stories.get(counter).getUrl())
        );

        simpleExoPlayer.prepare(mediaSource, false, false);
        if (onResumeCalled) {
            simpleExoPlayer.setPlayWhenReady(true);
        }

        storyDisplayVideo.setShutterBackgroundColor(Color.BLACK);
        storyDisplayVideo.setPlayer(simpleExoPlayer);

        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onLoadingChanged(boolean isLoading) {
                if (isLoading) {
                    storyDisplayVideoProgress.setVisibility(View.VISIBLE);
                    pressTime = System.currentTimeMillis();
                    pauseCurrentStory();
                } else {
                    storyDisplayVideoProgress.setVisibility(View.GONE);
                    storiesProgressView.getProgressWithIndex(counter).setDuration(simpleExoPlayer.getDuration() != 0 ? simpleExoPlayer.getDuration() : 8000L);
                    onVideoPrepared = true;
                    resumeCurrentStory();
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                storyDisplayVideoProgress.setVisibility(View.GONE);
                if (counter == stories.size()) {
                    pageViewOperator.nextPageView();
                } else {
                    storiesProgressView.skip();
                }
            }
        });


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

    public void savePosition(int pos) {
        StoryViewFragment.progressStateArray.append(position, pos);
    }

    public int restorePosition() {
        return StoryViewFragment.progressStateArray.get(position);
    }

    public void pauseCurrentStory() {
        simpleExoPlayer.setPlayWhenReady(false);
        storiesProgressView.pause();
    }

    @Override
    public void onNext() {
        if (stories.size() <= counter + 1) {
            return;
        }
        ++counter;
        savePosition(counter);
        updateStory();
    }

    @Override
    public void onPrev() {
        if (counter - 1 < 0)
            return;
        --counter;
        savePosition(counter);
        updateStory();
    }

    @Override
    public void onComplete() {
        simpleExoPlayer.release();
        pageViewOperator.nextPageView();
    }

    public void resumeCurrentStory() {
        if (onResumeCalled) {
            simpleExoPlayer.setPlayWhenReady(true);
            showStoryOverlay();
            storiesProgressView.resume();
        }
    }

    public interface PageViewOperator extends Serializable {
        void backPageView();

        void nextPageView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        simpleExoPlayer.release();
        AndroidUtils.removeAdjustResize(getActivity(), getClass().getSimpleName());
    }
}
