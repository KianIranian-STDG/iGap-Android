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
            getSelectedListAndSetToViewModel();
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

        List<StructExplorerItem> items = mViewModel.getRootItems();

        //setup adapter
        mAdapter = new AdapterExplorer(items, this);
        binding.rvItems.setAdapter(mAdapter);

    }

    private void fillFoldersItems(String folder) {

        if (folder != null) {

            //show loader
            binding.loader.setVisibility(View.VISIBLE);
            mViewModel.getFoldersSubItems(folder , items -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.loader.setVisibility(View.GONE);
                        mAdapter = new AdapterExplorer(items, this);
                        binding.rvItems.setAdapter(mAdapter);
                    });
                }
            });
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

    private void getSelectedListAndSetToViewModel() {
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            List<String> selectedList = ((FileManagerFragment) getParentFragment()).getSelectedItemList();
            mViewModel.setSelectedList(selectedList);
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

}
