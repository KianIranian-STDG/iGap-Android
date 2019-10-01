package net.iGap.news.viewmodel;

import android.util.Log;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.module.enums.ProgressState;
import net.iGap.news.repository.DetailRepo;
import net.iGap.news.repository.MainRepo;
import net.iGap.news.repository.model.NewsComment;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsError;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsList;

import java.util.List;

public class NewsDetailVM extends ViewModel {

    private MutableLiveData<NewsDetail> data;
    private MutableLiveData<NewsComment> comments;
    private MutableLiveData<NewsList> relatedNews;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressStateContext;
    private MutableLiveData<Boolean> progressStateComment;
    private MutableLiveData<Boolean> progressStateRelated;

    private ObservableField<String> title;
    private ObservableField<String> rootTitle;
    private ObservableField<String> viewNum;
    private ObservableField<String> commentNum;
    private ObservableField<String> source;
    private ObservableField<String> tag;
    private ObservableField<String> date;

    private int newsID = -1;
    private DetailRepo repo;

    public NewsDetailVM() {
        data = new MutableLiveData<>();
        comments = new MutableLiveData<>();
        relatedNews = new MutableLiveData<>();
        error = new MutableLiveData<>();
        progressStateComment = new MutableLiveData<>();
        progressStateContext = new MutableLiveData<>();
        progressStateRelated = new MutableLiveData<>();
        repo = new DetailRepo();

        title = new ObservableField<>("عنوان خبر های ایران");
        rootTitle = new ObservableField<>("زیر عنوان خبرهای ایران");
        viewNum = new ObservableField<>("128");
        commentNum = new ObservableField<>("132");
        source = new ObservableField<>("منبع خبری");
        tag = new ObservableField<>("ورزشی، اجتماعی و...");
        date = new ObservableField<>("دو ساعت پیش");
    }

    public void getDataFromServer(String newsID) {
        this.newsID = Integer.parseInt(newsID);
        repo.getNewsDetail(this.newsID, new ApiResponse<NewsDetail>() {
            @Override
            public void onResponse(NewsDetail newsDetail) {
                data.setValue(newsDetail);
                title.set(newsDetail.getTitle());
                rootTitle.set(newsDetail.getRootTitle());
                viewNum.set(newsDetail.getView());
                commentNum.set(newsDetail.getView());
                source.set(newsDetail.getSrouce());
                tag.set(newsDetail.getTags());
                date.set(newsDetail.getDate());

                getNewsComment();
                getRelatedNewsS();
            }

            @Override
            public void onFailed(String errorM) {
                error.setValue(new NewsError(true, "", "", R.string.news_serverError));
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressStateContext.setValue(visibility);
            }
        });
    }

    private void getNewsComment() {
        repo.getNewsComment(newsID, 1, 3, new ApiResponse<NewsComment>() {
            @Override
            public void onResponse(NewsComment newsComment) {
                comments.setValue(newsComment);
            }

            @Override
            public void onFailed(String error) {
                NewsComment temp = new NewsComment();
                temp.setComments(temp.getFakeData());
                comments.setValue(temp);
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressStateComment.setValue(visibility);
            }
        });
    }

    private void getRelatedNewsS() {
        repo.getRelatedNews(newsID, new ApiResponse<NewsList>() {
            @Override
            public void onResponse(NewsList newsList) {
                relatedNews.setValue(newsList);
            }

            @Override
            public void onFailed(String error) {
                relatedNews.setValue(null);
            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressStateRelated.setValue(visibility);
            }
        });
    }

    public void setData(MutableLiveData<NewsDetail> data) {
        this.data = data;
    }

    public MutableLiveData<NewsComment> getComments() {
        return comments;
    }

    public void setComments(MutableLiveData<NewsComment> comments) {
        this.comments = comments;
    }

    public MutableLiveData<NewsError> getError() {
        return error;
    }

    public void setError(MutableLiveData<NewsError> error) {
        this.error = error;
    }

    public int getNewsID() {
        return newsID;
    }

    public void setNewsID(int newsID) {
        this.newsID = newsID;
    }

    public ObservableField<String> getTitle() {
        return title;
    }

    public void setTitle(ObservableField<String> title) {
        this.title = title;
    }

    public ObservableField<String> getRootTitle() {
        return rootTitle;
    }

    public void setRootTitle(ObservableField<String> rootTitle) {
        this.rootTitle = rootTitle;
    }

    public ObservableField<String> getViewNum() {
        return viewNum;
    }

    public void setViewNum(ObservableField<String> viewNum) {
        this.viewNum = viewNum;
    }

    public ObservableField<String> getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(ObservableField<String> commentNum) {
        this.commentNum = commentNum;
    }

    public ObservableField<String> getSource() {
        return source;
    }

    public void setSource(ObservableField<String> source) {
        this.source = source;
    }

    public ObservableField<String> getTag() {
        return tag;
    }

    public void setTag(ObservableField<String> tag) {
        this.tag = tag;
    }

    public ObservableField<String> getDate() {
        return date;
    }

    public void setDate(ObservableField<String> date) {
        this.date = date;
    }

    public MutableLiveData<NewsDetail> getData() {
        return data;
    }

    public void setRelatedNews(MutableLiveData<NewsList> relatedNews) {
        this.relatedNews = relatedNews;
    }

    public MutableLiveData<NewsList> getRelatedNews() {
        return relatedNews;
    }

    public MutableLiveData<Boolean> getProgressStateContext() {
        return progressStateContext;
    }

    public void setProgressStateContext(MutableLiveData<Boolean> progressStateContext) {
        this.progressStateContext = progressStateContext;
    }

    public MutableLiveData<Boolean> getProgressStateComment() {
        return progressStateComment;
    }

    public void setProgressStateComment(MutableLiveData<Boolean> progressStateComment) {
        this.progressStateComment = progressStateComment;
    }

    public MutableLiveData<Boolean> getProgressStateRelated() {
        return progressStateRelated;
    }

    public void setProgressStateRelated(MutableLiveData<Boolean> progressStateRelated) {
        this.progressStateRelated = progressStateRelated;
    }
}
