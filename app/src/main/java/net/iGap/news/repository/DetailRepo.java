package net.iGap.news.repository;

import net.iGap.api.apiService.ApiResponse;
import net.iGap.news.repository.api.NewsAPIRepository;
import net.iGap.news.repository.model.NewsApiArg;
import net.iGap.news.repository.model.NewsComment;
import net.iGap.news.repository.model.NewsDetail;
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsGroup;
import net.iGap.news.repository.model.NewsList;
import net.iGap.news.repository.model.NewsPublisher;
import net.iGap.realm.RealmUserInfo;
import net.iGap.request.RequestUserProfileGetEmail;

import java.util.List;

import io.realm.Realm;

public class DetailRepo {

    private NewsAPIRepository repository = new NewsAPIRepository();
    private Realm realm;
    private RealmUserInfo userInfo;

    public DetailRepo() {
        updateUserInfo();
    }

    public void getNewsDetail(int newsID, ApiResponse<NewsDetail> apiResponse) {
        repository.getNewsDetail(newsID, apiResponse);
    }

    public void getNewsComment(int newsID, int start, int display, ApiResponse<NewsComment> apiResponse) {
        repository.getNewsComment(newsID, start, display, apiResponse);
    }

    public void postNewsComment(String newsID, String comment, String author, String email, ApiResponse<NewsDetail> apiResponse) {
        repository.postNewsComment(newsID, comment, author, email, apiResponse);
    }

    public void getRelatedNews(int newsID, ApiResponse<NewsList> apiResponse) {
        repository.getNewsList(new NewsApiArg(1, 5, -1, NewsApiArg.NewsType.Latest), apiResponse);
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
