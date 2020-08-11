package net.iGap.adapter.items;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.items.AbstractItem;

import net.iGap.R;
import net.iGap.observers.interfaces.OnClickCamera;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.fotoapparat.view.CameraView;

public class AdapterCamera extends AbstractItem<AdapterCamera, AdapterCamera.ViewHolder> {

    public String item;
    public OnClickCamera onClickCamera;

    //public String getItem() {
    //    return item;
    //}

    public AdapterCamera(String item, OnClickCamera onClickCamera) {
        this.item = item;
        this.onClickCamera = onClickCamera;
    }

    //public void setItem(String item) {
    //    this.item = item;
    //}

    //The unique ID for this type of item
    @Override
    public int getType() {
        return R.id.rootCamera;
    }

    //The layout to be used for this type of item
    @Override
    public int getLayoutRes() {
        return R.layout.adapter_camera;
    }

    //The logic to bind your data to the view

    @Override
    public void unbindView(ViewHolder holder) {
        super.unbindView(holder);
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    //The viewHolder used for this item. This viewHolder is always reused by the RecyclerView so scrolling is blazing fast
    protected class ViewHolder extends RecyclerView.ViewHolder {

        CameraView cm2;
        private TextView rootCamera;

        public ViewHolder(View view) {
            super(view);

            cm2 = view.findViewById(R.id.cameraView);
            rootCamera = view.findViewById(R.id.txtIconCamera);
            rootCamera.setOnClickListener(v -> {
                if (onClickCamera != null) {
                    onClickCamera.onclickCamera();
                }
            });
        }
    }
}
