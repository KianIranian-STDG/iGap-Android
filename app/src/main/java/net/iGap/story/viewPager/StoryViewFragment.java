package net.iGap.story.viewPager;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.realm.RealmStory;
import net.iGap.realm.RealmStoryProto;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmResults;

public class StoryViewFragment extends BaseFragment implements StoryDisplayFragment.PageViewOperator {

    public static SparseIntArray progressStateArray = new SparseIntArray();
    private float prevDragPosition = 0;

    private int currentPage = 0;
    private StoryViewPager viewPager;
    private StoryPagerAdapter pagerAdapter;
    private ArrayList<StructBottomSheet> itemGalleryList;
    private Bitmap bitmap;
    private boolean myStory = true;  // ->  To determine if I have storyList or my contacts has storyList
    private RealmResults<RealmStory> storyResults;
    private long userId;
    private boolean isSingle = false;
    private long storyId;

    public StoryViewFragment(long userId, boolean muStory) {
        this.userId = userId;
        this.myStory = muStory;
    }

    public StoryViewFragment(long userId, boolean muStory, boolean isSingle, long storyId) {
        this.userId = userId;
        this.myStory = muStory;
        this.isSingle = isSingle;
        this.storyId = storyId;
    }

    public StoryViewFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.story_fragment, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isSingle) {
            DbManager.getInstance().doRealmTransaction(realm -> {
                storyResults = realm.where(RealmStory.class).equalTo("id", userId).findAll();
            });
        } else {
            DbManager.getInstance().doRealmTransaction(realm -> {
                storyResults = realm.where(RealmStory.class).findAll();
            });
        }

        setUpPager();
    }

    public void setItemGalleryList(ArrayList<StructBottomSheet> itemGalleryList, boolean muStory) {
        this.itemGalleryList = itemGalleryList;
        this.myStory = muStory;
    }

    private void setUpPager() {
        List<StoryUser> storyUserList = new ArrayList<>();

        for (int i = 0; i < storyResults.size(); i++) {
            RealmList<RealmStoryProto> realmStoryProtos = storyResults.get(i).getRealmStoryProtos();
            StoryUser storyUser = new StoryUser();
            List<Story> stories = new ArrayList<>();
            storyUser.setUserName(getMessageDataStorage().getDisplayNameWithUserId(userId));
            storyUser.setProfilePicUrl("https://randomuser.me/api/portraits/women/1.jpg");
            storyUser.setUserId(storyResults.get(i).getUserId());
            for (int j = 0; j < realmStoryProtos.size(); j++) {
                RealmStoryProto realmStoryProto = realmStoryProtos.get(j);
                if (isSingle) {
                    if (realmStoryProto.getStoryId() == storyId) {
                        Story story = new Story(null, bitmap, realmStoryProto.getCaption(), realmStoryProto.getCreatedAt(),
                                realmStoryProto.getUserId(), realmStoryProto.getStoryId(), realmStoryProto.getFile(), null,realmStoryProto.getViewCount());
                        stories.add(story);
                        storyUser.setStories(stories);
                        break;
                    }
                } else {
                    Story story = new Story(null, bitmap, realmStoryProto.getCaption(), realmStoryProto.getCreatedAt(),
                            realmStoryProto.getUserId(), realmStoryProto.getStoryId(), realmStoryProto.getFile(), null,realmStoryProto.getViewCount());
                    stories.add(story);
                    storyUser.setStories(stories);
                }

            }
            storyUserList.add(storyUser);
        }


        pagerAdapter = new StoryPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, storyUserList, this, myStory);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentPage);
        viewPager.setPageTransformer(true, new PageTransformer());

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
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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
                viewPager.getCurrentItem();
            } catch (Exception e) {
                HelperLog.getInstance().setErrorLog(e);
            }
        }
    }

    @Override
    public void nextPageView(boolean clickable) {
        if (viewPager.getCurrentItem() + 1 < (viewPager.getAdapter() != null ? viewPager.getAdapter().getCount() : 0)) {
            try {
                fakeDrag(true);
            } catch (Exception e) {
                HelperLog.getInstance().setErrorLog(e);
            }
        } else {
            if (!clickable)
                new HelperFragment(getActivity().getSupportFragmentManager(), StoryViewFragment.this).popBackStack();
        }
    }
}
