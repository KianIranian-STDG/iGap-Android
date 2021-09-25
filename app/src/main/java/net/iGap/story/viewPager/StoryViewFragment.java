package net.iGap.story.viewPager;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import net.iGap.realm.RealmStoryProto;
import net.iGap.story.MainStoryObject;
import net.iGap.story.StoryObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Sort;

public class StoryViewFragment extends BaseFragment implements StoryDisplayFragment.PageViewOperator {

    private float prevDragPosition = 0;

    private int currentPage = 0;
    private StoryViewPager viewPager;
    private StoryPagerAdapter pagerAdapter;
    private ArrayList<StructBottomSheet> itemGalleryList;
    private Bitmap bitmap;
    private boolean myStory = true;  // ->  To determine if I have storyList or my contacts has storyList
    private List<MainStoryObject> storyResults = new ArrayList<>();
    private long userId;
    private boolean isSingle = false;
    private long storyId;
    private int myStoryCount = 0;
    private boolean isForReply = false;


    public StoryViewFragment(long userId, boolean myStory) {
        this.userId = userId;
        this.myStory = myStory;
    }

    public StoryViewFragment(long userId, boolean myStory, boolean isSingle, long storyId) {
        this.userId = userId;
        this.myStory = myStory;
        this.isSingle = isSingle;
        this.storyId = storyId;
    }

    public StoryViewFragment(long userId, boolean myStory, boolean isSingle, boolean isForReply, long storyId) {
        this.userId = userId;
        this.myStory = myStory;
        this.isSingle = isSingle;
        this.storyId = storyId;
        this.isForReply = isForReply;
    }

    public StoryViewFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        View view = LayoutInflater.from(getContext()).inflate(R.layout.story_fragment, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isSingle) {
            if (isForReply) {
                DbManager.getInstance().doRealmTransaction(realm -> {
                    RealmStoryProto realmStoryProto = realm.where(RealmStoryProto.class).equalTo("storyId", storyId).findFirst();
                    if (realmStoryProto != null) {
                        MainStoryObject mainStoryObject = new MainStoryObject();
                        List<StoryObject> storyObjects = new ArrayList<>();
                        mainStoryObject.userId = realmStoryProto.getUserId();
                        storyObjects.add(StoryObject.create(realmStoryProto));
                        mainStoryObject.displayName = realmStoryProto.getDisplayName();
                        mainStoryObject.profileColor = realmStoryProto.getProfileColor();
                        mainStoryObject.storyObjects = storyObjects;
                        storyResults.add(mainStoryObject);
                    }
                });
            } else {
                storyResults.addAll(getMessageDataStorage().getSortedStoryObjectsInMainStoryObject(userId, new String[]{"createdAt"}, new Sort[]{Sort.ASCENDING}));
            }

        } else {
            storyResults.addAll(getMessageDataStorage().getSortedStoryObjectsInMainStoryObject(0, new String[]{"createdAt"}, new Sort[]{Sort.ASCENDING}));
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
            List<StoryObject> realmStoryProtos = storyResults.get(i).storyObjects;
            StoryUser storyUser = new StoryUser();
            List<Story> stories = new ArrayList<>();
            storyUser.setUserName(storyResults.get(i).displayName);
            storyUser.setUserId(storyResults.get(i).userId);
            for (int j = 0; j < realmStoryProtos.size(); j++) {
                StoryObject storyObject = realmStoryProtos.get(j);
                if (storyObject.userId == userId) {
                    currentPage = i;
                }
                if (isSingle) {
                    if (storyObject.storyId != 0 && storyObject.storyId == storyId) {
                        Story story = new Story(null, bitmap, storyObject.caption, storyObject.createdAt,
                                storyObject.userId, storyObject.storyId, storyObject.attachmentObject, null, storyObject.viewCount, storyObject.storyViewInfoObjects);
                        stories.add(story);
                        storyUser.setStories(stories);
                        break;
                    } else if (storyObject.index == storyId) {
                        Story story = new Story(null, bitmap, storyObject.caption, storyObject.createdAt,
                                storyObject.userId, storyObject.storyId, storyObject.attachmentObject, null, storyObject.viewCount, storyObject.storyViewInfoObjects);
                        stories.add(story);
                        storyUser.setStories(stories);
                        break;
                    }
                } else {
                    Story story = new Story(null, bitmap, storyObject.caption, storyObject.createdAt,
                            storyObject.userId, storyObject.storyId, storyObject.attachmentObject, null, storyObject.viewCount, storyObject.storyViewInfoObjects);
                    stories.add(story);
                    storyUser.setStories(stories);
                }

            }
            storyUserList.add(storyUser);
        }


        pagerAdapter = new StoryPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, storyUserList, this, myStory, isForReply);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(currentPage);
        viewPager.setPageTransformer(true, new PageTransformer());

        viewPager.addOnPageChangeListener(new PageChangeListener() {
            @Override
            void onPageScrollCanceled() {
                StoryDisplayFragment fragment = (StoryDisplayFragment) pagerAdapter.findFragmentByPosition(viewPager, currentPage);
//                Log.e("mmd", "onPageScrollCanceled resumeCurrentStory: "  );
//                fragment.resumeCurrentStory();
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
            if (!clickable) {
                requireActivity().onBackPressed();
            }
        }
    }
}
