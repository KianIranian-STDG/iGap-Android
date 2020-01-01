package net.iGap.viewmodel.sticker;

import android.view.View;

import androidx.lifecycle.MutableLiveData;

import net.iGap.G;
import net.iGap.api.apiService.ResponseCallback;
import net.iGap.api.errorhandler.ErrorModel;
import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.interfaces.ObserverView;
import net.iGap.module.FileUtils;
import net.iGap.repository.sticker.StickerRepository;
import net.iGap.viewmodel.BaseViewModel;

import java.io.File;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RemoveStickerViewModel extends BaseViewModel implements ObserverView {
    private StickerRepository repository;
    private CompositeDisposable compositeDisposable;

    private MutableLiveData<Integer> removeStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> clearRecentStickerLiveData = new MutableLiveData<>();
    private MutableLiveData<String> stickerFileSizeLiveData = new MutableLiveData<>();

    public RemoveStickerViewModel() {
        repository = new StickerRepository();
        compositeDisposable = new CompositeDisposable();
        stickerFileSizeLiveData.postValue(getStickerFolderSize());
        subscribe();
    }

    public List<StructIGStickerGroup> getFavoriteStickers() {
        return repository.getMyStickers();
    }

    public void removeStickerFromFavorite(String groupId, int adapterPosition) {
        repository.removeStickerGroupFromFavorite(groupId, new ResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                removeStickerLiveData.postValue(adapterPosition);
            }

            @Override
            public void onError(ErrorModel error) {

            }
        });
    }

    @Override
    public void subscribe() {
        Disposable disposable = repository.getIntervalFlowable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(integer -> stickerFileSizeLiveData.postValue(getStickerFolderSize()));
        compositeDisposable.add(disposable);
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

    public void clearRecentSticker() {
        clearRecentStickerLiveData.postValue(View.VISIBLE);
        repository.clearRecentSticker(new ResponseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                clearRecentStickerLiveData.postValue(View.GONE);
            }

            @Override
            public void onError(ErrorModel error) {
                clearRecentStickerLiveData.postValue(View.GONE);
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

    @Override
    public void unsubscribe() {
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }
    }
}
