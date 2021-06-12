package net.iGap.story;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;


import net.iGap.activities.ActivityTrimVideo;
import net.iGap.adapter.AdapterGalleryPhoto;
import net.iGap.fragments.BaseFragment;

import net.iGap.fragments.FragmentEditImage;
import net.iGap.fragments.FragmentGallery;
import net.iGap.helper.FileManager;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageHelper;
import net.iGap.helper.LayoutCreator;

import net.iGap.model.GalleryAlbumModel;
import net.iGap.model.GalleryItemModel;
import net.iGap.module.AndroidUtils;
import net.iGap.module.AttachFile;
import net.iGap.module.MaterialDesignTextView;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.Theme;
import net.iGap.module.dialog.ChatAttachmentPopup;
import net.iGap.module.structs.StructBottomSheet;
import net.iGap.observers.interfaces.GalleryItemListener;
import net.iGap.observers.interfaces.OnRotateImage;
import net.iGap.observers.interfaces.ToolbarListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class StoryGalleryFragment extends BaseFragment {
    LinearLayout rootView;
    private RecyclerView mainRecyclerView;
    private NestedScrollableHost nestedScrollableHost;
    private FrameLayout toolbarView;
    private MaterialDesignTextView backIcon;
    private MaterialDesignTextView sendIcon;
    private AppCompatTextView toolbarTitle;
    private AdapterGallery adapterGalleryPhoto;
    private OnRVScrolled onRVScrolled;
    private Typeface tfMain;

    public StoryGalleryFragment(OnRVScrolled onRVScrolled) {
        this.onRVScrolled = onRVScrolled;
    }

    @Override
    public void onResume() {
        super.onResume();
        mainRecyclerView.getParent().requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = new LinearLayout(context);
        rootView = (LinearLayout) fragmentView;
        rootView.setOrientation(LinearLayout.VERTICAL);

        if (tfMain == null)
            tfMain = ResourcesCompat.getFont(getContext(), R.font.main_font);


        toolbarView = new FrameLayout(context);


        toolbarView.setBackgroundResource(new Theme().getToolbarDrawableSharpe(getContext()));

        backIcon = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        backIcon.setText(getString(R.string.back_icon));
        backIcon.setTextColor(context.getResources().getColor(R.color.white));
        backIcon.setTextSize(22);
        backIcon.setGravity(Gravity.CENTER);
        toolbarView.addView(backIcon, LayoutCreator.createFrame(40, 40, Gravity.LEFT | Gravity.CENTER_VERTICAL, 8, 0, 0, 0));

        sendIcon = new MaterialDesignTextView(new ContextThemeWrapper(context, R.style.myIconToolbarStyle));
        sendIcon.setText(getString(R.string.md_send_button));
        sendIcon.setTextColor(context.getResources().getColor(R.color.white));
        sendIcon.setTextSize(22);
        sendIcon.setGravity(Gravity.CENTER);
        toolbarView.addView(sendIcon, LayoutCreator.createFrame(40, 40, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 8, 0, 0, 0));

        toolbarTitle = new AppCompatTextView(context);
        toolbarTitle.setText(getString(R.string.gallery));
        toolbarTitle.setTypeface(tfMain);
        toolbarTitle.setTextSize(22);
        toolbarTitle.setTextColor(getResources().getColor(R.color.whit_background));
        toolbarView.addView(toolbarTitle, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 0, 0, 0));

        rootView.addView(toolbarView, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 60, Gravity.CENTER | Gravity.TOP));


        nestedScrollableHost = new NestedScrollableHost(context);
        mainRecyclerView = new RecyclerView(context);
        mainRecyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        nestedScrollableHost.addView(mainRecyclerView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));

        rootView.addView(nestedScrollableHost, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, 0, 1F, Gravity.CENTER));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FileManager.getFolderPhotosById(getContext(), "-1", result -> {
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                adapterGalleryPhoto = new AdapterGallery(true);
                adapterGalleryPhoto.setMultiState(true);
                adapterGalleryPhoto.setPhotosItem(result);
                adapterGalleryPhoto.setListener(new GalleryItemListener() {
                    @Override
                    public void onItemClicked(String path, String id) {
                        if (path == null) return;
                        openImageForEdit(path);
                    }

                    @Override
                    public void onMultiSelect(int size) {
                        toolbarView.setVisibility(View.VISIBLE);

                        sendIcon.setVisibility(View.VISIBLE);
                        backIcon.setVisibility(View.VISIBLE);
                        if (size > 0) {
                            sendIcon.setText(R.string.md_send_button);

                        } else {
                            sendIcon.setText(R.string.close_icon);
                        }

                    }
                });
                mainRecyclerView.setAdapter(adapterGalleryPhoto);

            });
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galleryOnBackPressed();
            }
        });
        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPhotoMultiSelectAndSendToEdit();
            }
        });

        // initToolbar(rootView);

    }

    private void checkPhotoMultiSelectAndSendToEdit() {
        if (adapterGalleryPhoto == null) return;
        if (adapterGalleryPhoto.getMultiSelectState()) {
            sendIcon.setText(R.string.edit_icon);
            if (adapterGalleryPhoto.getSelectedPhotos().size() > 0)
                sendSelectedPhotos(adapterGalleryPhoto.getSelectedPhotos());
        } else {
            sendIcon.setText(R.string.close_icon);
        }
        adapterGalleryPhoto.setMultiSelectState(!adapterGalleryPhoto.getMultiSelectState());
    }

    private void sendSelectedPhotos(List<GalleryItemModel> selectedPhotos) {
        if (getActivity() == null || selectedPhotos.size() == 0) return;

        FragmentEditImage.itemGalleryList.clear();
        FragmentEditImage.textImageList.clear();
        for (GalleryItemModel photo : selectedPhotos) {
            FragmentEditImage.insertItemList(photo.getAddress(), "", false);
        }

        PhotoViewer photoViewer = PhotoViewer.newInstance((ArrayList<GalleryItemModel>) selectedPhotos);
        new HelperFragment(getActivity().getSupportFragmentManager(), photoViewer).setReplace(false).load();


    }

    private void openImageForEdit(String path) {
        FragmentEditImage.itemGalleryList.clear();
        FragmentEditImage.textImageList.clear();

        FragmentEditImage fragmentEditImage = FragmentEditImage.newInstance(null, true, false, 0);
        fragmentEditImage.setIsReOpenChatAttachment(false);
        ImageHelper.correctRotateImage(path, true, new OnRotateImage() {
            @Override
            public void startProcess() {

            }

            @Override
            public void success(String newPath) {
                G.handler.post(() -> {
                    PhotoViewer photoViewer = PhotoViewer.newInstance(newPath);
                    FragmentEditImage.insertItemList(newPath, "", false);
                    if (getActivity() != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), photoViewer).setReplace(false).load();
                    }
                });
            }
        });

        fragmentEditImage.setGalleryListener(() -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack(FragmentGallery.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

    }

    private void galleryOnBackPressed() {

        if (adapterGalleryPhoto != null && adapterGalleryPhoto.getMultiSelectState()) {
            sendIcon.setVisibility(View.GONE);
            adapterGalleryPhoto.setMultiSelectState(!adapterGalleryPhoto.getMultiSelectState());
            return;
        }


        onRVScrolled.changeItem();
        adapterGalleryPhoto.setMultiSelectState(false);

    }


    public interface OnRVScrolled {
        void scrolled(boolean isScrolled);

        void changeItem();
    }

    public class AdapterGallery extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private boolean isPhotoMode;
        private boolean isMultiSelect;
        private boolean canMultiSelect;
        private List<GalleryAlbumModel> albumsItem = new ArrayList<>();
        private List<GalleryItemModel> photosItem = new ArrayList<>();
        private List<GalleryItemModel> mSelectedPhotos = new ArrayList<>();
        private GalleryItemListener listener;

        public AdapterGallery(boolean isPhotoMode) {
            this.isPhotoMode = isPhotoMode;
        }

        public void setAlbumsItem(List<GalleryAlbumModel> albumsItem) {
            this.albumsItem = albumsItem;
            notifyDataSetChanged();
        }

        public void setPhotosItem(List<GalleryItemModel> photosItem) {
            this.photosItem = photosItem;
            notifyDataSetChanged();
        }

        public void setListener(GalleryItemListener listener) {
            this.listener = listener;
        }

        public void setMultiSelectState(boolean enable) {
            this.isMultiSelect = enable;
            if (!enable) mSelectedPhotos.clear();
            notifyDataSetChanged();
        }

        public void setMultiState(boolean canMultiSelect) {
            this.canMultiSelect = canMultiSelect;
        }

        public List<GalleryItemModel> getPhotosItem() {
            return photosItem;
        }

        public List<GalleryAlbumModel> getAlbumsItem() {
            return albumsItem;
        }

        public boolean getMultiSelectState() {
            return isMultiSelect;
        }

        public List<GalleryItemModel> getSelectedPhotos() {
            return mSelectedPhotos;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new AdapterGallery.ViewHolderGallery(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_image, parent, false));
                case 1:
                    return new AdapterGallery.VideoViewHolderGallery(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_video, parent, false));
            }
            return new AdapterGallery.ViewHolderGallery(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery_image, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    ViewHolderGallery viewHolderGallery = (ViewHolderGallery) holder;
                    if (isMultiSelect) {
                        viewHolderGallery.check.setVisibility(View.VISIBLE);
                        viewHolderGallery.check.setChecked(mSelectedPhotos.contains(photosItem.get(position)));
                    } else {
                        viewHolderGallery.check.setChecked(false);
                        viewHolderGallery.check.setVisibility(View.GONE);
                    }

                    viewHolderGallery.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            if (mSelectedPhotos.size() == 10) {
                                Toast.makeText(getContext(), "نمی توانید بیش از ۱۰ مورد رسانه را به اشتراک بگذارید", Toast.LENGTH_LONG).show();
                                viewHolderGallery.check.setChecked(false);
                            } else if (!mSelectedPhotos.contains(photosItem.get(viewHolderGallery.getAdapterPosition()))) {
                                mSelectedPhotos.add(photosItem.get(viewHolderGallery.getAdapterPosition()));
                            }


                        } else {
                            mSelectedPhotos.remove(photosItem.get(viewHolderGallery.getAdapterPosition()));
                        }
                    });


                    viewHolderGallery.image.setOnLongClickListener(v -> {
                        if (canMultiSelect) {
                            if (!isMultiSelect && isPhotoMode) {
                                viewHolderGallery.check.setChecked(!viewHolderGallery.check.isChecked());
                                listener.onMultiSelect(mSelectedPhotos.size());
                                setMultiSelectState(!getMultiSelectState());
                            }
                            return true;
                        }
                        return false;
                    });

                    //handle item click
                    viewHolderGallery.image.setOnClickListener(v -> {

                        if (!isMultiSelect) {
                            listener.onItemClicked(
                                    isPhotoMode ? photosItem.get(viewHolderGallery.getAdapterPosition()).getAddress() : albumsItem.get(viewHolderGallery.getAdapterPosition()).getCaption(),
                                    isPhotoMode ? null : albumsItem.get(viewHolderGallery.getAdapterPosition()).getId()
                            );
                        } else {
                            viewHolderGallery.check.setChecked(!viewHolderGallery.check.isChecked());
                            listener.onMultiSelect(mSelectedPhotos.size());
                        }

                    });

                    //load image
                    Glide.with(viewHolderGallery.image.getContext())
                            .load(Uri.parse("file://" + (isPhotoMode ? photosItem.get(position).getAddress() : albumsItem.get(position).getCover())))
                            .into(viewHolderGallery.image);


                    break;

                case 1:
                    VideoViewHolderGallery videoViewHolderGallery = (VideoViewHolderGallery) holder;


                    if (isMultiSelect) {
                        videoViewHolderGallery.check.setVisibility(View.VISIBLE);
                        videoViewHolderGallery.check.setChecked(mSelectedPhotos.contains(photosItem.get(position)));
                    } else {
                        videoViewHolderGallery.check.setChecked(false);
                        videoViewHolderGallery.check.setVisibility(View.GONE);
                    }

                    videoViewHolderGallery.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            if (!mSelectedPhotos.contains(photosItem.get(videoViewHolderGallery.getAdapterPosition())))
                                mSelectedPhotos.add(photosItem.get(videoViewHolderGallery.getAdapterPosition()));
                        } else {
                            mSelectedPhotos.remove(photosItem.get(videoViewHolderGallery.getAdapterPosition()));
                        }
                    });


                    videoViewHolderGallery.image.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            if (!isMultiSelect && isPhotoMode) {
                                videoViewHolderGallery.check.setChecked(!videoViewHolderGallery.check.isChecked());
                                listener.onMultiSelect(mSelectedPhotos.size());
                                setMultiSelectState(!getMultiSelectState());
                            }
                            return true;
                        }
                    });
                    //handle item click
                    videoViewHolderGallery.image.setOnClickListener(v -> {

                        if (!isMultiSelect) {
                            openVideoForEdit(photosItem.get(videoViewHolderGallery.getAdapterPosition()).getAddress());
                        } else {
                            videoViewHolderGallery.check.setChecked(!videoViewHolderGallery.check.isChecked());
                            listener.onMultiSelect(mSelectedPhotos.size());
                        }

                    });

                    //load image
                    Glide.with(videoViewHolderGallery.image.getContext())
                            .load(Uri.fromFile(new File(photosItem.get(position).getAddress())))
                            .into(videoViewHolderGallery.image);

                    break;

            }
        }

        private void openVideoForEdit(String path) {
            if (getActivity() == null) return;
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE);
            if (sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1) == 1) {
                Intent intent = new Intent(getActivity(), ActivityTrimVideo.class);
                intent.putExtra("PATH", path);
                getActivity().startActivityForResult(intent, AttachFile.request_code_trim_video);
                return;
            }
            List<String> videos = new ArrayList<>();
            videos.add(path);
        }

        @Override
        public int getItemCount() {
            return isPhotoMode ? photosItem.size() : albumsItem.size();
        }

        @Override
        public int getItemViewType(int position) {
//            if (photosItem.get(position).getMediaType().equals("1")) {
//                return 0;
//            } else if (photosItem.get(position).getMediaType().equals("3")) {
//                return 1;
//            }
            return 0;
        }

        class ViewHolderGallery extends RecyclerView.ViewHolder {

            TextView caption;
            ImageView image;
            CheckBox check;

            ViewHolderGallery(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                caption = itemView.findViewById(R.id.caption);
                check = itemView.findViewById(R.id.check);
            }

        }


        class VideoViewHolderGallery extends RecyclerView.ViewHolder {

            TextView caption;
            ImageView image;
            ImageView play;
            CheckBox check;

            VideoViewHolderGallery(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.image);
                caption = itemView.findViewById(R.id.caption);
                check = itemView.findViewById(R.id.check);
                play = itemView.findViewById(R.id.play);
            }

        }


    }
}
