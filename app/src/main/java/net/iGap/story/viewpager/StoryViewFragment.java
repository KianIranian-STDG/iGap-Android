package net.iGap.story.viewpager;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheUtil;
import com.google.android.exoplayer2.util.Util;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.DispatchQueue;
import net.iGap.helper.HelperLog;

import java.util.ArrayList;
import java.util.List;

public class StoryViewFragment extends BaseFragment implements StoryDisplayFragment.PageViewOperator {

    public static SparseIntArray progressStateArray = new SparseIntArray();
    private float prevDragPosition = 0;
    private int currentPage = 0;
    private StoryViewPager viewPager;
    private StoryPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.story_fragment, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        new StoryDisplayFragment(this);
        setUpPager();
        return view;
    }


    private void setUpPager() {
        List<StoryUser> storyUserList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            StoryUser storyUser = new StoryUser();
            switch (i) {
                case 0:
                    List<Story> stories = new ArrayList<>();
                    storyUser.setUserName("Sahar");
                    storyUser.setProfilePicUrl("https://randomuser.me/api/portraits/women/1.jpg");
                    for (int j = 0; j < 3; j++) {
                        Story story = new Story();
                        switch (j) {
                            case 0:
                                story.setUrl("https://www.koko.org/wp-content/uploads/2019/08/koko_smoky_hat1_T-phone.jpg");
                                break;
                            case 1:
                                story.setUrl("https://www.koko.org/wp-content/uploads/2019/08/koko_smoky_hat1_T-phone.jpg");
                                break;
                            case 2:
                                story.setUrl("https://image.freepik.com/free-vector/shining-bokeh-overlay-background_1409-778.jpg");
                                break;
                        }
                        story.setStoryData(System.currentTimeMillis() - (1 * (24 - j) * 60 * 60 * 1000));
                        stories.add(story);

                    }
                    storyUser.setStories(stories);
                    break;
                case 1:
                    List<Story> stories1 = new ArrayList<>();
                    Story story1 = new Story();
                    storyUser.setUserName("Mary");
                    storyUser.setProfilePicUrl("https://randomuser.me/api/portraits/women/2.jpg");
                    story1.setUrl("https://image.freepik.com/free-vector/shining-bokeh-overlay-background_1409-778.jpg");
                    story1.setStoryData(System.currentTimeMillis() - (1 * (24) * 60 * 60 * 1000));
                    stories1.add(story1);
                    storyUser.setStories(stories1);
                    break;
            }


            storyUserList.add(storyUser);
        }
        pagerAdapter = new StoryPagerAdapter(getChildFragmentManager(), storyUserList);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentPage);
        viewPager.setPageTransformer(true, new PageTransformer(20));

        viewPager.addOnPageChangeListener(new PageChangeListener() {
            @Override
            void onPageScrollCanceled() {
                StoryDisplayFragment fragment = (StoryDisplayFragment) pagerAdapter.findFragmentByPosition(viewPager, currentPage);
                fragment.resumeCurrentStory();
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPage = position;
            }
        });

          preLoadStories(storyUserList);
    }

    private void preLoadStories(List<StoryUser> storyUserList) {
        List<String> imageList = new ArrayList<>();
        List<String> videoList = new ArrayList<>();

        for (StoryUser storyUser : storyUserList) {
            for (Story story : storyUser.getStories()) {
                if (story.isVideo()) {
                    videoList.add(story.getUrl());
                } else {
                    imageList.add(story.getUrl());
                }
            }
        }
        preLoadVideos(videoList);
        preLoadImages(imageList);
    }

    private void preLoadVideos(List<String> videoList) {
       for (String data : videoList) {
            new DispatchQueue("videoListQue").postRunnable(() -> {
                Uri dataUri = Uri.parse(data);
                DataSpec dataSpec = new DataSpec(dataUri, 0, 500 * 1024, null);
                DataSource dataSource = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), getString(R.string.app_name))).createDataSource();
                CacheUtil.ProgressListener listener = (requestLength, bytesCached, newBytesCached) -> {
                    double downloadPercentage = (bytesCached * 100.0 / requestLength);
                };
                try {
                    CacheUtil.cache(
                            dataSpec,
                            StoryApp.simpleCache,
                            CacheUtil.DEFAULT_CACHE_KEY_FACTORY,
                            dataSource,
                            listener,
                            null
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private void preLoadImages(List<String> imageList) {
        for (String imageStory : imageList) {
            // TODO: 4/21/21 perload image bitmap
            //         Glide.with(this).load(imageStory).preload();
        }

    }

    private void fakeDrag(boolean forward) {
        if (prevDragPosition == 0 && viewPager.beginFakeDrag()) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, viewPager.getWidth());
            valueAnimator.setDuration(200L);
            valueAnimator.setInterpolator(new FastOutLinearInInterpolator());
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    valueAnimator.removeAllUpdateListeners();
                    if (viewPager.isFakeDragging()) {
                        viewPager.endFakeDrag();
                    }
                    prevDragPosition = 0;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    valueAnimator.removeAllUpdateListeners();
                    if (viewPager.isFakeDragging()) {
                        viewPager.endFakeDrag();
                    }
                    prevDragPosition = 0;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueAnimator.addUpdateListener(animation -> {
                if (!viewPager.isFakeDragging()) return;
                float dragPosition = (float) valueAnimator.getAnimatedValue();
                float dragOffset = ((dragPosition - prevDragPosition) * (forward ? -1 : 1));
                prevDragPosition = dragPosition;
                viewPager.fakeDragBy(dragOffset);
            });

            valueAnimator.start();

        }

    }

    @Override
    public void backPageView() {

        if (viewPager.getCurrentItem() > 0) {
            try {
                fakeDrag(false);
            } catch (Exception e) {
                HelperLog.getInstance().setErrorLog(e);
            }
        }
    }

    @Override
    public void nextPageView() {
        if (viewPager.getCurrentItem() + 1 < (viewPager.getAdapter() != null ? viewPager.getAdapter().getCount() : 0)) {
            try {
                fakeDrag(true);
            } catch (Exception e) {
                HelperLog.getInstance().setErrorLog(e);
            }
        } else {
            //there is no next story
        }
    }
}
