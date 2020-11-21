package net.iGap.viewmodel.sticker;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.libs.emojiKeyboard.emoji.EmojiManager;
import net.iGap.module.FileUtils;
import net.iGap.observers.interfaces.OnResponse;
import net.iGap.observers.interfaces.ResponseCallback;
import net.iGap.observers.rx.IGSingleObserver;
import net.iGap.observers.rx.ObserverViewModel;
import net.iGap.repository.StickerRepository;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RemoveStickerViewModel extends ObserverViewModel {
    private StickerRepository repository;

    private MutableLiveData<Integer> removeStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> clearRecentStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> emptyRecentStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> recyclerVisibilityRecentStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<String> stickerFileSizeLiveData = new MutableLiveData<>();
    private MutableLiveData<List<StructIGStickerGroup>> stickersLiveData = new MutableLiveData<>();

    public RemoveStickerViewModel() {
        repository = StickerRepository.getInstance();
        stickerFileSizeLiveData.postValue(getStickerFolderSize());
    }

    @Override
    public void subscribe() {
        addFileObserver();
        addStickerObserver();
    }

    private void addStickerObserver() {
        Disposable disposable = repository.getMySticker()
                .doOnNext(structIGStickerGroups -> stickersLiveData.postValue(structIGStickerGroups))
                .map(List::size)
                .subscribe(this::checkStickerSize);
        backgroundDisposable.add(disposable);
    }

    private void addFileObserver() {
        Disposable disposable = Flowable.interval(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(integer -> stickerFileSizeLiveData.postValue(getStickerFolderSize()));
        mainThreadDisposable.add(disposable);
    }

    private void checkStickerSize(int size) {
        if (size > 0) {
            recyclerVisibilityRecentStickerLiveData.postValue(View.VISIBLE);
            emptyRecentStickerLiveData.postValue(View.GONE);
        } else {
            recyclerVisibilityRecentStickerLiveData.postValue(View.GONE);
            emptyRecentStickerLiveData.postValue(View.VISIBLE);
        }
    }

    public void removeStickerFromMySticker(StructIGStickerGroup stickerGroup, OnResponse onResponse) {
        repository.removeStickerGroupFromMyStickers(stickerGroup)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new IGSingleObserver<StructIGStickerGroup>(backgroundDisposable) {
                    @Override
                    public void onSuccess(StructIGStickerGroup stickerGroup) {
                        onResponse.onReceived(stickerGroup, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        onResponse.onReceived(null, e);
                    }
                });
    }

    public void clearRecentSticker() {
        clearRecentStickerLiveData.postValue(View.VISIBLE);
        repository.clearRecentSticker(new ResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                clearRecentStickerLiveData.postValue(View.GONE);
            }

            @Override
            public void onError(String error) {
                clearRecentStickerLiveData.postValue(View.GONE);
            }

            @Override
            public void onFailed() {

            }
        });
    }

    public void clearRecentEmoji() {
        EmojiManager.getInstance().clearRecentEmoji();
    }

    public void clearStickerFromInternalStorage() {
        repository.clearStickerInternalStorage();
    }

    private String getStickerFolderSize() {
        return FileUtils.formatFileSize(getFolderByteSize());
    }

    private long getFolderByteSize() throws RuntimeException {
        File dir = new File(G.downloadDirectoryPath);
        long size = 0;

        if (dir.listFiles() != null) {

            for (File file : dir.listFiles()) {
                if (file != null) {
                    if (file.isFile()) {
                        size += file.length();
                    } else {
                        size += getFolderByteSize();
                    }
                } else {
                    return size;
                }
            }
        }
        return size;
    }

    public MutableLiveData<Integer> getRemoveStickerLiveData() {
        return removeStickerLiveData;
    }

    public MutableLiveData<Integer> getClearRecentStickerLiveData() {
        return clearRecentStickerLiveData;
    }

    public MutableLiveData<String> getStickerFileSizeLiveData() {
        return stickerFileSizeLiveData;
    }

    public MutableLiveData<Integer> getEmptyRecentStickerLiveData() {
        return emptyRecentStickerLiveData;
    }

    public MutableLiveData<Integer> getRecyclerVisibilityRecentStickerLiveData() {
        return recyclerVisibilityRecentStickerLiveData;
    }

    public MutableLiveData<List<StructIGStickerGroup>> getStickersLiveData() {
        return stickersLiveData;
    }
}
