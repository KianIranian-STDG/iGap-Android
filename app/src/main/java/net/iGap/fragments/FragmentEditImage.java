package net.iGap.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.filterImage.FragmentFilterImage;
import net.iGap.helper.HelperFragment;
import net.iGap.module.AttachFile;

import static android.app.Activity.RESULT_OK;
import static net.iGap.module.AndroidUtils.suitablePath;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEditImage extends Fragment {

    private final static String PATH = "PATH";
    private String path;
    private ImageView imgEditImage;
    public static UpdateImage updateImage;


    public FragmentEditImage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_image, container, false);
    }

    public static FragmentEditImage newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PATH, path);
        FragmentEditImage fragment = new FragmentEditImage();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            path = bundle.getString(PATH);
        }

        imgEditImage = (ImageView) view.findViewById(R.id.imgEditImage);

        TextView txtEditImage = (TextView) view.findViewById(R.id.txtEditImage);
        txtEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(FragmentFilterImage.newInstance(path)).setReplace(false).load();

            }
        });

        G.imageLoader.displayImage(suitablePath(path), imgEditImage);

        updateImage = new UpdateImage() {
            @Override
            public void result(String pathImageFilter) {

                path = pathImageFilter;
                G.imageLoader.displayImage(suitablePath(path), imgEditImage);
                Log.i("SSSSSSSSSSSS", "result: " + pathImageFilter);
            }
        };


        view.findViewById(R.id.pu_ripple_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HelperFragment(FragmentEditImage.this).remove();
            }
        });

        view.findViewById(R.id.pu_txt_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPath = "file://" + path;
                Uri uri = Uri.parse(newPath);

                CropImage.activity(uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(120, 120)
                        .setAutoZoomEnabled(false)
                        .setInitialCropWindowPaddingRatio(.08f) // padding window from all
                        .setBorderCornerLength(50)
                        .setBorderCornerOffset(0)
                        .setAllowCounterRotation(true)
                        .setBorderCornerThickness(8.0f)
                        .setShowCropOverlay(true)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(false)
                        .setBorderCornerColor(getResources().getColor(R.color.whit_background))
                        .setBackgroundColor(getResources().getColor(R.color.ou_background_crop))
                        .setScaleType(CropImageView.ScaleType.FIT_CENTER)
                        .start(G.fragmentActivity, FragmentEditImage.this);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { // result for crop
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgEditImage.setImageURI(result.getUri());
                path = AttachFile.getFilePathFromUri(result.getUri());
            }
        }

    }

    public interface UpdateImage {
        void result(String path);
    }

}
