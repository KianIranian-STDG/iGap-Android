package com.iGap.adapter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.R;
import com.iGap.activitys.ActivityChatBackground;
import com.iGap.module.StructAdapterBackground;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class AdapterChatBackground extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<StructAdapterBackground> items;
    int selected_position = 1;

    private boolean isSelect = true;

    private int myResultCodeCamera = 1;
    private int myResultCodeGallery = 0;
    private Uri uriIntent;
    public static ImageLoader imageLoader;
    private ViewHolderItem imageItem = null;
    private ViewHolderImage imageHolder = null;

    public AdapterChatBackground(List<StructAdapterBackground> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_background_choose, parent, false);
            viewHolder = new ViewHolderImage(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_background_image, parent, false);
            viewHolder = new ViewHolderItem(view);
        }
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final StructAdapterBackground item = items.get(position);

        imageLoader = ImageLoader.getInstance();
        if (selected_position == (position) && selected_position != 0) {

            imageItem = (ViewHolderItem) holder;
            imageItem.itemView.setBackgroundColor(G.context.getResources().getColor(R.color.toolbar_background));
            imageItem.itemView.setPadding(3, 3, 3, 3);

        } else if (position != 0) {
            imageItem = (ViewHolderItem) holder;
            imageItem.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
        if ((position) == 0 && imageHolder == null) {

            imageHolder = (ViewHolderImage) holder;

        } else if ((position) == 1 && G.chatBackground != null) {

            imageItem = (ViewHolderItem) holder;
            Bitmap bmp = imageLoader.loadImageSync("file://" + G.chatBackground);
            imageItem.img.setImageBitmap(bmp);
        } else {
            imageHolder = null;
            imageItem = (ViewHolderItem) holder;
            Bitmap bmp = imageLoader.loadImageSync("file://" + item.getPathImage());
            imageItem.img.setImageBitmap(bmp);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolderImage extends RecyclerView.ViewHolder {

        private ImageView imageView;
        public ViewHolderImage(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imgBackgroundImage);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(G.currentActivity)
                            .title("Choose Picture")
                            .negativeText("CANCEL")
                            .items(R.array.profile)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                                    if (text.toString().equals("From Camera")) {
                                        if (G.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            uriIntent = Uri.fromFile(G.chatBackground);
                                            ActivityChatBackground.uriIntent = uriIntent;
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriIntent);
                                            G.currentActivity.startActivityForResult(intent, myResultCodeCamera);
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(G.currentActivity, "Please check your Camera", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        G.currentActivity.startActivityForResult(intent, myResultCodeGallery);
                                        dialog.dismiss();
                                    }
                                }
                            })
                            .show();
                }
            });
        }
    }
    public class ViewHolderItem extends RecyclerView.ViewHolder {

        private ImageView img;

        public ViewHolderItem(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.imgBackground);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Updating old as well as new positions
                    notifyItemChanged(selected_position);
                    selected_position = getLayoutPosition();
                    notifyItemChanged(selected_position);
                    StructAdapterBackground item = items.get(getPosition());

                    ActivityChatBackground.savePath = item.getPathImage();
                    // Do your another stuff for your onClick
                }
            });

        }
    }
}
