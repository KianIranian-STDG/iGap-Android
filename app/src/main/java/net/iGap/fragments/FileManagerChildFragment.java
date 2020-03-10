package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.adapter.AdapterFileManager;
import net.iGap.databinding.FileManagerChildFragmentBinding;
import net.iGap.module.structs.StructExplorerItem;
import net.iGap.viewmodel.FileManagerChildViewModel;

import java.util.ArrayList;
import java.util.List;

public class FileManagerChildFragment extends BaseFragment implements AdapterFileManager.OnItemClickListenerExplorer {

    public static String ROOT_FILE_MANAGER = "ROOT_FILE_MANAGER";
    private static String FOLDER_NAME = "FOLDER";

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
            fillRootAndShowRecent();
        } else {
            fillFoldersItems(mFolderName);
        }
    }

    private void fillRootAndShowRecent() {

        List<StructExplorerItem> items = mViewModel.getRootItems();

        //setup adapter
        mAdapter = new AdapterFileManager(items, this);
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
                        mAdapter = new AdapterFileManager(items, this);
                        binding.rvItems.setAdapter(mAdapter);

                        if(items.size() == 0){
                            binding.lytNothing.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
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
            return;
        }
        List<StructExplorerItem> searchedResult;
        searchedResult = getSearchResultFromMainList(text);
        if (searchedResult.size() == 0) {
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
