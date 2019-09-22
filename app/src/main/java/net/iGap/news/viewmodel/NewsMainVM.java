package net.iGap.news.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.news.repository.MainRepo;
import net.iGap.news.repository.model.NewsError;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsFirstPage;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsMainBTN;
import net.iGap.news.repository.model.NewsSlider;

import java.util.ArrayList;
import java.util.List;

public class NewsMainVM extends ViewModel {

    private MutableLiveData<List<NewsFirstPage>> mainList;
    private List<NewsFirstPage> temp;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressState;
    private MainRepo repo;

    public NewsMainVM() {
        mainList = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressState = new MutableLiveData<>();
        repo = new MainRepo();
        temp = new ArrayList<>();
    }

    public void getData() {
        repo.getSlideNews(1, 5, new ApiResponse<NewsList>() {
            @Override
            public void onResponse(NewsList newsList) {
                addSlider(newsList);
                getNews();
            }

            @Override
            public void onFailed(String errorM) {
                getNews();
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                if (visibility)
                    progressState.setValue(visibility);
            }
        });
    }

    private void getNews() {
        repo.getLastGroupNews(new ApiResponse<List<NewsFPList>>() {
            @Override
            public void onResponse(List<NewsFPList> newsFPList) {
//                addFakeData(newsFPList);
                addTree4(newsFPList);
            }

            @Override
            public void onFailed(String errorM) {
                addFakeData(null);
                addTree4(null);
//                error.setValue(new NewsError(true, "", "", R.string.news_serverError));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressState.setValue(visibility);
            }
        });
    }

    private void addSlider(NewsList newsList) {
        if (newsList == null || newsList.getNews().size() == 0)
            return;

        List<NewsSlider> tempList = new ArrayList<>();
        for (NewsList.News news:newsList.getNews()) {
            tempList.add(new NewsSlider(news.getImage(), news.getTitle(), ""));
        }
        NewsFirstPage nfp = new NewsFirstPage(tempList, null, null, 0);
        this.temp.add(nfp);
    }

    private void addTree4(List<NewsFPList> newsFPList) {
        if (newsFPList == null || newsFPList.size() == 0) {
            mainList.setValue(temp);
            return;
        }
        if (newsFPList.size() < 4) {
            addTree2(newsFPList);
            return;
        }
        List<NewsFPList> tempList = new ArrayList<>(newsFPList.subList(0, 4));
        NewsFirstPage nfp = new NewsFirstPage(null, null, tempList, 5);
        this.temp.add(nfp);
        newsFPList.subList(0, 4).clear();
        addTree2(newsFPList);
    }
    private void addTree2(List<NewsFPList> newsFPList) {
        Log.d("amini", "addTree2: " + newsFPList.size());
        if (newsFPList.size() == 0) {
            mainList.setValue(temp);
            return;
        }
        if (newsFPList.size() < 2) {
            addTree1(newsFPList);
            return;
        }
        List<NewsFPList> tempList = new ArrayList<>(newsFPList.subList(0, 2));
        NewsFirstPage nfp = new NewsFirstPage(null, null, tempList, 4);
        this.temp.add(nfp);
        newsFPList.subList(0, 2).clear();
        addTree1(newsFPList);
    }
    private void addTree1(List<NewsFPList> newsFPList) {
        Log.d("amini", "addTree1: "+newsFPList.size());
        if (newsFPList.size() == 0) {
            mainList.setValue(temp);
            return;
        }
        List<NewsFPList> tempList = new ArrayList<>(newsFPList.subList(0, 1));
        NewsFirstPage nfp = new NewsFirstPage(null, null, tempList, 3);
        this.temp.add(nfp);
        newsFPList.remove(0);
        addTree4(newsFPList);
    }

    private void addFakeData(List<NewsFPList> newsFPList) {

        //TODO remove this code from here for final release

        NewsMainBTN btn = new NewsMainBTN(101, "آرشیو اخبار",0);
        List<NewsMainBTN> tempBtn = new ArrayList<>();
        tempBtn.add(btn);
        NewsFirstPage nfp1 = new NewsFirstPage(null, tempBtn, null, 2);
        this.temp.add(nfp1);

        if (newsFPList != null) {
            List<NewsFPList> tempList = new ArrayList<>();
            for (int i = 0; i < 6; i++)
                tempList.add(newsFPList.get(0));
            NewsFirstPage nfp = new NewsFirstPage(null, null, tempList, 5);
            this.temp.add(nfp);
        }
        else {
            List<NewsFPList> tempList = new ArrayList<>();
            for (int i = 0; i < 6; i++)
                tempList.add(new NewsFPList().addFakeData());
            NewsFirstPage nfp = new NewsFirstPage(null, null, tempList, 5);
            NewsFirstPage nfp2 = new NewsFirstPage(null, null, tempList, 4);
            NewsFirstPage nfp3 = new NewsFirstPage(null, null, tempList, 3);
            this.temp.add(nfp);
            this.temp.add(nfp2);
            this.temp.add(nfp3);
        }

        NewsMainBTN btn2 = new NewsMainBTN(102, "گروه های خبری",0);
        NewsMainBTN btn3 = new NewsMainBTN(103, "منابع خبری",0);
        List<NewsMainBTN> tempBtn2 = new ArrayList<>();
        tempBtn2.add(btn2);
        tempBtn2.add(btn3);
        NewsFirstPage nfp2 = new NewsFirstPage(null, tempBtn2, null, 1);
        this.temp.add(nfp2);
    }

    public MutableLiveData<List<NewsFirstPage>> getMainList() {
        return mainList;
    }

    public void setMainList(MutableLiveData<List<NewsFirstPage>> mainList) {
        this.mainList = mainList;
    }

    public MutableLiveData<NewsError> getError() {
        return error;
    }

    public void setError(MutableLiveData<NewsError> error) {
        this.error = error;
    }

    public MutableLiveData<Boolean> getProgressState() {
        return progressState;
    }

    public void setProgressState(MutableLiveData<Boolean> progressState) {
        this.progressState = progressState;
    }
}
