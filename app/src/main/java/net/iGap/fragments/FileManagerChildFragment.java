package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.common.collect.Ordering;

import net.iGap.R;
import net.iGap.adapter.AdapterFileManager;
import net.iGap.databinding.FileManagerChildFragmentBinding;
import net.iGap.helper.FileManager;
import net.iGap.model.GalleryMusicModel;
import net.iGap.module.structs.StructExplorerItem;
import net.iGap.viewmodel.FileManagerChildViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileManagerChildFragment extends BaseFragment implements AdapterFileManager.OnItemClickListenerExplorer {

    static String ROOT_FILE_MANAGER = "ROOT_FILE_MANAGER";
    static String FILE_MANAGER_IMAGE = "FILE_MANAGER_GALLERY_IMAGE";
    static String FILE_MANAGER_VIDEO = "FILE_MANAGER_GALLERY_VIDEO";
    static String FILE_MANAGER_MUSIC = "FILE_MANAGER_GALLERY_MUSIC";
    private static String FOLDER_NAME = "FOLDER";
    private static String GALLERY_FOLDER_ID = "FOLDER_ID";

    private FileManagerChildFragmentBinding binding;
    private FileManagerChildViewModel mViewModel;
    private AdapterFileManager mAdapter;
    private String mFolderName;

    public static FileManagerChildFragment newInstance(String folder) {
        FileManagerChildFragment fragment = new FileManagerChildFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FOLDER_NAME, folder);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static FileManagerChildFragment newInstance(String type , String id) {
        FileManagerChildFragment fragment = new FileManagerChildFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FOLDER_NAME, type);
        bundle.putString(GALLERY_FOLDER_ID, id);
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
            setupListeners();
        }
    }

    private void setupListeners() {

        binding.btnBack.setOnClickListener(v -> {
            closeSearch();
            popBackStackFragment();
        });

    }

    private void setupRecyclerView() {
        if (mFolderName.equals(ROOT_FILE_MANAGER)) {
            setupListItems(mViewModel.getRootItems());
        }else if(mFolderName.equals(FILE_MANAGER_IMAGE)){
            fillWithGalleryItems("image");
        }else if(mFolderName.equals(FILE_MANAGER_VIDEO)){
            fillWithGalleryItems("video");
        }else if(mFolderName.equals(FILE_MANAGER_MUSIC)){
            fillWithGalleryItems("music");
        } else {
            fillFoldersItems(mFolderName);
        }
    }

    private void fillWithGalleryItems(String type){
        binding.loader.setVisibility(View.VISIBLE);
        switch (type){
            case "image":

                break;

            case "video":
                break;

            case "music":
                FileManager.getDeviceMusics(getContext() , false , result -> {
                    mViewModel.setItems(mViewModel.convertMusicGalleryItems(result));
                    mViewModel.checkListHasSelectedBefore();
                    if(getActivity() != null){
                        getActivity().runOnUiThread(() -> setupListItems(mViewModel.getItems()));
                    }
                });
                break;
        }
    }

    private void fillFoldersItems(String folder) {

        if (folder != null) {

            //show loader
            binding.loader.setVisibility(View.VISIBLE);
            mViewModel.getFoldersSubItems(folder , items -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        setupListItems(items);
                    });
                }
            });
        }

    }

    private void setupListItems(List<StructExplorerItem> items) {
        binding.loader.setVisibility(View.GONE);
        mAdapter = new AdapterFileManager(items, this);
        binding.rvItems.setAdapter(mAdapter);

        if(items.size() == 0){
            binding.btnBack.setVisibility(View.VISIBLE);
            binding.lytNothing.setVisibility(View.VISIBLE);
        }
    }

    void onToolbarClicked() {
        binding.rvItems.smoothScrollToPosition(0);
    }

    void onSortClicked(Boolean sortByDate) {
        if(mFolderName.equals(ROOT_FILE_MANAGER)) return;
        mViewModel.sortList(sortByDate);
        //mAdapter.removeAll();
        mAdapter.update(mViewModel.getItems());
    }

    void doSearch(String text) {
        if (text == null) {
            binding.lytNothing.setVisibility(View.GONE);
            binding.rvItems.setVisibility(View.VISIBLE);
            mAdapter.update(mViewModel.getItems());
            if(mViewModel.getItems().size() == 0){
                binding.btnBack.setVisibility(View.VISIBLE);
                binding.lytNothing.setVisibility(View.VISIBLE);
            }
            return;
        }
        List<StructExplorerItem> searchedResult;
        searchedResult = getSearchResultFromMainList(text);
        if (searchedResult.size() == 0) {
            binding.btnBack.setVisibility(View.GONE);
            binding.lytNothing.setVisibility(View.VISIBLE);
            binding.rvItems.setVisibility(View.GONE);
        } else {
            binding.lytNothing.setVisibility(View.GONE);
            binding.rvItems.setVisibility(View.VISIBLE);
            mAdapter.update(searchedResult);
        }
    }

    private List<StructExplorerItem> getSearchResultFromMainList(String text) {
        List<StructExplorerItem> result = new ArrayList<>();
        for (int i = 0; i < mViewModel.getItems().size(); i++) {
            if (mViewModel.getItems().get(i).nameStr != null && mViewModel.getItems().get(i).nameStr.toLowerCase().contains(text.toLowerCase().trim())) {
                result.add(mViewModel.getItems().get(i));
            }
        }
        return result;
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
        closeSearch();
        FileManagerChildFragment fragment = FileManagerChildFragment.newInstance(path);
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            FileManagerFragment parent = ((FileManagerFragment) getParentFragment());
            parent.setToolbarIconVisibility(true);
            parent.loadFragment(fragment, FileManagerChildFragment.class.getName());
        }
    }

    @Override
    public void onGalleryClicked(int type, int position) {
        String tempType = null ;

        switch (type){
            case R.string.images:
                tempType = FILE_MANAGER_IMAGE;
                break;

            case R.string.videos:
                tempType = FILE_MANAGER_VIDEO;
                break;

            case R.string.audios:
                tempType = FILE_MANAGER_MUSIC;
                break;
        }

        closeSearch();
        FileManagerChildFragment fragment = FileManagerChildFragment.newInstance(tempType);
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            FileManagerFragment parent = ((FileManagerFragment) getParentFragment());
            parent.setToolbarIconVisibility(true);
            parent.loadFragment(fragment, FileManagerChildFragment.class.getName());
        }

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

    private void closeSearch() {
        if (getParentFragment() != null && getParentFragment() instanceof FileManagerFragment) {
            ((FileManagerFragment) getParentFragment()).closeSearch();
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
