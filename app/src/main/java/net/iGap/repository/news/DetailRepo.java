package net.iGap.repository.news;

import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.model.news.NewsApiArg;
import net.iGap.model.news.NewsComment;
import net.iGap.model.news.NewsDetail;
import net.iGap.model.news.NewsList;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import java.util.List;

public class DetailRepo {

    private NewsAPIRepository repository = new NewsAPIRepository();
    private RealmUserInfo userInfo;

    public DetailRepo() {
        updateUserInfo();
    }

    public void getNewsDetail(int newsID, HandShakeCallback handShakeCallback, ResponseCallback<NewsDetail> apiResponse) {
        repository.getNewsDetail(newsID, handShakeCallback, apiResponse);
    }

    public void getNewsComment(int newsID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<List<NewsComment>> apiResponse) {
        repository.getNewsComment(newsID, start, display, handShakeCallback, apiResponse);
    }

    public void postNewsComment(String newsID, String comment, String author, String email, HandShakeCallback handShakeCallback, ResponseCallback<NewsDetail> apiResponse) {
        repository.postNewsComment(newsID, comment, author, email, handShakeCallback, apiResponse);
    }

    public void getRelatedNews(int newsID, HandShakeCallback handShakeCallback, ResponseCallback<NewsList> apiResponse) {
        repository.getNewsList(new NewsApiArg(1, 5, newsID, NewsApiArg.NewsType.RELATED_NEWS), handShakeCallback, apiResponse);
    }

    public String getUserFirstName() {
        return userInfo.getUserInfo().getDisplayName();
    }

    public String getUserLastName() {
        return userInfo.getUserInfo().getLastName();
    }

    public String getUserEmail() {
        return userInfo.getEmail();
    }

    private void updateUserInfo() {
        DbManager.getInstance().doRealmTask(realm -> {
            userInfo = realm.where(RealmUserInfo.class).findFirst();
        });

        if (userInfo.getEmail() == null)
            new RequestUserProfileGetEmail().userProfileGetEmail();
    }

}
