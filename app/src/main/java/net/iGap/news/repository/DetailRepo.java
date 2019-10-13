package net.iGap.news.repository;

import net.iGap.api.apiService.HandShakeCallback;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.news.repository.api.NewsAPIRepository;
import net.iGap.news.repository.model.NewsApiArg;
import net.iGap.news.repository.model.NewsComment;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsList;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import io.realm.Realm;

public class DetailRepo {

    private NewsAPIRepository repository = new NewsAPIRepository();
    private Realm realm;
    private RealmUserInfo userInfo;

    public DetailRepo() {
        updateUserInfo();
    }

    public void getNewsDetail(int newsID, HandShakeCallback handShakeCallback, ResponseCallback<NewsDetail> apiResponse) {
        repository.getNewsDetail(newsID, handShakeCallback, apiResponse);
    }

    public void getNewsComment(int newsID, int start, int display, HandShakeCallback handShakeCallback, ResponseCallback<NewsComment> apiResponse) {
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
        userInfo = getRealm().where(RealmUserInfo.class).findFirst();
        closeRealm();

        if (userInfo.getEmail() == null)
            new RequestUserProfileGetEmail().userProfileGetEmail();
    }

    private Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    private void closeRealm() {
        realm.close();
    }

}
