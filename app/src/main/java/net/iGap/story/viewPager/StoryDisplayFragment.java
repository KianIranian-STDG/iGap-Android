package net.iGap.story.viewPager;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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

import com.bumptech.glide.Glide;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.module.AndroidUtils;
import net.iGap.module.customView.EventEditText;
import net.iGap.story.ExpandableTextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class StoryDisplayFragment extends BaseFragment implements StoriesProgressView.StoriesListener {
    private static final String EXTRA_POSITION = "EXTRA_POSITION";
    private static final String EXTRA_STORY_USER = "EXTRA_STORY_USER";
    private int position;
    private StoryUser storyUser;
    private List<Story> stories;
    private long pressTime = 0L;
    private final long limit = 500L;
    private int counter = 0;
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

    public static StoryDisplayFragment newInstance(int position, StoryUser storyModel) {

        Bundle args = new Bundle();
        StoryDisplayFragment fragment = new StoryDisplayFragment();
        args.putInt(EXTRA_POSITION, position);
        args.putSerializable(EXTRA_STORY_USER, storyModel);
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
        assert getArguments() != null;
        position = getArguments().getInt(EXTRA_POSITION);
        storyUser = (StoryUser) getArguments().getSerializable(EXTRA_STORY_USER);
        stories = storyUser.getStories();
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
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateStory();
        setUpUi();
        setupReplay();
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
        storiesProgressView.abandon();
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

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(stories.get(counter).getStoryData());
        storyTime.setText(DateFormat.format(" HH:mm", calendar).toString());
        userName.setText(storyUser.getUserName());
        nameFrom.setText(storyUser.getUserName());
        descriptionFrom.setText(stories.get(counter).getTxt() != null ? stories.get(counter).getTxt() : "Photo");
        Glide.with(this).load(stories.get(counter).getUrl()).into(storyDisplayImage);
        Glide.with(this).load(storyUser.getProfilePicUrl()).circleCrop().into(userImage);
        Glide.with(this).load(stories.get(counter).getUrl()).into(tumNailImage);

    }

    private void setUpUi() {

        OnSwipeTouchListener touchListener = new OnSwipeTouchListener(getActivity()) {

            @Override
            public void onSwipeTop() {
                Toast.makeText(getActivity(), "onSwipeTop", Toast.LENGTH_LONG).show();
                new HelperFragment(getChildFragmentManager(), StoryDisplayFragment.this).popBackStack();
            }

            @Override
            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "onSwipeBottom", Toast.LENGTH_LONG).show();
                new HelperFragment(getChildFragmentManager(), StoryDisplayFragment.this).popBackStack();

            }

            @Override
            public void onClick(View view) {
                if (view == next)
                    if (counter == stories.size() - 1) {
                        pageViewOperator.nextPageView();
                    } else {
                        storiesProgressView.skip();
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
            public boolean onTouchView(View view, MotionEvent event) {
                super.onTouchView(view, event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressTime = System.currentTimeMillis();
                        pauseCurrentStory();
                        return false;
                    case MotionEvent.ACTION_UP:
                        resumeCurrentStory();
                        return limit < System.currentTimeMillis() - pressTime;
                }
                return false;

            }
        };
        previous.setOnTouchListener(touchListener);
        next.setOnTouchListener(touchListener);

        storiesProgressView.setStoriesCount(stories.size());
        storiesProgressView.setAllStoryDuration(4000L);
        storiesProgressView.setStoriesListener(this);
    }

    private void setupReplay() {
        replayFrame.setOnClickListener(view1 -> {
            openKeyBoard();
            getKeyboardState();
            pauseCurrentStory();
        });
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
        } else {
            pauseCurrentStory();
        }
        Log.i("nazanin", "keyboardStateChanged: " + closeKeyboard);
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
        StoryViewFragment.progressStateArray.append(position, pos);
    }

    public int restorePosition() {
        return StoryViewFragment.progressStateArray.get(position);
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
        pageViewOperator.nextPageView();
    }

    public void resumeCurrentStory() {
        if (onResumeCalled) {
            //  showStoryOverlay();
            storiesProgressView.resume();
        }
    }

    public interface PageViewOperator {
        void backPageView();

        void nextPageView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideKeyboard();
/*        counter = 0;
        savePosition(counter);*/
        AndroidUtils.removeAdjustResize(getActivity(), getClass().getSimpleName());
    }
}
