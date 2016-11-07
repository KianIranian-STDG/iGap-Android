package com.iGap.adapter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iGap.G;
import com.iGap.IntentRequests;
import com.iGap.R;
import com.iGap.activities.ActivityChatBackground;
import com.iGap.fragments.FragmentFullChatBackground;
import com.iGap.module.StructAdapterBackground;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class AdapterChatBackground extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static ImageLoader imageLoader;
    private final int CHOOSE = 0;
    private final int ALL = 1;
    int selected_position = 1;
    private List<StructAdapterBackground> items;
    private Uri uriIntent;

    public AdapterChatBackground(List<StructAdapterBackground> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == CHOOSE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_background_choose, parent, false);
            return new ViewHolderImage(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_background_image, parent, false);
            return new ViewHolderItem(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final StructAdapterBackground item = items.get(position);

        imageLoader = ImageLoader.getInstance();
        switch (holder.getItemViewType()) {
            case CHOOSE: {
                ViewHolderImage holder1 = (ViewHolderImage) holder;
            }
            break;
            case ALL: {
                ViewHolderItem holder2 = (ViewHolderItem) holder;
                Bitmap bmp = imageLoader.loadImageSync("file://" + item.getPathImage());
                holder2.img.setImageBitmap(bmp);

                if (selected_position == position) {
                    holder2.itemView.setBackgroundColor(
                            G.context.getResources().getColor(R.color.toolbar_background));
                    holder2.itemView.setPadding(3, 3, 3, 3);
                } else {
                    holder2.itemView.setBackgroundColor(Color.TRANSPARENT);
                }
            }
            break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return CHOOSE;
        } else {
            return ALL;
        }
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
                    new MaterialDialog.Builder(G.currentActivity).title("Choose Picture")
                            .negativeText("CANCEL")
                            .items(R.array.profile)
                            .itemsCallback(new MaterialDialog.ListCallback() {
                                @Override
                                public void onSelection(MaterialDialog dialog, View view, int which,
                                                        CharSequence text) {

                                    if (text.toString().equals("From Camera")) {
                                        if (G.context.getPackageManager()
                                                .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                                    Uri.fromFile(G.chatBackground));
                                            G.currentActivity.startActivityForResult(intent,
                                                    IntentRequests.REQ_CAMERA);
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(G.currentActivity,
                                                    "Please check your Camera", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Intent intent = new Intent(Intent.ACTION_PICK,
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        G.currentActivity.startActivityForResult(intent,
                                                IntentRequests.REQ_GALLERY);
                                        dialog.dismiss();
                                    }
                                }
                            })
                            .show();
                }
            });
        }
    }

    private class ViewHolderItem extends RecyclerView.ViewHolder {

        private ImageView img;

        ViewHolderItem(View itemView) {
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

                    FragmentFullChatBackground fragmentActivity = new FragmentFullChatBackground();
                    Bundle bundle = new Bundle();
                    bundle.putString("IMAGE", item.getPathImage());
                    fragmentActivity.setArguments(bundle);
                    ((FragmentActivity) G.currentActivity).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.stcb_root, fragmentActivity)
                            .commit();

                    ActivityChatBackground.savePath = item.getPathImage();
                    // Do your another stuff for your onClick
                }
            });
        }
    }
}
