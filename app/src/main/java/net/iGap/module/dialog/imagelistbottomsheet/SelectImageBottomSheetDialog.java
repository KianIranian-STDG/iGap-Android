package net.iGap.module.dialog.imagelistbottomsheet;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.AdapterCamera;
import net.iGap.adapter.items.BottomSheetItem;
import net.iGap.databinding.FragmentBottomSheetSelectImageBinding;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperPermission;
import net.iGap.module.AttachFile;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.dialog.BaseBottomSheet;
import net.iGap.module.dialog.BottomSheetItemClickCallback;
import net.iGap.module.dialog.BottomSheetListAdapter;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.observers.interfaces.OnClickCamera;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnPathAdapterBottomSheet;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.selector.ResolutionSelectorsKt;

import static android.content.Context.MODE_PRIVATE;
import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static net.iGap.fragments.FragmentChat.getAllShownImagesPath;

public class SelectImageBottomSheetDialog extends BaseBottomSheet {

    private List<String> itemList;
    private int range;
    private BottomSheetItemClickCallback bottomSheetItemClickCallback;
    private OnPathAdapterBottomSheet onPathAdapterBottomSheet;
    private FragmentBottomSheetSelectImageBinding binding;
    private FastItemAdapter fastItemAdapter;
    private Fotoapparat fotoapparatSwitcher;
    private SharedPreferences sharedPreferences;
    private boolean isPermissionCamera = false;
    private boolean isCameraAttached = false;
    private boolean isCameraStart = false;
    private boolean isNewBottomSheet = true;


    //usage
    /*List<String> items = new ArrayList<>();
                        items.add(getString(R.string.gallery));
                        items.add(getString(R.string.remove));
                        new SelectImageBottomSheetDialog().setData(items, 0, new BottomSheetItemClickCallback() {
        @Override
        public void onClick(int position) {

        }
    }).show(getFragmentManager(), "test");*/


    public SelectImageBottomSheetDialog setData(List<String> itemListId, int range, BottomSheetItemClickCallback bottomSheetItemClickCallback) {
        this.itemList = itemListId;
        this.range = range;
        this.bottomSheetItemClickCallback = bottomSheetItemClickCallback;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bottom_sheet_select_image, container, false);

        BottomSheetListAdapter bottomSheetListAdapter = new BottomSheetListAdapter(itemList, range, position -> {
            dismiss();
            bottomSheetItemClickCallback.onClick(position);
        });
        binding.bottomSheetList.setAdapter(bottomSheetListAdapter);
        sharedPreferences = G.fragmentActivity.getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
        initAttach();

        return binding.getRoot();
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    private void initAttach() {

        fastItemAdapter = new FastItemAdapter();

        onPathAdapterBottomSheet = new OnPathAdapterBottomSheet() {
            @Override
            public void path(String path, boolean isCheck, boolean isEdit, StructBottomSheet mList, int id) {
                if (isEdit) {
                    dismiss();
                    if (getActivity() != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), FragmentEditImage.newInstance(null, true, false, id)).setReplace(false).load();
                    }
                } else {
                    if (isCheck) {
                        StructBottomSheet item = new StructBottomSheet();
                        item.setPath(path);
                        item.setText("");
                        item.setId(id);
                        FragmentEditImage.textImageList.put(path, item);
                    } else {
                        FragmentEditImage.textImageList.remove(path);
                    }
                }
            }
        };


        FragmentEditImage.completeEditImage = new FragmentEditImage.CompleteEditImage() {
            @Override
            public void result(String path, String message, HashMap<String, StructBottomSheet> textImageList) {

                if (textImageList.size() == 0) {
                    return;
                }

                /**
                 * sort list
                 */
                ArrayList<StructBottomSheet> itemList = new ArrayList<>();
                for (Map.Entry<String, StructBottomSheet> items : textImageList.entrySet()) {
                    itemList.add(items.getValue());
                }

                Collections.sort(itemList);

                for (StructBottomSheet item : itemList) {
                    Log.wtf("select image bottom sheet", "value of path: " + item.getPath());
                }
            }
        };

        binding.imageList.setLayoutManager(new LinearLayoutManager(binding.imageList.getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.imageList.setItemViewCacheSize(100);
        binding.imageList.setAdapter(fastItemAdapter);

        binding.imageList.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NotNull final View view) {
                if (isPermissionCamera) {

                    if (binding.imageList.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = true;
                    }
                    if (isCameraAttached) {
                        if (fotoapparatSwitcher != null) {
                            if (!isCameraStart) {
                                isCameraStart = true;
                                try {
                                    /*G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {*/
                                    fotoapparatSwitcher.start();
                                        /*}
                                    }, 50);*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            if (!isCameraStart) {
                                isCameraStart = true;
                                try {
                                    /*G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {*/
                                    fotoapparatSwitcher = Fotoapparat.with(G.fragmentActivity).into(view.findViewById(R.id.cameraView))           // view which will draw the camera preview
                                            .photoResolution(ResolutionSelectorsKt.highestResolution())   // we want to have the biggest photo possible
                                            .lensPosition(back())       // we want back camera
                                            .build();

                                    fotoapparatSwitcher.start();
                                        /*}
                                    }, 100);*/
                                } catch (IllegalStateException e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull final View view) {

                if (isPermissionCamera) {
                    if (binding.imageList.getChildAdapterPosition(view) == 0) {
                        isCameraAttached = false;
                    }
                    if (!isCameraAttached) {
                        if (fotoapparatSwitcher != null) {
                            //                    if (isCameraStart && ( rcvBottomSheet.getChildAdapterPosition(view)> 4  || rcvBottomSheet.computeHorizontalScrollOffset() >200)){
                            if (isCameraStart) {

                                try {
                                    fotoapparatSwitcher.stop();
                                    isCameraStart = false;
                                } catch (Exception e) {
                                    e.getMessage();
                                }
                            }
                        } else {
                            if (!isCameraStart) {
                                isCameraStart = false;
                                try {
                                    /*G.handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {*/
                                    fotoapparatSwitcher = Fotoapparat.with(G.fragmentActivity).into(view.findViewById(R.id.cameraView))           // view which will draw the camera preview
                                            .photoResolution(ResolutionSelectorsKt.highestResolution())   // we want to have the biggest photo possible
                                            .lensPosition(back())       // we want back camera
                                            .build();

                                    fotoapparatSwitcher.stop();
                                        /*}
                                    }, 100);*/
                                } catch (IllegalStateException e) {
                                    e.getMessage();
                                }
                            }
                        }
                    }
                }
            }
        });

        binding.imageList.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(final View v) {
                if (isPermissionCamera) {

                    if (fotoapparatSwitcher != null) {
                        /*G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {*/
                        if (!isCameraStart) {
                            fotoapparatSwitcher.start();
                            isCameraStart = true;
                        }
                            /*}
                        }, 50);*/
                    }
                }
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (isPermissionCamera) {
                    if (fotoapparatSwitcher != null) {
                        if (isCameraStart) {
                            fotoapparatSwitcher.stop();
                            isCameraStart = false;
                        }
                    }
                }
            }
        });

        if (HelperPermission.grantedUseStorage()) {
            binding.imageList.setVisibility(View.VISIBLE);
        } else {
            binding.imageList.setVisibility(View.GONE);
        }

        fastItemAdapter.clear();
        if(FragmentEditImage.itemGalleryList == null){
            FragmentEditImage.itemGalleryList = new ArrayList<>();
        }
        if (isNewBottomSheet || FragmentEditImage.itemGalleryList.size() <= 1) {

            FragmentEditImage.itemGalleryList.clear();
            if (isNewBottomSheet) {
                FragmentEditImage.textImageList.clear();
            }

            try {
                HelperPermission.getStoragePermission(getContext(), new OnGetPermission() {
                    @Override
                    public void Allow() {
                        FragmentEditImage.itemGalleryList = getAllShownImagesPath(getActivity());
                        checkCameraAndLoadImage();
                    }

                    @Override
                    public void deny() {
                        loadImageGallery();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            checkCameraAndLoadImage();
        }
    }

    private void checkCameraAndLoadImage() {
        boolean isCameraButtonSheet = sharedPreferences.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
        if (isCameraButtonSheet) {
            try {
                HelperPermission.getCameraPermission(G.fragmentActivity, new OnGetPermission() {
                    @Override
                    public void Allow() {
                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                fastItemAdapter.add(new AdapterCamera("", new OnClickCamera() {
                                    @Override
                                    public void onclickCamera() {
                                        try {
                                            dismiss();
                                            if (getActivity() != null) {
                                                new AttachFile(getActivity()).requestTakePicture(SelectImageBottomSheetDialog.this);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).withIdentifier(99));
                                for (int i = 0; i < FragmentEditImage.itemGalleryList.size(); i++) {
                                    fastItemAdapter.add(new BottomSheetItem(FragmentEditImage.itemGalleryList.get(i), onPathAdapterBottomSheet).withIdentifier(100 + i));
                                }
                                isPermissionCamera = true;
                            }
                        });
                        G.handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isAdded()) {
                                    /*showBottomSheet();*/
                                }
                            }
                        }, 100);
                    }

                    @Override
                    public void deny() {

                        loadImageGallery();

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadImageGallery();
        }
    }

    private void loadImageGallery() {

        G.handler.post(() -> {
            for (int i = 0; i < FragmentEditImage.itemGalleryList.size(); i++) {
                fastItemAdapter.add(new BottomSheetItem(FragmentEditImage.itemGalleryList.get(i), onPathAdapterBottomSheet).withIdentifier(100 + i));
            }
        });

        G.handler.postDelayed(() -> {
            if (isAdded()) {
                /*showBottomSheet();*/
            }
        }, 100);

    }
}
