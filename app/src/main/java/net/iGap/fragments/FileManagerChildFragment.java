package net.iGap.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterExplorer;
import net.iGap.databinding.FileManagerChildFragmentBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.module.FileUtils;
import net.iGap.module.structs.StructExplorerItem;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.viewmodel.FileManagerChildViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManagerChildFragment extends BaseFragment implements AdapterExplorer.OnItemClickListenerExplorer {

    public static String ROOT_FILE_MANAGER = "ROOT_FILE_MANAGER";
    private static String FOLDER_NAME = "FOLDER";

    private FileManagerChildFragmentBinding binding;
    private FileManagerChildViewModel mViewModel;
    private AdapterExplorer mAdapter;
    private List<StructExplorerItem> mItems = new ArrayList<>();
    private String mFolderName;

    public static FileManagerChildFragment newInstance(String folder) {
        FileManagerChildFragment fragment = new FileManagerChildFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FOLDER_NAME, folder);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.file_manager_child_fragment, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FileManagerChildViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mFolderName = getArguments().getString(FOLDER_NAME);
            setupRecyclerView();
        }
    }

    private void setupRecyclerView() {
        if (mFolderName.equals(ROOT_FILE_MANAGER)) {
            fillRootAndShowRecent();
        } else {
            fillFoldersItems(mFolderName);
        }
    }

    private void fillRootAndShowRecent() {

        List<String> storageList = FileUtils.getSdCardPathList();

        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath()).exists()) {
            addItemToList(
                    "Internal Storage",
                    R.drawable.ic_fm_internal,
                    Environment.getExternalStorageDirectory().getAbsolutePath(),
                    "Browse your file system",
                    R.drawable.shape_file_manager_file_bg,
                    true
            );
        }

        for (String sdPath : storageList) {
            if (new File(sdPath).exists()) {
                addItemToList(
                        "External Storage",
                        R.drawable.ic_fm_memory,
                        sdPath + "/",
                        "Browse your external storage",
                        R.drawable.shape_file_manager_folder_bg,
                        true
                );
            }
        }

        if (!G.DIR_SDCARD_EXTERNAL.equals("")) {
            if (new File(G.DIR_SDCARD_EXTERNAL).exists()) {
                addItemToList(
                        "iGap SdCard",
                        R.drawable.ic_fm_folder,
                        G.DIR_SDCARD_EXTERNAL + "/",
                        "Browse the app's folder",
                        R.drawable.shape_file_manager_folder_bg,
                        true
                );
            }
        }

        if (new File(G.DIR_APP).exists()) {
            addItemToList(
                    "iGap",
                    R.drawable.ic_fm_folder,
                    G.DIR_APP + "/",
                    "Browse the app's folder",
                    R.drawable.shape_file_manager_file_bg,
                    true
            );
        }

        addItemToList(
                "Pictures",
                R.drawable.ic_fm_image,
                null,
                "To send images file",
                R.drawable.shape_file_manager_file_1_bg,
                false
        );

        addItemToList(
                "Videos",
                R.drawable.ic_fm_video,
                null,
                "To send videos file",
                R.drawable.shape_file_manager_file_1_bg,
                false
        );

        addItemToList(
                "Musics",
                R.drawable.ic_fm_music_file,
                G.DIR_APP + "/",
                "To send musics file",
                R.drawable.shape_file_manager_file_2_bg,
                false
        );

        //setup adapter
        mAdapter = new AdapterExplorer(mItems, this);
        binding.rvItems.setAdapter(mAdapter);

    }

    private void addItemToList(String title, int image, String path) {
        StructExplorerItem item = new StructExplorerItem();
        item.name = title;
        item.image = image;
        item.path = path;
        mItems.add(item);
    }

    private void addItemToList(String title, int image, String path, String desc, int background, boolean isFolderOrFile) {
        StructExplorerItem item = new StructExplorerItem();
        item.name = title;
        item.image = image;
        item.path = path;
        item.backColor = background;
        item.description = desc;
        item.isFolderOrFile = isFolderOrFile;
        mItems.add(item);
    }

    private void fillFoldersItems(String folder) {

    }

    void onToolbarClicked() {
        binding.rvItems.smoothScrollToPosition(0);
    }

    void onSortClicked(boolean sortByDate) {

    }

    @Override
    public void onItemClick(View view, int position) {
        if (mItems.size() > position) {
            if (mItems.get(position).isFolderOrFile) {
                openSubFolderOrSendFile(mItems.get(position).name);
            } else {
                //getOpenGallery(mItems.get(position).name);
            }
        }
    }

    private void openSubFolderOrSendFile(String name) {

    }

    private void getOpenGallery(String name) {
        switch (name) {
            case "Pictures":
                openPhotoGallery();
                break;

            case "Videos":
                openVideoGallery();
                break;

            case "Musics":
                openMusicGallery();
                break;
        }
    }


    private void openPhotoGallery() {
        try {

            HelperPermission.getStoragePermision(getActivity(), new OnGetPermission() {
                @Override
                public void Allow() {

                    if (getActivity() == null) return;
                    Fragment fragment = FragmentGallery.newInstance(FragmentGallery.GalleryMode.PHOTO, true, getString(R.string.gallery), "-1", new FragmentGallery.GalleryFragmentListener() {
                        @Override
                        public void openOsGallery() {
                        }

                        @Override
                        public void onGalleryResult(String path) {
                            sendResultToParent(Arrays.asList(path));
                            closeFileManager();
                        }
                    });
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                }

                @Override
                public void deny() {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openVideoGallery() {
        try {

            HelperPermission.getStoragePermision(getActivity(), new OnGetPermission() {
                @Override
                public void Allow() {
                    if (getActivity() == null) return;
                    Fragment fragment = FragmentGallery.newInstance(FragmentGallery.GalleryMode.VIDEO, new FragmentGallery.GalleryFragmentListener() {
                        @Override
                        public void openOsGallery() {

                        }

                        @Override
                        public void onVideoPickerResult(List<String> videos) {
                            sendResultToParent(videos);
                            closeFileManager();
                        }
                    });
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                }

                @Override
                public void deny() {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openMusicGallery() {
        try {
            HelperPermission.getStoragePermision(getActivity(), new OnGetPermission() {
                @Override
                public void Allow() {
                    if (getActivity() == null) return;
                    Fragment fragment = FragmentGallery.newInstance(FragmentGallery.GalleryMode.MUSIC, new FragmentGallery.GalleryFragmentListener() {
                        @Override
                        public void openOsGallery() {

                        }

                        @Override
                        public void onMusicPickerResult(String music) {
                            sendResultToParent(Arrays.asList(music));
                            closeFileManager();
                        }
                    });
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
                }

                @Override
                public void deny() {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResultToParent(List<String> items) {
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            ((FileManagerFragment) getParentFragment()).sendResult(items);
        }
    }

    private void closeFileManager() {
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            ((FileManagerFragment) getParentFragment()).closeFileManager();
        }
    }

}
