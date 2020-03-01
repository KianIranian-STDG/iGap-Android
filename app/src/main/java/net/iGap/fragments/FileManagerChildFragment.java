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

import com.google.common.collect.Ordering;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.AdapterExplorer;
import net.iGap.databinding.FileManagerChildFragmentBinding;
import net.iGap.helper.FileManager;
import net.iGap.helper.HelperMimeType;
import net.iGap.module.FileUtils;
import net.iGap.module.structs.StructExplorerItem;
import net.iGap.viewmodel.FileManagerChildViewModel;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
                R.drawable.ic_fm_audio,
                G.DIR_APP + "/",
                "To send musics file",
                R.drawable.shape_file_manager_file_2_bg,
                false
        );

        //setup adapter
        mAdapter = new AdapterExplorer(mItems, this);
        binding.rvItems.setAdapter(mAdapter);

    }

    private void fillFoldersItems(String folder) {

        if (folder != null) {

            //show loader
            binding.loader.setVisibility(View.VISIBLE);

            new Thread(() -> {

                File file = new File(folder);
                if (file.isDirectory()) {
                    String[] items = file.list();
                    for (String item : items) {

                        //ignore hidden and temp files
                        if (item.startsWith(".")) continue;
                        if (item.endsWith(".tmp")) continue;

                        String address = folder + "/" + item;
                        File subFile = new File(address);

                        addItemToList(
                                item,
                                subFile.isDirectory() ? R.drawable.ic_fm_folder : HelperMimeType.getMimeResource(address),
                                address,
                                getFileDescription(subFile),
                                subFile.isDirectory() ? R.drawable.shape_file_manager_folder_bg : R.drawable.shape_file_manager_file_bg,
                                true
                        );
                    }

                    Collections.sort(mItems, Ordering.from(new FileManager.SortFolder()).compound(new FileManager.SortFileName()));
                    checkListHasSelectedBefore();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            binding.loader.setVisibility(View.GONE);
                            mAdapter = new AdapterExplorer(mItems, this);
                            binding.rvItems.setAdapter(mAdapter);
                        });
                    }
                }

            }).start();

        }

    }

    private String getFileDescription(File file) {
        if (file.isDirectory()) {
            return "Folder";
        } else {
            float kb, mb;
            kb = file.length() / 1024;
            mb = kb / 1024;
            if (mb == 0) {
                return (new DecimalFormat("##.##").format(kb)) + " kb";
            } else {
                return (new DecimalFormat("##.##").format(mb)) + " mb";
            }
        }
    }

    void onToolbarClicked() {
        binding.rvItems.smoothScrollToPosition(0);
    }

    void onSortClicked(boolean sortByDate) {

    }

    @Override
    public void onFileClicked(String path, int position , boolean isSelected) {
        if(isSelected){
            addItemToParentSelectedList(path);
        }else {
            removeItemFromParentSelectedList(path);
        }
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onFolderClicked(String path, int position) {
        FileManagerChildFragment fragment = FileManagerChildFragment.newInstance(path);
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            ((FileManagerFragment) getParentFragment()).loadFragment(fragment, FileManagerChildFragment.class.getName());
        }
    }

    @Override
    public void onGalleryClicked(String type, int position) {
        //getOpenGallery(type);
    }

    private void checkListHasSelectedBefore() {
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            List<String> selectedList = ((FileManagerFragment) getParentFragment()).getSelectedItemList();
            for (int i = 0 ; i < mItems.size() ; i++){
                for(int j = 0 ; j < selectedList.size() ; j++){
                    if(mItems.get(i).path.equals(selectedList.get(j)))
                        mItems.get(i).isSelected = true;
                }
            }
        }
    }

    private void addItemToParentSelectedList(String item) {
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            ((FileManagerFragment) getParentFragment()).addItemToSelectedList(item);
        }
    }

    private void removeItemFromParentSelectedList(String item) {
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            ((FileManagerFragment) getParentFragment()).removeItemFromSelectedList(item);
        }
    }

    private void closeFileManager() {
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            ((FileManagerFragment) getParentFragment()).closeFileManager();
        }
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

}
