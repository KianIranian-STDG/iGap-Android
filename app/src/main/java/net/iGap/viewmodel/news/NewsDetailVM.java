package net.iGap.viewmodel.news;

import android.view.View;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.helper.HelperCalander;
import net.iGap.model.news.NewsComment;
import net.iGap.model.news.NewsDetail;
import net.iGap.model.news.NewsError;
import net.iGap.model.news.NewsList;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.news.DetailRepo;

import java.util.List;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

public class NewsDetailVM extends BaseAPIViewModel {

    private MutableLiveData<NewsDetail> data;
    private MutableLiveData<List<NewsComment>> comments;
    private MutableLiveData<NewsList> relatedNews;
    private MutableLiveData<NewsError> error;
    private MutableLiveData<Boolean> progressStateContext;
    private MutableLiveData<Boolean> progressStateComment;
    private MutableLiveData<Boolean> progressStateRelated;

    private ObservableField<String> title;
    private ObservableField<String> rootTitle;
    private ObservableField<String> lead;
    private ObservableField<String> viewNum;
    private ObservableField<String> commentNum;
    private ObservableField<String> source;
    private ObservableField<String> tag;
    private ObservableField<String> date;
    private ObservableField<Integer> viewVisibility;
    private ObservableField<Integer> rootTitleVisibility;
    private ObservableField<Integer> pageVisibility;

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
        lead = new ObservableField<>("توضیح کوتاه خبرهای ایران");
        viewNum = new ObservableField<>("0");
        commentNum = new ObservableField<>("0");
        source = new ObservableField<>("منبع خبری");
        tag = new ObservableField<>("ورزشی، اجتماعی و...");
        date = new ObservableField<>("دو ساعت پیش");
        viewVisibility = new ObservableField<>(View.INVISIBLE);
        pageVisibility = new ObservableField<>(View.VISIBLE);
        rootTitleVisibility = new ObservableField<>(View.VISIBLE);
    }

    public void getDataFromServer(String newsID) {
        try {
            this.newsID = Integer.parseInt(newsID);
        } catch (Exception e) {
            error.setValue(new NewsError(true, "", "API Input is NOT valid.", R.string.news_serverError));
            return;
        }
        progressStateContext.setValue(true);
        repo.getNewsDetail(this.newsID, this, new ResponseCallback<NewsDetail>() {
            @Override
            public void onSuccess(NewsDetail newsDetail) {
                data.setValue(newsDetail);
                title.set(newsDetail.getTitle());
                if (newsDetail.getRootTitle() == null || newsDetail.getRootTitle().isEmpty() || newsDetail.getRootTitle().length() < 2)
                    rootTitleVisibility.set(View.GONE);
                rootTitle.set(newsDetail.getRootTitle());
                lead.set(newsDetail.getLead());
                int viewTemp = Integer.valueOf(newsDetail.getView()) + Integer.valueOf(newsID);
                if (viewTemp > 1000000) {
                    viewTemp = viewTemp / 1000000;
                    viewNum.set((HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(viewTemp)) : viewTemp) + "M");
                } else if (viewTemp > 1000) {
                    viewTemp = viewTemp / 1000;
                    viewNum.set((HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(viewTemp)) : viewTemp) + "K");
                } else {
                    viewNum.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(viewTemp)) : String.valueOf(viewTemp));
                }
//                viewVisibility.set(View.VISIBLE);
                commentNum.set(newsDetail.getView());
                source.set(newsDetail.getSource());
                if (newsDetail.getTags() == null || newsDetail.getTags().equals("null"))
                    tag.set("");
                else
                    tag.set("برچسب ها: " + newsDetail.getTags());
                date.set(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(newsDetail.getDate()) : newsDetail.getDate());
                pageVisibility.set(View.GONE);
                getNewsComment();
                getRelatedNewsS();
                progressStateContext.setValue(false);
            }

            @Override
            public void onError(String e) {
                progressStateContext.setValue(false);
                error.setValue(new NewsError(true, "001", e, R.string.news_serverError));
            }

            @Override
            public void onFailed() {
                progressStateContext.setValue(false);
            }
        });
    }

    private void getNewsComment() {
        progressStateComment.setValue(true);
        repo.getNewsComment(newsID, 1, 3, this, new ResponseCallback<List<NewsComment>>() {
            @Override
            public void onSuccess(List<NewsComment> data) {
                progressStateComment.setValue(false);
                comments.setValue(data);
            }

            @Override
            public void onError(String error) {
                progressStateComment.setValue(false);
            }

            @Override
            public void onFailed() {
                progressStateComment.setValue(false);
            }
        });
    }

    private void getRelatedNewsS() {
        progressStateRelated.setValue(true);
        repo.getRelatedNews(newsID, this, new ResponseCallback<NewsList>() {
            @Override
            public void onSuccess(NewsList data) {
                relatedNews.setValue(data);
                progressStateRelated.setValue(false);
            }

            @Override
            public void onError(String error) {
                relatedNews.setValue(null);
                progressStateRelated.setValue(false);
            }

            @Override
            public void onFailed() {
                progressStateRelated.setValue(false);
            }
        });
    }

    public MutableLiveData<List<NewsComment>> getComments() {
        return comments;
    }

    public void setComments(MutableLiveData<List<NewsComment>> comments) {
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

    public void setData(MutableLiveData<NewsDetail> data) {
        this.data = data;
    }

    public MutableLiveData<NewsList> getRelatedNews() {
        return relatedNews;
    }

    public void setRelatedNews(MutableLiveData<NewsList> relatedNews) {
        this.relatedNews = relatedNews;
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

    public ObservableField<Integer> getViewVisibility() {
        return viewVisibility;
    }

    public void setViewVisibility(ObservableField<Integer> viewVisibility) {
        this.viewVisibility = viewVisibility;
    }

    public ObservableField<Integer> getPageVisibility() {
        return pageVisibility;
    }

    public void setPageVisibility(ObservableField<Integer> pageVisibility) {
        this.pageVisibility = pageVisibility;
    }

    public ObservableField<String> getLead() {
        return lead;
    }

    public void setLead(ObservableField<String> lead) {
        this.lead = lead;
    }

    public ObservableField<Integer> getRootTitleVisibility() {
        return rootTitleVisibility;
    }

    public void setRootTitleVisibility(ObservableField<Integer> rootTitleVisibility) {
        this.rootTitleVisibility = rootTitleVisibility;
    }
}
