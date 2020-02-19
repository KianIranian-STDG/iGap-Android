package net.iGap.viewmodel.news;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.repository.news.DetailRepo;
import net.iGap.model.news.NewsDetail;

public class NewsAddCommentVM extends BaseAPIViewModel {

    private MutableLiveData<Boolean> complete = new MutableLiveData<>();

    private ObservableField<String> author = new ObservableField<>();
    private ObservableField<Integer> authorError = new ObservableField<>(0);
    private ObservableField<String> email = new ObservableField<>();
    private ObservableField<Integer> emailError = new ObservableField<>(0);
    private ObservableField<String> comment = new ObservableField<>();
    private ObservableField<Integer> commentError = new ObservableField<>(0);
    private ObservableField<Integer> progress = new ObservableField<>(R.string.news_add_comment_submit);

    private DetailRepo repo = new DetailRepo();
    private String newsID;

    public NewsAddCommentVM() {
        author.set(repo.getUserFirstName());
        email.set(repo.getUserEmail());
    }

    private boolean checkData() {
        // check author
        if (author.get() != null) {
            if (!author.get().isEmpty()) {

            } else {
                authorError.set(R.string.news_add_comment_errorAuthor);
                return false;
            }
        } else {
            authorError.set(R.string.news_add_comment_errorAuthor);
            return false;
        }
        // check email
        if (email.get() != null) {
            if (!email.get().isEmpty()) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.get()).matches()) {
                    emailError.set(R.string.news_add_comment_errorEmail2);
                    return false;
                }
            } else {
                emailError.set(R.string.news_add_comment_errorEmail);
                return false;
            }
        } else {
            emailError.set(R.string.news_add_comment_errorEmail);
            return false;
        }
        // check comment
        if (comment.get() != null) {
            if (!comment.get().isEmpty()) {
                return true;
            } else {
                commentError.set(R.string.news_add_comment_errorComment);
                return false;
            }
        } else {
            commentError.set(R.string.news_add_comment_errorComment);
            return false;
        }
    }

    private void sendData() {
        progress.set(R.string.news_add_comment_load);
        repo.postNewsComment(newsID, comment.get(), author.get(), email.get(), this, new ResponseCallback<NewsDetail>() {
            @Override
            public void onSuccess(NewsDetail data) {
                progress.set(R.string.news_add_comment_success);
                complete.setValue(true);
            }

            @Override
            public void onError(String error) {
                progress.set(R.string.news_add_comment_fail);
                complete.setValue(false);
            }

            @Override
            public void onFailed() {
                progress.set(R.string.connection_error);
                complete.setValue(false);
            }
        });
    }

    public void onContinueBtnClick() {
        if (!checkData())
            return;
        sendData();
    }

    public ObservableField<Integer> getProgress() {
        return progress;
    }

    public void setProgress(ObservableField<Integer> progress) {
        this.progress = progress;
    }

    public ObservableField<String> getAuthor() {
        return author;
    }

    public void setAuthor(ObservableField<String> author) {
        this.author = author;
    }

    public ObservableField<Integer> getAuthorError() {
        return authorError;
    }

    public void setAuthorError(ObservableField<Integer> authorError) {
        this.authorError = authorError;
    }

    public ObservableField<String> getEmail() {
        return email;
    }

    public void setEmail(ObservableField<String> email) {
        this.email = email;
    }

    public ObservableField<Integer> getEmailError() {
        return emailError;
    }

    public void setEmailError(ObservableField<Integer> emailError) {
        this.emailError = emailError;
    }

    public ObservableField<String> getComment() {
        return comment;
    }

    public void setComment(ObservableField<String> comment) {
        this.comment = comment;
    }

    public ObservableField<Integer> getCommentError() {
        return commentError;
    }

    public void setCommentError(ObservableField<Integer> commentError) {
        this.commentError = commentError;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public MutableLiveData<Boolean> getComplete() {
        return complete;
    }

    public void setComplete(MutableLiveData<Boolean> complete) {
        this.complete = complete;
    }
}
