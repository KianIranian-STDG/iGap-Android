package net.iGap.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterExplorer;
import net.iGap.databinding.FileManagerChildFragmentBinding;
import net.iGap.module.FileUtils;
import net.iGap.module.structs.StructExplorerItem;
import net.iGap.viewmodel.FileManagerChildViewModel;

import java.io.File;
import java.util.ArrayList;
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
                    R.drawable.shape_file_manager_file_bg
            );
        }

        for (String sdPath : storageList) {
            if (new File(sdPath).exists()) {
                addItemToList(
                        "External Storage",
                        R.drawable.ic_fm_memory,
                        sdPath + "/",
                        "Browse your external storage",
                        R.drawable.shape_file_manager_folder_bg
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
                        R.drawable.shape_file_manager_folder_bg
                );
            }
        }

        if (new File(G.DIR_APP).exists()) {
            addItemToList(
                    "iGap",
                    R.drawable.ic_fm_folder,
                    G.DIR_APP + "/",
                    "Browse the app's folder",
                    R.drawable.shape_file_manager_file_bg
            );
        }

        addItemToList(
                "Pictures",
                R.drawable.ic_fm_image,
                null,
                "To send images file",
                R.drawable.shape_file_manager_file_1_bg
        );

        addItemToList(
                "Videos",
                R.drawable.ic_fm_video,
                null,
                "To send videos file",
                R.drawable.shape_file_manager_file_1_bg
        );

        addItemToList(
                "Musics",
                R.drawable.ic_fm_music_file,
                G.DIR_APP + "/",
                "To send musics file",
                R.drawable.shape_file_manager_file_2_bg
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

    private void addItemToList(String title, int image, String path, String desc, int background) {
        StructExplorerItem item = new StructExplorerItem();
        item.name = title;
        item.image = image;
        item.path = path;
        item.backColor = background;
        item.description = desc;
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

    }
}
