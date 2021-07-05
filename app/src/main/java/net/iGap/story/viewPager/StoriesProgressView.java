package net.iGap.story.viewPager;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;

import java.util.ArrayList;
import java.util.List;

public class StoriesProgressView extends LinearLayout {

    private int storiesCount = -1;
    private int current = -1;
    private boolean isSkipStart = false;
    private boolean isReverseStart = false;
    private boolean isComplete = false;
    private StoriesListener storiesListener;
    private List<StoryProgress> progressBars = new ArrayList<>();
    private StoryProgress StoryProgress;

    public StoriesProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StoriesProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView);
        storiesCount = typedArray.getInt(R.styleable.StoriesProgressView_progressCount, 0);
        typedArray.recycle();
        bindViews();
    }

    public void setStoriesCount(int storiesCount) {
        this.storiesCount = storiesCount;
        bindViews();
    }

    public void setStoriesListener(StoriesListener listener) {
        storiesListener = listener;
    }

    private void bindViews() {
        progressBars.clear();
        removeAllViews();
        for (int i = 0; i < storiesCount; i++) {
            StoryProgress = createProgressBar();
            progressBars.add(StoryProgress);
            addView(StoryProgress);
            if (i + 1 < storiesCount) {
                addView(createSpace());
            }
        }
    }

    private StoryProgress createProgressBar() {
        StoryProgress = new StoryProgress(getContext(), null, 0);
        StoryProgress.setLayoutParams(LayoutCreator.createLinear(0, 4, 1F));
        return StoryProgress;
    }

    private View createSpace() {
        View view = new View(getContext());
        view.setLayoutParams(LayoutCreator.createRelative(5, 4));
        return view;
    }

    private StoryProgress.Callback callback(int index) {

        return new StoryProgress.Callback() {
            @Override
            public void onStartProgress() {
                current = index;
            }

            @Override
            public void onFinishProgress() {
                if (isReverseStart) {
                    if (storiesListener != null) storiesListener.onPrev();
                    if (0 <= current - 1) {
                        StoryProgress p = progressBars.get(current - 1);
                        p.setMinWithoutCallback();
                        progressBars.get(--current).startProgress();
                    } else {
                        progressBars.get(current).startProgress();
                    }
                    isReverseStart = false;
                    return;
                }
                int next = current + 1;
                if (next <= progressBars.size() - 1) {
                    if (storiesListener != null) storiesListener.onNext();
                    progressBars.get(next).startProgress();
                    ++current;
                } else {
                    isComplete = true;
                    if (storiesListener != null) storiesListener.onComplete();
                }
                isSkipStart = false;
            }
        };
    }

    public void skip() {
        if (isSkipStart || isReverseStart) return;
        if (isComplete) return;
        if (current < 0) return;
        StoryProgress progress = progressBars.get(current);
        isSkipStart = true;
        progress.setMax();
    }

    public void reverse() {
        if (isSkipStart || isReverseStart) return;
        if (isComplete) return;
        if (current < 0) return;
        StoryProgress progress = progressBars.get(current);
        isReverseStart = true;
        progress.setMin();
    }

    public void setAllStoryDuration(long duration) {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).setDuration(duration);
            progressBars.get(i).setCallback(callback(i));
        }
    }

    public void startStories() {
        if (progressBars.size() > 0) {
            progressBars.get(0).startProgress();
        }
    }

    public void startStories(int from) {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.get(i).clear();
        }
        for (int i = 0; i < from; i++) {
            if (progressBars.size() > i) {
                progressBars.get(i).setMaxWithoutCallback();
            }
        }
        if (progressBars.size() > from) {
            progressBars.get(from).startProgress();
        }
    }

    public void destroy() {
        for (int i = 0; i < progressBars.size(); i++) {
            progressBars.clear();
        }
    }

    public void abandon() {
        if (progressBars.size() > current && current >= 0) {
            progressBars.get(current).setMinWithoutCallback();
        }
    }

    public void pause() {
        if (current < 0) return;
        progressBars.get(current).pauseProgress();
    }

    public void resume() {
        if (current < 0 && progressBars.size() > 0) {
            progressBars.get(0).startProgress();
            return;
        }
        progressBars.get(current).resumeProgress();
    }

    public interface StoriesListener {
        void onNext();

        void onPrev();

        void onComplete();
    }
}
