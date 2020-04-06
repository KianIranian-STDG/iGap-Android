package net.iGap.adapter.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.model.news.NewsComment;

import java.util.List;

public class NewsCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<NewsComment> mData;

    public NewsCommentAdapter(List<NewsComment> mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View singleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_comment_item, parent, false);
        return new GroupViewHolder(singleVH);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((GroupViewHolder) holder).initView(position);
    }

    @Override
    public int getItemCount() {
        if (mData.size() > 5)
            return 5;
        return mData.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {

        private TextView author, comment;
        private CardView container;

        GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            comment = itemView.findViewById(R.id.comment);
            container = itemView.findViewById(R.id.container);
        }

        void initView(int position) {
            author.setText(mData.get(position).getUsername());
            comment.setText(mData.get(position).getComment());
            setColor();
        }

        private void setColor() {
            if (G.themeColor == Theme.DARK) {
                changeToDark();
                return;
            }
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
        }

        private void changeToDark() {
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.background_setting_dark));
        }
    }

}
