package net.iGap.fragments.filterImage;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.FragmentEditImage;
import net.iGap.helper.HelperFragment;
import net.iGap.module.AttachFile;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.module.AndroidUtils.suitablePath;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFilterImage extends Fragment implements ThumbnailsAdapter.ThumbnailsAdapterListener {

    private RecyclerView rcvEditImage;
    private ImageView imageFilter;
    private final static String PATH = "PATH";
    private String path;

    Bitmap originalImage;
    Bitmap filteredImage;
    Bitmap finalImage;
    ThumbnailsAdapter mAdapter;
    List<ThumbnailItem> thumbnailItemList;
    static {
        System.loadLibrary("NativeImageProcessor");
    }


    public FragmentFilterImage() {
        // Required empty public constructor
    }

    public static FragmentFilterImage newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        FragmentFilterImage fragment = new FragmentFilterImage();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter_image, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString(PATH);
        }

        imageFilter = (ImageView) view.findViewById(R.id.imageFilter);
        rcvEditImage = (RecyclerView) view.findViewById(R.id.rcvEditImage);


        thumbnailItemList = new ArrayList<>();
        mAdapter = new ThumbnailsAdapter(getActivity(), thumbnailItemList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rcvEditImage.setLayoutManager(mLayoutManager);
        rcvEditImage.setItemAnimator(new DefaultItemAnimator());
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        rcvEditImage.addItemDecoration(new SpacesItemDecoration(space));
        rcvEditImage.setAdapter(mAdapter);
        prepareThumbnail(null);
        loadImage();
        G.imageLoader.displayImage(suitablePath(path), imageFilter);

        view.findViewById(R.id.pu_ripple_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(FragmentFilterImage.this).remove();
            }
        });

        view.findViewById(R.id.pu_txt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String path = BitmapUtils.insertImage(getActivity().getContentResolver(), finalImage, System.currentTimeMillis() + "_profile.jpg", null);
                FragmentEditImage.updateImage.result(AttachFile.getFilePathFromUri(Uri.parse(path)));
                new HelperFragment(FragmentFilterImage.this).remove();
            }
        });

//        view.findViewById(R.id.pu_txt_clear).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new MaterialDialog.Builder(G.fragmentActivity)
//                        .title("Clear")
//                        .content("Are you sure")
//                        .positiveText("ok")
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            }
//                        })
//                        .negativeText("cancel")
//                        .show();
//            }
//        });

    }
    @Override
    public void onFilterSelected(Filter filter) {
        // reset image controls

        // applying the selected filter
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        // preview filtered image
        imageFilter.setImageBitmap(filter.processFilter(filteredImage));

        finalImage = filteredImage.copy(Bitmap.Config.ARGB_8888, true);
    }
    // load the default image from assets on app launch
    private void loadImage() {
        originalImage = BitmapUtils.getBitmapFile(getActivity(), path, 300, 300);
        filteredImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imageFilter.setImageBitmap(originalImage);
    }

    public void prepareThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            public void run() {
                Bitmap thumbImage;

                if (bitmap == null) {
                    thumbImage = BitmapUtils.getBitmapFile(getActivity(), path, 100, 100);
                } else {
                    thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                }

                if (thumbImage == null)
                    return;

                ThumbnailsManager.clearThumbs();
                thumbnailItemList.clear();

                // add normal bitmap first
                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImage;
                thumbnailItem.filterName = getString(R.string.about);
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters = FilterPack.getFilterPack(getActivity());

                for (Filter filter : filters) {
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbImage;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }

                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(getActivity()));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        };

        new Thread(r).start();
    }
}