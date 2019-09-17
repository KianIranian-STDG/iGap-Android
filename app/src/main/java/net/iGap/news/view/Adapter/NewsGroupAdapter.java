package net.iGap.news.view.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.news.repository.model.NewsGroup;

public class NewsGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private NewsGroup mData;
    private onClickListener callBack;

    public NewsGroupAdapter(NewsGroup mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View singleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_group_item, parent, false);
        GroupViewHolder groupViewHolder = new GroupViewHolder(singleVH);

       /* Display display = G.currentActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        groupViewHolder.itemView.getLayoutParams().height = (int) (size.x *0.45);
        groupViewHolder.itemView.getLayoutParams().width = (int) (size.x *0.45);*/

        return groupViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((GroupViewHolder)holder).initView(position);
    }

    @Override
    public int getItemCount() {
        return mData.getGroups().size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView image;
        private CardView container;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }

        public void initView(int position) {
            title.setText("" + mData.getGroups().get(position).getTitle());
            Picasso.get()
                    .load("https://images-eu.ssl-images-amazon.com/images/I/71T0kQ9FJPL.jpg")
//                    .load(mData.getGroups().get(position).getImage())
                    .placeholder(R.mipmap.logo)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getGroups().get(position)));
        }
    }

    public interface onClickListener {
        void onNewsGroupClick(NewsGroup.Groups news);
    }

    public onClickListener getCallBack() {
        return callBack;
    }

    public void setCallBack(onClickListener callBack) {
        this.callBack = callBack;
    }
}
