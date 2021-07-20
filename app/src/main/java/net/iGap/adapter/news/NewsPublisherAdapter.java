package net.iGap.adapter.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.model.news.NewsPublisher;

import java.util.List;

public class NewsPublisherAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsPublisher> mData;
    private onClickListener callBack;

    public NewsPublisherAdapter(List<NewsPublisher> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View singleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_group_item, parent, false);

       /* Display display = G.currentActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        groupViewHolder.itemView.getLayoutParams().height = (int) (size.x *0.45);
        groupViewHolder.itemView.getLayoutParams().width = (int) (size.x *0.45);*/

        return new GroupViewHolder(singleVH);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((GroupViewHolder) holder).initView(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface onClickListener {
        void onNewsGroupClick(NewsPublisher news);
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView image;
        private CardView container;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }

        void initView(int position) {
            title.setText(mData.get(position).getName());
            Glide.with(G.context)
                    .load(mData.get(position).getImage())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.get(position)));
        }
    }
}
