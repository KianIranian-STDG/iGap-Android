package net.iGap.viewmodel.sticker;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.interfaces.ObserverView;
import net.iGap.module.FileUtils;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.viewmodel.BaseViewModel;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RemoveStickerViewModel extends BaseViewModel implements ObserverView {
    private StickerRepository repository;
    private CompositeDisposable compositeDisposable;

    private MutableLiveData<Integer> removeStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> clearRecentStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> emptyRecentStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> recyclerVisibilityRecentStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<String> stickerFileSizeLiveData = new MutableLiveData<>();
    private MutableLiveData<List<StructIGStickerGroup>> stickersLiveData = new MutableLiveData<>();

    public RemoveStickerViewModel() {
        repository = StickerRepository.getInstance();
        compositeDisposable = new CompositeDisposable();
        stickerFileSizeLiveData.postValue(getStickerFolderSize());
        subscribe();
    }

    @Override
    public void subscribe() {
        addFileObserver();
        addStickerObserver();
        getUserSticker();
    }

    private void getUserSticker() {
        Disposable disposable = repository.getUserStickersGroup()
                .subscribe(structIGStickerGroups -> stickersLiveData.postValue(structIGStickerGroups));
        compositeDisposable.add(disposable);
    }

    private void addStickerObserver() {
        Disposable disposable = repository.getMySticker()
                .map(List::size)
                .subscribe(this::checkStickerSize);
        compositeDisposable.add(disposable);
    }

    private void addFileObserver() {
        Disposable disposable = Flowable.interval(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(integer -> stickerFileSizeLiveData.postValue(getStickerFolderSize()));
        compositeDisposable.add(disposable);
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

    public void removeStickerFromFavorite(String groupId, int adapterPosition) {
        repository.removeStickerGroupFromFavorite(groupId, new ResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                removeStickerLiveData.postValue(adapterPosition);
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void onFailed() {

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

    @Override
    public void unsubscribe() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }
}
